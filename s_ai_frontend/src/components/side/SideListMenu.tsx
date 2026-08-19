import { useState } from "react";
import SideList from "./SideList"
import { useConversation } from "../../store/useConversation";

export default function SideListMenu() {
    const conversations = useConversation((s) => s.conversations)
    const conversation = useConversation((s) => s.conversation)

    return (
        <>
            <div className=" flex flex-col gap-2 flex-1 scrollbar-hide">
                {
                    conversations.map((item) => {
                        return <SideList
                            key={item.id}
                            id={`${item.id}`}
                            title={`${item.title}`}
                            active={item.id === conversation.id}
                        />
                    })
                }
                {
                    conversations.length === 0 && <div className="text-center text-gray-500">暂无对话</div>
                }
            </div>
        </>
    )
}