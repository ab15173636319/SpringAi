

export default function SideHeader() {
    return (
        <>
            <div className=" h-16  flex items-center">
                <div className=" flex items-center gap-2">
                    <img className=" w-8" src="/logo.png" alt="刘某的聊天机器人" />
                    <span className=" text-gray-500 font-serif text-xl">luizhen's chatbot</span>
                </div>
            </div>
        </>
    )
}