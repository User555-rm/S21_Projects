package com.shop.api.repository;

import com.shop.api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByAvailableStockGreaterThan(int minStock);
}
