package com.sai.conversation.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class ConversationDto {

    @Id
    private String id;
    private String title;

}
