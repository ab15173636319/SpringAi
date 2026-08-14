package ai.s_ai.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "ai_chat_memory")
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    @Id
    private String id;

    private String conversationId;

    private Message message;

    private LocalDateTime timestamp;

    private String _class;

}
