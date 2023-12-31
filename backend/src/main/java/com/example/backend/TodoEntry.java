package com.example.backend;

import lombok.With;

@With
public record TodoEntry(String id, String description, TodoEntryStatus status) {
    public TodoEntry(NewTodoEntry newTodoEntry) {
        this(null, newTodoEntry.description(), newTodoEntry.status());
    }
}
