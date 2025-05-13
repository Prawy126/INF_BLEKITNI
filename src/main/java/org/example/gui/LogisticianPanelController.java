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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sys.Product; //IMPORT KLASY PRODUCT Z BIBLIOTEKI , NIE Z GŁÓWNEGO PROJEKTU!
import pdf.WarehouseRaport;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Kontroler obsługujący logikę interfejsu użytkownika
 * dla panelu logistyka.
 */
public class LogisticianPanelController {

    private final LogisticianPanel logisticianPanel;
    private final Stage primaryStage;
    private static final Logger logger = LogManager.getLogger(LogisticianPanelController.class);

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
        stage.setTitle("Generuj raport magazynowy");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Elementy formularza
        Label categoriesLabel = new Label("Wybierz kategorie:");
        ListView<String> categoriesList = new ListView<>();
        categoriesList.getItems().addAll(getAllCategories());
        categoriesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Label outputLabel = new Label("Ścieżka zapisu:");
        TextField outputPath = new TextField();

        Button browseButton = new Button("Przeglądaj");
        browseButton.setOnAction(e -> handleBrowseButton(stage, outputPath));

        Button generate = new Button("Generuj");
        generate.setOnAction(e -> handleGenerateButton(outputPath, categoriesList, stage));

        // Rozmieszczenie elementów
        grid.add(categoriesLabel, 0, 0);
        grid.add(categoriesList, 1, 0);
        grid.add(outputLabel, 0, 1);
        grid.add(outputPath, 1, 1);
        grid.add(browseButton, 2, 1);
        grid.add(generate, 1, 2);

        // Dodatkowy przycisk testowy
        Button testButton = new Button("Testuj bibliotekę");
        testButton.setOnAction(e -> runLibraryTest());
        grid.add(testButton, 1, 3);

        stage.setScene(new Scene(grid, 500, 350));
        stage.show();
    }

    private void handleBrowseButton(Stage stage, TextField outputPath) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            outputPath.setText(file.getAbsolutePath());
        }
    }

    private void handleGenerateButton(TextField outputPath, ListView<String> categoriesList, Stage stage) {
        try {
            WarehouseRaport generator = new WarehouseRaport();
            generator.setLogoPath("src/main/resources/logo.png");
            generator.setLowStockThreshold(5);

            generator.generateReport(
                    outputPath.getText(),
                    (List<sys.Product>) (List<?>) getSampleProducts(),
                    new ArrayList<>(categoriesList.getSelectionModel().getSelectedItems())
            );

            showAlert(Alert.AlertType.INFORMATION, "Sukces", "Raport wygenerowany pomyślnie!");
            stage.close();
        } catch (Exception ex) {
            logger.error("Błąd generowania raportu", ex);
            showAlert(Alert.AlertType.ERROR, "Błąd", "Błąd generowania raportu: " + ex.getMessage());
        }
    }

    private void runLibraryTest() {
        try {
            WarehouseRaport generator = new WarehouseRaport();
            generator.setLogoPath("src/main/resources/logo.png");
            generator.setLowStockThreshold(3);

            String testPath = System.getProperty("user.dir") + "/test_report.pdf";
            generator.generateReport(
                    testPath,
                    (List<sys.Product>) (List<?>) getSampleProducts(),
                    Arrays.asList("Elektronika", "Żywność")
            );

            showAlert(Alert.AlertType.INFORMATION,
                    "Test powiódł się",
                    "Wygenerowano testowy raport:\n" + testPath);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR,
                    "Test nie powiódł się",
                    "Błąd: " + ex.getMessage());
        }
    }

    // Metody pomocnicze
    private List<String> getAllCategories() {
        return Arrays.asList(
                "Elektronika",
                "Odzież",
                "Akcesoria",
                "Żywność"
        );
    }

    private List<Product> getSampleProducts() {
        return Arrays.asList(
                new Product("Laptop HP Pavilion", "Elektronika", 8, 3499),
                new Product("Mysz Logitech MX", "Elektronika", 2, 299),
                new Product("Kurtka zimowa", "Odzież", 15, 199),
                new Product("Powerbank Xiaomi", "Akcesoria", 4, 89),
                new Product("Kawa Arabica 1kg", "Żywność", 1, 39),
                new Product("Słuchawki Sony", "Elektronika", 6, 599)
        );
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
