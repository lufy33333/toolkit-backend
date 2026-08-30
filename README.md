# toolkit-backend
a backend for toolkit


操作步骤
启动后端（toolkit-backend 下 mvn spring-boot:run 或 docker compose up -d）
前端 toolkit 下运行 npm run dev
浏览器访问 http://localhost:5173，注册/登录即可测试联通性
注意：Vite proxy 仅在 npm run dev 开发模式下生效；生产构建（npm run build）时 /api 相对路径由部署环境（如 Nginx 反代到 8080）接管，部署时如后端独立域名，可通过 VITE_API_BASE_URL 指定完整地址。