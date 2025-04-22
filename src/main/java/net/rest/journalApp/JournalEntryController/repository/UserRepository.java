package net.rest.journalApp.JournalEntryController.repository;

import net.rest.journalApp.JournalEntryController.entity.JournalEntry;
import net.rest.journalApp.JournalEntryController.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository <User, ObjectId> {

   public User findByUserName(String userName);


}
