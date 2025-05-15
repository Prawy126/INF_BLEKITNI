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
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import org.example.database.AbsenceRequestRepository;
import org.example.database.ProductRepository;
import org.example.database.TransactionRepository;
import org.example.database.TechnicalIssueRepository;
import org.example.sys.AbsenceRequest;
import org.example.sys.Employee;
import org.example.database.UserRepository;
import org.example.sys.Product;
import org.example.sys.Report;
import org.example.sys.Transaction;
import org.example.sys.TechnicalIssue;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
        // Create a new transaction dialog
        Stage dialog = createStyledDialog("Nowa transakcja");
        dialog.setMinWidth(800);
        dialog.setMinHeight(600);

        // Create a model for the transaction
        ObservableList<TransactionItem> cartItems = FXCollections.observableArrayList();

        // Create the main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));

        // Create the product search section
        VBox productSearchBox = new VBox(10);
        Label searchLabel = new Label("Wyszukaj produkt:");
        TextField searchField = createStyledTextField("Wpisz nazwę produktu...");

        // Create product table
        TableView<Product> productTable = createProductTableWithSearch(searchField);

        // Add quantity field
        HBox quantityBox = new HBox(10);
        Label quantityLabel = new Label("Ilość:");
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1);
        quantitySpinner.setEditable(true);
        quantitySpinner.setPrefWidth(100);

        Button addToCartButton = cashierPanel.createStyledButton("Dodaj do koszyka");

        quantityBox.getChildren().addAll(quantityLabel, quantitySpinner, addToCartButton);
        quantityBox.setAlignment(Pos.CENTER_LEFT);

        productSearchBox.getChildren().addAll(searchLabel, searchField, productTable, quantityBox);

        // Create the cart section
        VBox cartBox = new VBox(10);
        Label cartLabel = new Label("Koszyk:");

        // Create cart table
        TableView<TransactionItem> cartTable = createCartTable();
        cartTable.setItems(cartItems);

        // Total price display
        HBox totalBox = new HBox(10);
        Label totalLabel = new Label("Suma:");
        Label totalPriceLabel = new Label("0.00 zł");
        totalPriceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        totalBox.getChildren().addAll(totalLabel, totalPriceLabel);
        totalBox.setAlignment(Pos.CENTER_RIGHT);

        // Buttons
        HBox buttonBox = new HBox(10);
        Button confirmButton = cashierPanel.createStyledButton("Zatwierdź transakcję", "#27AE60");
        Button cancelButton = cashierPanel.createStyledButton("Anuluj", "#E74C3C");

        buttonBox.getChildren().addAll(confirmButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        cartBox.getChildren().addAll(cartLabel, cartTable, totalBox, buttonBox);

        // Add to cart action
        addToCartButton.setOnAction(e -> {
            Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                int quantity = quantitySpinner.getValue();

                // Check if we have enough quantity
                if (selectedProduct.getQuantity() < quantity) {
                    showNotification("Błąd", "Niewystarczająca ilość produktu. Dostępne: " + selectedProduct.getQuantity());
                    return;
                }

                // Check if product already in cart
                boolean found = false;
                for (TransactionItem item : cartItems) {
                    if (item.getProduct().getId() == selectedProduct.getId()) {
                        // Update quantity
                        int newQuantity = item.getQuantity() + quantity;
                        if (newQuantity > selectedProduct.getQuantity()) {
                            showNotification("Błąd", "Niewystarczająca ilość produktu. Dostępne: " + selectedProduct.getQuantity());
                            return;
                        }
                        item.setQuantity(newQuantity);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // Add new item to cart
                    TransactionItem newItem = new TransactionItem(selectedProduct, quantity);
                    cartItems.add(newItem);
                }

                // Refresh cart table
                cartTable.refresh();

                // Update total price
                updateTotalPrice(cartItems, totalPriceLabel);
            }
        });

        // Confirm transaction action
        confirmButton.setOnAction(e -> {
            if (cartItems.isEmpty()) {
                showNotification("Błąd", "Koszyk jest pusty. Dodaj produkty do koszyka.");
                return;
            }

            // Save transaction to database
            saveTransaction(cartItems, dialog);
        });

        // Cancel action
        cancelButton.setOnAction(e -> dialog.close());

        // Set up the layout
        mainLayout.setLeft(productSearchBox);
        mainLayout.setRight(cartBox);

        Scene scene = new Scene(mainLayout);
        dialog.setScene(scene);
        dialog.show();
    }

    private TableView<Product> createProductTableWithSearch(TextField searchField) {
        // Create product table
        TableView<Product> table = new TableView<>();
        table.setMinHeight(300);

        // Create columns
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

        // Load products from database
        ProductRepository productRepo = new ProductRepository();
        ObservableList<Product> productList = FXCollections.observableArrayList(productRepo.pobierzWszystkieProdukty());
        table.setItems(productList);
        productRepo.close();

        // Add search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                table.setItems(productList);
            } else {
                ObservableList<Product> filteredList = FXCollections.observableArrayList();
                for (Product product : productList) {
                    if (product.getName().toLowerCase().contains(newValue.toLowerCase()) ||
                            product.getCategory().toLowerCase().contains(newValue.toLowerCase())) {
                        filteredList.add(product);
                    }
                }
                table.setItems(filteredList);
            }
        });

        return table;
    }

    private TableView<TransactionItem> createCartTable() {
        TableView<TransactionItem> table = new TableView<>();
        table.setMinHeight(300);

        // Create columns
        TableColumn<TransactionItem, String> nameCol = new TableColumn<>("Nazwa");
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduct().getName()));

        TableColumn<TransactionItem, Integer> quantityCol = new TableColumn<>("Ilość");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Make quantity editable
        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantityCol.setOnEditCommit(event -> {
            TransactionItem item = event.getRowValue();
            int newValue = event.getNewValue();
            if (newValue > 0 && newValue <= item.getProduct().getQuantity()) {
                item.setQuantity(newValue);
                updateTotalPrice(table.getItems(), null);
            } else {
                // Reset to old value if invalid
                table.refresh();
                showNotification("Błąd", "Nieprawidłowa ilość. Maksymalna dostępna ilość: " +
                        item.getProduct().getQuantity());
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
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
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
            // Get current employee
            UserRepository userRepo = new UserRepository();
            Employee currentEmployee = userRepo.getCurrentEmployee();

            if (currentEmployee == null) {
                showNotification("Błąd", "Nie jesteś zalogowany. Zaloguj się ponownie.");
                userRepo.close();
                return;
            }

            // Create transaction
            Transaction transaction = new Transaction();
            transaction.setPracownik(currentEmployee);
            transaction.setData(new java.util.Date());

            // Create a set of products for the transaction
            Set<Product> produkty = new HashSet<>();

            // Get product repository for updating quantities
            ProductRepository productRepo = new ProductRepository();

            // Process each item in the cart
            for (TransactionItem item : items) {
                Product product = item.getProduct();

                // Check if we have enough quantity
                if (product.getQuantity() < item.getQuantity()) {
                    showNotification("Błąd", "Niewystarczająca ilość produktu: " + product.getName());
                    userRepo.close();
                    productRepo.close();
                    return;
                }

                // Update product quantity in database
                int newQuantity = product.getQuantity() - item.getQuantity();
                productRepo.aktualizujIloscProduktu(product.getId(), newQuantity);

                // Add product to transaction's set
                produkty.add(product);
            }

            // Set products for the transaction
            //TODO: Linjka zakomentowana bo sypało, uściślić działąnie Warehouse a produktów
            //transaction.setProdukty(produkty);

            // Save transaction to database
            TransactionRepository transactionRepo = new TransactionRepository();
            transactionRepo.dodajTransakcje(transaction);
            transactionRepo.close();

            // Close repositories
            userRepo.close();
            productRepo.close();

            showNotification("Sukces", "Transakcja została zapisana pomyślnie.");
            dialog.close();

        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Błąd", "Wystąpił błąd podczas zapisywania transakcji: " + e.getMessage());
        }
    }

    private void showProductSelectionDialog() {
        Stage dialog = createStyledDialog("Wybór produktów");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        TextField searchField = createStyledTextField("Szukaj produktów...");

        // Use the repository to get actual products
        ProductRepository productRepo = new ProductRepository();
        ObservableList<Product> productList = FXCollections.observableArrayList(
                productRepo.pobierzWszystkieProdukty());
        productRepo.close();

        TableView<Product> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Product, String> nameCol = new TableColumn<>("Nazwa");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Cena");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Product, String> categoryCol = new TableColumn<>("Kategoria");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Ilość");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        table.getColumns().addAll(nameCol, priceCol, categoryCol, quantityCol);
        table.setItems(productList);

        HBox buttons = new HBox(10);
        Button addButton = cashierPanel.createStyledButton("Dodaj do koszyka");
        Button cancelButton = cashierPanel.createStyledButton("Anuluj", "#E74C3C");

        addButton.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showNotification("Dodano do koszyka", selected.getName());
                dialog.close();
            }
        });
        cancelButton.setOnAction(e -> dialog.close());

        buttons.getChildren().addAll(addButton, cancelButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(searchField, table, buttons);
        setupDialog(dialog, root);
    }

    // Panel raportów
    public void showSalesReportsPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        TableView<Report> tableView = createReportTable();
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
            showNotification("Sukces", "Raport " + format + " wygenerowany");
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

    // Zgłoszenie problemu
    // Panel zgłoszeń technicznych
    public void showIssueReportPanel() {
        Stage dialog = createStyledDialog("Zgłoszenie problemu");
        ComboBox<String> typeBox = createStyledComboBox("Awaria sprzętu", "Błąd oprogramowania", "Inne");
        TextArea description = createStyledTextArea("Opisz problem...");
        Button sendButton = cashierPanel.createStyledButton("Wyślij", "#27AE60");
        Button cancelButton = cashierPanel.createStyledButton("Anuluj", "#E74C3C");

        // Obsługa zdarzenia wysyłania zgłoszenia
        sendButton.setOnAction(e -> {
            if (validateReport(typeBox.getValue(), description.getText())) {
                UserRepository userRepository = new UserRepository();
                TechnicalIssueRepository issueRepo = new TechnicalIssueRepository();
                try {
                    // Pobranie aktualnego pracownika
                    Employee currentEmployee = userRepository.getCurrentEmployee();
                    if (currentEmployee == null) {
                        showNotification("Błąd", "Nie jesteś zalogowany. Zaloguj się ponownie.");
                        return;
                    }

                    // Utworzenie nowego zgłoszenia
                    TechnicalIssue issue = new TechnicalIssue(
                            typeBox.getValue(),
                            description.getText(),
                            LocalDate.now(),
                            currentEmployee,
                            "Nowe"
                    );

                    // Zapisanie zgłoszenia do bazy danych
                    issueRepo.dodajZgloszenie(issue);

                    // Potwierdzenie i zamknięcie okna
                    showNotification("Sukces", "Zgłoszenie wysłane");
                    dialog.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showNotification("Błąd", "Wystąpił problem podczas zapisywania zgłoszenia: " + ex.getMessage());
                } finally {
                    // Zamykanie repozytoriów
                    userRepository.close();
                    issueRepo.close();
                }
            }
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

    private TableView<Report> createReportTable() {
        TableView<Report> tableView = new TableView<>();
        tableView.setMinHeight(300);

        TableColumn<Report, Integer> idColumn = new TableColumn<>("Id_raportu");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(100);

        TableColumn<Report, LocalDate> dateStartColumn = new TableColumn<>("Data początkowa");
        dateStartColumn.setCellValueFactory(new PropertyValueFactory<>("dataPoczatku"));
        dateStartColumn.setPrefWidth(150);

        TableColumn<Report, LocalDate> dateEndColumn = new TableColumn<>("Data końcowa");
        dateEndColumn.setCellValueFactory(new PropertyValueFactory<>("dataZakonczenia"));
        dateEndColumn.setPrefWidth(150);

        TableColumn<Report, String> typeColumn = new TableColumn<>("Typ raportu");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typRaportu"));
        typeColumn.setPrefWidth(150);

        TableColumn<Report, Void> viewColumn = new TableColumn<>("Podgląd");
        viewColumn.setCellFactory(getReportViewButtonCellFactory());
        viewColumn.setPrefWidth(100);

        tableView.getColumns().addAll(idColumn, dateStartColumn, dateEndColumn, typeColumn, viewColumn);

        ObservableList<Report> reports = FXCollections.observableArrayList();
        tableView.setItems(reports);

        return tableView;
    }

    private Callback<TableColumn<Report, Void>, TableCell<Report, Void>> getReportViewButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Report, Void> call(TableColumn<Report, Void> param) {
                return new TableCell<>() {
                    private final Button viewButton = new Button("Podgląd");

                    {
                        viewButton.setOnAction(event -> {
                            Report report = getTableView().getItems().get(getIndex());
                            showReportDetails(report);
                        });
                        viewButton.setStyle("-fx-background-color: #2980B9; -fx-text-fill: white;");
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : viewButton);
                    }
                };
            }
        };
    }

    private void showReportDetails(Report report) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Szczegóły raportu");
        alert.setHeaderText("Raport ID: " + report.getId());
        alert.setContentText(
                "Typ raportu: " + report.getTypRaportu() + "\n" +
                        "Data początkowa: " + report.getDataPoczatku() + "\n" +
                        "Data końcowa: " + report.getDataZakonczenia() + "\n" +
                        "Pracownik: " + report.getPracownik().getName() + " " + report.getPracownik().getSurname() + "\n" +
                        "Plik: " + report.getSciezkaPliku()
        );
        alert.showAndWait();
    }

    // Pomocnicze metody
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

        // Utworzenie repozytorium użytkowników
        UserRepository userRepository = new UserRepository();

        // Pobranie bieżącego pracownika
        Employee currentEmployee = userRepository.getCurrentEmployee();

        if (currentEmployee == null) {
            showNotification("Błąd", "Nie jesteś zalogowany. Zaloguj się ponownie.");
            userRepository.close();
            return;
        }

        // Wyświetlenie informacji o zalogowanym pracowniku
        Label employeeInfoLabel = new Label("Pracownik: " + currentEmployee.getName() + " " +
                currentEmployee.getSurname() + " (ID: " + currentEmployee.getId() + ")");
        employeeInfoLabel.setStyle("-fx-font-weight: bold;");

        Label reasonLabel = new Label("Opis:");
        TextField reasonField = new TextField();

        Label fromDateLabel = new Label("Data od:");
        DatePicker fromDatePicker = new DatePicker();

        Label toDateLabel = new Label("Data do:");
        DatePicker toDatePicker = new DatePicker();

        // Dodanie pola wyboru typu wniosku
        Label typeLabel = new Label("Typ wniosku:");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Urlop wypoczynkowy", "Urlop na żądanie", "Zwolnienie lekarskie", "Inne");
        typeComboBox.setValue("Urlop wypoczynkowy");

        Button submitButton = cashierPanel.createStyledButton("Wyślij wniosek", "#27AE60");
        submitButton.setOnAction(e -> {
            if (validateAbsenceForm(reasonField.getText(), fromDatePicker.getValue(), toDatePicker.getValue())) {
                try {
                    // Utworzenie repozytorium wniosków
                    AbsenceRequestRepository absenceRepository = new AbsenceRequestRepository();

                    // Konwersja LocalDate na java.util.Date
                    Date fromDate = java.sql.Date.valueOf(fromDatePicker.getValue());
                    Date toDate = java.sql.Date.valueOf(toDatePicker.getValue());

                    // Utworzenie obiektu wniosku z bieżącym pracownikiem
                    AbsenceRequest request = new AbsenceRequest(
                            typeComboBox.getValue(),
                            fromDate,
                            toDate,
                            reasonField.getText(),
                            currentEmployee
                    );

                    // Zapisanie wniosku w bazie danych
                    absenceRepository.dodajWniosek(request);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sukces");
                    alert.setHeaderText("Wniosek został wysłany");
                    alert.setContentText("Oczekuj potwierdzenia od kierownika");
                    alert.showAndWait();

                    // Zamknięcie repozytorium
                    absenceRepository.close();

                    // Zamknięcie okna
                    stage.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showNotification("Błąd", "Wystąpił problem podczas zapisywania wniosku: " + ex.getMessage());
                }
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

        new ParallelTransition(ft, tt).play();
    }

    private void finalizeSale() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Finalizacja sprzedaży");
        alert.setHeaderText("Transakcja zakończona");
        alert.setContentText("Dziękujemy za zakupy w Stonce!");
        alert.showAndWait();
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

    private boolean validateReport(String type, String desc) {
        if (type == null || desc.trim().isEmpty()) {
            showNotification("Błąd", "Uzupełnij wszystkie pola");
            return false;
        }
        return true;
    }

    private void showNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Wylogowuje użytkownika i uruchamia okno logowania.
     */
    public void logout() {
        // Reset the current user
        UserRepository.resetCurrentEmployee();

        Stage primaryStage = cashierPanel.getPrimaryStage();
        primaryStage.close();
        HelloApplication.showLoginScreen(primaryStage);
    }

    // Inner class to represent items in the cart
    public static class TransactionItem {
        private final Product product;
        private int quantity;

        public TransactionItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getTotal() {
            return product.getPrice() * quantity;
        }
    }
}

