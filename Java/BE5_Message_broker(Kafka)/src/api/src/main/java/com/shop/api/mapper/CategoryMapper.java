package com.shop.api.mapper;

import com.shop.api.dto.CategoryDto;
import com.shop.api.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public Category toEntity(CategoryDto dto) {
        return new Category(dto.getId(),
                dto.getName());
    }

    public CategoryDto toDto(Category entity) {
        return new CategoryDto(entity.getId(),
                entity.getName());
    }
}
