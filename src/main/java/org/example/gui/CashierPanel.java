package org.example.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CashierPanel {
    private BorderPane root;
    private Stage primaryStage;
    private CashierPanelController controller;

    public CashierPanel(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.controller = new CashierPanelController(this);
        primaryStage.setTitle("Panel kasjera");

        // Główna struktura układu
        root = new BorderPane();
        root.setPadding(new Insets(10));

        // Lewy panel nawigacyjny
        VBox menu = createMenu();
        root.setLeft(menu);

        // Domyślnie wyświetl ekran sprzedaży
        controller.showSalesScreen();

        // Scena
        Scene scene = new Scene(root, 700, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #E0E0E0;");
        menu.setAlignment(Pos.TOP_LEFT);

        Button salesButton = new Button("Ekran sprzedaży");
        salesButton.setOnAction(e -> controller.showSalesScreen());

        Button reportsButton = new Button("Raporty sprzedaży");
        reportsButton.setOnAction(e -> controller.showSalesReportsPanel());

        Button closeShiftButton = new Button("Zamknięcie zmiany");
        closeShiftButton.setOnAction(e -> controller.showCloseShiftPanel());

        Button issueReportButton = new Button("Zgłoszenie awarii");
        issueReportButton.setOnAction(e -> controller.showIssueReportPanel());

        Button logoutButton = new Button("Wyloguj się");
        logoutButton.setOnAction(e -> controller.logout());

        menu.getChildren().addAll(salesButton, reportsButton, closeShiftButton, issueReportButton, logoutButton);
        return menu;
    }

    public void setCenterPane(javafx.scene.layout.Pane pane) {
        root.setCenter(pane);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
