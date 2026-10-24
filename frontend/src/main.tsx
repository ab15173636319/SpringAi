import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { RouterProvider } from "react-router-dom";
import AppProviders from "./components/AppProviders";
import { router } from "./router/index";
import "./assets/styles/main.css";

const root = document.getElementById("root");
if (!root) throw new Error("缺少应用挂载节点 #root");

createRoot(root).render(
  <StrictMode>
    <AppProviders>
      <RouterProvider router={router} />
    </AppProviders>
  </StrictMode>
);
