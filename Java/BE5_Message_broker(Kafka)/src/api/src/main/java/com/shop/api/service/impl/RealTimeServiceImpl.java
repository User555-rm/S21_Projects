package com.shop.api.service.impl;

import com.shop.api.dto.ProductDto;
import com.shop.api.service.RealTimeService;
import com.shop.api.websocket.ProductWebSocketHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class RealTimeServiceImpl implements RealTimeService {
    private final ProductWebSocketHandler webSocketHandler;
    private final List<SseEmitter> sseEmitters = new CopyOnWriteArrayList<>();

    public RealTimeServiceImpl(ProductWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void addSseEmitter(SseEmitter emitter) {
        sseEmitters.add(emitter);
    }

    @Override
    public void removeSseEmitter(SseEmitter emitter) {
        sseEmitters.remove(emitter);
    }

    @Override
    public void broadcast(ProductDto dto) {
        webSocketHandler.broadcast(dto);
        for(SseEmitter emitter: sseEmitters) {
            try {
                emitter.send(dto);
            } catch (IllegalStateException e) {
                sseEmitters.remove(emitter);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
