package ai.s_ai.entity.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class ConversationDto {

    @Id
    private String id;
    private String title;

}
