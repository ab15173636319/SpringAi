import type { IconNameKey } from "../../types/IconName";
import { useConversation } from "../../store/useConversation";
import HoverTExtButton from "../base/HoverTextButton";

interface IMenuList {
    id: number,
    title: string,
    icon: IconNameKey,
    onClick: () => void
}


export default function SideHiddenHider({ togglOpenSide }: { togglOpenSide: () => void }) {
    const addConversation = useConversation((s) => s.addConversation)
    const menuList: IMenuList[] = [
        {
            id: 1,
            title: "展开",
            icon: "Afenleizhediecebianlan",
            onClick: () => {
                togglOpenSide()
            }
        },
        {
            id: 2,
            title: "新对话",
            icon: "Tianjiazengjiajia",
            onClick: () => {
                addConversation()
            }
        },
    ]


    return (
        <>
            <div className="flex items-center fixed top-2 left-2 bg-white z-50 shadow w-fit px-8 rounded-[999px]">
                <div className=" flex items-center gap-2">
                    <img className=" w-8" src="/logo.png" alt="刘某的聊天机器人" />
                </div>
                {
                    menuList.map((item) => (
                        <HoverTExtButton key={item.id} icon={item.icon} hoverText={item.title} onClick={item.onClick} />
                    ))
                }

            </div>
        </>
    )
}