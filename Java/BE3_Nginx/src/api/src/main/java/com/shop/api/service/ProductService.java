package com.shop.api.service;

import com.shop.api.dto.ProductDto;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDto create(ProductDto dto);
    ProductDto decreaseStock(UUID id, int decreaseBy);
    ProductDto getById(UUID id);
    List<ProductDto> getAll();
    void deleteById(UUID id);
}
