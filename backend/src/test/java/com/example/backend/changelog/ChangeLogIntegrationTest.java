package com.example.backend.changelog;

import com.example.backend.IntegrationTestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ChangeLogIntegrationTest {

	@Autowired private MockMvc mockMvc;
	@Autowired private ObjectMapper objectMapper;
	private IntegrationTestHelper helper;

	@BeforeEach
	void setupEach() {
		helper = new IntegrationTestHelper(mockMvc, objectMapper);
	}

	@Test
	void showChangeLog() {
	}

	@Test
	void showChangeLogIndex() {
	}

	@Test
	void undoLastChange() {
	}

	@Test
	void redoUndoneChange() {
	}
}