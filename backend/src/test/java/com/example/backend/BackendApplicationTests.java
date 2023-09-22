package com.example.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class BackendApplicationTests {

    @DynamicPropertySource
    static void setUrlDynamically(DynamicPropertyRegistry reg) {
        reg.add("app.openai-api-url", ()->"dummy_url");
        reg.add("app.openai-api-key", ()->"dummy_api_key");
        reg.add("app.openai-api-organization", ()->"dummy_api_org");
    }

    @Test
    void contextLoads() {
    }

}
