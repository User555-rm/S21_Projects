package com.shop.api.mapper;

import com.shop.api.dto.ImageDto;
import com.shop.api.dto.ProductDto;
import com.shop.api.model.Image;
import com.shop.api.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {
    public Image toEntity(ImageDto dto) {
        Product product = null;
            if(dto.getProduct() != null)
                product = new ProductMapper()
                .toEntity(dto.getProduct());

        return new Image(dto.getId(),
                dto.getImage(),
                product
                );
    }

    public ImageDto toDto(Image entity) {
        ProductDto productDto = null;
            if(entity.getProduct() != null)
                productDto = new ProductMapper()
                .toDto(entity.getProduct());

        return new ImageDto(entity.getId(),
                entity.getImage(),
                productDto
                );
    }
}
