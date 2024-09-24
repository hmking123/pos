package com.example.pos.controller;

import com.example.pos.model.DashboardWidget;
import com.example.pos.model.User;
import com.example.pos.model.UserDashboardPreference;
import com.example.pos.service.AuthenticationService;
import com.example.pos.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/widgets")
    public ResponseEntity<List<DashboardWidget>> getAllWidgets() {
        return ResponseEntity.ok(dashboardService.getAllWidgets());
    }

    @GetMapping("/preferences")
    public ResponseEntity<?> getUserPreferences(@RequestHeader("Authorization") String token) {
        Optional<User> userOptional = authenticationService.getUserByToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return ResponseEntity.ok(dashboardService.getUserDashboardPreferences(user));
        } else {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }

    @PostMapping("/preferences")
    public ResponseEntity<?> saveUserPreference(@RequestHeader("Authorization") String token, @RequestBody UserDashboardPreference preference) {
        Optional<User> userOptional = authenticationService.getUserByToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            preference.setUser(user);
            UserDashboardPreference savedPreference = dashboardService.saveUserDashboardPreference(preference);
            return ResponseEntity.ok(savedPreference);
        } else {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }
}
