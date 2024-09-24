package com.example.pos.repository;

import com.example.pos.model.TwoFactorAuth;
import com.example.pos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TwoFactorAuthRepository extends JpaRepository<TwoFactorAuth, Long> {
    Optional<TwoFactorAuth> findByUser(User user);
}
