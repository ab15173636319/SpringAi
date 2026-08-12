import type { FC, ReactNode } from "react";
import { IconNameValue, type IconNameKey } from "../types/IconName";

const IconSend: FC<{ className?: string; icon?: IconNameKey, children?: ReactNode }> = ({
    className,
    icon = "Fasong1",
    children
}) => {
    const iconName = IconNameValue[icon];
    return (
        <div className=" w-fit flex items-center gap-2">
            <i
                className={"icon iconfont " + className}
                dangerouslySetInnerHTML={{ __html: iconName }}
            />
            {children}
        </div>
    );
};

export default IconSend;
