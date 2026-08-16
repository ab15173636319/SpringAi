import { useState } from "react";
import type { Message } from "../types/Message";
import { get } from "../utils/request";
import type { Response } from "../types/Request";

export function useMessage() {

    const [messages, setMessages] = useState<Message[]>([])


    function add(message: Message) {
        setMessages(prev => [...prev, message])
    }

    function updateLastMessage(text: string) {
        setMessages(pre => {
            if (pre.length === 0) return pre
            const copy = [...pre]
            const last = copy[copy.length - 1]
            copy[copy.length - 1] = { ...last, text }
            return copy
        })
    }

    async function getMessages(conversationId: string) {
        const res = await get<Response<Message[]>>(`/ai/currentHistory/${conversationId}`, {})
        console.log(res);
        setMessages(res.data)

    }

    return {
        messages,
        add,
        getMessages,
        updateLastMessage
    }
}