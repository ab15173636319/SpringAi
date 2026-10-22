import type { ChatMessage, Conversation } from "../types";
import { api } from "./client";

export async function listConversations() {
  return api.get<Conversation[]>("/conversations");
}

export async function createConversation() {
  return api.post<Conversation>("/conversations");
}

export async function renameConversation(id: string, title: string) {
  return api.patch<Conversation>(`/conversations/${id}`, { title });
}

export async function toggleConversationPin(id: string) {
  return api.patch<Conversation>(`/conversations/${id}/pin`);
}

export async function deleteConversation(id: string) {
  await api.delete(`/conversations/${id}`);
}

export async function clearConversation(id: string) {
  await api.post(`/conversations/${id}/clear`);
}

export async function getMessages(id: string) {
  return api.get<ChatMessage[]>(`/conversations/${id}/messages`);
}
