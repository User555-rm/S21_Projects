package com.shop.api.service.impl;

import com.shop.api.dto.ImageDto;
import com.shop.api.exception.ResourceNotFoundException;
import com.shop.api.mapper.ImageMapper;
import com.shop.api.model.Image;
import com.shop.api.model.Product;
import com.shop.api.repository.ImageRepository;
import com.shop.api.repository.ProductRepository;
import com.shop.api.service.ImageService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ImageServiceImpl implements ImageService {
    // константы для кеша картинов в Redis
    // ключ image:<id>
    private static final String KEY_PREFIX = "image:";
    // ключ image:prod:<productId>
    private static final String KEY_PREFIX_BY_PRODUCT = "image:prod:";
    // скока хранится
    private static final long CACHE_TTL_MINUTES = 10;

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final ProductRepository productRepository;
    private final RedisTemplate<String, byte[]> redisTemplate;

    public ImageServiceImpl(ImageRepository imageRepository,
                            ImageMapper imageMapper,
                            ProductRepository productRepository,
                            RedisTemplate<String, byte[]> redisTemplate) {
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
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
        redisTemplate.delete(KEY_PREFIX_BY_PRODUCT + productId);
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
        redisTemplate.delete(KEY_PREFIX + imageId);
        redisTemplate.delete(KEY_PREFIX_BY_PRODUCT + image.getProduct().getId());
        return imageMapper.toDto(image);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        redisTemplate.delete(KEY_PREFIX + id);
        redisTemplate.delete(KEY_PREFIX_BY_PRODUCT + image.getProduct().getId());
        imageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public byte[] getByProductId(UUID productId) {
        String key = KEY_PREFIX_BY_PRODUCT + productId;
        byte[] cached = redisTemplate.opsForValue().get(key);
        if(cached != null) return cached;

        Image image = imageRepository.findByProductId(productId)
                .orElseThrow(ResourceNotFoundException::new);
        byte[] res = image.getImage();
        redisTemplate.opsForValue().set(key, res, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        return res;
    }

    @Override
    @Transactional
    public byte[] getById(UUID id) {
        String key = KEY_PREFIX + id;
        byte[] cached = redisTemplate.opsForValue().get(key);
        if(cached != null) return cached;

        Image image = imageRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        byte[] res = image.getImage();
        redisTemplate.opsForValue().set(key, res, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        return res;
    }
}
