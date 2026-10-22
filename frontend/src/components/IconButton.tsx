import type { ButtonHTMLAttributes } from "react";
import { cn } from "../lib/cn";

export function IconButton({
  className,
  label,
  children,
  ...props
}: ButtonHTMLAttributes<HTMLButtonElement> & { label: string }) {
  return (
    <button
      type="button"
      className={cn(
        "focus-ring grid size-10 cursor-pointer place-items-center rounded-lg text-muted transition-[background-color,color,transform] duration-150 ease-[cubic-bezier(0.23,1,0.32,1)] hover:bg-surface-muted hover:text-ink active:scale-[0.95] disabled:pointer-events-none disabled:opacity-40",
        className
      )}
      aria-label={label}
      title={label}
      {...props}
    >
      {children}
    </button>
  );
}
