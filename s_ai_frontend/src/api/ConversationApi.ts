import type { Conversation } from "../types/Conversation";
import type { Response } from "../types/Request";
import { get, post } from "../utils/request";
const createConversation = async (): Promise<Conversation> => {
    const res = await post<Response<Conversation>>("/conversation/create", {})
    return res.data
}

const getConversation = async (): Promise<Conversation[]> => {
    const res = await get<Response<Conversation[]>>("/conversation/list", {})
    return res.data
}

const modifyConversation = async ({ id, title }: { id: string, title: string }): Promise<Conversation> => {
    const res = await post<Response<Conversation>>("/conversation/modify", { id, title })
    return res.data
}

const topConversation = async (id: string): Promise<Conversation> => {
    const res = await post<Response<Conversation>>(`/conversation/top/${id}`, {})
    return res.data
}


export { createConversation, getConversation, modifyConversation, topConversation }