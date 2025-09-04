package ru.itmo.labs.soa_lab1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.labs.soa_lab1.repository.entity.Flat;
import ru.itmo.labs.soa_lab1.repository.entity.Furnish;

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
    @PostMapping
    public ResponseEntity<Flat> createFlat(@Valid @RequestBody Flat flat) {
        var newId = idCounter.getAndIncrement();
        flat.setId((int) newId);
        flat.setCreationDate(LocalDate.now());
        flats.put(newId, flat);
        return ResponseEntity.status(HttpStatus.CREATED).body(flat);
    }

    @Operation(summary = "Получить квартиру по ID")
    @GetMapping("/{id}")
    public ResponseEntity<Flat> getFlat(
            @Parameter(description = "ID квартиры") @PathVariable Long id) {
        return Optional.ofNullable(flats.get(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Обновить квартиру")
    @PutMapping("/{id}")
    public ResponseEntity<Flat> updateFlat(
            @Parameter(description = "ID квартиры") @PathVariable Long id,
            @Valid @RequestBody Flat flat) {
        if (!flats.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        
        var existingFlat = flats.get(id);
        flat.setId(existingFlat.getId());
        flat.setCreationDate(existingFlat.getCreationDate());
        
        flats.put(id, flat);
        return ResponseEntity.ok(flat);
    }

    @Operation(summary = "Удалить квартиру по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlat(
            @Parameter(description = "ID квартиры") @PathVariable Long id) {
        return Optional.ofNullable(flats.remove(id))
                .map(flat -> ResponseEntity.ok().<Void>build())
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить квартиры по типу отделки")
    @DeleteMapping("/by-furnish/{furnish}")
    public ResponseEntity<Void> deleteByFurnish(
            @Parameter(description = "Тип отделки") @PathVariable Furnish furnish) {
        flats.values().removeIf(flat -> 
            furnish.equals(flat.getFurnish()));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить квартиры с количеством комнат больше указанного")
    @GetMapping("/rooms-greater-than/{rooms}")
    public List<Flat> getFlatsWithMoreRooms(
            @Parameter(description = "Минимальное количество комнат") @PathVariable Integer rooms) {
        return flats.values().stream()
                .filter(flat -> flat.getNumberOfRooms() > rooms)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Получить уникальные значения жилой площади")
    @GetMapping("/unique-living-spaces")
    public Set<Double> getUniqueLivingSpaces() {
        return flats.values().stream()
                .map(Flat::getLivingSpace)
                .collect(Collectors.toSet());
    }

    private Object getFieldValue(Flat flat, String fieldName) {
        switch (fieldName) {
            case "id": return flat.getId();
            case "name": return flat.getName();
            case "area": return flat.getArea();
            case "numberOfRooms": return flat.getNumberOfRooms();
            case "livingSpace": return flat.getLivingSpace();
            case "furnish": return flat.getFurnish();
            case "transport": return flat.getTransport();
            default: return null;
        }
    }
}