import { create } from "zustand";
import type { Conversation } from "../types/Conversation";
import { createConversation, getConversation } from "../api/ConversationApi";

interface ConversationState {
    conversations: Conversation[];
    conversation: Conversation;
    getConversations: () => Promise<void>;
    addConversation: () => void;
    selConversation: (id: string) => void;
    initConversation: () => Promise<string>;
}

const useConversation = create<ConversationState>((set) => ({
    conversations: [],
    conversation: { id: "", title: "", createTime: "", updateTime: "", top: 0 },
    initConversation: async (): Promise<string> => {
        // 初始化
        // 从本地存储中获取会话ID
        const id = localStorage.getItem("conversationId") ?? "-1"
        set((state) => ({ conversation: { ...state.conversation, id } }))
        return id
    },
    getConversations: async () => {
        const res = await getConversation()
        set({ conversations: res })
    },
    addConversation: async () => {
        const res = await createConversation()
        set((state) => ({ conversations: [...state.conversations, res] }))
    },
    selConversation: (id) => {
        localStorage.setItem("conversationId", id)
        set((state) => ({ conversation: state.conversations.find(c => c.id === id) ?? { id: "", title: "", createTime: "", updateTime: "", top: 0 } }))
    },
}))

export { useConversation }