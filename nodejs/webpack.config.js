const path = require("path");

module.exports = {
  entry: "./src/index.js", // 프로젝트의 실제 엔트리 파일 경로로 설정
  output: {
    filename: "bundle.js",
    path: path.resolve(__dirname, "dist"),
  },
  target: "node", // Node.js 환경에서 실행되도록 설정
  externals: {
    fs: "commonjs fs",
    net: "commonjs net",
    async_hooks: "commonjs async_hooks",
  },
  resolve: {
    extensions: [".js", ".json"],
  },
};
