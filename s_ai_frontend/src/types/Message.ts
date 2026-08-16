export const AssistantTypeValue = {
    User: "USER",
    Assistant: "ASSISTANT",
    System: "SYSTEM"
} as const;

export type AssistantType = typeof AssistantTypeValue[keyof typeof AssistantTypeValue]

export const MessageTypeValue = {
    Html: "Html",
    Text: "Text",
    MarkDown: "MarkDown",
    Json: "Json",
    Math: "Math"
} as const;

export type MessageType = typeof MessageTypeValue[keyof typeof MessageTypeValue]

/*
    private String id;

    private String conversationId;

    private Message message;

    private LocalDateTime timestamp;
*/
/**
 *     private String content;
    private String sender;
    private String receiver;
    private MessageType messageType;
 */
interface IMessage {
    text: string
    messageType: AssistantType
}

export type Message = IMessage


interface IMessageSend {
    id: string
    message: string
}

export type MessageSend = IMessageSend

interface IAmount {
    amount: number;
    max: number
}

export type Amount = IAmount

