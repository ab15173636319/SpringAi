import type { ReactNode } from "react";

type ButtonProps = {
    className?: string;
    children?: ReactNode;
    onClick?: () => void;
    disabled?: boolean;
    onKeyDown?: (event: React.KeyboardEvent<HTMLButtonElement>) => void;
};

function Button({ className = "", children, onClick, disabled, onKeyDown }: ButtonProps) {
    return (
        <button disabled={disabled}
            className={" px-3 p-2 bg-gray-200 text-md rounded-md cursor-pointer hover:bg-gray-300 transition-bg duration-200 disabled:cursor-not-allowed" + className}
            onClick={onClick}
            onKeyDown={onKeyDown}
        >
            {children}
        </button>
    );
}

export default Button;
