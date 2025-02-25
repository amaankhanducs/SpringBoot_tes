package net.rest.journalApp.JournalEntryController.controlller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import net.rest.journalApp.JournalEntryController.dto.UserDTO;
import net.rest.journalApp.JournalEntryController.entity.User;
import net.rest.journalApp.JournalEntryController.payload.GenericResponse;
import net.rest.journalApp.JournalEntryController.services.impl.UserServiceImpl;
import net.rest.journalApp.JournalEntryController.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<GenericResponse<Map<String, Object>>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserName(),
                            loginRequest.getPassword()
                    )
            );

            // Generate JWT token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUserName());
            final String token = jwtUtil.generateToken(userDetails);

            // Return token and user info
            User user = userService.findByUserName(loginRequest.getUserName());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userName", user.getUserName());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("email", user.getEmail());

            GenericResponse<Map<String, Object>> genericResponse = new GenericResponse<>(
                    "success",
                    "User authenticated successfully",
                    response
            );

            return ResponseEntity.ok(genericResponse);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<GenericResponse<Map<String, Object>>> registerUser(@Valid @RequestBody UserDTO userDTO) {
        // Check if username already exists
        if (userService.findByUserName(userDTO.getUserName()) != null) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", "Username is already taken!");

            GenericResponse<Map<String, Object>> errorResponse = new GenericResponse<>(
                    "error",
                    "Registration failed",
                    errorData
            );

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Check if email already exists
        if (userService.findByEmail(userDTO.getEmail()) != null) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", "Email is already registered!");

            GenericResponse<Map<String, Object>> errorResponse = new GenericResponse<>(
                    "error",
                    "Registration failed",
                    errorData
            );

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Create user from DTO
        User user = new User();
        user.setUserName(userDTO.getUserName());
        user.setPassword(userDTO.getPassword());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddressLine1(userDTO.getAddressLine1());
        user.setAddressLine2(userDTO.getAddressLine2());
        user.setCity(userDTO.getCity());
        user.setState(userDTO.getState());
        user.setCountry(userDTO.getCountry());
        user.setZipCode(userDTO.getZipCode());

        // Create new user
        userService.saveEntry(user);

        // Prepare response
        Map<String, Object> userData = new HashMap<>();
        userData.put("message", "User registered successfully");
        userData.put("userName", user.getUserName());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("email", user.getEmail());

        GenericResponse<Map<String, Object>> response = new GenericResponse<>(
                "success",
                "Registration successful",
                userData
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Login request class
    public static class LoginRequest {
        @NotBlank(message = "Username cannot be blank")
        private String userName;

        @NotBlank(message = "Password cannot be blank")
        private String password;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}