package net.rest.journalApp.JournalEntryController.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private ObjectId id;

    // Ensures 'userName' is unique and indexed
    @Indexed(unique = true)
    @NonNull
    private String userName;

    @NonNull
    private String password;

    // Added new fields
    private String firstName;

    private String lastName;

    @Indexed(unique = true)
    private String email;

    private String phoneNumber;

    // Optional address fields
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    // Defining a list of journal entries, mapped as a reference to another collection
    @DBRef
    private List<JournalEntry> journalEntries = new ArrayList<>();

    // Added for role-based authorization
    @NonNull
    private List<String> role = List.of("ROLE_USER");
}
