import { useCallback, useRef, useState } from "react";

export function useAuthSubmit() {
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const errorRef = useRef<HTMLDivElement>(null);

  const showError = useCallback((message: string) => {
    setError(message);
    requestAnimationFrame(() => errorRef.current?.focus());
  }, []);

  const run = useCallback(
    async (action: () => Promise<void>) => {
      setSubmitting(true);
      setError("");
      try {
        await action();
      } catch (exception) {
        showError(
          exception instanceof Error ? exception.message : "操作失败，请稍后重试"
        );
      } finally {
        setSubmitting(false);
      }
    },
    [showError]
  );

  return { submitting, error, errorRef, showError, run };
}
