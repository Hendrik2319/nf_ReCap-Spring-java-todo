package com.example.backend.changelog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChangeLogEntryIndexService {

	private final ChangeLogEntryIndexRepository changeLogEntryIndexRepository;

	void setIndex(int index) {
		List<ChangeLogEntryIndex> indexes = changeLogEntryIndexRepository.findAll();
		ChangeLogEntryIndex storedIndex;
		if (indexes.isEmpty())
			storedIndex = new ChangeLogEntryIndex(null, index);
		else
			storedIndex = indexes.get(0).withIndex(index);
		changeLogEntryIndexRepository.save(storedIndex);
	}

	int getIndex() {
		List<ChangeLogEntryIndex> indexes = changeLogEntryIndexRepository.findAll();
		if (indexes.isEmpty())
			return 0;
		return indexes.get(0).index();
	}
}
