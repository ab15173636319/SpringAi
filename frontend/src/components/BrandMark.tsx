import { cn } from "../lib/cn";

interface BrandMarkProps {
  size?: "sm" | "md" | "lg";
  className?: string;
}

const sizes = {
  sm: "size-7 rounded-full",
  md: "size-9 rounded-full",
  lg: "size-12 rounded-full",
};

export function BrandMark({ size = "md", className }: BrandMarkProps) {
  return (
    <span
      className={cn(
        "brand-mark relative inline-flex shrink-0 items-center justify-center overflow-hidden border border-primary/30 bg-primary text-on-primary",
        sizes[size],
        className
      )}
      aria-hidden="true"
    >
      <svg
        viewBox="0 0 32 32"
        fill="none"
        className="size-[66%]"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          d="M16 4.5C16.8 10.6 21.4 15.2 27.5 16C21.4 16.8 16.8 21.4 16 27.5C15.2 21.4 10.6 16.8 4.5 16C10.6 15.2 15.2 10.6 16 4.5Z"
          fill="currentColor"
        />
        <circle cx="24.8" cy="7.2" r="2.2" fill="var(--canvas)" />
      </svg>
    </span>
  );
}
