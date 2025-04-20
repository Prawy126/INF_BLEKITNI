/*
 * Classname: LogisticianPanelController
 * Version information: 1.1
 * Date: 2025-04-21
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;

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
     * Wyświetla ekran zarządzania magazynem (produkty).
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
        filterButton.setOnAction(e -> showFilterProductDialog());

        Button reportsButton = new Button("Raporty");

        layout.getChildren().addAll(titleLabel, tableView, addProductButton, filterButton, reportsButton);
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

        TableView<Order> tableView = new TableView<>();
        tableView.setMinHeight(200);

        TableColumn<Order, Integer> idCol = new TableColumn<>("Id");
        TableColumn<Order, Integer> productIdCol = new TableColumn<>("Id_produktu");
        TableColumn<Order, Integer> employeeIdCol = new TableColumn<>("Id_pracownika");
        TableColumn<Order, Integer> qtyCol = new TableColumn<>("Ilosc");
        TableColumn<Order, Double> priceCol = new TableColumn<>("Cena");
        TableColumn<Order, String> dateCol = new TableColumn<>("Data");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productIdCol.setCellValueFactory(new PropertyValueFactory<>("productId"));
        employeeIdCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        tableView.getColumns().addAll(idCol, productIdCol, employeeIdCol, qtyCol, priceCol, dateCol);
        tableView.setItems(getSampleOrders());

        Button addOrderButton = new Button("Dodaj zamówienie");
        addOrderButton.setOnAction(e -> showAddOrderForm());

        Button filterButton = new Button("Filtruj");
        filterButton.setOnAction(e -> showFilterDialog(tableView));

        layout.getChildren().addAll(titleLabel, tableView, addOrderButton, filterButton);
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

        Button generateButton = new Button("Generuj raport");
        generateButton.setOnAction(e -> showReportDialog());

        layout.getChildren().addAll(titleLabel, tableView, generateButton);
        logisticianPanel.setCenterPane(layout);
    }

    /**
     * Okno dialogowe do wyboru formatu i zakresu dat raportu.
     */
    private void showReportDialog() {
        Stage stage = new Stage();
        stage.setTitle("Generuj raport");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        Label startDateLabel = new Label("Data od:");
        Label endDateLabel = new Label("Data do:");
        Label formatLabel = new Label("Format:");

        DatePicker startDate = new DatePicker();
        DatePicker endDate = new DatePicker();

        ComboBox<String> formatBox = new ComboBox<>();
        formatBox.getItems().addAll("PDF", "CSV");

        Button generate = new Button("Generuj");
        generate.setOnAction(e -> {
            if (startDate.getValue() != null && endDate.getValue() != null && formatBox.getValue() != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Wygenerowano raport od " + startDate.getValue() +
                                " do " + endDate.getValue() +
                                " w formacie: " + formatBox.getValue());
                alert.showAndWait();
                stage.close();
            } else {
                new Alert(Alert.AlertType.WARNING, "Uzupełnij wszystkie pola.").showAndWait();
            }
        });

        grid.add(startDateLabel, 0, 0);
        grid.add(startDate, 1, 0);
        grid.add(endDateLabel, 0, 1);
        grid.add(endDate, 1, 1);
        grid.add(formatLabel, 0, 2);
        grid.add(formatBox, 1, 2);
        grid.add(generate, 1, 3);

        stage.setScene(new Scene(grid, 320, 250));
        stage.show();
    }

    /**
     * Okno dodawania nowego zamówienia.
     */
    private void showAddOrderForm() {
        Stage stage = new Stage();
        stage.setTitle("Dodaj zamówienie");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label productLabel = new Label("Id produktu:");
        Label employeeLabel = new Label("Id pracownika:");
        Label qtyLabel = new Label("Ilość:");
        Label priceLabel = new Label("Cena:");
        Label dateLabel = new Label("Data:");

        TextField productId = new TextField();
        TextField employeeId = new TextField();
        TextField quantity = new TextField();
        TextField price = new TextField();
        DatePicker datePicker = new DatePicker();

        Button submit = new Button("Zapisz");
        submit.setOnAction(e -> {
            if (productId.getText().isEmpty() || employeeId.getText().isEmpty() ||
                    quantity.getText().isEmpty() || price.getText().isEmpty() || datePicker.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Uzupełnij wszystkie pola.").showAndWait();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Zamówienie dodane pomyślnie.").showAndWait();
                stage.close();
            }
        });

        grid.add(productLabel, 0, 0);
        grid.add(productId, 1, 0);
        grid.add(employeeLabel, 0, 1);
        grid.add(employeeId, 1, 1);
        grid.add(qtyLabel, 0, 2);
        grid.add(quantity, 1, 2);
        grid.add(priceLabel, 0, 3);
        grid.add(price, 1, 3);
        grid.add(dateLabel, 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(submit, 1, 5);

        stage.setScene(new Scene(grid, 350, 300));
        stage.show();
    }

    /**
     * Okno filtrowania zamówień.
     */
    private void showFilterDialog(TableView<Order> table) {
        Stage stage = new Stage();
        stage.setTitle("Filtrowanie zamówień");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        Label productIdLabel = new Label("Id produktu:");
        Label employeeIdLabel = new Label("Id pracownika:");
        Label minQtyLabel = new Label("Min. ilość:");

        TextField productIdField = new TextField();
        TextField employeeIdField = new TextField();
        TextField minQtyField = new TextField();

        Button filterBtn = new Button("Filtruj");
        filterBtn.setOnAction(e -> {
            table.setItems(getSampleOrders().filtered(order ->
                    (productIdField.getText().isEmpty() || String.valueOf(order.getProductId()).equals(productIdField.getText())) &&
                            (employeeIdField.getText().isEmpty() || String.valueOf(order.getEmployeeId()).equals(employeeIdField.getText())) &&
                            (minQtyField.getText().isEmpty() || order.getQuantity() >= Integer.parseInt(minQtyField.getText()))
            ));
            stage.close();
        });

        grid.add(productIdLabel, 0, 0);
        grid.add(productIdField, 1, 0);
        grid.add(employeeIdLabel, 0, 1);
        grid.add(employeeIdField, 1, 1);
        grid.add(minQtyLabel, 0, 2);
        grid.add(minQtyField, 1, 2);
        grid.add(filterBtn, 1, 3);

        stage.setScene(new Scene(grid, 320, 250));
        stage.show();
    }

    /**
     * Wyświetla okno formularza filtrowania produktów.
     * Pola: Id, Nazwa, Cena, Ilość w magazynie.
     * Przycisk „Filtruj” zamyka okno bez dalszej logiki filtrowania (symulacja działania).
     */
    private void showFilterProductDialog() {
        Stage stage = new Stage();
        stage.setTitle("Filtrowanie produktów");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label idLabel = new Label("Id:");
        TextField idField = new TextField();

        Label nameLabel = new Label("Nazwa:");
        TextField nameField = new TextField();

        Label priceLabel = new Label("Cena:");
        TextField priceField = new TextField();

        Label stockLabel = new Label("Ilość w magazynie:");
        TextField stockField = new TextField();

        Button filterButton = new Button("Filtruj");
        filterButton.setOnAction(e -> {
            stage.close(); // symulacja filtrowania — tylko zamyka okno
        });

        // Ustawienie pól w siatce (etykiety po lewej stronie)
        grid.add(idLabel, 0, 0);       grid.add(idField, 1, 0);
        grid.add(nameLabel, 0, 1);     grid.add(nameField, 1, 1);
        grid.add(priceLabel, 0, 2);    grid.add(priceField, 1, 2);
        grid.add(stockLabel, 0, 3);    grid.add(stockField, 1, 3);
        grid.add(filterButton, 1, 4);

        Scene scene = new Scene(grid, 360, 250);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Wyświetla formularz zgłoszenia wniosku o nieobecność.
     */
    public void showAbsenceRequestForm() {
        Stage stage = new Stage();
        stage.setTitle("Wniosek o nieobecność");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        Label reasonLabel = new Label("Powód:");
        TextField reasonField = new TextField();

        Label fromDateLabel = new Label("Data od:");
        DatePicker fromDatePicker = new DatePicker();

        Label toDateLabel = new Label("Data do:");
        DatePicker toDatePicker = new DatePicker();

        Button submitButton = new Button("Złóż wniosek");
        submitButton.setOnAction(e -> {
            if (reasonField.getText().isEmpty() || fromDatePicker.getValue() == null || toDatePicker.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Uzupełnij wszystkie pola.").showAndWait();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Wniosek złożony pomyślnie.").showAndWait();
                stage.close();
            }
        });

        grid.add(reasonLabel, 0, 0);   grid.add(reasonField, 1, 0);
        grid.add(fromDateLabel, 0, 1); grid.add(fromDatePicker, 1, 1);
        grid.add(toDateLabel, 0, 2);   grid.add(toDatePicker, 1, 2);
        grid.add(submitButton, 1, 3);

        Scene scene = new Scene(grid, 350, 250);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Zwraca przykładową listę zamówień.
     */
    private ObservableList<Order> getSampleOrders() {
        return FXCollections.observableArrayList(
                new Order(1, 101, 201, 10, 99.99, "2025-04-01"),
                new Order(2, 102, 202, 5, 49.50, "2025-04-03"),
                new Order(3, 103, 203, 12, 135.00, "2025-04-10")
        );
    }

    /**
     * Wewnętrzna klasa reprezentująca dane zamówienia.
     */
    public static class Order {
        private final int id;
        private final int productId;
        private final int employeeId;
        private final int quantity;
        private final double price;
        private final String date;

        public Order(int id, int productId, int employeeId, int quantity, double price, String date) {
            this.id = id;
            this.productId = productId;
            this.employeeId = employeeId;
            this.quantity = quantity;
            this.price = price;
            this.date = date;
        }

        public int getId() { return id; }
        public int getProductId() { return productId; }
        public int getEmployeeId() { return employeeId; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public String getDate() { return date; }
    }

    /**
     * Zamyka aplikację i powraca do okna logowania.
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
