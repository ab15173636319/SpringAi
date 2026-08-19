import type { MessageData } from "../types/Message";
import type { Response } from "../types/Request";
import { get } from "../utils/request";

const getHistories = async (id: string): Promise<MessageData[]> => {
    console.log(id);
    
    const res = await get<Response<MessageData[]>>(`/ai/currentHistory/${id}`, {})
    return res.data;
};

export { getHistories }