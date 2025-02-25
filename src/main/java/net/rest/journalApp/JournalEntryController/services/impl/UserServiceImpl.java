package net.rest.journalApp.JournalEntryController.services.impl;

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
public class UserServiceImpl {
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
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole(Arrays.asList("ROLE_USER"));
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

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
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

            // Update the new fields
            if (user.getFirstName() != null) existingUser.setFirstName(user.getFirstName());
            if (user.getLastName() != null) existingUser.setLastName(user.getLastName());
            if (user.getEmail() != null) existingUser.setEmail(user.getEmail());
            if (user.getPhoneNumber() != null) existingUser.setPhoneNumber(user.getPhoneNumber());
            if (user.getAddressLine1() != null) existingUser.setAddressLine1(user.getAddressLine1());
            if (user.getAddressLine2() != null) existingUser.setAddressLine2(user.getAddressLine2());
            if (user.getCity() != null) existingUser.setCity(user.getCity());
            if (user.getState() != null) existingUser.setState(user.getState());
            if (user.getCountry() != null) existingUser.setCountry(user.getCountry());
            if (user.getZipCode() != null) existingUser.setZipCode(user.getZipCode());

            userRepository.save(existingUser);
        }
    }
}