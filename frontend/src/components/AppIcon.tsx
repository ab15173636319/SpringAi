import type { IconDefinition } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import type { ComponentProps } from "react";

type FontAwesomeIconProps = ComponentProps<typeof FontAwesomeIcon>;

interface AppIconProps extends Omit<FontAwesomeIconProps, "icon" | "size"> {
  icon: IconDefinition;
  size?: number;
}

export function AppIcon({
  icon,
  size = 16,
  style,
  ...props
}: AppIconProps) {
  return (
    <FontAwesomeIcon
      icon={icon}
      aria-hidden={props["aria-label"] ? undefined : true}
      style={{ ...style, fontSize: `${size}px` }}
      {...props}
    />
  );
}
