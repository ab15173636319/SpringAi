import { ArrowUpRight } from "@phosphor-icons/react";
import { useLayoutEffect, useRef, useState } from "react";
import { AssistantBubble } from "../components/bubble/AssistantBubble";
import { BrandMark } from "../components/BrandMark";
import { UserBubble } from "../components/bubble/UserBubble";
import { useChatStore } from "../stores/chat";
import type { ChatMessage } from "../types";

function MessageItem({ message }: { message: ChatMessage }) {
  return message.role === "USER" ? (
    <UserBubble message={message} />
  ) : (
    <AssistantBubble message={message} />
  );
}

export function MessageSkeleton() {
  return (
    <div className="space-y-10 py-4" aria-label="正在加载消息" role="status">
      {[1, 2].map((item) => (
        <Skeleton key={item} align={item === 1 ? "right" : "left"} />
      ))}
    </div>
  );
}

function Skeleton({ align }: { align: "left" | "right" }) {
  if (align === "right") {
    return (
      <div className="ml-auto w-[min(62%,460px)] space-y-2">
        <div className="ml-auto h-3 w-16 animate-pulse rounded bg-surface-muted" />
        <div className="h-20 animate-pulse rounded-xl bg-surface-muted" />
      </div>
    );
  }
  return (
    <div className="flex gap-4">
      <div className="size-7 shrink-0 animate-pulse rounded-full bg-surface-muted" />
      <div className="w-[min(78%,580px)] space-y-3">
        <div className="h-3 w-28 animate-pulse rounded bg-surface-muted" />
        <div className="h-3 w-full animate-pulse rounded bg-surface-muted" />
        <div className="h-3 w-[88%] animate-pulse rounded bg-surface-muted" />
        <div className="h-3 w-[64%] animate-pulse rounded bg-surface-muted" />
      </div>
    </div>
  );
}

export function MessageList({
  onSuggestion,
}: {
  onSuggestion: (prompt: string) => void;
}) {
  const messages = useChatStore((state) => state.messages);
  const loading = useChatStore((state) => state.loadingMessages);
  const streaming = useChatStore((state) => state.streaming);
  const [messageArray, setMessageArray] = useState<string[]>([])
  const scrollRef = useRef<HTMLDivElement>(null);
  const bottomRef = useRef<HTMLDivElement>(null);
  const followRef = useRef(true);

  useLayoutEffect(() => {
    if (followRef.current) {
      bottomRef.current?.scrollIntoView({
        block: "end",
        behavior: streaming ? "auto" : "smooth",
      });
    }
  }, [messages, loading, streaming]);

  return (
    <div className="flex min-h-0 flex-1 flex-col">
      <div
        ref={scrollRef}
        className="scrollbar-thin min-h-0 flex-1 overflow-y-auto overscroll-contain"
        onScroll={(event) => {
          const element = event.currentTarget;
          followRef.current =
            element.scrollHeight - element.scrollTop - element.clientHeight < 120;
        }}
      >
        <div className="mx-auto w-full max-w-3xl px-4 pb-10 pt-7 sm:px-7 lg:px-8">
          {loading ? (
            <MessageSkeleton />
          ) : messages.length === 0 ? (
            <EmptyState onSuggestion={onSuggestion} />
          ) : (
            <div>
              {messages.map((message) => (
                <div key={message.id}>
                  <div className=" size-4 relative">
                    <input className=" absolute size-4  cursor-pointer invisible " type="checkbox" name="" id="" />
                    <div className=" size-4 bg-amber-200"></div>
                  </div>
                  <MessageItem message={message} />
                </div>
              ))}
            </div>
          )}
          <div ref={bottomRef} className="h-1" />
        </div>
      </div>
    </div>
  );
}

const suggestions = [
  {
    title: "梳理一个复杂问题",
    prompt: "我有一个复杂的产品问题，请先帮我拆解关键变量，再给出可执行的分析框架。",
  },
  {
    title: "改进一段代码",
    prompt: "请帮我审查一段代码，重点找出性能、可读性和潜在 Bug。",
  },
  {
    title: "整理工作思路",
    prompt: "帮我把本周要做的事情整理成按优先级排序的行动计划。",
  },
];

function EmptyState({ onSuggestion }: { onSuggestion: (prompt: string) => void }) {
  return (
    <section className="flex min-h-[58vh] flex-col justify-center py-10">
      <BrandMark size="lg" />
      <p className="mt-7 text-[11px] font-semibold uppercase tracking-[0.18em] text-primary">
        A new page
      </p>
      <h1 className="mt-2 max-w-2xl font-display text-5xl leading-[1.02] tracking-[-0.025em] text-ink sm:text-6xl">
        今天想一起解决什么？
      </h1>
      <p className="mt-5 max-w-xl text-[15px] leading-7 text-muted">
        描述目标、贴上一段代码，或从一个还不清晰的问题开始。答案会在这里展开。
      </p>
      <div className="mt-10 border-t border-line">
        {suggestions.map((suggestion, index) => (
          <button
            key={suggestion.title}
            type="button"
            onClick={() => onSuggestion(suggestion.prompt)}
            className="focus-ring group flex min-h-16 w-full cursor-pointer items-center gap-4 border-b border-line px-1 py-4 text-left transition-colors duration-150 hover:bg-panel/60"
          >
            <span className="w-6 shrink-0 text-[11px] tabular-nums text-muted">
              0{index + 1}
            </span>
            <span className="min-w-0 flex-1">
              <span className="block text-sm font-semibold text-ink">
                {suggestion.title}
              </span>
              <span className="mt-1 block truncate text-xs text-muted">
                {suggestion.prompt}
              </span>
            </span>
            <ArrowUpRight
              size={17}
              className="shrink-0 text-muted transition-[color,transform] duration-150 ease-[cubic-bezier(0.23,1,0.32,1)] group-hover:-translate-y-0.5 group-hover:translate-x-0.5 group-hover:text-primary"
              aria-hidden="true"
            />
          </button>
        ))}
      </div>
    </section>
  );
}
