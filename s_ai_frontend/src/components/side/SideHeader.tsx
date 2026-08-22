import HoverTExtButton from "../base/HoverTextButton";


export default function SideHeader({ togglOpenSide }: { togglOpenSide: () => void }) {
    return (
        <>
            <div className=" h-16  flex items-center justify-between">
                <div className=" flex items-center gap-2">
                    <img className=" w-8" src="/logo.png" alt="刘某的聊天机器人" />
                    <span className=" text-gray-500 font-serif text-md whitespace-nowrap">luizhen's chatbot</span>
                </div>
                <HoverTExtButton onClick={togglOpenSide} icon="Acebianlanfenleizhedie" hoverText="收拢侧边栏" />
            </div>
        </>
    )
}