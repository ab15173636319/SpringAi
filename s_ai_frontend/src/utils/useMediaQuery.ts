import { useEffect, useState } from "react"

/**
 * 监听 CSS 媒体查询，返回当前是否匹配。
 * 首次渲染即返回真实匹配结果，避免闪烁。
 */
export function useMediaQuery(query: string): boolean {
    const [matches, setMatches] = useState(() =>
        typeof window !== "undefined" ? window.matchMedia(query).matches : false
    )

    useEffect(() => {
        const mql = window.matchMedia(query)
        const handler = (event: MediaQueryListEvent) => setMatches(event.matches)

        setMatches(mql.matches)
        mql.addEventListener("change", handler)
        return () => mql.removeEventListener("change", handler)
    }, [query])

    return matches
}
