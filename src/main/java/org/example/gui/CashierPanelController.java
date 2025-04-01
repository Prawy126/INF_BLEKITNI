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

public class CashierPanelController {

    private final CashierPanel cashierPanel;

    public CashierPanelController(CashierPanel cashierPanel) {
        this.cashierPanel = cashierPanel;
    }

    public void showSalesScreen() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Button addToCartButton = new Button("Dodaj do koszyka");
        Button finalizeSaleButton = new Button("Finalizuj sprzedaż");

        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(addToCartButton, finalizeSaleButton);

        cashierPanel.setCenterPane(layout);
    }

    public void showSalesReportsPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

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

    public void showCloseShiftPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Button closeShiftButton = new Button("Zamknij zmianę");
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(closeShiftButton);

        cashierPanel.setCenterPane(layout);
    }

    public void showIssueReportPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Button reportIssueButton = new Button("Zgłoś problem");
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(reportIssueButton);

        cashierPanel.setCenterPane(layout);
    }

    public void logout() {
        cashierPanel.getPrimaryStage().close();
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
