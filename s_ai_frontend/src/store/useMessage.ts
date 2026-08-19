import { create } from "zustand";
import type { MessageData } from "../types/Message";
import { getHistories } from "../api/ChatApi";

interface MessageState {
    id: string;
    messages: MessageData[];
    add: (message: MessageData) => void;
    updateLastMessage: (text: string) => void;
    getMessages: (conversationId: string) => Promise<void>;
}

export const useMessage = create<MessageState>((set) => ({
    id: "2",
    messages: [],

    // 添加消息到本地数组
    add: (message) => set((state) => ({ messages: [...state.messages, message] })),

    updateLastMessage: (content: string) => set((state) => {
        if (state.messages.length === 0) return state
        const copy = [...state.messages]
        const last = copy[copy.length - 1]
        copy[copy.length - 1] = { ...last, message: { ...last.message, content } }
        return { messages: copy }
    }),

    // 获取历史消息
    getMessages: async (id: string) => {
        console.log(id)
        const data = await getHistories(id)
        set({ id, messages: data ?? [] })
    },
}))
