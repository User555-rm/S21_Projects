package com.shop.api.service.impl;

import com.shop.api.dto.AddressDto;
import com.shop.api.dto.ClientDto;
import com.shop.api.exception.ResourceNotFoundException;
import com.shop.api.mapper.AddressMapper;
import com.shop.api.mapper.ClientMapper;
import com.shop.api.model.Address;
import com.shop.api.model.Client;
import com.shop.api.repository.AddressRepository;
import com.shop.api.repository.ClientRepository;
import com.shop.api.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;

    public ClientServiceImpl(ClientRepository clientRepository,
                             ClientMapper clientMapper,
                             AddressMapper addressMapper,
                             AddressRepository addressRepository) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.addressMapper = addressMapper;
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional
    public ClientDto create(ClientDto dto) {
        Client client = clientMapper.toEntity(dto);
        if (client.getAddress() != null) {
            addressRepository.save(client.getAddress());   // ← сохраняем адрес!
        }
        clientRepository.save(client);
        return clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        if (client.getAddress() != null) {
            addressRepository.deleteById(client.getAddress().getId());
        }

        clientRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<ClientDto> getByNameAndSurname(String name, String surname) {
        List<Client> clients = clientRepository
                .findByClientNameAndClientSurname(name, surname);
        List<ClientDto> result = new ArrayList<>();
        for(Client client: clients) {
            result.add(clientMapper.toDto(client));
        }
        return result;
    }

    @Override
    @Transactional
    public List<ClientDto> getAll(Integer limit, Integer offset) {
        int defaultLimit = 10;
        int defaultOffset = 0;

        // Если параметры не переданы - возвращаем всех
        if (limit == null && offset == null) {
            return clientRepository.findAll().stream()
                    .map(clientMapper::toDto)
                    .collect(Collectors.toList());
        }

        int pageSize = (limit != null && limit > 0) ? Math.min(limit, 100) : defaultLimit;
        int off = (offset != null && offset >= 0) ? offset : defaultOffset;

        // ИСПОЛЬЗУЕМ КАСТОМНЫЙ МЕТОД
        List<Client> clients = clientRepository.findWithOffsetLimit(off, pageSize);

        return clients.stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClientDto updateAddress(UUID id, AddressDto addressDto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        if(client.getAddress() != null)
        addressRepository.deleteById(client.getAddress().getId());
        Address address = addressMapper.toEntity(addressDto);
        addressRepository.save(address);
        client.setAddress(address);
        clientRepository.save(client);
        ClientDto result = clientMapper.toDto(client);
        return result;
    }

}
