package com.example.backend.changelog;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "change_log")
public record ChangeLogEntryIndex(
        String id,
        int index
) {
}
