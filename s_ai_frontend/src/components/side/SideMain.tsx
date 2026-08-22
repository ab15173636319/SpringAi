import { cn } from "../../utils/cn";
import IconSend from "../../icons/Icon";
import SideHeader from "./SideHeader";
import SideHiddenHider from "./SideHiddenHider";
import SideListMenu from "./SideListMenu";
import SideNewChat from "./SideNewChat";

interface ISideMain {
    isOpen: boolean;
    isMobile: boolean;
    togglOpenSide: () => void;
}
export default function SideMain({ isOpen, isMobile, togglOpenSide }: ISideMain) {
    // 小屏：侧边栏使用定位，抽屉覆盖在内容上方
    if (isMobile) {
        return (
            <>
                {/* 遮罩层 */}
                <div
                    className={cn(
                        "fixed inset-0 z-40 bg-black/40 transition-opacity duration-300",
                        isOpen ? "opacity-100" : "pointer-events-none opacity-0"
                    )}
                    onClick={togglOpenSide}
                />
                {/* 抽屉 */}
                <div
                    className={cn(
                        "fixed inset-y-0 left-0 z-50 flex h-full w-72 max-w-[85vw] flex-col gap-5 border-r border-gray-200 bg-white px-4 transition-transform duration-300 ease-in-out",
                        isOpen ? "translate-x-0" : "-translate-x-full"
                    )}
                >
                    <SideHeader togglOpenSide={togglOpenSide} />
                    <SideNewChat />
                    <SideListMenu />
                </div>
                {/* 打开按钮 */}
                <button
                    aria-label="打开侧边栏"
                    onClick={togglOpenSide}
                    className={cn(
                        "fixed top-4 left-4 z-30 flex size-10 items-center justify-center rounded-full border border-gray-200 bg-white shadow-md transition-opacity duration-300",
                        isOpen ? "pointer-events-none opacity-0" : "opacity-100"
                    )}
                >
                    <IconSend icon="Afenleizhediecebianlan" />
                </button>
            </>
        )
    }

    // 大/中屏：侧边栏占位布局，收起时显示折叠条
    return (
        <>
            <div className={cn(
                "h-screen flex flex-col gap-5 border-r border-gray-200 px-4 transition-all",
                " duration-300 ease-in-out",
                isOpen ?
                    "w-62.5 opacity-100 translate-x-0"
                    :
                    "w-0 min-w-0 opacity-0 -translate-x-52 pointer-events-none"
            )}>
                <SideHeader togglOpenSide={togglOpenSide} />
                <SideNewChat />
                <SideListMenu />
            </div>
            <div className={cn(
                "fixed top-0 left-0 transition-all duration-300",
                isOpen ? " opacity-0 pointer-events-none" : " opacity-100"
            )}>
                <SideHiddenHider togglOpenSide={togglOpenSide} />
            </div>

        </>
    )
}
