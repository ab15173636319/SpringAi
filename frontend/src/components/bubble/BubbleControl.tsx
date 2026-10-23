import {
    faCheck,
    faClock,
    faCopy,
    faTrashCan,
} from "@fortawesome/free-solid-svg-icons";
import { useEffect, useRef, useState } from "react";
import type { ReactNode } from "react";
import { AppIcon } from "../AppIcon";
import { cn } from "../../lib/cn";
import { timeOfDay } from "../../lib/format";
import type { ChatMessage } from "../../types";
import { useChatStore } from "../../stores/chat";

interface BubbleControlProps {
    message: ChatMessage;
    align?: "start" | "end";
    className?: string;
}

export function BubbleControl({
    message,
    align = "start",
    className,
}: BubbleControlProps) {

    const enableDelete = useChatStore((state) => state.enableDelete);
    const [copied, setCopied] = useState(false);
    const timer = useRef<number | undefined>(undefined);

    useEffect(() => () => window.clearTimeout(timer.current), []);

    const copy = async () => {
        try {
            await navigator.clipboard.writeText(message.content);
            setCopied(true);
            window.clearTimeout(timer.current);
            timer.current = window.setTimeout(() => setCopied(false), 1600);
        } catch {
            // Clipboard access can be blocked; leave the control in its idle state.
        }
    };

    const busy = Boolean(message.pending);
    const canCopy = !busy && message.content.length > 0;

    return (
        <div
            className={cn(
                "bubble-controls mt-1 flex items-center gap-0.5 text-muted",
                align === "end" ? "justify-end" : "justify-start",
                className
            )}
        >
            <span
                className="mr-1 flex items-center text-muted"
                title={`发送于 ${timeOfDay(message.createdAt)}`}
                aria-label={`发送时间 ${timeOfDay(message.createdAt)}`}
            >
                <div className=" flex items-center gap-1">
                    <AppIcon icon={faClock} size={13} />
                    <span>{timeOfDay(message.createdAt)}</span>
                </div>
            </span>
            <ControlButton
                label={copied ? "已复制" : "复制"}
                onClick={() => void copy()}
                disabled={!canCopy}
            >
                {copied ? (
                    <AppIcon icon={faCheck} size={15} />
                ) : (
                    <AppIcon icon={faCopy} size={15} />
                )}
            </ControlButton>
            <ControlButton
                label="删除"
                onClick={enableDelete}
                disabled={busy}
                danger
            >
                <AppIcon icon={faTrashCan} size={15} />
            </ControlButton>
        </div>
    );
}

function ControlButton({
    label,
    onClick,
    disabled,
    danger,
    children,
}: {
    label: string;
    onClick: () => void;
    disabled?: boolean;
    danger?: boolean;
    children: ReactNode;
}) {
    return (
        <button
            type="button"
            onClick={onClick}
            disabled={disabled}
            aria-label={label}
            title={label}
            className={cn(
                "focus-ring grid size-7 cursor-pointer place-items-center rounded-md text-muted transition-[background-color,color,transform] duration-150 ease-[cubic-bezier(0.23,1,0.32,1)] hover:bg-surface-muted active:scale-[0.94] disabled:pointer-events-none disabled:opacity-40",
                danger ? "hover:text-danger" : "hover:text-ink"
            )}
        >
            {children}
        </button>
    );
}
