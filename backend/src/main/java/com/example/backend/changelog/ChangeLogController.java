package com.example.backend.changelog;

import com.example.backend.TodoEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/change-log")
@RequiredArgsConstructor
public class ChangeLogController {

    private final ChangeLogService changeLogService;

    @GetMapping("/show")
    public String showChangeLog() {
        return toString(changeLogService.getAllEntries());
    }

    @GetMapping("/index")
    public String showChangeLogIndedx() {
        return "Current log entry index: %d".formatted(changeLogService.getChangeLogEntryIndex());
    }

    @GetMapping("/undo")
    public String undoLastChange() throws ChangeLogException {
        return toString(changeLogService.undoLastChange());
    }

    @GetMapping("/redo")
    public String redoUndoneChange() throws ChangeLogException {
        return toString(changeLogService.redoUndoneChange());
    }

    private String toString(List<ChangeLogEntry> allEntries) {
        StringBuilder sb = new StringBuilder();
        sb.append("<pre>\r\n");
        sb.append("[\r\n");
        for (ChangeLogEntry changeLogEntry : allEntries)
            print(sb, "    ", changeLogEntry);
        sb.append("]\r\n");
        sb.append("</pre>\r\n");
        return sb.toString();
    }

    private String toString(ChangeLogEntry changeLogEntry) {
        StringBuilder sb = new StringBuilder();
        sb.append("<pre>\r\n");
        print(sb, "", changeLogEntry);
        sb.append("</pre>\r\n");
        return sb.toString();
    }

    private void print(StringBuilder sb, String indent, ChangeLogEntry changeLogEntry) {
        if (changeLogEntry ==null)
            sb.append("%s<null>%n".formatted(indent));
        else {
            sb.append("%s{%n".formatted(indent));
            sb.append("%s    index  : %d%n".formatted(indent, changeLogEntry.index()));
            sb.append("%s    todoId : %s%n".formatted(indent, changeLogEntry.todoId()));
            print(sb, indent +"    prev   : ", indent +"    ", changeLogEntry.prevState());
            print(sb, indent +"    next   : ", indent +"    ", changeLogEntry.nextState());
            sb.append("%s}%n".formatted(indent));
        }
    }

    private void print(StringBuilder sb, String prefix, String indent, TodoEntry todoEntry) {
        if (todoEntry==null)
            sb.append(prefix).append("<null>");
        else {
            sb.append(prefix).append("{\r\n");
            sb.append(indent).append("    id          : %s%n"    .formatted(todoEntry.id         ()));
            sb.append(indent).append("    description : \"%s\"%n".formatted(todoEntry.description()));
            sb.append(indent).append("    status      : %s%n"    .formatted(todoEntry.status     ()));
            sb.append(indent).append("}\r\n");
        }
    }

    @ExceptionHandler(ChangeLogException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleChangeLogException(ChangeLogException ex) {
        System.err.printf("ChangeLogException: %s%n", ex.getMessage());
        return "ChangeLogException: %s".formatted(ex.getMessage());
    }
}
