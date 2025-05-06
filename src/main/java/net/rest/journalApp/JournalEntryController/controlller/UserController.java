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