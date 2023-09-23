package com.example.backend.changelog;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/change-log")
@RequiredArgsConstructor
public class ChangeLogController {

    @GetMapping("/show")
    String showChangeLog() {

        return "----";
    }

    @GetMapping("/undo")
    String undoLastChange() {

        return "----";
    }

    @GetMapping("/redo")
    String redoUndoneChange() {

        return "----";
    }
}
