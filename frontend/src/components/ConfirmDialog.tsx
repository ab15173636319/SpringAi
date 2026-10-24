import { faCircleExclamation } from "@fortawesome/free-solid-svg-icons";
import Modal from "./base/Modal";

interface ConfirmDialogProps {
    open: boolean;
    title: string;
    description: string;
    confirmLabel: string;
    onConfirm: () => void;
    onClose: () => void;
    loading?: boolean;
}

export function ConfirmDialog({
    open,
    title,
    description,
    confirmLabel,
    onConfirm,
    onClose,
    loading = false,
}: ConfirmDialogProps) {
    return (
        <Modal
            open={open}
            title={title}
            description={description}
            icon={faCircleExclamation}
            tone="danger"
            okText={confirmLabel}
            okVariant="danger"
            confirmLoading={loading}
            onOk={onConfirm}
            onCancel={onClose}
        />
    );
}
