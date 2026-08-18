import { create } from "zustand";
import type { MessageData } from "../types/Message";
import { get } from "../utils/request";
import type { Response } from "../types/Request";

interface MessageState {
    conversationId: string;
    messages: MessageData[];
    add: (message: MessageData) => void;
    updateLastMessage: (text: string) => void;
    getMessages: (conversationId: string) => Promise<void>;
}

export const useMessage = create<MessageState>((set) => ({
    conversationId: "2",
    messages: [],
    add: (message) => set((state) => ({ messages: [...state.messages, message] })),

    updateLastMessage: (content: string) => set((state) => {
        if (state.messages.length === 0) return state
        const copy = [...state.messages]
        const last = copy[copy.length - 1]
        copy[copy.length - 1] = { ...last, message: { ...last.message, content } }
        return { messages: copy }
    }),


    getMessages: async (conversationId) => {
        const res = await get<Response<MessageData[]>>(`/ai/currentHistory/${conversationId}`, {})
        set({ conversationId, messages: res.data ?? [] })
    },
}))
