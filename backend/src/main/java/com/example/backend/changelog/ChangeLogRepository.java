package com.example.backend.changelog;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangeLogRepository extends MongoRepository<ChangeLogEntry, Integer> {

	@Query("{ 'index': { '$gt': ?0 } }")
	List<ChangeLogEntry> findAllAboveIndex(int index);

}
