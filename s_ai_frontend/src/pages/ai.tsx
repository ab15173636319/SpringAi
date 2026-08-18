
import { useMessage } from "../store/useMessage";
import { useAmount } from "../store/useAmount";
import SideMain from "../components/side/SideMain";
import MessageMain from "../components/chat/MessageMain";
import { useEffect } from "react";

export function Ai() {

    const getAmount = useAmount((s) => s.getAmount)
    const getMessages = useMessage((s) => s.getMessages)


    useEffect(() => {
        getAmount()
    }, [])

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