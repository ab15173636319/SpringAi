import SideHeader from "./SideHeader";
import SideListMenu from "./SideListMenu";
import SideNewChat from "./SideNewChat";


export default function SideMain() {
    return (
        <>
            <div className=" h-screen w-[15vw] min-w-[250px] border-r border-gray-200 px-4 flex flex-col gap-5">
                <SideHeader />
                <SideNewChat />
                <SideListMenu />
            </div>
        </>
    )
}