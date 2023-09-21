package com.example.backend;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoEntryController {

    private final TodoEntryRepository todoEntryRepository;

    @GetMapping
    List<TodoEntry> getAllEntries() {
        return todoEntryRepository.findAll();
    }

    @PostMapping
    TodoEntry createEntry(@RequestBody NewTodoEntry newTodoEntry) {
        return todoEntryRepository.save(new TodoEntry(newTodoEntry));
    }

    @GetMapping("/{id}")
    ResponseEntity<TodoEntry> getEntry(@PathVariable String id) {
        return ResponseEntity.of(todoEntryRepository.findById(id));
    }

    @PutMapping("/{id}")
    ResponseEntity<TodoEntry> updateEntry(@PathVariable String id, @RequestBody TodoEntry todoEntry) {
        if (!id.equals(todoEntry.id()))
            return ResponseEntity.badRequest().build();

        Optional<TodoEntry> savedEntryOpt = todoEntryRepository.findById(id);
        if (savedEntryOpt.isPresent()) {
            TodoEntry savedEntry = savedEntryOpt.get()
                    .withDescription(todoEntry.description())
                    .withStatus(todoEntry.status());
            savedEntryOpt = Optional.of(todoEntryRepository.save(savedEntry));
        }

        return ResponseEntity.of(savedEntryOpt);
    }

    @DeleteMapping("/{id}")
    void deleteEntry(@PathVariable String id) {
        todoEntryRepository.deleteById(id);
    }

}
