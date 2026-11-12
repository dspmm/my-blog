#!/bin/bash
set -e

echo "=========================================="
echo "🚀 [CI/CD] 开始构建并部署小东的全栈个人博客..."
echo "=========================================="

# 1. 拉取最新代码
if [ -d ".git" ]; then
    echo "📥 [Git] 拉取远程最新代码..."
    git pull origin main || true
fi

# 2. 清理旧的独立 blog-mysql 容器（改用 1Panel 现有 MySQL）
docker stop blog-mysql 2>/dev/null || true
docker rm blog-mysql 2>/dev/null || true

# 3. 重新编译后端与前端容器
echo "🐳 [Docker] 重新构建并运行全栈容器..."
docker compose down
docker compose up -d --build

# 4. 检查服务健康状态
echo "⏳ [HealthCheck] 等待服务启动..."
sleep 3
docker ps --filter "name=blog-"

echo "=========================================="
echo "🎉 部署完成！"
echo "🌐 博客前端: http://<你的服务器IP>:8088"
echo "🔌 后端 API: http://<你的服务器IP>:8089/api/posts"
echo "=========================================="
