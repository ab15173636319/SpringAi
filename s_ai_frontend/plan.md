# 优化计划（Optimization Plan）

> 基于当前代码现状梳理，按优先级排序。状态：`pending`

## 一、严重逻辑 Bug（高优先级，必须修复）

### 1.1 无限请求次数接口  √
- 文件：`src/pages/ai.tsx:16-18` 
- 问题：`useEffect(getAmount, [messages])` 依赖 `messages`，每次流式输出 `updateLastMessage` 都会触发 `getAmount`，造成无限请求。
- 方案：改为仅在组件挂载时请求一次（`[]`），或在每次对话结束（`end()`）后调用。

### 1.2 conversationId 写死 ——
- 文件：`src/components/chat/MessageInput.tsx:27`、`src/pages/ai.tsx:21`
- 问题：`id: "2"`、`getMessages("2")` 写死字符串，无法支持多会话。
- 方案：抽成统一的会话 ID 来源（store / 路由参数 / 常量），两处共用。

### 1.3 列表使用 index 作 key √
- 文件：`src/components/chat/MessageBorad.tsx:12`
- 问题：`key={index}`，流式更新消息时会导致重渲染异常、输入框/状态错乱。
- 方案：为 `Message` 增加唯一 `id` 字段，使用 `key={item.id}`。

## 二、代码质量

### 2.1 删除调试代码 √
- 文件：`src/store/useMessage.ts:25`
- 问题：`console.log(res)` 残留。
- 方案：删除。

### 2.2 消除 any 类型 √
- 文件：`src/components/chat/MessageCharge.tsx:43`
- 问题：`useState<any[]>([])`。
- 方案：定义 `Charge` 类型并替换。

### 2.3 统一请求层与设备标识 
- 文件：`src/utils/request.ts`、`src/utils/ai.ts`
- 问题：axios 与 fetch 两套实现各自注入 `deviceId`、且流式请求 `localhost:9999` 硬编码。
- 方案：流式请求 baseURL 也走 `VITE_API_BASE_URL` 环境变量；统一设备标识注入逻辑（抽公共函数）。

## 三、体验与健壮性

### 3.1 重新审视"防扒"逻辑
- 文件：`src/utils/default.ts`
- 问题：禁用右键菜单、Ctrl+U / Ctrl+Shift+I 等，影响调试与用户体验，且无法阻止查看源码。
- 方案：移除或改为可配置开关（环境变量控制）。

### 3.2 恢复独立 Loading 组件
- 文件：`src/components/base/Spain.tsx`
- 问题：加载图标写死 `text-5xl!`，原先的 `Loading.tsx` 已被删除，缺乏语义化复用。
- 方案：抽成独立 `Loading` 组件，由 `Spain` 调用。

### 3.3 流式请求支持中断
- 文件：`src/utils/ai.ts`、`src/components/chat/MessageInput.tsx`
- 问题：发送新消息时无法中断上一次 `fetch` 流。
- 方案：引入 `AbortController`，在重新发送/卸载时 `abort()`。

### 3.4 加载状态竞态
- 文件：`src/store/useLoad.ts`
- 问题：`start()` 立即置 true，`end()` 有 100ms 延迟，快速连续发送可能产生竞态。
- 方案：用计数/引用或 `start` 时清掉旧 timer，避免提前关闭遮罩。

## 四、工程化

### 4.1 统一包管理器锁定文件
- 文件：`package-lock.json`、`pnpm-lock.yaml`
- 问题：同时存在 npm 与 pnpm 两份 lock，易造成依赖不一致。
- 方案：统一使用 pnpm，删除 `package-lock.json`。

### 4.2 环境变量文档
- 文件：`README.md`、`.env.example`
- 问题：`VITE_API_BASE_URL` 未文档化。
- 方案：补充 `.env.example` 与 README 说明。

### 4.3 类型检查门禁
- 方案：在 CI 中加入 `tsc -b` 与 `oxlint` 校验，避免问题合入。

---

## 执行顺序建议
1. 修复 1.1 / 1.2 / 1.3（功能正确性）
2. 清理 2.1 / 2.2（代码质量）
3. 完善 3.2 / 3.3 / 3.4（体验与健壮性）
4. 收尾 4.1 / 4.2 / 4.3（工程化）
