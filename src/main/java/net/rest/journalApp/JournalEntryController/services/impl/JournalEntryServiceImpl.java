package net.rest.journalApp.JournalEntryController.services.impl;

import net.rest.journalApp.JournalEntryController.entity.JournalEntry;
import net.rest.journalApp.JournalEntryController.entity.User;
import net.rest.journalApp.JournalEntryController.repository.JournalRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryServiceImpl {

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private JournalEntry journalEntry;

    @Autowired
    private UserServiceImpl userService;

    public void saveEntry(JournalEntry journalEntry, String userName) {
        try {
            System.out.println("inside save entry");
            User user = userService.findByUserName(userName);

            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalRepository.save((journalEntry));
            user.getJournalEntries().add(saved);
            userService.saveEntry(user);
        } catch(Exception e) {
            System.out.println(e);
            throw new RuntimeException("An error occurred while saving the entry", e);
        }
    }

    public void saveEntry(JournalEntry journalEntry) {
        journalRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll() {
        return journalRepository.findAll();
    }

    /**
     * Get journal entries for a specific user with pagination
     * @param userName the username
     * @param pageable pagination information
     * @return a Page of JournalEntry objects
     */
    public Page<JournalEntry> getEntriesByUser(String userName, Pageable pageable) {
        User user = userService.findByUserName(userName);
        if (user == null || user.getJournalEntries() == null || user.getJournalEntries().isEmpty()) {
            return Page.empty(pageable);
        }

        List<JournalEntry> allEntries = user.getJournalEntries();

        // Sort entries according to pageable
        if (pageable.getSort().isSorted()) {
            // We need to implement specific sorting based on the provided Sort
            // This is a simplified approach, you may need to enhance it
            if (pageable.getSort().getOrderFor("date") != null) {
                if (pageable.getSort().getOrderFor("date").isAscending()) {
                    allEntries.sort((e1, e2) -> e1.getDate().compareTo(e2.getDate()));
                } else {
                    allEntries.sort((e1, e2) -> e2.getDate().compareTo(e1.getDate()));
                }
            } else if (pageable.getSort().getOrderFor("title") != null) {
                if (pageable.getSort().getOrderFor("title").isAscending()) {
                    allEntries.sort((e1, e2) -> e1.getTitle().compareTo(e2.getTitle()));
                } else {
                    allEntries.sort((e1, e2) -> e2.getTitle().compareTo(e1.getTitle()));
                }
            }
            // Add more sorting options if needed
        }

        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allEntries.size());

        if (start >= allEntries.size()) {
            return Page.empty(pageable);
        }

        List<JournalEntry> pageContent = allEntries.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allEntries.size());
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        return journalRepository.findById(id);
    }

    public void deleteById(ObjectId id, String userName) {
        User user = userService.findByUserName(userName);
        user.getJournalEntries().removeIf(x -> x.getId().equals(id));
        userService.saveEntry(user);
        journalRepository.deleteById(id);
    }
}