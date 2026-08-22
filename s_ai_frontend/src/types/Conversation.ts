interface IConversation {
  id: string;
  title: string;
  createTime: string;
  updateTime: string;
  top: number;
}

export type Conversation = IConversation;
