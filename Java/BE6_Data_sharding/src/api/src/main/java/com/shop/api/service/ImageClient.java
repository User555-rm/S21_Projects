package com.shop.api.service;

import com.shop.api.dto.ImageIdDto;
import com.shop.api.exception.ImageClientException;
import com.shop.api.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Component
public class ImageClient {
    private final RestClient restClient;

    public ImageClient(@Value("${image.service.url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

    }

    public UUID uploadImage(byte[] bytes) {
        try {
            ResponseEntity<ImageIdDto> response = restClient.post()
                    .uri("/api/v1/images")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(bytes)
                    .retrieve()
                    .toEntity(ImageIdDto.class);
            ImageIdDto body = response.getBody();
            if (body == null || body.getId() == null) {
                throw new ImageClientException("Пустой ответ image-service");
            }
            return body.getId();
        } catch (RestClientResponseException | ResourceAccessException e) {
            throw new ImageClientException("Ошибка вызова image-service", e);
        }
    }

    public void updateImage(UUID id, byte[] bytes) {
        try{
            restClient.put()
                .uri("/api/v1/images/{id}", id)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes)
                .retrieve()
                .toBodilessEntity();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new ResourceNotFoundException("Изображение не найдено");   // 404
            throw new ImageClientException("Ошибка вызова image-service", e);     // 502
        } catch (ResourceAccessException e) {
            throw new ImageClientException("Ошибка вызова image-service", e);
        }
    }

    public void deleteImage(UUID id) {
        try {
        restClient.delete()
                .uri("/api/v1/images/{id}", id)
                .retrieve()
                .toBodilessEntity();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new ResourceNotFoundException("Изображение не найдено");   // 404
            throw new ImageClientException("Ошибка вызова image-service", e);     // 502
        } catch (ResourceAccessException e) {
            throw new ImageClientException("Ошибка вызова image-service", e);
        }
    }

    public byte[] getImage(UUID id) {
        try {
            ResponseEntity<byte[]> response = restClient.get()
                    .uri("/api/v1/images/{id}", id)
                    .retrieve()
                    .toEntity(byte[].class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new ResourceNotFoundException("Изображение не найдено");   // 404
            throw new ImageClientException("Ошибка вызова image-service", e);     // 502
        } catch (ResourceAccessException e) {
            throw new ImageClientException("Ошибка вызова image-service", e);
        }
    }
}