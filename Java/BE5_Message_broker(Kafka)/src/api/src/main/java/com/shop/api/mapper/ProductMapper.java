package com.shop.api.mapper;

import com.shop.api.dto.CategoryDto;
import com.shop.api.dto.ImageDto;
import com.shop.api.dto.ProductDto;
import com.shop.api.dto.SupplierDto;
import com.shop.api.model.Category;
import com.shop.api.model.Image;
import com.shop.api.model.Product;
import com.shop.api.model.Supplier;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public Product toEntity(ProductDto dto) {
        Category category = null;
        if(dto.getCategory() != null)
            category = new CategoryMapper()
                    .toEntity(dto.getCategory());
        Supplier supplier = null;
        if(dto.getSupplier() != null)
            supplier = new SupplierMapper()
                    .toEntity(dto.getSupplier());
        Image image = null;
        if(dto.getImage() != null) {
            image = new Image();
            image.setId(dto.getImage().getId());
        }

        return new Product(dto.getId(),
                dto.getName(),
                category,
                dto.getPrice(),
                dto.getAvailableStock(),
                dto.getLastUpdateDate(),
                supplier,
                image
                );
    }

    public ProductDto toDto(Product entity) {
        CategoryDto categoryDto = null;
        if(entity.getCategory() != null)
            categoryDto = new CategoryMapper()
                    .toDto(entity.getCategory());
        SupplierDto supplierDto = null;
        if(entity.getSupplier() != null)
            supplierDto = new SupplierMapper()
                    .toDto(entity.getSupplier());
        ImageDto imageDto = null;
        if(entity.getImage() != null) {
            imageDto = new ImageDto();
            imageDto.setId(entity.getImage().getId());
        }

        return new ProductDto(entity.getId(),
                entity.getName(),
                categoryDto,
                entity.getPrice(),
                entity.getAvailableStock(),
                entity.getLastUpdateDate(),
                supplierDto,
                imageDto
                );
    }
}
