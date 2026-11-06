package com.shop.api.consumer;

import com.shop.api.config.RabbitConfig;
import com.shop.api.dto.ProductDto;
import com.shop.api.dto.ProductEventDto;
import com.shop.api.exception.ResourceNotFoundException;
import com.shop.api.service.ProductService;
import com.shop.api.service.RealTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventConsumer {
    private final ProductService productService;
    private final RealTimeService realTimeService;

    @RabbitListener(queues = RabbitConfig.PRODUCT_EVENTS_QUEUE)
    public void onProductEvent(ProductEventDto event) {
        try {
            ProductDto dto = productService.applyEvent(event);
            realTimeService.broadcast(dto);
        } catch (ResourceNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
