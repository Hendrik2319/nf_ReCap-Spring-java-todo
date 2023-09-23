package com.example.backend.changelog;

import com.example.backend.TodoEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeLogRepository extends MongoRepository<ChangeLogEntry, String> {
}
