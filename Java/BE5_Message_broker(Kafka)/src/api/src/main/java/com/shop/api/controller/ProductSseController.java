package com.shop.api.controller;

import com.shop.api.service.RealTimeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class ProductSseController {
    private final RealTimeService realTimeService;

    public ProductSseController(RealTimeService realTimeService) {
        this.realTimeService = realTimeService;
    }

    @GetMapping(value = "/sse/products", produces = "text/event-stream")
    public SseEmitter subscribeProducts() {
        SseEmitter emitter = new SseEmitter(0L);
        realTimeService.addSseEmitter(emitter);

        emitter.onCompletion(() -> realTimeService.removeSseEmitter(emitter));
        emitter.onTimeout(() -> realTimeService.removeSseEmitter(emitter));
        emitter.onError((e) -> {
                realTimeService.removeSseEmitter(emitter);
                System.out.println(e.getMessage());});
        return emitter;
    }
}
