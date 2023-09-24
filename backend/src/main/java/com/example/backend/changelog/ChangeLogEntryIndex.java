package com.example.backend.changelog;

import lombok.With;
import org.springframework.data.mongodb.core.mapping.Document;

@With
@Document(collection = "change_log_index")
public record ChangeLogEntryIndex(
        String id,
        int index
) {
}
