import { useState } from "react";
import SideList from "./SideList"

export default function SideListMenu() {

    const msg: number[] = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    const [openId, setOpenId] = useState<number | null>(null)

    const togglePopup = (id: number) => {
        setOpenId((prev) => prev === id ? null : id)
    }

    return (
        <>
            <div className=" flex flex-col gap-2 flex-1 scrollbar-hide">
                {
                    msg.map((item, index) => {
                        return <SideList
                            key={index}
                            id={`${item}`}
                            title={`对话${item}`}
                            onClick={() => { }}
                            active={false}
                            popupOpen={openId === item}
                            onTogglePopup={() => togglePopup(item)}
                        />
                    })
                }
            </div>
        </>
    )
}