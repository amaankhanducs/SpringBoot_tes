package net.rest.journalApp.JournalEntryController.repository;

import net.rest.journalApp.JournalEntryController.entity.JournalEntry;
import org.bson.types.ObjectId;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JournalRepository extends MongoRepository <JournalEntry, ObjectId> {



}
