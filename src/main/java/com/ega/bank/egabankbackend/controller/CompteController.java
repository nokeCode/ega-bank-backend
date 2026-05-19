package com.ega.bank.egabankbackend.controller;

import com.ega.bank.egabankbackend.dto.CompteDTO;
import com.ega.bank.egabankbackend.service.CompteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompteController {

    private final CompteService accountService;

    @PostMapping
    public ResponseEntity<CompteDTO> createAccount(@Valid @RequestBody CompteDTO accountDTO) {
        CompteDTO created = accountService.createAccount(accountDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CompteDTO>> getAllAccounts() {
        List<CompteDTO> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompteDTO> getAccountById(@PathVariable Long id) {
        CompteDTO account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/numero/{numeroCompte}")
    public ResponseEntity<CompteDTO> getAccountByNumero(@PathVariable String numeroCompte) {
        CompteDTO account = accountService.getAccountByNumero(numeroCompte);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<CompteDTO>> getAccountsByClientId(@PathVariable Long clientId) {
        List<CompteDTO> accounts = accountService.getAccountsByClientId(clientId);
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompteDTO> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody CompteDTO accountDTO) {
        CompteDTO updated = accountService.updateAccount(id, accountDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
