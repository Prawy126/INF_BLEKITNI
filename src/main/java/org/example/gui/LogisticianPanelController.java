/*
 * Classname: LogisticianPanelController
 * Version information: 1.1
 * Date: 2025-04-11
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Kontroler obsługujący logikę interfejsu użytkownika
 * dla panelu logistyka.
 */
public class LogisticianPanelController {

    private final LogisticianPanel logisticianPanel;
    private final Stage primaryStage;

    /**
     * Konstruktor przypisujący panel logistyka.
     *
     * @param logisticianPanel obiekt klasy LogisticianPanel
     */
    public LogisticianPanelController(LogisticianPanel logisticianPanel) {
        this.logisticianPanel = logisticianPanel;
        this.primaryStage = logisticianPanel.getPrimaryStage();
    }

    /**
     * Wyświetla ekran zarządzania magazynem.
     */
    public void showInventoryManagement() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Zarządzanie magazynem");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<?> tableView = new TableView<>();
        tableView.setMinHeight(200);

        Button addProductButton = new Button("Dodaj produkt");
        Button filterButton = new Button("Filtruj");
        Button reportsButton = new Button("Raporty");

        layout.getChildren().addAll(
                titleLabel,
                tableView,
                addProductButton,
                filterButton,
                reportsButton
        );

        logisticianPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla panel zarządzania zamówieniami.
     */
    public void showOrdersPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Zamówienia");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<?> tableView = new TableView<>();
        tableView.setMinHeight(200);

        Button addOrderButton = new Button("Dodaj zamówienie");
        Button filterButton = new Button("Filtruj");
        Button backButton = new Button("Wróć");

        layout.getChildren().addAll(
                titleLabel,
                tableView,
                addOrderButton,
                filterButton,
                backButton
        );

        logisticianPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla ekran raportów magazynowych.
     */
    public void showInventoryReports() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Raporty magazynowe");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<?> tableView = new TableView<>();
        tableView.setMinHeight(200);

        Button generatePDFButton = new Button("Generuj raport do PDF");
        Button generateCSVButton = new Button("Generuj raport do CSV");
        Button backButton = new Button("Wróć");

        layout.getChildren().addAll(
                titleLabel,
                tableView,
                generatePDFButton,
                generateCSVButton,
                backButton
        );

        logisticianPanel.setCenterPane(layout);
    }

    /**
     * Zamyka okno aplikacji (wylogowanie).
     */
    public void logout() {
        primaryStage.close();

        Stage loginStage = new Stage();
        try {
            new HelloApplication().start(loginStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
