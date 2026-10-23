import { create } from "zustand";
import * as conversationApi from "../api/conversations";
import { deleteMessages as requestDeleteMessages, streamChat } from "../api/chat";
import * as workspaceApi from "../api/workspace";
import type {
  ChatMessage,
  Conversation,
  SystemInfo,
  Usage,
} from "../types";

interface ChatState {
  conversations: Conversation[];
  activeId: string | null;
  messages: ChatMessage[];
  usage: Usage | null;
  system: SystemInfo | null;
  initializing: boolean;
  loadingMessages: boolean;
  streaming: boolean;
  error: string | null;
  abortController: AbortController | null;
  isDeletion: boolean;
  messageArray: string[];
  initialize: () => Promise<void>;
  selectConversation: (id: string) => Promise<void>;
  createConversation: () => Promise<void>;
  sendMessage: (content: string) => Promise<void>;
  stopStreaming: () => void;
  deleteMessage: (id: string) => void;
  deleteMessages: (ids: string[]) => Promise<void>;
  renameConversation: (id: string, title: string) => Promise<void>;
  togglePin: (id: string) => Promise<void>;
  deleteConversation: (id: string) => Promise<void>;
  clearConversation: () => Promise<void>;
  refreshUsage: () => Promise<void>;
  clearError: () => void;
  enableDelete: () => void;
  changeMessageArray: (id: string) => void;
}

function sortConversations(items: Conversation[]) {
  return [...items].sort((left, right) => {
    if (left.pinned !== right.pinned) return left.pinned ? -1 : 1;
    return new Date(right.updatedAt).getTime() - new Date(left.updatedAt).getTime();
  });
}

/**
 * 把服务端新落库的消息按角色顺序配对到本地乐观消息上，用真实的 MongoDB _id 替换临时 id。
 * 本地 id 由 crypto.randomUUID() 生成，只是占位；后端删除只认 _id，不替换就会出现
 * “第一次删除无效、刷新后才能删掉”的现象。
 */
function adoptServerIds(
  messages: ChatMessage[],
  freshMessages: ChatMessage[],
  optimisticIds: string[]
) {
  if (freshMessages.length === 0) return null;
  const adopted = new Map<string, ChatMessage>();
  let cursor = 0;
  for (const id of optimisticIds) {
    const local = messages.find((message) => message.id === id);
    if (!local) continue;
    const index = freshMessages.findIndex(
      (message, position) => position >= cursor && message.role === local.role
    );
    if (index === -1) continue;
    adopted.set(id, freshMessages[index]);
    cursor = index + 1;
  }
  if (adopted.size === 0) return null;
  return messages.map((message) => {
    const persisted = adopted.get(message.id);
    if (!persisted) return message;
    return {
      ...message,
      id: persisted.id,
      conversationId: persisted.conversationId ?? message.conversationId,
      createdAt: persisted.createdAt,
      unsynced: false,
    };
  });
}

/**
 * 流式结束后拉取最新消息，把本轮落库消息的真实 id 回填到本地。
 * 中断场景下后端可能在连接关闭后才写入助手回复，故带一次延迟重试。
 */
async function syncPersistedMessages(
  conversationId: string,
  knownIds: Set<string>,
  optimisticIds: string[]
) {
  for (let attempt = 0; attempt < 2; attempt += 1) {
    if (attempt > 0) {
      await new Promise((resolve) => setTimeout(resolve, 400));
    }
    if (useChatStore.getState().activeId !== conversationId) return;
    let serverMessages: ChatMessage[];
    try {
      serverMessages = await conversationApi.getMessages(conversationId);
    } catch {
      return;
    }
    const state = useChatStore.getState();
    if (state.activeId !== conversationId) return;
    const fresh = serverMessages.filter((message) => !knownIds.has(message.id));
    const next = adoptServerIds(state.messages, fresh, optimisticIds);
    if (!next) return;
    useChatStore.setState({ messages: next });
    const pendingIds = optimisticIds.filter((id) =>
      next.some((message) => message.id === id)
    );
    if (pendingIds.length === 0) return;
  }
}

export const useChatStore = create<ChatState>((set, get) => ({
  conversations: [],
  activeId: null,
  messages: [],
  usage: null,
  system: null,
  initializing: true,
  loadingMessages: false,
  streaming: false,
  error: null,
  abortController: null,
  isDeletion: false,
  messageArray: [],

  initialize: async () => {
    set({ initializing: true, error: null });
    try {
      const [items, usage, system] = await Promise.all([
        conversationApi.listConversations(),
        workspaceApi.getUsage(),
        workspaceApi.getSystemInfo(),
      ]);
      let conversations = items;
      if (conversations.length === 0) {
        conversations = [await conversationApi.createConversation()];
      }
      const activeId = conversations[0]?.id ?? null;
      set({
        conversations: sortConversations(conversations),
        activeId,
        usage,
        system,
      });
      if (activeId) {
        const messages = await conversationApi.getMessages(activeId);
        set({ messages });
      }
    } catch (error) {
      set({ error: error instanceof Error ? error.message : "工作区加载失败" });
    } finally {
      set({ initializing: false });
    }
  },

  selectConversation: async (id) => {
    if (id === get().activeId && !get().loadingMessages) return;
    get().stopStreaming();
    set({ activeId: id, loadingMessages: true, error: null, messages: [] });
    try {
      const messages = await conversationApi.getMessages(id);
      if (get().activeId === id) {
        set({ messages });
      }
    } catch (error) {
      set({ error: error instanceof Error ? error.message : "消息加载失败" });
    } finally {
      if (get().activeId === id) set({ loadingMessages: false });
    }
  },

  createConversation: async () => {
    get().stopStreaming();
    try {
      const conversation = await conversationApi.createConversation();
      set((state) => ({
        conversations: sortConversations([
          conversation,
          ...state.conversations,
        ]),
        activeId: conversation.id,
        messages: [],
        error: null,
      }));
    } catch (error) {
      set({ error: error instanceof Error ? error.message : "新建会话失败" });
    }
  },

  sendMessage: async (content) => {
    const text = content.trim();
    const conversationId = get().activeId;
    if (!text || !conversationId || get().streaming) return;

    const now = new Date().toISOString();
    // 记录发送前的已知 id，流结束后据此识别本轮新落库的消息
    const knownIds = new Set(get().messages.map((message) => message.id));
    const userMessage: ChatMessage = {
      id: crypto.randomUUID(),
      conversationId,
      role: "USER",
      content: text,
      createdAt: now,
      unsynced: true,
    };
    const assistantMessage: ChatMessage = {
      id: crypto.randomUUID(),
      conversationId,
      role: "ASSISTANT",
      content: "",
      createdAt: now,
      pending: true,
      unsynced: true,
    };
    const controller = new AbortController();
    set((state) => ({
      messages: [...state.messages, userMessage, assistantMessage],
      streaming: true,
      error: null,
      abortController: controller,
    }));

    let accumulated = "";
    try {
      for await (const chunk of streamChat(conversationId, text, controller.signal)) {
        accumulated += chunk;
        set((state) => ({
          messages: state.messages.map((message) =>
            message.id === assistantMessage.id
              ? { ...message, content: accumulated }
              : message
          ),
        }));
      }
      set((state) => ({
        messages: state.messages.map((message) =>
          message.id === assistantMessage.id
            ? { ...message, content: accumulated, pending: false }
            : message
        ),
      }));
    } catch (error) {
      if (controller.signal.aborted) {
        set((state) => ({
          messages: state.messages.map((message) =>
            message.id === assistantMessage.id
              ? {
                ...message,
                pending: false,
                content: accumulated || "回答已停止",
              }
              : message
          ),
        }));
      } else {
        const message =
          error instanceof Error ? error.message : "消息发送失败，请稍后重试";
        set((state) => ({
          error: message,
          messages: state.messages.map((item) =>
            item.id === assistantMessage.id
              ? {
                ...item,
                content: accumulated || message,
                pending: false,
                error: !accumulated,
              }
              : item
          ),
        }));
      }
    } finally {
      set({ streaming: false, abortController: null });
      Promise.all([
        conversationApi.listConversations().then((items) =>
          set({ conversations: sortConversations(items) })
        ),
        workspaceApi.getUsage().then((usage) => set({ usage })),
      ]).catch(() => undefined);
      await syncPersistedMessages(conversationId, knownIds, [
        userMessage.id,
        assistantMessage.id,
      ]);
    }
  },

  stopStreaming: () => {
    const controller = get().abortController;
    if (controller) controller.abort();
  },

  deleteMessage: (id) => {
    const target = get().messages.find((message) => message.id === id);
    if (!target || target.pending) return;
    set((state) => ({
      messages: state.messages.filter((message) => message.id !== id),
    }));
  },

  // 批量删除：先乐观移除并退出删除模式，失败时回滚消息与选中态
  deleteMessages: async (ids) => {
    const conversationId = get().activeId;
    const targets = get().messages.filter(
      (message) => ids.includes(message.id) && !message.pending
    );
    if (!conversationId || targets.length === 0) return;

    const targetIds = targets.map((message) => message.id);
    // 未落库的本地消息后端并不存在，只做本地移除，绝不把临时 id 提交给后端
    const persistedIds = targets
      .filter((message) => !message.unsynced)
      .map((message) => message.id);

    const snapshot = get().messages;
    set({
      messages: snapshot.filter((message) => !targetIds.includes(message.id)),
      isDeletion: false,
      messageArray: [],
      error: null,
    });

    if (persistedIds.length === 0) return;

    try {
      await requestDeleteMessages({ conversationId, messageIds: persistedIds });
      conversationApi
        .listConversations()
        .then((items) => set({ conversations: sortConversations(items) }))
        .catch(() => undefined);
    } catch (error) {
      set({
        messages: snapshot,
        isDeletion: true,
        messageArray: targetIds,
        error: error instanceof Error ? error.message : "删除消息失败",
      });
    }
  },

  renameConversation: async (id, title) => {
    try {
      const updated = await conversationApi.renameConversation(id, title);
      set((state) => ({
        conversations: sortConversations(
          state.conversations.map((item) => (item.id === id ? updated : item))
        ),
      }));
    } catch (error) {
      set({ error: error instanceof Error ? error.message : "重命名失败" });
      throw error;
    }
  },

  togglePin: async (id) => {
    try {
      const updated = await conversationApi.toggleConversationPin(id);
      set((state) => ({
        conversations: sortConversations(
          state.conversations.map((item) => (item.id === id ? updated : item))
        ),
      }));
    } catch (error) {
      set({ error: error instanceof Error ? error.message : "操作失败" });
    }
  },

  deleteConversation: async (id) => {
    get().stopStreaming();
    try {
      await conversationApi.deleteConversation(id);
      const remaining = get().conversations.filter((item) => item.id !== id);
      if (remaining.length === 0) {
        const conversation = await conversationApi.createConversation();
        set({
          conversations: [conversation],
          activeId: conversation.id,
          messages: [],
        });
      } else {
        const activeId = get().activeId === id ? remaining[0].id : get().activeId;
        set({ conversations: remaining, activeId });
        if (get().activeId === remaining[0].id) {
          await get().selectConversation(remaining[0].id);
        }
      }
    } catch (error) {
      set({ error: error instanceof Error ? error.message : "删除会话失败" });
    }
  },

  clearConversation: async () => {
    const id = get().activeId;
    if (!id) return;
    get().stopStreaming();
    try {
      await conversationApi.clearConversation(id);
      const items = await conversationApi.listConversations();
      set({
        messages: [],
        conversations: sortConversations(items),
      });
    } catch (error) {
      set({ error: error instanceof Error ? error.message : "清空会话失败" });
    }
  },

  refreshUsage: async () => {
    try {
      set({ usage: await workspaceApi.getUsage() });
    } catch {
      // Usage is non-critical; the next successful action will refresh it.
    }
  },

  enableDelete: () => {
    set((state) => ({ isDeletion: !state.isDeletion }));
  },

  changeMessageArray: (id: string) => {
    set((state) => ({
      messageArray: state.messageArray.includes(id)
        ? state.messageArray.filter((item) => item !== id)
        : [...state.messageArray, id],
    }));
  },

  clearError: () => set({ error: null }),
}));

