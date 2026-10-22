import type { ButtonHTMLAttributes } from "react";
import { cn } from "../lib/cn";
import { Spinner } from "./Loading";

type Variant = "primary" | "secondary" | "ghost" | "danger";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant;
  loading?: boolean;
}

const variants: Record<Variant, string> = {
  primary:
    "bg-ink text-canvas hover:bg-primary hover:text-on-primary active:bg-primary-strong",
  secondary:
    "border border-line bg-panel text-ink hover:border-primary/40 hover:bg-surface-muted",
  ghost: "text-muted hover:bg-surface-muted hover:text-ink",
  danger:
    "bg-danger text-on-danger hover:brightness-95",
};

export function Button({
  className,
  variant = "primary",
  loading = false,
  disabled,
  children,
  ...props
}: ButtonProps) {
  return (
    <button
      type="button"
      className={cn(
        "focus-ring inline-flex min-h-11 cursor-pointer items-center justify-center gap-2 rounded-lg px-4 text-sm font-semibold transition-[background-color,border-color,color,opacity,transform] duration-150 ease-[cubic-bezier(0.23,1,0.32,1)] active:scale-[0.97] disabled:pointer-events-none disabled:opacity-45",
        variants[variant],
        className
      )}
      disabled={disabled || loading}
      {...props}
    >
      {loading && <Spinner className="size-4" />}
      {children}
    </button>
  );
}
