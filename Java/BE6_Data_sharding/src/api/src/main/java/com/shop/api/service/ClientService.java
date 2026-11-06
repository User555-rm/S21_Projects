package com.shop.api.service;

import com.shop.api.dto.AddressDto;
import com.shop.api.dto.ClientDto;

import java.util.List;
import java.util.UUID;

public interface ClientService {
    ClientDto create(ClientDto dto);
    void deleteById(UUID id);
    List<ClientDto> getByNameAndSurname(String name, String surname);
    List<ClientDto> getAll(Integer limit, Integer offset);
    ClientDto updateAddress(UUID id, AddressDto addressDto);
}
