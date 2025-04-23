package org.example.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Callback;

/**
 * Kontroler logiki interfejsu użytkownika dla panelu kasjera.
 * Odpowiada za obsługę przełączania widoków.
 */
public class CashierPanelController {

    private final CashierPanel cashierPanel;

    /**
     * Tworzy kontroler dla panelu kasjera.
     *
     * @param cashierPanel główny panel kasjera
     */
    public CashierPanelController(CashierPanel cashierPanel) {
        this.cashierPanel = cashierPanel;
    }

    /**
     * Wyświetla ekran sprzedaży z przyciskami do dodawania produktów
     * i finalizacji transakcji.
     */
    public void showSalesScreen() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Button addToCartButton = new Button("Dodaj do koszyka");
        Button finalizeSaleButton = new Button("Finalizuj sprzedaż");

        layout.getChildren().addAll(addToCartButton, finalizeSaleButton);
        cashierPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla panel raportów sprzedaży z opcją eksportu do PDF lub CSV.
     */
    public void showSalesReportsPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Raporty sprzedażowe");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

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

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        Button pdfButton = new Button("Generuj raport do PDF");
        Button csvButton = new Button("Generuj raport do CSV");

        pdfButton.setOnAction(e -> showReportGenerationDialog("PDF"));
        csvButton.setOnAction(e -> showReportGenerationDialog("CSV"));

        buttons.getChildren().addAll(pdfButton, csvButton);

        layout.getChildren().addAll(titleLabel, tableView, buttons);
        cashierPanel.setCenterPane(layout);
    }

    private ObservableList<SalesReport> getSampleReports() {
        return FXCollections.observableArrayList(
                new SalesReport("1", "2024-04-01", "Dzienny"),
                new SalesReport("2", "2024-04-02", "Tygodniowy"),
                new SalesReport("3", "2024-04-03", "Miesięczny")
        );
    }

    private Callback<TableColumn<SalesReport, Void>, TableCell<SalesReport, Void>> getViewButtonCellFactory() {
        return col -> new TableCell<>() {
            private final Button viewButton = new Button("Podgląd");

            {
                viewButton.setOnAction(e -> {
                    SalesReport report = getTableView().getItems().get(getIndex());
                    showAlert("Podgląd raportu",
                            "ID: " + report.getId() + "\nData: " + report.getDate() + "\nTyp: " + report.getType());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        };
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Nowa funkcjonalność: Dialog do wyboru typu raportu i daty
    private void showReportGenerationDialog(String reportType) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Wybór raportu");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        // Wybór typu raportu
        Label typeLabel = new Label("Wybierz typ raportu:");
        ComboBox<String> reportTypeCombo = new ComboBox<>();
        reportTypeCombo.getItems().addAll("Dobowy", "Miesięczny");
        reportTypeCombo.setValue("Dobowy");

        // Wybór daty
        Label dateLabel = new Label("Wybierz datę:");
        DatePicker datePicker = new DatePicker();

        // Przycisk generowania raportu
        Button generateButton = new Button("Generuj raport");
        generateButton.setOnAction(e -> {
            String selectedType = reportTypeCombo.getValue();
            String selectedDate = datePicker.getValue().toString();
            showAlert("Generowanie raportu",
                    "Typ raportu: " + selectedType + "\nData: " + selectedDate + "\nRodzaj: " + reportType);
            dialog.close();
        });

        layout.getChildren().addAll(typeLabel, reportTypeCombo, dateLabel, datePicker, generateButton);

        Scene scene = new Scene(layout, 300, 250);
        dialog.setScene(scene);
        dialog.show();
    }

    /**
     * Wyświetla ekran umożliwiający zamknięcie zmiany.
     */
    public void showCloseShiftPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Button closeShiftButton = new Button("Zamknij zmianę");

        layout.getChildren().addAll(closeShiftButton);
        cashierPanel.setCenterPane(layout);
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
     * Wyświetla formularz do zgłoszenia problemu lub awarii.
     */
    public void showIssueReportPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Button reportIssueButton = new Button("Zgłoś problem");

        layout.getChildren().addAll(reportIssueButton);
        cashierPanel.setCenterPane(layout);
    }

    /**
     * Wylogowuje użytkownika i przenosi go do panelu logowania.
     */
    public void logout() {
        // Pobranie głównej sceny aplikacji
        Stage primaryStage = cashierPanel.getPrimaryStage();

        // Zamknięcie bieżącego okna
        primaryStage.close();

        // Wyświetlenie panelu logowania
        HelloApplication.showLoginScreen(primaryStage);
    }

    // Wewnętrzna klasa pomocnicza do przechowywania danych raportu
    public static class SalesReport {
        private final String id;
        private final String date;
        private final String type;

        public SalesReport(String id, String date, String type) {
            this.id = id;
            this.date = date;
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public String getDate() {
            return date;
        }

        public String getType() {
            return type;
        }
    }
}