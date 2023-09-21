package com.example.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TodoEntryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String addTestTodoEntry(String description, String status) {
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

            return getIdFromResultTodoEntry(description, status, resultActions);

        } catch (Exception e) {
            System.err.printf("%s while performing mock request: %s%n", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    private String getIdFromResultTodoEntry(String description, String status, ResultActions resultActions) {
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
        String id1 = addTestTodoEntry("Entry 1", "OPEN"      );
        String id2 = addTestTodoEntry("Entry 2", "DONE"      );
        String id3 = addTestTodoEntry("Entry 3", "IN_PROGESS");

        // When
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/api/todo")
                )

                // Then
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    [
                        { "id":"%s", "description":"Entry 1", "status":"OPEN"       },
                        { "id":"%s", "description":"Entry 2", "status":"DONE"       },
                        { "id":"%s", "description":"Entry 3", "status":"IN_PROGESS" }
                    ]
                """.formatted(id1, id2, id3)));
    }

    @Test
    @DirtiesContext
    void whenCreateEntry_getsNewTodoEntry_returnsTodoEntry() throws Exception {
        // Given
        // When
        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"description\":\"Entry 1\", \"status\":\"OPEN\" }")
                )

                // Then
                .andExpect(status().isOk())
                .andExpect(content().json("{ \"description\":\"Entry 1\", \"status\":\"OPEN\" }"))
                .andExpect(jsonPath("$.id").isString());

        String id = getIdFromResultTodoEntry("Entry 1", "OPEN", resultActions);
        assertNotNull( id );
        assertTrue( repoContainsTodoEntry(id) );
    }

    @Test
    @DirtiesContext
    void whenGetEntry_getsValidId_returnsTodoEntry() throws Exception {
        // Given
        String id = addTestTodoEntry("Entry 1", "OPEN" );

        // When
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/api/todo/%s".formatted(id))
                )

                // Then
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    { "id":"%s", "description":"Entry 1", "status":"OPEN" }
                """.formatted(id)));
    }

    @Test
    @DirtiesContext
    void whenGetEntry_getsInvalidId_returns404() throws Exception {
        // Given
        String id = addTestTodoEntry("Entry 1", "OPEN" );

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
        String id = addTestTodoEntry("Entry 1", "OPEN" );

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
        String id = addTestTodoEntry("Entry 1", "OPEN" );

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
    void whenUpdateEntry_getsValidIdAndDescription_returnsChangedProduct() throws Exception {
        // Given
        String id = addTestTodoEntry("Entry 1", "OPEN" );

        // When
        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/api/todo/%s".formatted(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "description":"Entry A" }
                                """)
                )

                // Then
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        { "id":"%s", "description":"%s", "status":"%s" }
                """.formatted(id, "Entry A", "OPEN")));
    }

    @Test
    @DirtiesContext
    void whenUpdateEntry_getsValidIdAndStatus_returnsChangedProduct() throws Exception {
        // Given
        String id = addTestTodoEntry("Entry 1", "OPEN" );

        // When
        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/api/todo/%s".formatted(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "status":"DONE" }
                                """)
                )

                // Then
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        { "id":"%s", "description":"%s", "status":"%s" }
                """.formatted(id, "Entry 1", "DONE")));
    }

    @Test
    @DirtiesContext
    void whenUpdateEntry_getsValidIdAndDescriptionAndStatus_returnsChangedProduct() throws Exception {
        // Given
        String id = addTestTodoEntry("Entry 1", "OPEN" );

        // When
        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/api/todo/%s".formatted(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "description":"Entry A", "status":"DONE" }
                                """)
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
        String id = addTestTodoEntry("Entry 1", "OPEN" );
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