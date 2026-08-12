import IconSend from "../../icons/Icon"
import Button from "../base/Button"

export default function SideNewChat() {
    return (
        <>
            <Button className=" w-full flex justify-center items-center gap-5 bg-white  rounded-[9999px]! hover:bg-white  shadow-[0_0_10px_rgba(0,0,0,0.2)] hover:shadow-[0_0_15px_rgba(0,0,0,0.3)]">
                <span>+</span>
                <span>新建对话</span>
            </Button>
        </>
    )
}