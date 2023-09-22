package com.example.backend.chatgpt;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatGptController {

    private final ChatGptService chatGptService;

    @PostMapping
    String askChatGpt(@RequestBody String prompt) {
        return chatGptService.askChatGPT(prompt);
    }

}
