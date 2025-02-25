package net.rest.journalApp.JournalEntryController.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.management.ObjectName;
import java.time.LocalDateTime;
import java.util.Date;
@Data
@Document
        (collection = "journal_entries")
public class JournalEntry {


    @Id
    private ObjectId id;


    private String title;


    private String content;

    private LocalDateTime date;

    public LocalDateTime getDate() {
        return date;
    }


}
