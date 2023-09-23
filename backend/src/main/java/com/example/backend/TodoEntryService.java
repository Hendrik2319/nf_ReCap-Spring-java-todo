package com.example.backend;

import com.example.backend.changelog.ChangeLogService;
import com.example.backend.chatgpt.ChatGptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoEntryService {

    private final TodoEntryRepository todoEntryRepository;
    private final ChatGptService chatGptService;
    private final ChangeLogService changeLogService;

    public List<TodoEntry> getAllEntries() {
        return todoEntryRepository.findAll();
    }

    public TodoEntry createEntry(NewTodoEntry newTodoEntry) {
        String prompt = "Bitte folgenden Text in Deutsch auf Rechtschreibung und Grammatik prüfen" +
                " und die korrigierte Fassung als Antwort ohne zusätzlichen Text zurückgeben: " +
                newTodoEntry.description();

        String fixedDescription = chatGptService.askChatGPT(prompt);
        newTodoEntry = newTodoEntry.withDescription(fixedDescription);

        TodoEntry saved = todoEntryRepository.save(new TodoEntry(newTodoEntry));
        changeLogService.logChange(null, saved);
        return saved;
    }

    public Optional<TodoEntry> getEntry(String id) {
        return todoEntryRepository.findById(id);
    }

    public Optional<TodoEntry> updateEntry(String id, TodoEntry todoEntry) {
        Optional<TodoEntry> savedEntryOpt = todoEntryRepository.findById(id);

        if (savedEntryOpt.isPresent()) {
            TodoEntry savedEntry = savedEntryOpt.get();
            TodoEntry prev = savedEntry;
            if (todoEntry.description()!=null) savedEntry = savedEntry.withDescription(todoEntry.description());
            if (todoEntry.status()     !=null) savedEntry = savedEntry.withStatus     (todoEntry.status     ());
            savedEntry = todoEntryRepository.save(savedEntry);
            changeLogService.logChange(prev, savedEntry);
            savedEntryOpt = Optional.of(savedEntry);
        }

        return savedEntryOpt;
    }

    public void deleteEntry(String id) {
        TodoEntry prev = todoEntryRepository.findById(id).orElse(null);
        todoEntryRepository.deleteById(id);
        changeLogService.logChange(prev, null);
    }

}
