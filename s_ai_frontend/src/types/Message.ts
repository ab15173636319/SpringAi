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


/**
{
    "id": "6a844b2db3e8080a37dfe1c0",
    "conversationId": "2",
    "message": {
        "content": "hi",
        "type": "USER"
    },
    "timestamp": "2026-08-18T20:08:13.277"
}
 */




interface IMessage {
    content: string
    type: AssistantType
}

export type Message = IMessage


interface IMessageData {
    id: string
    conversationId: string
    message: IMessage
    timestamp: string
}

export type MessageData = IMessageData

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

interface ICharge {
    id: number;
    charge: number;
    price: number;
}

export type Charge = ICharge