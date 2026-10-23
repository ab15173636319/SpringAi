import { faCheck } from "@fortawesome/free-solid-svg-icons";
import { AppIcon } from "../AppIcon";
import { cn } from "../../lib/cn";
import { useChatStore } from "../../stores/chat";

export default function BubbleDelete({ id }: { id: string }) {
    const messageArray = useChatStore((state) => state.messageArray);
    const changeMessageArray = useChatStore((state) => state.changeMessageArray);

    const selected = messageArray.includes(id);

    return (
        <button
            type="button"
            role="checkbox"
            aria-checked={selected}
            aria-label={selected ? "取消选择这条消息" : "选择这条消息"}
            onClick={() => changeMessageArray(id)}
            className="focus-ring group/check mt-1 grid size-9 shrink-0 cursor-pointer place-items-center self-start rounded-lg transition-[background-color,transform] duration-150 ease-[cubic-bezier(0.23,1,0.32,1)] hover:bg-surface-muted active:scale-[0.92] motion-reduce:transition-none"
        >
            <span
                aria-hidden="true"
                className={cn(
                    "grid size-4.5 place-items-center rounded-md border transition-[background-color,border-color,box-shadow] duration-150 ease-[cubic-bezier(0.23,1,0.32,1)] motion-reduce:transition-none",
                    selected
                        ? "border-primary bg-primary text-on-primary shadow-sm"
                        : "border-line-strong bg-panel group-hover/check:border-primary/55"
                )}
            >
                <AppIcon
                    icon={faCheck}
                    size={11}
                    className={cn(
                        "transition-[opacity,transform] duration-150 ease-[cubic-bezier(0.23,1,0.32,1)] motion-reduce:transition-none",
                        selected ? "scale-100 opacity-100" : "scale-50 opacity-0"
                    )}
                />
            </span>
        </button>
    );
}
