package com.ega.bank.egabankbackend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFilterDTO {
    private String numeroCompte;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
}
