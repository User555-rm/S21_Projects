package com.shop.generator.config;

import org.springframework.kafka.support.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.generator.dto.ProductEventDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    public static final String PRODUCT_EVENTS_TOPIC = "product.events";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, ProductEventDto> producerFactory(
                                            ObjectMapper objectMapper) {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        JsonSerializer<ProductEventDto> jsonSerializer = new JsonSerializer<>(objectMapper);
        DefaultKafkaProducerFactory<String, ProductEventDto> factory =
                new DefaultKafkaProducerFactory<>(config);
        factory.setValueSerializer(jsonSerializer);
        return factory;
    }

    @Bean
    KafkaTemplate<String, ProductEventDto> productEventKafkaTemplate(
            ProducerFactory<String, ProductEventDto> factory) {
        return new KafkaTemplate<>(factory);
    }

}
