package org.example.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LogisticianPanelController {
    private final LogisticianPanel logisticianPanel;
    private final Stage primaryStage;

    public LogisticianPanelController(LogisticianPanel logisticianPanel) {
        this.logisticianPanel = logisticianPanel;
        this.primaryStage = logisticianPanel.getPrimaryStage();
    }

    public void showInventoryManagement() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Zarządzanie magazynem");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView tableView = new TableView();
        tableView.setMinHeight(200);

        Button addProductButton = new Button("Dodaj produkt");
        Button filterButton = new Button("Filtruj");
        Button reportsButton = new Button("Raporty");

        layout.getChildren().addAll(titleLabel, tableView, addProductButton, filterButton, reportsButton);
        logisticianPanel.setCenterPane(layout);
    }

    public void showOrdersPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Zamówienia");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView tableView = new TableView();
        tableView.setMinHeight(200);

        Button addOrderButton = new Button("Dodaj zamówienie");
        Button filterButton = new Button("Filtruj");
        Button backButton = new Button("Wróć");

        layout.getChildren().addAll(titleLabel, tableView, addOrderButton, filterButton, backButton);
        logisticianPanel.setCenterPane(layout);
    }

    public void showInventoryReports() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Raporty magazynowe");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView tableView = new TableView();
        tableView.setMinHeight(200);

        Button generatePDFButton = new Button("Generuj raport do PDF");
        Button generateCSVButton = new Button("Generuj raport do CSV");
        Button backButton = new Button("Wróć");

        layout.getChildren().addAll(titleLabel, tableView, generatePDFButton, generateCSVButton, backButton);
        logisticianPanel.setCenterPane(layout);
    }

    public void logout() {
        primaryStage.close();
    }
}
