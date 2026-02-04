# 零食商场（snack_store）

基于 Spring Boot + Vue3 的零食电商小项目，包含前台商城与后台管理。

## 目录结构

- backend：后端（Spring Boot 2.7.x，JPA，MySQL）
- frontend_v2：前端（Vue 3 + Vite）

## 运行环境

- JDK 8
- Maven 3.x
- Node.js 18+（建议）
- MySQL 8.x

## 快速启动

## Docker 一键启动（推荐）

确保本机已安装 Docker Desktop，然后在项目根目录执行：

```bash
docker compose up -d --build
```

启动后访问：

- 前端：http://localhost:5173
- 后端：http://localhost:8080
- MySQL：localhost:3307（root / 12345678）

说明：
- MySQL 会在首次启动时自动执行 [schema.sql](file:///Users/libra520/JavaProject/biyesheji/ling_shi_shang_cheng/snack_store/backend/src/main/resources/schema.sql)，包含建表与初始化数据
- 如需清空数据库并重置初始化数据：

```bash
docker compose down -v
```

### 1）启动后端

1. 启动 MySQL
2. 配置数据库账号密码（二选一）：
   - 方式 A：使用默认配置（见 [application.yml](file:///Users/libra520/JavaProject/biyesheji/ling_shi_shang_cheng/snack_store/backend/src/main/resources/application.yml#L1-L18)）
     - username：root
     - password：12345678
   - 方式 B：通过环境变量覆盖
     - MYSQL_USERNAME
     - MYSQL_PASSWORD
3. 在项目根目录执行：

```bash
cd backend
mvn spring-boot:run
```

后端默认端口：`http://localhost:8080`

数据库初始化说明：
- 项目使用 [schema.sql](file:///Users/libra520/JavaProject/biyesheji/ling_shi_shang_cheng/snack_store/backend/src/main/resources/schema.sql) 初始化表结构与部分演示数据
- 由于配置了 `spring.sql.init.mode: always`，启动时会执行初始化脚本（仅在不存在时创建表，且部分数据使用 `ON DUPLICATE KEY UPDATE`）

### 2）启动前端

```bash
cd frontend_v2
npm install
npm run dev
```

前端默认地址：`http://localhost:5173`

## 账号说明（初始化数据）

初始化数据来自 [schema.sql](file:///Users/libra520/JavaProject/biyesheji/ling_shi_shang_cheng/snack_store/backend/src/main/resources/schema.sql#L144-L158)。

- 后台管理员
  - 用户名：admin
  - 密码：admin123
- 测试用户
  - 手机号：13800001234 / 密码：user123
  - 手机号：13900001111 / 密码：user123

## 主页推荐规则

后端接口在 [ProductController](file:///Users/libra520/JavaProject/biyesheji/ling_shi_shang_cheng/snack_store/backend/src/main/java/com/snackstore/controller/ProductController.java) 中提供：

- 今日上新：`GET /api/products/new-arrivals?limit=3`
  - 取最新创建的商品
  - 不足 3 个时，用最新商品补齐
- 精选推荐：`GET /api/products/featured?limit=3`
  - 取订单最多的商品
  - 不足 3 个时，用最新商品补齐

## 构建

后端：

```bash
cd backend
mvn clean package
```

前端：

```bash
cd frontend_v2
npm run build
```
