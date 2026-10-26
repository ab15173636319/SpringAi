import { useState } from "react";
import { Button } from "./Button";
import { useChatStore } from "../stores/chat";
import Modal from "./base/Modal";

interface DeleteBarProps {
  onConfirm?: () => void;
  onCancel?: () => void;
}

export function DeleteBar({ onConfirm, onCancel }: DeleteBarProps) {
  const [isOpen, setOpen] = useState(false)
  const messageArray = useChatStore((state) => state.messageArray);
  const enableDelete = useChatStore((state) => state.enableDelete);
  const deleteMessages = useChatStore((state) => state.deleteMessages);
  const [deleting, setDeleting] = useState(false);

  const empty = messageArray.length === 0;

  const handleConfirm = async () => {
    if (deleting || empty) return;
    setDeleting(true);
    try {
      await deleteMessages(messageArray);
      onConfirm?.();
    } finally {
      setDeleting(false);
    }
  };

  const handleCancel = () => {
    if (deleting) return;
    onCancel?.();
    enableDelete();
  };

  return (
    <div className="w-full flex justify-center">
      <Modal
        title="删除选中的消息？"
        description="选中的消息将被永久删除，会话本身会保留。"
        open={isOpen}
        onOk={handleConfirm}
        onCancel={() => setOpen((pre) => !pre)}
      />
      <div className="w-full max-w-3xl px-4 pb-10 pt-7 flex justify-end gap-5">
        <Button
          type="button"
          className=" bg-red-600 text-white"
          disabled={empty || deleting}
          loading={deleting}
          onClick={() => setOpen((pre) => !pre)}
        >
          确认
        </Button>
        <Button
          type="button"
          className=" bg-gray-300 text-black"
          disabled={deleting}
          onClick={handleCancel}
        >
          取消
        </Button>
      </div>
    </div>
  );
}
