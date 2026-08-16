import type { Amount, Message } from "../../types/Message";
import Button from "../base/Button";
import MessageBoard from "./MessageBorad";
import MessageCharge from "./MessageCharge";
import MessageInput from "./MessageInput";




interface IMessageMain {
    messages: Message[];
    onSend: (msg: string) => void;
    loading: boolean;
    amount: Amount;
}

export default function MessageMain({ messages, onSend, loading, amount }: IMessageMain) {

    return (

        <>
            <div className=" w-full flex justify-center">
                <div className="w-full h-screen max-w-200 flex flex-col ">
                    <div className="flex-1 overflow-y-auto scrollbar-hide">
                        <MessageBoard messages={messages} />
                    </div>
                    <div className="relative">
                        <div className="absolute -top-6 left-0 right-0 h-6 bg-gradient-to-t from-white to-transparent dark:from-gray-950"></div>
                        <div className="absolute inset-0 -z-10 backdrop-blur-md bg-white/30 dark:bg-gray-950/30"></div>
                        <div className="">
                            <MessageInput onSend={onSend} loading={loading} />
                        </div>
                    </div>

                    <MessageCharge amount={amount} />
                </div>
            </div>

        </>

    )


}