import express from "express";
import http from "http";
import { Server } from "socket.io";
import dotenv from "dotenv";

dotenv.config();

const app = express();
const server = http.createServer(app);
const io = new Server(server);
const SERVER_PORT = process.env.SERVER_PORT;

app.use(express.json());

const roomWords = {};
const roomDrawings = {};

io.on("connection", (socket) => {
  console.log("클라이언트가 연결되었습니다.");

  // 입장
  socket.on("join", (roomId) => {
    socket.join(roomId);
    socket.roomId = roomId;
    roomWords[roomId] = "";

    // 새로운 사용자가 접속하면 현재 그림 데이터 전송
    if (roomDrawings[roomId]) {
      socket.emit("initDrawing", roomDrawings[roomId]);
    }
    console.log(`클라이언트가 방 ${roomId}에 참여했습니다.`);
  });

  // 좌표 통신
  socket.on("draw", (draw) => {
    if (!roomDrawings[socket.roomId]) {
      roomDrawings[socket.roomId] = [];
    }
    roomDrawings[socket.roomId].push(draw);

    socket.to(socket.roomId).emit("draw", draw);
  });

  // 단어 설정
  socket.on("setWord", (data) => {
    const { setWord } = JSON.parse(data);
    roomWords[socket.roomId] = setWord;
  });

  // 단어 확인
  socket.on("checkWord", (data) => {
    const { checkWord } = JSON.parse(data);
    const isCorrect =
      checkWord.trim().toLowerCase() ===
      roomWords[socket.roomId].trim().toLowerCase(); // 전역 객체에서 단어 비교
    console.log(
      `방 ${socket.roomId}에서 단어 확인 요청: ${checkWord}, 정답 여부: ${isCorrect}`
    );

    io.to(socket.roomId).emit("checkWord", isCorrect);
  });

  // 그림 초기화
  socket.on("clear", () => {
    console.log(`Path 초기화 요청을 받았습니다.`);
    roomDrawings[socket.roomId] = [];
    io.to(socket.roomId).emit("clear");
  });

  // 연결 종료
  socket.on("disconnect", () => {
    console.log(`클라이언트가 방에서 연결이 종료되었습니다.`);
    roomDrawings[socket.roomId] = [];
    io.to(socket.roomId).emit("disconnect");
  });
});

server.listen(SERVER_PORT, () => {
  console.log("Server started on port:", SERVER_PORT);
});
