import { createBrowserRouter } from "react-router";
import { aiRouter } from "./ai";
import App from "../pages/App";

const router = createBrowserRouter([
    {
        path: "/",
        Component: App
    },
    {
        path: "/ai",
        children: [
            ...aiRouter,
        ],
    },
]);

export default router;
