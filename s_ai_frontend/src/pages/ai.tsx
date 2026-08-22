
import { useMessage } from "../store/useMessage";
import { useAmount } from "../store/useAmount";
import { useConversation } from "../store/useConversation";
import { useGlobalLoad } from "../store/useGlobalLoad";
import SideMain from "../components/side/SideMain";
import MessageMain from "../components/chat/MessageMain";
import { useEffect, useRef, useState } from "react";
import { useMediaQuery } from "../utils/useMediaQuery";

export function Ai() {
    // 响应式侧边栏：大屏(≥1024px)默认展开，中屏(768~1023px)默认收起，小屏(<768px)使用定位抽屉
    const isMobile = useMediaQuery("(max-width: 767px)")
    const isLarge = useMediaQuery("(min-width: 1024px)")
    const [isOpen, setIsOpen] = useState(() => !isMobile && isLarge)
    const userPrefRef = useRef(isOpen)

    const getConversations = useConversation((s) => s.getConversations)
    const currentConversation = useConversation((s) => s.conversation)
    const getAmount = useAmount((s) => s.getAmount)
    const getMessages = useMessage((s) => s.getMessages)
    const startLoading = useGlobalLoad((s) => s.startLoading)
    const endLoading = useGlobalLoad((s) => s.endLoading)
    const initConversation = useConversation((s) => s.initConversation)

    const togglOpenSide = () => {
        setIsOpen((prev) => {
            const next = !prev
            if (!isMobile) {
                userPrefRef.current = next
            }
            return next
        })
    }

    // 进入移动端时强制收起；离开移动端或跨非移动端断点时恢复用户偏好，避免已折叠的侧边栏闪烁弹出
    useEffect(() => {
        if (isMobile) {
            setIsOpen(false)
        } else {
            setIsOpen(userPrefRef.current)
        }
    }, [isMobile])

    // 移动端选择会话后收起抽屉
    useEffect(() => {
        if (isMobile) setIsOpen(false)
    }, [currentConversation.id, isMobile])

    useEffect(() => {
        (async () => {
            startLoading()
            try {
                const id = await initConversation()
                if (id.trim()) {
                    getMessages(id)
                }
                getConversations()
                getAmount()
            } catch (error) {
                throw new Error(error instanceof Error ? error.message : "未知错误")
            } finally {
                endLoading()
            }
        })()
    }, [])


    useEffect(() => {
        startLoading()
        try {
            getMessages(currentConversation.id)
        } catch (error) {
            throw new Error(error instanceof Error ? error.message : "未知错误")
        } finally {
            endLoading()
        }
    }, [currentConversation])

    return (
        <>
            <div className="flex w-full">
                <SideMain isOpen={isOpen} isMobile={isMobile} togglOpenSide={togglOpenSide} />
                <div className="flex-1 min-w-0">
                    <MessageMain />
                </div>
            </div>
        </>
    )


}
