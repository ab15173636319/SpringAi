import { WarningCircle, X } from "@phosphor-icons/react";
import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ChatHeader } from "../components/ChatHeader";
import { Composer } from "../components/Composer";
import { MessageList } from "../layout/Messages";
import { Sidebar } from "../components/Sidebar";
import { FullPageLoader } from "../components/Loading";
import { useChatStore } from "../stores/chat";

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
      className="flex h-dvh min-h-0 overflow-hidden bg-canvas text-ink outline-none"
    >
      <Sidebar open={sidebarOpen} onClose={() => setSidebarOpen(false)} />
      <section className="flex min-w-0 flex-1 flex-col">
        <ChatHeader onOpenMenu={() => setSidebarOpen(true)} />
        {error && (
          <div
            role="alert"
            className="flex items-start gap-2 border-b border-danger/20 bg-danger-soft px-4 py-2.5 text-sm text-danger sm:px-6"
          >
            <WarningCircle size={18} weight="fill" className="mt-0.5 shrink-0" aria-hidden="true" />
            <span className="min-w-0 flex-1">{error}</span>
            <button
              type="button"
              onClick={clearError}
              className="focus-ring grid size-7 cursor-pointer place-items-center rounded-lg hover:bg-danger/10"
              aria-label="关闭错误提示"
            >
              <X size={16} aria-hidden="true" />
            </button>
          </div>
        )}
        <MessageList
          onSuggestion={(prompt) => {
            void sendMessage(prompt);
          }}
        />
        <Composer
          onSend={sendMessage}
          streaming={streaming}
          onStop={stopStreaming}
          usageRemaining={
            usage ? Math.max(0, usage.limit - usage.used) : null
          }
          disabled={!activeId}
        />
      </section>
    </main>
  );
}
