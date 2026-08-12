
import { useLoad } from "../hooks/useLoad";
import { AssistantTypeValue, MessageTypeValue, type Message, type MessageSend } from "../types/Message";
import { sendMessage } from "../utils/ai";
import { useMessage } from "../hooks/useMessage";
import { useAmount } from "../store/useAmount";
import SideMain from "../components/side/SideMain";
import MessageMain from "../components/chat/MessageMain";
import { useEffect } from "react";

export function Ai() {
    const { start, end, loading } = useLoad()
    const { getAmount, amount, max } = useAmount()
    const { messages, add, updateLastMessage } = useMessage()


    useEffect(() => {
        getAmount()
    }, [messages])

    async function sendChat(msg: string) {
        start()

        add({
            assistant: AssistantTypeValue.User,
            type: MessageTypeValue.Text,
            message: msg,
        })

        const obj: MessageSend = {
            id: "2",
            message: msg
        }

        add({
            assistant: AssistantTypeValue.Assistant,
            type: MessageTypeValue.Text,
            message: "",
        })


        let str = ""
        for await (const chunk of sendMessage(obj)) {
            str += chunk
            updateLastMessage(str)
        }

        end()

    }


    return (
        <>

            <div className="w-full flex justify-center">
                <SideMain />
                <MessageMain messages={messages} onSend={sendChat} loading={loading} amount={{ amount, max }} />
            </div>

        </>
    )


}