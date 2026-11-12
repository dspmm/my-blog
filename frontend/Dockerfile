# Stage 1: 编译构建前端现代化 SPA
FROM node:20-alpine AS builder
WORKDIR /app

# 优先缓存依赖层
COPY package*.json ./
RUN npm install

# 复制源码并构建生成生产包
COPY . .
RUN npm run build

# Stage 2: 极简 Nginx 生产环境服务镜像
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
