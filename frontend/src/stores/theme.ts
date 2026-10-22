import { create } from "zustand";

export type Theme = "light" | "dark";

function initialTheme(): Theme {
  const saved = localStorage.getItem("aether-theme");
  if (saved === "light" || saved === "dark") return saved;
  return window.matchMedia("(prefers-color-scheme: dark)").matches
    ? "dark"
    : "light";
}

function applyTheme(theme: Theme) {
  document.documentElement.dataset.theme = theme;
  document.documentElement.style.colorScheme = theme;
  document
    .querySelector('meta[name="theme-color"]')
    ?.setAttribute("content", theme === "dark" ? "#151411" : "#F4F6FA");
}

const startTheme = initialTheme();
applyTheme(startTheme);

interface ThemeState {
  theme: Theme;
  toggle: () => void;
}

export const useThemeStore = create<ThemeState>((set, get) => ({
  theme: startTheme,
  toggle: () => {
    const next = get().theme === "light" ? "dark" : "light";
    localStorage.setItem("aether-theme", next);
    applyTheme(next);
    set({ theme: next });
  },
}));
