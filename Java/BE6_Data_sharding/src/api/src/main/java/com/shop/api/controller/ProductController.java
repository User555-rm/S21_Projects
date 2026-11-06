package com.shop.api.controller;

import com.shop.api.dto.ProductDto;
import com.shop.api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Добавить продукт", description =
            "Добавление товара (на вход подается json, соответствующей структуре, описанной сверху)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Товар создан",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@RequestBody
                                                 @Valid ProductDto dto) {
        ProductDto product = productService.create(dto);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @Operation(summary = "Уменьшить количество товара", description =
            "Уменьшение количества товара (на вход запросу подается id товара и на сколько уменьшить)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Количество обновлено",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "400", description = "Недостаточно товара или " +
                    "некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDto> decreaseProductStock(@PathVariable UUID id,
                                                 @RequestBody Map<String, Integer> map) {
        ProductDto product = productService.decreaseStock(id, map.getOrDefault("decreaseBy", 0));
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @Operation(summary = "Найти товар по id", description =
            "Получение товара по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Товар найден",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID id) {
        ProductDto product = productService.getById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @Operation(summary = "Найти все товары", description =
            "Получение всех доступных товаров")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список товаров",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.getAll();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Удалить товар", description =
            "Удаление товара по id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Товар удалён"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable UUID id) {
        productService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
