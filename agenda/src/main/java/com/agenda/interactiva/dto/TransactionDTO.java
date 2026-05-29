package com.agenda.interactiva.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para la transferencia de información de Transacciones.
 */
@Getter
@Setter
public class TransactionDTO {

    private Long id;

    @NotBlank(message = "El tipo de transacción es obligatorio.")
    private String type; // 'INCOME' o 'EXPENSE'

    @NotNull(message = "La cantidad es obligatoria.")
    @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor que cero.")
    private BigDecimal amount;

    @NotBlank(message = "La categoría es obligatoria.")
    private String category;

    private String description;

    @NotNull(message = "La fecha es obligatoria.")
    private LocalDate date;
}
