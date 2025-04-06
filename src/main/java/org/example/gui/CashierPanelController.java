/*
 * Classname: CashierPanelController
 * Version information: 1.0
 * Date: 2025-04-06
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Kontroler logiki interfejsu użytkownika dla panelu kasjera.
 * Odpowiada za obsługę przełączania widoków.
 */
public class CashierPanelController {

    private final CashierPanel cashierPanel;

    /**
     * Tworzy kontroler dla panelu kasjera.
     *
     * @param cashierPanel główny panel kasjera
     */
    public CashierPanelController(CashierPanel cashierPanel) {
        this.cashierPanel = cashierPanel;
    }

    /**
     * Wyświetla ekran sprzedaży z przyciskami do dodawania produktów
     * i finalizacji transakcji.
     */
    public void showSalesScreen() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Button addToCartButton = new Button("Dodaj do koszyka");
        Button finalizeSaleButton = new Button("Finalizuj sprzedaż");

        layout.getChildren().addAll(addToCartButton, finalizeSaleButton);
        cashierPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla panel raportów sprzedaży z opcją eksportu do PDF lub CSV.
     */
    public void showSalesReportsPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Button generatePDFButton = new Button("Generuj raport do PDF");
        Button generateCSVButton = new Button("Generuj raport do CSV");

        layout.getChildren().addAll(generatePDFButton, generateCSVButton);
        cashierPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla ekran umożliwiający zamknięcie zmiany.
     */
    public void showCloseShiftPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Button closeShiftButton = new Button("Zamknij zmianę");

        layout.getChildren().addAll(closeShiftButton);
        cashierPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla formularz do zgłoszenia problemu lub awarii.
     */
    public void showIssueReportPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Button reportIssueButton = new Button("Zgłoś problem");

        layout.getChildren().addAll(reportIssueButton);
        cashierPanel.setCenterPane(layout);
    }

    /**
     * Zamyka aplikację po wylogowaniu użytkownika.
     */
    public void logout() {
        cashierPanel.getPrimaryStage().close();
    }
}
