package com.shop.api.service.impl;

import com.shop.api.dto.AddressDto;
import com.shop.api.dto.SupplierDto;
import com.shop.api.exception.ResourceNotFoundException;
import com.shop.api.mapper.AddressMapper;
import com.shop.api.mapper.SupplierMapper;
import com.shop.api.model.Address;
import com.shop.api.model.Supplier;
import com.shop.api.repository.AddressRepository;
import com.shop.api.repository.SupplierRepository;
import com.shop.api.service.SupplierService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public SupplierServiceImpl(SupplierRepository supplierRepository,
                               SupplierMapper supplierMapper,
                               AddressRepository addressRepository,
                               AddressMapper addressMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }


    @Override
    @Transactional
    public SupplierDto create(SupplierDto dto) {
        Supplier supplier = supplierMapper.toEntity(dto);
        if (supplier.getAddress() != null) {
            addressRepository.save(supplier.getAddress());
        }
        supplierRepository.save(supplier);
        return supplierMapper.toDto(supplier);
    }

    @Override
    @Transactional
    public SupplierDto updateAddress(UUID id, AddressDto addressDto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        Address address = supplier.getAddress();
        if (address == null) {
            address = new Address();
        }
        address.setCountry(addressDto.getCountry());
        address.setCity(addressDto.getCity());
        address.setStreet(addressDto.getStreet());

        addressRepository.save(address);
        supplier.setAddress(address);
        supplierRepository.save(supplier);
        return supplierMapper.toDto(supplier);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        if (supplier.getAddress() != null) {
            addressRepository.deleteById(supplier.getAddress().getId());
        }

        supplierRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<SupplierDto> getAll() {
        List<Supplier> suppliers = supplierRepository.findAll();
        List<SupplierDto> result = new ArrayList<>();
        for (Supplier supplier: suppliers) {
            result.add(supplierMapper.toDto(supplier));
        }
        return result;
    }

    @Override
    @Transactional
    public SupplierDto getById(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        return supplierMapper.toDto(supplier);
    }
}
