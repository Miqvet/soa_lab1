package ru.itmo.labs.soa_lab1.repository.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
@Data
public class Flat {

    @Schema(title = "ID квартиры", description = "Генерируется автоматически, должно быть больше 0 и уникальным",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    @Min(value = 1, message = "ID должен быть больше 0")
    private int id;

    @JsonProperty("name")
    @Schema(title = "Название квартиры", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Название не может быть пустым")
    @NotNull(message = "Название не может быть null")
    private String name;

    @JsonProperty("coordinates")
    @Schema(title = "Координаты", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Координаты не могут быть null")
    private Coordinates coordinates;

    @Schema(title = "Дата создания", description = "Генерируется автоматически",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    @NotNull(message = "Дата создания не может быть null")
    private LocalDate creationDate;

    @JsonProperty("area")
    @Schema(title = "Площадь", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Максимальное значение: 784, должно быть больше 0")
    @Min(value = 1, message = "Площадь должна быть больше 0")
    @Max(value = 784, message = "Площадь не может превышать 784")
    private int area;

    @JsonProperty("number_of_rooms")
    @Schema(title = "Количество комнат", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Максимальное значение: 8, должно быть больше 0")
    @Min(value = 1, message = "Количество комнат должно быть больше 0")
    @Max(value = 8, message = "Количество комнат не может превышать 8")
    private int numberOfRooms;

    @JsonProperty("living_space")
    @Schema(title = "Жилая площадь", requiredMode = Schema.RequiredMode.REQUIRED)
    @Positive(message = "Жилая площадь должна быть больше 0")
    private double livingSpace;

    @JsonProperty("furnish")
    @Schema(title = "Отделка", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Furnish furnish;

    @JsonProperty("transport")
    @Schema(title = "Транспортная доступность", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Транспортная доступность не может быть null")
    private Transport transport;

    @JsonProperty("house")
    @Schema(title = "Дом", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Дом не может быть null")
    private House house;
}