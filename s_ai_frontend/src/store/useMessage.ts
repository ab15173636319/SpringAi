import { create } from "zustand";
import type { Message } from "../types/Message";
import { get } from "../utils/request";
import type { Response } from "../types/Request";

interface MessageState {
    messages: Message[];
    add: (message: Message) => void;
    updateLastMessage: (text: string) => void;
    getMessages: (conversationId: string) => Promise<void>;
}

export const useMessage = create<MessageState>((set) => ({
    messages: [],
    add: (message) => set((state) => ({ messages: [...state.messages, message] })),
    updateLastMessage: (text) => set((state) => {
        if (state.messages.length === 0) return state
        const copy = [...state.messages]
        const last = copy[copy.length - 1]
        copy[copy.length - 1] = { ...last, text }
        return { messages: copy }
    }),
    getMessages: async (conversationId) => {
        const res = await get<Response<Message[]>>(`/ai/currentHistory/${conversationId}`, {})
        console.log(res);
        set({ messages: res.data })
    },
}))
 