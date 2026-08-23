interface IConversation {
  id: string;
  title: string;
  createTime: string;
  updateTime: string;
  top: 0 | 1;
}

export type Conversation = IConversation;
