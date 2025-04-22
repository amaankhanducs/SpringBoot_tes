package net.rest.journalApp.JournalEntryController.services;

import net.rest.journalApp.JournalEntryController.entity.User;
import net.rest.journalApp.JournalEntryController.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveEntry(User user) {
        // Encode the password before saving
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Ensure the user has the USER role
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Arrays.asList("ROLE_USER"));
        }

        userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(ObjectId id) {
        return userRepository.findById(id);
    }

    public void deleteById(ObjectId id) {
        userRepository.deleteById(id);
    }

    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    // Method to update user without changing the password
    public void updateUserDetails(User user, String userName) {
        User existingUser = findByUserName(userName);
        if (existingUser != null) {
            existingUser.setUserName(user.getUserName());
            // Only update password if a new one is provided
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userRepository.save(existingUser);
        }
    }
}