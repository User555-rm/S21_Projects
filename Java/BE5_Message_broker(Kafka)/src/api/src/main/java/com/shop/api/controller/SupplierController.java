package com.shop.api.controller;

import com.shop.api.dto.AddressDto;
import com.shop.api.dto.SupplierDto;
import com.shop.api.service.SupplierService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {
    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @Operation(summary = "Добавить поставщика", description =
            "Добавление поставщика (на вход подается json, соответствующей структуре, " +
                    "описанной сверху)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Поставщик создан", content =
            @Content(schema = @Schema(implementation = SupplierDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    public ResponseEntity<SupplierDto> addSupplier(
            @RequestBody @Valid SupplierDto dto) {
        SupplierDto supplier = supplierService.create(dto);
        return new ResponseEntity<>(supplier, HttpStatus.CREATED);
    }

    @Operation(summary = "Изменить адрес поставщика", description =
            "Изменение адреса поставщика (параметры: id и новый адрес в виде json " +
                    "в соответствии с выше описанным форматом)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Адрес обновлён",
                    content = @Content(schema = @Schema(implementation = SupplierDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Поставщик не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PatchMapping("/{id}/address")
    public ResponseEntity<SupplierDto> changeAddressById(
            @PathVariable UUID id, @RequestBody @Valid AddressDto address) {
        SupplierDto supplier = supplierService.updateAddress(id, address);
        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }

    @Operation(summary = "Удалить поставщика", description =
            "Удаление поставщика по id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Поставщик удалён"),
            @ApiResponse(responseCode = "404", description = "Поставщик не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSupplierById(@PathVariable UUID id) {
        supplierService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Найти всех поставщиков", description =
            "Получение всех поставщиков")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список поставщиков",
                    content = @Content(schema = @Schema(implementation = SupplierDto.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<List<SupplierDto>> getAllSuppliers() {
        List<SupplierDto> suppliers = supplierService.getAll();
        return new ResponseEntity<>(suppliers, HttpStatus.OK);
    }

    @Operation(summary = "Найти поставщика по id", description =
            "Получение поставщика по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Поставщик найден",
                    content = @Content(schema = @Schema(implementation = SupplierDto.class))),
            @ApiResponse(responseCode = "404", description = "Поставщик не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SupplierDto> getSupplierById(@PathVariable
                                                       UUID id) {
        SupplierDto supplier = supplierService.getById(id);
        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }
}
