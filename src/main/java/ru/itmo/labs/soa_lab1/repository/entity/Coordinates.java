package ru.itmo.labs.soa_lab1.repository.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Coordinates {

    @JsonProperty("x")
    @Schema(title = "Координата X", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Значение должно быть больше -13. Поле не может быть null")
    @NotNull(message = "Координата X не может быть null")
    @DecimalMin(value = "-12.999", inclusive = false, message = "Координата X должна быть больше -13")
    private Long x;

    @JsonProperty("y")
    @Schema(title = "Координата Y", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Значение должно быть больше -733")
    @DecimalMin(value = "-732.999", inclusive = false, message = "Координата Y должна быть больше -733")
    private float y;
}