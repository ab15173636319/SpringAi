import { create } from "zustand";
import { get } from "../utils/request";
import type { Response } from "../types/Request";
import type { Amount } from "../types/Message";

interface AmountState {
    amount: number;
    max: number;
    getAmount: () => Promise<void>;
}

export const useAmount = create<AmountState>((set) => ({
    amount: 0,
    max: 0,
    getAmount: async () => {
        const res = await get<Response<Amount>>("/ai/getCurrentDeviceAmount", {})
        set({ amount: res.data.amount, max: res.data.max })
    },
}))
