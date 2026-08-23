package com.sai.chat.entity;

import lombok.Data;
import org.springframework.ai.chat.messages.MessageType;

@Data
public class Message {
    private String content;
    private MessageType type;

}

