export const AssistantTypeValue = {
    User: "user",
    Assistant: "assistant",
    System: "system"
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

interface IMessage {
    assistant: AssistantType
    type: MessageType
    message: string
}

export type Message = IMessage

interface IMessageSend {
    id: string
    message: string
}

export type MessageSend = IMessageSend

