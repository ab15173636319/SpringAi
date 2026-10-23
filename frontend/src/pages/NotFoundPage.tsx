import { faArrowLeft, faArrowRight } from "@fortawesome/free-solid-svg-icons";
import { useNavigate } from "react-router-dom";
import { AppIcon } from "../components/AppIcon";
import { BrandMark } from "../components/BrandMark";
import { Button } from "../components/Button";
import { useAuthStore } from "../stores/auth";

export function NotFoundPage() {
  const navigate = useNavigate();
  const status = useAuthStore((state) => state.status);
  const authenticated = status === "authenticated";

  return (
    <main
      data-route-focus
      tabIndex={-1}
      className="not-found-page grid min-h-dvh place-items-center bg-canvas px-6 text-ink outline-none"
    >
      <section className="w-full max-w-2xl">
        <BrandMark size="lg" />
        <p className="mt-8 font-display text-[clamp(5rem,18vw,10rem)] leading-[0.72] text-primary">
          404
        </p>
        <h1 className="mt-7 font-display text-4xl leading-tight sm:text-5xl">
          这一页不在对话里。
        </h1>
        <p className="mt-4 max-w-lg text-sm leading-6 text-muted">
          地址可能已经改变，或者这个页面从来没有存在过。你的会话和历史不会受到影响。
        </p>
        <div className="mt-8 flex flex-wrap gap-3">
          <Button
            variant="secondary"
            onClick={() => navigate(-1)}
          >
            <AppIcon icon={faArrowLeft} size={17} />
            返回上一页
          </Button>
          <Button
            onClick={() =>
              navigate(authenticated ? "/chat" : "/login", { replace: true })
            }
          >
            {authenticated ? "回到工作台" : "返回登录"}
            <AppIcon icon={faArrowRight} size={17} />
          </Button>
        </div>
      </section>
    </main>
  );
}
