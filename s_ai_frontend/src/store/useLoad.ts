import { create } from "zustand";

interface LoadState {
    loading: boolean;
    timer: ReturnType<typeof setTimeout> | null;
    start: () => void;
    end: (duration?: number) => void;
}

export const useLoad = create<LoadState>((set, get) => ({
    loading: false,
    timer: null,
    start: () => set({ loading: true }),
    end: (duration = 100) => {
        const { timer } = get()
        if (timer)
            clearTimeout(timer)

        const newTimer = setTimeout(() => {
            set({ loading: false, timer: null })
        }, duration)
        set({ timer: newTimer })
    },
}))
