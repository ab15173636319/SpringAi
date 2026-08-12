import type { Message } from "../../types/Message";
import Button from "../base/Button";
import MessageBoard from "./MessageBorad";
import MessageInput from "./MessageInput";

interface Amount {
    amount: number;
    max: number
}


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
                        <div className=" py-1 text-sm flex gap-4">
                            <div>
                                <span>剩余次数：</span>
                                <span className=" text-amber-800">{amount.max - amount.amount}</span>
                            </div>
                            <Button className=" py-0 text-sm bg-blue-300! text-white hover:bg-blue-600!">次数不足，购买次数</Button>
                        </div>
                    </div>
                </div>
            </div>

        </>

    )


}