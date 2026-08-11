import { useState } from "react";
import type { Message } from "../types/Message";

export function useMessage() {

    const [messages, setMessages] = useState<Message[]>([])


    function add(message: Message) {
        setMessages(prev => [...prev, message])
    }

    function updateLastMessage(message: string) {
        setMessages(pre => {
            if (pre.length === 0) return pre
            const copy = [...pre]
            const last = copy[copy.length - 1]
            copy[copy.length - 1] = { ...last, message }
            return copy
        })
    }

    return {
        messages,
        add,
        updateLastMessage
    }
}