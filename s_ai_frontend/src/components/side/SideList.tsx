import { useCallback, useEffect, useRef, useState } from "react";
import IconSend from "../../icons/Icon";
import SideListMorePopup from "./SideListMorePopup";
import { useConversation } from "../../store/useConversation";


interface ISideList {
    title: string;
    id: string;
    active: boolean;
}

export default function SideList({
    title,
    id,
    active = true
}: ISideList) {
    const target = useRef<HTMLDivElement>(null)
    const [popupOpen, setPopupOpen] = useState(false)
    const selConversation = useConversation((s) => s.selConversation)
    const [hovered, setHovered] = useState(false);
    const clickHandler = () => {
        selConversation(id)
    }

    const handlerClickOutside = useCallback((e: MouseEvent | TouchEvent) => {
        if (target.current && !target.current.contains(e.target as Node)) {
            setPopupOpen(false)
        }
    }, [])


    useEffect(() => {
        if (!popupOpen) return
        window.addEventListener('mousedown', handlerClickOutside)
        window.addEventListener("touchstart", handlerClickOutside)
        return () => {
            document.removeEventListener('mousedown', handlerClickOutside)
            document.removeEventListener('touchstart', handlerClickOutside)
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
                <div className="text-[14px]">{title}</div>
                <div className={`text-gray-400 ${hovered ? " opacity-100" : "opacity-0"}`} onClick={() => { setPopupOpen((prev) => !prev) }}>
                    <IconSend icon="Gengduo1" />
                </div>
                {popupOpen ? <SideListMorePopup id={id} /> : ""}
            </div >

        </>
    )
}