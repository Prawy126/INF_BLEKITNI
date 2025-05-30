/*
 * Classname: CashierPanelController
 * Version information: 1.10.0
 * Date: 2025-05-27
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import org.example.database.*;
import org.example.sys.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pdf.SalesReportGenerator;
import org.hibernate.Session;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Kontroler obsługujący panel kasjera.
 */
public class CashierPanelController {

    private static final Logger log = LoggerFactory.getLogger(CashierPanelController.class);
    private final CashierPanel cashierPanel;
    private final ReportRepository reportRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private boolean reportGeneratedInCurrentSession = false;

    // Ścieżka do katalogu z raportami
    private static final String REPORTS_DIRECTORY = "reports";

    public CashierPanelController(CashierPanel cashierPanel) {
        this.cashierPanel = cashierPanel;
        this.reportRepository = new ReportRepository();
        this.transactionRepository = new TransactionRepository();
        this.userRepository = new UserRepository();

        // Utworzenie katalogu na raporty, jeśli nie istnieje
        File reportsDir = new File(REPORTS_DIRECTORY);
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
    }

    /**
     * Ekran sprzedaży - przycisk do rozpoczęcia nowej transakcji.
     */
    public void showSalesScreen() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Button newTransactionButton = cashierPanel.createStyledButton("Rozpocznij nową transakcję");
        newTransactionButton.setOnAction(e -> startNewTransaction());
        layout.getChildren().add(newTransactionButton);
        cashierPanel.setCenterPane(layout);
    }

    /**
     * Okno nowej transakcji z wyszukiwarką produktów i koszykiem.
     */
    private void startNewTransaction() {
        Stage dialog = createStyledDialog("Nowa transakcja");
        dialog.setMinWidth(800);
        dialog.setMinHeight(600);

        ObservableList<TransactionItem> cartItems = FXCollections.observableArrayList();
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));

        // Lewa strona: wyszukiwanie produktów
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

        // Prawa strona: koszyk
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
        Button cancelButton  = cashierPanel.createStyledButton("Anuluj", "#E74C3C");
        buttonBox.getChildren().addAll(confirmButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        cartBox.getChildren().addAll(cartLabel, cartTable, totalBox, buttonBox);

        // Akcje przycisków
        addToCartButton.setOnAction(e -> {
            Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                int quantity = quantitySpinner.getValue();
                int availableQuantity = getAvailableQuantity(selectedProduct);

                if (availableQuantity < quantity) {
                    showNotification("Błąd", "Niewystarczająca ilość produktu. Dostępne: " + availableQuantity);
                    return;
                }
                boolean found = false;
                for (TransactionItem item : cartItems) {
                    if (item.getProduct().getId() == selectedProduct.getId()) {
                        int newQuantity = item.getQuantity() + quantity;
                        if (newQuantity > availableQuantity) {
                            showNotification("Błąd", "Niewystarczająca ilość produktu. Dostępne: " + availableQuantity);
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
                showNotification("Błąd", "Koszyk jest pusty. Dodaj produkty do koszyka.");
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

    /**
     * Pokazuje panel z raportami sprzedaży.
     * Wyświetla tylko raporty aktualnie zalogowanego pracownika.
     */
    public void showSalesReportsPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Raporty sprzedaży");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Report> tableView = createReportTable();

        HBox buttons = new HBox(10);
        Button newReportButton = cashierPanel.createStyledButton("Nowy raport", "#27AE60");
        Button refreshButton   = cashierPanel.createStyledButton("Odśwież", "#3498DB");
        newReportButton.setOnAction(e -> showReportDialog());
        refreshButton.setOnAction(e -> refreshReportTable(tableView));
        buttons.getChildren().addAll(newReportButton, refreshButton);
        buttons.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(titleLabel, tableView, buttons);
        cashierPanel.setCenterPane(layout);

        // Wczytaj raporty dla zalogowanego
        refreshReportTable(tableView);
    }

    /**
     * Odświeża tabelę raportów, pokazując tylko te wygenerowane przez aktualnego pracownika.
     */
    private void refreshReportTable(TableView<Report> tableView) {
        Employee currentEmployee = userRepository.getCurrentEmployee();
        if (currentEmployee == null) {
            tableView.setItems(FXCollections.observableArrayList());
            return;
        }
        // Jeśli ReportRepository ma metodę getReportsByEmployee(id), użyj jej zamiast filtra:
        // List<Report> reports = reportRepository.getReportsByEmployee(currentEmployee.getId());
        List<Report> allReports = reportRepository.getAllReports();
        List<Report> filtered = new ArrayList<>();
        for (Report r : allReports) {
            if (r.getEmployee() != null && r.getEmployee().getId() == currentEmployee.getId()) {
                filtered.add(r);
            }
        }
        tableView.setItems(FXCollections.observableArrayList(filtered));
    }

    /**
     * Oblicza granice dat dla raportów dziennych, miesięcznych, rocznych.
     */
    private LocalDate[] calculateReportDates(PeriodType periodType, LocalDate selectedDate) {
        return switch (periodType) {
            case DAILY   -> new LocalDate[]{selectedDate, selectedDate};
            case MONTHLY -> {
                LocalDate start = selectedDate.withDayOfMonth(1);
                yield new LocalDate[]{start, start.plusMonths(1).minusDays(1)};
            }
            case YEARLY  -> {
                LocalDate start = selectedDate.withDayOfYear(1);
                yield new LocalDate[]{start, start.plusYears(1).minusDays(1)};
            }
        };
    }

    /**
     * Dialog generowania raportu sprzedaży.
     */
    private void showReportDialog() {
        Stage dialog = createStyledDialog("Generowanie raportu sprzedaży");

        Label typeLabel = new Label("Typ raportu:");
        ComboBox<String> typeBox = createStyledComboBox(
                PeriodType.DAILY.getDisplayName(),
                PeriodType.MONTHLY.getDisplayName(),
                PeriodType.YEARLY.getDisplayName()
        );
        typeBox.setValue(PeriodType.DAILY.getDisplayName());

        Label dateLabel = new Label("Data raportu:");
        DatePicker datePicker = createStyledDatePicker();
        datePicker.setValue(LocalDate.now());

        Label categoryLabel = new Label("Kategorie (opcjonalnie):");
        ListView<String> categoryListView = new ListView<>();
        categoryListView.setPrefHeight(150);
        categoryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ProductRepository productRepo = new ProductRepository();
        categoryListView.setItems(FXCollections.observableArrayList(productRepo.getCategories()));
        productRepo.close();

        Button generateBtn = cashierPanel.createStyledButton("Generuj raport", "#2980B9");
        Button cancelBtn   = cashierPanel.createStyledButton("Anuluj", "#E74C3C");

        HBox buttonBox = new HBox(10, generateBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        generateBtn.setOnAction(e -> {
            String reportTypeStr = typeBox.getValue();
            LocalDate selectedDate = datePicker.getValue();
            List<String> selectedCategories = new ArrayList<>(categoryListView.getSelectionModel().getSelectedItems());

            if (selectedDate == null) {
                showNotification("Błąd", "Wybierz datę raportu.");
                return;
            }

            try {
                PeriodType periodType = getPeriodTypeFromString(reportTypeStr);
                LocalDate[] dates = calculateReportDates(periodType, selectedDate);

                Date d1 = Date.from(dates[0].atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date d2 = Date.from(dates[1].atTime(23,59,59).atZone(ZoneId.systemDefault()).toInstant());
                List<Transaction> transactions = transactionRepository.getTransactionsBetweenDates(d1, d2);

                if (transactions.isEmpty()) {
                    saveEmptyReportInfo(periodType, dates[0], dates[1]);
                    showNotification("Brak danych", "Nie znaleziono transakcji w wybranym okresie.");
                    dialog.close();
                    return;
                }

                String reportPath = generateSalesReport(periodType, dates[0], dates[1], selectedCategories);
                saveReportInfo(periodType, dates[0], dates[1], reportPath);
                showNotification("Sukces", "Raport zapisano: " + reportPath);
                dialog.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                showNotification("Błąd", "Nie udało się wygenerować raportu: " + ex.getMessage());
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        VBox root = new VBox(10,
                typeLabel, typeBox,
                dateLabel, datePicker,
                categoryLabel, categoryListView,
                new Separator(), buttonBox);
        root.setPadding(new Insets(20));

        setupDialog(dialog, root);
    }

    /**
     * Zapisuje w bazie informację o próbie wygenerowania raportu bez danych.
     */
    private void saveEmptyReportInfo(PeriodType periodType, LocalDate startDate, LocalDate endDate) {
        Employee current = userRepository.getCurrentEmployee();
        if (current == null) throw new IllegalStateException("Nie jesteś zalogowany.");

        Report report = new Report();
        report.setEmployee(current);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setReportType(periodType.getDisplayName() + " (brak danych)");
        report.setFilePath("");
        reportRepository.addReport(report);
        reportGeneratedInCurrentSession = true;
    }

    private PeriodType getPeriodTypeFromString(String typeStr) {
        return PeriodType.fromDisplay(typeStr);
    }

    public boolean isReportGeneratedInCurrentSession() {
        return reportGeneratedInCurrentSession;
    }

    /**
     * Zapisuje raport w bazie danyc
     */
    private void saveReportInfo(PeriodType periodType, LocalDate startDate, LocalDate endDate, String reportPath) {
        Employee current = userRepository.getCurrentEmployee();
        if (current == null) throw new IllegalStateException("Nie jesteś zalogowany.");

        switch (periodType) {
            case MONTHLY -> {
                startDate = startDate.withDayOfMonth(1);
                endDate   = startDate.plusMonths(1).minusDays(1);
            }
            case YEARLY  -> {
                startDate = startDate.withDayOfYear(1);
                endDate   = startDate.plusYears(1).minusDays(1);
            }
            default -> {}
        }

        Report report = new Report();
        report.setEmployee(current);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setReportType(periodType.getDisplayName());
        report.setFilePath(reportPath);
        reportRepository.addReport(report);
        reportGeneratedInCurrentSession = true;
    }

    /**
     * Tworzy tabelę raportów z kolumnami i przyciskami akcji.
     */
    private TableView<Report> createReportTable() {
        TableView<Report> tableView = new TableView<>();
        tableView.setMinHeight(300);

        TableColumn<Report, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(50);

        TableColumn<Report, String> typeColumn = new TableColumn<>("Typ raportu");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        typeColumn.setPrefWidth(120);

        TableColumn<Report, LocalDate> dateStartColumn = new TableColumn<>("Od");
        dateStartColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        dateStartColumn.setPrefWidth(100);

        TableColumn<Report, LocalDate> dateEndColumn = new TableColumn<>("Do");
        dateEndColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        dateEndColumn.setPrefWidth(100);

        TableColumn<Report, String> employeeColumn = new TableColumn<>("Wygenerował");
        employeeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmployee().getName() + " " +
                        cellData.getValue().getEmployee().getSurname()));
        employeeColumn.setPrefWidth(150);

        TableColumn<Report, Void> actionsColumn = new TableColumn<>("Akcje");
        actionsColumn.setPrefWidth(200);
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton   = new Button("Podgląd");
            private final Button openButton   = new Button("Otwórz");
            private final Button deleteButton = new Button("Usuń");
            private final HBox pane = new HBox(5, viewButton, openButton, deleteButton);

            {
                viewButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
                openButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");

                viewButton.setOnAction(event -> showReportDetails(getCurrentReport()));
                openButton.setOnAction(event -> openReportFile(getCurrentReport().getFilePath()));
                deleteButton.setOnAction(event -> confirmAndDeleteReport(getCurrentReport(), tableView));
            }

            private Report getCurrentReport() {
                return getTableView().getItems().get(getIndex());
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableView.getColumns().addAll(
                idColumn, typeColumn, dateStartColumn, dateEndColumn,
                employeeColumn, actionsColumn
        );
        return tableView;
    }

    private void openReportFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                showNotification("Błąd", "Plik nie istnieje: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showNotification("Błąd", "Nie można otworzyć pliku: " + e.getMessage());
        }
    }

    private void confirmAndDeleteReport(Report report, TableView<Report> tableView) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potwierdzenie usunięcia");
        alert.setHeaderText("Czy na pewno chcesz usunąć ten raport?");
        alert.setContentText("Ta operacja jest nieodwracalna.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            reportRepository.removeReport(report.getId());
            refreshReportTable(tableView);
            showNotification("Sukces", "Raport został usunięty.");
        }
    }

    private void showReportDetails(Report report) {
        Stage dialog = createStyledDialog("Szczegóły raportu");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("Raport ID: " + report.getId());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        infoGrid.setVgap(10);
        int row = 0;
        infoGrid.add(new Label("Typ raportu:"), 0, row);
        infoGrid.add(new Label(report.getReportType()), 1, row++);
        infoGrid.add(new Label("Okres:"), 0, row);
        infoGrid.add(new Label(report.getStartDate() + " - " + report.getEndDate()), 1, row++);
        infoGrid.add(new Label("Wygenerował:"), 0, row);
        infoGrid.add(new Label(report.getEmployee().getName() + " " + report.getEmployee().getSurname()), 1, row++);
        infoGrid.add(new Label("Ścieżka pliku:"), 0, row);
        infoGrid.add(new Label(report.getFilePath()), 1, row++);

        HBox buttonBox = new HBox(10);
        Button openButton = cashierPanel.createStyledButton("Otwórz plik", "#27AE60");
        Button closeButton = cashierPanel.createStyledButton("Zamknij", "#7F8C8D");
        openButton.setOnAction(e -> openReportFile(report.getFilePath()));
        closeButton.setOnAction(e -> dialog.close());
        buttonBox.getChildren().addAll(openButton, closeButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        content.getChildren().addAll(titleLabel, infoGrid, new Separator(), buttonBox);
        dialog.setScene(new Scene(content));
        dialog.showAndWait();
    }

    private void simulateGenerateReport(String reportType, LocalDate date, List<Transaction> transactions) {
        // Możliwość symulacji generowania
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

        // Nowa kolumna ze stanem magazynowym
        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stan");
        stockCol.setCellValueFactory(cd -> {
            Product p = cd.getValue();
            int qty = getAvailableQuantity(p);
            return new SimpleIntegerProperty(qty).asObject();
        });

        table.getColumns().addAll(idCol, nameCol, categoryCol, priceCol, stockCol);

        ProductRepository productRepo = new ProductRepository();
        ObservableList<Product> productList = FXCollections.observableArrayList(productRepo.getAllProducts());
        productRepo.close();
        table.setItems(productList);

        // Aktualizacja filtrowania z uwzględnieniem nowej kolumny nie jest potrzebna, ale odświeżamy stan przy każdym odfiltrowaniu
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            ObservableList<Product> filtered = FXCollections.observableArrayList();
            for (Product p : productList) {
                if (newVal == null || newVal.isEmpty()
                        || p.getName().toLowerCase().contains(newVal.toLowerCase())
                        || p.getCategory().toLowerCase().contains(newVal.toLowerCase())) {
                    filtered.add(p);
                }
            }
            table.setItems(filtered);
            // Odśwież kolumnę stanu magazynowego
            table.refresh();
        });

        return table;
    }

    private int getAvailableQuantity(Product product) {
        WarehouseRepository warehouseRepo = new WarehouseRepository();
        int qty = 0;
        try {
            Warehouse state = warehouseRepo.findStateByProductId(product.getId());
            if (state != null) qty = state.getQuantity();
        } finally {
            warehouseRepo.close();
        }
        return qty;
    }

    private TableView<TransactionItem> createCartTable() {
        TableView<TransactionItem> table = new TableView<>();
        table.setMinHeight(300);

        TableColumn<TransactionItem, String> nameCol = new TableColumn<>("Nazwa");
        nameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getProduct().getName()));

        TableColumn<TransactionItem, Integer> quantityCol = new TableColumn<>("Ilość");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantityCol.setOnEditCommit(ev -> {
            TransactionItem item = ev.getRowValue();
            int newVal = ev.getNewValue();
            int maxQty = getAvailableQuantity(item.getProduct());
            if (newVal > 0 && newVal <= maxQty) {
                item.setQuantity(newVal);
                updateTotalPrice(table.getItems(), null);
            } else {
                table.refresh();
                showNotification("Błąd", "Nieprawidłowa ilość. Maksymalna: " + maxQty);
            }
        });

        TableColumn<TransactionItem, Double> priceCol = new TableColumn<>("Cena jedn.");
        priceCol.setCellValueFactory(cd ->
                new SimpleDoubleProperty(cd.getValue().getProduct().getPrice().doubleValue()).asObject());

        TableColumn<TransactionItem, Double> totalCol = new TableColumn<>("Suma");
        totalCol.setCellValueFactory(cd ->
                new SimpleDoubleProperty(cd.getValue().getTotal()).asObject());

        TableColumn<TransactionItem, Void> actionCol = new TableColumn<>("Akcje");
        actionCol.setCellFactory(p -> new TableCell<>() {
            private final Button removeButton = new Button("Usuń");
            { removeButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
                removeButton.setOnAction(e -> {
                    TransactionItem item = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(item);
                    updateTotalPrice(getTableView().getItems(), null);
                });
            }
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : removeButton);
            }
        });

        table.getColumns().addAll(nameCol, quantityCol, priceCol, totalCol, actionCol);
        table.setEditable(true);
        return table;
    }

    private void updateTotalPrice(ObservableList<TransactionItem> items, Label totalLabel) {
        double total = 0;
        for (TransactionItem ti : items) total += ti.getTotal();
        if (totalLabel != null) totalLabel.setText(String.format("%.2f zł", total));
    }

    private void saveTransaction(ObservableList<TransactionItem> items, Stage dialog) {
        try {
            Employee current = userRepository.getCurrentEmployee();
            if (current == null) {
                showNotification("Błąd", "Nie jesteś zalogowany.");
                return;
            }

            // 1) Zapisz transakcję
            Transaction tx = new Transaction();
            tx.setEmployee(current);
            tx.setDate(new Date());
            transactionRepository.addTransaction(tx);
            int txId = tx.getId();

            // 2) Aktualizuj stan magazynowy i zapisuj pozycje
            WarehouseRepository whRepo = new WarehouseRepository();
            for (TransactionItem item : items) {
                int pid = item.getProduct().getId();
                int qty = item.getQuantity();
                int avail = whRepo.findStateByProductId(pid).getQuantity();
                whRepo.setProductQuantity(pid, avail - qty);

                TransactionProduct tp = new TransactionProduct();
                tp.setTransaction(transactionRepository.findTransactionById(txId));
                tp.setProduct(item.getProduct());
                tp.setQuantity(qty);

                TransactionProductRepository tpRepo = new TransactionProductRepository();
                tpRepo.addTransactionProduct(tp);
            }

            showNotification("Sukces", "Transakcja zapisana pomyślnie.");
            dialog.close();

        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Błąd", "Wystąpił błąd podczas zapisu: " + e.getMessage());
        }
    }



    /**
     * Panel zgłoszenia problemu przez kasjera.
     */
    public void showIssueReportPanel() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Zgłoszenie problemu");

        Label typeLabel = new Label("Typ zgłoszenia:");
        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList(
                "Awaria sprzętu", "Błąd oprogramowania", "Inne"));

        Label descLabel = new Label("Opis:");
        TextArea description = new TextArea();
        description.setPromptText("Opisz problem...");

        Button sendButton = new Button("Wyślij");
        sendButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-weight: bold;");
        sendButton.setOnAction(e -> {
            if (typeBox.getValue() == null || description.getText().trim().isEmpty()) {
                showNotification("Błąd", "Uzupełnij wszystkie pola.");
            } else {
                showNotification("Sukces", "Zgłoszenie wysłane.");
                dialog.close();
            }
        });

        Button cancelButton = new Button("Anuluj");
        cancelButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;");
        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10, sendButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(15, typeLabel, typeBox, descLabel, description, buttonBox);
        root.setPadding(new Insets(20));

        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    /**
     * Panel zamknięcia zmiany - wymusza raport dzienny.
     */
    public void showCloseShiftPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        if (!reportGeneratedInCurrentSession) {
            Label warning = new Label("Uwaga: Nie wygenerowano jeszcze raportu dziennego!");
            warning.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
            Button genBtn = cashierPanel.createStyledButton("Wygeneruj raport dzienny", "#3498DB");
            genBtn.setOnAction(e -> {
                showReportDialog();
                // Ustaw flagę po wygenerowaniu raportu
                reportGeneratedInCurrentSession = isDailyReportGeneratedToday();
            });
            layout.getChildren().addAll(warning, genBtn);
        }

        Button confirmButton = cashierPanel.createStyledButton("Potwierdź zamknięcie zmiany", "#E67E22");
        confirmButton.setOnAction(e -> {
            boolean today = isDailyReportGeneratedToday();

            if (!reportGeneratedInCurrentSession && !today) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ostrzeżenie");
                alert.setHeaderText("Nie wygenerowano raportu dziennego");
                alert.setContentText("Przed zamknięciem zmiany wygeneruj raport dzienny.");
                ButtonType gen = new ButtonType("Generuj raport");
                ButtonType ign = new ButtonType("Ignoruj i zamknij");
                ButtonType canc = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(gen, ign, canc);

                Optional<ButtonType> res = alert.showAndWait();
                if (res.isPresent()) {
                    if (res.get() == gen) {
                        showReportDialog();
                        reportGeneratedInCurrentSession = isDailyReportGeneratedToday();
                        return;
                    } else if (res.get() == canc) {
                        return;
                    }
                }
            }

            // Zakończ zadania i wyloguj
            Employee current = userRepository.getCurrentEmployee();
            if (current != null) {
                completeAllTasksForEmployee(current.getId());
            }

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Zamknięcie zmiany");
            success.setHeaderText("Zmiana została pomyślnie zamknięta");
            success.setContentText("Dziękujemy za pracę w tej zmianie!");
            success.showAndWait();

            // Zresetuj flagę po pomyślnym zamknięciu zmiany
            reportGeneratedInCurrentSession = false;

            // Wyloguj użytkownika
            userRepository.resetCurrentEmployee();
            Stage primaryStage = cashierPanel.getPrimaryStage();
            primaryStage.close();
            HelloApplication.showLoginScreen(primaryStage);
        });
        reportGeneratedInCurrentSession = false; // Reset flagi przy otwarciu panelu
        layout.getChildren().add(confirmButton);
        cashierPanel.setCenterPane(layout);
    }

    public void resetReportGeneratedFlag() {
        reportGeneratedInCurrentSession = false;
    }

    /**
     * Sprawdza, czy dzienny raport wygenerowano już dziś.
     */
    public boolean isDailyReportGeneratedToday() {
        LocalDate today = LocalDate.now();
        Employee current = userRepository.getCurrentEmployee();
        if (current == null) {
            return false;
        }
        List<Report> todays = reportRepository.getEmployeeDayReport(current.getId(), today);
        return !todays.isEmpty();
    }

    public void markReportAsGenerated() {
        this.reportGeneratedInCurrentSession = true;
    }

    public void checkReportFlagState(String panelName) {
        System.out.println("Flaga przed przejściem do " + panelName + ": " + reportGeneratedInCurrentSession);
    }

    /**
     * Formularz wniosku o nieobecność.
     */
    public void showAbsenceRequestForm() {
        Stage stage = new Stage();
        stage.setTitle("Wniosek o nieobecność");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        UserRepository ur = new UserRepository();
        Employee current = ur.getCurrentEmployee();
        if (current == null) {
            showNotification("Błąd", "Nie jesteś zalogowany.");
            ur.close();
            return;
        }

        Label info = new Label(String.format("Pracownik: %s %s (ID: %d)",
                current.getName(), current.getSurname(), current.getId()));
        info.setStyle("-fx-font-weight: bold;");

        Label typeLabel = new Label("Typ wniosku:");
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList(
                "Urlop wypoczynkowy", "Urlop na żądanie", "Zwolnienie lekarskie", "Inne"));
        typeCombo.setValue("Urlop wypoczynkowy");

        Label reasonLabel = new Label("Opis:");
        TextField reasonField = new TextField();

        Label fromDateLabel = new Label("Data od:");
        DatePicker fromDatePicker = new DatePicker();

        Label toDateLabel = new Label("Data do:");
        DatePicker toDatePicker = new DatePicker();

        Button submit = cashierPanel.createStyledButton("Wyślij wniosek", "#27AE60");
        submit.setOnAction(e -> {
            if (validateAbsenceForm(reasonField.getText(), fromDatePicker.getValue(), toDatePicker.getValue())) {
                showNotification("Sukces", "Wniosek został wysłany.");
                stage.close();
            }
        });

        grid.add(info, 0, 0, 2, 1);
        grid.add(typeLabel, 0, 1);
        grid.add(typeCombo, 1, 1);
        grid.add(reasonLabel, 0, 2);
        grid.add(reasonField, 1, 2);
        grid.add(fromDateLabel, 0, 3);
        grid.add(fromDatePicker, 1, 3);
        grid.add(toDateLabel, 0, 4);
        grid.add(toDatePicker, 1, 4);
        grid.add(submit, 1, 5);

        Scene scene = new Scene(grid, 400, 350);
        stage.setScene(scene);
        stage.setOnHidden(evt -> ur.close());
        stage.show();
    }

    private boolean validateAbsenceForm(String reason, LocalDate from, LocalDate to) {
        if (reason == null || reason.trim().isEmpty()) {
            showNotification("Błąd", "Musisz podać powód nieobecności");
            return false;
        }
        if (from == null || to == null) {
            showNotification("Błąd", "Musisz wybrać daty");
            return false;
        }
        if (from.isAfter(to)) {
            showNotification("Błąd", "Data od nie może być później niż do");
            return false;
        }
        return true;
    }

    private void animateDialog(Stage dialog, Pane root) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), root);
        ft.setFromValue(0.0); ft.setToValue(1.0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), root);
        tt.setFromY(-20); tt.setToY(0);
        new ParallelTransition(ft, tt).play();
    }

    private Stage createStyledDialog(String title) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);
        return dialog;
    }

    private TextField createStyledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 8px;");
        return tf;
    }

    private <T> ComboBox<T> createStyledComboBox(T... items) {
        ComboBox<T> box = new ComboBox<>(FXCollections.observableArrayList(items));
        box.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 6px;");
        return box;
    }

    private DatePicker createStyledDatePicker() {
        DatePicker dp = new DatePicker();
        dp.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 6px;");
        return dp;
    }

    private void setupDialog(Stage dialog, Pane root) {
        dialog.setScene(new Scene(root));
        animateDialog(dialog, root);
        dialog.showAndWait();
    }

    private void showNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Generuje plik PDF z raportem sprzedaży.
     */
    private String generateSalesReport(PeriodType periodType,
                                       LocalDate startDate,
                                       LocalDate endDate,
                                       List<String> categories) throws Exception {

        List<SalesReportGenerator.SalesRecord> salesData = getSalesDataForReport(startDate, endDate);
        if (salesData.isEmpty()) {
            throw new SalesReportGenerator.NoDataException("Brak danych transakcji");
        }

        SalesReportGenerator gen = new SalesReportGenerator();
        gen.setSalesData(salesData);

        String fileName = String.format("raport_%s_%s_%s.pdf",
                periodType.getDisplayName().toLowerCase(),
                startDate.format(DateTimeFormatter.BASIC_ISO_DATE),
                endDate.format(DateTimeFormatter.BASIC_ISO_DATE));
        String outputPath = REPORTS_DIRECTORY + File.separator + fileName;

        pdf.SalesReportGenerator.PeriodType pdfType = toPdfPeriodType(periodType);
        gen.generateReport(outputPath, pdfType, categories == null ? List.of() : categories);

        return new File(outputPath).getAbsolutePath();
    }

    private List<SalesReportGenerator.SalesRecord> getSalesDataForReport(LocalDate startDate, LocalDate endDate) {
        Date d1 = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date d2 = Date.from(endDate.atTime(23,59,59).atZone(ZoneId.systemDefault()).toInstant());

        List<Transaction> txs = transactionRepository.getTransactionsBetweenDates(d1, d2);
        List<SalesReportGenerator.SalesRecord> out = new ArrayList<>();
        for (Transaction tx : txs) {
            LocalDateTime txTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(tx.getDate().getTime()), ZoneId.systemDefault());
            for (TransactionProduct tp : tx.getTransactionProducts()) {
                Product p = tp.getProduct();
                int qty = tp.getQuantity();
                out.add(new SalesReportGenerator.SalesRecord(
                        tx.getId(), txTime, p.getName(), p.getCategory(),
                        qty, qty * p.getPrice().doubleValue()));
            }
        }
        return out;
    }

    private pdf.SalesReportGenerator.PeriodType toPdfPeriodType(PeriodType periodType) {
        return switch (periodType) {
            case DAILY   -> pdf.SalesReportGenerator.PeriodType.DAILY;
            case MONTHLY -> pdf.SalesReportGenerator.PeriodType.MONTHLY;
            case YEARLY  -> pdf.SalesReportGenerator.PeriodType.YEARLY;
        };
    }

    /**
     * Klasa pomocnicza reprezentująca pozycję w koszyku.
     */
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
        public double getTotal() { return product.getPrice().doubleValue() * quantity; }
    }

    /**
     * Wylogowuje kasjera, resetując flagę, kończąc zadania i przechodząc do ekranu logowania.
     */
    public void logout() {
        reportGeneratedInCurrentSession = false; // Resetuj flagę po zakończeniu zmiany
        if (!reportGeneratedInCurrentSession) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie wygenerowano raportu dziennego");
            alert.setContentText("Czy chcesz się wylogować bez raportu dziennego?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }

        Employee current = userRepository.getCurrentEmployee();
        if (current != null) {
            completeAllTasksForEmployee(current.getId());
        }

        // Zresetuj flagę przy wylogowaniu
        reportGeneratedInCurrentSession = false;

        // Wyloguj użytkownika
        userRepository.resetCurrentEmployee();
        Stage primaryStage = cashierPanel.getPrimaryStage();
        primaryStage.close();
        HelloApplication.showLoginScreen(primaryStage);
    }

    /**
     * Ustawia status „completed” dla WSZYSTKICH zadań przypisanych bieżącemu
     * pracownikowi i rozpoczętych DZISIAJ.  Aktualizacja odbywa się w jednej
     * transakcji na każdym rekordzie, dzięki czemu zmiany są trwałe.
     */
    public void completeAllTasksForEmployee(int employeeId) {
        try (TaskEmployeeRepository repo = new TaskEmployeeRepository()) {

            List<TaskEmployee> todays =
                    repo.findEmployeeTasksForDate(employeeId, LocalDate.now());

            log.info("completeAllTasksForEmployee() – zamykam {} zadań pracownika {} ({}).",
                    todays.size(), employeeId, LocalDate.now());

            for (TaskEmployee te : todays) {
                // ustawiamy czas zakończenia, obliczamy czas trwania i status
                te.setEndTime(LocalDateTime.now());
                te.getTask().setStatus("completed");         // aktualizujemy encję Task

                repo.updateTaskStatus(te.getTask().getId(), "Zakończone");
                // zapis w bazie
            }
        }
    }

}
