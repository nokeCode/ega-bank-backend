package com.ega.bank.egabankbackend.dto;

import com.ega.bank.egabankbackend.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private Long id;

    @NotNull(message = "Le type de transaction est obligatoire")
    private TransactionType typeTransaction;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal montant;

    private LocalDateTime dateTransaction;

    @NotNull(message = "Le numéro de compte est obligatoire")
    private String numeroCompte;

    private String compteDestination;
    private String description;
}

