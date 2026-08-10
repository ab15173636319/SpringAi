# S_AI

基于 Spring AI 的智能对话助手后端服务，接入 DeepSeek 大模型，支持流式对话、工具调用（Function Calling）与基于 MongoDB 的会话历史记忆。

## 功能特性

- **智能对话**：接入 DeepSeek（`deepseek-v4-flash`）大模型，`GET /ai/chat` 接口以 SSE 流式返回回复内容。
- **工具调用（Function Calling）**：
  - **定位服务**：通过高德地图 IP 定位接口获取用户所在城市。
  - **天气查询**：根据城市编码（adcode）查询实时天气，并给出穿着 / 出行建议。
  - **情绪识别**：基于关键词规则分析用户当前心情（开心 / 愤怒 / 悲伤 / 焦虑 / 平静）。
- **会话记忆**：使用 MongoDB 持久化聊天记忆，配合 `MessageWindowChatMemory` 保留最近 20 条消息，同一会话（`id` 参数）自动携带上下文。
- **情绪化回复**：系统提示词要求助手以 JSON 返回回复内容 `{msg, qute}`，msg 为 Markdown 格式回答，qute 为用户提供三条话题建议。
- **跨域支持**：已配置全局 CORS，方便前端联调。

## 技术栈

| 组件 | 说明 |
| --- | --- |
| Spring Boot 4.1.0 | Web 框架（`spring-boot-starter-webmvc`） |
| Spring AI 2.0.0 | AI 编排框架，DeepSeek 对话、ChatMemory、Advisor、工具调用 |
| DeepSeek | 大模型服务，模型 `deepseek-v4-flash` |
| MongoDB | 聊天记忆持久化（`MongoChatMemoryRepository`） |
| Redis | 已引入 `spring-boot-starter-data-redis`（预留，可扩展缓存） |
| 高德地图开放平台 | IP 定位、天气查询接口 |
| Java 17 + Maven | 构建运行 |

## 环境要求

- JDK 17+
- Maven 3.6+（或使用项目自带的 `mvnw` / `mvnw.cmd`）
- MongoDB（默认 `mongodb://root:123456@localhost:27017/ai`）
- 高德地图开放平台 API Key

## 快速开始

### 1. 配置

修改 `src/main/resources/application.yaml`：

```yaml
spring:
  ai:
    deepseek:
      chat:
        model: deepseek-v4-flash
        api-key: <你的 DeepSeek API Key>
        base-url: https://api.deepseek.com
        temperature: 0.7
  mongodb:
    uri: mongodb://root:123456@localhost:27017/ai?authSource=admin
  data:
    redis:
      host: localhost
      port: 6379
server:
  port: 9999

config:
  gaode:
    amap_ip_url: https://restapi.amap.com/v3/ip
    amap_weather: https://restapi.amap.com/v3/weather/weatherInfo
    apikey: <你的高德 API Key>
    secret: <你的高德密钥>
```

### 2. 启动

```bash
# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

启动成功后服务监听 `http://localhost:9999`。

## 接口说明

### 对话接口

```
GET /ai/chat?id={会话ID}&message={用户消息}
```

- `id`：会话 ID（`@PathParam`），同一 ID 共享聊天记忆，用于区分不同用户 / 会话。
- `message`：用户输入的消息。
- 返回：`text/plain;charset=UTF-8`，SSE 流式文本。

**示例：**

```bash
curl -N "http://localhost:9999/ai/chat?id=user-001&message=今天天气怎么样"
```

> 注意：接口返回的是模型原始流式输出（系统提示词要求模型以 JSON `{msg, qute}` 格式回答），前端需自行解析 / 渲染。

## 项目结构

```
s_ai
├── src/main/java/ai/s_ai
│   ├── SAiApplication.java          # 启动类
│   ├── controller
│   │   └── AiChatController.java    # /ai/chat 流式对话接口
│   ├── configuration
│   │   ├── ChatConfiguration.java   # ChatClient / ChatMemory / Advisor 装配
│   │   ├── MvcConfiguration.java    # 全局 CORS 跨域配置
│   │   └── RedisConfig.java         # Redis 配置（预留）
│   ├── components
│   │   └── JsonRedisTemplate.java   # Redis JSON 序列化模板（预留）
│   └── tools
│       ├── GaodeTools.java          # 高德 IP 定位 / 天气查询工具
│       └── EmotionUtil.java         # 情绪识别工具（关键词规则）
├── src/main/resources
│   ├── application.yaml             # 主配置（模型、数据库、高德 Key）
│   └── AMap_adcode_citycode.xlsx    # 高德城市编码表
├── pom.xml                          # Maven 依赖
└── readme.md
```

## 核心实现说明

### 1. 聊天记忆（ChatMemory）

`ChatConfiguration#chatMemory` 使用 `MessageWindowChatMemory`，窗口大小 20 条，底层由 `MongoChatMemoryRepository` 持久化到 MongoDB。`/ai/chat` 接口通过 Advisor 的 `ChatMemory.CONVERSATION_ID` 参数把会话 ID 绑定到内存，实现多轮上下文。

### 2. 工具调用（Tools）

`ChatClient` 注册了两个工具对象：

- `EmotionUtil`：`@Tool getEmotion`，按关键词命中判断情绪，供模型在需要共情时调用。
- `GaodeTools`：`@Tool getUserIp`（IP 定位）、`@Tool getIpWeather`（天气查询），模型在用户询问位置 / 天气 / 出行建议时自动调用。

### 3. 系统提示词

```text
你是一个善解人意的助手，输出内容要多情绪化,以json格式返回{msg:'回答用户的内容，以markdown格式返回',qute:[为用户提供三条话题]}
```

模型被要求以固定 JSON 结构返回，便于前端结构化展示。

## 常见问题

- **接口返回原始 JSON 字符串而非 Markdown**：系统提示词要求模型返回 `{msg, qute}` JSON，前端需将 `msg` 字段解析后再按 Markdown 渲染。
- **天气查询无结果**：需先确保模型正确获取到 adcode 城市编码，可参考 `AMap_adcode_citycode.xlsx` 城市编码表。
- **MongoDB 连接失败**：请确认本地 MongoDB 已启动且 `spring.mongodb.uri` 账号密码正确。
