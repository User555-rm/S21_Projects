package com.shop.api.service;

import com.shop.api.dto.ImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ImageService {
    ImageDto create(UUID productId, MultipartFile file);
    ImageDto update(UUID imageId, MultipartFile file);
    void deleteById(UUID id);
    byte[] getByProductId(UUID productId);
    byte[] getById(UUID id);
}
