import type { AuthResponse, User } from "../types";
import { api, refreshAccessToken } from "./http";

export async function login(identifier: string, password: string) {
  return api.post<AuthResponse>(
    "/auth/login",
    { identifier, password },
    { skipAuth: true, skipAuthRefresh: true }
  );
}

export async function register(input: {
  username: string;
  displayName: string;
  email: string;
  password: string;
}) {
  return api.post<AuthResponse>(
    "/auth/register",
    input,
    { skipAuth: true, skipAuthRefresh: true }
  );
}

export async function refresh() {
  return refreshAccessToken();
}

export async function logout() {
  await api.post<null>("/auth/logout", undefined, {
    skipAuth: true,
    skipAuthRefresh: true,
  });
}

export async function me() {
  return api.get<User>("/auth/me");
}
