import type { SystemInfo, Usage } from "../types";
import { api } from "./http";

export async function getUsage() {
  return api.get<Usage>("/usage");
}

export async function getSystemInfo() {
  return api.get<SystemInfo>("/system/info");
}
