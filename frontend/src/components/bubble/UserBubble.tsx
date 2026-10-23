import { faCircleExclamation } from "@fortawesome/free-solid-svg-icons";
import type { ChatMessage } from "../../types";
import { AppIcon } from "../AppIcon";
import { BubbleControl } from "./BubbleControl";

export function UserBubble({ message }: { message: ChatMessage }) {

  return (
    <article className="w-full group flex justify-end py-3" data-testid="user-message">
      <div className="flex min-w-0 max-w-full items-start gap-3 sm:gap-4">
        <div className="min-w-0 flex-1" data-testid="user-content">
          {message.error && (
            <div className="mb-3 flex items-center justify-end gap-1 text-[11px] text-danger">
              <AppIcon icon={faCircleExclamation} size={13} />
              未能完成
            </div>
          )}

          <div className="user-message-bubble rounded-2xl rounded-br-md px-4 py-3 text-[15px] leading-7 text-ink">
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
