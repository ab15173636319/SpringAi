interface IConversation {
    id: string;
    title: string;
    top: 1 | 0;
    createTime: string;
    updateTime: string;
}

export type Conversation = IConversation