import { useState } from "react";
import IconSend from "../../icons/Icon";
import Button from "../base/Button";
import { useLoad } from "../../store/useLoad";
import { useMessage } from "../../store/useMessage";
import { AssistantTypeValue, type MessageSend } from "../../types/Message";
import { sendMessage } from "../../utils/ai";


export default function MessageInput() {
    const [msg, setMsg] = useState("")
    const start = useLoad((s) => s.start)
    const end = useLoad((s) => s.end)
    const loading = useLoad((s) => s.loading)
    const add = useMessage((s) => s.add)
    const updateLastMessage = useMessage((s) => s.updateLastMessage)
    const conversationId = useMessage((s) => s.conversationId)

    const clickHandler = async () => {
        start()

        add({
            id: crypto.randomUUID(),
            conversationId,
            message: { content: msg, type: AssistantTypeValue.User },
            timestamp: new Date().toISOString(),
        })

        const obj: MessageSend = {
            id: conversationId,
            message: msg
        }

        add({
            id: crypto.randomUUID(),
            conversationId,
            message: { content: "工作中......", type: AssistantTypeValue.Assistant },
            timestamp: new Date().toISOString(),
        })

        let str = ""
        try {
            for await (const chunk of sendMessage(obj)) {
                str += chunk
                updateLastMessage(str)
            }
        } catch (error) {
            updateLastMessage(error instanceof Error ? error.message : "未知错误")
        } finally {
            end()
        }
        setMsg("")
    }

    return (
        <>
            <div className=" bg-gray-200 p-2 flex items-end gap-5 rounded-md">
                <textarea placeholder="畅所欲言......" value={msg} onChange={(e) => setMsg(e.target.value)} className=" outline-0 p-2 bg-transparent w-full resize-none max-h-40 overflow-y-auto scrollbar-none [&::-webkit-scrollbar]:hidden"></textarea>
                <Button className=" flex items-center gap-2 w-fit rounded-full" disabled={msg.trim() === ""} onClick={clickHandler}>
                    {
                        loading ?
                            <IconSend className=" text-red-600" icon="Loading"></IconSend>
                            :
                            <IconSend className=" text-blue-600" icon="Send" ></IconSend>
                    }
                </Button>
            </div>


        </>
    )
}