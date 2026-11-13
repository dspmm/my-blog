# 小东的技术博客

一个基于 Vue 3 和 Spring Boot 的前后端分离博客，用于记录技术文章、分享实践，并与读者交流。

**线上体验：** [https://jxdcloud.duckdns.org](https://jxdcloud.duckdns.org)

## 功能

- 文章列表、搜索、分类与归档，支持 Markdown 阅读和代码高亮。
- 文章点赞、评论及回复；管理员可审核和管理评论。
- 邮箱验证码注册、登录、个人资料管理和站内通知。
- 管理员维护文章、分类和用户，图片通过 MinIO 存储。
- 适配桌面端和移动端，提供深色模式及搜索引擎抓取页面。

## 技术栈

| 部分 | 技术 |
| --- | --- |
| 前端 | Vue 3、Vite、Tailwind CSS |
| 后端 | Java 17、Spring Boot 3、Spring Data JPA |
| 数据与文件 | MySQL 8、MinIO |
| 运行与交付 | Docker Compose、Nginx、Gitea Actions |

## 项目结构

~~~text
my-blog/
├── frontend/                 Vue 前端及 Nginx 配置
├── backend/                  Spring Boot 后端
├── .env.example              环境变量模板
├── docker-compose.yml        前端、后端和 MinIO 编排
└── .gitea/workflows/         作者使用的 Gitea Actions 流水线
~~~

## 本地运行

以下步骤以 **Docker Desktop（Windows / macOS，使用 Linux 容器）** 为例。仓库的 Compose 文件会启动前端、后端和 MinIO；MySQL 需要单独准备。

### 1. 准备环境

安装 Git 和带 Compose 插件的 Docker，并启动 Docker Desktop。确保本机的 `3306`、`8088`、`8089`、`9000` 和 `9001` 端口未被占用：

~~~bash
git clone https://github.com/dspmm/my-blog.git
cd my-blog
docker --version
docker compose version
~~~

### 2. 启动 MySQL

如果已有 MySQL 8，可以使用现有实例：创建 `my_blog` 数据库，并准备对该数据库具有读写和建表权限的用户。

也可以使用下面的命令启动一个独立的 MySQL 容器。先将两个密码占位符换成自己的密码；后续 `.env` 的 `DB_PASSWORD` 必须与这里的应用用户密码一致。

~~~bash
docker run -d --name my-blog-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=YOUR_MYSQL_ROOT_PASSWORD -e MYSQL_DATABASE=my_blog -e MYSQL_USER=blog_app -e MYSQL_PASSWORD=YOUR_MYSQL_APP_PASSWORD -v myblog-local-mysql:/var/lib/mysql mysql:8.0
~~~

首次启动需要等待 MySQL 初始化完成。可运行 `docker logs my-blog-mysql`，看到数据库已准备接受连接后继续。命名卷 `myblog-local-mysql` 用于保存数据库；已存在的卷不会因修改上述环境变量而重新初始化账号或密码。

### 3. 配置环境变量

在项目根目录将 `.env.example` 复制为 `.env`，然后用文本编辑器填写：

~~~bash
# macOS / Linux
cp .env.example .env
~~~

~~~powershell
# Windows PowerShell
Copy-Item .env.example .env
~~~

下面列出本地运行时需要重点修改的值；其余配置可保留模板默认值。所有 `YOUR_...` 都需要替换，尤其是密码和 JWT 密钥。

~~~dotenv
# Docker Desktop：后端容器通过宿主机访问上面启动的 MySQL
DB_HOST=host.docker.internal
DB_PORT=3306
DB_NAME=my_blog
DB_USER=blog_app
DB_PASSWORD=YOUR_MYSQL_APP_PASSWORD

# 后端使用 MinIO 根账号连接；两组值必须分别一致
MINIO_ROOT_USER=blogminio
MINIO_ROOT_PASSWORD=YOUR_MINIO_PASSWORD
MINIO_ENDPOINT=http://minio:9000
MINIO_ACCESS_KEY=blogminio
MINIO_SECRET_KEY=YOUR_MINIO_PASSWORD
MINIO_BUCKET_NAME=my-blog
MINIO_PUBLIC_URL=/my-blog

# 至少 32 个字符的高强度随机串
JWT_SECRET=YOUR_RANDOM_JWT_SECRET_AT_LEAST_32_CHARACTERS

# 可选：首次初始化 admin 用户时使用
INIT_ADMIN_PASSWORD=YOUR_ADMIN_PASSWORD
INIT_ADMIN_EMAIL=

# 暂不配置邮件时留空；注册验证码需要 SMTP 或本地调试回退
MAIL_USERNAME=
MAIL_PASSWORD=
~~~

可以用 `openssl rand -base64 48` 生成 JWT 密钥。`.env` 含本地凭据，不要提交或分享。

在 **Linux Docker Engine** 上，`host.docker.internal` 不一定可用。若按上面的命令将 MySQL 端口发布到宿主机，可将 `DB_HOST` 设置为宿主机的 Docker 网桥地址；通过以下命令查看默认网桥网关（通常为 `172.17.0.1`）：

~~~bash
docker network inspect bridge --format '{{(index .IPAM.Config 0).Gateway}}'
~~~

若使用已有 MySQL，`DB_HOST` 和 `DB_PORT` 必须是**从后端容器可访问**的地址和端口，而非容器内的 `localhost`。如果宿主机的 3306 端口已被占用，可将上面的 MySQL 端口映射改为 `-p 3307:3306`，并将 `DB_PORT` 设为 `3307`。

### 4. 配置本地 MinIO 数据卷

仓库默认将 MinIO 数据绑定到宿主机的 `/opt/minio/data`。为了在 Docker Desktop 上使用持久化命名卷，在项目根目录手动创建 `compose.local.yaml`：

~~~yaml
services:
  minio:
    volumes:
      - myblog-local-minio:/data

volumes:
  myblog-local-minio:
~~~

启动时同时指定这个文件，它会覆盖 MinIO 的 `/data` 挂载位置。这个文件是本地配置，建议不要提交到仓库。

### 5. 构建并启动

~~~bash
docker compose -f docker-compose.yml -f compose.local.yaml up -d --build
docker compose -f docker-compose.yml -f compose.local.yaml ps
~~~

首次构建需要下载依赖和镜像。后端会连接 MySQL 并初始化表结构及示例内容；MinIO 存储桶会在连接成功后初始化。

| 地址 | 用途 |
| --- | --- |
| [http://localhost:8088](http://localhost:8088) | 博客前端 |
| http://localhost:8089 | 后端服务端口 |
| http://localhost:9000 | MinIO API |
| [http://localhost:9001](http://localhost:9001) | MinIO 控制台，使用 `MINIO_ROOT_USER` 和 `MINIO_ROOT_PASSWORD` 登录 |

首次连接成功后，应用会将 `my-blog` 存储桶设为公开读取，以便展示上传的图片。Compose 默认将服务端口发布到宿主机；请按自己的网络环境配置访问限制。

### 管理员与邮箱

- 当数据库中还没有 `admin` 用户，且启动前设置了 `INIT_ADMIN_PASSWORD` 时，后端会创建管理员。用户名为 `admin`，密码为你设置的值；之后修改该环境变量不会重置已有账号。
- 注册需要邮箱验证码。填写 `MAIL_HOST`、`MAIL_PORT`、`MAIL_USERNAME` 和 `MAIL_PASSWORD` 后可使用 SMTP 发信；密码通常是邮箱服务商提供的授权码。
- 本地没有 SMTP 时，可将 `APP_AUTH_LOG_VERIFICATION_CODE` 临时设为 `true`，重新创建后端容器后在后端日志中查看验证码。生产环境应保持 `false`。

### 常用命令与排错

~~~bash
# 查看服务状态和后端日志
docker compose -f docker-compose.yml -f compose.local.yaml ps
docker compose -f docker-compose.yml -f compose.local.yaml logs -f backend

# 停止博客服务；命名卷中的数据会保留
docker compose -f docker-compose.yml -f compose.local.yaml down

# MySQL 是单独启动的容器，可按需停止
docker stop my-blog-mysql
~~~

- 后端无法连接数据库：确认 MySQL 已完成初始化，检查容器可访问的 `DB_HOST`、端口、账号和密码。
- 图片上传失败：检查 MinIO 是否运行，以及 `MINIO_ACCESS_KEY` / `MINIO_SECRET_KEY` 是否与根账号匹配。
- 注册收不到验证码：检查 SMTP 配置，或在本地调试时检查是否开启了日志回退。
- 修改 `.env` 后，重新执行上面的 Compose 启动命令以重建受影响的容器。

数据库保存在 `myblog-local-mysql` 卷中，MinIO 文件保存在 `myblog-local-minio` 卷中。不要随意删除这些卷；原始 Compose 配置使用的 MinIO 目录则是 `/opt/minio/data`。

## 持续部署

仓库中的 `.gitea/workflows/deploy.yaml` 是作者在 Gitea Actions 上使用的自动部署流程。本地运行不需要配置 Gitea、Runner 或部署密钥。
