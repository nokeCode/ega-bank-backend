package com.ega.bank.egabankbackend.repository;

import com.ega.bank.egabankbackend.entity.Compte;
import com.ega.bank.egabankbackend.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {
    Optional<Compte> findByNumeroCompte(String numeroCompte);
    List<Compte> findByProprietaire(Client client);
    List<Compte> findByProprietaireId(Long clientId);
    boolean existsByNumeroCompte(String numeroCompte);
}
