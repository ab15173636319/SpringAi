import {
  Broom,
  DotsThree,
  MagnifyingGlass,
  Moon,
  PencilSimple,
  Plus,
  PushPin,
  SignOut,
  Sun,
  Trash,
  X,
} from "@phosphor-icons/react";
import {
  useEffect,
  useMemo,
  useState,
  type FormEvent,
  type ReactNode,
} from "react";
import { useNavigate } from "react-router-dom";
import { BrandMark } from "./BrandMark";
import { Button } from "./Button";
import { ConfirmDialog } from "./ConfirmDialog";
import { IconButton } from "./IconButton";
import { cn } from "../lib/cn";
import { relativeTime } from "../lib/format";
import { useAuthStore } from "../stores/auth";
import { useChatStore } from "../stores/chat";
import { useThemeStore } from "../stores/theme";
import type { Conversation } from "../types";

interface SidebarProps {
  open: boolean;
  onClose: () => void;
}

interface PendingAction {
  type: "delete" | "clear";
  id: string;
  title: string;
}

export function Sidebar({ open, onClose }: SidebarProps) {
  const [query, setQuery] = useState("");
  const [menuId, setMenuId] = useState<string | null>(null);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [draftTitle, setDraftTitle] = useState("");
  const [pending, setPending] = useState<PendingAction | null>(null);
  const [confirming, setConfirming] = useState(false);
  const conversations = useChatStore((state) => state.conversations);
  const activeId = useChatStore((state) => state.activeId);
  const usage = useChatStore((state) => state.usage);
  const createConversation = useChatStore((state) => state.createConversation);
  const renameConversation = useChatStore((state) => state.renameConversation);
  const togglePin = useChatStore((state) => state.togglePin);
  const deleteConversation = useChatStore((state) => state.deleteConversation);
  const clearConversation = useChatStore((state) => state.clearConversation);
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);
  const theme = useThemeStore((state) => state.theme);
  const toggleTheme = useThemeStore((state) => state.toggle);
  const navigate = useNavigate();

  useEffect(() => {
    if (!open) return;
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") onClose();
    };
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [onClose, open]);

  const filtered = useMemo(() => {
    const normalized = query.trim().toLocaleLowerCase("zh-CN");
    if (!normalized) return conversations;
    return conversations.filter((conversation) =>
      conversation.title.toLocaleLowerCase("zh-CN").includes(normalized)
    );
  }, [conversations, query]);

  const openConversation = (conversation: Conversation) => {
    setMenuId(null);
    navigate(`/chat/${conversation.id}`);
    onClose();
  };

  const startNew = async () => {
    await createConversation();
    const id = useChatStore.getState().activeId;
    if (id) navigate(`/chat/${id}`);
    onClose();
  };

  const beginRename = (conversation: Conversation) => {
    setMenuId(null);
    setEditingId(conversation.id);
    setDraftTitle(conversation.title);
  };

  const finishRename = async (event: FormEvent) => {
    event.preventDefault();
    if (!editingId || !draftTitle.trim()) return;
    try {
      await renameConversation(editingId, draftTitle.trim());
      setEditingId(null);
    } catch {
      // Store surfaces the actionable error in the main workspace.
    }
  };

  const confirmAction = async () => {
    if (!pending) return;
    setConfirming(true);
    if (pending.type === "delete") {
      await deleteConversation(pending.id);
      const next = useChatStore.getState().activeId;
      if (next)
        navigate(`/chat/${next}`, { replace: true });
    } else {
      await clearConversation();
    }
    setConfirming(false);
    setPending(null);
    setMenuId(null);
  };

  return (
    <>
      {open && (
        <button
          type="button"
          className="fixed inset-0 z-30 cursor-pointer bg-overlay/45 backdrop-blur-[2px] lg:hidden"
          aria-label="关闭会话侧栏"
          onClick={onClose}
        />
      )}
      <aside
        className={cn(
          "fixed inset-y-0 left-0 z-40 flex w-[min(88vw,320px)] flex-col border-r border-line bg-sidebar px-3 pb-3 pt-4 shadow-2xl transition-transform duration-200 ease-[cubic-bezier(0.23,1,0.32,1)] lg:relative lg:z-0 lg:w-[304px] lg:translate-x-0 lg:shadow-none",
          open ? "translate-x-0" : "-translate-x-full"
        )}
        aria-label="会话导航"
      >
        <div className="flex h-11 items-center justify-between px-2">
          <div className="flex items-center gap-2.5">
            <BrandMark size="sm" />
            <div>
              <p className="text-sm font-bold tracking-[-0.02em]">Aether AI</p>
              <p className="text-[11px] text-muted">专注对话与思考</p>
            </div>
          </div>
          <IconButton label="关闭侧栏" onClick={onClose} className="lg:hidden">
            <X size={19} aria-hidden="true" />
          </IconButton>
        </div>

        <Button onClick={startNew} className="mt-4 w-full justify-between px-3.5">
          <span className="flex items-center gap-2">
            <Plus size={18} weight="bold" aria-hidden="true" />
            新建对话
          </span>
          <kbd className="rounded-md border border-white/20 px-1.5 py-0.5 text-[10px] font-medium text-on-primary/75">
            N
          </kbd>
        </Button>

        <label className="relative mt-4 block">
          <span className="sr-only">搜索会话</span>
          <MagnifyingGlass
            size={17}
            className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-muted"
            aria-hidden="true"
          />
          <input
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="搜索会话"
            className="focus-ring h-10 w-full rounded-xl border border-transparent bg-surface-muted/70 pl-9 pr-3 text-sm text-ink placeholder:text-muted hover:border-line"
          />
        </label>

        <div className="mt-5 flex items-center justify-between px-2">
          <p className="text-[11px] font-semibold uppercase tracking-[0.12em] text-muted">
            最近会话
          </p>
          <span className="text-[11px] tabular-nums text-muted">
            {conversations.length}
          </span>
        </div>

        <nav className="scrollbar-thin mt-2 min-h-0 flex-1 overflow-y-auto pr-1">
          {filtered.length === 0 ? (
            <div className="mx-2 mt-8 rounded-2xl border border-dashed border-line px-4 py-8 text-center">
              <p className="text-sm font-medium">没有匹配的会话</p>
              <p className="mt-1 text-xs leading-5 text-muted">
                换个关键词，或创建一个新对话。
              </p>
            </div>
          ) : (
            <ul className="space-y-1">
              {filtered.map((conversation) => {
                const isActive = conversation.id === activeId;
                const isEditing = conversation.id === editingId;
                return (
                  <li
                    key={conversation.id}
                    className={cn(
                      "group relative rounded-lg border border-transparent transition-colors",
                      isActive
                        ? "bg-panel"
                        : "hover:bg-surface-muted/70"
                    )}
                  >
                    {isEditing ? (
                      <form onSubmit={finishRename} className="flex items-center gap-1 p-1">
                        <input
                          autoFocus
                          value={draftTitle}
                          onChange={(event) => setDraftTitle(event.target.value)}
                          onKeyDown={(event) => {
                            if (event.key === "Escape") setEditingId(null);
                          }}
                          maxLength={80}
                          aria-label="会话标题"
                          className="focus-ring min-w-0 flex-1 rounded-lg border border-line bg-canvas px-2.5 py-2 text-sm font-medium"
                        />
                        <button
                          type="submit"
                          className="focus-ring min-h-9 cursor-pointer rounded-lg bg-primary px-2.5 text-xs font-semibold text-on-primary"
                        >
                          保存
                        </button>
                      </form>
                    ) : (
                      <>
                        <button
                          type="button"
                          onClick={() => openConversation(conversation)}
                          className="focus-ring flex min-h-[58px] w-full cursor-pointer items-center gap-2.5 rounded-xl px-3 py-2 pr-11 text-left"
                          aria-current={isActive ? "page" : undefined}
                        >
                          <span
                            className={cn(
                              "h-7 w-[3px] shrink-0 rounded-full",
                              isActive ? "bg-primary" : "bg-transparent"
                            )}
                          />
                          <span className="min-w-0 flex-1">
                            <span
                              className={cn(
                                "block truncate text-sm",
                                isActive ? "font-semibold text-ink" : "font-medium text-ink/85"
                              )}
                              title={conversation.title}
                            >
                              {conversation.title}
                            </span>
                            <span className="mt-0.5 flex items-center gap-1.5 text-[11px] text-muted">
                              {conversation.pinned && (
                                <PushPin
                                  size={11}
                                  weight="fill"
                                  className="text-primary"
                                  aria-label="已置顶"
                                />
                              )}
                              {relativeTime(conversation.updatedAt)}
                            </span>
                          </span>
                        </button>
                        <button
                          type="button"
                          onClick={() =>
                            setMenuId((current) =>
                              current === conversation.id ? null : conversation.id
                            )
                          }
                          aria-label={`管理会话：${conversation.title}`}
                          aria-expanded={menuId === conversation.id}
                          className="focus-ring absolute right-1.5 top-2.5 grid size-9 cursor-pointer place-items-center rounded-lg text-muted opacity-70 transition-opacity hover:bg-surface-muted hover:text-ink group-hover:opacity-100 focus-visible:opacity-100"
                        >
                          <DotsThree size={20} weight="bold" aria-hidden="true" />
                        </button>
                        {menuId === conversation.id && (
                          <div className="absolute right-2 top-12 z-20 w-40 origin-top-right rounded-lg border border-line bg-panel p-1.5 shadow-xl">
                            <MenuAction
                              icon={<PushPin size={16} aria-hidden="true" />}
                              label={conversation.pinned ? "取消置顶" : "置顶会话"}
                              onClick={() => {
                                setMenuId(null);
                                void togglePin(conversation.id);
                              }}
                            />
                            <MenuAction
                              icon={<PencilSimple size={16} aria-hidden="true" />}
                              label="重命名"
                              onClick={() => beginRename(conversation)}
                            />
                            {isActive && (
                              <MenuAction
                                icon={<Broom size={16} aria-hidden="true" />}
                                label="清空消息"
                                onClick={() => {
                                  setMenuId(null);
                                  setPending({
                                    type: "clear",
                                    id: conversation.id,
                                    title: conversation.title,
                                  });
                                }}
                              />
                            )}
                            <MenuAction
                              icon={<Trash size={16} aria-hidden="true" />}
                              label="删除会话"
                              danger
                              onClick={() => {
                                setMenuId(null);
                                setPending({
                                  type: "delete",
                                  id: conversation.id,
                                  title: conversation.title,
                                });
                              }}
                            />
                          </div>
                        )}
                      </>
                    )}
                  </li>
                );
              })}
            </ul>
          )}
        </nav>

        <div className="mt-3 rounded-xl border border-line bg-panel p-3.5">
          <div className="flex items-center justify-between text-xs">
            <span className="font-semibold">今日对话</span>
            <span className="tabular-nums text-muted">
              {usage?.used ?? 0} / {usage?.limit ?? "—"}
            </span>
          </div>
          <div className="mt-2.5 h-1.5 overflow-hidden rounded-full bg-surface-muted">
            <div
              className="h-full rounded-full bg-primary transition-[width] duration-500"
              style={{
                width: `${Math.min(100, ((usage?.used ?? 0) / Math.max(1, usage?.limit ?? 1)) * 100)}%`,
              }}
            />
          </div>
          <p className="mt-2 text-[11px] leading-4 text-muted">
            每天北京时间 00:00 重置
          </p>
        </div>

        <div className="mt-3 flex items-center gap-2 border-t border-line pt-3">
          <span
            className="grid size-9 shrink-0 place-items-center rounded-full text-xs font-bold text-white"
            style={{ backgroundColor: user?.avatarColor ?? "var(--primary)" }}
            aria-hidden="true"
          >
            {user?.displayName?.slice(0, 1).toUpperCase() ?? "A"}
          </span>
          <div className="min-w-0 flex-1">
            <p className="truncate text-sm font-semibold">{user?.displayName}</p>
            <p className="truncate text-[11px] text-muted">@{user?.username}</p>
          </div>
          <IconButton
            label={theme === "light" ? "切换到深色主题" : "切换到浅色主题"}
            onClick={toggleTheme}
          >
            {theme === "light" ? (
              <Moon size={17} aria-hidden="true" />
            ) : (
              <Sun size={17} aria-hidden="true" />
            )}
          </IconButton>
          <IconButton
            label="退出登录"
            onClick={() => {
              void logout();
            }}
          >
            <SignOut size={17} aria-hidden="true" />
          </IconButton>
        </div>
      </aside>

      <ConfirmDialog
        open={pending !== null}
        title={pending?.type === "delete" ? "删除这个会话？" : "清空全部消息？"}
        description={
          pending?.type === "delete"
            ? `“${pending.title}”及其中的全部消息会从 MongoDB 永久删除，此操作无法撤销。`
            : `“${pending?.title ?? ""}”中的消息会被永久删除，会话本身将保留。`
        }
        confirmLabel={pending?.type === "delete" ? "删除会话" : "清空消息"}
        loading={confirming}
        onClose={() => setPending(null)}
        onConfirm={() => {
          void confirmAction();
        }}
      />
    </>
  );
}

function MenuAction({
  icon,
  label,
  onClick,
  danger,
}: {
  icon: ReactNode;
  label: string;
  onClick: () => void;
  danger?: boolean;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={cn(
        "focus-ring flex min-h-9 w-full cursor-pointer items-center gap-2 rounded-lg px-2.5 text-left text-xs font-medium hover:bg-surface-muted",
        danger ? "text-danger" : "text-ink"
      )}
    >
      {icon}
      {label}
    </button>
  );
}
