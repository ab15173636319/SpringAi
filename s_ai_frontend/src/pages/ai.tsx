
import { useMessage } from "../store/useMessage";
import { useAmount } from "../store/useAmount";
import SideMain from "../components/side/SideMain";
import MessageMain from "../components/chat/MessageMain";
import { useEffect } from "react";

export function Ai() {

    const getAmount = useAmount((s) => s.getAmount)

    const messages = useMessage((s) => s.messages)
    const getMessages = useMessage((s) => s.getMessages)


    useEffect(() => {
        getAmount()
    }, [messages])

    useEffect(() => {
        getMessages("2")
    }, [])

    return (
        <>
            <div className="w-full flex justify-center">
                <SideMain />
                <MessageMain />
            </div>
        </>
    )


}