package com.example.backend.chatgpt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ChatGptService {

    private final WebClient webClient;

    public ChatGptService(
            @Value("${app.openai-api-url}") String openaiApiUrl,
            @Value("${app.openai-api-key}") String openaiApiKey,
            @Value("${app.openai-api-organization}") String openaiApiOrganization
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(openaiApiUrl)
                .defaultHeader("Authorization", "Bearer " + openaiApiKey)
                .defaultHeader("OpenAI-Organization", openaiApiOrganization)
                .build();
    }

    public String askChatGPT(String prompt) {
        ChatGptRequest request = new ChatGptRequest(
                "gpt-3.5-turbo",
                List.of(
                        new ChatGptRequest.Message(
                                "user",
                                prompt
                        )
                )
        );

        request.showContent(System.out, "request");

        ChatGptResponse response = executeRequest(request);
        if (response==null) {
            System.out.println("response: <null>");
            return null;
        }

        response.showContent(System.out, "response");

        List<ChatGptResponse.Choice> choices = response.choices();
        if (choices==null || choices.isEmpty()) return null;

        ChatGptResponse.Choice firstChoice = choices.get(0);
        if (firstChoice==null) return null;

        ChatGptResponse.Message message = firstChoice.message();
        if (message==null) return null;

        return message.content();
    }

    private ChatGptResponse executeRequest(ChatGptRequest request) {
        ResponseEntity<ChatGptResponse> responseEntity = webClient.post()
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.empty())
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.empty())
                .toEntity(ChatGptResponse.class)
                .block();

        if (responseEntity==null) return null;

        HttpStatusCode statusCode = responseEntity.getStatusCode();
        if (statusCode.is4xxClientError()) return null;
        if (statusCode.is5xxServerError()) return null;

        return responseEntity.getBody();
    }
}
