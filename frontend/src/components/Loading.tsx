import { BrandMark } from "./BrandMark";
import { cn } from "../lib/cn";

export function Spinner({ className }: { className?: string }) {
  return (
    <span
      className={cn(
        "inline-block size-5 animate-spin rounded-full border-2 border-current border-r-transparent",
        className
      )}
      aria-hidden="true"
    />
  );
}

export function FullPageLoader({ label }: { label: string }) {
  return (
    <main className="grid min-h-dvh place-items-center bg-canvas px-6 text-ink">
      <div className="flex flex-col items-center gap-5 text-center">
        <BrandMark size="lg" />
        <div>
          <p className="text-base font-semibold">{label}</p>
          <p className="mt-1 text-sm text-muted">正在确认安全会话</p>
        </div>
        <Spinner className="text-primary" />
      </div>
    </main>
  );
}
