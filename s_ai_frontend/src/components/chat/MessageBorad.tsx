import MessageBubble from "./MessageBubble.tsx";
import { AssistantTypeValue, MessageTypeValue, type Message } from "../../types/Message.ts";

export default function MessageBoard({ messages }: { messages: Message[] }) {
    return (
        <>
            <div className={" p-4 rounded-md dark:bg-gray-950"}>

                {
                    messages.map((item, index) => (
                        <MessageBubble key={index} {...item} />
                    ))
                }
            </div>
        </>
    )
}