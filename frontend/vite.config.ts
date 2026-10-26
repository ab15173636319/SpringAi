import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";
import { fileURLToPath, URL } from "node:url";

function srcPath(path: string) {
  return fileURLToPath(new URL(`./src/${path}`, import.meta.url));
}

export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      "@": srcPath(""),
      "@api": srcPath("api"),
      "@css": srcPath("assets/styles"),
      "@type": srcPath("types"),
      "@utils": srcPath("lib"),
      "@views": srcPath("views"),
    },
  },
  server: {
    port: 5173,
    proxy: {
      "/api": {
        target: process.env.VITE_PROXY_TARGET ?? "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});

