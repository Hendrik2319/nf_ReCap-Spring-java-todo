package com.example.backend.changelog;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeLogEntryIndexRepository extends MongoRepository<ChangeLogEntryIndex, String> {
}
