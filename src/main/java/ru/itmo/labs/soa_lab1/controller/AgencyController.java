package ru.itmo.labs.soa_lab1.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/agency")
public class AgencyController {
    @Operation(summary = "Найти самую дешёвую/дорогую квартиру с балконом/без балкона")
    @GetMapping("/find-with-balcony/{cheapest}/{withBalcony}")
    public ResponseEntity<Map<String, Object>> findWithBalcony(
            @PathVariable Boolean cheapest,
            @PathVariable Boolean withBalcony) {
        // Заглушка реализации
        return ResponseEntity.ok().body(Map.of(
            "message", "Функция в разработке",
            "cheapest", cheapest,
            "withBalcony", withBalcony
        ));
    }

    @Operation(summary = "Выбрать наиболее дорогую квартиру из трёх")
    @GetMapping("/get-most-expensive/{id1}/{id2}/{id3}")
    public ResponseEntity<Map<String, Object>> getMostExpensive(
            @PathVariable Long id1,
            @PathVariable Long id2,
            @PathVariable Long id3) {
        // Заглушка реализации
        return ResponseEntity.ok().body(Map.of(
            "message", "Функция в разработке",
            "comparedFlats", List.of(id1, id2, id3)
        ));
    }
}