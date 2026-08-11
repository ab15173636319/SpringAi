import type { RouteObject } from "react-router";
import { Ai } from "../pages/ai";

const aiRouter: RouteObject[] = [
    {
        index: true,
        Component: Ai,
    },
];

export { aiRouter };
