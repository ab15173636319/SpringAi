import { WarningCircle, X } from "@phosphor-icons/react";
import { useEffect, useRef } from "react";
import { Button } from "./Button";

interface ConfirmDialogProps {
  open: boolean;
  title: string;
  description: string;
  confirmLabel: string;
  onConfirm: () => void;
  onClose: () => void;
  loading?: boolean;
}

export function ConfirmDialog({
  open,
  title,
  description,
  confirmLabel,
  onConfirm,
  onClose,
  loading,
}: ConfirmDialogProps) {
  const closeButton = useRef<HTMLButtonElement>(null);
  const dialogRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!open) return;
    const previousFocus =
      document.activeElement instanceof HTMLElement ? document.activeElement : null;
    closeButton.current?.focus();
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") onClose();
      if (event.key !== "Tab" || !dialogRef.current) return;
      const focusable = Array.from(
        dialogRef.current.querySelectorAll<HTMLElement>(
          'button:not([disabled]), [href], input:not([disabled]), [tabindex]:not([tabindex="-1"])'
        )
      );
      if (focusable.length === 0) return;
      const first = focusable[0];
      const last = focusable[focusable.length - 1];
      if (event.shiftKey && document.activeElement === first) {
        event.preventDefault();
        last.focus();
      } else if (!event.shiftKey && document.activeElement === last) {
        event.preventDefault();
        first.focus();
      }
    };
    window.addEventListener("keydown", onKeyDown);
    return () => {
      window.removeEventListener("keydown", onKeyDown);
      previousFocus?.focus();
    };
  }, [open, onClose]);

  if (!open) return null;

  return (
    <div
      className="fixed inset-0 z-50 grid place-items-center bg-overlay/55 p-4"
      role="presentation"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget) onClose();
      }}
    >
      <div
        ref={dialogRef}
        role="dialog"
        aria-modal="true"
        aria-labelledby="confirm-title"
        aria-describedby="confirm-description"
        className="w-full max-w-md rounded-xl border border-line bg-panel p-6 shadow-2xl"
      >
        <div className="flex items-start justify-between gap-4">
          <span className="grid size-10 place-items-center rounded-full bg-danger-soft text-danger">
            <WarningCircle size={23} weight="duotone" aria-hidden="true" />
          </span>
          <button
            ref={closeButton}
            type="button"
            onClick={onClose}
            className="focus-ring grid size-10 cursor-pointer place-items-center rounded-xl text-muted hover:bg-surface-muted hover:text-ink"
            aria-label="关闭确认窗口"
          >
            <X size={19} aria-hidden="true" />
          </button>
        </div>
        <h2 id="confirm-title" className="mt-5 text-xl font-bold tracking-[-0.02em]">
          {title}
        </h2>
        <p id="confirm-description" className="mt-2 text-sm leading-6 text-muted">
          {description}
        </p>
        <div className="mt-7 flex justify-end gap-3">
          <Button variant="secondary" onClick={onClose}>
            取消
          </Button>
          <Button variant="danger" onClick={onConfirm} loading={loading}>
            {confirmLabel}
          </Button>
        </div>
      </div>
    </div>
  );
}
