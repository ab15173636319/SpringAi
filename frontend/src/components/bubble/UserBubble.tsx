import { WarningCircle } from "@phosphor-icons/react";
import type { ChatMessage } from "../../types";
import { BubbleControl } from "./BubbleControl";

export function UserBubble({ message }: { message: ChatMessage }) {
  return (
    <article className="group flex justify-end py-3" data-testid="user-message">
      <div className="flex min-w-0 max-w-full items-start gap-3 sm:gap-4">
        <div className="min-w-0 flex-1" data-testid="user-content">
          {message.error && (
            <div className="mb-3 flex items-center justify-end gap-1 text-[11px] text-danger">
              <WarningCircle size={13} weight="fill" aria-hidden="true" />
              未能完成
            </div>
          )}

          <div className="rounded-xl rounded-br-sm bg-user-bubble px-4 py-3 text-[15px] leading-7 text-ink">
            <p className="whitespace-pre-wrap break-words [overflow-wrap:anywhere]">
              {message.content}
            </p>
          </div>

          <BubbleControl message={message} align="end" className="mt-1" />
        </div>
      </div>
    </article>
  );
}
