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

@Embeddable
@Getter
@Setter(value = AccessLevel.PACKAGE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {
    @NotNull
    @DecimalMin(value = "-12.999", inclusive = true)
    private Long x;

    @NotNull
    @DecimalMin(value = "-732.999", inclusive = true)
    private Float y;
}