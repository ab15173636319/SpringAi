import IconSend from "../../icons/Icon";
import type { IconNameKey } from "../../types/IconName";
import Button from "./Button";

interface IHoverTExtButton {
    children?: React.ReactNode;
    hoverText: string;
    icon?: IconNameKey;
    onClick?: () => void;
    className?: string;
    hoverClassName?: string;
    disabled?: boolean;
    onMouseEnter?: () => void;
    onMouseLeave?: () => void;

}

export default function HoverTExtButton({ icon, hoverText, children, onClick, className, hoverClassName, disabled, onMouseEnter, onMouseLeave }: IHoverTExtButton) {
    if (children && icon) {
        throw new Error("children and icon can not be used together")
    }


    return <>
        {

            <Button disabled={disabled} onMouseEnter={onMouseEnter} onMouseLeave={onMouseLeave} className="group  relative bg-transparent! flex items-center" onClick={onClick}>
                <span className={"flex size-8 items-center justify-center rounded-full hover:bg-gray-100 " + className}>
                    <IconSend icon={icon} /> {children}
                </span>
                <span className={
                    ` w-[200%] justify-center absolute top-[120%] left-[50%] translate-x-[-50%] rounded-[900px] bg-gray-800 text-white px-2  hidden group-hover:block ${hoverClassName}`
                }>
                    {hoverText}
                </span>
            </Button>

        }

    </>
}