package com.ega.bank.egabankbackend.util;

import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class IbanGenerator {

    private final Random random = new Random();

    /**
     * Génère un numéro IBAN valide
     * @return Un numéro IBAN sous forme de chaîne
     */
    public String generateIban() {
        try {
            // Générer un IBAN pour le Togo (TG) ou un autre pays
            Iban iban = new Iban.Builder()
                    .countryCode(CountryCode.FR) // Vous pouvez changer pour TG si disponible
                    .bankCode(generateRandomBankCode())
                    .branchCode(generateRandomBranchCode())
                    .accountNumber(generateRandomAccountNumber())
                    .build();

            return iban.toString();
        } catch (Exception e) {
            // En cas d'erreur, générer un numéro personnalisé
            return generateCustomAccountNumber();
        }
    }

    /**
     * Génère un numéro de compte personnalisé
     */
    private String generateCustomAccountNumber() {
        StringBuilder accountNumber = new StringBuilder("EGA");
        accountNumber.append(String.format("%014d", random.nextInt(1000000000)));
        return accountNumber.toString();
    }

    private String generateRandomBankCode() {
        return String.format("%05d", random.nextInt(100000));
    }

    private String generateRandomBranchCode() {
        return String.format("%05d", random.nextInt(100000));
    }

    private String generateRandomAccountNumber() {
        return String.format("%011d", random.nextInt(Integer.MAX_VALUE));
    }
}
