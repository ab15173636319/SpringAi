import { createBrowserRouter, Navigate } from "react-router-dom";
import NotFoundPage from "../views/NotFoundPage";
import RouteError from "../views/error/RouteError";
import { authRoutes } from "./module/auth";
import { chatRoutes } from "./module/chat";
import { GuestOnly, RequireAuth, SessionGate } from "./guards";

export const router = createBrowserRouter([
  {
    element: <SessionGate />,
    errorElement: <RouteError />,
    children: [
      { path: "/", element: <Navigate to="/chat" replace /> },
      {
        element: <GuestOnly />,
        children: authRoutes,
      },
      {
        element: <RequireAuth />,
        children: chatRoutes,
      },
      { path: "*", element: <NotFoundPage /> },
    ],
  },
]);
