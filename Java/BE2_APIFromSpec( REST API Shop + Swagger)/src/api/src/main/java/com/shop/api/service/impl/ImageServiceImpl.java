package com.shop.api.service.impl;

import com.shop.api.dto.ImageDto;
import com.shop.api.exception.ResourceNotFoundException;
import com.shop.api.mapper.ImageMapper;
import com.shop.api.model.Image;
import com.shop.api.model.Product;
import com.shop.api.repository.ImageRepository;
import com.shop.api.repository.ProductRepository;
import com.shop.api.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final ProductRepository productRepository;

    public ImageServiceImpl(ImageRepository imageRepository,
                            ImageMapper imageMapper,
                            ProductRepository productRepository) {
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ImageDto create(UUID productId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                        .orElseThrow(ResourceNotFoundException::new);
        Image image = new Image();
        try {
            image.setImage(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл", e);
        }
        image.setProduct(product);
        imageRepository.save(image);
        return imageMapper.toDto(image);
    }

    @Override
    @Transactional
    public ImageDto update(UUID imageId, MultipartFile file) {
        Image image = imageRepository.findById(imageId)
                        .orElseThrow(ResourceNotFoundException::new);
        try {
            image.setImage(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл", e);
        }
        imageRepository.save(image);
        return imageMapper.toDto(image);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        if(imageRepository.findById(id).isEmpty())
            throw new ResourceNotFoundException();
        imageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public byte[] getByProductId(UUID productId) {
        Image image = imageRepository.findByProductId(productId)
                .orElseThrow(ResourceNotFoundException::new);
        return image.getImage();
    }

    @Override
    @Transactional
    public byte[] getById(UUID id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        return image.getImage();
    }
}
