import { useState } from "react";

export function useLoad(initValue: boolean = false) {
    const [loading, setLoading] = useState(initValue)

    function start() {
        setLoading(true)
    }

    /**
     * 
     * @param delay 持续时间
     */
    function end(duration: number = 100) {
        let timer: number | null = null
        if (timer)
            clearTimeout(timer)

        timer = setTimeout(() => {
            setLoading(false)
        }, duration)
    }

    return {
        loading,
        start,
        end
    }

}