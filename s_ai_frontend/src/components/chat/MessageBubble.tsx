import {AssistantTypeValue, type Message} from "../../types/Message.ts";

export default function MessageBubble(props: Message) {
    const {assistant, message} = props;


    return (
        <>
            <div className={
                assistant === AssistantTypeValue.User ?
                    "flex justify-end p-2 rounded-md"
                    :
                    "flex justify-start"}>
                <article className={
                    assistant === AssistantTypeValue.User ?
                        " w-fit bg-gray-200 py-2 px-3 rounded-md"
                        :
                        ""}>
                    <p>
                        {message}
                    </p>
                </article>
            </div>

        </>
    )


}