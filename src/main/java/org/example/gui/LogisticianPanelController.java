/*
 * Classname: LogisticianPanelController
 * Version information: 1.5
 * Date: 2025-06-06
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.example.database.*;
import org.example.pdflib.ConfigManager;
import org.example.sys.*;

import pdf.WarehouseRaport;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static javafx.scene.control.Alert.AlertType.ERROR;

/**
 * Kontroler obsługujący logikę interfejsu użytkownika
 * dla panelu logistyka.
 */
public class LogisticianPanelController {

    private final LogisticianPanel logisticianPanel;
    private final Stage primaryStage;
    private static final Logger logger = LogManager.getLogger(LogisticianPanelController.class);
    private final ProductRepository productRepository = new ProductRepository();
    private final WarehouseRepository warehouseRepository = new WarehouseRepository();
    private boolean reportGeneratedInCurrentSession;

    /**
     * Konstruktor przypisujący panel logistyka.
     *
     * @param logisticianPanel obiekt klasy LogisticianPanel
     */
    public LogisticianPanelController(LogisticianPanel logisticianPanel) {
        this.logisticianPanel = logisticianPanel;
        this.primaryStage = logisticianPanel.getPrimaryStage();
        reportGeneratedInCurrentSession = false;
        logger.info("LogisticianPanelController utworzony i przypisany do panelu wartość {}", logisticianPanel);
        this.primaryStage.setOnCloseRequest(event -> {
            if (!reportGeneratedInCurrentSession) {
                event.consume();  // Zatrzymaj zamknięcie
                showAlert(Alert.AlertType.ERROR,
                        "Zamknięcie zablokowane",
                        "Musisz wygenerować raport, aby zamknąć aplikację.");
            }
        });
        logger.debug("Ustawiono obsługę zdarzenia zamknięcia okna");
    }

    public void showInventoryManagement() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label title = new Label("Zarządzanie magazynem");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        /* -------- tabela stanów magazynowych -------- */
        TableView<StockRow> table = new TableView<>();
        table.setMinHeight(250);

        TableColumn<StockRow,Integer> idCol  = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<StockRow,String> nameCol = new TableColumn<>("Nazwa");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<StockRow,Integer> qtyCol = new TableColumn<>("Ilość");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        table.getColumns().addAll(idCol, nameCol, qtyCol);
        refreshStockTable(table);

        /* -------- przyciski -------- */
        Button filterBtn  = new Button("Filtruj");
        styleLogisticButton(filterBtn, "#2980B9");
        filterBtn.setOnAction(e -> showFilterStockDialog(table));

        Button refreshBtn = new Button("Odśwież");
        styleLogisticButton(refreshBtn, "#3498DB");
        refreshBtn.setOnAction(e -> refreshStockTable(table));

        Button reportsBtn = new Button("Raporty");
        styleLogisticButton(reportsBtn, "#27AE60");
        reportsBtn.setOnAction(e -> showInventoryReports());

        Button addProductBtn = new Button("Dodaj produkt");
        styleLogisticButton(addProductBtn, "#27AE60");
        addProductBtn.setOnAction(e -> showAddProductDialog(table));

        HBox btnBox = new HBox(10, filterBtn, refreshBtn, reportsBtn, addProductBtn);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        layout.getChildren().addAll(title, table, btnBox);
        logisticianPanel.setCenterPane(layout);
    }

    /**
     * Ładuje i wyświetla w tabeli aktualne stany magazynowe (id, nazwa, ilość).
     *
     * @param table tabela do wypełnienia danymi
     */
    private void refreshStockTable(TableView<StockRow> table) {
        try {
            Map<Integer,Integer> qtyById = warehouseRepository.getAllStates()
                    .stream()
                    .collect(Collectors.toMap(Warehouse::getProductId,
                            Warehouse::getQuantity));

            List<StockRow> rows = productRepository.getAllProducts().stream()
                    .filter(p -> qtyById.containsKey(p.getId()))
                    .map(p -> new StockRow(
                            p.getId(),
                            p.getName(),
                            qtyById.getOrDefault(p.getId(),0)))
                    .toList();

            table.setItems(FXCollections.observableArrayList(rows));
        } catch (Exception ex) {
            logger.error("Błąd ładowania stanów",ex);
            showAlert(ERROR,"Błąd","Nie udało się pobrać stanów magazynowych");
        }
    }

    /**
     * Otwiera panel „Zamówienia” z tabelą wszystkich zamówień.
     * Ustawia kolumny, ładuje dane z repozytorium oraz tworzy przyciski Dodaj, Filtruj i Odśwież.
     */
    public void showOrdersPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Zamówienia");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<Order> tableView = new TableView<>();
        tableView.setMinHeight(200);

        TableColumn<Order, Integer> idCol = new TableColumn<>("Id");
        TableColumn<Order, String> productCol = new TableColumn<>("Produkt");
        TableColumn<Order, String> employeeCol = new TableColumn<>("Pracownik");
        TableColumn<Order, Integer> qtyCol = new TableColumn<>("Ilość");
        TableColumn<Order, BigDecimal> priceCol = new TableColumn<>("Cena");
        TableColumn<Order, Date> dateCol = new TableColumn<>("Data");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getProduct() != null ? cellData.getValue().getProduct().getName() : ""));
        employeeCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getEmployee() != null ? cellData.getValue().getEmployee().getLogin() : ""));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        tableView.getColumns().addAll(idCol, productCol, employeeCol, qtyCol, priceCol, dateCol);

        OrderRepository orderRepo = new OrderRepository();
        List<Order> orders = orderRepo.getAllOrders();
        tableView.setItems(FXCollections.observableArrayList(orders));

        Button addOrderButton = new Button("Dodaj zamówienie");
        styleLogisticButton(addOrderButton, "#27AE60");
        addOrderButton.setOnAction(e -> showAddOrderForm());

        Button filterButton = new Button("Filtruj");
        styleLogisticButton(filterButton, "#2980B9");
        filterButton.setOnAction(e -> showFilterOrderDialog(tableView));

        Button refreshBtn = new Button("Odśwież");
        styleLogisticButton(refreshBtn, "#3498DB");
        refreshBtn.setOnAction(e ->
                tableView.setItems(FXCollections.observableArrayList(new OrderRepository().getAllOrders()))
        );

        HBox btnBox = new HBox(10, addOrderButton, filterButton, refreshBtn);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        layout.getChildren().addAll(titleLabel, tableView, btnBox);
        logisticianPanel.setCenterPane(layout);
    }

    /**
     * Otwiera prosty dialog filtrowania zamówień po ID zamówienia i ID produktu.
     * Po zatwierdzeniu zastępuje zawartość tabeli wyfiltrowanymi rekordami.
     *
     * @param tableView tabela zamówień, która ma zostać przefiltrowana
     */
    private void showFilterOrderDialog(TableView<Order> tableView) {
        Stage stage = new Stage();
        stage.setTitle("Filtrowanie zamówień");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label idLabel = new Label("Id zamówienia:");
        TextField idField = new TextField();

        Label productIdLabel = new Label("Id produktu:");
        TextField productIdField = new TextField();

        Button filterButton = new Button("Filtruj");
        styleLogisticButton(filterButton, "#27AE60");
        filterButton.setOnAction(ev -> {
            ObservableList<Order> base =
                    FXCollections.observableArrayList(new OrderRepository().getAllOrders());

            ObservableList<Order> out = base.filtered(o -> {
                if (!idField.getText().isBlank()) {
                    try {
                        if (o.getId() != Integer.parseInt(idField.getText().trim()))
                            return false;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                }
                if (!productIdField.getText().isBlank()) {
                    try {
                        if (o.getProduct().getId() != Integer.parseInt(productIdField.getText().trim()))
                            return false;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                }
                return true;
            });

            tableView.setItems(out);
            stage.close();
        });

        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(productIdLabel, 0, 1);
        grid.add(productIdField, 1, 1);
        grid.add(filterButton, 1, 2);

        stage.setScene(new Scene(grid, 320, 180));
        stage.show();
    }

    /**
     * Otwiera panel „Raporty magazynowe” z przyciskiem uruchamiającym kreator raportu.
     * Wyświetla ekran raportów magazynowych.
     */
    public void showInventoryReports() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Raporty magazynowe");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button generateButton = new Button("Generuj raport");
        styleLogisticButton(generateButton, "#27AE60");
        generateButton.setOnAction(e -> showReportDialog());

        layout.getChildren().addAll(titleLabel, generateButton);
        logisticianPanel.setCenterPane(layout);
    }

    /**
     * Okienko konfiguracji: wybór kategorii i progu niskiego stanu magazynowego.
     * Po naciśnięciu „Generuj” tworzy PDF-owy raport.
     */
    private void showReportDialog() {
        Stage stage = new Stage();
        stage.setTitle("Generuj raport magazynowy");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        Label categoriesLabel = new Label("Wybierz kategorie:");
        ListView<String> categoriesList = new ListView<>();
        try {
            categoriesList.getItems().addAll(productRepository.getCategories());
        } catch (Exception ex) {
            logger.error("Błąd pobierania kategorii", ex);
        }
        categoriesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Label thresholdLabel = new Label("Próg niskiego stanu magazynowego:");
        Spinner<Integer> thresholdSpinner = new Spinner<>(1, 100, 5);

        Label pathInfoLabel = new Label("Plik zostanie zapisany w katalogu:");
        TextField pathDisplay = new TextField();
        pathDisplay.setEditable(false);
        pathDisplay.setFocusTraversable(false);
        pathDisplay.setPrefWidth(400);
        pathDisplay.setText(ConfigManager.getReportPath());

        Button generate = new Button("Generuj");
        styleLogisticButton(generate, "#27AE60");
        generate.setOnAction(e ->
                handleGenerateButton(
                        new ArrayList<>(categoriesList.getSelectionModel().getSelectedItems()),
                        thresholdSpinner.getValue(),
                        stage
                )
        );

        grid.add(categoriesLabel, 0, 0);
        grid.add(categoriesList, 1, 0);
        grid.add(thresholdLabel, 0, 1);
        grid.add(thresholdSpinner, 1, 1);
        grid.add(pathInfoLabel, 0, 2);
        grid.add(pathDisplay, 1, 2);
        grid.add(generate, 1, 3);

        stage.setScene(new Scene(grid, 600, 350));
        stage.show();
    }

    /**
     * Pokazuje systemowy FileChooser do wybrania katalogu i pliku .pdf.
     *
     * @param stage okno-rodzic dla FileChooser
     * @param outputPath pole tekstowe, w które wpisany zostanie wybrany path
     */
    private void handleBrowseButton(Stage stage, TextField outputPath) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            outputPath.setText(file.getAbsolutePath());
        }
    }

    /**
     * Generuje raport magazynowy na podstawie wybranych kategorii i progu niskiego stanu.
     * Zapisuje go w katalogu zdefiniowanym w ConfigManager.
     *
     * @param selectedCategories lista wybranych kategorii
     * @param lowStockThreshold próg niskiego stanu
     * @param stage dialog-rodzic, który zostanie zamknięty po wygenerowaniu
     */
    private void handleGenerateButton(List<String> selectedCategories,
                                      int lowStockThreshold,
                                      Stage stage) {

        String basePath = ConfigManager.getReportPath();
        if (basePath == null || basePath.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Brak ścieżki",
                    "Ustaw domyślną ścieżkę zapisu raportów w ustawieniach administratora.");
            return;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File targetFile = new File(basePath, "warehouse-report-" + timestamp + ".pdf");

        try {
            List<org.example.sys.Product> products = productRepository.getAllProducts();
            Map<Integer, Integer> qtyById = warehouseRepository.getAllStates()
                    .stream()
                    .collect(Collectors.toMap(
                            org.example.sys.Warehouse::getProductId,
                            org.example.sys.Warehouse::getQuantity
                    ));

            List<org.example.sys.Product> filteredProducts = products.stream()
                    .filter(p ->
                            selectedCategories.isEmpty() ||
                                    selectedCategories.contains(p.getCategory()))
                    .toList();

            WarehouseRaport.ProductDataExtractor<org.example.sys.Product> extractor =
                    new WarehouseRaport.ProductDataExtractor<>() {
                        public String getName(org.example.sys.Product p)     { return p.getName(); }
                        public String getCategory(org.example.sys.Product p) { return p.getCategory(); }
                        public double getPrice(org.example.sys.Product p)    { return p.getPrice().doubleValue(); }
                        public int getQuantity(org.example.sys.Product p)    { return qtyById.getOrDefault(p.getId(), 0); }
                    };

            WarehouseRaport raport = new WarehouseRaport();
            raport.setLogoPath("src/main/resources/logo.png");
            raport.setLowStockThreshold(lowStockThreshold);
            raport.generateReport(targetFile.getAbsolutePath(), filteredProducts, extractor, selectedCategories);

            showAlert(Alert.AlertType.INFORMATION, "Sukces",
                    "Raport zapisany: " + targetFile.getAbsolutePath());
            stage.close();
            reportGeneratedInCurrentSession = true; // raport został wygenerowany w tej sesji
            logger.info("Raport magazynowy wygenerowany: {}", targetFile.getAbsolutePath());
            logger.info("Flag reportGeneratedInCurrentSession ustawiona na true");

        } catch (Exception ex) {
            logger.error("Błąd generowania raportu", ex);
            showAlert(ERROR, "Błąd",
                    "Generowanie raportu nie powiodło się: " + ex.getMessage());
        }
    }

    /* -------------------------------------------------------------------- */
    /*  PANEL ZAMKNIĘCIA ZMIANY                                             */
    /* -------------------------------------------------------------------- */
    public void showCloseShiftPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label info = new Label("Zamknięcie zmiany (logistyk)");
        info.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button genBtn  = new Button("Generuj raport dzienny");
        styleLogisticButton(genBtn, "#2980B9");
        genBtn.setOnAction(e -> showReportDialog());

        Button confirm = new Button("Potwierdź zamknięcie zmiany");
        styleLogisticButton(confirm, "#E67E22");
        confirm.setOnAction(e -> {
            if (!reportGeneratedInCurrentSession) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Brak raportu");
                alert.setHeaderText("Nie wygenerowano raportu dziennego");
                alert.setContentText("Czy mimo to chcesz zamknąć aplikację?");

                ButtonType cancelBtn = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
                ButtonType forceExit = new ButtonType("Zamknij mimo wszystko", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll(cancelBtn, forceExit);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == forceExit) {
                    logger.warn("Forsowne wylogowanie logistyka bez raportu dziennego");
                    reportGeneratedInCurrentSession = false;
                    logoutByForce();
                }
                return;
            }
            logout();
        });

        layout.getChildren().addAll(info, genBtn, confirm);
        logisticianPanel.setCenterPane(layout);
    }



    /**
     * Otwiera formularz dodawania nowego zamówienia.
     * Pole "Produkt" zastąpiono ComboBoxem wypełnionym nazwami produktów.
     * Pole "Ilość" oraz "Data" pozostały bez zmian.
     * ID pracownika pobierane jest automatycznie z zalogowanego użytkownika,
     * a cena jest liczona jako (cena jednostkowa produktu * ilość).
     */
    private void showAddOrderForm() {
        Stage stage = new Stage();
        stage.setTitle("Dodaj zamówienie");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label productLabel = new Label("Produkt:");
        Label qtyLabel     = new Label("Ilość:");
        Label dateLabel    = new Label("Data:");

        // 1) ComboBox z produktami zamiast TextField na ID
        ComboBox<Product> productComboBox = new ComboBox<>();
        productComboBox.setPrefWidth(200);
        productComboBox.setPromptText("Wybierz produkt");
        // Wypełniamy ComboBox wszystkimi produktami (z repozytorium)
        List<Product> allProducts = productRepository.getAllProducts();
        productComboBox.getItems().addAll(allProducts);
        // Wyświetlamy w liście jedynie nazwę produktu
        productComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        productComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        TextField quantityField = new TextField();
        quantityField.setPromptText("liczba całkowita");

        DatePicker datePicker = new DatePicker();

        Button submit = new Button("Zapisz");
        styleLogisticButton(submit, "#27AE60");
        submit.setOnAction(ev -> {
            // 2) Sprawdzenie, czy wszystkie pola wypełniono
            if (productComboBox.getValue() == null
                    || quantityField.getText().isBlank()
                    || datePicker.getValue() == null) {
                showAlert(Alert.AlertType.WARNING,
                        "Brak danych",
                        "Uzupełnij wszystkie pola (produkt, ilość, data)");
                return;
            }

            try {
                // 3) Parsowanie ilości
                int qty = Integer.parseInt(quantityField.getText().trim());
                if (qty <= 0) {
                    showAlert(Alert.AlertType.WARNING,
                            "Nieprawidłowa ilość",
                            "Ilość musi być dodatnią liczbą całkowitą");
                    return;
                }

                // 4) Pobranie zalogowanego pracownika
                UserRepository ur = new UserRepository();
                Employee empl = ur.getCurrentEmployee();
                if (empl == null) {
                    showAlert(Alert.AlertType.ERROR,
                            "Błąd",
                            "Brak zalogowanego pracownika. Zaloguj się ponownie.");
                    return;
                }

                // 5) Pobranie wybranego produktu z ComboBoxa
                Product prod = productComboBox.getValue();

                // 6) Obliczenie ceny: cena jednostkowa * ilość
                BigDecimal unitPrice = prod.getPrice();
                BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(qty));

                // 7) Utworzenie i zapis zamówienia
                Order ord = new Order();
                ord.setProduct(prod);
                ord.setEmployee(empl);
                ord.setQuantity(qty);
                ord.setPrice(totalPrice);
                ord.setDate(java.sql.Date.valueOf(datePicker.getValue()));

                OrderRepository or = new OrderRepository();
                or.addOrder(ord);

                showAlert(Alert.AlertType.INFORMATION,
                        "Sukces",
                        "Zapisano zamówienie (ID=" + ord.getId() +
                                ", cena = " + totalPrice + ")");
                stage.close();
                showOrdersPanel(); // odśwież panel zamówień

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR,
                        "Błąd",
                        "Pole Ilość musi być liczbą całkowitą!");
            } catch (Exception ex) {
                logger.error("Błąd dodawania zamówienia", ex);
                showAlert(Alert.AlertType.ERROR,
                        "Błąd",
                        "Nie udało się dodać zamówienia:\n" + ex.getMessage());
            }
        });

        // Ułożenie kontrolek w siatce
        grid.add(productLabel,       0, 0);
        grid.add(productComboBox,    1, 0);
        grid.add(qtyLabel,           0, 1);
        grid.add(quantityField,      1, 1);
        grid.add(dateLabel,          0, 2);
        grid.add(datePicker,         1, 2);
        grid.add(submit,             1, 3);

        stage.setScene(new Scene(grid, 380, 230));
        stage.show();
    }

    /**
     * Otwiera okienko filtrowania stanów magazynowych.
     * Po zatwierdzeniu pobiera pełny zestaw danych, filtruje po Id, nazwie i minimalnej ilości
     * i aktualizuje tabelę.
     *
     * @param table tabela stanów magazynowych, która ma zostać przefiltrowana
     */
    private void showFilterStockDialog(TableView<StockRow> table) {
        Stage st = new Stage();
        st.setTitle("Filtrowanie stanów");
        GridPane g = new GridPane();
        g.setPadding(new Insets(20)); g.setHgap(10); g.setVgap(10);

        TextField idF   = new TextField();
        TextField nameF = new TextField();
        TextField qtyF  = new TextField();

        g.addRow(0,new Label("Id:"),           idF);
        g.addRow(1,new Label("Nazwa:"),        nameF);
        g.addRow(2,new Label("Min. ilość:"),   qtyF);

        Button filt = new Button("Filtruj");
        styleLogisticButton(filt, "#2980B9");
        filt.setOnAction(ev -> {
            refreshStockTable(table);
            ObservableList<StockRow> base = table.getItems();
            ObservableList<StockRow> out = base.filtered(r -> {
                if (!idF.getText().isBlank()) {
                    try {
                        if (r.getId() != Integer.parseInt(idF.getText().trim()))
                            return false;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                }
                if (!nameF.getText().isBlank() &&
                        !r.getName().toLowerCase().contains(nameF.getText().toLowerCase())) {
                    return false;
                }
                if (!qtyF.getText().isBlank()) {
                    try {
                        int minQty = Integer.parseInt(qtyF.getText().trim());
                        if (r.getQuantity() < minQty) return false;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                }
                return true;
            });
            table.setItems(out);
            st.close();
        });

        g.add(filt,1,3);

        st.setScene(new Scene(g,320,220));
        st.show();
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
        styleLogisticButton(filterButton, "#2980B9");
        filterButton.setOnAction(e -> stage.close());

        grid.add(idLabel, 0, 0);       grid.add(idField, 1, 0);
        grid.add(nameLabel, 0, 1);     grid.add(nameField, 1, 1);
        grid.add(priceLabel, 0, 2);    grid.add(priceField, 1, 2);
        grid.add(stockLabel, 0, 3);    grid.add(stockField, 1, 3);
        grid.add(filterButton, 1, 4);

        stage.setScene(new Scene(grid, 360, 250));
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
        styleLogisticButton(submitButton, "#27AE60");
        submitButton.setOnAction(ev -> {
            if (reasonField.getText().isBlank()
                    || fromDatePicker.getValue() == null
                    || toDatePicker.getValue()   == null) {
                showAlert(Alert.AlertType.WARNING, "Brak danych",
                        "Uzupełnij wszystkie pola");
                return;
            }

            try {
                UserRepository ur = new UserRepository();
                Employee emp = ur.getCurrentEmployee();
                if (emp == null) {
                    showAlert(ERROR,"Błąd","Brak zalogowanego pracownika");
                    return;
                }

                AbsenceRequest ar = new AbsenceRequest();
                ar.setRequestType("Inny");
                ar.setDescription(reasonField.getText());
                ar.setStartDate(java.sql.Date.valueOf(fromDatePicker.getValue()));
                ar.setEndDate  (java.sql.Date.valueOf(toDatePicker.getValue()));
                ar.setEmployee(emp);

                new AbsenceRequestRepository().addRequest(ar);
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Wniosek zapisany");
                stage.close();

            } catch (Exception ex) {
                logger.error("Błąd zapisu wniosku", ex);
                showAlert(ERROR, "Błąd", "Nie udało się zapisać wniosku");
            }
        });

        grid.add(reasonLabel, 0, 0);   grid.add(reasonField, 1, 0);
        grid.add(fromDateLabel, 0, 1); grid.add(fromDatePicker, 1, 1);
        grid.add(toDateLabel, 0, 2);   grid.add(toDatePicker, 1, 2);
        grid.add(submitButton, 1, 3);

        stage.setScene(new Scene(grid, 350, 250));
        stage.show();
    }

    /**
     * Wyświetla prosty alert z podanym typem, tytułem i treścią.
     *
     * @param type typ alertu (INFORMATION, WARNING, ERROR)
     * @param title tytuł okna alertu
     * @param content treść komunikatu
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /** Reprezentuje pojedynczy stan magazynowy w tabeli */
    public static class StockRow {
        private final int id;
        private final String name;
        private final int quantity;
        public StockRow(int id, String name, int quantity) {
            this.id = id; this.name = name; this.quantity = quantity;
        }
        public int getId() { return id; }
        public String getName() { return name; }
        public int getQuantity() { return quantity; }
    }

    /**
     * Zamyka aplikację i powraca do okna logowania.
     */
    public void logout() {
        if (!reportGeneratedInCurrentSession) {
            showAlert(Alert.AlertType.ERROR,
                    "Wylogowanie zablokowane",
                    "Musisz najpierw wygenerować raport dzienny, aby się wylogować.");
            return;
        }

        reportGeneratedInCurrentSession = false;
        logger.info("Wylogowanie z panelu logistyka, reset flagi reportGeneratedInCurrentSession");
        logger.info("Uruchamianie ekranu logowania...");

        primaryStage.close();
        try {
            new HelloApplication().start(new Stage());
        } catch (Exception e) {
            logger.error("Błąd przy starcie ekranu logowania", e);
        }
    }

    /**
     * Wyświetla formularz dodawania nowego produktu.
     * @param stockTable referencja do tabeli stanów magazynowych, którą odświeżamy po zapisie
     */
    private void showAddProductDialog(TableView<StockRow> stockTable) {
        Stage stage = new Stage();
        stage.setTitle("Dodaj nowy produkt");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label nameLabel       = new Label("Nazwa produktu:");
        TextField nameField   = new TextField();
        nameField.setPromptText("np. Kawa");

        Label categoryLabel   = new Label("Kategoria:");
        TextField categoryField = new TextField();
        categoryField.setPromptText("np. Napoje");

        Label priceLabel      = new Label("Cena (PLN):");
        TextField priceField  = new TextField();
        priceField.setPromptText("liczba, np. 12.99");

        Label qtyLabel        = new Label("Początkowy stan:");
        TextField qtyField    = new TextField();
        qtyField.setPromptText("liczba całkowita, np. 50");

        Button saveBtn = new Button("Zapisz produkt");
        styleLogisticButton(saveBtn, "#27AE60");
        saveBtn.setOnAction(ev -> {

            /* ---- podstawowa weryfikacja pustych pól ---- */
            if (nameField.getText().isBlank() ||
                    categoryField.getText().isBlank() ||
                    priceField.getText().isBlank() ||
                    qtyField.getText().isBlank()) {
                showAlert(Alert.AlertType.WARNING, "Brak danych",
                        "Uzupełnij wszystkie pola (nazwa, kategoria, cena, stan).");
                return;
            }

            /* ---- sprawdzenie, czy nazwa i kategoria to TYLKO litery/spacje ---- */
            // [\\p{L}] = dowolna litera unicode, spacja dozwolona
            String lettersOnlyRegex = "[\\p{L} ]+";
            if (!nameField.getText().matches(lettersOnlyRegex) ||
                    !categoryField.getText().matches(lettersOnlyRegex)) {
                showAlert(Alert.AlertType.WARNING, "Nieprawidłowe dane",
                        "Pola 'Nazwa' i 'Kategoria' mogą zawierać wyłącznie litery oraz spacje.");
                return;
            }

            /* ---- cena ---- */
            BigDecimal price;
            try {
                price = new BigDecimal(priceField.getText().trim().replace(',', '.'));
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Nieprawidłowa cena",
                            "Cena musi być dodatnią liczbą.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Nieprawidłowy format ceny",
                        "Podaj poprawną liczbę (np. 12.99).");
                return;
            }

            /* ---- początkowy stan ---- */
            int initQty;
            try {
                initQty = Integer.parseInt(qtyField.getText().trim());
                if (initQty < 0) {
                    showAlert(Alert.AlertType.WARNING, "Nieprawidłowa ilość",
                            "Ilość początkowa nie może być ujemna.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Nieprawidłowy format ilości",
                        "Pole 'Początkowy stan' musi być liczbą całkowitą.");
                return;
            }

            /* ---- zapis w bazie ---- */
            try {
                Product p = new Product();
                p.setName(nameField.getText().trim());
                p.setCategory(categoryField.getText().trim());
                p.setPrice(price);
                productRepository.addProduct(p);

                Warehouse w = new Warehouse();
                w.setProductId(p.getId());
                w.setQuantity(initQty);
                warehouseRepository.addWarehouseState(w);

                showAlert(Alert.AlertType.INFORMATION, "Sukces",
                        "Dodano produkt: " + p.getName() +
                                " (ID=" + p.getId() + "), stan początkowy: " + initQty);

                stage.close();
                if (stockTable != null) refreshStockTable(stockTable);
            } catch (Exception ex) {
                logger.error("Błąd podczas dodawania produktu", ex);
                showAlert(Alert.AlertType.ERROR, "Błąd",
                        "Nie udało się dodać produktu:\n" + ex.getMessage());
            }
        });

        grid.addRow(0, nameLabel,     nameField);
        grid.addRow(1, categoryLabel, categoryField);
        grid.addRow(2, priceLabel,    priceField);
        grid.addRow(3, qtyLabel,      qtyField);
        grid.add(saveBtn, 1, 4);

        stage.setScene(new Scene(grid, 400, 280));
        stage.show();
    }

    /**
     * Wspólny styl dla przycisków w panelu logistyka.
     */
    private void styleLogisticButton(Button button, String color) {
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        " -fx-text-fill: white;" +
                        " -fx-font-weight: bold;"
        );
        button.setOnMouseEntered(e -> {
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });
        button.setOnMouseExited(e -> {
            button.setScaleX(1);
            button.setScaleY(1);
        });
    }
    /**
     * Zamyka aplikację bez sprawdzania flagi raportu.
     * Używane tylko przy forsownym wyjściu.
     */
    private void logoutByForce() {
        logger.info("Forsowne zamknięcie aplikacji bez raportu");
        primaryStage.close();
        try {
            new HelloApplication().start(new Stage());
        } catch (Exception e) {
            logger.error("Błąd przy starcie ekranu logowania (forsowne)", e);
        }
    }

}
