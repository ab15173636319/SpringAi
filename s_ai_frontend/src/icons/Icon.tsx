import type { FC } from "react";
import { IconNameValue, type IconNameKey } from "../types/IconName";

const IconSend: FC<{ className?: string; icon?: IconNameKey }> = ({
    className,
    icon = "Fasong1",
}) => {
    const iconName = IconNameValue[icon];
    return (
        <i
            className={"icon iconfont " + className}
            dangerouslySetInnerHTML={{ __html: iconName }}
        />
    );
};

export default IconSend;
