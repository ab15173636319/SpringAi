import MessageBoard from "../components/chat/MessageBorad";
import MessageInput from "../components/chat/MessageInput";
import { useLoad } from "../hooks/useLoad";
import { AssistantTypeValue, MessageTypeValue, type Message, type MessageSend } from "../types/Message";
import { sendMessage } from "../utils/ai";
import { useMessage } from "../hooks/useMessage";

export function Ai() {
    const { start, end, loading } = useLoad()
    const { messages, add, updateLastMessage } = useMessage()

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
            str+=chunk
            updateLastMessage(str)
        }

        end()

    }


    return (
        <>

            <div className="w-full flex justify-center">
                <div className="w-full max-w-225">
                    <MessageBoard messages={messages} />
                    <MessageInput onSend={sendChat} loading={loading} />
                </div>
            </div>

        </>
    )


}