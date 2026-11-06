package com.shop.api.service;

import com.shop.api.dto.ImageIdDto;

import java.util.UUID;

public interface ImageService {
    ImageIdDto addProductImage(UUID productId, byte[] bytes);
    byte[] getProductImage(UUID productId);
    void updateImage(UUID imageId, byte[] bytes);
    void deleteImageById(UUID imageId);
    byte[] getImage(UUID imageId);
}
