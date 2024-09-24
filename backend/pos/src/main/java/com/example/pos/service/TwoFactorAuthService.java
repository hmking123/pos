package com.example.pos.service;

import com.example.pos.model.TwoFactorAuth;
import com.example.pos.model.User;
import com.example.pos.repository.TwoFactorAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Service
public class TwoFactorAuthService {

    @Autowired
    private TwoFactorAuthRepository twoFactorAuthRepository;

    public TwoFactorAuth enableTwoFactorAuth(User user) {
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setUser(user);
        twoFactorAuth.setSecretKey(generateSecretKey());
        twoFactorAuth.setEnabled(true);
        return twoFactorAuthRepository.save(twoFactorAuth);
    }

    public void disableTwoFactorAuth(User user) {
        twoFactorAuthRepository.findByUser(user).ifPresent(twoFactorAuth -> {
            twoFactorAuth.setEnabled(false);
            twoFactorAuthRepository.save(twoFactorAuth);
        });
    }

    public Optional<TwoFactorAuth> getTwoFactorAuthForUser(User user) {
        return twoFactorAuthRepository.findByUser(user);
    }

    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
