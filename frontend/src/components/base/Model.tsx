import type { IconDefinition } from "@fortawesome/fontawesome-svg-core";
import { faXmark } from "@fortawesome/free-solid-svg-icons";
import { useEffect, useId, useRef, type ReactNode } from "react";
import { createPortal } from "react-dom";
import { cn } from "../../lib/cn";
import { AppIcon } from "../AppIcon";
import { Button } from "../Button";
import { IconButton } from "../IconButton";

export type ModalSize = "sm" | "md" | "lg" | "xl";
export type ModalTone = "primary" | "danger" | "warning" | "success";

export interface ModalProps {
  /** 是否显示 */
  open: boolean;
  /** 标题，为空时显示无标题面板 */
  title?: ReactNode;
  /** 标题下方的说明文字 */
  description?: ReactNode;
  /** 正文内容 */
  children?: ReactNode;
  /** 标题左侧图标 */
  icon?: IconDefinition;
  /** 图标底色语义，默认 primary */
  tone?: ModalTone;
  /** 面板宽度 */
  size?: ModalSize;
  /** 自定义底部；传 null 隐藏底部 */
  footer?: ReactNode | null;
  okText?: ReactNode;
  /** 传 null 隐藏取消按钮 */
  cancelText?: ReactNode | null;
  onOk?: () => void;
  onCancel?: () => void;
  /** 确定按钮 loading */
  confirmLoading?: boolean;
  okDisabled?: boolean;
  okVariant?: "primary" | "danger";
  /** 是否显示右上角关闭按钮 */
  closable?: boolean;
  /** 点击遮罩是否关闭 */
  maskClosable?: boolean;
  /** Esc 是否关闭 */
  keyboard?: boolean;
  /** 面板附加类名 */
  className?: string;
  /** 正文附加类名 */
  bodyClassName?: string;
}

const sizeClass: Record<ModalSize, string> = {
  sm: "max-w-sm",
  md: "max-w-md",
  lg: "max-w-xl",
  xl: "max-w-3xl",
};

const toneClass: Record<ModalTone, string> = {
  primary: "bg-primary/10 text-primary",
  danger: "bg-danger-soft text-danger",
  warning: "bg-warning/12 text-warning",
  success: "bg-success/12 text-success",
};

const FOCUSABLE =
  'button:not([disabled]), [href], input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])';

let lockCount = 0;
let previousOverflow = "";
let previousPaddingRight = "";

export function Modal({
  open,
  title,
  description,
  children,
  icon,
  tone = "primary",
  size = "md",
  footer,
  okText = "确定",
  cancelText = "取消",
  onOk,
  onCancel,
  confirmLoading = false,
  okDisabled = false,
  okVariant = "primary",
  closable = true,
  maskClosable = true,
  keyboard = true,
  className,
  bodyClassName,
}: ModalProps) {
  const panelRef = useRef<HTMLDivElement>(null);
  const cancelRef = useRef(onCancel);
  const titleId = useId();
  const descriptionId = useId();

  useEffect(() => {
    cancelRef.current = onCancel;
  });

  // 打开时锁定页面滚动，关闭后恢复
  useEffect(() => {
    if (!open) return;
    if (lockCount === 0) {
      previousOverflow = document.body.style.overflow;
      previousPaddingRight = document.body.style.paddingRight;
      const gap = window.innerWidth - document.documentElement.clientWidth;
      document.body.style.overflow = "hidden";
      if (gap > 0) document.body.style.paddingRight = `${gap}px`;
    }
    lockCount += 1;
    return () => {
      lockCount -= 1;
      if (lockCount === 0) {
        document.body.style.overflow = previousOverflow;
        document.body.style.paddingRight = previousPaddingRight;
      }
    };
  }, [open]);

  // 焦点管理：入场聚焦首个可聚焦元素，Tab 循环，关闭后归还焦点
  useEffect(() => {
    if (!open) return;
    const panel = panelRef.current;
    if (!panel) return;
    const previousFocus =
      document.activeElement instanceof HTMLElement ? document.activeElement : null;
    const focusables = () =>
      Array.from(panel.querySelectorAll<HTMLElement>(FOCUSABLE));
    (focusables()[0] ?? panel).focus({ preventScroll: true });

    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        if (!keyboard) return;
        event.stopPropagation();
        cancelRef.current?.();
        return;
      }
      if (event.key !== "Tab") return;
      const items = focusables();
      if (items.length === 0) {
        event.preventDefault();
        panel.focus({ preventScroll: true });
        return;
      }
      const first = items[0];
      const last = items[items.length - 1];
      const active = document.activeElement;
      if (event.shiftKey && (active === first || active === panel)) {
        event.preventDefault();
        last.focus();
      } else if (!event.shiftKey && active === last) {
        event.preventDefault();
        first.focus();
      }
    };

    document.addEventListener("keydown", onKeyDown, true);
    return () => {
      document.removeEventListener("keydown", onKeyDown, true);
      previousFocus?.focus({ preventScroll: true });
    };
  }, [open, keyboard]);

  if (!open) return null;

  const hasHeader = Boolean(title || description || icon || closable);
  const defaultFooter = (
    <>
      {cancelText !== null && onCancel && (
        <Button variant="secondary" onClick={onCancel}>
          {cancelText}
        </Button>
      )}
      {onOk && (
        <Button
          variant={okVariant}
          onClick={onOk}
          loading={confirmLoading}
          disabled={okDisabled}
        >
          {okText}
        </Button>
      )}
    </>
  );

  return createPortal(
    <div className="dialog-mask fixed inset-0 z-50 overflow-y-auto bg-overlay/55">
      <div
        className="flex min-h-full items-center justify-center p-4"
        onMouseDown={(event) => {
          if (!maskClosable) return;
          if (event.target !== event.currentTarget) return;
          cancelRef.current?.();
        }}
      >
        <div
          ref={panelRef}
          role="dialog"
          aria-modal="true"
          aria-labelledby={title ? titleId : undefined}
          aria-describedby={description ? descriptionId : undefined}
          aria-label={title ? undefined : "对话框"}
          tabIndex={-1}
          className={cn(
            "dialog-panel relative flex max-h-[calc(100dvh-2rem)] w-full flex-col overflow-hidden rounded-2xl border border-line outline-none",
            sizeClass[size],
            className
          )}
        >
          {hasHeader && (
            <div
              className={cn(
                "flex shrink-0 items-start gap-3.5 px-6 pt-5",
                children || footer !== null ? "pb-4" : "pb-5"
              )}
            >
              {icon && (
                <span
                  className={cn(
                    "grid size-10 shrink-0 place-items-center rounded-xl",
                    toneClass[tone]
                  )}
                >
                  <AppIcon icon={icon} size={20} />
                </span>
              )}
              <div className="min-w-0 flex-1">
                {title && (
                  <h2
                    id={titleId}
                    className="text-lg font-bold tracking-[-0.01em] text-ink"
                  >
                    {title}
                  </h2>
                )}
                {description && (
                  <p
                    id={descriptionId}
                    className={cn(
                      "text-sm leading-6 text-muted",
                      title && "mt-1.5"
                    )}
                  >
                    {description}
                  </p>
                )}
              </div>
              {closable && (
                <IconButton
                  label="关闭对话框"
                  onClick={() => cancelRef.current?.()}
                  className="-mt-1 -mr-2 shrink-0"
                >
                  <AppIcon icon={faXmark} size={18} />
                </IconButton>
              )}
            </div>
          )}

          {children && (
            <div
              className={cn(
                "scrollbar-thin min-h-0 flex-1 overflow-y-auto px-6 pt-1 pb-5",
                bodyClassName
              )}
            >
              {children}
            </div>
          )}

          {footer !== null && (
            <div className="flex shrink-0 flex-col-reverse gap-3 border-t border-line bg-surface-muted/40 px-6 py-4 sm:flex-row sm:justify-end">
              {footer ?? defaultFooter}
            </div>
          )}
        </div>
      </div>
    </div>,
    document.body
  );
}

export default Modal;
