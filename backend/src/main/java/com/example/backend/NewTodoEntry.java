package com.example.backend;

import lombok.With;

@With
public record NewTodoEntry(String description, TodoEntryStatus status) {
}
