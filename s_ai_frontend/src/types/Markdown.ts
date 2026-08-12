import type { ReactNode } from "react";

export const HeaderLevelValue = {
    H1: 1,
    H2: 2,
    H3: 3,
    H4: 4,
    H5: 5,
    H6: 6
} as const;


export type HeaderLevel = typeof HeaderLevelValue[keyof typeof HeaderLevelValue];

interface IHeader {
    level: HeaderLevel;
    className?: string;
    children?: ReactNode;
    onClick?: () => void;
}


export type HeaderProps = IHeader