package com.shop.api.service.impl;

import com.shop.api.dto.ProductDto;
import com.shop.api.exception.BadRequestException;
import com.shop.api.exception.ResourceNotFoundException;
import com.shop.api.mapper.ProductMapper;
import com.shop.api.model.Product;
import com.shop.api.repository.*;
import com.shop.api.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final AddressRepository addressRepository;
    private final ImageRepository imageRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductMapper productMapper,
                              CategoryRepository categoryRepository,
                              SupplierRepository supplierRepository,
                              AddressRepository addressRepository,
                              ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.addressRepository = addressRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    @Transactional
    public ProductDto create(ProductDto dto) {
        Product product = productMapper.toEntity(dto);

        if (product.getCategory() != null) {
            categoryRepository.save(product.getCategory());
        }

        if (product.getSupplier() != null) {
            if (product.getSupplier().getAddress() != null) {
                addressRepository.save(product.getSupplier().getAddress());
            }
            supplierRepository.save(product.getSupplier());
        }

        productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductDto decreaseStock(UUID id, int decreaseBy) {
        Product product = productRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        if (product.getAvailableStock() < decreaseBy)
            throw new BadRequestException("Недостаточно товара в наличии");
        product.setAvailableStock(product.getAvailableStock() - decreaseBy);
        productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductDto getById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public List<ProductDto> getAll() {
        List<Product> products = productRepository.findAll();
        List<ProductDto> result = new ArrayList<>();
        for (Product product : products) {
            result.add(productMapper.toDto(product));
        }
        return result;
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        if (product.getImage() != null) {
            imageRepository.deleteById(product.getImage().getId());
        }

        productRepository.deleteById(id);
    }
}
