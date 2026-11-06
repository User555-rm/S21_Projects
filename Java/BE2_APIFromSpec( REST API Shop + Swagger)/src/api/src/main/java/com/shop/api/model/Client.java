package com.shop.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "client")
public class Client {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "client_name", nullable = false, length = 50)
    private String clientName;

    @Column(name = "client_surname", nullable = false, length = 50)
    private String clientSurname;

    @Column
    private LocalDate birthday;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column(name = "registration_date", updatable = false, nullable = false) @CreationTimestamp
    private LocalDateTime registrationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;
}