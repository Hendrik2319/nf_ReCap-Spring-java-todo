package com.example.backend;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoEntryController {

    private final TodoEntryService todoEntryRepository;

    @GetMapping
    List<TodoEntry> getAllEntries() {
        return todoEntryRepository.getAllEntries();
    }

    @PostMapping
    TodoEntry createEntry(@RequestBody NewTodoEntry newTodoEntry) {
        return todoEntryRepository.createEntry(newTodoEntry);
    }

    @GetMapping("/{id}")
    ResponseEntity<TodoEntry> getEntry(@PathVariable String id) {
        return ResponseEntity.of(todoEntryRepository.getEntry(id));
    }

    @PutMapping("/{id}")
    ResponseEntity<TodoEntry> updateEntry(@PathVariable String id, @RequestBody TodoEntry todoEntry) {
        if (!id.equals(todoEntry.id()))
            return ResponseEntity.badRequest().build();

        return ResponseEntity.of(todoEntryRepository.updateEntry(id,todoEntry));
    }

    @DeleteMapping("/{id}")
    void deleteEntry(@PathVariable String id) {
        todoEntryRepository.deleteEntry(id);
    }

}
