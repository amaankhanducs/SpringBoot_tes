package net.rest.journalApp.JournalEntryController.repository;

import net.rest.journalApp.JournalEntryController.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    User findByUserName(String userName);
    User findByEmail(String email);
}