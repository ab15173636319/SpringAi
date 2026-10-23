import { StrictMode } from "react";

import { ConfigProvider } from 'antd';
import { createRoot } from "react-dom/client";
import { RouterProvider } from "react-router-dom";
import { router } from "./router/index";
import "./assets/styles/main.css"
import 'dayjs/locale/zh-cn';

import zhCN from 'antd/locale/zh_CN';

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <ConfigProvider locale={zhCN}>
      <RouterProvider router={router} />
    </ConfigProvider>
  </StrictMode>
);
