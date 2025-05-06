//package net.rest.journalApp.JournalEntryController.repository;
//
//import net.rest.journalApp.JournalEntryController.entity.JournalEntry;
//import net.rest.journalApp.JournalEntryController.entity.User;
//import org.bson.types.ObjectId;
//import org.springframework.data.mongodb.repository.MongoRepository;
//
//import java.util.Optional;
//import java.util.UUID;
//
//public interface UserRepository extends MongoRepository <User, ObjectId> {
//
//   public User findByUserName(String userName);
//
//
//    Optional<User> findById(java.util.UUID id);
//}
package net.rest.journalApp.JournalEntryController.repository;

import net.rest.journalApp.JournalEntryController.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    User findByUserName(String userName);
}