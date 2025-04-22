//package net.rest.journalApp.JournalEntryController.entity;
//
//import org.bson.types.ObjectId;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.index.Indexed;
//import org.springframework.data.mongodb.core.mapping.DBRef;
//import org.springframework.data.mongodb.core.mapping.Document;
//import lombok.Data;
//import lombok.NonNull;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Data
//@Document(collection = "users")
//public class User {
//
//    @Id
//    private ObjectId id;
//
//    // Ensures 'userName' is unique and indexed
//    @Indexed(unique = true)
//    @NonNull
//    private String userName;
//
//    @NonNull
//    private String password;
//
//    // Added for role-based authorization
//    private List<String> roles = new ArrayList<>();
//
//    // Defining a list of journal entries, mapped as a reference to another collection
//    @DBRef
//    private List<JournalEntry> journalEntries = new ArrayList<>();
//}
// package net.rest.journalApp.JournalEntryController.entity;

//import org.bson.types.ObjectId;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.index.Indexed;
//import org.springframework.data.mongodb.core.mapping.DBRef;
//import org.springframework.data.mongodb.core.mapping.Document;
//import lombok.Data;
//import lombok.NonNull;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Data
//@Document(collection = "users")
//public class User {
//
//    @Id
//    private ObjectId id;
//
//    // Ensures 'userName' is unique and indexed
//    @Indexed(unique = true)
//    @NonNull
//    private String userName;
//
//    @NonNull
//    private String password;
//
//    // Added for role-based authorization
//    private List<String> roles = new ArrayList<>();
//
//    // Defining a list of journal entries, mapped as a reference to another collection
//    @DBRef
//    private List<JournalEntry> journalEntries = new ArrayList<>();
//}
package net.rest.journalApp.JournalEntryController.entity;

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

    // Added for role-based authorization
    private List<String> roles = new ArrayList<>();

    // Defining a list of journal entries, mapped as a reference to another collection
    @DBRef
    private List<JournalEntry> journalEntries = new ArrayList<>();
}