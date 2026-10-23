import { faArrowUp, faStop } from "@fortawesome/free-solid-svg-icons";
import { useEffect, useRef, useState, type KeyboardEvent } from "react";
import { AppIcon } from "./AppIcon";
import { IconButton } from "./IconButton";

interface ComposerProps {
  onSend: (message: string) => Promise<void>;
  streaming: boolean;
  onStop: () => void;
  usageRemaining: number | null;
  disabled?: boolean;
}

export function Composer({
  onSend,
  streaming,
  onStop,
  usageRemaining,
  disabled,
}: ComposerProps) {
  const [value, setValue] = useState("");
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    const textarea = textareaRef.current;
    if (!textarea) return;
    textarea.style.height = "0px";
    textarea.style.height = `${Math.min(textarea.scrollHeight, 180)}px`;
  }, [value]);

  const submit = async () => {
    const content = value.trim();
    if (!content || streaming || disabled) return;
    setValue("");
    await onSend(content);
    textareaRef.current?.focus();
  };

  const onKeyDown = (event: KeyboardEvent<HTMLTextAreaElement>) => {
    if (
      event.key === "Enter" &&
      !event.shiftKey &&
      !event.nativeEvent.isComposing
    ) {
      event.preventDefault();
      void submit();
    }
  };

  return (
    <div className="composer-dock relative px-3 pb-[max(12px,env(safe-area-inset-bottom))] pt-4 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-4xl">
        <div className="composer-panel border p-2">
          <label htmlFor="message-composer" className="sr-only">
            输入消息
          </label>
          <textarea
            ref={textareaRef}
            id="message-composer"
            value={value}
            disabled={disabled}
            onChange={(event) => setValue(event.target.value)}
            onKeyDown={onKeyDown}
            rows={1}
            maxLength={12_000}
            placeholder={disabled ? "选择或创建一个会话后开始输入" : "向 Aether 发送消息"}
            className="scrollbar-thin block max-h-44 min-h-12 w-full resize-none bg-transparent px-2.5 py-2.5 text-[15px] leading-6 text-ink outline-none placeholder:text-muted/70 disabled:cursor-not-allowed"
          />
          <div className="flex items-center justify-between gap-3 px-1 pb-0.5 pt-1">
            <div className="flex min-w-0 items-center gap-2 text-[11px] text-muted">
              <span className="hidden sm:inline">Enter 发送 · Shift + Enter 换行</span>
              {usageRemaining !== null && (
                <span className="rounded-full bg-surface-muted px-2 py-1 tabular-nums">
                  今日剩余 {usageRemaining}
                </span>
              )}
            </div>
            {streaming ? (
              <IconButton
                label="停止生成"
                onClick={onStop}
                className="bg-ink text-canvas hover:bg-ink/85 hover:text-canvas dark:bg-primary dark:text-on-primary dark:hover:bg-primary-strong"
              >
                <AppIcon icon={faStop} size={17} />
              </IconButton>
            ) : (
              <IconButton
                label="发送消息"
                onClick={() => {
                  void submit();
                }}
                disabled={!value.trim() || disabled}
                className="bg-ink text-canvas hover:bg-primary hover:text-on-primary disabled:opacity-35 dark:bg-primary dark:text-on-primary dark:hover:bg-primary-strong"
              >
                <AppIcon icon={faArrowUp} size={18} />
              </IconButton>
            )}
          </div>
        </div>
        <p className="mt-2 text-center text-[10px] leading-4 text-muted">
          Aether 可能会出错，请核对重要信息。
        </p>
      </div>
    </div>
  );
}
