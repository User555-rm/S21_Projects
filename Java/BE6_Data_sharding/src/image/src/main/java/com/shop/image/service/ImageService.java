package com.shop.image.service;

import com.shop.image.dto.ImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ImageService {
    ImageDto create(byte[] image);
    ImageDto update(UUID id, byte[] image);
    void deleteById(UUID id);
    byte[] getById(UUID id);
}
