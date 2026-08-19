import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import "./assets/iconfont/iconfont.css"
import "./assets/common.css"
import { RouterProvider } from 'react-router'
import router from './router/index.ts'
import "./utils/default.ts"
import Spain from './components/base/Spain.tsx'
import { useGlobalLoad } from './store/useGlobalLoad.ts'


function App() {
    const loading = useGlobalLoad((s) => s.loading)
    return (
        <Spain loading={loading}>
            <RouterProvider router={router} />
        </Spain>
    )
}


createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <App />
    </StrictMode>,
)
