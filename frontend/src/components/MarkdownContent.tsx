import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";

export default function MarkdownContent({ children }: { children: string }) {
  return (
    <div className="prose-aether">
      <ReactMarkdown
        remarkPlugins={[remarkGfm]}
        components={{
          a: ({ children: linkChildren, ...props }) => (
            <a {...props} target="_blank" rel="noreferrer">
              {linkChildren}
            </a>
          ),
          table: ({ children: tableChildren }) => (
            <div className="table-wrap">
              <table>{tableChildren}</table>
            </div>
          ),
          code: ({ children: codeChildren, className }) => (
            <code className={className}>{codeChildren}</code>
          ),
          input: ({ ...props }) => <input {...props} disabled />,
        }}
      >
        {children}
      </ReactMarkdown>
    </div>
  );
}

