package ru.itmo.labs.soa_lab1.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "coordinates")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Координаты")
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Coordinates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(title = "ID координат", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @JsonProperty("x")
    @Column(name = "x", nullable = false)
    @Schema(title = "Координата X", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Значение должно быть больше -13. Поле не может быть null")
    @NotNull(message = "Координата X не может быть null")
    @DecimalMin(value = "-12.999", inclusive = false, message = "Координата X должна быть больше -13")
    private Long x;

    @JsonProperty("y")
    @Column(name = "y", nullable = false)
    @Schema(title = "Координата Y", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Значение должно быть больше -733")
    @DecimalMin(value = "-732.999", inclusive = false, message = "Координата Y должна быть больше -733")
    private float y;

    @OneToOne(mappedBy = "coordinates")
    @JsonIgnore
    private Flat flat;
}