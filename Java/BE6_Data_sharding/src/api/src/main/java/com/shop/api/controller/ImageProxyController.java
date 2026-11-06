package com.shop.api.controller;

import com.shop.api.dto.ImageIdDto;
import com.shop.api.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/images")
public class ImageProxyController {
    private final ImageService imageService;

    public ImageProxyController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Operation(summary = "Добавить изображение", description =
            "Добавление изображения (на вход подается byte array изображения и id товара)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Изображение загружено", content =
            @Content(schema = @Schema(implementation = ImageIdDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "502", description = "image-service недоступен")
    })
    @PostMapping(value = "/product/{productId}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ImageIdDto> addProductImage(@PathVariable UUID productId,
                                                      @RequestBody byte[] bytes) {
        ImageIdDto dto = imageService.addProductImage(productId, bytes);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Operation(summary = "Получить изображение по id товара", description =
            "Получение изображения конкретного товара (по id товара)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Изображение (скачивание)",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Изображение не найдено"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "502", description = "image-service недоступен")
    })
    @GetMapping(value = "/product/{productId}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable UUID productId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "img_product-" + productId + ".png");
        return new ResponseEntity<>(imageService.getProductImage(productId), headers, HttpStatus.OK);
    }

    @Operation(summary = "Изменить изображение", description =
            "Изменение изображения (на вход подается id изображения и новая строка для замены)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Изображение заменено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Изображение не найдено"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "502", description = "image-service недоступен")
    })
    @PutMapping(value = "/{imageId}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Void> updateImage(@PathVariable UUID imageId,
                                            @RequestBody byte[] bytes) {
        imageService.updateImage(imageId, bytes);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Удалить изображение", description =
            "Удаление изображения по id изображения")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Изображение удалено"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Изображение не найдено"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "502", description = "image-service недоступен")
    })
    @DeleteMapping(value = "/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID imageId) {
        imageService.deleteImageById(imageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Получить изображение по id", description =
            "Получение изображения по id изображения")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Изображение (скачивание)",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Изображение не найдено"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "502", description = "image-service недоступен")
    })
    @GetMapping(value = "/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable UUID imageId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "img-" + imageId + ".png");
        return new ResponseEntity<>(imageService.getImage(imageId), headers, HttpStatus.OK);
    }
}
