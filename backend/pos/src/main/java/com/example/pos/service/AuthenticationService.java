package com.example.pos.service;

import com.example.pos.model.Session;
import com.example.pos.model.User;
import com.example.pos.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<Session> authenticate(String username, String password) {
        Optional<User> userOpt = userService.getUserByUsername(username);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPasswordHash())) {
            User user = userOpt.get();
            Session session = new Session();
            session.setUser(user);
            session.setToken(UUID.randomUUID().toString());
            session.setExpiresAt(LocalDateTime.now().plusHours(24));
            session.setCreatedAt(LocalDateTime.now());
            return Optional.of(sessionRepository.save(session));
        }
        return Optional.empty();
    }

    public void logout(String token) {
        sessionRepository.findByToken(token).ifPresent(sessionRepository::delete);
    }

    public Optional<User> getUserByToken(String token) {
        return sessionRepository.findByToken(token)
                .filter(session -> session.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(Session::getUser);
    }
}
