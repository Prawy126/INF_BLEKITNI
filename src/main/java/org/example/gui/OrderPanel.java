package org.example.gui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class OrderPanel extends VBox {

    private TableView<Order> ordersTable;
    private ObservableList<Order> ordersData;

    public OrderPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        initializeUI();
        loadSampleData();
    }

    private void initializeUI() {
        Label titleLabel = new Label("Panel Zamówień");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Tabela z zamówieniami
        ordersTable = new TableView<>();

        TableColumn<Order, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Order, String> productCol = new TableColumn<>("Produkt");
        productCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productCol.setPrefWidth(150);

        TableColumn<Order, Integer> quantityCol = new TableColumn<>("Ilość");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Order, String> supplierCol = new TableColumn<>("Dostawca");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        supplierCol.setPrefWidth(150);

        TableColumn<Order, String> dateCol = new TableColumn<>("Data zamówienia");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        ordersTable.getColumns().addAll(idCol, productCol, quantityCol, supplierCol, dateCol, statusCol);

        // Przyciski zarządzania zamówieniami
        Button newOrderButton = new Button("Nowe zamówienie");
        newOrderButton.setOnAction(e -> showNewOrderDialog());

        Button editOrderButton = new Button("Edytuj");
        editOrderButton.setOnAction(e -> editSelectedOrder());

        Button cancelOrderButton = new Button("Anuluj");
        cancelOrderButton.setOnAction(e -> cancelSelectedOrder());

        Button receiveOrderButton = new Button("Przyjmij dostawę");
        receiveOrderButton.setOnAction(e -> receiveSelectedOrder());

        HBox buttonsBox = new HBox(10, newOrderButton, editOrderButton, cancelOrderButton, receiveOrderButton);

        getChildren().addAll(titleLabel, ordersTable, buttonsBox);
    }

    private void loadSampleData() {
        ordersData = FXCollections.observableArrayList(
                new Order(1, "Mleko 1L", 50, "Mlekovita", "2023-05-15", "Oczekujące"),
                new Order(2, "Chleb pszenny", 100, "Piekarnia XYZ", "2023-05-10", "Dostarczone"),
                new Order(3, "Jajka L", 30, "Ferma Drobiu", "2023-05-12", "W drodze")
        );
        ordersTable.setItems(ordersData);
    }

    private void showNewOrderDialog() {
        // Tutaj implementacja okna dialogowego nowego zamówienia
        System.out.println("Otwieranie formularza nowego zamówienia...");
    }

    private void editSelectedOrder() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Edytowanie zamówienia: " + selected.getId());
        } else {
            showAlert("Brak wybranego zamówienia", "Proszę wybrać zamówienie do edycji");
        }
    }

    private void cancelSelectedOrder() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Anulowanie zamówienia: " + selected.getId());
        } else {
            showAlert("Brak wybranego zamówienia", "Proszę wybrać zamówienie do anulowania");
        }
    }

    private void receiveSelectedOrder() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Przyjmowanie dostawy dla zamówienia: " + selected.getId());
        } else {
            showAlert("Brak wybranego zamówienia", "Proszę wybrać zamówienie do przyjęcia");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}