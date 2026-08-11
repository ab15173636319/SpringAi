package ai.s_ai.controller;

import io.swagger.v3.oas.annotations.parameters.ValidatedParameter;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final ChatClient chatClient;

    @GetMapping(value = "/chat/{id}", produces = "text/plain;charset=UTF-8")
    public Flux<String> chat(@PathVariable String id, @RequestParam String message) {
        return chatClient
                .prompt(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, id))
                .stream()
                .content();
    }

    @GetMapping(value = "/currentHistory/{id}", produces = "text/plain;charset=UTF-8")
    public List<String> currentHistory(@PathVariable String id, @RequestParam String message) {
        //  return chatClient.currentHistory(id);
        return List.of();
    }
}
