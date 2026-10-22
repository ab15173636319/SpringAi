export function RouteError() {
  return (
    <main className="grid min-h-dvh place-items-center bg-canvas px-6 text-ink">
      <div className="text-center">
        <h1 className="font-display text-5xl">页面加载失败</h1>
        <button
          type="button"
          onClick={() => window.location.reload()}
          className="focus-ring mt-6 min-h-11 cursor-pointer rounded-lg bg-ink px-5 text-sm font-semibold text-canvas"
        >
          重新加载
        </button>
      </div>
    </main>
  );
}
