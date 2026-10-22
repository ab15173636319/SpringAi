import { chromium } from "playwright";
import AxeBuilder from "@axe-core/playwright";
import { mkdir } from "node:fs/promises";
import { resolve } from "node:path";

const outputDir = resolve("../.artifacts");
await mkdir(outputDir, { recursive: true });

const browser = await chromium.launch({
  executablePath: "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe",
  headless: true,
});

const context = await browser.newContext({
  viewport: { width: 1440, height: 960 },
  deviceScaleFactor: 1,
});
const page = await context.newPage();
const consoleErrors = [];
page.on("console", (message) => {
  if (message.type() === "error") consoleErrors.push(message.text());
});
page.on("pageerror", (error) => consoleErrors.push(error.message));

await page.goto("http://127.0.0.1:5173/login", { waitUntil: "networkidle" });
await page.screenshot({
  path: resolve(outputDir, "auth-desktop.png"),
  fullPage: true,
});

await page.goto("http://127.0.0.1:5173/register", {
  waitUntil: "networkidle",
});
const suffix = Date.now().toString().slice(-8);
await page.getByLabel("用户名").fill(`visual_${suffix}`);
await page.getByLabel("显示名称").fill("林默");
await page.getByLabel("邮箱").fill(`visual_${suffix}@example.com`);
await page.getByLabel("密码", { exact: true }).fill("Aether-test-12345");
await page.getByRole("button", { name: "创建账号并进入" }).click();
await page.waitForURL("**/chat/**", { timeout: 15_000 });
const activeConversationPath = new URL(page.url()).pathname;
await page.waitForTimeout(800);
await page.screenshot({
  path: resolve(outputDir, "chat-desktop.png"),
  fullPage: true,
});

await page.getByRole("button", { name: /梳理一个复杂问题/ }).click();
await page.waitForTimeout(2_500);
await page.screenshot({
  path: resolve(outputDir, "chat-response-desktop.png"),
  fullPage: true,
});

const accessibility = await new AxeBuilder({ page }).analyze();
const assistantSurface = await page
  .getByTestId("assistant-content")
  .first()
  .evaluate((element) => {
    const style = getComputedStyle(element);
    return {
      background: style.backgroundColor,
      border: style.borderTopWidth,
      shadow: style.boxShadow,
    };
  });
const assistantHasBubble =
  assistantSurface.background !== "rgba(0, 0, 0, 0)" ||
  assistantSurface.border !== "0px" ||
  assistantSurface.shadow !== "none";

await page
  .locator("header")
  .getByRole("button", { name: "切换到深色主题" })
  .click();
await page.waitForTimeout(250);
await page.screenshot({
  path: resolve(outputDir, "chat-dark-desktop.png"),
  fullPage: true,
});

const desktopOverflow = await page.evaluate(
  () => document.documentElement.scrollWidth - document.documentElement.clientWidth
);

await page.setViewportSize({ width: 390, height: 844 });
await page.waitForTimeout(400);
await page.screenshot({
  path: resolve(outputDir, "chat-mobile.png"),
  fullPage: true,
});
await page.getByRole("button", { name: "打开会话侧栏" }).click();
await page.waitForTimeout(350);
await page.screenshot({
  path: resolve(outputDir, "chat-mobile-sidebar.png"),
  fullPage: true,
});

const mobileOverflow = await page.evaluate(
  () => document.documentElement.scrollWidth - document.documentElement.clientWidth
);

await page.goto("http://127.0.0.1:5173/missing-route", {
  waitUntil: "networkidle",
});
await page.getByRole("heading", { name: "这一页不在对话里。" }).waitFor();
const notFoundTitle = await page.title();
await page.screenshot({
  path: resolve(outputDir, "not-found.png"),
  fullPage: true,
});

await page.goto(
  `http://127.0.0.1:5173/login?redirect=${encodeURIComponent(activeConversationPath)}`,
  { waitUntil: "networkidle" }
);
await page.waitForURL((url) => url.pathname === activeConversationPath);
const validRedirectWorked = new URL(page.url()).pathname === activeConversationPath;

await page.goto(
  "http://127.0.0.1:5173/login?redirect=%2F%2Fevil.example",
  { waitUntil: "networkidle" }
);
await page.waitForURL((url) => url.pathname.startsWith("/chat"));
const openRedirectBlocked = new URL(page.url()).hostname === "127.0.0.1";

const anonymousContext = await browser.newContext({
  viewport: { width: 1280, height: 900 },
});
const anonymousPage = await anonymousContext.newPage();
await anonymousPage.goto(
  "http://127.0.0.1:5173/chat/protected-example",
  { waitUntil: "networkidle" }
);
await anonymousPage.waitForURL((url) => url.pathname === "/login");
const protectedRouteRedirect = new URL(anonymousPage.url());
const guestRedirectWorked =
  protectedRouteRedirect.searchParams.get("redirect") ===
  "/chat/protected-example";
await anonymousPage.screenshot({
  path: resolve(outputDir, "auth-protected-redirect.png"),
  fullPage: true,
});
await anonymousContext.close();

console.log(
  JSON.stringify(
    {
      desktopOverflow,
      mobileOverflow,
      consoleErrors,
      accessibilityViolations: accessibility.violations.map((violation) => ({
        id: violation.id,
        impact: violation.impact,
        help: violation.help,
      })),
      assistantHasBubble,
      routeChecks: {
        notFoundTitle,
        validRedirectWorked,
        guestRedirectWorked,
        openRedirectBlocked,
      },
      screenshots: [
        "auth-desktop.png",
        "chat-desktop.png",
        "chat-response-desktop.png",
        "chat-dark-desktop.png",
        "chat-mobile.png",
        "chat-mobile-sidebar.png",
        "not-found.png",
        "auth-protected-redirect.png",
      ],
    },
    null,
    2
  )
);

await browser.close();
