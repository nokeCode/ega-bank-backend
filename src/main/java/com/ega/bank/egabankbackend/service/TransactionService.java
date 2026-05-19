package com.ega.bank.egabankbackend.service;


import com.ega.bank.egabankbackend.dto.*;
import com.ega.bank.egabankbackend.entity.Compte;
import com.ega.bank.egabankbackend.entity.Transaction;
import com.ega.bank.egabankbackend.enums.TransactionType;
import com.ega.bank.egabankbackend.exception.InsufficientBalanceException;
import com.ega.bank.egabankbackend.exception.InvalidOperationException;
import com.ega.bank.egabankbackend.repository.TransactionRepository;
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
public class TransactionService {

    private TransactionRepository transactionRepository;
    private CompteService accountService;

    /**
     * Effectuer un dépôt sur un compte
     */
    public TransactionDTO depot(DepotRequest request) {
        // Récupérer le compte
        Compte compte = accountService.getAccountEntityByNumero(request.getNumeroCompte());

        // Valider le montant
        if (request.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Le montant du dépôt doit être supérieur à 0");
        }

        // Créer la transaction
        Transaction transaction = Transaction.builder()
                .typeTransaction(TransactionType.DEPOT)
                .montant(request.getMontant())
                .dateTransaction(LocalDateTime.now())
                .compte(compte)
                .description(request.getDescription() != null ?
                        request.getDescription() : "Dépôt sur le compte")
                .build();

        // Mettre à jour le solde
        compte.setSolde(compte.getSolde().add(request.getMontant()));

        // Sauvegarder la transaction
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToDTO(savedTransaction);
    }

    /**
     * Effectuer un retrait sur un compte
     */
    public TransactionDTO retrait(RetraitRequest request) {
        // Récupérer le compte
        Compte compte = accountService.getAccountEntityByNumero(request.getNumeroCompte());

        // Valider le montant
        if (request.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Le montant du retrait doit être supérieur à 0");
        }

        // Vérifier le solde
        if (compte.getSolde().compareTo(request.getMontant()) < 0) {
            throw new InsufficientBalanceException(
                    "Solde insuffisant. Solde actuel : " + compte.getSolde() +
                            ", Montant demandé : " + request.getMontant());
        }

        // Créer la transaction
        Transaction transaction = Transaction.builder()
                .typeTransaction(TransactionType.RETRAIT)
                .montant(request.getMontant())
                .dateTransaction(LocalDateTime.now())
                .compte(compte)
                .description(request.getDescription() != null ?
                        request.getDescription() : "Retrait du compte")
                .build();

        // Mettre à jour le solde
        compte.setSolde(compte.getSolde().subtract(request.getMontant()));

        // Sauvegarder la transaction
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToDTO(savedTransaction);
    }

    /**
     * Effectuer un virement d'un compte à un autre
     */
    public TransactionDTO virement(VirementRequest request) {
        // Récupérer les comptes
        Compte compteSource = accountService.getAccountEntityByNumero(
                request.getNumeroCompteSource());
        Compte compteDestination = accountService.getAccountEntityByNumero(
                request.getNumeroCompteDestination());

        // Valider que les comptes sont différents
        if (request.getNumeroCompteSource().equals(request.getNumeroCompteDestination())) {
            throw new InvalidOperationException(
                    "Le compte source et le compte destination doivent être différents");
        }

        // Valider le montant
        if (request.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Le montant du virement doit être supérieur à 0");
        }

        // Vérifier le solde du compte source
        if (compteSource.getSolde().compareTo(request.getMontant()) < 0) {
            throw new InsufficientBalanceException(
                    "Solde insuffisant sur le compte source. Solde actuel : " +
                            compteSource.getSolde() + ", Montant demandé : " + request.getMontant());
        }

        // Créer la transaction de débit (compte source)
        Transaction transactionDebit = Transaction.builder()
                .typeTransaction(TransactionType.VIREMENT)
                .montant(request.getMontant())
                .dateTransaction(LocalDateTime.now())
                .compte(compteSource)
                .compteDestination(request.getNumeroCompteDestination())
                .description(request.getDescription() != null ?
                        request.getDescription() :
                        "Virement vers " + request.getNumeroCompteDestination())
                .build();

        // Créer la transaction de crédit (compte destination)
        Transaction transactionCredit = Transaction.builder()
                .typeTransaction(TransactionType.VIREMENT)
                .montant(request.getMontant())
                .dateTransaction(LocalDateTime.now())
                .compte(compteDestination)
                .compteDestination(request.getNumeroCompteSource())
                .description(request.getDescription() != null ?
                        request.getDescription() :
                        "Virement reçu de " + request.getNumeroCompteSource())
                .build();

        // Mettre à jour les soldes
        compteSource.setSolde(compteSource.getSolde().subtract(request.getMontant()));
        compteDestination.setSolde(compteDestination.getSolde().add(request.getMontant()));

        // Sauvegarder les transactions
        transactionRepository.save(transactionDebit);
        Transaction savedCredit = transactionRepository.save(transactionCredit);

        return mapToDTO(savedCredit);
    }

    /**
     * Récupérer toutes les transactions d'un compte
     */
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByAccount(String numeroCompte) {
        Compte compte = accountService.getAccountEntityByNumero(numeroCompte);
        return transactionRepository.findByCompteOrderByDateTransactionDesc(compte)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les transactions d'un compte pour une période donnée
     */
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByPeriod(TransactionFilterDTO filter) {
        // Valider les paramètres
        if (filter.getNumeroCompte() == null || filter.getNumeroCompte().isBlank()) {
            throw new InvalidOperationException("Le numéro de compte est obligatoire");
        }

        if (filter.getDateDebut() == null || filter.getDateFin() == null) {
            throw new InvalidOperationException("Les dates de début et de fin sont obligatoires");
        }

        if (filter.getDateDebut().isAfter(filter.getDateFin())) {
            throw new InvalidOperationException(
                    "La date de début doit être antérieure à la date de fin");
        }

        // Vérifier que le compte existe
        Compte compte = accountService.getAccountEntityByNumero(filter.getNumeroCompte());

        // Récupérer les transactions
        return transactionRepository.findByCompteAndDateBetween(
                        compte.getId(),
                        filter.getDateDebut(),
                        filter.getDateFin())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer toutes les transactions
     */
    @Transactional(readOnly = true)
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mapper une entité Transaction vers un TransactionDTO
     */
    private TransactionDTO mapToDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .typeTransaction(transaction.getTypeTransaction())
                .montant(transaction.getMontant())
                .dateTransaction(transaction.getDateTransaction())
                .numeroCompte(transaction.getCompte().getNumeroCompte())
                .compteDestination(transaction.getCompteDestination())
                .description(transaction.getDescription())
                .build();
    }
}