import IconSend from "../../icons/Icon";

export default function SideListMorePopup() {
    return (
        <>
            <div className=" absolute top-11/12 left-10/12 p-4 bg-white shadow-[0_0_10px_rgba(0,0,0,0.2)] w-30 rounded-md select-none z-10 flex flex-col gap-2">
                <div className=" text-gray-500 hover:bg-gray-200 px-2 py-1 rounded-md">
                    <IconSend icon="Xiugai">修改</IconSend>
                </div>
                <div className=" text-gray-500 hover:bg-gray-200 px-2 py-1 rounded-md">
                    <IconSend icon="Zhiding">置顶</IconSend>
                </div>
                <div className=" text-red-500 hover:bg-red-200 px-2 py-1 rounded-md">
                    <IconSend icon="Shanchu">删除</IconSend>
                </div>
            </div>
        </>
    )
}