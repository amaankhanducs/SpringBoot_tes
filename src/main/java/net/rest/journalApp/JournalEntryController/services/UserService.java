package net.rest.journalApp.JournalEntryController.services;

import net.rest.journalApp.JournalEntryController.entity.User;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void saveEntry(User user);

    List<User> getAll();

    Optional<User> findById(ObjectId id);

    void deleteById(ObjectId id);

    User findByUserName(String userName);

    void updateUserDetails(User user, String userName);
}
