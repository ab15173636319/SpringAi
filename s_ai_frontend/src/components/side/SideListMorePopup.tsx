import { deleteConversation, topConversation } from "../../api/ConversationApi";
import { useConversation } from "../../store/useConversation";
import IconSend from "../../icons/Icon";

interface ISideListMorePopup {
    id: string;
    onUpdate: () => void
}

export default function SideListMorePopup({ id, onUpdate }: ISideListMorePopup) {
    const getConversations = useConversation((s) => s.getConversations)
    const selConversation = useConversation((s) => s.selConversation)
    
    // 置顶
    const topHandler = async (id: string) => {
        await topConversation(id)
        await getConversations()
    }

    const deleteHandler = async (id: string) => {
        await deleteConversation(id)
        await getConversations()
        selConversation("")

    }


    return (
        <>
            <div className=" absolute top-11/12 left-10/12 z p-4 bg-white shadow-[0_0_10px_rgba(0,0,0,0.2)] w-30 rounded-md select-none z-20 flex flex-col gap-2">
                <div className=" text-gray-500 hover:bg-gray-200 px-2 py-1 rounded-md" onClick={onUpdate}>
                    <IconSend icon="Xiugai">修改</IconSend>
                </div>
                <div className=" text-gray-500 hover:bg-gray-200 px-2 py-1 rounded-md" onClick={() => topHandler(id)}>
                    <IconSend icon="Zhiding">置顶</IconSend>
                </div>
                <div className=" text-red-500 hover:bg-red-200 px-2 py-1 rounded-md" onClick={() => deleteHandler(id)} >
                    <IconSend icon="Shanchu">删除</IconSend>
                </div>
            </div>
        </>
    )
}