/*
 * Classname: LogisticianPanelController
 * Version information: 1.3
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


package org.example.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.Insets;

import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.database.*;
import org.example.sys.*;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.example.pdflib.ConfigManager;
import pdf.WarehouseRaport;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    /**
     * Konstruktor przypisujący panel logistyka.
     *
     * @param logisticianPanel obiekt klasy LogisticianPanel
     */
    public LogisticianPanelController(LogisticianPanel logisticianPanel) {
        this.logisticianPanel = logisticianPanel;
        this.primaryStage = logisticianPanel.getPrimaryStage();
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

        table.getColumns().addAll(idCol,nameCol,qtyCol);
        refreshStockTable(table);                           // <-- ładowanie

        /* -------- przyciski -------- */
        Button filterBtn  = new Button("Filtruj");
        filterBtn.setOnAction(e -> showFilterStockDialog(table));

        Button refreshBtn = new Button("Odśwież");
        refreshBtn.setOnAction(e -> refreshStockTable(table));

        Button reportsBtn = new Button("Raporty");
        reportsBtn.setOnAction(e -> showInventoryReports());

        layout.getChildren().addAll(title, table, filterBtn, refreshBtn, reportsBtn);
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
                    .filter(p -> qtyById.containsKey(p.getId()))        // tylko produkty mające stan
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

        // Ładowanie danych z bazy
        OrderRepository orderRepo = new OrderRepository();
        List<Order> orders = orderRepo.getAllOrders();
        tableView.setItems(FXCollections.observableArrayList(orders));

        Button addOrderButton = new Button("Dodaj zamówienie");
        addOrderButton.setOnAction(e -> showAddOrderForm());

        Button filterButton = new Button("Filtruj");
        filterButton.setOnAction(e -> showFilterOrderDialog(tableView));

        Button refreshBtn = new Button("Odśwież");
        refreshBtn.setOnAction(e ->
                tableView.setItems(
                        FXCollections.observableArrayList(
                                new OrderRepository().getAllOrders()
                        )
                )
        );

        layout.getChildren().addAll(titleLabel, tableView, addOrderButton, filterButton, refreshBtn);
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
        filterButton.setOnAction(ev -> {
            // ← TUTAJ ZASTĄP OBECNĄ LAMBDA PONIŻSZYM KODEM

            // 1) pobierz pełną listę z repo
            ObservableList<Order> base =
                    FXCollections.observableArrayList(
                            new OrderRepository().getAllOrders()
                    );

            // 2) filtruj
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
                        if (o.getProduct().getId() !=
                                Integer.parseInt(productIdField.getText().trim()))
                            return false;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                }
                return true;
            });

            // 3) pokaż wyfiltrowane i zamknij okno
            tableView.setItems(out);
            stage.close();
        });

        // ustawienie w GridPane
        grid.add(idLabel, 0, 0);               grid.add(idField, 1, 0);
        grid.add(productIdLabel, 0, 1);        grid.add(productIdField, 1, 1);
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

        // Informacja o docelowej ścieżce (tylko do odczytu)
        Label pathInfoLabel = new Label("Plik zostanie zapisany w katalogu:");
        TextField pathDisplay = new TextField();
        pathDisplay.setEditable(false);
        pathDisplay.setFocusTraversable(false);
        pathDisplay.setPrefWidth(400);
        pathDisplay.setText(ConfigManager.getReportPath());

        Button generate = new Button("Generuj");
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

        // Generujemy unikalną nazwę pliku
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File targetFile = new File(basePath, "warehouse-report-" + timestamp + ".pdf");

        try {
            // Pobierz produkty z repozytorium
            List<org.example.sys.Product> products = productRepository.getAllProducts();

            // Pobierz stany magazynowe
            Map<Integer, Integer> qtyById = warehouseRepository
                    .getAllStates()
                    .stream()
                    .collect(Collectors.toMap(
                            org.example.sys.Warehouse::getProductId,
                            org.example.sys.Warehouse::getQuantity
                    ));

            // Filtruj produkty według wybranych kategorii
            List<org.example.sys.Product> filteredProducts = products.stream()
                    .filter(p ->
                            selectedCategories.isEmpty() ||
                                    selectedCategories.contains(p.getCategory()))
                    .collect(Collectors.toList());

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

        } catch (Exception ex) {
            logger.error("Błąd generowania raportu", ex);
            showAlert(ERROR, "Błąd",
                    "Generowanie raportu nie powiodło się: " + ex.getMessage());
        }
    }

    /**
     * Otwiera formularz dodawania nowego zamówienia: wprowadzasz ID produktu,
     * ID pracownika, ilość, cenę i datę. Po zapisie automatycznie odświeża panel.
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
        submit.setOnAction(ev -> {
            if (productId.getText().isBlank()  || employeeId.getText().isBlank()
                    || quantity.getText().isBlank() || price.getText().isBlank()
                    || datePicker.getValue()  == null) {
                showAlert(Alert.AlertType.WARNING, "Brak danych",
                        "Uzupełnij wszystkie pola");
                return;
            }

            try {
                OrderRepository or = new OrderRepository();
                ProductRepository pr = new ProductRepository();
                UserRepository    ur = new UserRepository();

                /* ── budujemy encję Order ───────────────────────────── */
                Order ord = new Order();

                Product prod = pr.findProductById(Integer.parseInt(productId.getText().trim()));
                if (prod == null) {
                    showAlert(ERROR,"Błąd","Brak produktu o ID="+productId.getText());
                    return;
                }
                Employee empl = ur.findById(Integer.parseInt(employeeId.getText().trim()));
                if (empl == null) {
                    showAlert(ERROR,"Błąd","Brak prac. o ID="+employeeId.getText());
                    return;
                }

                ord.setProduct(prod);
                ord.setEmployee(empl);
                ord.setQuantity(Integer.parseInt(quantity.getText().trim()));
                ord.setPrice   (new BigDecimal(price.getText().replace(",",".")));
                ord.setDate    (java.sql.Date.valueOf(datePicker.getValue()));

                or.addOrder(ord);

                showAlert(Alert.AlertType.INFORMATION, "Sukces",
                        "Zapisano zamówienie (id = " + ord.getId() + ")");
                stage.close();
                showOrdersPanel();                                   // odśwież całą zakładkę

            } catch (Exception ex) {
                logger.error("Błąd dodawania zamówienia", ex);
                showAlert(ERROR, "Błąd",
                        "Nie udało się dodać zamówienia");
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
        filt.setOnAction(ev -> {
            // 1) odśwież pełne dane
            refreshStockTable(table);

            // 2) weź je jako bazę do filtrowania
            ObservableList<StockRow> base = table.getItems();

            // 3) filtruj
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
                        !r.getName().toLowerCase().contains(nameF.getText().toLowerCase()))
                {
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

            // 4) ustaw wyfiltrowane i zamknij dialog
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
        submitButton.setOnAction(ev -> {
            if (reasonField.getText().isBlank()
                    || fromDatePicker.getValue() == null
                    || toDatePicker.getValue()   == null) {
                showAlert(Alert.AlertType.WARNING, "Brak danych",
                        "Uzupełnij wszystkie pola");
                return;
            }

            try {
                /* ── pobierz aktualnie zalogowanego ──────────────────── */
                UserRepository ur = new UserRepository();
                Employee emp = ur.getCurrentEmployee();      //  ◄─ to istnieje w repo
                if (emp == null) {
                    showAlert(ERROR,"Błąd","Brak zalogowanego pracownika");
                    return;
                }

                /* ── budujemy wniosek ────────────────────────────────── */
                AbsenceRequest ar = new AbsenceRequest();
                ar.setRequestType("Inny");                           // typ ­– dowolny
                ar.setDescription(reasonField.getText());
                ar.setStartDate(java.sql.Date.valueOf(fromDatePicker.getValue()));
                ar.setEndDate  (java.sql.Date.valueOf(toDatePicker.getValue()));
                ar.setEmployee(emp);

                new AbsenceRequestRepository().addRequest(ar);      // zapis
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

        Scene scene = new Scene(grid, 350, 250);
        stage.setScene(scene);
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
        primaryStage.close();
        Stage loginStage = new Stage();
        try {
            new HelloApplication().start(loginStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
