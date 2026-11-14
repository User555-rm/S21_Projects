package com.shop.api.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.api.dto.ProductEventDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    public static final String PRODUCT_EVENTS_TOPIC = "product.events";
    public static final String PRODUCT_EVENTS_GROUP = "api-product-events-group";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public NewTopic productEventsTopic() {
        return new NewTopic(PRODUCT_EVENTS_TOPIC, 1, (short) 1);
    }

    @Bean
    ConsumerFactory<String, ProductEventDto> consumerFactory(
                            ObjectMapper objectMapper) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, PRODUCT_EVENTS_GROUP);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        JsonDeserializer<ProductEventDto> deserializer = new JsonDeserializer<>(
                ProductEventDto.class, objectMapper, false);
        deserializer.trustedPackages("com.shop.api.dto", "java.time", "java.util");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, ProductEventDto>
    kafkaListenerContainerFactory(ConsumerFactory<String, ProductEventDto> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, ProductEventDto> cf =
                new ConcurrentKafkaListenerContainerFactory<>();
        cf.setConsumerFactory(consumerFactory);
        cf.setConcurrency(1);
        return cf;
    }

}
