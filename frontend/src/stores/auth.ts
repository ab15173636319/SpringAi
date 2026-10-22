import { create } from "zustand";
import * as authApi from "../api/auth";
import { setAccessToken } from "../lib/token";
import type { User } from "../types";

export type AuthStatus = "booting" | "anonymous" | "authenticated";

interface AuthState {
  status: AuthStatus;
  user: User | null;
  boot: () => Promise<void>;
  login: (identifier: string, password: string) => Promise<void>;
  register: (input: {
    username: string;
    displayName: string;
    email: string;
    password: string;
  }) => Promise<void>;
  logout: () => Promise<void>;
  expire: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  status: "booting",
  user: null,

  boot: async () => {
    try {
      const session = await authApi.refresh();
      setAccessToken(session.accessToken);
      set({ status: "authenticated", user: session.user });
    } catch {
      setAccessToken(null);
      set({ status: "anonymous", user: null });
    }
  },

  login: async (identifier, password) => {
    const session = await authApi.login(identifier, password);
    setAccessToken(session.accessToken);
    set({ status: "authenticated", user: session.user });
  },

  register: async (input) => {
    const session = await authApi.register(input);
    setAccessToken(session.accessToken);
    set({ status: "authenticated", user: session.user });
  },

  logout: async () => {
    try {
      await authApi.logout();
    } finally {
      setAccessToken(null);
      set({ status: "anonymous", user: null });
    }
  },

  expire: () => {
    setAccessToken(null);
    set({ status: "anonymous", user: null });
  },
}));

if (typeof window !== "undefined") {
  window.addEventListener("aether:session-expired", () => {
    useAuthStore.getState().expire();
  });
}

