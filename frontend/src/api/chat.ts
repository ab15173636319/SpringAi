import type { ApiResult } from "../types";
import { getAccessToken } from "../lib/token";
import { api, apiClient, refreshAccessToken } from "./client";

const baseURL = String(apiClient.defaults.baseURL ?? "/api");

async function openStream(conversationId: string, message: string, signal: AbortSignal) {
  const send = () =>
    fetch(`${baseURL}/chat/${conversationId}`, {
      method: "POST",
      credentials: "include",
      signal,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${getAccessToken() ?? ""}`,
      },
      body: JSON.stringify({ message }),
    });

  let response = await send();
  if (response.status === 401) {
    await refreshAccessToken();
    response = await send();
  }
  if (!response.ok) {
    let messageText = "发送消息失败，请稍后重试";
    try {
      const payload = (await response.json()) as ApiResult<never>;
      messageText = payload.message ?? messageText;
    } catch {
      // The server may close a stream before a JSON body is available.
    }
    throw new Error(messageText);
  }
  if (!response.body) {
    throw new Error("服务端没有返回可读取的内容");
  }
  return response.body;
}

export async function* streamChat(
  conversationId: string,
  message: string,
  signal: AbortSignal
) {
  const body = await openStream(conversationId, message, signal);
  const reader = body.getReader();
  const decoder = new TextDecoder();
  try {
    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      const chunk = decoder.decode(value, { stream: true });
      if (chunk) yield chunk;
    }
    const tail = decoder.decode();
    if (tail) yield tail;
  } finally {
    reader.releaseLock();
  }
}


interface DeleteDto {
  messageIds: string[],
  conversationId: string,
}

export async function deleteMessage(deleteDto: DeleteDto) {
  return api.post("/api/chat/delete", { deleteDto })
}