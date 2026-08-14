package ai.s_ai.service;


import ai.s_ai.entity.Chat;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiChatService {

    public List<Chat> getChats(String conversationId) {
        if(conversationId == null){
            return null;
        }
    }

}
