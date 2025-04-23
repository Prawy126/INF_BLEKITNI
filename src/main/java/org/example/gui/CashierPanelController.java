package org.example.gui;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Callback;
import javafx.util.Duration;

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

        Button addToCartButton = cashierPanel.createStyledButton("Dodaj do koszyka");
        Button finalizeSaleButton = cashierPanel.createStyledButton("Finalizuj sprzedaż");

        addToCartButton.setOnAction(e -> showProductSelectionDialog());
        finalizeSaleButton.setOnAction(e -> finalizeSale());

        layout.getChildren().addAll(addToCartButton, finalizeSaleButton);
        cashierPanel.setCenterPane(layout);
    }

    private void showProductSelectionDialog() {
        Stage dialog = createStyledDialog("Wybór produktów");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        TextField searchField = createStyledTextField("Szukaj produktów...");
        TableView<Product> table = createProductTable();

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

    private TableView<Product> createProductTable() {
        TableView<Product> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Product, String> nameCol = new TableColumn<>("Nazwa");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Cena");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        table.getColumns().addAll(nameCol, priceCol);
        table.setItems(getSampleProducts());
        return table;
    }

    // Panel raportów
    public void showSalesReportsPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        TableView<SalesReport> tableView = createReportTable();
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
    public void showIssueReportPanel() {
        Stage dialog = createStyledDialog("Zgłoszenie problemu");

        ComboBox<String> typeBox = createStyledComboBox("Awaria sprzętu", "Błąd oprogramowania", "Inne");
        TextArea description = createStyledTextArea("Opisz problem...");

        Button sendButton = cashierPanel.createStyledButton("Wyślij", "#27AE60");
        sendButton.setOnAction(e -> {
            if (validateReport(typeBox.getValue(), description.getText())) {
                showNotification("Sukces", "Zgłoszenie wysłane");
                dialog.close();
            }
        });

        VBox root = new VBox(20);
        root.getChildren().addAll(typeBox, description, sendButton);
        setupDialog(dialog, root);
    }

    private ObservableList<SalesReport> getSampleReports() {
        return FXCollections.observableArrayList(
                new SalesReport("1", "2024-04-01", "Dzienny"),
                new SalesReport("2", "2024-04-02", "Tygodniowy"),
                new SalesReport("3", "2024-04-03", "Miesięczny")
        );
    }

    // 2. Cell factory for the "Podgląd" button column
    private Callback<TableColumn<SalesReport, Void>, TableCell<SalesReport, Void>> getViewButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<SalesReport, Void> call(TableColumn<SalesReport, Void> param) {
                return new TableCell<>() {
                    private final Button viewButton = new Button("Podgląd");

                    {
                        viewButton.setOnAction(event -> {
                            SalesReport report = getTableView().getItems().get(getIndex());
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

    // 3. Method to show report details (helper method)
    private void showReportDetails(SalesReport report) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Szczegóły raportu");
        alert.setHeaderText("Raport ID: " + report.getId());
        alert.setContentText(
                "Data utworzenia: " + report.getDate() + "\n" +
                        "Typ raportu: " + report.getType()
        );
        alert.showAndWait();
    }

    // Pomocnicze metody
    private Stage createStyledDialog(String title) {
        Stage dialog = new Stage(StageStyle.UNDECORATED);
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

    private ObservableList<Product> getSampleProducts() {
        return FXCollections.observableArrayList(
                new Product("Mleko", 3.50),
                new Product("Chleb", 4.20),
                new Product("Jajka", 8.99),
                new Product("Masło", 6.50),
                new Product("Ser", 12.99)
        );
    }

    private TableView<SalesReport> createReportTable() {
        TableView<SalesReport> tableView = new TableView<>();
        tableView.setMinHeight(300);

        TableColumn<SalesReport, String> idColumn = new TableColumn<>("Id_raportu");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(100);

        TableColumn<SalesReport, String> dateColumn = new TableColumn<>("Data utworzenia");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setPrefWidth(150);

        TableColumn<SalesReport, String> typeColumn = new TableColumn<>("Typ raportu");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setPrefWidth(150);

        TableColumn<SalesReport, Void> viewColumn = new TableColumn<>("Podgląd");
        viewColumn.setCellFactory(getViewButtonCellFactory());
        viewColumn.setPrefWidth(100);

        tableView.getColumns().addAll(idColumn, dateColumn, typeColumn, viewColumn);
        tableView.setItems(getSampleReports());

        return tableView;
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

    // Pozostałe metody z oryginalnej implementacji...

    // Wewnętrzne klasy danych
    public static class Product {
        private final String name;
        private final double price;

        public Product(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() { return name; }
        public double getPrice() { return price; }
    }

    public static class SalesReport {
        private final String id;
        private final String date;
        private final String type;

        public SalesReport(String id, String date, String type) {
            this.id = id;
            this.date = date;
            this.type = type;
        }

        public String getId() { return id; }
        public String getDate() { return date; }
        public String getType() { return type; }
    }
}