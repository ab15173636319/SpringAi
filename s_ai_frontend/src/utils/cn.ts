import type { ClassValue } from "clsx";
import clsx from "clsx";
import { twMerge } from "tailwind-merge";

/**
 * 合并 Tailwind CSS 类名，并自动去重冲突的样式。
 *
 * 内部先使用 `clsx` 处理条件类名（支持字符串、数组、对象等形式），
 * 再通过 `tailwind-merge` 合并，确保后出现的同名工具类覆盖前者，
 * 避免例如 `px-2 px-4` 这类冲突导致样式不符合预期。
 *
 * @param inputs 任意数量的类名输入，可为字符串、数组、对象或它们的组合（clsx 支持的 ClassValue 类型）
 * @returns 合并并去重后的 className 字符串
 *
 * @example
 * cn("px-2", condition && "px-4", { "text-red": isError }) // 结果为 "text-red px-4"
 */
export function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs))
}