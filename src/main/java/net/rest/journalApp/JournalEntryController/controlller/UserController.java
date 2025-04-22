package net.rest.journalApp.JournalEntryController.controlller;

import net.rest.journalApp.JournalEntryController.entity.User;
import net.rest.journalApp.JournalEntryController.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
     @Autowired
     private UserService userService;

     @GetMapping
     public List<User> getAllUsers() {
         return  userService.getAll();
     }
     @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user){

         userService.saveEntry(user);
         return new ResponseEntity<>("User added successfully", HttpStatus.CREATED);

     }

     @PutMapping("/{userName}")


         public ResponseEntity<?> updateUser (@RequestBody User user,@PathVariable String userName) {
         User userInDb = userService.findByUserName ( userName);
         if(userInDb!=null) {
             userInDb.setUserName(user.getUserName());
             userInDb.setPassword(user.getPassword());
             userService.saveEntry(userInDb);
         }
         return new ResponseEntity<>((HttpStatus.OK));
     }
}
