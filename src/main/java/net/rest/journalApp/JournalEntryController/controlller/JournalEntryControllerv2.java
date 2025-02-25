package net.rest.journalApp.JournalEntryController.controlller;

import jakarta.validation.Valid;
import net.rest.journalApp.JournalEntryController.dto.JournalEntryDTO;
import net.rest.journalApp.JournalEntryController.entity.JournalEntry;
import net.rest.journalApp.JournalEntryController.entity.User;
import net.rest.journalApp.JournalEntryController.exception.ResourceNotFoundException;
import net.rest.journalApp.JournalEntryController.payload.GenericResponse;
import net.rest.journalApp.JournalEntryController.services.impl.JournalEntryServiceImpl;
import net.rest.journalApp.JournalEntryController.services.impl.UserServiceImpl;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerv2 {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JournalEntryServiceImpl journalEntryService;

    @GetMapping()
    public ResponseEntity<GenericResponse<?>> getAllJournalEntriesOfUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with username: " + userName);
        }

        // Create pagination request
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        // Get paginated entries
        Page<JournalEntry> journalEntryPage = journalEntryService.getEntriesByUser(
                userName, pageable);

        if (journalEntryPage.isEmpty()) {
            GenericResponse<String> response = new GenericResponse<>(
                    "error",
                    "No journal entries found",
                    "No journal entries found for user: " + userName
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Create a clean response without timestamps
        List<Map<String, Object>> cleanEntries = journalEntryPage.getContent().stream()
                .map(entry -> {
                    Map<String, Object> entryMap = new HashMap<>();
                    entryMap.put("id", entry.getId().toString());
                    entryMap.put("title", entry.getTitle());
                    entryMap.put("content", entry.getContent());
                    return entryMap;
                })
                .toList();

        // Add pagination info
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("entries", cleanEntries);
        responseData.put("currentPage", journalEntryPage.getNumber());
        responseData.put("totalItems", journalEntryPage.getTotalElements());
        responseData.put("totalPages", journalEntryPage.getTotalPages());

        GenericResponse<Map<String, Object>> response = new GenericResponse<>(
                "success",
                "Journal entries retrieved successfully",
                responseData
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<GenericResponse<?>> createEntry(@Valid @RequestBody JournalEntryDTO journalEntryDTO) {
        try {
            // Get the authenticated username from the security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            User user = userService.findByUserName(userName);
            if (user == null) {
                throw new ResourceNotFoundException("User not found with username: " + userName);
            }

            JournalEntry journalEntry = new JournalEntry();
            journalEntry.setTitle(journalEntryDTO.getTitle());
            journalEntry.setContent(journalEntryDTO.getContent());
            journalEntry.setDate(LocalDateTime.now());

            journalEntryService.saveEntry(journalEntry, userName);


            Map<String, Object> responseData = new HashMap<>();
            responseData.put("id", journalEntry.getId().toString());
            responseData.put("title", journalEntry.getTitle());
            responseData.put("content", journalEntry.getContent());

            GenericResponse<Map<String, Object>> response = new GenericResponse<>(
                    "success",
                    "Journal entry created successfully",
                    responseData
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            Map<String, String> errorDetails = new HashMap<>();
            errorDetails.put("error", e.getMessage());

            GenericResponse<Map<String, String>> errorResponse = new GenericResponse<>(
                    "error",
                    "Failed to create journal entry",
                    errorDetails
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("id/{myId}")
    public ResponseEntity<GenericResponse<Map<String, Object>>> getJournalEntryById(@PathVariable String myId) {
        try {
            ObjectId objectId = new ObjectId(myId);
            Optional<JournalEntry> journalEntry = journalEntryService.findById(objectId);
            if (journalEntry.isPresent()) {
                JournalEntry entry = journalEntry.get();

                // Create a clean response without timestamp
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("id", entry.getId().toString());
                responseData.put("title", entry.getTitle());
                responseData.put("content", entry.getContent());

                GenericResponse<Map<String, Object>> response = new GenericResponse<>(
                        "success",
                        "Journal entry retrieved successfully",
                        responseData
                );
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            throw new ResourceNotFoundException("Journal entry not found with id: " + myId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid journal entry ID format");
        }
    }

    @DeleteMapping("id/{myId}")
    public ResponseEntity<GenericResponse<Map<String, String>>> deleteJournalEntryById(@PathVariable String myId) {
        try {
            // Get the authenticated username
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            ObjectId objectId = new ObjectId(myId);
            User user = userService.findByUserName(userName);
            if (user == null) {
                throw new ResourceNotFoundException("User not found with username: " + userName);
            }

            Optional<JournalEntry> journalEntry = journalEntryService.findById(objectId);
            if (journalEntry.isEmpty()) {
                throw new ResourceNotFoundException("Journal entry not found with id: " + myId);
            }

            journalEntryService.deleteById(objectId, userName);

            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("message", "Journal entry deleted successfully");

            GenericResponse<Map<String, String>> response = new GenericResponse<>(
                    "success",
                    "Journal entry deleted",
                    messageMap
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid journal entry ID format");
        }
    }

    @PutMapping("id/{myId}")
    public ResponseEntity<GenericResponse<Map<String, Object>>> updateJournalById(
            @PathVariable String myId,
            @Valid @RequestBody JournalEntryDTO journalEntryDTO) {
        try {
            // Get the authenticated username
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            ObjectId objectId = new ObjectId(myId);
            User user = userService.findByUserName(userName);
            if (user == null) {
                throw new ResourceNotFoundException("User not found with username: " + userName);
            }

            Optional<JournalEntry> optional = journalEntryService.findById(objectId);
            if (optional.isEmpty()) {
                throw new ResourceNotFoundException("Journal entry not found with id: " + myId);
            }

            JournalEntry old = optional.get();
            old.setTitle(journalEntryDTO.getTitle());
            old.setContent(journalEntryDTO.getContent());
            journalEntryService.saveEntry(old);

            // Create a clean response without timestamp
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("id", old.getId().toString());
            responseData.put("title", old.getTitle());
            responseData.put("content", old.getContent());

            GenericResponse<Map<String, Object>> response = new GenericResponse<>(
                    "success",
                    "Journal entry updated successfully",
                    responseData
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid journal entry ID format");
        }
    }
}