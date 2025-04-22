package net.rest.journalApp.JournalEntryController.services;

import net.rest.journalApp.JournalEntryController.entity.JournalEntry;
import net.rest.journalApp.JournalEntryController.entity.User;
import net.rest.journalApp.JournalEntryController.repository.JournalRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {


    @Autowired
    private JournalRepository journalRepository;

    @Autowired JournalEntry journalEntry;


    @Autowired
    private UserService userService;


    @Transactional
    public void saveEntry(JournalEntry journalEntry, String userName) {
        try
        {
            System.out.println("inside save entry");
            User user = userService.findByUserName(userName);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalRepository.save((journalEntry));
            user.getJournalEntries().add(saved);
//            user.setUserName(null);
            userService.saveEntry(user);
        }catch(Exception e){
            System.out.println(e);
            throw new RuntimeException("An error occurred while saving the entry",e);
        }

    }
    public void saveEntry(JournalEntry journalEntry) {
        journalRepository.save(journalEntry);
    }


    public List<JournalEntry>  getAll() {
        return  journalRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        return journalRepository.findById(id);
    }

    public void deleteById(ObjectId id, String userName) {
        User user=userService.findByUserName(userName);
        user.getJournalEntries().removeIf(x -> x.getId().equals(id));
        userService.saveEntry(user);
        journalRepository.deleteById(id);
    }

    }

