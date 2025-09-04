package ru.itmo.labs.soa_lab1.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.labs.soa_lab1.repository.entity.Flat;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/flats")
public class FlatController {

    private final Map<Long, Map<String, Object>> flats = new HashMap<>();
    private long idCounter = 1;

    @Operation(summary = "Добавить новую квартиру")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createFlat(
            @RequestBody Map<String, Object> flat) {
        flat.put("id", idCounter);
        flats.put(idCounter, flat);
        return ResponseEntity.ok(flat);
    }

    @Operation(summary = "Получить квартиру по ID")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFlat(
            @PathVariable Long id) {
        return Optional.ofNullable(flats.get(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Обновить квартиру")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateFlat(
            @PathVariable Long id,
            @RequestBody Map<String, Object> flat) {
        if (!flats.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        flat.put("id", id);
        flats.put(id, flat);
        return ResponseEntity.ok(flat);
    }

    @Operation(summary = "Удалить квартиру по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlat(@PathVariable Long id) {
        return Optional.ofNullable(flats.remove(id))
                .map(flat -> ResponseEntity.ok().<Void>build())
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить квартиры по типу furnishings")
    @DeleteMapping("/by-furnish/{furnish}")
    public ResponseEntity<Void> deleteByFurnish(
            @PathVariable String furnish) {
        flats.values().removeIf(flat -> 
            furnish.equals(flat.get("furnish")));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить квартиры с количеством комнат больше указанного")
    @GetMapping("/rooms-more-than/{rooms}")
    public List<Map<String, Object>> getFlatsWithMoreRooms(
            @PathVariable Integer rooms) {
        return flats.values().stream()
                .filter(flat -> {
                    Object roomCount = flat.get("numberOfRooms");
                    return roomCount instanceof Integer &&
                           (Integer) roomCount > rooms;
                })
                .collect(Collectors.toList());
    }

    @Operation(summary = "Получить уникальные значения livingSpace")
    @GetMapping("/unique-living-spaces")
    public Set<Object> getUniqueLivingSpaces() {
        return flats.values().stream()
                .map(flat -> flat.get("livingSpace"))
                .collect(Collectors.toSet());
    }

    @Operation(summary = "Тестовый контроллер для отображения")
    @GetMapping("/flats")
    public Flat test() {
        return null;
    }
}