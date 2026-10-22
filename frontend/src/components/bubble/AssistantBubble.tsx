import { WarningCircle } from "@phosphor-icons/react";
import { lazy, Suspense } from "react";
import type { ChatMessage } from "../../types";
import { BubbleControl } from "./BubbleControl";

const MarkdownContent = lazy(() => import("../MarkdownContent"));

export function AssistantBubble({ message }: { message: ChatMessage }) {
  return (
    <article className="group py-7 sm:py-9" data-testid="assistant-message">
      <div className="flex items-start gap-3 sm:gap-4">
        {/* <BrandMark size="sm" className="mt-1" /> */}
        <div className="min-w-0 flex-1" data-testid="assistant-content">
          {message.error && (
            <div className="mb-3 flex items-center gap-1 text-[11px] text-danger">
              <WarningCircle size={13} weight="fill" aria-hidden="true" />
              未能完成
            </div>
          )}

          <div className={message.error ? "text-danger" : undefined}>
            {message.pending && !message.content ? (
              <TypingIndicator />
            ) : (
              <Suspense fallback={<MarkdownFallback content={message.content} />}>
                <MarkdownContent>{message.content}</MarkdownContent>
              </Suspense>
            )}
            {message.pending && message.content && (
              <span className="ml-1 inline-block h-4 w-px animate-pulse bg-primary align-middle" />
            )}
          </div>

          <BubbleControl message={message} className="mt-3" />
        </div>
      </div>
    </article>
  );
}

function MarkdownFallback({ content }: { content: string }) {
  return (
    <p className="whitespace-pre-wrap break-words text-[16px] leading-[1.8]">
      {content}
    </p>
  );
}

function TypingIndicator() {
  return (
    <div
      className="flex min-h-8 items-center gap-1.5"
      role="status"
      aria-label="Aether 正在思考"
    >
      {[0, 1, 2].map((index) => (
        <span
          key={index}
          className="h-1 w-4 animate-pulse rounded-full bg-primary/55"
          style={{ animationDelay: `${index * 120}ms` }}
        />
      ))}
    </div>
  );
}
