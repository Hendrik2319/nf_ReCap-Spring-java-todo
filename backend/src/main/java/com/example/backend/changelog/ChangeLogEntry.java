package com.example.backend.changelog;

import com.example.backend.TodoEntry;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "change_log")
public record ChangeLogEntry(
        String id,
        int index,
        String todoId,
        TodoEntry prevState,
        TodoEntry nextState
) {
}
