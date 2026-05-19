package com.ega.bank.egabankbackend.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VirementRequest {
    @NotBlank(message = "Le numéro de compte source est obligatoire")
    private String numeroCompteSource;

    @NotBlank(message = "Le numéro de compte destination est obligatoire")
    private String numeroCompteDestination;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal montant;

    private String description;
}