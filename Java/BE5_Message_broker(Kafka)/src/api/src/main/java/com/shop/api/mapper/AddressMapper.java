package com.shop.api.mapper;

import com.shop.api.dto.AddressDto;
import com.shop.api.model.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public Address toEntity(AddressDto dto) {
        return new Address(dto.getId(),
                dto.getCountry(), dto.getCity(),
                dto.getStreet());
    }

    public AddressDto toDto(Address entity) {
        return new AddressDto(entity.getId(),
                entity.getCountry(), entity.getCity(),
                entity.getStreet());
    }
}
