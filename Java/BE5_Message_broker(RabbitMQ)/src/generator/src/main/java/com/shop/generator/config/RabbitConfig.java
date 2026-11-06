package com.shop.generator.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    /** Имя обменника, куда генератор публикует события.
     *  ДОЛЖНО совпадать с именем обменника в api — это «контракт» между двумя сервисами. */
    public static final String PRODUCT_EVENTS_EXCHANGE = "product.events.exchange";
    /** Routing key, по которому сообщение уходит в нужную очередь.
       В api по этому же ключу очередь product.events.queue привязана к обменнику. */
    public static final String PRODUCT_EVENTS_ROUTING_KEY = "product.events.updated";

    @Bean
    public TopicExchange productEventsExchange() {
        // durable=true, autoDelete=false по умолчанию — обменник живёт в брокере постоянно
        return new TopicExchange(PRODUCT_EVENTS_EXCHANGE);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}