package com.example.backend.changelog;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/change-log")
@RequiredArgsConstructor
public class ChangeLogController {

    private final ChangeLogService changeLogService;

    @GetMapping("/show")
    List<ChangeLogEntry> showChangeLog() {
        return changeLogService.getAllEntries();
    }

    @GetMapping("/undo")
    ChangeLogEntry undoLastChange() {
        return changeLogService.undoLastChange();
    }

    @GetMapping("/redo")
    ChangeLogEntry redoUndoneChange() {
        return changeLogService.redoUndoneChange();
    }
}
