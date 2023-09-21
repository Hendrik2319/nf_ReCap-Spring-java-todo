package com.example.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TodoEntryServiceTest {

    private TodoEntryRepository todoEntryRepository;
    private TodoEntryService todoEntryService;

    @BeforeEach
    void setup() {
        todoEntryRepository = mock(TodoEntryRepository.class);
        todoEntryService = new TodoEntryService(todoEntryRepository);
    }

    @Test
    void whenGetAllEntries_calledOnEmptyRepo_returnEmptyList() {
        // Given
        when(todoEntryRepository.findAll()).thenReturn(List.of());

        // When
        List<TodoEntry> actual = todoEntryService.getAllEntries();

        // Then
        verify(todoEntryRepository).findAll();
        List<Object> expected = List.of();
        assertEquals(expected, actual);
    }

    @Test
    void whenGetAllEntries_calledOnFilledRepo_returnListOfTodoEntries() {
        // Given
        when(todoEntryRepository.findAll()).thenReturn(List.of(
                new TodoEntry("123","Entry 1","OPEN"       ),
                new TodoEntry("124","Entry 2","DONE"       ),
                new TodoEntry("125","Entry 3","IN_PROGRESS"),
                new TodoEntry("126","Entry 4","DONE"       ),
                new TodoEntry("127","Entry 5","OPEN"       )
        ));

        // When
        List<TodoEntry> actual = todoEntryService.getAllEntries();

        // Then
        verify(todoEntryRepository).findAll();
        List<TodoEntry> expected = List.of(
                new TodoEntry("123","Entry 1","OPEN"       ),
                new TodoEntry("124","Entry 2","DONE"       ),
                new TodoEntry("125","Entry 3","IN_PROGRESS"),
                new TodoEntry("126","Entry 4","DONE"       ),
                new TodoEntry("127","Entry 5","OPEN"       )
        );
        assertEquals(expected, actual);
    }

    @Test
    void whenCreateEntry_isCalled_returnsSavedTodoEntry() {
        // Given
        when(todoEntryRepository.save( new TodoEntry(null,"Entry 1","OPEN") ))
                          .thenReturn( new TodoEntry("123","Entry 1","OPEN") );

        // When
        TodoEntry actual = todoEntryService.createEntry(new NewTodoEntry("Entry 1","OPEN"));

        // Then
        verify(todoEntryRepository).save(new TodoEntry(null,"Entry 1","OPEN"));
        TodoEntry expected = new TodoEntry("123","Entry 1","OPEN");
        assertEquals(expected, actual);
    }

    @Test
    void whenGetEntry_getsInvalidID_returnEmptyOptional() {
        // Given
        when(todoEntryRepository.findById("456")).thenReturn(
                Optional.empty()
        );

        // When
        Optional<TodoEntry> actual = todoEntryService.getEntry("456");

        // Then
        verify(todoEntryRepository).findById("456");
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void whenGetEntry_getsValidID_returnTodoEntryInOptional() {
        // Given
        when(todoEntryRepository.findById("456")).thenReturn(
                Optional.of(new TodoEntry("456","Entry 1","OPEN"))
        );

        // When
        Optional<TodoEntry> actual = todoEntryService.getEntry("456");

        // Then
        verify(todoEntryRepository).findById("456");
        TodoEntry expected = new TodoEntry("456","Entry 1","OPEN");
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void whenUpdateEntry_getsInvalidID_returnEmptyOptional() {
        // Given
        when(todoEntryRepository.findById("456")).thenReturn(
                Optional.empty()
        );

        // When
        Optional<TodoEntry> actual = todoEntryService.updateEntry("456", new TodoEntry("456","Entry 1","OPEN"));

        // Then
        verify(todoEntryRepository).findById("456");
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void whenUpdateEntry_getsValidIDWithoutOtherValues_returnUnchangedTodoEntryInOptional() {
        // Given
        when(todoEntryRepository.findById("456"))
                .thenReturn( Optional.of( new TodoEntry("456","Entry 1","OPEN") ));
        when(todoEntryRepository.save(    new TodoEntry("456","Entry 1","OPEN") ))
                .thenReturn(              new TodoEntry("456","Entry 1","OPEN") );

        // When
        Optional<TodoEntry> actual = todoEntryService.updateEntry("456", new TodoEntry(null,null,null));

        // Then
        verify(todoEntryRepository).findById("456");
        TodoEntry expected = new TodoEntry("456", "Entry 1", "OPEN");
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void whenUpdateEntry_getsValidIDAndDescription_returnUpdatedTodoEntryInOptional() {
        // Given
        when(todoEntryRepository.findById("456"))
                .thenReturn( Optional.of( new TodoEntry("456","Entry 1","OPEN") ));
        when(todoEntryRepository.save(    new TodoEntry("456","ABC"    ,"OPEN") ))
                .thenReturn(              new TodoEntry("456","ABC"    ,"OPEN") );

        // When
        Optional<TodoEntry> actual = todoEntryService.updateEntry("456", new TodoEntry(null,"ABC",null));

        // Then
        verify(todoEntryRepository).findById("456");
        TodoEntry expected = new TodoEntry("456", "ABC", "OPEN");
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void whenUpdateEntry_getsValidIDAndStatus_returnUpdatedTodoEntryInOptional() {
        // Given
        when(todoEntryRepository.findById("456"))
                .thenReturn( Optional.of( new TodoEntry("456","Entry 1","OPEN") ));
        when(todoEntryRepository.save(    new TodoEntry("456","Entry 1","DONE") ))
                .thenReturn(              new TodoEntry("456","Entry 1","DONE") );

        // When
        Optional<TodoEntry> actual = todoEntryService.updateEntry("456", new TodoEntry(null,null,"DONE"));

        // Then
        verify(todoEntryRepository).findById("456");
        TodoEntry expected = new TodoEntry("456", "Entry 1", "DONE");
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void whenUpdateEntry_getsValidIDAndDescriptionAndStatus_returnUpdatedTodoEntryInOptional() {
        // Given
        when(todoEntryRepository.findById("456"))
                .thenReturn( Optional.of( new TodoEntry("456","Entry 1","OPEN") ));
        when(todoEntryRepository.save(    new TodoEntry("456","ABC","DONE") ))
                .thenReturn(              new TodoEntry("456","ABC","DONE") );

        // When
        Optional<TodoEntry> actual = todoEntryService.updateEntry("456", new TodoEntry(null,"ABC","DONE"));

        // Then
        verify(todoEntryRepository).findById("456");
        TodoEntry expected = new TodoEntry("456", "ABC", "DONE");
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void whenDeleteEntry_isCalled() {
        // Given

        // When
        todoEntryService.deleteEntry("456");

        // Then
        verify(todoEntryRepository).deleteById("456");
    }
}