package ru.itmo.labs.soa_lab1.repository.entity;

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
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Дом")
@AllArgsConstructor
@Builder
@Data
public class House {

    @JsonProperty("name")
    @Schema(title = "Имя дома", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name; // Поле может быть null

    @JsonProperty("year")
    @Schema(title = "Год", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Год не может быть null")
    @Min(value = 1, message = "Год должен быть больше 0")
    @Max(value = 370, message = "Год не может превышать 370")
    private Long year; // Изменил на Long для лучшей обработки null

    @JsonProperty("number_of_floors")
    @Schema(title = "Количество этажей", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Количество этажей не может быть null")
    @Positive(message = "Количество этажей должно быть положительным числом")
    @Min(value = 1, message = "Количество этажей должно быть не менее 1")
    private Integer numberOfFloors;

    @JsonProperty("number_of_lifts")
    @Schema(title = "Количество лифтов", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Количество лифтов не может быть null")
    @Positive(message = "Количество лифтов должно быть положительным числом")
    @Min(value = 1, message = "Количество лифтов должно быть не менее 1")
    private Integer numberOfLifts;
}