import { useState } from "react";
import { get } from "../utils/request";

export const useAmount = () => {
    const [amount, setAmount] = useState(0);
    const [max, setMax] = useState(0);

    const getAmount = async () => {
        const res = await get<{ amount: number, max: number }>("/ai/getCurrentDeviceAmount", {})
        setAmount(res.amount)
        setMax(res.max)
    }



    return { amount, max, getAmount }
}