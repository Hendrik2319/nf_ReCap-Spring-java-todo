package com.example.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
public class IntegrationTestHelper {

	private final MockMvc mockMvc;
	private final ObjectMapper objectMapper;

	public String addTestTodoEntry(String description, String fixedDescription, TodoEntryStatus status) {
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

	public String getIdFromResultTodoEntry(String description, TodoEntryStatus status, ResultActions resultActions) {
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

	public boolean repoContainsTodoEntry(String id) {
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

}
