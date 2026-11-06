package com.shop.image.controller;

import com.shop.image.dto.ImageDto;
import com.shop.image.service.ImageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ImageDto> addImage(@RequestBody byte[] bytes) {
        ImageDto image = imageService.create(bytes);
        return new ResponseEntity<>(image, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ImageDto> changeImage(
            @PathVariable UUID id, @RequestBody byte[] bytes) {
        ImageDto image = imageService.update(id, bytes);
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable UUID id) {
        imageService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImageById(@PathVariable UUID id) {
        byte[] image = imageService.getById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "image" + id + ".png");
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}