import MessageBubble from "./MessageBubble.tsx";
import { useMessage } from "../../store/useMessage.ts";

export default function MessageBoard() {
    const messages = useMessage((s) => s.messages)
    return (
        <>
            <div className={" p-4 rounded-md dark:bg-gray-950"}>

                {
                    messages.map((item) => (
                        <MessageBubble key={item.id} {...item} />
                    ))
                }
            </div>
        </>
    )
}