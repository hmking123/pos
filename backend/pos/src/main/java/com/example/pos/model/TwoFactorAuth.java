package com.example.pos.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "two_factor_auth")
public class TwoFactorAuth {
    @Id
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "secret_key", nullable = false)
    private String secretKey;

    @Column(name = "is_enabled")
    private boolean isEnabled;
}
