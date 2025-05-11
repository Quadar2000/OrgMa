package com.example.backend.entities.accessToken;

import java.time.LocalDateTime;

import com.example.backend.entities.users.Users;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id",nullable = false)
    private Users users;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String token;

    @NotBlank
    @Column(nullable = false)
    private LocalDateTime expiryDate;
}
