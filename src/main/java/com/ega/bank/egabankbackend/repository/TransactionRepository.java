package com.ega.bank.egabankbackend.repository;

import com.ega.bank.egabankbackend.entity.Compte;
import com.ega.bank.egabankbackend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCompteOrderByDateTransactionDesc(Compte compte);

    @Query("SELECT t FROM Transaction t WHERE t.compte.id = :compteId " +
            "AND t.dateTransaction BETWEEN :dateDebut AND :dateFin " +
            "ORDER BY t.dateTransaction DESC")
    List<Transaction> findByCompteAndDateBetween(
            @Param("compteId") Long compteId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );

    List<Transaction> findByCompteIdOrderByDateTransactionDesc(Long compteId);
}