package net.rest.journalApp.JournalEntryController.controlller;

import jakarta.validation.Valid;
import net.rest.journalApp.JournalEntryController.dto.SetRolesDTO;
import net.rest.journalApp.JournalEntryController.dto.UserDTO;
import net.rest.journalApp.JournalEntryController.entity.User;
import net.rest.journalApp.JournalEntryController.exception.ResourceNotFoundException;
import net.rest.journalApp.JournalEntryController.payload.GenericResponse;
import net.rest.journalApp.JournalEntryController.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse<?>> getAllUsers() {
        List<User> users = userService.getAll();
        if (users.isEmpty()) {
            GenericResponse<String> response = new GenericResponse<>(
                    "error",
                    "No users found",
                    "No users found in the database"
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Create a clean response without sensitive information
        List<Map<String, Object>> cleanUsers = users.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    // Convert ObjectId to String properly
                    userMap.put("id", user.getId() != null ? user.getId().toString() : null);
                    userMap.put("userName", user.getUserName());
                    userMap.put("firstName", user.getFirstName());
                    userMap.put("lastName", user.getLastName());
                    userMap.put("email", user.getEmail());
                    userMap.put("phoneNumber", user.getPhoneNumber());
                    userMap.put("role", user.getRole());
                    return userMap;
                })
                .collect(Collectors.toList());

        GenericResponse<List<Map<String, Object>>> response = new GenericResponse<>(
                "success",
                "Users retrieved successfully",
                cleanUsers
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<GenericResponse<Map<String, Object>>> getUserProfile() {
        // Get the authenticated username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User user = userService.findByUserName(userName);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with username: " + userName);
        }

        // Create user profile data excluding sensitive information
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("userName", user.getUserName());
        profileData.put("firstName", user.getFirstName());
        profileData.put("lastName", user.getLastName());
        profileData.put("email", user.getEmail());
        profileData.put("phoneNumber", user.getPhoneNumber());
        profileData.put("addressLine1", user.getAddressLine1());
        profileData.put("addressLine2", user.getAddressLine2());
        profileData.put("city", user.getCity());
        profileData.put("state", user.getState());
        profileData.put("country", user.getCountry());
        profileData.put("zipCode", user.getZipCode());
        profileData.put("role", user.getRole());

        GenericResponse<Map<String, Object>> response = new GenericResponse<>(
                "success",
                "User profile retrieved successfully",
                profileData
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<GenericResponse<Map<String, Object>>> updateUserProfile(
            @Valid @RequestBody UserDTO userDTO) {
        // Get the authenticated username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User user = userService.findByUserName(userName);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with username: " + userName);
        }

        // Update user with new information
        if (userDTO.getFirstName() != null) user.setFirstName(userDTO.getFirstName());
        if (userDTO.getLastName() != null) user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if (userDTO.getPhoneNumber() != null) user.setPhoneNumber(userDTO.getPhoneNumber());
        if (userDTO.getAddressLine1() != null) user.setAddressLine1(userDTO.getAddressLine1());
        if (userDTO.getAddressLine2() != null) user.setAddressLine2(userDTO.getAddressLine2());
        if (userDTO.getCity() != null) user.setCity(userDTO.getCity());
        if (userDTO.getState() != null) user.setState(userDTO.getState());
        if (userDTO.getCountry() != null) user.setCountry(userDTO.getCountry());
        if (userDTO.getZipCode() != null) user.setZipCode(userDTO.getZipCode());
        if (userDTO.getPassword() != null) user.setPassword(userDTO.getPassword());

        userService.saveEntry(user);

        // Prepare response with updated profile
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("userName", user.getUserName());
        responseData.put("firstName", user.getFirstName());
        responseData.put("lastName", user.getLastName());
        responseData.put("email", user.getEmail());
        responseData.put("phoneNumber", user.getPhoneNumber());
        responseData.put("addressLine1", user.getAddressLine1());
        responseData.put("addressLine2", user.getAddressLine2());
        responseData.put("city", user.getCity());
        responseData.put("state", user.getState());
        responseData.put("country", user.getCountry());
        responseData.put("zipCode", user.getZipCode());

        GenericResponse<Map<String, Object>> response = new GenericResponse<>(
                "success",
                "User profile updated successfully",
                responseData
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")  // Only ADMIN can access this endpoint
    public ResponseEntity<GenericResponse<Map<String, Object>>> setUserRoles(@Valid @RequestBody SetRolesDTO setRolesDTO) {
        User user = userService.findByUserName(setRolesDTO.getUserName());

        if (user == null) {
            GenericResponse<Map<String, Object>> errorResponse = new GenericResponse<>(
                    "error",
                    "User not found",
                    Map.of("error", "User not found with username: " + setRolesDTO.getUserName())
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        List<String> roles = setRolesDTO.getRole();
        if (roles == null || roles.isEmpty()) {
            GenericResponse<Map<String, Object>> errorResponse = new GenericResponse<>(
                    "error",
                    "Invalid roles",
                    Map.of("error", "Roles cannot be empty")
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Validate roles format (optional)
        for (String role : roles) {
            if (!role.startsWith("ROLE_")) {
                roles.set(roles.indexOf(role), "ROLE_" + role);
            }
        }

        user.setRole(roles);
        userService.saveEntry(user);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("message", "Roles updated successfully");
        responseData.put("userName", user.getUserName());
        responseData.put("roles", user.getRole());

        GenericResponse<Map<String, Object>> response = new GenericResponse<>(
                "success",
                "Roles updated successfully",
                responseData
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}