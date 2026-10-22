package com.aether.chat.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DeleteMessage {

    @NotBlank(message = "对话id不能为空")
    private String conversationId;

    @NotEmpty(message = "消息id列表不能为空")
    private List<String> messageIds;

}
