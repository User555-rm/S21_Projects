package com.shop.api.service.impl;

import com.shop.api.dto.ImageIdDto;
import com.shop.api.exception.ResourceNotFoundException;
import com.shop.api.model.Product;
import com.shop.api.repository.ProductRepository;
import com.shop.api.service.ImageClient;
import com.shop.api.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {
    private final ProductRepository productRepository;
    private final ImageClient imageClient;

    public ImageServiceImpl(ProductRepository productRepository,
                            ImageClient imageClient) {
        this.productRepository = productRepository;
        this.imageClient = imageClient;
    }

    @Override
    @Transactional
    public ImageIdDto addProductImage(UUID productId, byte[] bytes) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Товар не найден"));
        UUID oldImageId = product.getImageId();
        if(oldImageId != null) imageClient.deleteImage(oldImageId);
        ImageIdDto dto = new ImageIdDto(imageClient.uploadImage(bytes));
        product.setImageId(dto.getId());
        productRepository.save(product);
        return dto;
    }

    @Override
    public byte[] getProductImage(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Товар не найден"));
        if (product.getImageId() == null)
            throw new ResourceNotFoundException("Изображение не найдено");
        return imageClient.getImage(product.getImageId());
    }

    @Override
    public void updateImage(UUID imageId, byte[] bytes) {
        imageClient.updateImage(imageId, bytes);
    }

    @Override
    @Transactional
    public void deleteImageById(UUID imageId){
        List<Product> products = productRepository.findByImageId(imageId);
        for (Product product: products) {
            product.setImageId(null);
            productRepository.save(product);
        }
        imageClient.deleteImage(imageId);
    }

    @Override
    public byte[] getImage(UUID imageId){
        return imageClient.getImage(imageId);
    }
}
