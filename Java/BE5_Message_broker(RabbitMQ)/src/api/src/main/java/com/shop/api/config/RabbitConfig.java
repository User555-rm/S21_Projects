package com.shop.api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String PRODUCT_EVENTS_EXCHANGE = "product.events.exchange";
    public static final String PRODUCT_EVENTS_ROUTING_KEY = "product.events.updated";
    public static final String PRODUCT_EVENTS_QUEUE = "product.events.queue";

    @Bean
    public TopicExchange productEventsExchange() {
        return new TopicExchange(PRODUCT_EVENTS_EXCHANGE);
    }

    @Bean
    public Binding productEventsBinding(Queue queue, TopicExchange exchange) {
        return new Binding(queue.getName(), Binding.DestinationType.QUEUE, exchange.getName(),
                PRODUCT_EVENTS_ROUTING_KEY, null);
    }

    @Bean
    public Queue productEventsQueue() {
        return new Queue(PRODUCT_EVENTS_QUEUE, true);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
