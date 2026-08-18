import { AssistantTypeValue, type MessageData } from "../../types/Message.ts";
import { markdown } from "../../utils/markdown.ts";

export default function MessageBubble(props: MessageData) {
    const { message: { content, type } } = props;

    return (
        <>
            <div className={
                type === AssistantTypeValue.User ?
                    "flex justify-end p-2 rounded-md"
                    :
                    "flex justify-start"}>
                <article className={
                    type === AssistantTypeValue.Assistant ?
                        " w-auto max-w-[75%] bg-gray-200 py-2 px-3 rounded-md"
                        :
                        "w-fit max-w-[75%]  py-2 px-3"}>
                    <div className="wrap-break-word" dangerouslySetInnerHTML={{ __html: markdown.parse(content) }}>
                    </div>
                </article>
            </div>

        </>
    )


}