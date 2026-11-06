package com.shop.api.mapper;

import com.shop.api.dto.AddressDto;
import com.shop.api.dto.ClientDto;
import com.shop.api.model.Address;
import com.shop.api.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {
    public Client toEntity(ClientDto dto) {
        Address address = null;
        if(dto.getAddress() != null)
            address = new AddressMapper()
                .toEntity(dto.getAddress());

        return new Client(dto.getId(),
                dto.getClientName(),
                dto.getClientSurname(),
                dto.getBirthday(),
                dto.getGender(),
                dto.getRegistrationDate(),
                address
                );
    }

    public ClientDto toDto(Client entity) {
        AddressDto addressDto = null;
        if(entity.getAddress() != null)
                addressDto = new AddressMapper()
                .toDto(entity.getAddress());

        return new ClientDto(entity.getId(),
                entity.getClientName(),
                entity.getClientSurname(),
                entity.getBirthday(),
                entity.getGender(),
                entity.getRegistrationDate(),
                addressDto
                );
    }
}
