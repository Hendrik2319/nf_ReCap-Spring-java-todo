package com.example.backend.changelog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ChangeLogEntryIndexServiceTest {

	private ChangeLogEntryIndexRepository changeLogEntryIndexRepository;
	private ChangeLogEntryIndexService changeLogEntryIndexService;

	@BeforeEach
	void setUp() {
		changeLogEntryIndexRepository = mock(ChangeLogEntryIndexRepository.class);
		changeLogEntryIndexService = new ChangeLogEntryIndexService(
				changeLogEntryIndexRepository
		);
	}

	@Test
	void whenSetIndex_calledOnInitiallyEmptyIndexRepo() {
		// Given
		when(changeLogEntryIndexRepository.findAll()).thenReturn(List.of());

		// When
		changeLogEntryIndexService.setIndex(1);

		// Then
		verify(changeLogEntryIndexRepository).findAll();
		verify(changeLogEntryIndexRepository).save(
				new ChangeLogEntryIndex(null, 1)
		);
	}

	@Test
	void whenSetIndex_calledOnNonEmptyIndexRepo() {
		// Given
		when(changeLogEntryIndexRepository.findAll()).thenReturn(List.of(
				new ChangeLogEntryIndex("id1", 1)
		));

		// When
		changeLogEntryIndexService.setIndex(2);

		// Then
		verify(changeLogEntryIndexRepository).findAll();
		verify(changeLogEntryIndexRepository).save(
				new ChangeLogEntryIndex("id1", 2)
		);
	}

	@Test
	void whenGetIndex_calledOnInitiallyEmptyIndexRepo() {
		// Given
		when(changeLogEntryIndexRepository.findAll()).thenReturn(List.of());

		// When
		int actual = changeLogEntryIndexService.getIndex();

		// Then
		verify(changeLogEntryIndexRepository).findAll();
		int expected = 0;
		assertEquals(expected, actual);
	}

	@Test
	void whenGetChangeLogEntryIndex_calledOnNonEmptyIndexRepo() {
		// Given
		when(changeLogEntryIndexRepository.findAll()).thenReturn(List.of(
				new ChangeLogEntryIndex("id1", 1)
		));

		// When
		int actual = changeLogEntryIndexService.getIndex();

		// Then
		verify(changeLogEntryIndexRepository).findAll();
		int expected = 1;
		assertEquals(expected, actual);
	}}