package com.example.pos.controller;

import com.example.pos.model.User;
import com.example.pos.service.AuthenticationService;
import com.example.pos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        Optional<User> userOptional = authenticationService.getUserByToken(token);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@RequestHeader("Authorization") String token, @RequestBody User updatedUser) {
        Optional<User> userOptional = authenticationService.getUserByToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setEmail(updatedUser.getEmail());
            User savedUser = userService.createUser(user);
            return ResponseEntity.ok(savedUser);
        } else {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }
}
