package com.example.retix.controller;

import com.example.retix.model.User;
import com.example.retix.model.UserDTO;
import com.example.retix.model.Role;
import com.example.retix.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
        try {
            if (userDTO.getRole() == null || userDTO.getRole().isEmpty()) {
                logger.error("Role is missing in userDTO");
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            Role role;
            try {
                role = Role.valueOf(userDTO.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid role value: {}", userDTO.getRole());
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            User user = new User();
            user.setName(userDTO.getName());
            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword());
            user.setRole(role);
            User createdUser = userService.createUser(user);
            logger.info("User created successfully: {}", createdUser.getEmail());
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            logger.error("Duplicate email or constraint violation: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (Exception e) {

            logger.error("Error creating user", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        logger.info("GET request received for user by email: {}", email);
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

   // Delete user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error deleting user with id " + id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update user by ID
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            Role role;
            try {
                role = Role.valueOf(userDTO.getRole().toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            User user = new User();
            user.setName(userDTO.getName());
            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword());
            user.setRole(role);
            Optional<User> updatedUser = userService.updateUser(id, user);
            return updatedUser.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                              .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            logger.error("Error updating user with id " + id, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    

    @GetMapping("/test")
    public ResponseEntity<String> testGet() {
        logger.info("GET request received for test endpoint");
        return new ResponseEntity<>("Test GET endpoint is working", HttpStatus.OK);
    }
}
