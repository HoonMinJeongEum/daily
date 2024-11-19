import express from "express";
import http from "http";
import { Server } from "socket.io";
import dotenv from "dotenv";
import axios from "axios";
import Room from "./Room.js";

dotenv.config();

const SPRING_SERVER_URL = process.env.SPRING_SERVER_URL;
const app = express();
const server = http.createServer(app);
const io = new Server(server);
const SERVER_PORT = process.env.SERVER_PORT;

app.use(express.json());

const roomData = {};

io.on("connection", (socket) => {
  console.log("클라이언트가 연결되었습니다.");

  // 입장
  socket.on("join", (roomId) => {
    socket.join(roomId);
    socket.roomId = roomId;

    if (!roomData[roomId]) {
      roomData[roomId] = new Room();
    }
    console.log(`클라이언트가 방 ${roomId}에 참여했습니다.`);
  });

  // jwt와 refresh 토큰 저장
  socket.on("authenticate", (authData) => {
    const { jwtToken, refreshToken } = JSON.parse(authData); // JWT와 리프레시 토큰을 분리하여 추출
    console.log("JWT 토큰을 수신하였습니다:", jwtToken);
    console.log("리프레시 토큰을 수신하였습니다:", refreshToken);
    const parsedRefreshToken = refreshToken.split(";")[0];

    // 방 데이터에 토큰 저장
    roomData[socket.roomId].jwtToken = jwtToken;
    roomData[socket.roomId].refreshToken = parsedRefreshToken;
    console.log("저장된 리프레시 토큰:", parsedRefreshToken);
  });

  // 부모님 입장
  socket.on("joinParents", (roomId) => {
    socket.join(roomId);
    socket.roomId = roomId;
    socket.emit("aspectRatio", roomData[socket.roomId].aspectRatio);
    socket.to(socket.roomId).emit("joinParents");
    console.log(`클라이언트가 방 ${roomId}에 참여했습니다.`);
  });

  // 비율 전달
  socket.on("aspectRatio", (aspectRatio) => {
    roomData[socket.roomId].aspectRatio = aspectRatio;
    io.to(socket.roomId).emit("aspectRatio", aspectRatio); 
  });

  // 실시간 그림
  socket.on("draw", (draw) => {
    socket.to(socket.roomId).emit("draw", draw);
  });
  
  socket.on("addPath", () => {
    socket.to(socket.roomId).emit("addPath");
  });

  socket.on("undoPath", () => {
    socket.to(socket.roomId).emit("undoPath");
  });

  socket.on("redoPath", () => {
    socket.to(socket.roomId).emit("redoPath");
  });

  socket.on("color", (colorData) => {
    socket.to(socket.roomId).emit("color", colorData);
  });

  socket.on("width", (widthData) => {
    socket.to(socket.roomId).emit("width", widthData);
  });

  socket.on("alpha", (alphaData) => {
    socket.to(socket.roomId).emit("alpha", alphaData);
  });

  // 단어 설정
  socket.on("setWord", (data) => {
    const { setWord } = JSON.parse(data);
    roomData[socket.roomId].word = setWord;
    io.to(socket.roomId).emit("setWord");
  });

  // 단어 확인
  socket.on("checkWord", (data) => {
    const { checkWord } = JSON.parse(data);

    const processedWord = checkWord.trim().toLowerCase();
    const isCorrect =
      processedWord === roomData[socket.roomId].word.trim().toLowerCase(); 
    
      io.to(socket.roomId).emit("checkWord", { isCorrect, processedWord });
  });

  // 그림 초기화
  socket.on("clear", () => {
    console.log(`Path 초기화 요청을 받았습니다.`);
    io.to(socket.roomId).emit("clear");
  });

  // 퀴즈 시작
  socket.on("quizStart", () => {
    console.log(`그림 퀴즈를 시작합니다.`);
    socket.to(socket.roomId).emit("quizStart");
  });

  // 연결 종료
  socket.on("disconnect", async () => {
    console.log(`클라이언트가 방에서 연결이 종료되었습니다.`);
    if (io.sockets.adapter.rooms.get(socket.roomId)?.size === 0) {
      delete roomData[socket.roomId];
    }
    socket.to(socket.roomId).emit("userDisconnected");

    try {
      await sendEndSessionRequest(roomData[socket.roomId].jwtToken); // 기존 JWT 토큰으로 요청 시도
    } catch (error) {
      if (error.response && error.response.status === 401) {
        console.log("401 오류 발생 - 리프레시 토큰으로 재발급 요청을 시작합니다.");
        const newTokens = await requestNewToken(); // 새로운 토큰 요청
        if (newTokens) {
          roomData[socket.roomId].jwtToken = newTokens.jwtToken;
          roomData[socket.roomId].refreshToken = newTokens.refreshToken;
          console.log("새로운 JWT 및 리프레시 토큰을 저장했습니다.");
          await sendEndSessionRequest(roomData[socket.roomId].jwtToken); // 새 토큰으로 재시도
        }
      } else {
        console.error("스프링 서버로 전송 중 오류 발생:", error);
      }
    }
  });

  async function sendEndSessionRequest(token) {
    const response = await axios.post(
      `${SPRING_SERVER_URL}/api/quiz/sessions/end`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`, // 토큰을 사용하여 요청
        },
      }
    );
    console.log("스프링 서버 응답:", response.data);
  }
  
  async function requestNewToken() {
    try {
      const reissueResponse = await axios.post(
        `${SPRING_SERVER_URL}/api/user/reissue`,
        {},
        {
          headers: {
            Cookie: `${roomData[socket.roomId].refreshToken}`, // 리프레시 토큰을 쿠키로 설정
          },
        }
      );
      if (reissueResponse.status === 200) {
        const jwtToken = reissueResponse.headers['authorization'].replace("Bearer ", "");
        const setCookieHeader = reissueResponse.headers['set-cookie'][0];
        const refreshToken = setCookieHeader.split(";")[0];
        return {
          jwtToken: jwtToken,
          refreshToken: refreshToken,
        };
      } else {
        console.error("JWT 재발급에 실패했습니다.");
        return null;
      }
    } catch (reissueError) {
      console.error("리프레시 토큰으로 JWT 재발급 중 오류 발생:", reissueError);
      return null;
    }
  }
});

server.listen(SERVER_PORT, () => {
  console.log("Server started on port:", SERVER_PORT);
});

