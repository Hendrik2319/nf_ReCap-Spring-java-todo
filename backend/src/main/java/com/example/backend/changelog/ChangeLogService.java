package com.example.backend.changelog;

import com.example.backend.TodoEntry;
import com.example.backend.TodoEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChangeLogService {

    private final TodoEntryRepository todoEntryRepository;
    private final ChangeLogRepository changeLogRepository;
    private final ChangeLogEntryIndexRepository changeLogEntryIndexRepository;

    public List<ChangeLogEntry> getAllEntries() {
        return changeLogRepository.findAll();
    }

    public ChangeLogEntry undoLastChange() throws ChangeLogException {
        int index = getChangeLogEntryIndex();
        if (index==0) // no undoable entries in log
            return null;
        if (index<0)
            throw new ChangeLogException("Can't undo last change:" +
                    " Stored log entry index has wrong value");

        index--;
        int finalIndex = index;
        ChangeLogEntry changeLogEntry = changeLogRepository
                .findById(index)
                .orElseThrow(
                        ()->new ChangeLogException("Can't undo last change:" +
                                " Can't find log entry with index %d.",
                                finalIndex)
                );

        TodoEntry prev = changeLogEntry.prevState();
        TodoEntry next = changeLogEntry.nextState();
        checkEntry("Can't undo last change:", changeLogEntry, prev, next);

        if (prev==null)
            todoEntryRepository.deleteById(changeLogEntry.todoId());
        else
            todoEntryRepository.save(prev);

        setChangeLogEntryIndex(index);

        return changeLogEntry;
    }

    public ChangeLogEntry redoUndoneChange() throws ChangeLogException {
        int index = getChangeLogEntryIndex();
        if (index<0)
            throw new ChangeLogException("Can't undo last change:" +
                    " Stored log entry index has wrong value");

	    ChangeLogEntry changeLogEntry = changeLogRepository
                .findById(index)
                .orElseThrow(
                        ()->new ChangeLogException("Can't redo undone change:" +
                                " Can't find log entry with index %d.",
		                        index)
                );

        TodoEntry prev = changeLogEntry.prevState();
        TodoEntry next = changeLogEntry.nextState();
        checkEntry("Can't redo undone change:", changeLogEntry, prev, next);

        if (next==null)
            todoEntryRepository.deleteById(changeLogEntry.todoId());
        else
            todoEntryRepository.save(next);

        setChangeLogEntryIndex(index+1);

        return changeLogEntry;
    }

    private static void checkEntry(String prefix, ChangeLogEntry changeLogEntry, TodoEntry prev, TodoEntry next) throws ChangeLogException
    {
        if (prev == null && next == null)
            throw new ChangeLogException(prefix + " Log entry with index %d has no previous and no next state.",
                    changeLogEntry.index());

        if (prev != null && !Objects.equals(prev.id(), changeLogEntry.todoId()))
            throw new ChangeLogException(prefix + " Log entry with index %d has a previous state with a different id (%d) than expected (%d).",
                    changeLogEntry.index(), prev.id(), changeLogEntry.todoId());

        if (next != null && !Objects.equals(next.id(), changeLogEntry.todoId()))
            throw new ChangeLogException(prefix + " Log entry with index %d has a next state with a different id (%d) than expected (%d).",
                    changeLogEntry.index(), next.id(), changeLogEntry.todoId());
    }

    public void logChange(TodoEntry prev, TodoEntry next) {
        if (prev==null && next==null) return;

        String todoId = prev!=null ? prev.id() : next.id();
        int index = getChangeLogEntryIndex();

        changeLogRepository.deleteAll(changeLogRepository.findAllAboveIndex(index));
        changeLogRepository.save(new ChangeLogEntry(index, todoId, prev, next));

        setChangeLogEntryIndex(index+1);
    }

    private void setChangeLogEntryIndex(int index) {
        List<ChangeLogEntryIndex> indexes = changeLogEntryIndexRepository.findAll();
        ChangeLogEntryIndex storedIndex;
        if (indexes.isEmpty())
            storedIndex = new ChangeLogEntryIndex(null, index);
        else
            storedIndex = indexes.get(0).withIndex(index);
        changeLogEntryIndexRepository.save(storedIndex);
    }

    public int getChangeLogEntryIndex() {
        List<ChangeLogEntryIndex> indexes = changeLogEntryIndexRepository.findAll();
        if (indexes.isEmpty())
            return 0;
        return indexes.get(0).index();
    }
}
