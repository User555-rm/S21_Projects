package com.shop.image.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter @Getter @NoArgsConstructor
@Entity @Table(name = "images")
public class Image {
    @Id
    private UUID id;
    
    @Column(name = "image", columnDefinition = "bytea", nullable = false)
    private byte[] image;
}
