package ru.itmo.labs.soa_lab1.repository.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "flats")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Квартира")
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Flat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(title = "ID квартиры", description = "Генерируется автоматически, должно быть больше 0 и уникальным",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    @Min(value = 1, message = "ID должен быть больше 0")
    private Long id;

    @JsonProperty("name")
    @Column(name = "name", nullable = false)
    @Schema(title = "Название квартиры", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Название не может быть пустым")
    @NotNull(message = "Название не может быть null")
    private String name;

    @JsonProperty("coordinates")
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "coordinates_id", referencedColumnName = "id", nullable = false)
    @Schema(title = "Координаты", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Координаты не могут быть null")
    private Coordinates coordinates;

    @Column(name = "creation_date", nullable = false, updatable = false)
    @Schema(title = "Дата создания", description = "Генерируется автоматически",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    @NotNull(message = "Дата создания не может быть null")
    private LocalDate creationDate;

    @JsonProperty("area")
    @Column(name = "area", nullable = false)
    @Schema(title = "Площадь", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Максимальное значение: 784, должно быть больше 0")
    @Min(value = 1, message = "Площадь должна быть больше 0")
    @Max(value = 784, message = "Площадь не может превышать 784")
    private int area;

    @JsonProperty("number_of_rooms")
    @Column(name = "number_of_rooms", nullable = false)
    @Schema(title = "Количество комнат", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Максимальное значение: 8, должно быть больше 0")
    @Min(value = 1, message = "Количество комнат должно быть больше 0")
    @Max(value = 8, message = "Количество комнат не может превышать 8")
    private int numberOfRooms;

    @JsonProperty("living_space")
    @Column(name = "living_space", nullable = false)
    @Schema(title = "Жилая площадь", requiredMode = Schema.RequiredMode.REQUIRED)
    @Positive(message = "Жилая площадь должна быть больше 0")
    private double livingSpace;

    @JsonProperty("furnish")
    @Enumerated(EnumType.STRING)
    @Column(name = "furnish")
    @Schema(title = "Отделка", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Furnish furnish;

    @JsonProperty("transport")
    @Enumerated(EnumType.STRING)
    @Column(name = "transport", nullable = false)
    @Schema(title = "Транспортная доступность", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Транспортная доступность не может быть null")
    private Transport transport;

    @JsonProperty("house")
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "house_id", referencedColumnName = "id", nullable = false)
    @Schema(title = "Дом", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Дом не может быть null")
    private House house;

    @Column(nullable = false)
    @Schema(title = "Цена", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Поле цена не может быть null")
    private double price;

    @Column(name = "has_balcony", nullable = false)
    @Schema(title = "Балкон>", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Поле балкон не может быть null")
    private boolean hasBalcony;

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDate.now();
        }
    }
}