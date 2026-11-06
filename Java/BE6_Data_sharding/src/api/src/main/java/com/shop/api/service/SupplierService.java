package com.shop.api.service;

import com.shop.api.dto.AddressDto;
import com.shop.api.dto.SupplierDto;

import java.util.List;
import java.util.UUID;

public interface SupplierService {
    SupplierDto create(SupplierDto dto);
    SupplierDto updateAddress(UUID id, AddressDto addressDto);
    void deleteById(UUID id);
    List<SupplierDto> getAll();
    SupplierDto getById(UUID id);
}
