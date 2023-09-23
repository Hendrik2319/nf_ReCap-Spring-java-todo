package com.example.backend.changelog;

import com.example.backend.TodoEntry;

public record ChangeLogEntry(
        String id,
        String todoId,
        TodoEntry prevState,
        TodoEntry nextState
) {
}
