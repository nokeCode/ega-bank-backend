package com.ega.bank.egabankbackend.controller;

import com.ega.bank.egabankbackend.dto.*;
import com.ega.bank.egabankbackend.service.TransactionService;
import com.ega.bank.egabankbackend.service.PdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;
    private final PdfService pdfService;

    @PostMapping("/depot")
    public ResponseEntity<TransactionDTO> depot(@Valid @RequestBody DepotRequest request) {
        TransactionDTO transaction = transactionService.depot(request);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PostMapping("/retrait")
    public ResponseEntity<TransactionDTO> retrait(@Valid @RequestBody RetraitRequest request) {
        TransactionDTO transaction = transactionService.retrait(request);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PostMapping("/virement")
    public ResponseEntity<TransactionDTO> virement(@Valid @RequestBody VirementRequest request) {
        TransactionDTO transaction = transactionService.virement(request);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<TransactionDTO> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{numeroCompte}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByAccount(
            @PathVariable String numeroCompte) {
        List<TransactionDTO> transactions =
                transactionService.getTransactionsByAccount(numeroCompte);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByPeriod(
            @Valid @RequestBody TransactionFilterDTO filter) {
        List<TransactionDTO> transactions =
                transactionService.getTransactionsByPeriod(filter);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/releve")
    public ResponseEntity<byte[]> generateReleve(
            @Valid @RequestBody TransactionFilterDTO filter) {
        byte[] pdfContent = pdfService.generateReleve(filter);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "releve_" + filter.getNumeroCompte() + ".pdf");

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
}
