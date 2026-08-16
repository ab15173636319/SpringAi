import { useState } from "react";
import { get } from "../utils/request";
import type { Response } from "../types/Request";
import type { Amount } from "../types/Message";

export const useAmount = () => {
    const [amount, setAmount] = useState(0);
    const [max, setMax] = useState(0);

    const getAmount = async () => {
        const res = await get<Response<Amount>>("/ai/getCurrentDeviceAmount", {})
        setAmount(res.data.amount)
        setMax(res.data.max)
    }



    return { amount, max, getAmount }
}