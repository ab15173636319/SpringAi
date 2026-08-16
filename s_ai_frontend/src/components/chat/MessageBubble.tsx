import { AssistantTypeValue, type Message } from "../../types/Message.ts";
import { markdown } from "../../utils/markdown.ts";

export default function MessageBubble(props: Message) {
    const { messageType, text } = props;

    return (
        <>
            <div className={
                messageType === AssistantTypeValue.User ?
                    "flex justify-end p-2 rounded-md"
                    :
                    "flex justify-start"}>
                <article className={
                    messageType === AssistantTypeValue.Assistant ?
                        " w-auto max-w-[75%] bg-gray-200 py-2 px-3 rounded-md"
                        :
                        "w-fit max-w-[75%]  py-2 px-3"}>
                    <div className="wrap-break-word" dangerouslySetInnerHTML={{ __html: markdown.parse(text) }}>
                    </div>
                </article>
            </div>

        </>
    )


}