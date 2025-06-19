/*
 * Classname: LogisticianPanelController
 * Version information: 1.8
 * Date: 2025-06-08
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui.controllers;

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

import org.example.database.repositories.*;
import org.example.gui.HelloApplication;
import org.example.gui.panels.LogisticianPanel;
import org.example.pdflib.ConfigManager;
import org.example.sys.*;

import pdf.WarehouseRaport;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    private static final Logger logger =
            LogManager.getLogger(LogisticianPanelController.class);
    private final ProductRepository productRepository
            = new ProductRepository();
    private final WarehouseRepository warehouseRepository =
            new WarehouseRepository();
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
        logger.info("LogisticianPanelController utworzony " +
                        "i przypisany do panelu wartość {}",
                logisticianPanel);
        this.primaryStage.setOnCloseRequest(event -> {
            if (!reportGeneratedInCurrentSession) {
                event.consume();  // Zatrzymaj zamknięcie
                showAlert(Alert.AlertType.ERROR,
                        "Zamknięcie zablokowane",
                        "Musisz wygenerować raport, aby zamknąć " +
                                "aplikację.");
            }
        });
        logger.debug("Ustawiono obsługę zdarzenia zamknięcia okna");
    }

    public void showInventoryManagement() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label title = new Label("Zarządzanie magazynem");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

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

        Button editProductBtn = new Button("Edytuj produkt");
        styleLogisticButton(editProductBtn, "#E67E22");
        editProductBtn.setOnAction(
                e -> showEditProductDialog(table));

        HBox btnBox = new HBox(10, filterBtn, refreshBtn, reportsBtn,
                addProductBtn, editProductBtn);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        layout.getChildren().addAll(title, table, btnBox);
        logisticianPanel.setCenterPane(layout);
    }


    /**
     * Ładuje i wyświetla w tabeli aktualne
     * stany magazynowe (id, nazwa, ilość).
     *
     * @param table tabela do wypełnienia danymi
     */
    private void refreshStockTable(TableView<StockRow> table) {
        try {
            Map<Integer,Integer> qtyById
                    = warehouseRepository.getAllStates()
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
            showAlert(ERROR,"Błąd","Nie udało się pobrać " +
                    "stanów magazynowych");
        }
    }

    /**
     * Otwiera panel „Zamówienia” z tabelą wszystkich zamówień.
     * Ustawia kolumny, ładuje dane z repozytorium
     * oraz tworzy przyciski Dodaj, Filtruj i Odśwież.
     */
    public void showOrdersPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Zamówienia");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<Order> tableView = new TableView<>();
        tableView.setMinHeight(200);

        TableColumn<Order, Integer> idCol = new TableColumn<>("Id");
        TableColumn<Order, String> productCol = new TableColumn<>(
                "Produkt");
        TableColumn<Order, String> employeeCol = new TableColumn<>(
                "Pracownik");
        TableColumn<Order, Integer> qtyCol = new TableColumn<>("Ilość");
        TableColumn<Order, BigDecimal> priceCol = new TableColumn<>("Cena");
        TableColumn<Order, Date> dateCol = new TableColumn<>(
                "Data złożenia");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                cellData.getValue().getProduct() !=
                        null ? cellData.getValue().getProduct()
                        .getName() : ""));
        employeeCol.setCellValueFactory(cellData -> {
            Employee emp = cellData.getValue().getEmployee();
            String fullName = emp != null
                    ? emp.getName() + " " + emp.getSurname()
                    : "";
            return new SimpleStringProperty(fullName);
        });
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        tableView.getColumns().addAll(idCol, productCol, employeeCol,
                qtyCol, priceCol, dateCol);

        OrderRepository orderRepo = new OrderRepository();
        List<Order> orders = orderRepo.getAllOrders();
        tableView.setItems(FXCollections.observableArrayList(orders));

        Button addOrderButton = new Button("Dodaj zamówienie");
        styleLogisticButton(addOrderButton, "#27AE60");
        addOrderButton.setOnAction(e -> showAddOrderForm());

        Button filterButton = new Button("Filtruj");
        styleLogisticButton(filterButton, "#2980B9");
        filterButton.setOnAction(
                e -> showFilterOrderDialog(tableView));

        Button refreshBtn = new Button("Odśwież");
        styleLogisticButton(refreshBtn, "#3498DB");
        refreshBtn.setOnAction(e ->
                tableView.setItems(FXCollections.observableArrayList(
                        new OrderRepository().getAllOrders()))
        );

        HBox btnBox = new HBox(10, addOrderButton, filterButton, refreshBtn);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        layout.getChildren().addAll(titleLabel, tableView, btnBox);
        logisticianPanel.setCenterPane(layout);
    }

    /**
     * Otwiera prosty dialog filtrowania zamówień
     * po nazwie produktu, pracownika, cenie, ilości
     * i dacie złożenia zamówienia
     * Po zatwierdzeniu zastępuje zawartość tabeli
     * wyfiltrowanymi rekordami.
     *
     * @param tableView tabela zamówień,
     *                  która ma zostać przefiltrowana
     */
    private void showFilterOrderDialog(TableView<Order> tableView) {
        Stage stage = new Stage();
        stage.setTitle("Filtrowanie zamówień");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label productLabel  = new Label("Produkt:");
        ComboBox<String> productBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        new ProductRepository().getAllProducts().stream()
                                .map(Product::getName)
                                .collect(Collectors.toList())
                )
        );
        productBox.setPromptText("Wybierz produkt");

        Label employeeLabel = new Label("Pracownik:");
        ComboBox<String> employeeBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        new UserRepository().getAllEmployees().stream()
                                .map(emp -> emp.getName() + " "
                                        + emp.getSurname())
                                .collect(Collectors.toList())
                )
        );
        employeeBox.setPromptText("Wybierz pracownika");

        Label minQtyLabel   = new Label("Min. ilość:");
        TextField minQtyField = new TextField();

        Label minPriceLabel = new Label("Min. cena:");
        TextField minPriceField = new TextField();

        Label minDateLabel  = new Label("Min. data złożenia:");
        DatePicker minDatePicker = new DatePicker();

        Button filterButton = new Button("Filtruj");
        styleLogisticButton(filterButton, "#27AE60");
        filterButton.setOnAction(ev -> {
            ObservableList<Order> all = FXCollections.observableArrayList(
                    new OrderRepository().getAllOrders()
            );
            ObservableList<Order> filtered = all.filtered(o -> {
                // produkt
                String prodSel = productBox.getValue();
                if (prodSel != null && !prodSel.isEmpty()) {
                    if (o.getProduct() == null
                            || !prodSel.equals(o.getProduct().getName())) {
                        return false;
                    }
                }
                // pracownik (imię + nazwisko)
                String empSel = employeeBox.getValue();
                if (empSel != null && !empSel.isEmpty()) {
                    String full = o.getEmployee().getName()
                            + " " + o.getEmployee().getSurname();
                    if (!empSel.equals(full)) {
                        return false;
                    }
                }
                // min ilość
                if (!minQtyField.getText().isBlank()) {
                    try {
                        int minQ = Integer.parseInt(
                                minQtyField.getText().trim());
                        if (o.getQuantity() < minQ) return false;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                }
                // min cena
                if (!minPriceField.getText().isBlank()) {
                    try {
                        BigDecimal minP = new BigDecimal(
                                minPriceField.getText().trim());
                        if (o.getPrice().compareTo(minP) < 0) return false;
                    } catch (Exception ex) {
                        return false;
                    }
                }
                // min data
                if (minDatePicker.getValue() != null) {
                    LocalDate orderDate = ((java.sql.Date)o.getDate())
                            .toLocalDate();
                    if (orderDate.isBefore(minDatePicker.getValue()))
                        return false;
                }
                return true;
            });

            tableView.setItems(filtered);
            stage.close();
        });

        grid.addRow(0, productLabel,  productBox);
        grid.addRow(1, employeeLabel, employeeBox);
        grid.addRow(2, minQtyLabel,   minQtyField);
        grid.addRow(3, minPriceLabel, minPriceField);
        grid.addRow(4, minDateLabel,  minDatePicker);
        grid.add(filterButton, 1, 5);

        Scene scene = new Scene(grid, 500, 350);
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(350);
        stage.show();
    }

    /**
     * Otwiera panel „Raporty magazynowe” z
     * przyciskiem uruchamiającym kreator raportu.
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
     * Okienko konfiguracji: wybór kategorii
     * i progu niskiego stanu magazynowego.
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
        categoriesList.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE);

        Label thresholdLabel
                = new Label("Próg niskiego stanu " + "magazynowego:");
        Spinner<Integer> thresholdSpinner
                = new Spinner<>(1, 100, 5);

        Label pathInfoLabel = new Label("Plik zostanie zapisany " +
                "w katalogu:");
        TextField pathDisplay = new TextField();
        pathDisplay.setEditable(false);
        pathDisplay.setFocusTraversable(false);
        pathDisplay.setPrefWidth(400);
        pathDisplay.setText(ConfigManager.getReportPath());

        Button generate = new Button("Generuj");
        styleLogisticButton(generate, "#27AE60");
        generate.setOnAction(e ->
                handleGenerateButton(
                        new ArrayList<>(categoriesList.getSelectionModel()
                                .getSelectedItems()),
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
     * Pokazuje systemowy FileChooser do wybrania katalogu
     * i pliku .pdf.
     *
     * @param stage okno-rodzic dla FileChooser
     * @param outputPath pole tekstowe,
     *                   w które wpisany zostanie wybrany path
     */
    private void handleBrowseButton(Stage stage, TextField outputPath) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files",
                        "*.pdf"));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            outputPath.setText(file.getAbsolutePath());
        }
    }

    /**
     * Generuje raport magazynowy na podstawie
     * wybranych kategorii i progu niskiego stanu.
     * Zapisuje go w katalogu zdefiniowanym w ConfigManager.
     *
     * @param selectedCategories lista wybranych kategorii
     * @param lowStockThreshold próg niskiego stanu
     * @param stage dialog-rodzic, który zostanie zamknięty
     *              po wygenerowaniu
     */
    private void handleGenerateButton(List<String> selectedCategories,
                                      int lowStockThreshold,
                                      Stage stage) {

        String basePath = ConfigManager.getReportPath();
        if (basePath == null || basePath.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Brak ścieżki",
                    "Ustaw domyślną ścieżkę zapisu raportów " +
                            "w ustawieniach administratora.");
            return;
        }

        String timestamp =
                LocalDateTime.now().format(DateTimeFormatter
                        .ofPattern("yyyyMMdd_HHmmss"));
        File targetFile = new File(basePath, "warehouse-report-"
                + timestamp + ".pdf");

        try {
            List<org.example.sys.Product> products
                    = productRepository.getAllProducts();
            Map<Integer, Integer> qtyById
                    = warehouseRepository.getAllStates()
                    .stream()
                    .collect(Collectors.toMap(
                            org.example.sys.Warehouse::getProductId,
                            org.example.sys.Warehouse::getQuantity
                    ));

            List<org.example.sys.Product> filteredProducts = products.stream()
                    .filter(p ->
                            selectedCategories.isEmpty() ||
                                    selectedCategories.contains(
                                            p.getCategory()))
                    .toList();

            WarehouseRaport.
                    ProductDataExtractor<org.example.sys.Product> extractor =
                    new WarehouseRaport.ProductDataExtractor<>() {
                        public String getName(org.example.sys.Product p)     {
                            return p.getName(); }
                        public String getCategory(org.example.sys.Product p) {
                            return p.getCategory(); }
                        public double getPrice(org.example.sys.Product p)    {
                            return p.getPrice().doubleValue(); }
                        public int getQuantity(org.example.sys.Product p)    {
                            return qtyById.getOrDefault(p.getId(),
                                    0); }
                    };

            String logoPath = ConfigManager.getLogoPath();
            if (logoPath == null || logoPath.isBlank()) {
                showAlert(Alert.AlertType.ERROR,
                        "Brak logo",
                        "W konfiguracji nie ustawiono ścieżki " +
                                "do logo. Ustaw logo w" +
                                " panelu administratora.");
                return;
            }

            File logoFile = new File(logoPath);
            if (!logoFile.exists() || !logoFile.isFile()) {
                showAlert(Alert.AlertType.ERROR,
                        "Niepoprawny plik logo",
                        "Nie odnaleziono pliku logo pod ścieżką: "
                                + logoPath);
                return;
            }

            WarehouseRaport raport = new WarehouseRaport();
            raport.setLogoPath(logoPath);
            raport.setLowStockThreshold(lowStockThreshold);
            raport.generateReport(
                    targetFile.getAbsolutePath(),
                    filteredProducts,
                    extractor,
                    selectedCategories
            );

            showAlert(Alert.AlertType.INFORMATION, "Sukces",
                    "Raport zapisany: " + targetFile.getAbsolutePath());
            stage.close();
            reportGeneratedInCurrentSession = true;
            logger.info("Raport magazynowy wygenerowany: {}",
                    targetFile.getAbsolutePath());
            logger.info("Flag reportGeneratedInCurrentSession ustawiona " +
                    "na true");

        } catch (Exception ex) {
            logger.error("Błąd generowania raportu", ex);
            showAlert(ERROR, "Błąd",
                    "Generowanie raportu nie powiodło się: "
                            + ex.getMessage());
        }
    }

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

                ButtonType cancelBtn = new ButtonType("Anuluj",
                        ButtonBar.ButtonData.CANCEL_CLOSE);
                ButtonType forceExit = new ButtonType("Zamknij mimo " +
                        "wszystko",
                        ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll(cancelBtn, forceExit);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == forceExit) {
                    logger.warn("Forsowne wylogowanie logistyka bez raportu " +
                            "dziennego");
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
     * Pole "Produkt" zastąpiono ComboBoxem wypełnionym
     * nazwami produktów.
     * Pole "Ilość" oraz "Data" pozostały bez zmian.
     * ID pracownika pobierane jest automatycznie
     * z zalogowanego użytkownika, a cena jest liczona
     * jako (cena jednostkowa produktu * ilość).
     */
    private void showAddOrderForm() {
        Stage stage = new Stage();
        stage.setTitle("Dodaj zamówienie");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label productLabel = new Label("Produkt:");
        ComboBox<Product> productComboBox = new ComboBox<>();
        productComboBox.setPrefWidth(200);
        productComboBox.setPromptText("Wybierz produkt");

        List<Product> allProducts = productRepository.getAllProducts();

        productComboBox.getItems().addAll(allProducts);
        productComboBox.setCellFactory(
                cb -> new ListCell<>() {
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

        // 2) Pole ilości
        Label qtyLabel = new Label("Ilość:");
        TextField quantityField = new TextField();
        quantityField.setPromptText("liczba całkowita");

        Button submit = new Button("Zapisz");
        styleLogisticButton(submit, "#27AE60");
        submit.setOnAction(ev -> {

            if (productComboBox.getValue() == null
                    || quantityField.getText().isBlank()) {
                showAlert(Alert.AlertType.WARNING,
                        "Brak danych",
                        "Uzupełnij wszystkie pola (produkt, ilość)");
                return;
            }

            try {
                int qty = Integer.parseInt(quantityField.getText().trim());
                if (qty <= 0) {
                    showAlert(Alert.AlertType.WARNING,
                            "Nieprawidłowa ilość",
                            "Ilość musi być dodatnią liczbą całkowitą");
                    return;
                }

                UserRepository ur = new UserRepository();
                Employee empl = ur.getCurrentEmployee();
                if (empl == null) {
                    showAlert(Alert.AlertType.ERROR,
                            "Błąd",
                            "Brak zalogowanego pracownika. " +
                                    "Zaloguj się ponownie.");
                    return;
                }

                Product prod = productComboBox.getValue();

                BigDecimal unitPrice = prod.getPrice();
                BigDecimal totalPrice = unitPrice.multiply(
                        BigDecimal.valueOf(qty));

                Order ord = new Order();
                ord.setProduct(prod);
                ord.setEmployee(empl);
                ord.setQuantity(qty);
                ord.setPrice(totalPrice);
                ord.setDate(java.sql.Date.valueOf(
                        java.time.LocalDate.now()));

                OrderRepository or = new OrderRepository();
                or.addOrder(ord);

                try {
                    Warehouse stan = warehouseRepository
                            .findStateByProductId(prod.getId());

                    if (stan == null) {
                        stan = new Warehouse();
                        stan.setProductId(prod.getId());
                        stan.setQuantity(Math.max(0, qty));
                        warehouseRepository.addWarehouseState(stan);
                    } else {
                        int newQty = stan.getQuantity() + qty;
                        if (newQty < 0) newQty = 0;
                        stan.setQuantity(newQty);
                        warehouseRepository.updateState(stan);
                    }
                } catch (Exception ex) {
                    logger.error("Błąd aktualizacji stanu " +
                            "magazynowego", ex);
                }

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
                        "Nie udało się dodać zamówienia:\n"
                                + ex.getMessage());
            }
        });

        grid.add(productLabel,    0, 0);
        grid.add(productComboBox, 1, 0);
        grid.add(qtyLabel,        0, 1);
        grid.add(quantityField,   1, 1);
        grid.add(submit,          1, 2);

        stage.setScene(new Scene(grid, 400, 250));
        stage.show();
    }

    /**
     * Otwiera okienko filtrowania stanów magazynowych.
     * Po zatwierdzeniu pobiera pełny zestaw danych, filtruje
     * po nazwie produktu i minimalnej ilości
     * i aktualizuje tabelę.
     *
     * @param table tabela stanów magazynowych,
     *              która ma zostać przefiltrowana
     */
    private void showFilterStockDialog(TableView<StockRow> table) {
        Stage st = new Stage();
        st.setTitle("Filtrowanie stanów");
        GridPane g = new GridPane();
        g.setPadding(new Insets(20)); g.setHgap(10); g.setVgap(10);

        // ComboBox z nazwami produktów
        Label nameLabel = new Label("Nazwa produktu:");
        ComboBox<String> nameCombo = new ComboBox<>(
                FXCollections.observableArrayList(
                        productRepository.getAllProducts()
                                .stream()
                                .map(org.example.sys.Product::getName)
                                .distinct()
                                .sorted()
                                .collect(Collectors.toList())
                )
        );
        nameCombo.setPromptText("Wybierz produkt");

        // Pole minimalnej ilości
        Label minQtyLabel = new Label("Min. ilość:");
        TextField qtyF = new TextField();

        Button filt = new Button("Filtruj");
        styleLogisticButton(filt, "#2980B9");
        filt.setOnAction(ev -> {
            // Załaduj świeże dane
            refreshStockTable(table);
            ObservableList<StockRow> base = table.getItems();
            ObservableList<StockRow> out = base.filtered(r -> {
                // nazwa produktu
                String selName = nameCombo.getValue();
                if (selName != null && !selName.isEmpty()) {
                    if (!r.getName().equals(selName)) return false;
                }
                // minimalna ilość
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

        // layout
        g.addRow(0, nameLabel, nameCombo);
        g.addRow(1, minQtyLabel, qtyF);
        g.add(filt, 1, 2);

        st.setScene(new Scene(g, 400, 250));
        st.show();
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
                    showAlert(ERROR,"Błąd","Brak zalogowanego " +
                            "pracownika");
                    return;
                }

                AbsenceRequest ar = new AbsenceRequest();
                ar.setRequestType("Inny");
                ar.setDescription(reasonField.getText());
                ar.setStartDate(java.sql.Date.valueOf(
                        fromDatePicker.getValue()));
                ar.setEndDate  (java.sql.Date.valueOf(
                        toDatePicker.getValue()));
                ar.setEmployee(emp);

                new AbsenceRequestRepository().addRequest(ar);
                showAlert(Alert.AlertType.INFORMATION, "Sukces",
                        "Wniosek zapisany");
                stage.close();

            } catch (Exception ex) {
                logger.error("Błąd zapisu wniosku", ex);
                showAlert(ERROR, "Błąd", "Nie udało się " +
                        "zapisać wniosku");
            }
        });

        grid.add(reasonLabel, 0, 0);   grid.add(reasonField,
                1, 0);
        grid.add(fromDateLabel, 0, 1); grid.add(fromDatePicker,
                1, 1);
        grid.add(toDateLabel, 0, 2);   grid.add(toDatePicker,
                1, 2);
        grid.add(submitButton, 1, 3);

        stage.setScene(new Scene(grid, 400, 300));
        stage.show();
    }

    /**
     * Wyświetla prosty alert z podanym typem, tytułem i
     * treścią.
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
        alert.getDialogPane().setMinWidth(400);
        alert.getDialogPane().setMinHeight(200);
        alert.showAndWait();
    }

    /**
     * Zamyka aplikację i powraca do okna logowania.
     */
    public void logout() {
        if (!reportGeneratedInCurrentSession) {
            showAlert(Alert.AlertType.ERROR,
                    "Wylogowanie zablokowane",
                    "Musisz najpierw wygenerować raport dzienny, " +
                            "aby się wylogować.");
            return;
        }

        reportGeneratedInCurrentSession = false;
        logger.info("Wylogowanie z panelu logistyka," +
                " reset flagi reportGeneratedInCurrentSession");
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
     * @param stockTable referencja do tabeli stanów
     *                   magazynowych, którą odświeżamy
     *                   po zapisie
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

            if (nameField.getText().isBlank() ||
                    categoryField.getText().isBlank() ||
                    priceField.getText().isBlank() ||
                    qtyField.getText().isBlank()) {
                showAlert(Alert.AlertType.WARNING, "Brak danych",
                        "Uzupełnij wszystkie pola (nazwa, kategoria, " +
                                "cena, stan).");
                return;
            }

            String lettersOnlyRegex = "[\\p{L} ]+";
            if (!nameField.getText().matches(lettersOnlyRegex) ||
                    !categoryField.getText().matches(lettersOnlyRegex)) {
                showAlert(Alert.AlertType.WARNING, "Nieprawidłowe dane",
                        "Pola 'Nazwa' i 'Kategoria' mogą zawierać " +
                                "wyłącznie litery oraz spacje.");
                return;
            }

            BigDecimal price;
            try {
                price = new BigDecimal(priceField.getText().trim().replace(
                        ',', '.'));
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Nieprawidłowa " +
                                    "cena",
                            "Cena musi być dodatnią liczbą.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Nieprawidłowy format " +
                                "ceny",
                        "Podaj poprawną liczbę (np. 12.99).");
                return;
            }

            int initQty;
            try {
                initQty = Integer.parseInt(qtyField.getText().trim());
                if (initQty < 0) {
                    showAlert(Alert.AlertType.WARNING,
                            "Nieprawidłowa ilość",
                            "Ilość początkowa nie może być ujemna.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Nieprawidłowy format " +
                                "ilości",
                        "Pole 'Początkowy stan' musi być liczbą " +
                                "całkowitą.");
                return;
            }

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
                                " (ID=" + p.getId() + "), stan początkowy: "
                                + initQty);

                stage.close();
                if (stockTable != null) refreshStockTable(stockTable);
            } catch (Exception ex) {
                logger.error("Błąd podczas dodawania produktu", ex);
                showAlert(Alert.AlertType.ERROR, "Błąd",
                        "Nie udało się dodać produktu:\n"
                                + ex.getMessage());
            }
        });

        grid.addRow(0, nameLabel,     nameField);
        grid.addRow(1, categoryLabel, categoryField);
        grid.addRow(2, priceLabel,    priceField);
        grid.addRow(3, qtyLabel,      qtyField);
        grid.add(saveBtn, 1, 4);

        stage.setScene(new Scene(grid, 450, 320));
        stage.show();
    }

    /**
     * Pokazuje formularz edycji wybranego produktu
     * wraz z jego stanem magazynowym.
     * - Pola: Nazwa, Kategoria, Cena, Ilość w magazynie.
     * - Wypełnia je wartościami z bazy.
     * - Po zatwierdzeniu waliduje i aktualizuje Product
     * oraz Warehouse.
     *
     * @param table tabela stanów magazynowych, z
     *              której czytamy zaznaczony wiersz
     */
    private void showEditProductDialog(TableView<StockRow> table) {
        StockRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Brak wyboru",
                    "Wybierz wiersz, który chcesz edytować.");
            return;
        }

        // 1) Pobierz właściwy obiekt Product po ID
        ProductRepository pr = new ProductRepository();
        Product prod = pr.getAllProducts().stream()
                .filter(p -> p.getId() == selected.getId())
                .findFirst()
                .orElse(null);

        if (prod == null) {
            showAlert(Alert.AlertType.ERROR, "Błąd",
                    "Nie znaleziono produktu o ID="
                            + selected.getId());
            return;
        }

        // 2) Pobierz stan magazynowy dla tego produktu
        Warehouse stanMag = warehouseRepository.findStateByProductId(
                prod.getId());
        int currentQty = (stanMag != null ? stanMag.getQuantity() : 0);

        // 3) Zbuduj nowe okienko
        Stage stage = new Stage();
        stage.setTitle("Edytuj produkt (ID=" + prod.getId() + ")");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label nameLabel     = new Label("Nazwa produktu:");
        TextField nameField = new TextField(prod.getName());
        nameField.setPromptText("np. Kawa");

        Label categoryLabel     = new Label("Kategoria:");
        TextField categoryField = new TextField(prod.getCategory());
        categoryField.setPromptText("np. Napoje");

        Label priceLabel     = new Label("Cena (PLN):");
        TextField priceField = new TextField(prod.getPrice().toString());
        priceField.setPromptText("np. 12.99");

        Label qtyLabel     = new Label("Ilość w magazynie:");
        TextField qtyField = new TextField(String.valueOf(currentQty));
        qtyField.setPromptText("liczba całkowita");

        Button saveBtn = new Button("Zapisz zmiany");
        styleLogisticButton(saveBtn, "#27AE60");
        saveBtn.setOnAction(ev -> {
            // WALIDACJA PÓL:
            String newName = nameField.getText().trim();
            String newCat  = categoryField.getText().trim();
            String newPriceText = priceField.getText().trim().replace(
                    ',', '.');
            String newQtyText   = qtyField.getText().trim();

            if (newName.isBlank() || newCat.isBlank()
                    || newPriceText.isBlank() || newQtyText.isBlank()) {
                showAlert(Alert.AlertType.WARNING, "Brak danych",
                        "Uzupełnij wszystkie pola (nazwa, kategoria, " +
                                "cena, ilość).");
                return;
            }

            String lettersOnlyRegex = "[\\p{L} ]+";
            if (!newName.matches(lettersOnlyRegex)
                    || !newCat.matches(lettersOnlyRegex)) {
                showAlert(Alert.AlertType.WARNING, "Nieprawidłowe dane",
                        "Pola 'Nazwa' i 'Kategoria' mogą zawierać " +
                                "wyłącznie litery oraz spacje.");
                return;
            }

            BigDecimal newPrice;
            try {
                newPrice = new BigDecimal(newPriceText);
                if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Nieprawidłowa " +
                                    "cena",
                            "Cena musi być dodatnią liczbą.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Błąd formatu",
                        "Podaj poprawną liczbę (np. 12.99).");
                return;
            }

            int newQty;
            try {
                newQty = Integer.parseInt(newQtyText);
                if (newQty < 0) {
                    showAlert(Alert.AlertType.WARNING, "Nieprawidłowa " +
                                    "ilość",
                            "Ilość nie może być ujemna.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Błąd formatu",
                        "Ilość musi być liczbą całkowitą.");
                return;
            }

            try {
                prod.setName(newName);
                prod.setCategory(newCat);
                prod.setPrice(newPrice);

                pr.updateProduct(prod);

                warehouseRepository.setProductQuantity(prod.getId(), newQty);

                refreshStockTable(table);

                showAlert(Alert.AlertType.INFORMATION, "Sukces",
                        "Zapisano zmiany dla produktu \"" + newName
                                + "\".");
                stage.close();
            } catch (Exception ex) {
                logger.error("Błąd przy aktualizacji produktu", ex);
                showAlert(Alert.AlertType.ERROR, "Błąd",
                        "Nie udało się zapisać zmian:\n"
                                + ex.getMessage());
            }
        });

        // Układ w siatce:
        grid.add(nameLabel,     0, 0);
        grid.add(nameField,     1, 0);
        grid.add(categoryLabel, 0, 1);
        grid.add(categoryField, 1, 1);
        grid.add(priceLabel,    0, 2);
        grid.add(priceField,    1, 2);
        grid.add(qtyLabel,      0, 3);
        grid.add(qtyField,      1, 3);
        grid.add(saveBtn,       1, 4);

        stage.setScene(new Scene(grid, 450, 350));
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
            logger.error("Błąd przy starcie ekranu logowania " +
                    "(forsowne)", e);
        }
    }

}
