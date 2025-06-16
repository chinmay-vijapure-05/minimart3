package com.minimart.controller;

import com.minimart.model.User;
import com.minimart.repository.UserRepository;
import com.minimart.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            System.out.println("Registration attempt for email: " + user.getEmail());
            
            // Validate input
            if (user.getEmail() == null || user.getEmail().trim().isEmpty() || 
                user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email and password are required");
            }
            
            // Check if user with this email already exists
            if (userRepository.findByEmail(user.getEmail()) != null) {
                System.out.println("Registration failed: Email already exists");
                return ResponseEntity.badRequest().body("User with this email already exists");
            }
            
            // Set the username to email if not provided
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                user.setUsername(user.getEmail());
            }
            
            // Encode the password
            String rawPassword = user.getPassword();
            user.setPassword(passwordEncoder.encode(rawPassword));
            
            // Save the user
            User savedUser = userRepository.save(user);
            
            System.out.println("User registered successfully. Password hash: " + savedUser.getPassword());
            System.out.println("Raw password was: " + rawPassword);
            
            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("email", savedUser.getEmail());
            response.put("username", savedUser.getUsername());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred during registration");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String password = body.get("password");
            
            if (email == null || password == null) {
                return ResponseEntity.badRequest().body("Email and password are required");
            }
            
            System.out.println("Login attempt for email: " + email);
            
            User user = userRepository.findByEmail(email);
            if (user == null) {
                System.out.println("No user found with email: " + email);
                return ResponseEntity.status(401).body("Invalid credentials");
            }
            
            System.out.println("User found. Stored password hash: " + user.getPassword());
            
            // Check if the provided password is already hashed (starts with $2a$)
            boolean isAlreadyHashed = password.startsWith("$2a$");
            boolean passwordMatches;
            
            if (isAlreadyHashed) {
                // If password is already hashed, compare directly
                passwordMatches = password.equals(user.getPassword());
                System.out.println("Comparing hashed passwords directly");
            } else {
                // If password is plain text, use the password encoder
                passwordMatches = passwordEncoder.matches(password, user.getPassword());
                System.out.println("Using password encoder to match plain text password");
            }
            
            System.out.println("Password matches: " + passwordMatches);
            
            if (passwordMatches) {
                // Create a simple UserDetails object with just the email
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password("")
                    .authorities("ROLE_USER")
                    .build();
                
                String token = jwtUtil.generateToken(userDetails);
                System.out.println("Generated token: " + token);
                
                // Return user info along with the token
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("email", user.getEmail());
                response.put("username", user.getUsername());
                response.put("name", user.getName());
                
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.status(401).body("Invalid credentials");
            
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred during login");
        }
    }
}
