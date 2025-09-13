package ru.itmo.labs.soa_lab1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.labs.soa_lab1.controller.dto.*;
import ru.itmo.labs.soa_lab1.repository.entity.Flat;
import ru.itmo.labs.soa_lab1.repository.entity.Furnish;
import ru.itmo.labs.soa_lab1.repository.entity.Transport;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/flats")
@Tag(name = "Flat API", description = "API для управления квартирами")
public class FlatController {

    private final Map<Long, Flat> flats = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Operation(summary = "Добавить новую квартиру")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Квартира успешно создана",
                    content = @Content(schema = @Schema(implementation = Flat.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные квартиры",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Flat> createFlat(@Valid @RequestBody FlatDto flatDto) {
        var newId = idCounter.getAndIncrement();
        var flat = FlatMapper.mapFlatFromFlatDto(flatDto);
        flat.setCreationDate(LocalDate.now());
        flat.setId((int) newId);
        flat.setCreationDate(LocalDate.now());
        flats.put(newId, flat);
        return ResponseEntity.status(HttpStatus.CREATED).body(flat);
    }

    @Operation(summary = "Получить квартиру по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Квартира найдена",
                    content = @Content(schema = @Schema(implementation = Flat.class))),
            @ApiResponse(responseCode = "404", description = "Квартира не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Flat> getFlat(
            @Parameter(description = "ID квартиры") @PathVariable Long id) {
        return Optional.ofNullable(flats.get(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Обновить квартиру")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Квартира успешно обновлена",
                    content = @Content(schema = @Schema(implementation = Flat.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные квартиры",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Квартира не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Flat> updateFlat(
            @Parameter(description = "ID квартиры") @PathVariable Long id,
            @Valid @RequestBody FlatDto flatDto) {
        if (!flats.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        var flat = FlatMapper.mapFlatFromFlatDto(flatDto);

        var existingFlat = flats.get(id);
        flat.setId(existingFlat.getId());
        flat.setCreationDate(existingFlat.getCreationDate());

        flats.put(id, flat);
        return ResponseEntity.ok(flat);
    }

    @Operation(summary = "Удалить квартиру по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Квартира успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Квартира не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlat(
            @Parameter(description = "ID квартиры") @PathVariable Long id) {
        return Optional.ofNullable(flats.remove(id))
                .map(flat -> ResponseEntity.ok().<Void>build())
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить список квартир с фильтрацией, сортировкой и постраничным выводом")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список квартир успешно получен",
                    content = @Content(schema = @Schema(implementation = FlatPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/filtered")
    public ResponseEntity<FlatPageResponse> getFlats(
            // Параметры фильтрации
            @Parameter(description = "Поля фильтрации")
            @RequestBody FlatsFilterDto flatsFilter,

            // Параметры сортировки
            @Parameter(description = "Поле для сортировки (name, area, numberOfRooms, etc.)")
            @RequestParam(required = false) String sortBy,

            @Parameter(description = "Направление сортировки (asc, desc)")
            @RequestParam(required = false, defaultValue = "asc") String sortDirection,

            // Параметры пагинации
            @Parameter(description = "Номер страницы (начиная с 0)")
            @RequestParam(required = false, defaultValue = "0") Integer page,

            @Parameter(description = "Размер страницы")
            @RequestParam(required = false, defaultValue = "20") Integer size) {

        return ResponseEntity.ok().body(null); // Заглушка
    }

    @Operation(summary = "Получить список квартир с фильтрацией, сортировкой и постраничным выводом")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список квартир успешно получен",
                    content = @Content(schema = @Schema(implementation = FlatPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping()
    public ResponseEntity<FlatPageResponse> getFlatsPost(
            // Параметры фильтрации
            @Parameter(description = "Фильтр по названию") @RequestParam(required = false) String name,
            @Parameter(description = "Фильтр по минимальной площади") @RequestParam(required = false) Integer minArea,
            @Parameter(description = "Фильтр по максимальной площади") @RequestParam(required = false) Integer maxArea,
            @Parameter(description = "Фильтр по минимальному количеству комнат") @RequestParam(required = false) Integer minRooms,
            @Parameter(description = "Фильтр по максимальному количеству комнат") @RequestParam(required = false) Integer maxRooms,
            @Parameter(description = "Фильтр по типу отделки") @RequestParam(required = false) Furnish furnish,
            @Parameter(description = "Фильтр по транспорту") @RequestParam(required = false) Transport transport,

            // Параметры сортировки
            @Parameter(description = "Поле для сортировки (name, area, numberOfRooms, etc.)")
            @RequestParam(required = false) String sortBy,

            @Parameter(description = "Направление сортировки (asc, desc)")
            @RequestParam(required = false, defaultValue = "asc") String sortDirection,

            // Параметры пагинации
            @Parameter(description = "Номер страницы (начиная с 0)")
            @RequestParam(required = false, defaultValue = "0") Integer page,

            @Parameter(description = "Размер страницы")
            @RequestParam(required = false, defaultValue = "20") Integer size) {

        return ResponseEntity.ok().body(null); // Заглушка
    }

    //last three

    @Operation(summary = "Удалить квартиры по типу отделки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Квартиры успешно удалены"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/by_furnish/{furnish}")
    public ResponseEntity<Void> deleteByFurnish(
            @Parameter(description = "Тип отделки") @PathVariable Furnish furnish) {
        flats.values().removeIf(flat ->
            furnish.equals(flat.getFurnish()));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить квартиры с количеством комнат больше указанного")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список квартир получен",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Flat.class)))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/rooms_greater_than/{rooms}")
    public List<Flat> getFlatsWithMoreRooms(
            @Parameter(description = "Минимальное количество комнат") @PathVariable Integer rooms) {
        return flats.values().stream()
                .filter(flat -> flat.getNumberOfRooms() > rooms)
                .toList();
    }

    @Operation(summary = "Получить уникальные значения жилой площади")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уникальные значения жилой площади получены",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Double.class)))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/unique_living_spaces")
    public Set<Double> getUniqueLivingSpaces() {
        return flats.values().stream()
                .map(Flat::getLivingSpace)
                .collect(Collectors.toSet());
    }
}