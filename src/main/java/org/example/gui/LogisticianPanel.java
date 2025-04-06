/*
 * Classname: LogisticianPanel
 * Version information: 1.0
 * Date: 2025-04-06
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Klasa reprezentująca panel logistyka w aplikacji GUI.
 */
public class LogisticianPanel {

    private BorderPane root;
    private Stage primaryStage;
    private LogisticianPanelController controller;

    /**
     * Konstruktor klasy LogisticianPanel.
     *
     * @param primaryStage główna scena przypisana do panelu
     */
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

        // Inicjalizacja sceny
        Scene scene = new Scene(root, 700, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Tworzy menu boczne z przyciskami nawigacyjnymi.
     *
     * @return VBox zawierający menu
     */
    private VBox createMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setAlignment(Pos.TOP_LEFT);
        menu.setStyle("-fx-background-color: #E0E0E0;");

        Button inventoryButton = new Button("Zarządzanie magazynem");
        inventoryButton.setOnAction(e -> controller.showInventoryManagement());

        Button ordersButton = new Button("Zamówienia");
        ordersButton.setOnAction(e -> controller.showOrdersPanel());

        Button reportsButton = new Button("Raporty magazynowe");
        reportsButton.setOnAction(e -> controller.showInventoryReports());

        Button logoutButton = new Button("Wyloguj");
        logoutButton.setOnAction(e -> controller.logout());

        menu.getChildren().addAll(
                inventoryButton,
                ordersButton,
                reportsButton,
                logoutButton
        );

        return menu;
    }

    /**
     * Ustawia panel centralny w układzie głównym.
     *
     * @param pane nowy panel do wyświetlenia
     */
    public void setCenterPane(javafx.scene.layout.Pane pane) {
        root.setCenter(pane);
    }

    /**
     * Zwraca główną scenę przypisaną do panelu logistyka.
     *
     * @return obiekt Stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
