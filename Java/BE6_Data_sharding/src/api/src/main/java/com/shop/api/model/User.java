package com.shop.api.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "users")
@Setter @Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @Column(unique = true, nullable = false)
    String email;
    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    String surname;
    @Column(name = "phone_number")
    String phoneNumber;
    @Column(name = "password_hash")
    String passwordHash;
    @Column(name = "google_sub")
    String googleSub;
}
