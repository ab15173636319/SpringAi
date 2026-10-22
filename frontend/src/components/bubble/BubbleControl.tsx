import { Check, Clock, Copy, Trash } from "@phosphor-icons/react";
import { useEffect, useRef, useState } from "react";
import type { ReactNode } from "react";
import { cn } from "../../lib/cn";
import { timeOfDay } from "../../lib/format";
import { useChatStore } from "../../stores/chat";
import type { ChatMessage } from "../../types";

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
    const deleteMessage = useChatStore((state) => state.deleteMessage);
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
                "mt-1 flex items-center gap-0.5 text-muted",
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
                    <Clock size={13} aria-hidden="true" />
                    <span>{timeOfDay(message.createdAt)}</span>
                </div>
            </span>
            <ControlButton
                label={copied ? "已复制" : "复制"}
                onClick={() => void copy()}
                disabled={!canCopy}
            >
                {copied ? (
                    <Check size={15} weight="bold" aria-hidden="true" />
                ) : (
                    <Copy size={15} aria-hidden="true" />
                )}
            </ControlButton>
            <ControlButton
                label="删除"
                onClick={() => deleteMessage(message.id)}
                disabled={busy}
                danger
            >
                <Trash size={15} aria-hidden="true" />
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
