package com.shop.api.controller;

import com.shop.api.dto.AddressDto;
import com.shop.api.dto.ClientDto;
import com.shop.api.service.ClientService;
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
@RequestMapping("/api/v1/clients")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(summary = "Создать клиента",
            description = "Добавление клиента (на вход подается json, " +
                    "соответствующей структуре, описанной сверху)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Клиент создан",
                    content = @Content(schema = @Schema(implementation = ClientDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    public ResponseEntity<ClientDto> addClient(@Valid @RequestBody ClientDto dto) {
        ClientDto client = clientService.create(dto);
        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }

    @Operation(summary = "Удалить клиента", description =
            "Удаление клиента (по его идентификатору).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Клиент удалён"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable UUID id) {
        clientService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Найти клиента по имени и фамилии", description =
            "Получение клиентов по имени и фамилии (параметры — имя и фамилия)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список клиентов",
                    content = @Content(schema = @Schema(implementation = ClientDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/search")
    public ResponseEntity<List<ClientDto>> findClientByNameAndSurname(
            @Valid @RequestParam String name, @Valid @RequestParam String surname) {
        List<ClientDto> clients = clientService.getByNameAndSurname(name, surname);
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @Operation(summary = "Найти всех клиентов", description =
            "Получение всех клиентов (В данном запросе необходимо предусмотреть " +
                    "опциональные параметры пагинации в строке запроса: limit и offset). " +
                    "В случае отсутствия этих параметров возвращать весь список.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список клиентов",
                    content = @Content(schema = @Schema(implementation = ClientDto.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients(
            @Valid @RequestParam(required = false) Integer limit,
            @Valid @RequestParam(required = false) Integer offset) {
        List<ClientDto> clients = clientService.getAll(limit, offset);
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @Operation(summary = "Изменить адрес клиента по id", description =
            "Изменение адреса клиента (параметры: id и новый адрес " +
                    "в виде json в соответствии с выше описанным форматом)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Адрес обновлён",
                    content = @Content(schema = @Schema(implementation = ClientDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PatchMapping("/{id}/address")
    public ResponseEntity<ClientDto> changeAddress(@PathVariable UUID id, @Valid
                                           @RequestBody AddressDto addressDto) {
        ClientDto client = clientService.updateAddress(id, addressDto);
        return new ResponseEntity<>(client, HttpStatus.OK);
    }
}
