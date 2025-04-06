/*
 * Classname: OrderPanel
 * Version information: 1.0
 * Date: 2025-04-06
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Klasa reprezentująca panel do zarządzania zamówieniami.
 */
public class OrderPanel extends VBox {

    private TableView<Order> ordersTable;
    private ObservableList<Order> ordersData;

    /**
     * Konstruktor inicjalizujący interfejs i dane.
     */
    public OrderPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        initializeUI();
        loadSampleData();
    }

    /**
     * Inicjalizuje komponenty interfejsu użytkownika.
     */
    private void initializeUI() {
        Label titleLabel = new Label("Panel Zamówień");
        titleLabel.setStyle(
                "-fx-font-size: 18px; -fx-font-weight: bold;"
        );

        ordersTable = new TableView<>();

        TableColumn<Order, Integer> idCol =
                new TableColumn<>("ID");
        idCol.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        TableColumn<Order, String> productCol =
                new TableColumn<>("Produkt");
        productCol.setCellValueFactory(
                new PropertyValueFactory<>("productName")
        );
        productCol.setPrefWidth(150);

        TableColumn<Order, Integer> quantityCol =
                new TableColumn<>("Ilość");
        quantityCol.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );

        TableColumn<Order, String> supplierCol =
                new TableColumn<>("Dostawca");
        supplierCol.setCellValueFactory(
                new PropertyValueFactory<>("supplier")
        );
        supplierCol.setPrefWidth(150);

        TableColumn<Order, String> dateCol =
                new TableColumn<>("Data zamówienia");
        dateCol.setCellValueFactory(
                new PropertyValueFactory<>("orderDate")
        );

        TableColumn<Order, String> statusCol =
                new TableColumn<>("Status");
        statusCol.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        ordersTable.getColumns().addAll(
                idCol,
                productCol,
                quantityCol,
                supplierCol,
                dateCol,
                statusCol
        );

        Button newOrderButton = new Button("Nowe zamówienie");
        newOrderButton.setOnAction(
                e -> showNewOrderDialog()
        );

        Button editOrderButton = new Button("Edytuj");
        editOrderButton.setOnAction(
                e -> editSelectedOrder()
        );

        Button cancelOrderButton = new Button("Anuluj");
        cancelOrderButton.setOnAction(
                e -> cancelSelectedOrder()
        );

        Button receiveOrderButton = new Button("Przyjmij dostawę");
        receiveOrderButton.setOnAction(
                e -> receiveSelectedOrder()
        );

        HBox buttonsBox = new HBox(
                10,
                newOrderButton,
                editOrderButton,
                cancelOrderButton,
                receiveOrderButton
        );

        getChildren().addAll(titleLabel, ordersTable, buttonsBox);
    }

    /**
     * Załadowanie przykładowych danych do tabeli.
     */
    private void loadSampleData() {
        ordersData = FXCollections.observableArrayList(
                new Order(1, "Mleko 1L", 50,
                        "Mlekovita", "2023-05-15", "Oczekujące"),
                new Order(2, "Chleb pszenny", 100,
                        "Piekarnia XYZ", "2023-05-10", "Dostarczone"),
                new Order(3, "Jajka L", 30,
                        "Ferma Drobiu", "2023-05-12", "W drodze")
        );
        ordersTable.setItems(ordersData);
    }

    /**
     * Pokazuje formularz tworzenia nowego zamówienia.
     */
    private void showNewOrderDialog() {
        // TODO: implementacja okna dialogowego
        System.out.println("Otwieranie formularza nowego zamówienia...");
    }

    /**
     * Edytuje wybrane zamówienie.
     */
    private void editSelectedOrder() {
        Order selected = ordersTable.getSelectionModel()
                .getSelectedItem();
        if (selected != null) {
            System.out.println("Edytowanie zamówienia: "
                    + selected.getId());
        } else {
            showAlert(
                    "Brak wybranego zamówienia",
                    "Proszę wybrać zamówienie do edycji"
            );
        }
    }

    /**
     * Anuluje wybrane zamówienie.
     */
    private void cancelSelectedOrder() {
        Order selected = ordersTable.getSelectionModel()
                .getSelectedItem();
        if (selected != null) {
            System.out.println("Anulowanie zamówienia: "
                    + selected.getId());
        } else {
            showAlert(
                    "Brak wybranego zamówienia",
                    "Proszę wybrać zamówienie do anulowania"
            );
        }
    }

    /**
     * Zatwierdza przyjęcie dostawy wybranego zamówienia.
     */
    private void receiveSelectedOrder() {
        Order selected = ordersTable.getSelectionModel()
                .getSelectedItem();
        if (selected != null) {
            System.out.println("Przyjmowanie dostawy dla zamówienia: "
                    + selected.getId());
        } else {
            showAlert(
                    "Brak wybranego zamówienia",
                    "Proszę wybrać zamówienie do przyjęcia"
            );
        }
    }

    /**
     * Wyświetla komunikat w okienku dialogowym.
     *
     * @param title   tytuł okna
     * @param message treść wiadomości
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
