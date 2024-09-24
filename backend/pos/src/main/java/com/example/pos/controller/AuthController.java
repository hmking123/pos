package com.example.pos.controller;

import com.example.pos.model.Session;
import com.example.pos.model.User;
import com.example.pos.service.AuthenticationService;
import com.example.pos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        Optional<Session> sessionOpt = authenticationService.authenticate(username, password);
        return sessionOpt
                .map(session -> ResponseEntity.ok().body(session.getToken()))
                .orElse(ResponseEntity.badRequest().body("Invalid credentials"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        authenticationService.logout(token);
        return ResponseEntity.ok().body("Logged out successfully");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }
}
