package com.ega.bank.egabankbackend.service;

import com.ega.bank.egabankbackend.dto.CompteDTO;
import com.ega.bank.egabankbackend.dto.TransactionDTO;
import com.ega.bank.egabankbackend.dto.TransactionFilterDTO;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class PdfService {

    private CompteService accountService;
    private TransactionService transactionService;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Générer un relevé de compte au format PDF
     */
    public byte[] generateReleve(TransactionFilterDTO filter) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Récupérer les informations du compte
            CompteDTO account = accountService.getAccountByNumero(filter.getNumeroCompte());

            // Récupérer les transactions
            List<TransactionDTO> transactions = transactionService.getTransactionsByPeriod(filter);

            // En-tête du document
            addHeader(document, account, filter);

            // Tableau des transactions
            addTransactionsTable(document, transactions);

            // Pied de page
            addFooter(document, account);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }

    /**
     * Ajouter l'en-tête du relevé
     */
    private void addHeader(Document document, CompteDTO account, TransactionFilterDTO filter) {
        // Titre
        Paragraph title = new Paragraph("BANQUE EGA")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(title);

        Paragraph subtitle = new Paragraph("RELEVÉ DE COMPTE")
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subtitle);

        // Informations du compte
        document.add(new Paragraph("Numéro de compte : " + account.getNumeroCompte())
                .setFontSize(12));
        document.add(new Paragraph("Titulaire : " + account.getProprietaireNom())
                .setFontSize(12));
        document.add(new Paragraph("Type de compte : " + account.getTypeCompte())
                .setFontSize(12));
        document.add(new Paragraph("Solde actuel : " + account.getSolde() + " FCFA")
                .setFontSize(12)
                .setBold());

        // Période
        document.add(new Paragraph("Période : du " +
                filter.getDateDebut().format(DATE_FORMATTER) + " au " +
                filter.getDateFin().format(DATE_FORMATTER))
                .setFontSize(11)
                .setMarginTop(10)
                .setMarginBottom(20));
    }

    /**
     * Ajouter le tableau des transactions
     */
    private void addTransactionsTable(Document document, List<TransactionDTO> transactions) {
        // Créer le tableau
        float[] columnWidths = {2, 2, 3, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // En-têtes du tableau
        addTableHeader(table);

        // Lignes de données
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (TransactionDTO transaction : transactions) {
            addTableRow(table, transaction);

            // Calculer les totaux
            if (transaction.getTypeTransaction().toString().equals("RETRAIT") ||
                    (transaction.getTypeTransaction().toString().equals("VIREMENT") &&
                            transaction.getCompteDestination() != null)) {
                totalDebit = totalDebit.add(transaction.getMontant());
            } else {
                totalCredit = totalCredit.add(transaction.getMontant());
            }
        }

        document.add(table);

        // Totaux
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Total Crédit : " + totalCredit + " FCFA")
                .setBold()
                .setFontSize(11));
        document.add(new Paragraph("Total Débit : " + totalDebit + " FCFA")
                .setBold()
                .setFontSize(11));
    }

    /**
     * Ajouter les en-têtes du tableau
     */
    private void addTableHeader(Table table) {
        String[] headers = {"Date", "Type", "Description", "Montant (FCFA)"};

        for (String header : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell(cell);
        }
    }

    /**
     * Ajouter une ligne au tableau
     */
    private void addTableRow(Table table, TransactionDTO transaction) {
        table.addCell(new Cell()
                .add(new Paragraph(transaction.getDateTransaction().format(DATE_FORMATTER)))
                .setFontSize(10));

        table.addCell(new Cell()
                .add(new Paragraph(transaction.getTypeTransaction().toString()))
                .setFontSize(10));

        table.addCell(new Cell()
                .add(new Paragraph(transaction.getDescription() != null ?
                        transaction.getDescription() : ""))
                .setFontSize(10));

        table.addCell(new Cell()
                .add(new Paragraph(transaction.getMontant().toString()))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT));
    }

    /**
     * Ajouter le pied de page
     */
    private void addFooter(Document document, CompteDTO account) {
        document.add(new Paragraph("\n\n"));
        document.add(new Paragraph("Document généré le " +
                java.time.LocalDateTime.now().format(DATE_FORMATTER))
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setItalic());

        document.add(new Paragraph("Banque EGA - Votre partenaire financier de confiance")
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setItalic());
    }
}