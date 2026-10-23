import {
  faCircleExclamation,
  faXmark,
} from "@fortawesome/free-solid-svg-icons";
import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { AppIcon } from "../components/AppIcon";
import { ChatHeader } from "../components/ChatHeader";
import { Composer } from "../components/Composer";
import { MessageList } from "../layout/Messages";
import { Sidebar } from "../components/Sidebar";
import { FullPageLoader } from "../components/Loading";
import { useChatStore } from "../stores/chat";
import { DeleteBar } from "../components/DeleteBar";

export function ChatPage() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const initialized = useRef(false);
  const { conversationId } = useParams();
  const navigate = useNavigate();
  const initialize = useChatStore((state) => state.initialize);
  const selectConversation = useChatStore((state) => state.selectConversation);
  const sendMessage = useChatStore((state) => state.sendMessage);
  const stopStreaming = useChatStore((state) => state.stopStreaming);
  const clearError = useChatStore((state) => state.clearError);
  const initializing = useChatStore((state) => state.initializing);
  const conversations = useChatStore((state) => state.conversations);
  const activeId = useChatStore((state) => state.activeId);
  const streaming = useChatStore((state) => state.streaming);
  const usage = useChatStore((state) => state.usage);
  const error = useChatStore((state) => state.error);
  const isDelete = useChatStore((state) => state.isDeletion)


  useEffect(() => {
    if (initialized.current) return;
    initialized.current = true;
    void initialize();
  }, [initialize]);

  useEffect(() => {
    if (initializing) return;
    if (conversationId) {
      if (conversations.some((item) => item.id === conversationId)) {
        void selectConversation(conversationId);
      } else {
        navigate("/chat", { replace: true });
      }
    } else if (activeId) {
      navigate(`/chat/${activeId}`, { replace: true });
    }
  }, [
    activeId,
    conversationId,
    conversations,
    initializing,
    navigate,
    selectConversation,
  ]);

  if (initializing) {
    return <FullPageLoader label="正在打开对话工作区" />;
  }

  return (
    <main
      data-route-focus
      tabIndex={-1}
      className="workspace-shell flex h-dvh min-h-0 overflow-hidden text-ink outline-none"
    >
      <Sidebar open={sidebarOpen} onClose={() => setSidebarOpen(false)} />
      <section className="flex min-w-0 flex-1 flex-col">
        <ChatHeader onOpenMenu={() => setSidebarOpen(true)} />
        {error && (
          <div
            role="alert"
            className="flex items-start gap-2 border-b border-danger/20 bg-danger-soft px-4 py-2.5 text-sm text-danger sm:px-6"
          >
            <AppIcon
              icon={faCircleExclamation}
              size={18}
              className="mt-0.5 shrink-0"
            />
            <span className="min-w-0 flex-1">{error}</span>
            <button
              type="button"
              onClick={clearError}
              className="focus-ring grid size-7 cursor-pointer place-items-center rounded-lg hover:bg-danger/10"
              aria-label="关闭错误提示"
            >
              <AppIcon icon={faXmark} size={16} />
            </button>
          </div>
        )}
        <MessageList
          onSuggestion={(prompt) => {
            void sendMessage(prompt);
          }}
        />
        {
          isDelete
            ?
            <DeleteBar />
            :
            <Composer
              onSend={sendMessage}
              streaming={streaming}
              onStop={stopStreaming}
              usageRemaining={
                usage ? Math.max(0, usage.limit - usage.used) : null
              }
              disabled={!activeId}
            />
        }
      </section>
    </main>
  );
}
