package com.example.backend.changelog;

import com.example.backend.TodoEntry;
import com.example.backend.TodoEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChangeLogService {

    private final TodoEntryRepository todoEntryRepository;
    private final ChangeLogRepository changeLogRepository;
    private final ChangeLogEntryIndexRepository changeLogEntryIndexRepository;

    public List<ChangeLogEntry> getAllEntries() {
        return changeLogRepository.findAll();
    }

    public ChangeLogEntry undoLastChange() {
        // TODO
        return null;
    }

    public ChangeLogEntry redoUndoneChange() {
        // TODO
        return null;
    }

    public void logChange(TodoEntry prev, TodoEntry next) {
        // TODO
    }
}
