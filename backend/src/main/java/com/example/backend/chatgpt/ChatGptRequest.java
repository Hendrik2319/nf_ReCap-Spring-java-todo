package com.example.backend.chatgpt;

import org.springframework.lang.NonNull;

import java.io.PrintStream;
import java.util.List;

public record ChatGptRequest(
        @NonNull String model,
        @NonNull List<Message> messages
) {
    public void showContent(PrintStream out, String label) {
        out.printf("%s: ChatGptRequest%n", label);
        out.printf("    model: %s%n", model);
        out.printf("    messages: [%d]%n", messages.size());
        for (int i=0; i<messages.size(); i++) {
            Message message = messages.get(i);
            out.printf("        [%d] as \"%s\": %s%n", i, message.role, message.content);
        }
    }

    public record Message(
            @NonNull String role,
            @NonNull String content
    ) {}
}
