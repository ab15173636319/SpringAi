import {
  faArrowRight,
  faCircleCheck,
  faMoon,
  faShieldHalved,
  faSun,
} from "@fortawesome/free-solid-svg-icons";
import type { FormEvent, ReactNode, RefObject } from "react";
import { Link, useLocation } from "react-router-dom";
import { AppIcon } from "../../components/AppIcon";
import { BrandMark } from "../../components/BrandMark";
import { Button } from "../../components/Button";
import { IconButton } from "../../components/IconButton";
import { cn } from "../../lib/cn";
import { useThemeStore } from "../../stores/theme";

export type AuthMode = "login" | "register";

const COPY: Record<
  AuthMode,
  { tab: string; eyebrow: string; title: string; description: string; submit: string }
> = {
  login: {
    tab: "登录",
    eyebrow: "欢迎回来",
    title: "继续你的思考。",
    description: "登录后即可回到保存的会话与上下文。",
    submit: "进入工作区",
  },
  register: {
    tab: "注册",
    eyebrow: "创建工作区",
    title: "从一次对话开始。",
    description: "注册一个账号，建立只属于你的对话空间。",
    submit: "创建账号并进入",
  },
};

const TABS: { mode: AuthMode; to: string }[] = [
  { mode: "login", to: "/login" },
  { mode: "register", to: "/register" },
];

interface AuthLayoutProps {
  mode: AuthMode;
  submitting: boolean;
  error: string;
  errorRef: RefObject<HTMLDivElement | null>;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
  children: ReactNode;
}

export function AuthLayout({
  mode,
  submitting,
  error,
  errorRef,
  onSubmit,
  children,
}: AuthLayoutProps) {
  const { search } = useLocation();
  const theme = useThemeStore((state) => state.theme);
  const toggleTheme = useThemeStore((state) => state.toggle);
  const copy = COPY[mode];

  return (
    <main
      data-route-focus
      tabIndex={-1}
      className="auth-shell min-h-dvh bg-canvas text-ink outline-none lg:grid lg:grid-cols-[minmax(0,1.08fr)_minmax(440px,0.92fr)]"
    >
      <HeroPreview />

      <section className="auth-form-shell relative flex min-h-dvh flex-col px-5 py-6 sm:px-8 lg:px-12 xl:px-20">
        <div className="flex items-center justify-between lg:justify-end">
          <div className="flex items-center gap-2.5 lg:hidden">
            <BrandMark size="sm" />
            <span className="font-bold">Aether AI</span>
          </div>
          <IconButton
            label={theme === "light" ? "切换到深色主题" : "切换到浅色主题"}
            onClick={toggleTheme}
          >
            {theme === "light" ? (
              <AppIcon icon={faMoon} size={19} />
            ) : (
              <AppIcon icon={faSun} size={19} />
            )}
          </IconButton>
        </div>

        <div className="mx-auto flex w-full max-w-md flex-1 flex-col justify-center py-10">
          <p className="text-sm font-semibold text-primary">{copy.eyebrow}</p>
          <h2 className="mt-2 font-display text-4xl font-normal leading-tight tracking-[-0.02em] sm:text-5xl">
            {copy.title}
          </h2>
          <p className="mt-3 text-sm leading-6 text-muted">{copy.description}</p>

          <div
            className="auth-tabs mt-8 grid grid-cols-2 rounded-lg bg-surface-muted p-1"
            role="tablist"
            aria-label="账号操作"
          >
            {TABS.map((tab) => (
              <Link
                key={tab.mode}
                to={{ pathname: tab.to, search }}
                role="tab"
                aria-selected={mode === tab.mode}
                className={cn(
                  "focus-ring flex min-h-10 items-center justify-center rounded-lg text-sm font-semibold transition-colors",
                  mode === tab.mode
                    ? "bg-panel text-ink shadow-sm"
                    : "text-muted hover:text-ink"
                )}
              >
                {COPY[tab.mode].tab}
              </Link>
            ))}
          </div>

          <form className="mt-7 space-y-5" onSubmit={onSubmit} noValidate>
            {error && (
              <div
                ref={errorRef}
                tabIndex={-1}
                role="alert"
                className="rounded-xl border border-danger/25 bg-danger-soft px-4 py-3 text-sm leading-5 text-danger outline-none"
              >
                {error}
              </div>
            )}

            {children}

            <Button
              type="submit"
              className="mt-2 min-h-12 w-full"
              loading={submitting}
            >
              {copy.submit}
              {!submitting && <AppIcon icon={faArrowRight} size={18} />}
            </Button>
          </form>

          <p className="mt-7 text-center text-xs leading-5 text-muted">
            继续即表示你同意仅在本地项目中保存账号与会话数据。
          </p>
        </div>
      </section>
    </main>
  );
}

function HeroPreview() {
  return (
    <section className="auth-hero relative hidden min-h-dvh overflow-hidden border-r border-line bg-hero px-12 py-10 lg:flex lg:flex-col xl:px-20 xl:py-14">
      <header className="relative flex items-center gap-3">
        <BrandMark />
        <div>
          <p className="text-base font-bold tracking-[-0.02em]">Aether AI</p>
          <p className="text-xs text-muted">清晰思考，专注表达</p>
        </div>
      </header>

      <div className="relative my-auto max-w-2xl py-12">
        <p className="mb-7 text-[11px] font-semibold uppercase tracking-[0.18em] text-primary">
          A quiet place to think
        </p>
        <h1 className="font-display text-6xl font-normal leading-[0.94] tracking-[-0.025em] xl:text-7xl">
          <span className="block">把复杂问题，</span>
          <span className="mt-1 block italic text-primary">拆成清晰答案。</span>
        </h1>
        <p className="mt-7 max-w-lg text-[15px] leading-7 text-muted">
          对话、整理与回看都在同一个安静空间。每个会话独立保存，你的上下文与历史只属于你。
        </p>

        <div className="mt-12 max-w-xl border-y border-line py-5">
          <div className="flex items-center justify-between border-b border-line/70 pb-3">
            <span className="text-[10px] font-semibold uppercase tracking-[0.16em] text-muted">
              A thought in progress
            </span>
            <span className="text-[11px] tabular-nums text-muted">09:41</span>
          </div>
          <div className="space-y-6 pb-2 pt-5">
            <div className="ml-auto w-fit max-w-[78%] rounded-xl rounded-br-sm bg-user-bubble px-4 py-3 text-sm leading-6 text-ink">
              帮我把这次产品迭代整理成一份清晰的行动计划。
            </div>
            <div className="flex max-w-[92%] items-start gap-3">
              <BrandMark size="sm" />
              <div className="min-w-0 flex-1 text-sm leading-6 text-ink">
                <p className="mb-2 text-[10px] font-semibold uppercase tracking-[0.14em] text-muted">
                  Aether
                </p>
                <p className="font-medium">可以。先明确三个层次：</p>
                <div className="mt-2 space-y-1.5 text-muted">
                  {["本周必须完成的核心目标", "需要协作与验证的依赖项", "可延后但需记录的优化项"].map(
                    (item) => (
                      <p key={item} className="flex items-center gap-2">
                        <AppIcon
                          icon={faCircleCheck}
                          size={15}
                          className="text-success"
                        />
                        {item}
                      </p>
                    )
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <footer className="relative flex items-center gap-2 text-xs text-muted">
        <AppIcon
          icon={faShieldHalved}
          size={17}
          className="text-success"
        />
        刷新凭证保存在 HttpOnly Cookie，前端不接触长期密钥
      </footer>
    </section>
  );
}
