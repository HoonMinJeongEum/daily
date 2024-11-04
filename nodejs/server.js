import express from 'express';
import http from 'http';
import { Server } from 'socket.io';
import dotenv from 'dotenv';

dotenv.config();

const app = express();
const server = http.createServer(app); 
const io = new Server(server);
const SERVER_PORT = process.env.SERVER_PORT;

app.use(express.json()); 

io.on('connection', (socket) => {
  console.log('클라이언트가 연결되었습니다.');

  // 입장
  socket.on('join', (roomId) => {
      socket.join(roomId);
      socket.roomId = roomId;
      console.log(`클라이언트가 방 ${roomId}에 참여했습니다.`);
  });

  // 좌표 통신
  socket.on('draw', (draw) => {
    socket.to(socket.roomId).emit('draw', draw);
  });

});

server.listen(SERVER_PORT, () => {
  console.log("Server started on port:", SERVER_PORT);
});
