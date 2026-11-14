package com.shop.api.mapper;

import com.shop.api.dto.AddressDto;
import com.shop.api.dto.SupplierDto;
import com.shop.api.model.Address;
import com.shop.api.model.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {
    public Supplier toEntity(SupplierDto dto) {
        Address address = null;
        if(dto.getAddress() != null)
            address = new AddressMapper()
                    .toEntity(dto.getAddress());

        return new Supplier(dto.getId(),
                dto.getName(),
                address,
                dto.getPhoneNumber()
                );
    }

    public SupplierDto toDto(Supplier entity) {
        AddressDto addressDto = null;
        if(entity.getAddress() != null)
            addressDto = new AddressMapper()
                    .toDto(entity.getAddress());

        return new SupplierDto(entity.getId(),
                entity.getName(),
                addressDto,
                entity.getPhoneNumber()
                );
    }
}
