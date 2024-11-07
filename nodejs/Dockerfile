FROM node:16
WORKDIR /app
COPY package*.json ./
RUN npm install
RUN npm run build
EXPOSE 6080
CMD ["npm", "start"]
