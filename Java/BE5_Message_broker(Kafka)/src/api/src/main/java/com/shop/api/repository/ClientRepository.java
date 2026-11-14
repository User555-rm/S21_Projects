package com.shop.api.repository;

import com.shop.api.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    List<Client> findByClientNameAndClientSurname(String clientName, String clientSurname);
    @Query(value = "SELECT * FROM client OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Client> findWithOffsetLimit(@Param("offset") int offset, @Param("limit") int limit);
}
