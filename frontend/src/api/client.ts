import axios, {
  AxiosError,
  type AxiosRequestConfig,
  type AxiosResponse,
  type InternalAxiosRequestConfig,
} from "axios";
import type { ApiResult, AuthResponse } from "../types";
import { getAccessToken, setAccessToken } from "../lib/token";

interface RequestOptions extends AxiosRequestConfig {
  skipAuth?: boolean;
  skipAuthRefresh?: boolean;
}

type AppRequestConfig = InternalAxiosRequestConfig & {
  skipAuth?: boolean;
  skipAuthRefresh?: boolean;
  _retried?: boolean;
};

const baseURL = import.meta.env.VITE_API_BASE_URL ?? "/api";

export const apiClient = axios.create({
  baseURL,
  timeout: 20_000,
  withCredentials: true,
  headers: {
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.request.use((config) => {
  const requestConfig = config as AppRequestConfig;
  const token = getAccessToken();
  if (token && !requestConfig.skipAuth) {
    requestConfig.headers.Authorization = `Bearer ${token}`;
  }
  return requestConfig;
});

let refreshPromise: Promise<AuthResponse> | null = null;

export function refreshAccessToken() {
  if (!refreshPromise) {
    refreshPromise = apiClient
      .post<ApiResult<AuthResponse>>("/auth/refresh", null, {
        skipAuth: true,
        skipAuthRefresh: true,
      } as RequestOptions)
      .then((response) => {
        setAccessToken(response.data.data.accessToken);
        return response.data.data;
      })
      .catch(() => {
        setAccessToken(null);
        window.dispatchEvent(new Event("aether:session-expired"));
        throw new Error("登录已过期，请重新登录");
      })
      .finally(() => {
        refreshPromise = null;
      });
  }
  return refreshPromise;
}

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<ApiResult<never>>) => {
    const original = error.config as AppRequestConfig | undefined;

    if (
      error.response?.status === 401 &&
      original &&
      !original.skipAuthRefresh &&
      !original._retried
    ) {
      original._retried = true;
      const session = await refreshAccessToken();
      original.headers.Authorization = `Bearer ${session.accessToken}`;
      return apiClient(original);
    }

    throw new Error(
      error.response?.data?.message ??
        (error.code === "ECONNABORTED"
          ? "请求超时，请检查网络后重试"
          : "暂时无法连接服务")
    );
  }
);

function unwrap<T>(response: AxiosResponse<ApiResult<T>>) {
  return response.data.data;
}

export const api = {
  get<T>(url: string, options?: RequestOptions) {
    return apiClient.get<ApiResult<T>>(url, options).then(unwrap<T>);
  },

  post<T>(url: string, data?: unknown, options?: RequestOptions) {
    return apiClient.post<ApiResult<T>>(url, data, options).then(unwrap<T>);
  },

  patch<T>(url: string, data?: unknown, options?: RequestOptions) {
    return apiClient.patch<ApiResult<T>>(url, data, options).then(unwrap<T>);
  },

  delete<T = void>(url: string, options?: RequestOptions) {
    return apiClient.delete<ApiResult<T>>(url, options).then(unwrap<T>);
  },
};

