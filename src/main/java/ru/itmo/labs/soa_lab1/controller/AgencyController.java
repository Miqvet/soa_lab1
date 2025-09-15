package ru.itmo.labs.soa_lab1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.labs.soa_lab1.controller.dto.ErrorResponse;
import ru.itmo.labs.soa_lab1.repository.entity.Flat;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/agency")
@Tag(name = "Agency API", description = "API для выполнения части операций агенства недвижимости")
public class AgencyController {
    @Operation(summary = "Найти самую дешёвую/дорогую квартиру с балконом/без балкона")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Квартира найдена",
                    content = @Content(schema = @Schema(implementation = Flat.class))),
            @ApiResponse(responseCode = "404", description = "Квартира не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/find-with-balcony/{cheapest}/{withBalcony}")
    public ResponseEntity<Map<String, Object>> findWithBalcony(
            @PathVariable Boolean cheapest,
            @PathVariable Boolean withBalcony) {
        return ResponseEntity.ok().body(Map.of(
            "message", "Функция в разработке",
            "cheapest", cheapest,
            "withBalcony", withBalcony
        ));
    }

    @Operation(summary = "Выбрать наиболее дорогую квартиру из трёх")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Квартира найдена",
                    content = @Content(schema = @Schema(implementation = Flat.class))),
            @ApiResponse(responseCode = "404", description = "Одна или несколько квартир не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/get-most-expensive/{id1}/{id2}/{id3}")
    public ResponseEntity<Map<String, Object>> getMostExpensive(
            @PathVariable Long id1,
            @PathVariable Long id2,
            @PathVariable Long id3) {
        return ResponseEntity.ok().body(Map.of(
            "message", "Функция в разработке",
            "comparedFlats", List.of(id1, id2, id3)
        ));
    }
}