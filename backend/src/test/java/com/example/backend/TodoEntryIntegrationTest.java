package com.example.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TodoEntryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenGetAllEntries_calledWithEmptyDataBase() throws Exception {
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

/*
    @Test
    void whenGetAllEntries_calledWithEmptyDataBase() throws Exception {
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
*/

    @Test
    void createEntry() {
    }

    @Test
    void getEntry() {
    }

    @Test
    void updateEntry() {
    }

    @Test
    void deleteEntry() {
    }
}