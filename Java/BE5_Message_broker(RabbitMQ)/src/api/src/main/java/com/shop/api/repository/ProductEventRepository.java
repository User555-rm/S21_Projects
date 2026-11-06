package com.shop.api.repository;

import com.shop.api.model.ProductEvent;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProductEventRepository extends JpaRepository<ProductEvent, UUID> {
    List<ProductEvent> findTop30ByOrderByEventTimeDesc();
    // удаляем все кроме последних 30 записей в таблице product_events
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product_events WHERE id NOT IN " +
            "(SELECT id FROM product_events ORDER BY event_time DESC LIMIT :limit)", nativeQuery = true)
    void deleteOlderThanLast(int limit);
}
