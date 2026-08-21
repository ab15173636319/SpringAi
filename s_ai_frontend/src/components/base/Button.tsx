import type { ReactNode } from "react";
import { cn } from "../../utils/cn";

type ButtonType = "primary" | "danger" | "success" | "default"

interface ButtonProps {
    type: ButtonType;
    className?: string;
    children?: ReactNode;
    onClick?: () => void;
    disabled?: boolean;
    onKeyDown?: (event: React.KeyboardEvent<HTMLButtonElement>) => void;
    onMouseEnter?: (event: React.MouseEvent<HTMLButtonElement, MouseEvent>) => void;
    onMouseLeave?: (event: React.MouseEvent<HTMLButtonElement, MouseEvent>) => void;
};

function Button({ className = "", children, onClick, disabled, onKeyDown, type = "default" }: ButtonProps) {
    return (
        <button disabled={disabled}
            className={cn(
                "px-4 py-2 text-sm rounded-md font-medium cursor-pointer transition-colors duration-200 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-offset-1" + className,
                type === "primary" && "bg-blue-600 text-white hover:bg-blue-700 active:bg-blue-800 focus:ring-blue-500",
                type === "default" && "bg-gray-50 text-gray-700 hover:bg-gray-100 active:bg-gray-300 focus:ring-gray-400",
                type === "danger" && "bg-red-600 text-white hover:bg-red-700 active:bg-red-800 focus:ring-red-500",
                type === "success" && "bg-green-600 text-white hover:bg-green-700 active:bg-green-800 focus:ring-green-500",
            )}
            onClick={onClick}
            onKeyDown={onKeyDown}
        >
            {children}
        </button>
    );
}

export default Button;
