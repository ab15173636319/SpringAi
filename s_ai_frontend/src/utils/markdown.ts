import { Marked, Renderer } from "@ts-stack/markdown";

class MyRenderer extends Renderer {

    /**
     * 标题
     */
    override heading(text: string, level: number, _raw: string): string {
        const size: Record<1 | 2 | 3 | 4 | 5 | 6, string> = {
            1: "text-3xl",
            2: "text-2xl",
            3: "text-xl",
            4: "text-lg",
            5: "text-md",
            6: "text-sm",
        };

        const baseCls = `mt-3 mb-2 font-bold text-gray-950 dark:text-gray-50 ${size[level]}`;
        return `<h${level} class="${baseCls}">${text}</h${level}>`;
    }

    /**
     * 代码块
     */
    override code(code: string, language: string | undefined, _isEscaped: boolean): string {
        const langCls = language ? ` data-lang="${language}"` : "";
        const label = language
            ? `<div class="px-4 py-1 text-xs text-gray-400 bg-gray-800 dark:bg-gray-700">${language}</div>`
            : "";
        return (
            `<div class="my-3 overflow-hidden rounded-lg border border-gray-200 dark:border-gray-700">` +
            `${label}` +
            `<pre class="overflow-x-auto bg-gray-900 p-4 text-sm text-gray-100"><code${langCls}>${code}</code></pre>` +
            `</div>`
        );
    }

    /**
     * 行内代码
     */
    override codespan(text: string): string {
        return `<code class="rounded bg-gray-100 px-1.5 py-0.5 text-sm text-rose-600 dark:bg-gray-800 dark:text-rose-400">${text}</code>`;
    }

    /**
     * 段落
     */
    override paragraph(text: string): string {
        return `<p class="leading-7 text-gray-700 dark:text-gray-200">${text}</p>`;
    }

    /**
     * 引用块
     */
    override blockquote(quote: string): string {
        return (
            `<blockquote class="my-3 border-l-4 border-gray-300 pl-4 italic text-gray-600 dark:border-gray-600 dark:text-gray-400">` +
            `${quote}</blockquote>`
        );
    }

    /**
     * 原始 HTML（直接透传）
     */
    override html(html: string): string {
        return html;
    }

    /**
     * 分隔线
     */
    override hr(): string {
        return `<hr class="my-4 border-0 border-t border-gray-200 dark:border-gray-700" />`;
    }

    /**
     * 列表（有序 / 无序）
     */
    override list(body: string, ordered?: boolean): string {
        const tag = ordered ? "ol" : "ul";
        const cls = ordered
            ? "list-decimal pl-6 my-2 space-y-1"
            : "list-disc pl-6 my-2 space-y-1";
        return `<${tag} class="${cls}">${body}</${tag}>`;
    }

    /**
     * 列表项
     */
    override listitem(text: string): string {
        const content = text.replace(/<\/?[uo]l>/g, "").trim();
        return `<li class="text-gray-700 dark:text-gray-200">${content}</li>`;
    }

    /**
     * 表格
     */
    override table(header: string, body: string): string {
        return (
            `<div class="my-3 overflow-x-auto">` +
            `<table class="w-full border-collapse text-sm">` +
            `<thead class="bg-gray-100 dark:bg-gray-800">${header}</thead>` +
            `<tbody>${body}</tbody>` +
            `</table></div>`
        );
    }

    /**
     * 表格行
     */
    override tablerow(content: string): string {
        return `<tr class="border-b border-gray-200 dark:border-gray-700">${content}</tr>`;
    }

    /**
     * 表格单元格
     */
    override tablecell(
        content: string,
        flags: { header?: boolean; align?: "center" | "left" | "right" }
    ): string {
        const align = flags.align ? ` style="text-align:${flags.align}"` : "";
        const cellCls = flags.header
            ? "px-3 py-2 font-semibold text-gray-900 dark:text-gray-100"
            : "px-3 py-2 text-gray-700 dark:text-gray-200";
        const tag = flags.header ? "th" : "td";
        return `<${tag} class="${cellCls}"${align}>${content}</${tag}>`;
    }

    /**
     * 加粗
     */
    override strong(text: string): string {
        return `<strong class="font-bold text-gray-900 dark:text-gray-50">${text}</strong>`;
    }

    /**
     * 斜体
     */
    override em(text: string): string {
        return `<em class="italic">${text}</em>`;
    }

    /**
     * 删除线
     */
    override del(text: string): string {
        return `<del class="line-through opacity-70">${text}</del>`;
    }

    /**
     * 换行
     */
    override br(): string {
        return `<br />`;
    }

    /**
     * 链接
     */
    override link(href: string, title: string, text: string): string {
        const titleAttr = title ? ` title="${title}"` : "";
        return (
            `<a href="${href}"${titleAttr} target="_blank" rel="noopener noreferrer" ` +
            `class="text-sky-600 underline decoration-sky-300 hover:text-sky-500 dark:text-sky-400">${text}</a>`
        );
    }

    /**
     * 图片
     */
    override image(href: string, title: string, text: string): string {
        const titleAttr = title ? ` title="${title}"` : "";
        return (
            `<img src="${href}" alt="${text}"${titleAttr} ` +
            `class="my-2 max-w-full rounded-lg" />`
        );
    }

    /**
     * 纯文本
     */
    override text(text: string): string {
        return text;
    }
}

Marked.setOptions({
    gfm: true,
    breaks: false,
    renderer: new MyRenderer(),
});

export const markdown = Marked;
