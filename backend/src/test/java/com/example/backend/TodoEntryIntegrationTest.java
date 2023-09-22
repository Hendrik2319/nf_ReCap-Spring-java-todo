package com.example.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TodoEntryIntegrationTest {

    private static MockWebServer mockWebServer;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @BeforeAll
    static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void teardown() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void setUrlDynamically(DynamicPropertyRegistry reg) {
        reg.add("app.openai-api-url", ()->mockWebServer.url("/").toString());
        reg.add("app.openai-api-key", ()->"dummy_api_key");
        reg.add("app.openai-api-organization", ()->"dummy_api_org");
    }

    private static void enqueueMockResponseFromOpenAiApi(String expectedFixedText) {
        mockWebServer.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setBody("""
                                {
                                    "choices": [
                                        {
                                            "message": {
                                                "content": "%s"
                                            }
                                        }
                                    ]
                                }
                                """.formatted(expectedFixedText))
        );

    }

    private String addTestTodoEntry(String description, String fixedDescription, TodoEntryStatus status) {
        try {

            ResultActions resultActions = mockMvc
                    .perform(MockMvcRequestBuilders
                            .post("/api/todo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                        { "description":"%s", "status":"%s" }
                                    """.formatted(description, status))
                    )
                    .andExpect(status().isOk());

            return getIdFromResultTodoEntry(fixedDescription, status, resultActions);

        } catch (Exception e) {
            System.err.printf("%s while performing mock request: %s%n", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    private String getIdFromResultTodoEntry(String description, TodoEntryStatus status, ResultActions resultActions) {
        try {
            String body = resultActions
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            TodoEntry todoEntry = objectMapper.readValue(body, TodoEntry.class);

            assertNotNull(todoEntry);
            assertEquals(description, todoEntry.description());
            assertEquals(status, todoEntry.status());

            return todoEntry.id();

        } catch (Exception e) {
            System.err.printf("%s while parsing response: %s%n", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    private boolean repoContainsTodoEntry(String id) {
        try {
            return mockMvc
                    .perform(MockMvcRequestBuilders
                            .get("/api/todo/%s".formatted(id))
                    )
                    .andReturn()
                    .getResponse()
                    .getStatus() == 200;
        } catch (Exception e) {
            System.err.printf("%s while performing mock request: %s%n", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }

    @Test
    @DirtiesContext
    void whenGetAllEntries_calledOnEmptyDataBase_returnsEmptyList() throws Exception {
        // Given
        // When
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/api/todo")
                )
                // Then
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DirtiesContext
    void whenGetAllEntries_calledOnFilledDataBase_returnsFilledList() throws Exception {
        // Given
        enqueueMockResponseFromOpenAiApi("Fixed Entry 1");
        enqueueMockResponseFromOpenAiApi("Fixed Entry 2");
        enqueueMockResponseFromOpenAiApi("Fixed Entry 3");
        String id1 = addTestTodoEntry("Entry 1", "Fixed Entry 1", TodoEntryStatus.OPEN       );
        String id2 = addTestTodoEntry("Entry 2", "Fixed Entry 2", TodoEntryStatus.DONE       );
        String id3 = addTestTodoEntry("Entry 3", "Fixed Entry 3", TodoEntryStatus.IN_PROGRESS);

        // When
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/api/todo")
                )

                // Then
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    [
                        { "id":"%s", "description":"Fixed Entry 1", "status":"OPEN"        },
                        { "id":"%s", "description":"Fixed Entry 2", "status":"DONE"        },
                        { "id":"%s", "description":"Fixed Entry 3", "status":"IN_PROGRESS" }
                    ]
                """.formatted(id1, id2, id3)));
    }

    @Test
    @DirtiesContext
    void whenCreateEntry_getsNewTodoEntry_returnsTodoEntry() throws Exception {
        // Given
        enqueueMockResponseFromOpenAiApi("Fixed Entry Name");

        // When
        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"description\":\"Entry 1\", \"status\":\"OPEN\" }")
                )

                // Then
                .andExpect(status().isOk())
                .andExpect(content().json("{ \"description\":\"Fixed Entry Name\", \"status\":\"OPEN\" }"))
                .andExpect(jsonPath("$.id").isString());

        String id = getIdFromResultTodoEntry("Fixed Entry Name", TodoEntryStatus.OPEN, resultActions);
        assertNotNull( id );
        assertTrue( repoContainsTodoEntry(id) );
    }

    @Test
    @DirtiesContext
    void whenGetEntry_getsValidId_returnsTodoEntry() throws Exception {
        // Given
        enqueueMockResponseFromOpenAiApi("Fixed Entry 1");
        String id = addTestTodoEntry("Entry 1", "Fixed Entry 1", TodoEntryStatus.OPEN );

        // When
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/api/todo/%s".formatted(id))
                )

                // Then
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    { "id":"%s", "description":"Fixed Entry 1", "status":"OPEN" }
                """.formatted(id)));
    }

    @Test
    @DirtiesContext
    void whenGetEntry_getsInvalidId_returns404() throws Exception {
        // Given
        enqueueMockResponseFromOpenAiApi("Fixed Entry 1");
        String id = addTestTodoEntry("Entry 1", "Fixed Entry 1", TodoEntryStatus.OPEN );

        // When
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/api/todo/XX%sXX".formatted(id))
                )

                // Then
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    void whenUpdateEntry_getsInvalidId_returns404() throws Exception {
        // Given
        enqueueMockResponseFromOpenAiApi("Fixed Entry 1");
        String id = addTestTodoEntry("Entry 1", "Fixed Entry 1", TodoEntryStatus.OPEN );

        // When
        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/api/todo/XX%sXX".formatted(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "id":"XX%sXX", "description":"Entry 1", "status":"OPEN" }
                                """.formatted(id))
                )

                // Then
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    void whenUpdateEntry_getsDifferentIds_returns400() throws Exception {
        // Given
        enqueueMockResponseFromOpenAiApi("Fixed Entry 1");
        String id = addTestTodoEntry("Entry 1", "Fixed Entry 1", TodoEntryStatus.OPEN );

        // When
        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/api/todo/%s".formatted(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "id":"XX%sXX", "description":"Entry 1", "status":"OPEN" }
                                """.formatted(id))
                )

                // Then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext
    void whenUpdateEntry_getsValidIdWithoutOtherValues_returnsChangedTodoEntry() throws Exception {
        // Given
        enqueueMockResponseFromOpenAiApi("Fixed Entry 1");
        String id = addTestTodoEntry("Entry 1", "Fixed Entry 1", TodoEntryStatus.OPEN );

        // When
        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/api/todo/%s".formatted(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "id":"%s" }
                                """.formatted(id))
                )

                // Then
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        { "id":"%s", "description":"%s", "status":"%s" }
                """.formatted(id, "Fixed Entry 1", "OPEN")));
    }

    @Test
    @DirtiesContext
    void whenUpdateEntry_getsValidIdAndDescription_returnsChangedTodoEntry() throws Exception {
        // Given
        enqueueMockResponseFromOpenAiApi("Fixed Entry 1");
        String id = addTestTodoEntry("Entry 1", "Fixed Entry 1", TodoEntryStatus.OPEN );

        // When
        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/api/todo/%s".formatted(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "id":"%s", "description":"Entry A" }
                                """.formatted(id))
                )

                // Then
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        { "id":"%s", "description":"%s", "status":"%s" }
                """.formatted(id, "Entry A", "OPEN")));
    }

    @Test
    @DirtiesContext
    void whenUpdateEntry_getsValidIdAndStatus_returnsChangedTodoEntry() throws Exception {
        // Given
        enqueueMockResponseFromOpenAiApi("Fixed Entry 1");
        String id = addTestTodoEntry("Entry 1", "Fixed Entry 1", TodoEntryStatus.OPEN );

        // When
        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/api/todo/%s".formatted(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "id":"%s", "status":"DONE" }
                                """.formatted(id))
                )

                // Then
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        { "id":"%s", "description":"%s", "status":"%s" }
                """.formatted(id, "Fixed Entry 1", "DONE")));
    }

    @Test
    @DirtiesContext
    void whenUpdateEntry_getsValidIdAndDescriptionAndStatus_returnsUpdatedTodoEntry() throws Exception {
        // Given
        enqueueMockResponseFromOpenAiApi("Fixed Entry 1");
        String id = addTestTodoEntry("Entry 1", "Fixed Entry 1", TodoEntryStatus.OPEN );

        // When
        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/api/todo/%s".formatted(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "id":"%s", "description":"Entry A", "status":"DONE" }
                                """.formatted(id))
                )

                // Then
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        { "id":"%s", "description":"%s", "status":"%s" }
                """.formatted(id, "Entry A", "DONE")));
    }

    @Test
    @DirtiesContext
    void whenDeleteEntry_isCalled() throws Exception {
        // Given
        enqueueMockResponseFromOpenAiApi("Fixed Entry 1");
        String id = addTestTodoEntry("Entry 1", "Fixed Entry 1", TodoEntryStatus.OPEN );
        assertTrue( repoContainsTodoEntry(id) );

        // When
        mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/api/todo/%s".formatted(id))
                )

                // Then
                .andExpect(status().isOk())
                .andExpect(content().string(""))
        ;

        assertFalse( repoContainsTodoEntry(id) );
    }
}