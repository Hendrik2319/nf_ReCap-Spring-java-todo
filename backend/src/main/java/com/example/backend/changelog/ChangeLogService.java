package com.example.backend.changelog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChangeLogService {

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
}
