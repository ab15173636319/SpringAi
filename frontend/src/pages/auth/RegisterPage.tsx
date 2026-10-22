import { useState, type FormEvent } from "react";
import { useAuthStore } from "../../stores/auth";
import { PasswordField, TextField } from "./AuthFields";
import { AuthLayout } from "./AuthLayout";
import { useAuthSubmit } from "./useAuthSubmit";

export default function RegisterPage() {
  const register = useAuthStore((state) => state.register);
  const [username, setUsername] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const { submitting, error, errorRef, showError, run } = useAuthSubmit();

  const submit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!/^[a-zA-Z0-9_]{3,24}$/.test(username)) {
      showError("用户名需为 3–24 位字母、数字或下划线");
      return;
    }
    if (!displayName.trim()) {
      showError("请输入显示名称");
      return;
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      showError("请输入有效的邮箱地址");
      return;
    }
    if (password.length < 8) {
      showError("密码至少需要 8 个字符");
      return;
    }
    void run(() =>
      register({
        username: username.trim(),
        displayName: displayName.trim(),
        email: email.trim(),
        password,
      })
    );
  };

  return (
    <AuthLayout
      mode="register"
      submitting={submitting}
      error={error}
      errorRef={errorRef}
      onSubmit={submit}
    >
      <div className="grid gap-4 sm:grid-cols-2">
        <TextField
          label="用户名"
          value={username}
          onChange={(event) => setUsername(event.target.value)}
          autoComplete="username"
          placeholder="alex"
          required
        />
        <TextField
          label="显示名称"
          value={displayName}
          onChange={(event) => setDisplayName(event.target.value)}
          autoComplete="name"
          placeholder="Alex"
          required
        />
      </div>
      <TextField
        label="邮箱"
        type="email"
        value={email}
        onChange={(event) => setEmail(event.target.value)}
        autoComplete="email"
        placeholder="alex@example.com"
        required
      />
      <PasswordField
        value={password}
        onChange={setPassword}
        autoComplete="new-password"
        placeholder="至少 8 个字符"
      />
    </AuthLayout>
  );
}
