import type React from "react";
import IconSend from "../../icons/Icon";

interface Props {
    loading: boolean;
    children: React.ReactNode;
    className?: string;
}


const Spain = (props: Props) => {

    const { loading, children, className } = props;

    return (
        <>
            <div className={" w-auto relative " + className}>
                {loading ? <div className=" select-none absolute top-0 left-0 w-full h-full bg-black/20 flex items-center justify-center text-2xl text-gray00 z-10">
                    <IconSend className=" text-blue-500 font-bold animate-spin text-5xl!" icon="Loading" />
                </div> : ""}
                {children}
            </div>
        </>
    )
}


export default Spain;