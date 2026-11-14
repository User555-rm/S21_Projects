package com.shop.api.controller;

import com.shop.api.dto.ImageDto;
import com.shop.api.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Operation(summary = "Добавить изображение", description =
            "Добавление изображения (на вход подается byte array изображения и id товара)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Изображение загружено", content =
            @Content(schema = @Schema(implementation = ImageDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping(value = "/product/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageDto> addImage(
            @PathVariable UUID productId,
            @Parameter(description = "Файл изображения",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("file") MultipartFile file) {
        ImageDto image = imageService.create(productId, file);
        return new ResponseEntity<>(image, HttpStatus.CREATED);
    }

    @Operation(summary = "Изменить изображение", description =
            "Изменение изображения (на вход подается id изображения и новая строка для замены)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Изображение заменено",
                    content = @Content(schema = @Schema(implementation = ImageDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Изображение не найдено"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageDto> changeImage(
            @PathVariable UUID id,
            @Parameter(description = "Файл изображения",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("file") MultipartFile file) {
        ImageDto image = imageService.update(id, file);
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @Operation(summary = "Удалить изображение", description =
            "Удаление изображения по id изображения")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Изображение удалено"),
            @ApiResponse(responseCode = "404", description = "Изображение не найдено"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable UUID id) {
        imageService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Получить изображение по id товара", description =
            "Получение изображения конкретного товара (по id товара)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Изображение (скачивание)",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "404", description = "Изображение не найдено"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<byte[]> getImageByProductId(@PathVariable UUID productId) {
        byte[] image = imageService.getByProductId(productId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "image" + productId + ".png");
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    @Operation(summary = "Получить изображение по id", description =
            "Получение изображения по id изображения")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Изображение (скачивание)",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "404", description = "Изображение не найдено"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImageById(@PathVariable UUID id) {
        byte[] image = imageService.getById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "image" + id + ".png");
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}