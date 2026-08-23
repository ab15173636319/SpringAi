package com.sai.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatResp {

    public String type;
    public String msg;
    public List<String> quote;


}
