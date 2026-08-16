import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from "@tailwindcss/vite";
import { viteMockServe } from 'vite-plugin-mock'

// https://vite.dev/config/
export default defineConfig({
    plugins: [react(), tailwindcss(), viteMockServe({
        mockPath: 'mock',
        enable: true,
        watchFiles: true
    })],
    server: {
        proxy: {
            '/api': {
                target: "http://localhost:9999",
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, '')
            }
        }
    },
})
