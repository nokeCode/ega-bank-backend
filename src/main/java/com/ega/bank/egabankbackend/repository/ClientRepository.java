package com.ega.bank.egabankbackend.repository;

import com.ega.bank.egabankbackend.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByCourriel(String courriel);
    boolean existsByCourriel(String courriel);
    boolean existsByNumeroTelephone(String numeroTelephone);
}
