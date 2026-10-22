import { lazy } from "react";
import { createBrowserRouter, Navigate } from "react-router-dom";
import { NotFoundPage } from "../pages/NotFoundPage";
import { RouteError } from "../pages/error/RouteError";
import { GuestOnly, RequireAuth, SessionGate } from "./guards";

const ChatPage = lazy(() =>
  import("../pages/ChatPage").then((module) => ({ default: module.ChatPage }))
);
const LoginPage = lazy(() => import("../pages/auth/LoginPage"));
const RegisterPage = lazy(() => import("../pages/auth/RegisterPage"));

export const router = createBrowserRouter([
  {
    element: <SessionGate />,
    errorElement: <RouteError />,
    children: [
      { path: "/", element: <Navigate to="/chat" replace /> },
      {
        element: <GuestOnly />,
        children: [
          { path: "/login", element: <LoginPage /> },
          { path: "/register", element: <RegisterPage /> },
        ],
      },
      {
        element: <RequireAuth />,
        children: [{ path: "/chat/:conversationId?", element: <ChatPage /> }],
      },
      { path: "*", element: <NotFoundPage /> },
    ],
  },
]);
