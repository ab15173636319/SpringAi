import { useState, type FormEvent } from "react";
import { useAuthStore } from "../../stores/auth";
import { PasswordField, TextField } from "./AuthFields";
import { AuthLayout } from "./AuthLayout";
import { useAuthSubmit } from "./useAuthSubmit";

export default function LoginPage() {
  const login = useAuthStore((state) => state.login);
  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");
  const { submitting, error, errorRef, showError, run } = useAuthSubmit();

  const submit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!identifier.trim()) {
      showError("请输入用户名或邮箱");
      return;
    }
    if (!password) {
      showError("请输入密码");
      return;
    }
    void run(() => login(identifier.trim(), password));
  };

  return (
    <AuthLayout
      mode="login"
      submitting={submitting}
      error={error}
      errorRef={errorRef}
      onSubmit={submit}
    >
      <TextField
        label="用户名或邮箱"
        value={identifier}
        onChange={(event) => setIdentifier(event.target.value)}
        autoComplete="username"
        placeholder="例如 alex 或 alex@example.com"
        required
      />
      <PasswordField
        value={password}
        onChange={setPassword}
        autoComplete="current-password"
        placeholder="输入密码"
      />
    </AuthLayout>
  );
}
