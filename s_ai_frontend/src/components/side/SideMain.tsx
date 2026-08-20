import SideHeader from "./SideHeader";
import SideHiddenHider from "./SideHiddenHider";
import SideListMenu from "./SideListMenu";
import SideNewChat from "./SideNewChat";

interface ISideMain {
    isOpen: boolean;
    togglOpenSide: () => void;
}


export default function SideMain(iSideMain: ISideMain) {
    return (
        <>
            <div className={`h-screen flex flex-col gap-5 border-r border-gray-200 px-4 transition-all duration-300 ease-in-out overflow-hidden
                ${iSideMain.isOpen
                    ? " w-62.5 min-w-62.5 opacity-100 translate-x-0"
                    : " w-0 min-w-0 opacity-0 -translate-x-52 pointer-events-none"}`}>
                <SideHeader togglOpenSide={iSideMain.togglOpenSide} />
                <SideNewChat />
                <SideListMenu />
            </div>
            <div className={`fixed top-0 left-0 transition-all duration-300 ${iSideMain.isOpen ? " opacity-0 pointer-events-none" : " opacity-100"}`}>
                <SideHiddenHider togglOpenSide={iSideMain.togglOpenSide} />
            </div>

        </>
    )
}