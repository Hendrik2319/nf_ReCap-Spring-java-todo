package com.example.backend;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoEntryService {

    private final TodoEntryRepository todoEntryRepository;

    public List<TodoEntry> getAllEntries() {
        return todoEntryRepository.findAll();
    }

    public TodoEntry createEntry(NewTodoEntry newTodoEntry) {
        return todoEntryRepository.save(new TodoEntry(newTodoEntry));
    }

    public Optional<TodoEntry> getEntry(String id) {
        return todoEntryRepository.findById(id);
    }

    public Optional<TodoEntry> updateEntry(String id, TodoEntry todoEntry) {
        Optional<TodoEntry> savedEntryOpt = todoEntryRepository.findById(id);

        if (savedEntryOpt.isPresent()) {
            TodoEntry savedEntry = savedEntryOpt.get();
            if (todoEntry.description()!=null) savedEntry = savedEntry.withDescription(todoEntry.description());
            if (todoEntry.status()     !=null) savedEntry = savedEntry.withStatus     (todoEntry.status     ());
            savedEntryOpt = Optional.of(todoEntryRepository.save(savedEntry));
        }

        return savedEntryOpt;
    }

    public void deleteEntry(String id) {
        todoEntryRepository.deleteById(id);
    }

}
