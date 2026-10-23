import { faEye, faEyeSlash } from "@fortawesome/free-solid-svg-icons";
import { useId, useState, type InputHTMLAttributes } from "react";
import { AppIcon } from "../../components/AppIcon";
import { cn } from "../../lib/cn";

const inputClass =
  "focus-ring h-12 w-full rounded-lg border border-line bg-panel px-4 text-base text-ink placeholder:text-muted/70 hover:border-line-strong";

type TextFieldProps = InputHTMLAttributes<HTMLInputElement> & { label: string };

export function TextField({ label, className, ...props }: TextFieldProps) {
  const id = useId();

  return (
    <div>
      <label htmlFor={id} className="mb-2 block text-sm font-semibold">
        {label}
      </label>
      <input id={id} className={cn(inputClass, className)} {...props} />
    </div>
  );
}

interface PasswordFieldProps {
  value: string;
  onChange: (value: string) => void;
  autoComplete: string;
  placeholder: string;
}

export function PasswordField({
  value,
  onChange,
  autoComplete,
  placeholder,
}: PasswordFieldProps) {
  const id = useId();
  const [visible, setVisible] = useState(false);

  return (
    <div>
      <label htmlFor={id} className="mb-2 block text-sm font-semibold">
        密码
      </label>
      <span className="relative block">
        <input
          id={id}
          type={visible ? "text" : "password"}
          className={cn(inputClass, "pr-12")}
          value={value}
          onChange={(event) => onChange(event.target.value)}
          autoComplete={autoComplete}
          placeholder={placeholder}
          required
        />
        <button
          type="button"
          onClick={() => setVisible((current) => !current)}
          className="focus-ring absolute right-1 top-1 grid size-10 cursor-pointer place-items-center rounded-lg text-muted hover:bg-surface-muted hover:text-ink"
          aria-label={visible ? "隐藏密码" : "显示密码"}
        >
          {visible ? (
            <AppIcon icon={faEyeSlash} size={19} />
          ) : (
            <AppIcon icon={faEye} size={19} />
          )}
        </button>
      </span>
    </div>
  );
}
