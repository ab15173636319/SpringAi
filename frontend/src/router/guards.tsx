import { Suspense, useEffect } from "react";
import { Navigate, Outlet, useLocation } from "react-router-dom";
import { FullPageLoader } from "../components/Loading";
import { useAuthStore } from "../stores/auth";

function safeRedirect(value: string | null) {
  return value?.startsWith("/") && !value.startsWith("//") ? value : null;
}

/**
 * 预装页面
 */
export function SessionGate() {
  const status = useAuthStore((state) => state.status);
  const boot = useAuthStore((state) => state.boot);
  useEffect(() => {
    if (status === "booting") void boot();
  }, [boot, status]);

  if (status === "booting") {
    return <FullPageLoader label="正在恢复你的工作区" />;
  }

  return (
    <Suspense fallback={<FullPageLoader label="正在准备页面" />}>
      <Outlet />
    </Suspense>
  );
}

/*
  检查权限，是否登录
*/
export function RequireAuth() {
  const status = useAuthStore((state) => state.status);
  const location = useLocation();
  if (status !== "authenticated") {
    const redirect = encodeURIComponent(location.pathname + location.search);
    return <Navigate to={`/login?redirect=${redirect}`} replace />;
  }
  return <Outlet />;
}

export function GuestOnly() {
  const status = useAuthStore((state) => state.status);
  const location = useLocation();
  if (status !== "authenticated") return <Outlet />;

  const redirect = new URLSearchParams(location.search).get("redirect");
  return <Navigate to={safeRedirect(redirect) ?? "/chat"} replace />;
}
