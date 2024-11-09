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

  // jwt 토큰 저장
  socket.on("authenticate", (jwtToken) => {
    console.log("JWT 토큰을 수신하였습니다:", jwtToken);
    socket.jwtToken = jwtToken; 
  });

  // 부모님 입장
  socket.on("joinParents", (roomId) => {
    socket.join(roomId);
    socket.roomId = roomId;
    
    socket.emit("initDrawing", roomData[roomId].drawings);
    socket.emit("wordSelectionState", roomData[roomId].quizState.isWordSelected);
    socket.to(socket.roomId).emit("joinParents");
    console.log(`클라이언트가 방 ${roomId}에 참여했습니다.`);
  });

  // 좌표 통신
  socket.on("draw", (draw) => {
    roomData[socket.roomId].drawings.push(draw);
    socket.to(socket.roomId).emit("draw", draw);
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
    const isCorrect =
      checkWord.trim().toLowerCase() === roomData[socket.roomId].word.trim().toLowerCase(); 
    console.log(`방 ${socket.roomId}에서 단어 확인 요청: ${checkWord}, 정답 여부: ${isCorrect}`);
    io.to(socket.roomId).emit("checkWord", isCorrect);
  });

  // 그림 초기화
  socket.on("clear", () => {
    console.log(`Path 초기화 요청을 받았습니다.`);
    roomData[socket.roomId].drawings = []; 
    io.to(socket.roomId).emit("clear");
  });

  // 퀴즈 시작
  socket.on("quizStart", () => {
    console.log(`그림 퀴즈를 시작합니다.`);
    socket.to(socket.roomId).emit("quizStart");
  });

  // 연결 종료
  socket.on("disconnect", async() => {
    console.log(`클라이언트가 방에서 연결이 종료되었습니다.`);
    if (io.sockets.adapter.rooms.get(socket.roomId)?.size === 0) {
      delete roomData[socket.roomId];
    }
    socket.to(socket.roomId).emit("userDisconnected");
    console.log("JWT 토큰을 수신하였습니다:", socket.jwtToken);
    try {
      const response = await axios.post(
          `${SPRING_SERVER_URL}/api/quiz/sessions/end`,
          {},  
          {
              headers: {
                  Authorization: `Bearer ${socket.jwtToken}`,  // 헤더에 JWT 토큰 포함
              },
          }
      );
      console.log("스프링 서버 응답:", response.data);
    } catch (error) {
      console.error("스프링 서버로 전송 중 오류 발생:", error);
    }
  });
});

server.listen(SERVER_PORT, () => {
  console.log("Server started on port:", SERVER_PORT);
});
