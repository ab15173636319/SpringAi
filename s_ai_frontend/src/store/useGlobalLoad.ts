import { create } from "zustand";



interface GlobalLoadState {
    loading: boolean;
    setLoading: (loading: boolean) => void;
    startLoading: () => void;
    endLoading: () => void;
}


export const useGlobalLoad = create<GlobalLoadState>((set) => ({
    loading: false,
    setLoading: (loading) => set({ loading }),
    startLoading: () => set({ loading: true }),
    endLoading: () => {
        let timer = setTimeout(() => {
            set({ loading: false })
        }, 100)
        return () => {
            clearTimeout(timer)
        }
    },
}));