package com.ega.bank.egabankbackend.service;

import com.ega.bank.egabankbackend.dto.CompteDTO;
import com.ega.bank.egabankbackend.entity.Compte;
import com.ega.bank.egabankbackend.entity.Client;
import com.ega.bank.egabankbackend.exception.ResourceNotFoundException;
import com.ega.bank.egabankbackend.repository.CompteRepository;
import com.ega.bank.egabankbackend.repository.ClientRepository;
import com.ega.bank.egabankbackend.util.IbanGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CompteService {

    private CompteRepository accountRepository;
    private ClientRepository clientRepository;
    private IbanGenerator ibanGenerator;

    /**
     * Créer un nouveau compte
     */
    public CompteDTO createAccount(CompteDTO accountDTO) {
        // Vérifier que le client existe
        Client client = clientRepository.findById(accountDTO.getProprietaireId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client non trouvé avec l'ID : " + accountDTO.getProprietaireId()));

        // Générer un numéro de compte unique
        String numeroCompte = generateUniqueAccountNumber();

        // Créer le compte
        Compte account = Compte.builder()
                .numeroCompte(numeroCompte)
                .typeCompte(accountDTO.getTypeCompte())
                .dateCreation(LocalDateTime.now())
                .solde(BigDecimal.ZERO)
                .proprietaire(client)
                .build();

        Compte savedAccount = accountRepository.save(account);
        return mapToDTO(savedAccount);
    }

    /**
     * Récupérer tous les comptes
     */
    @Transactional(readOnly = true)
    public List<CompteDTO> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un compte par ID
     */
    @Transactional(readOnly = true)
    public CompteDTO getAccountById(Long id) {
        Compte account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Compte non trouvé avec l'ID : " + id));
        return mapToDTO(account);
    }

    /**
     * Récupérer un compte par numéro
     */
    @Transactional(readOnly = true)
    public CompteDTO getAccountByNumero(String numeroCompte) {
        Compte account = accountRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Compte non trouvé avec le numéro : " + numeroCompte));
        return mapToDTO(account);
    }

    /**
     * Récupérer tous les comptes d'un client
     */
    @Transactional(readOnly = true)
    public List<CompteDTO> getAccountsByClientId(Long clientId) {
        // Vérifier que le client existe
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client non trouvé avec l'ID : " + clientId);
        }

        return accountRepository.findByProprietaireId(clientId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour un compte
     */
    public CompteDTO updateAccount(Long id, CompteDTO accountDTO) {
        Compte account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Compte non trouvé avec l'ID : " + id));

        // Seul le type de compte peut être modifié
        account.setTypeCompte(accountDTO.getTypeCompte());

        Compte updatedAccount = accountRepository.save(account);
        return mapToDTO(updatedAccount);
    }

    /**
     * Supprimer un compte
     */
    public void deleteAccount(Long id) {
        Compte account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Compte non trouvé avec l'ID : " + id));
        accountRepository.delete(account);
    }

    /**
     * Générer un numéro de compte unique
     */
    private String generateUniqueAccountNumber() {
        String numeroCompte;
        do {
            numeroCompte = ibanGenerator.generateIban();
        } while (accountRepository.existsByNumeroCompte(numeroCompte));
        return numeroCompte;
    }

    /**
     * Récupérer une entité Account (utilisé par TransactionService)
     */
    @Transactional(readOnly = true)
    public Compte getAccountEntityByNumero(String numeroCompte) {
        return accountRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Compte non trouvé avec le numéro : " + numeroCompte));
    }

    /**
     * Mapper une entité Account vers un AccountDTO
     */
    private CompteDTO mapToDTO(Compte account) {
        return CompteDTO.builder()
                .id(account.getId())
                .numeroCompte(account.getNumeroCompte())
                .typeCompte(account.getTypeCompte())
                .dateCreation(account.getDateCreation())
                .solde(account.getSolde())
                .proprietaireId(account.getProprietaire().getId())
                .proprietaireNom(account.getProprietaire().getNom() + " " +
                        account.getProprietaire().getPrenom())
                .build();
    }
}