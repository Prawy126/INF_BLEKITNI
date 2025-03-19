package org.example.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LogisticianPanel {
    private BorderPane root;
    private Stage primaryStage;
    private LogisticianPanelController controller;

    // Konstruktor przyjmujący Stage
    public LogisticianPanel(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.controller = new LogisticianPanelController(this);

        primaryStage.setTitle("Panel logistyka");

        // Główna struktura układu
        root = new BorderPane();
        root.setPadding(new Insets(10));

        // Lewy panel nawigacyjny
        VBox menu = createMenu();
        root.setLeft(menu);

        // Domyślnie wyświetl widok raportów magazynowych
        controller.showInventoryReports();

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

        Button inventoryButton = new Button("Zarządzanie magazynem");
        inventoryButton.setOnAction(e -> controller.showInventoryManagement());

        Button ordersButton = new Button("Zamówienia");
        ordersButton.setOnAction(e -> controller.showOrdersPanel());

        Button reportsButton = new Button("Raporty magazynowe");
        reportsButton.setOnAction(e -> controller.showInventoryReports());

        Button logoutButton = new Button("Wyloguj");
        logoutButton.setOnAction(e -> controller.logout());

        menu.getChildren().addAll(inventoryButton, ordersButton, reportsButton, logoutButton);
        return menu;
    }

    public void setCenterPane(javafx.scene.layout.Pane pane) {
        root.setCenter(pane);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
