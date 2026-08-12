import { useState } from "react";
import IconSend from "../../icons/Icon";
import Button from "../base/Button";
import Spain from "../base/Loading";

interface IMessageInput {
    onSend: (msg: string) => void;
    loading: boolean;
}

export default function MessageInput(messageInput: IMessageInput) {
    const [msg, setMsg] = useState("")

    const onKeyDown = (e: React.KeyboardEvent<HTMLButtonElement>) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            messageInput.onSend(msg);
        }
    }

    const clickHandler = () => {
        messageInput.onSend(msg);
        setMsg("")
    }

    return (
        <>

            <div className=" bg-gray-200 p-2 flex items-end gap-5 rounded-md">
                <textarea placeholder="畅所欲言......" value={msg} onChange={(e) => setMsg(e.target.value)} className=" outline-0 p-2 bg-transparent w-full resize-none max-h-40 overflow-y-auto scrollbar-none [&::-webkit-scrollbar]:hidden"></textarea>
                {/* <Spain loading={messageInput.loading} className=" rounded-md overflow-hidden"> */}
                    <Button className=" flex items-center gap-2 w-fit rounded-full" disabled={msg.trim() === ""} onClick={clickHandler} onKeyDown={onKeyDown}>
                        {messageInput.loading ? <IconSend className=" text-red-600" icon="Loading"></IconSend> : <IconSend  className=" text-blue-600" icon="Send" ></IconSend>}
                    </Button>
                {/* </Spain> */}
            </div>


        </>
    )
}