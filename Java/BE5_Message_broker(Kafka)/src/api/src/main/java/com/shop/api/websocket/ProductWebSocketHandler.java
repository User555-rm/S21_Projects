package com.shop.api.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.api.dto.ProductDto;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ProductWebSocketHandler extends TextWebSocketHandler {
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper;

    public ProductWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessions.add(session);
    }

    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        sessions.remove(session);
    }

    public void handleTextMessage(@NonNull WebSocketSession session,
                                  @NonNull TextMessage message) throws Exception {
        if(!session.isOpen()) return;
        TextMessage textMessage = new TextMessage(message.getPayload());
        session.sendMessage(textMessage);
    }

    public void broadcast(ProductDto dto) {
        String text;
        try {
            text = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return;
        }
        for(WebSocketSession session: sessions) {
            if(!session.isOpen()) continue;
            TextMessage message = new TextMessage(text);
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
