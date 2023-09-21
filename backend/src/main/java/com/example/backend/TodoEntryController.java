package com.example.backend;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
public class TodoEntryController {

    @GetMapping
    List<TodoEntry> getAllEntries() {
        return List.of(
                new TodoEntry(1, "1dsfgaerstrdetrwezertw", "sadfdfsgfgd"),
                new TodoEntry(2, "2dsfgaerstrdetrwezertw", "OPEN"),
                new TodoEntry(3, "3dsfgaerstrdetrwezertw", "IN_PROGRESS"),
                new TodoEntry(4, "4dsfgaerstrdetrwezertw", "CLOSED"),
                new TodoEntry(5, "5dsfgaerstrdetrwezertw", "DONE")
        );
    }

    @GetMapping("/{id}")
    TodoEntry getEntries(@PathVariable int id) {
        return new TodoEntry(1, "dsfgaerstrdetrwezertw", "sdfsfgfgfg");
    }

    @PostMapping
    void createEntry(@RequestBody NewTodoEntry todoEntry) {
        //return new TodoEntry("mir wurscht", "OPEN");
    }

}
