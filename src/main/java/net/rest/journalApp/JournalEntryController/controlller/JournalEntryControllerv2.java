package net.rest.journalApp.JournalEntryController.controlller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import net.rest.journalApp.JournalEntryController.entity.JournalEntry;
import net.rest.journalApp.JournalEntryController.entity.User;
import net.rest.journalApp.JournalEntryController.services.JournalEntryService;
import net.rest.journalApp.JournalEntryController.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.jar.JarEntry;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerv2 {

    @Autowired
    private UserService userService;



    @Autowired
    private JournalEntryService journalEntryService;
    @GetMapping()
    public String printCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return "No cookies found!";
        }

        StringBuilder cookieString = new StringBuilder("Cookies:\n");
        for (Cookie cookie : cookies) {
            cookieString.append(cookie.getName())
                    .append(" = ")
                    .append(cookie.getValue())
                    .append("\n");
        }
        return cookieString.toString();
    }

    @GetMapping("{userName}")
    public ResponseEntity<?> getAllJournalEntriesOfUser(@PathVariable String userName) {
       try {
            User user = userService.findByUserName(userName);
           System.out.println();
            List<JournalEntry> all = user.getJournalEntries();
            if (all != null && !all.isEmpty()) {
                return new ResponseEntity<>(all, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(Exception e) {
           System.out.println("Error during get journal " + e);

        }
        return null;
    }

    @PostMapping("{userName}")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry,@PathVariable String userName){
//        System.out.println("My entry" + myEntry);


        try {
          System.out.println("user name" + userName);
          journalEntryService.saveEntry(myEntry,userName);
          return new ResponseEntity<>(myEntry, HttpStatus.CREATED);


      } catch (Exception e) {
          return new ResponseEntity<>(myEntry, HttpStatus.BAD_REQUEST);
      }

    }
    @GetMapping("id/{myId}")
    public ResponseEntity<JournalEntry>getJournalEntryById(@PathVariable ObjectId myId) {
      Optional<JournalEntry> journalEntry= journalEntryService.findById(myId);
      if(journalEntry.isPresent()) {
          return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
      }
      return new ResponseEntity<>( HttpStatus.NOT_FOUND);
    }
    @DeleteMapping ("id/{userName}/{myId}")
    public  ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId myId,@PathVariable String userName) {
        journalEntryService.deleteById(myId,userName);
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("id/{userName}/{myId}")
    public ResponseEntity<?> updateJournalById(@PathVariable ObjectId myId,
                                               @RequestBody JournalEntry newEntry,
                                               @PathVariable String userName) {

        JournalEntry old= journalEntryService.findById(myId).orElse(null);
        if(old!=null) {
            old.setTitle(newEntry.getTitle()!=null && !newEntry.getTitle().equals("")? newEntry.getTitle() : old.getTitle());
            old.setContent(newEntry.getContent()!=null && !newEntry.getContent().equals("")? newEntry.getContent(): old.getContent());
            journalEntryService.saveEntry(old);
            return  new ResponseEntity<>(old,HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}

