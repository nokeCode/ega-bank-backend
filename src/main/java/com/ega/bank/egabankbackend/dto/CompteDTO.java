package com.ega.bank.egabankbackend.dto;

import com.ega.bank.egabankbackend.enums.CompteType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompteDTO {
    private Long id;
    private String numeroCompte;

    @NotNull(message = "Le type de compte est obligatoire")
    private CompteType typeCompte;

    private LocalDateTime dateCreation;
    private BigDecimal solde;

    @NotNull(message = "L'ID du propriétaire est obligatoire")
    private Long proprietaireId;

    private String proprietaireNom;
}

