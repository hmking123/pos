package com.example.pos.service;

import com.example.pos.model.Session;
import com.example.pos.model.User;
import com.example.pos.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticate_success() {
        String username = "testuser";
        String password = "password";
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash("encodedPassword");

        when(userService.getUserByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);
        when(sessionRepository.save(any())).thenReturn(new Session());

        Optional<Session> result = authenticationService.authenticate(username, password);

        assertTrue(result.isPresent());
        verify(sessionRepository, times(1)).save(any());
    }

    @Test
    void authenticate_failure() {
        String username = "testuser";
        String password = "wrongpassword";
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash("encodedPassword");

        when(userService.getUserByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        Optional<Session> result = authenticationService.authenticate(username, password);

        assertFalse(result.isPresent());
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void logout() {
        String token = "validToken";
        Session session = new Session();
        session.setToken(token);

        when(sessionRepository.findByToken(token)).thenReturn(Optional.of(session));

        authenticationService.logout(token);

        verify(sessionRepository, times(1)).delete(session);
    }
}
