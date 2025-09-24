package ru.itmo.labs.soa_lab1.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "houses")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Дом")
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(title = "ID дома", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @JsonProperty("name")
    @Column(name = "name")
    @Schema(title = "Имя дома", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name; // Поле может быть null

    @JsonProperty("year")
    @Column(name = "year", nullable = false)
    @Schema(title = "Год", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Год не может быть null")
    @Min(value = 1, message = "Год должен быть больше 0")
    @Max(value = 2025, message = "Год не может превышать 370")
    private Long year;

    @JsonProperty("number_of_floors")
    @Column(name = "number_of_floors", nullable = false)
    @Schema(title = "Количество этажей", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Количество этажей не может быть null")
    @Positive(message = "Количество этажей должно быть положительным числом")
    @Min(value = 1, message = "Количество этажей должно быть не менее 1")
    private Integer numberOfFloors;

    @JsonProperty("number_of_lifts")
    @Column(name = "number_of_lifts", nullable = false)
    @Schema(title = "Количество лифтов", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Количество лифтов не может быть null")
    @Positive(message = "Количество лифтов должно быть положительным числом")
    @Min(value = 1, message = "Количество лифтов должно быть не менее 1")
    private Integer numberOfLifts;

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Flat> flats = new ArrayList<>();
}