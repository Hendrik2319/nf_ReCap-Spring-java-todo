package com.example.backend.changelog;

import com.example.backend.TodoEntry;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "change_log")
public record ChangeLogEntry(
		@Id int index,
        String todoId,
        TodoEntry prevState,
        TodoEntry nextState
) {
}
