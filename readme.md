# Aether AI

基于原 Spring AI 对话项目重构的多模块全栈应用。保留流式对话、会话历史、自动标题和每日额度，同时补齐用户体系、MongoDB 数据隔离、Spring Security JWT 双 Token 鉴权，以及重新设计的 React 工作台。

## 技术栈

- 后端：Java 17、Spring Boot 4.1、Spring AI 2.0、Spring Security、MongoDB、Redis
- 前端：React 19、TypeScript、Tailwind CSS 4、Vite、Zustand
- 包管理与构建：Bun、Maven
- AI：DeepSeek；未配置 API Key 时自动进入可交互的本地演示模式

## 项目结构

```text
.
├── pom.xml          # 工作区聚合 POM，只管理 backend
├── backend
│   ├── pom.xml      # 后端父 POM，统一依赖、版本和子模块
│   ├── common       # 统一响应、业务异常、全局异常处理
│   ├── security     # 用户、JWT、Redis 会话、黑名单、Spring Security 配置
│   ├── chat         # AI 对话、会话、消息、额度、工具
│   └── app          # Spring Boot 启动模块与运行配置
├── frontend         # React + TypeScript + Tailwind CSS
├── design-system    # UI 设计规范
├── docker-compose.yml
```

`backend/pom.xml` 是唯一的后端父项目，统一管理 Java 版本、Spring AI BOM、JWT 版本和插件配置。模块依赖方向为 `common <- security <- chat <- app`，业务代码不会反向依赖启动模块。

每个业务模块采用统一分层包结构：

```text
com.aether.<module>
├── controller       # HTTP 接口，只依赖 Service 接口
├── service          # 业务接口
│   └── impl         # Service 实现
├── mapper           # MongoDB 数据访问与 Entity/VTO 转换
├── pojo
│   ├── entity       # 数据库实体和领域枚举
│   ├── dto          # 请求及内部传输对象
│   └── vto          # 返回给前端或调用方的视图对象
├── config           # 配置类与类型安全配置属性
├── filter           # 安全过滤器
├── tool             # AI 工具
└── util             # 无状态工具类
```

Controller 不直接访问 Mapper，Entity、DTO、VTO 不混放，业务实现统一位于 `service/impl`。

## 本地启动

环境要求：JDK 17+、Maven 3.9+、Bun 1.2+、本地 MongoDB、Redis。

当前默认数据库连接：

```text
mongodb://root:123456@localhost:27017/aether_ai?authSource=admin
```

数据库已经在运行时可跳过第一条命令。否则可使用仓库内的 Compose 文件：

```powershell
docker compose up -d mongodb redis
```

启动后端：

```powershell
cd backend
mvn -pl app -am spring-boot:run
```

后端默认地址为 `http://localhost:8080`。

启动前端：

```powershell
cd frontend
bun install
bun run dev
```

访问 `http://localhost:5173`，首次进入时注册账号。开发服务器会把 `/api` 代理到 `8080`。

## AI 配置

不配置 Key 也能启动、注册、创建会话并体验流式输出；页面会明确显示“本地演示模式”。启用 DeepSeek：

```powershell
$env:DEEPSEEK_API_KEY="你的 DeepSeek API Key"
cd backend
mvn -pl app -am spring-boot:run
```

常用环境变量见 [.env.example](./.env.example)：

| 变量 | 默认值 | 说明 |
|---|---|---|
| `MONGODB_URI` | 本地 `aether_ai` 数据库 | MongoDB 连接串 |
| `REDIS_HOST` | `localhost` | Redis 地址 |
| `REDIS_PORT` | `6379` | Redis 端口 |
| `REDIS_PASSWORD` | `123456` | Redis 密码 |
| `REDIS_DB` | `0` | Redis 数据库编号 |
| `DEEPSEEK_API_KEY` | 空 | 设置后切换真实 AI |
| `DEEPSEEK_MODEL` | `deepseek-chat` | 模型名称 |
| `AI_DAILY_LIMIT` | `100` | 每用户每日对话次数 |
| `JWT_SECRET` | 开发用默认值 | 生产环境必须替换 |
| `COOKIE_SECURE` | `false` | HTTPS 部署时设为 `true` |

## 双 Token 鉴权

1. 登录或注册成功后，Access Token 仅在响应中返回，前端放在内存里，不写入 `localStorage`。
2. Refresh Token 写入 `HttpOnly` Cookie，作用路径为 `/api/auth`，前端脚本不可读取。
3. Access Token 默认 15 分钟过期；收到 401 时前端只发起一次刷新请求，并把并发的失败请求统一重试。
4. Refresh Token 默认 7 天过期，哈希和会话状态保存在 Redis；每次刷新都通过 Lua 脚本原子轮换。
5. 每个用户只有一个活动 `sessionId`。新设备登录会撤销旧刷新会话，旧设备的 Access Token 也会因会话不匹配而失效。
6. Access Token 包含 `jti` 与 `sid`。注销时 `jti` 写入 Redis 黑名单并设置与 Token 剩余寿命一致的 TTL。
7. JWT 过滤器同时校验签名、黑名单和当前活动会话，确保旧设备无法继续访问。
8. 除公共鉴权接口外，所有 API 都要求 Access Token；会话与消息查询强制附带当前用户 ID。

主要接口：

| 方法 | 路径 | 说明 |
|---|---|---|
| `POST` | `/api/auth/register` | 注册并登录 |
| `POST` | `/api/auth/login` | 登录 |
| `POST` | `/api/auth/refresh` | Cookie 换新 Access Token |
| `POST` | `/api/auth/logout` | 撤销刷新会话 |
| `GET` | `/api/auth/me` | 当前用户 |
| `GET/POST` | `/api/conversations` | 会话列表 / 新建 |
| `PATCH` | `/api/conversations/{id}` | 重命名 |
| `PATCH` | `/api/conversations/{id}/pin` | 置顶切换 |
| `DELETE` | `/api/conversations/{id}` | 删除会话及消息 |
| `GET` | `/api/conversations/{id}/messages` | 会话历史 |
| `POST` | `/api/chat/{id}` | 流式发送消息 |
| `GET` | `/api/usage` | 今日额度 |

## 验证命令

```powershell
# 后端编译与测试
mvn test

# 前端类型检查、构建、Lint
cd frontend
bun run check

# 需要同时启动前后端的视觉与无障碍检查
bun run visual:check
```

视觉检查使用本机 Edge，输出到仓库根目录的 `.artifacts/`，覆盖登录页、桌面会话、回答流、深色模式、移动端和移动侧栏。

## 依赖问题排查

1. IDE 应导入 [backend/pom.xml](./backend/pom.xml) 作为 Maven 根项目，而不是只单独打开 `app/pom.xml`。如果只打开子模块，先执行 `mvn install -DskipTests` 把内部模块安装到本地仓库。
2. 项目使用 Java 17 编译。IDE 的 Project SDK 和 Maven Runner JRE 都应为 JDK 17、21 或 25。
3. Spring Boot 4 使用 Jackson 3（`tools.jackson`），JJWT 仍使用 Jackson 2（`com.fasterxml.jackson`）。两套包名不同，可以共存，不是版本冲突。
4. 不要只执行 `mvn -pl app spring-boot:run`；该命令不会自动构建同级的内部模块。使用 `mvn -pl app -am spring-boot:run`。
5. 拉起依赖异常时执行 `mvn -U clean verify`。仍然失败时，检查 Maven `settings.xml` 镜像是否缺少 Spring AI 2.0.1 或 Spring Boot 4.1.1。

## 生产部署注意事项

- 替换 `JWT_SECRET`，建议使用至少 64 字节的随机值。
- 启用 HTTPS，并设置 `COOKIE_SECURE=true`。
- 将 `application.yaml` 中的 CORS 白名单改为实际前端域名。
- 不要把 `.env` 或真实 API Key 提交到仓库。
- 若前后端分域部署，需要重新设计 Refresh Cookie 的 `SameSite`、域名和 CORS 策略；当前默认配置面向同站部署。
