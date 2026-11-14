package com.shop.api.service;

import com.shop.api.dto.ProductDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.socket.WebSocketSession;

public interface RealTimeService {
    void addSseEmitter(SseEmitter emitter);
    void removeSseEmitter(SseEmitter emitter);
    void broadcast(ProductDto dto);
}
