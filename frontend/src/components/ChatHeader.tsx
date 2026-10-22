import {
  List,
  Moon,
  Sparkle,
  Sun,
} from "@phosphor-icons/react";
import { IconButton } from "./IconButton";
import { useChatStore } from "../stores/chat";
import { useThemeStore } from "../stores/theme";

export function ChatMenuButton({ onOpen }: { onOpen: () => void }) {
  return (
    <IconButton label="打开会话侧栏" onClick={onOpen} className="lg:hidden">
      <List size={21} aria-hidden="true" />
    </IconButton>
  );
}

export function ChatHeader({ onOpenMenu }: { onOpenMenu: () => void }) {
  const conversations = useChatStore((state) => state.conversations);
  const activeId = useChatStore((state) => state.activeId);
  const system = useChatStore((state) => state.system);
  const theme = useThemeStore((state) => state.theme);
  const toggleTheme = useThemeStore((state) => state.toggle);
  const active = conversations.find((item) => item.id === activeId);

  return (
    <header className="flex h-14 shrink-0 items-center justify-between gap-4 border-b border-line bg-canvas px-4 sm:px-6 lg:px-8">
      <div className="flex min-w-0 items-center gap-2">
        <ChatMenuButton onOpen={onOpenMenu} />
        <div className="min-w-0">
          <h1 className="truncate text-sm font-medium text-ink">
            {active?.title ?? "新对话"}
          </h1>
          <p className="mt-0.5 flex items-center gap-1.5 truncate text-[11px] text-muted">
            <span
              className={`size-1.5 shrink-0 rounded-full ${
                system?.mode === "live" ? "bg-success" : "bg-warning"
              }`}
              aria-hidden="true"
            />
            <span className="truncate">
              {system?.mode === "live" ? "DeepSeek 已连接" : "本地演示模式"}
              <span aria-hidden="true"> · </span>
              {system?.model ?? "deepseek-chat"}
            </span>
          </p>
        </div>
      </div>
      <div className="flex items-center gap-1.5">
        <span className="hidden items-center gap-1.5 text-[11px] font-medium text-muted sm:flex">
          <Sparkle size={13} className="text-primary" weight="fill" aria-hidden="true" />
          上下文已保存
        </span>
        <IconButton
          label={theme === "light" ? "切换到深色主题" : "切换到浅色主题"}
          onClick={toggleTheme}
          className="hidden lg:grid"
        >
          {theme === "light" ? (
            <Moon size={18} aria-hidden="true" />
          ) : (
            <Sun size={18} aria-hidden="true" />
          )}
        </IconButton>
      </div>
    </header>
  );
}
