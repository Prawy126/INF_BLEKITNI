package org.example.gui;

import javafx.animation.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import org.example.database.ProductRepository;
import org.example.database.TransactionRepository;
import org.example.sys.Employee;
import org.example.database.UserRepository;
import org.example.sys.Product;
import org.example.sys.Raport;
import org.example.sys.Transaction;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class CashierPanelController {
    private final CashierPanel cashierPanel;

    public CashierPanelController(CashierPanel cashierPanel) {
        this.cashierPanel = cashierPanel;
    }

    // Ekran sprzedaży
    public void showSalesScreen() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        Button newTransactionButton = cashierPanel.createStyledButton("Rozpocznij nową transakcję");
        newTransactionButton.setOnAction(e -> startNewTransaction());
        layout.getChildren().add(newTransactionButton);
        cashierPanel.setCenterPane(layout);
    }

    private void startNewTransaction() {
        Stage dialog = createStyledDialog("Nowa transakcja");
        dialog.setMinWidth(800);
        dialog.setMinHeight(600);

        ObservableList<TransactionItem> cartItems = FXCollections.observableArrayList();
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));

        VBox productSearchBox = new VBox(10);
        Label searchLabel = new Label("Wyszukaj produkt:");
        TextField searchField = createStyledTextField("Wpisz nazwę produktu...");
        TableView<Product> productTable = createProductTableWithSearch(searchField);

        HBox quantityBox = new HBox(10);
        Label quantityLabel = new Label("Ilość:");
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1);
        quantitySpinner.setEditable(true);
        quantitySpinner.setPrefWidth(100);
        Button addToCartButton = cashierPanel.createStyledButton("Dodaj do koszyka");
        quantityBox.getChildren().addAll(quantityLabel, quantitySpinner, addToCartButton);
        quantityBox.setAlignment(Pos.CENTER_LEFT);

        productSearchBox.getChildren().addAll(searchLabel, searchField, productTable, quantityBox);

        VBox cartBox = new VBox(10);
        Label cartLabel = new Label("Koszyk:");
        TableView<TransactionItem> cartTable = createCartTable();
        cartTable.setItems(cartItems);

        HBox totalBox = new HBox(10);
        Label totalLabel = new Label("Suma:");
        Label totalPriceLabel = new Label("0.00 zł");
        totalPriceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        totalBox.getChildren().addAll(totalLabel, totalPriceLabel);
        totalBox.setAlignment(Pos.CENTER_RIGHT);

        HBox buttonBox = new HBox(10);
        Button confirmButton = cashierPanel.createStyledButton("Zatwierdź transakcję", "#27AE60");
        Button cancelButton = cashierPanel.createStyledButton("Anuluj", "#E74C3C");
        buttonBox.getChildren().addAll(confirmButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        cartBox.getChildren().addAll(cartLabel, cartTable, totalBox, buttonBox);

        addToCartButton.setOnAction(e -> {
            Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                int quantity = quantitySpinner.getValue();
                if (selectedProduct.getQuantity() < quantity) {
                    showNotification("Błąd", "Niewystarczająca ilość produktu.");
                    return;
                }
                boolean found = false;
                for (TransactionItem item : cartItems) {
                    if (item.getProduct().getId() == selectedProduct.getId()) {
                        int newQuantity = item.getQuantity() + quantity;
                        if (newQuantity > selectedProduct.getQuantity()) {
                            showNotification("Błąd", "Niewystarczająca ilość produktu.");
                            return;
                        }
                        item.setQuantity(newQuantity);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    cartItems.add(new TransactionItem(selectedProduct, quantity));
                }
                cartTable.refresh();
                updateTotalPrice(cartItems, totalPriceLabel);
            }
        });

        confirmButton.setOnAction(e -> {
            if (cartItems.isEmpty()) {
                showNotification("Błąd", "Koszyk jest pusty.");
                return;
            }
            saveTransaction(cartItems, dialog);
        });

        cancelButton.setOnAction(e -> dialog.close());

        mainLayout.setLeft(productSearchBox);
        mainLayout.setRight(cartBox);
        Scene scene = new Scene(mainLayout);
        dialog.setScene(scene);
        dialog.show();
    }

    private TableView<Product> createProductTableWithSearch(TextField searchField) {
        TableView<Product> table = new TableView<>();
        table.setMinHeight(300);

        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Product, String> nameCol = new TableColumn<>("Nazwa");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Product, String> categoryCol = new TableColumn<>("Kategoria");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<Product, Double> priceCol = new TableColumn<>("Cena");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Dostępna ilość");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        table.getColumns().addAll(idCol, nameCol, categoryCol, priceCol, quantityCol);

        ProductRepository productRepo = new ProductRepository();
        ObservableList<Product> productList = FXCollections.observableArrayList(productRepo.pobierzWszystkieProdukty());
        table.setItems(productList);
        productRepo.close();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isBlank()) {
                table.setItems(productList);
            } else {
                ObservableList<Product> filtered = FXCollections.observableArrayList();
                for (Product p : productList) {
                    if (p.getName().toLowerCase().contains(newVal.toLowerCase()) ||
                            p.getCategory().toLowerCase().contains(newVal.toLowerCase())) {
                        filtered.add(p);
                    }
                }
                table.setItems(filtered);
            }
        });

        return table;
    }

    private TableView<TransactionItem> createCartTable() {
        TableView<TransactionItem> table = new TableView<>();
        table.setMinHeight(300);

        TableColumn<TransactionItem, String> nameCol = new TableColumn<>("Nazwa");
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduct().getName()));

        TableColumn<TransactionItem, Integer> quantityCol = new TableColumn<>("Ilość");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantityCol.setOnEditCommit(event -> {
            TransactionItem item = event.getRowValue();
            int newValue = event.getNewValue();
            if (newValue > 0 && newValue <= item.getProduct().getQuantity()) {
                item.setQuantity(newValue);
                updateTotalPrice(table.getItems(), null);
            } else {
                table.refresh();
                showNotification("Błąd", "Nieprawidłowa ilość.");
            }
        });

        TableColumn<TransactionItem, Double> priceCol = new TableColumn<>("Cena jedn.");
        priceCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getProduct().getPrice()).asObject());

        TableColumn<TransactionItem, Double> totalCol = new TableColumn<>("Suma");
        totalCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(
                        cellData.getValue().getProduct().getPrice() * cellData.getValue().getQuantity()
                ).asObject());

        TableColumn<TransactionItem, Void> actionCol = new TableColumn<>("Akcje");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button removeButton = new Button("Usuń");

            {
                removeButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
                removeButton.setOnAction(event -> {
                    TransactionItem item = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(item);
                    updateTotalPrice(getTableView().getItems(), null);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeButton);
            }
        });

        table.getColumns().addAll(nameCol, quantityCol, priceCol, totalCol, actionCol);
        table.setEditable(true);
        return table;
    }

    private void updateTotalPrice(ObservableList<TransactionItem> items, Label totalPriceLabel) {
        double total = 0;
        for (TransactionItem item : items) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        if (totalPriceLabel != null) {
            totalPriceLabel.setText(String.format("%.2f zł", total));
        }
    }

    private void saveTransaction(ObservableList<TransactionItem> items, Stage dialog) {
        try {
            UserRepository userRepo = new UserRepository();
            Employee currentEmployee = userRepo.getCurrentEmployee();
            if (currentEmployee == null) {
                showNotification("Błąd", "Nie jesteś zalogowany.");
                userRepo.close();
                return;
            }

            Transaction transaction = new Transaction();
            transaction.setPracownik(currentEmployee);
            transaction.setData(new Date());

            ProductRepository productRepo = new ProductRepository();
            for (TransactionItem item : items) {
                Product product = item.getProduct();
                int newQuantity = product.getQuantity() - item.getQuantity();
                productRepo.aktualizujIloscProduktu(product.getId(), newQuantity);
            }

            TransactionRepository transactionRepo = new TransactionRepository();
            transactionRepo.dodajTransakcje(transaction);
            transactionRepo.close();

            userRepo.close();
            productRepo.close();
            showNotification("Sukces", "Transakcja została zapisana.");
            dialog.close();
        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Błąd", "Nie można zapisać transakcji: " + e.getMessage());
        }
    }

    // Panel raportów
    public void showSalesReportsPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        TableView<Raport> tableView = createReportTable();
        HBox buttons = new HBox(10);
        Button pdfButton = cashierPanel.createStyledButton("Generuj PDF");
        Button csvButton = cashierPanel.createStyledButton("Generuj CSV");
        pdfButton.setOnAction(e -> showReportDialog("PDF"));
        csvButton.setOnAction(e -> showReportDialog("CSV"));
        buttons.getChildren().addAll(pdfButton, csvButton);
        buttons.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(tableView, buttons);
        cashierPanel.setCenterPane(layout);
    }

    private void showReportDialog(String format) {
        Stage dialog = createStyledDialog("Generowanie raportu");
        ComboBox<String> typeBox = createStyledComboBox("Dzienny", "Tygodniowy", "Miesięczny");
        DatePicker datePicker = createStyledDatePicker();
        Button generateBtn = cashierPanel.createStyledButton("Generuj " + format, "#2980B9");

        generateBtn.setOnAction(e -> {
            String reportType = typeBox.getValue();
            LocalDate selectedDate = datePicker.getValue();

            if (reportType == null || selectedDate == null) {
                showNotification("Błąd", "Wybierz typ raportu i datę.");
                return;
            }

            TransactionRepository transactionRepo = new TransactionRepository();
            List<Transaction> transactions = transactionRepo.getTransactionsByDate(selectedDate);
            transactionRepo.close();

            if (transactions.isEmpty()) {
                showNotification("Brak danych", "Nie znaleziono transakcji z tej daty.");
                return;
            }

            simulateGenerateReport(reportType, selectedDate, transactions);

            cashierPanel.setReportGenerated(true); // ✅ Ustawienie flagi
            showNotification("Sukces", "Raport " + format + " został wygenerowany.");

            dialog.close();
        });

        VBox root = new VBox(20);
        root.getChildren().addAll(
                new Label("Typ raportu:"), typeBox,
                new Label("Data:"), datePicker,
                generateBtn
        );
        setupDialog(dialog, root);
    }

    /**
     * Symuluje generowanie raportu — na razie tylko loguje dane.
     */
    private void simulateGenerateReport(String reportType, LocalDate date, List<Transaction> transactions) {
        System.out.println("Symulacja generowania raportu: " + reportType + " dla daty: " + date);
        System.out.println("Liczba transakcji: " + transactions.size());
        // Możesz tu później dodać prawdziwe wywołanie Twojej biblioteki PDF
    }

    private TableView<Raport> createReportTable() {
        TableView<Raport> tableView = new TableView<>();
        tableView.setMinHeight(300);

        TableColumn<Raport, Integer> idColumn = new TableColumn<>("Id_raportu");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Raport, LocalDate> dateStartColumn = new TableColumn<>("Data początkowa");
        dateStartColumn.setCellValueFactory(new PropertyValueFactory<>("dataPoczatku"));

        TableColumn<Raport, LocalDate> dateEndColumn = new TableColumn<>("Data końcowa");
        dateEndColumn.setCellValueFactory(new PropertyValueFactory<>("dataZakonczenia"));

        TableColumn<Raport, String> typeColumn = new TableColumn<>("Typ raportu");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typRaportu"));

        TableColumn<Raport, Void> viewColumn = new TableColumn<>("Podgląd");
        viewColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("Podgląd");
            {
                viewButton.setStyle("-fx-background-color: #2980B9; -fx-text-fill: white;");
                viewButton.setOnAction(event -> {
                    Raport raport = getTableView().getItems().get(getIndex());
                    showReportDetails(raport);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                setGraphic(empty ? null : viewButton);
            }
        });

        tableView.getColumns().addAll(idColumn, dateStartColumn, dateEndColumn, typeColumn, viewColumn);
        tableView.setItems(FXCollections.observableArrayList());
        return tableView;
    }

    private void showReportDetails(Raport report) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Szczegóły raportu");
        alert.setHeaderText("Raport ID: " + report.getId());
        alert.setContentText("""
                Typ raportu: %s
                Data początkowa: %s
                Data końcowa: %s
                Pracownik: %s %s
                Plik: %s
                """.formatted(
                report.getTypRaportu(),
                report.getDataPoczatku(),
                report.getDataZakonczenia(),
                report.getPracownik().getName(),
                report.getPracownik().getSurname(),
                report.getSciezkaPliku()
        ));
        alert.showAndWait();
    }

    // Zgłoszenie problemu
    public void showIssueReportPanel() {
        Stage dialog = createStyledDialog("Zgłoszenie problemu");
        ComboBox<String> typeBox = createStyledComboBox("Awaria sprzętu", "Błąd oprogramowania", "Inne");
        TextArea description = createStyledTextArea("Opisz problem...");

        Button sendButton = cashierPanel.createStyledButton("Wyślij", "#27AE60");
        Button cancelButton = cashierPanel.createStyledButton("Anuluj", "#E74C3C");

        sendButton.setOnAction(e -> {
            if (typeBox.getValue() == null || description.getText().trim().isEmpty()) {
                showNotification("Błąd", "Uzupełnij wszystkie pola.");
                return;
            }
            showNotification("Sukces", "Zgłoszenie wysłane.");
            dialog.close();
        });

        cancelButton.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(10, sendButton, cancelButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                new Label("Typ zgłoszenia:"), typeBox,
                new Label("Opis:"), description,
                buttons
        );

        setupDialog(dialog, root);
    }

    public void showCloseShiftPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        Button confirmButton = cashierPanel.createStyledButton("Potwierdź zamknięcie zmiany", "#E67E22");
        confirmButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Zamknięcie zmiany");
            alert.setHeaderText("Zmiana została pomyślnie zamknięta");
            alert.setContentText("Dziękujemy za pracę w tej zmianie!");
            alert.showAndWait();
        });
        layout.getChildren().add(confirmButton);
        cashierPanel.setCenterPane(layout);
    }

    public void showAbsenceRequestForm() {
        Stage stage = new Stage();
        stage.setTitle("Wniosek o nieobecność");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        UserRepository userRepository = new UserRepository();
        Employee currentEmployee = userRepository.getCurrentEmployee();
        if (currentEmployee == null) {
            showNotification("Błąd", "Nie jesteś zalogowany.");
            userRepository.close();
            return;
        }

        Label employeeInfoLabel = new Label("Pracownik: %s %s (ID: %d)"
                .formatted(currentEmployee.getName(), currentEmployee.getSurname(), currentEmployee.getId()));
        employeeInfoLabel.setStyle("-fx-font-weight: bold;");

        Label reasonLabel = new Label("Opis:");
        TextField reasonField = new TextField();

        Label fromDateLabel = new Label("Data od:");
        DatePicker fromDatePicker = new DatePicker();

        Label toDateLabel = new Label("Data do:");
        DatePicker toDatePicker = new DatePicker();

        Label typeLabel = new Label("Typ wniosku:");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Urlop wypoczynkowy", "Urlop na żądanie", "Zwolnienie lekarskie", "Inne");
        typeComboBox.setValue("Urlop wypoczynkowy");

        Button submitButton = cashierPanel.createStyledButton("Wyślij wniosek", "#27AE60");
        submitButton.setOnAction(e -> {
            if (validateAbsenceForm(reasonField.getText(), fromDatePicker.getValue(), toDatePicker.getValue())) {
                showNotification("Sukces", "Wniosek został wysłany.");
                stage.close();
            }
        });

        grid.add(employeeInfoLabel, 0, 0, 2, 1);
        grid.add(typeLabel, 0, 1);
        grid.add(typeComboBox, 1, 1);
        grid.add(reasonLabel, 0, 2);
        grid.add(reasonField, 1, 2);
        grid.add(fromDateLabel, 0, 3);
        grid.add(fromDatePicker, 1, 3);
        grid.add(toDateLabel, 0, 4);
        grid.add(toDatePicker, 1, 4);
        grid.add(submitButton, 1, 5);

        Scene scene = new Scene(grid, 400, 350);
        stage.setScene(scene);
        stage.setOnHidden(event -> userRepository.close());
        stage.show();
    }

    private boolean validateAbsenceForm(String reason, LocalDate fromDate, LocalDate toDate) {
        if (reason == null || reason.trim().isEmpty()) {
            showNotification("Błąd", "Musisz podać powód nieobecności");
            return false;
        }
        if (fromDate == null || toDate == null) {
            showNotification("Błąd", "Musisz wybrać datę rozpoczęcia i zakończenia");
            return false;
        }
        if (fromDate.isAfter(toDate)) {
            showNotification("Błąd", "Data rozpoczęcia nie może być późniejsza niż zakończenia");
            return false;
        }
        return true;
    }

    private void animateDialog(Stage dialog, Pane root) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);

        TranslateTransition tt = new TranslateTransition(Duration.millis(300), root);
        tt.setFromY(-20);
        tt.setToY(0);

        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.play();
    }

    private Stage createStyledDialog(String title) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);
        return dialog;
    }

    private void setupDialog(Stage dialog, Pane root) {
        Scene scene = new Scene(root);
        dialog.setScene(scene);
        animateDialog(dialog, root);
        dialog.showAndWait();
    }

    private ComboBox<String> createStyledComboBox(String... items) {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(items);
        combo.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 8px;");
        combo.getSelectionModel().selectFirst();
        return combo;
    }

    private DatePicker createStyledDatePicker() {
        DatePicker dp = new DatePicker();
        dp.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 8px;");
        dp.getEditor().setStyle("-fx-background-color: #E0E0E0;");
        return dp;
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 8px;");
        return field;
    }

    private TextArea createStyledTextArea(String prompt) {
        TextArea area = new TextArea();
        area.setPromptText(prompt);
        area.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 8px;");
        area.setWrapText(true);
        return area;
    }

    private void showNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void logout() {
        UserRepository.resetCurrentEmployee();
        Stage primaryStage = cashierPanel.getPrimaryStage();
        primaryStage.close();
        HelloApplication.showLoginScreen(primaryStage);
    }

    public static class TransactionItem {
        private final Product product;
        private int quantity;

        public TransactionItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getTotal() { return product.getPrice() * quantity; }
    }
}