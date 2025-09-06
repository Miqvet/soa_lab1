package ru.itmo.labs.soa_lab1.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.itmo.labs.soa_lab1.repository.entity.Flat;

import java.util.List;

@Schema(description = "Ответ с пагинированным списком квартир")
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
@Data
public class FlatPageResponse {
    @Schema(description = "Список квартир на текущей странице", 
             requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Flat> flats;
    @Schema(description = "Номер текущей страницы (начиная с 0)", 
             minimum = "0", 
             requiredMode = Schema.RequiredMode.REQUIRED)
    private int page;
    @Schema(description = "Количество элементов на странице",
             requiredMode = Schema.RequiredMode.REQUIRED)
    private int size;
}