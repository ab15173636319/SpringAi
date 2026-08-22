import { useCallback, useEffect, useRef, useState } from "react";
import IconSend from "../../icons/Icon";
import SideListMorePopup from "./SideListMorePopup";
import { useConversation } from "../../store/useConversation";
import { modifyConversation } from "../../api/ConversationApi";


interface ISideList {
    title: string;
    id: string;
    active: boolean;
    isTop: boolean;
}

export default function SideList({
    title,
    id,
    active = true,
    isTop = false
}: ISideList) {
    const target = useRef<HTMLDivElement>(null)
    const [popupOpen, setPopupOpen] = useState(false)
    const [isUpdate, setIsUpdate] = useState(false)
    const [updateTitle, setUpdateTitle] = useState(title)
    const selConversation = useConversation((s) => s.selConversation)
    const getConversations = useConversation((s) => s.getConversations)
    const [hovered, setHovered] = useState(false);
    const clickHandler = () => {
        if (!id) return
        selConversation(id)
    }

    const handlerClickOutside = useCallback((e: MouseEvent | TouchEvent) => {
        if (target.current && !target.current.contains(e.target as Node)) {
            setPopupOpen(false)
        }
    }, [])

    const toggleUpdate = () => {
        setIsUpdate((prev) => !prev)
    }

    const submitHandler = async () => {
        await modifyConversation({ id, title: updateTitle })
        await getConversations()
        setIsUpdate(false)
    }


    useEffect(() => {
        if (!popupOpen) return
        window.addEventListener('mousedown', handlerClickOutside)
        window.addEventListener("touchstart", handlerClickOutside)
        return () => {
            window.removeEventListener('mousedown', handlerClickOutside)
            window.removeEventListener('touchstart', handlerClickOutside)
        }
    }, [popupOpen])

    return (
        <>
            <div ref={target} onClick={clickHandler}
                className={
                    "w-full flex items-center justify-between rounded-[999px] p-2 px-4 cursor-pointer relative " +
                    (active ? " bg-blue-50 text-blue-700" : "hover:bg-gray-100 text-gray-800")
                }
                onMouseEnter={() => setHovered(true)}
                onMouseLeave={() => setHovered(false)}
            >
                <div className="text-[14px]">
                    {
                        isUpdate
                            ?
                            <div>
                                <input autoFocus onBlur={submitHandler} className=" border outline-0 border-amber-500 rounded-md" type="text" value={updateTitle} onChange={(e) => setUpdateTitle(e.target.value)} />
                            </div>
                            :
                            <div className=" flex gap-1 items-center">
                                <span>{title}</span>
                                {isTop ? <IconSend className=" text-sm" icon="Zhiding" /> : ""}
                            </div>
                    }
                </div>
                <div className={`text-gray-400 ${hovered ? " opacity-100" : "opacity-0"}`} onClick={() => { setPopupOpen((prev) => !prev) }}>
                    <IconSend icon="Gengduo1" />
                </div>
                {popupOpen ? <SideListMorePopup id={id} onUpdate={toggleUpdate} /> : ""}
            </div >

        </>
    )
}