package com.shop.generator.repository;


import com.shop.generator.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query(value = "SELECT * FROM product ORDER BY random() LIMIT 1", nativeQuery = true)
    Optional<Product> findRandom();
}
