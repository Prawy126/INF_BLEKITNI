package org.example.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class CashierPanelController {
    private final CashierPanel cashierPanel;

    public CashierPanelController(CashierPanel cashierPanel) {
        this.cashierPanel = cashierPanel;
    }

    public void showSalesScreen() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Button addToCartButton = new Button("Dodaj do koszyka");
        Button finalizeSaleButton = new Button("Finalizuj sprzedaż");
        // Usuwamy przycisk "Wyloguj się" z ekranu sprzedaży, bo jest już w lewym panelu
        // Button logoutButton = new Button("Wyloguj się");

        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(addToCartButton, finalizeSaleButton); // Brak przycisku logout

        cashierPanel.setCenterPane(layout);
    }

    public void showSalesReportsPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Button generatePDFButton = new Button("Generuj raport do PDF");
        Button generateCSVButton = new Button("Generuj raport do CSV");

        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(generatePDFButton, generateCSVButton);

        cashierPanel.setCenterPane(layout);
    }

    public void showCloseShiftPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Button closeShiftButton = new Button("Zamknij zmianę");
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(closeShiftButton);

        cashierPanel.setCenterPane(layout);
    }

    public void showIssueReportPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Button reportIssueButton = new Button("Zgłoś problem");
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(reportIssueButton);

        cashierPanel.setCenterPane(layout);
    }

    public void logout() {
        cashierPanel.getPrimaryStage().close();
    }
}
