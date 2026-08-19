
import { useMessage } from "../store/useMessage";
import { useAmount } from "../store/useAmount";
import { useConversation } from "../store/useConversation";
import { useGlobalLoad } from "../store/useGlobalLoad";
import SideMain from "../components/side/SideMain";
import MessageMain from "../components/chat/MessageMain";
import { useEffect } from "react";

export function Ai() {
    const getConversations = useConversation((s) => s.getConversations)
    const currentConversation = useConversation((s) => s.conversation)
    const getAmount = useAmount((s) => s.getAmount)
    const getMessages = useMessage((s) => s.getMessages)
    const startLoading = useGlobalLoad((s) => s.startLoading)
    const endLoading = useGlobalLoad((s) => s.endLoading)
    const initConversation = useConversation((s) => s.initConversation)

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
            <div className="w-full flex justify-center">
                <SideMain />
                <MessageMain />
            </div>
        </>
    )


}