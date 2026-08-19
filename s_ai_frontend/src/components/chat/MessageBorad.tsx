import MessageBubble from "./MessageBubble.tsx";
import { useMessage } from "../../store/useMessage.ts";
import IconSend from "../../icons/Icon.tsx";

export default function MessageBoard() {
    const messages = useMessage((s) => s.messages)
    return (
        <>
            <div className={" h-full flex flex-col p-6 rounded-md dark:bg-gray-950"}>
                {
                    messages.map((item) => (
                        <MessageBubble key={item.id} {...item} />
                    ))
                }
                {
                    messages.length === 0 && <div className="w-full flex justify-center items-center">
                        <div className=" flex justify-center items-center gap-5">

                            <div className=" size-20 bg-green-200 rounded-full flex items-center justify-center">
                                <IconSend className=" text-5xl! text-gray-700" icon="Duihua" />
                            </div>
                            <h1 className=" text-2xl">快来聊天吧~~</h1>
                        </div>
                    </div>
                }
            </div>
        </>
    )
}