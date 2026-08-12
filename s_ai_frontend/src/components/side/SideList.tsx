import { useState } from "react";
import IconSend from "../../icons/Icon";
import SideListMorePopup from "./SideListMorePopup";


interface ISideList {
    title: string;
    id?: string;
    onClick: () => void;
    active: boolean;
    popupOpen?: boolean;
    onTogglePopup?: () => void;
}

export default function SideList({
    title,
    id,
    onClick,
    active = true,
    popupOpen = false,
    onTogglePopup
}: ISideList) {
    const [hovered, setHovered] = useState(false);
    return (
        <>
            <div
                className={
                    "w-full flex items-center justify-between rounded-[999px] p-2 px-4 cursor-pointer relative " +
                    (active ? " bg-blue-50 text-blue-700" : "hover:bg-gray-100 text-gray-800")
                }
                onMouseEnter={() => setHovered(true)}
                onMouseLeave={() => setHovered(false)}
            >
                <div className="text-[14px]">{title}</div>
                <div className={`text-gray-400 ${hovered ? " opacity-100" : "opacity-0"}`} onClick={onTogglePopup}>
                    <IconSend icon="Gengduo1" />
                </div>
                {popupOpen ? <SideListMorePopup /> : ""}
            </div>

        </>
    )
}