package net.rest.journalApp.JournalEntryController.services;

import net.rest.journalApp.JournalEntryController.entity.JournalEntry;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface JournalEntryService {

    void saveEntry(JournalEntry journalEntry, String userName);

    void saveEntry(JournalEntry journalEntry);

    List<JournalEntry> getAll();

    Optional<JournalEntry> findById(ObjectId id);

    void deleteById(ObjectId id, String userName);



}
