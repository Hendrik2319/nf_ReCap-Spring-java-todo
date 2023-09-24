package com.example.backend.changelog;

import com.example.backend.TodoEntry;
import com.example.backend.TodoEntryRepository;
import com.example.backend.TodoEntryStatus;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChangeLogServiceTest {

	private TodoEntryRepository todoEntryRepository;
	private ChangeLogRepository changeLogRepository;
	private ChangeLogEntryIndexService changeLogEntryIndexService;
	private ChangeLogService changeLogService;

	@BeforeEach
	void setUp() {
		todoEntryRepository = mock(TodoEntryRepository.class);
		changeLogRepository = mock(ChangeLogRepository.class);
		changeLogEntryIndexService = mock(ChangeLogEntryIndexService.class);
		changeLogService = new ChangeLogService(
				todoEntryRepository,
				changeLogRepository,
				changeLogEntryIndexService
		);
	}

	@NotNull
	private static List<ChangeLogEntry> createDefaults(int index1, int index2) {
		return List.of(
				new ChangeLogEntry(index1, "Todo1",
						new TodoEntry("Todo1", "TODO 1", TodoEntryStatus.OPEN),
						new TodoEntry("Todo1", "TODO A", TodoEntryStatus.OPEN)
				),
				new ChangeLogEntry(index2, "Todo1",
						new TodoEntry("Todo1", "TODO 1", TodoEntryStatus.OPEN),
						new TodoEntry("Todo1", "TODO A", TodoEntryStatus.IN_PROGRESS)
				)
		);
	}

	@Test
	void whenGetAllEntries_calledOnEmptyRepo() {
		// Given
		when(changeLogRepository.findAll())
				.thenReturn(List.of());

		// When
		List<ChangeLogEntry> actual = changeLogService.getAllEntries();

		// Then
		List<Object> expected = List.of();
		assertEquals(expected, actual);
	}

	@Test
	void whenGetAllEntries_calledOnFilledRepo() {
		// Given
		when(changeLogRepository.findAll()).thenReturn(createDefaults(0, 1));

		// When
		List<ChangeLogEntry> actual = changeLogService.getAllEntries();

		// Then
		List<ChangeLogEntry> expected = createDefaults(0, 1);
		assertEquals(expected, actual);
	}

	@Test
	void whenUndoLastChange_calledAndNoUndoableEntriesInLog_returnsNull() throws ChangeLogException {
		// Given
		when(changeLogEntryIndexService.getIndex()).thenReturn(0);

		// When
		ChangeLogEntry actual = changeLogService.undoLastChange();

		// Then
		assertNull(actual);
	}

	@Test
	void whenUndoLastChange_calledAndGetsWrongIndex_throwsException() {
		// Given
		when(changeLogEntryIndexService.getIndex()).thenReturn(-1);

		// When
		Executable executable = () -> changeLogService.undoLastChange();

		// Then
		assertThrows(ChangeLogException.class, executable);
	}

	@Test
	void whenUndoLastChange_calledAndGetsUnknownIndex_throwsException() {
		// Given
		when(changeLogEntryIndexService.getIndex()).thenReturn(3);
		when(changeLogRepository.findById(2)).thenReturn(Optional.empty());

		// When
		Executable executable = () -> changeLogService.undoLastChange();

		// Then
		assertThrows(ChangeLogException.class, executable);
	}

	@Test
	void whenUndoLastChange_calledAndGetsWrongStructuredChangeLogEntry1_throwsException() {
		// Given
		when(changeLogEntryIndexService.getIndex()).thenReturn(3);
		when(changeLogRepository.findById(2)).thenReturn(Optional.of(
				new ChangeLogEntry(1, "Todo1", null, null )
		));

		// When
		Executable executable = () -> changeLogService.undoLastChange();

		// Then
		assertThrows(ChangeLogException.class, executable);
	}

	@Test
	void whenUndoLastChange_calledAndGetsWrongStructuredChangeLogEntry2_throwsException() {
		// Given
		when(changeLogEntryIndexService.getIndex()).thenReturn(3);
		when(changeLogRepository.findById(2)).thenReturn(Optional.of(
				new ChangeLogEntry(2, "Todo1",
						new TodoEntry("Todo2", "TODO 1", TodoEntryStatus.OPEN),
						new TodoEntry("Todo1", "TODO A", TodoEntryStatus.IN_PROGRESS)
				)
		));

		// When
		Executable executable = () -> changeLogService.undoLastChange();

		// Then
		assertThrows(ChangeLogException.class, executable);
	}

	@Test
	void whenUndoLastChange_calledAndGetsWrongStructuredChangeLogEntry3_throwsException() {
		// Given
		when(changeLogEntryIndexService.getIndex()).thenReturn(3);
		when(changeLogRepository.findById(2)).thenReturn(Optional.of(
				new ChangeLogEntry(2, "Todo1",
						new TodoEntry("Todo1", "TODO 1", TodoEntryStatus.OPEN),
						new TodoEntry("Todo2", "TODO A", TodoEntryStatus.IN_PROGRESS)
				)
		));

		// When
		Executable executable = () -> changeLogService.undoLastChange();

		// Then
		assertThrows(ChangeLogException.class, executable);
	}

	@Test
	void whenUndoLastChange_calledAndGetsAnAddChangeLogEntry_returnsChangelogEntry() throws ChangeLogException {
		// Given
		when(changeLogEntryIndexService.getIndex()).thenReturn(3);
		when(changeLogRepository.findById(2)).thenReturn(Optional.of(
				new ChangeLogEntry(2, "Todo1",
						null,
						new TodoEntry("Todo1", "TODO A", TodoEntryStatus.IN_PROGRESS)
				)
		));

		// When
		ChangeLogEntry actual = changeLogService.undoLastChange();

		// Then
		verify(todoEntryRepository).deleteById("Todo1");
		verify(changeLogEntryIndexService).setIndex(2);
		ChangeLogEntry expected = new ChangeLogEntry(2, "Todo1",
				null,
				new TodoEntry("Todo1", "TODO A", TodoEntryStatus.IN_PROGRESS)
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenUndoLastChange_calledAndGetsAUpdateChangeLogEntry_returnsChangelogEntry() throws ChangeLogException {
		// Given
		when(changeLogEntryIndexService.getIndex()).thenReturn(3);
		when(changeLogRepository.findById(2)).thenReturn(Optional.of(
				new ChangeLogEntry(2, "Todo1",
						new TodoEntry("Todo1", "TODO 1", TodoEntryStatus.OPEN),
						new TodoEntry("Todo1", "TODO A", TodoEntryStatus.IN_PROGRESS)
				)
		));

		// When
		ChangeLogEntry actual = changeLogService.undoLastChange();

		// Then
		verify(todoEntryRepository).save(new TodoEntry("Todo1", "TODO 1", TodoEntryStatus.OPEN));
		verify(changeLogEntryIndexService).setIndex(2);
		ChangeLogEntry expected = new ChangeLogEntry(2, "Todo1",
				new TodoEntry("Todo1", "TODO 1", TodoEntryStatus.OPEN),
				new TodoEntry("Todo1", "TODO A", TodoEntryStatus.IN_PROGRESS)
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenUndoLastChange_calledAndGetsADeleteChangeLogEntry_returnsChangelogEntry() throws ChangeLogException {
		// Given
		when(changeLogEntryIndexService.getIndex()).thenReturn(3);
		when(changeLogRepository.findById(2)).thenReturn(Optional.of(
				new ChangeLogEntry(2, "Todo1",
						new TodoEntry("Todo1", "TODO 1", TodoEntryStatus.OPEN),
						null
				)
		));

		// When
		ChangeLogEntry actual = changeLogService.undoLastChange();

		// Then
		verify(todoEntryRepository).save(new TodoEntry("Todo1", "TODO 1", TodoEntryStatus.OPEN));
		verify(changeLogEntryIndexService).setIndex(2);
		ChangeLogEntry expected = new ChangeLogEntry(2, "Todo1",
				new TodoEntry("Todo1", "TODO 1", TodoEntryStatus.OPEN),
				null
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenRedoUndoneChange_calledAndNoRedoableEntriesInLog_returnsNull() throws ChangeLogException {
		// Given
		when(changeLogEntryIndexService.getIndex()).thenReturn(3);
		when(changeLogRepository.findById(3)).thenReturn(Optional.empty());

		// When
		ChangeLogEntry actual = changeLogService.redoUndoneChange();

		// Then
		assertNull(actual);
	}

	@Test
	void whenRedoUndoneChange_calledAndGetsWrongIndex_throwsException() {
		// Given
		when(changeLogEntryIndexService.getIndex()).thenReturn(-1);

		// When
		Executable executable = () -> changeLogService.redoUndoneChange();

		// Then
		assertThrows(ChangeLogException.class, executable);
	}

	@Test
	void whenRedoUndoneChange_calledAndGetsAnAddChangeLogEntry_returnsChangelogEntry() throws ChangeLogException {
		// Given
		when(changeLogEntryIndexService.getIndex()).thenReturn(2);
		when(changeLogRepository.findById(2)).thenReturn(Optional.of(
				new ChangeLogEntry(2, "Todo1",
						null,
						new TodoEntry("Todo1", "TODO A", TodoEntryStatus.IN_PROGRESS)
				)
		));

		// When
		ChangeLogEntry actual = changeLogService.redoUndoneChange();

		// Then
		verify(todoEntryRepository).save(new TodoEntry("Todo1", "TODO A", TodoEntryStatus.IN_PROGRESS));
		verify(changeLogEntryIndexService).setIndex(3);
		ChangeLogEntry expected = new ChangeLogEntry(2, "Todo1",
				null,
				new TodoEntry("Todo1", "TODO A", TodoEntryStatus.IN_PROGRESS)
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenRedoUndoneChange_calledAndGetsAUpdateChangeLogEntry_returnsChangelogEntry() throws ChangeLogException {
		// Given
		when(changeLogEntryIndexService.getIndex()).thenReturn(2);
		when(changeLogRepository.findById(2)).thenReturn(Optional.of(
				new ChangeLogEntry(2, "Todo1",
						new TodoEntry("Todo1", "TODO 1", TodoEntryStatus.OPEN),
						new TodoEntry("Todo1", "TODO A", TodoEntryStatus.IN_PROGRESS)
				)
		));

		// When
		ChangeLogEntry actual = changeLogService.redoUndoneChange();

		// Then
		verify(todoEntryRepository).save(new TodoEntry("Todo1", "TODO A", TodoEntryStatus.IN_PROGRESS));
		verify(changeLogEntryIndexService).setIndex(3);
		ChangeLogEntry expected = new ChangeLogEntry(2, "Todo1",
				new TodoEntry("Todo1", "TODO 1", TodoEntryStatus.OPEN),
				new TodoEntry("Todo1", "TODO A", TodoEntryStatus.IN_PROGRESS)
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenRedoUndoneChange_calledAndGetsADeleteChangeLogEntry_returnsChangelogEntry() throws ChangeLogException {
		// Given
		when(changeLogEntryIndexService.getIndex()).thenReturn(2);
		when(changeLogRepository.findById(2)).thenReturn(Optional.of(
				new ChangeLogEntry(2, "Todo1",
						new TodoEntry("Todo1", "TODO 1", TodoEntryStatus.OPEN),
						null
				)
		));

		// When
		ChangeLogEntry actual = changeLogService.redoUndoneChange();

		// Then
		verify(todoEntryRepository).deleteById("Todo1");
		verify(changeLogEntryIndexService).setIndex(3);
		ChangeLogEntry expected = new ChangeLogEntry(2, "Todo1",
				new TodoEntry("Todo1", "TODO 1", TodoEntryStatus.OPEN),
				null
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenLogChange_called() {
		// Given
		when(changeLogEntryIndexService.getIndex()).thenReturn(1);
		when(changeLogRepository.findAllAboveIndex(1)).thenReturn(createDefaults(2, 3));

		// When
		changeLogService.logChange(
				new TodoEntry("Todo1", "TODO 1", TodoEntryStatus.OPEN),
				new TodoEntry("Todo1", "TODO A", TodoEntryStatus.OPEN)
		);

		// Then
		verify(changeLogEntryIndexService).getIndex();
		verify(changeLogRepository).findAllAboveIndex(1);
		verify(changeLogRepository).deleteAll(createDefaults(2, 3));
		verify(changeLogRepository).save(new ChangeLogEntry(1, "Todo1",
				new TodoEntry("Todo1", "TODO 1", TodoEntryStatus.OPEN),
				new TodoEntry("Todo1", "TODO A", TodoEntryStatus.OPEN)
		));
		verify(changeLogEntryIndexService).setIndex(2);
	}

}