import MessageBubble from "./MessageBubble.tsx";
import {AssistantTypeValue, MessageTypeValue} from "../../types/Message.ts";

export default function MessageBoard() {
    return (
        <>
            <div className={" p-4 rounded-md dark:bg-gray-950"}>

                <MessageBubble
                    assistant={AssistantTypeValue.User}
                    type={MessageTypeValue.Text}
                    message="你好"/>
                <MessageBubble
                    assistant={AssistantTypeValue.Assistant}
                    type={MessageTypeValue.Text}
                    message="你好，我是s_ai"
                />

            </div>
        </>
    )
}