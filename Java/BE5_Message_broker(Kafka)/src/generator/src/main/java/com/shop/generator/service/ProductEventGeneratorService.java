package com.shop.generator.service;

import com.shop.generator.dto.ProductEventDto;
import com.shop.generator.model.Product;
import com.shop.generator.repository.ProductRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.shop.generator.config.KafkaProducerConfig.PRODUCT_EVENTS_TOPIC;

@Service
public class ProductEventGeneratorService {
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, ProductEventDto> kafkaTemplate;

    public ProductEventGeneratorService(ProductRepository productRepository,
                KafkaTemplate<String, ProductEventDto> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${generator.interval-ms:5000}")
    public void generateEvent() {
        Optional<Product> p = productRepository.findRandom();
        if(p.isEmpty()) return;
        Product product = p.get();
        Random random = new Random();
        //±1..3 от текущего количества
        int delta = (1 + random.nextInt(3)) * (random.nextBoolean() ? 1 : -1);
        if(product.getAvailableStock() + delta < 0) delta = -delta;
        product.setAvailableStock(product.getAvailableStock() + delta);
        //±10..25% от текущей цены
        double priceAdjustment = (10 + random.nextInt(16)) / 100.0 * (random.nextBoolean() ? 1 : -1);
        // (цена + цена * изменение_цены < 0) изменение_цены = - изменение_цены; это если по-человечески
        if(product.getPrice().add(product.getPrice().multiply(
                BigDecimal.valueOf(priceAdjustment)))
                .compareTo(BigDecimal.ZERO) < 0) priceAdjustment = - priceAdjustment;
        product.setPrice(product.getPrice().add(product.getPrice().multiply(
                BigDecimal.valueOf(priceAdjustment))));

        ProductEventDto eventDto = new ProductEventDto(UUID.randomUUID(), product.getId(),
                product.getPrice(), product.getAvailableStock(), LocalDateTime.now());
        kafkaTemplate.send(PRODUCT_EVENTS_TOPIC, product.getId().toString(), eventDto);
    }
}
