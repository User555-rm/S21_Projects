package com.shop.image.mapper;

import com.shop.image.dto.ImageDto;
import com.shop.image.model.Image;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {
    public ImageDto toDto(Image entity) {
        return new ImageDto(entity.getId());
    }
}
