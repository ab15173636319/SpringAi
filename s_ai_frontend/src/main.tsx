import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import "./assets/iconfont/iconfont.css"
import "./assets/common.css"
import { RouterProvider } from 'react-router'
import router from './router/index.ts'

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <RouterProvider router={router} />
    </StrictMode>,
)
