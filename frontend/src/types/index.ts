export interface ApiResult<T> {
  code: number;
  message: string;
  data: T;
  timestamp: string;
}

export type UserRole = "USER" | "ADMIN";

export interface User {
  id: string;
  username: string;
  displayName: string;
  email: string;
  avatarColor: string;
  role: UserRole;
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  expiresIn: number;
  user: User;
}

export interface Conversation {
  id: string;
  title: string;
  model: string;
  pinned: boolean;
  createdAt: string;
  updatedAt: string;
}

export type MessageRole = "USER" | "ASSISTANT";

export interface ChatMessage {
  id: string;
  conversationId: string;
  role: MessageRole;
  content: string;
  createdAt: string;
  pending?: boolean;
  error?: boolean;
  /** 仅存在于本地、后端尚无对应记录（id 还是临时值） */
  unsynced?: boolean;
}

export interface Usage {
  used: number;
  limit: number;
  day: string;
}

export interface SystemInfo {
  mode: "live" | "demo";
  model: string;
  dailyLimit: number;
}

