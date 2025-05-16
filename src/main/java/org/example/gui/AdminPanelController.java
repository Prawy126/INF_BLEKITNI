package org.example.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.database.RaportRepository;
import org.example.database.TechnicalIssueRepository;
import org.example.database.UserRepository;
import org.example.pdflib.ConfigManager;
import org.example.pdflib.ReportGenerator;
import org.example.sys.Employee;
import org.example.sys.Raport;
import org.example.sys.TechnicalIssue;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class AdminPanelController {

    private final AdminPanel adminPanel;
    private final UserRepository userRepository;
    private final RaportRepository raportRepository;
    private final TechnicalIssueRepository technicalIssueRepository;

    public AdminPanelController(AdminPanel adminPanel) {
        this.adminPanel = adminPanel;
        this.userRepository = new UserRepository();
        this.raportRepository = new RaportRepository();
        this.technicalIssueRepository = new TechnicalIssueRepository();
    }

    public void showUserManagement() {
        showAlert(Alert.AlertType.INFORMATION, "Info", "Zarządzanie użytkownikami - do zaimplementowania.");
    }

    public void showReportsPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Wybierz rodzaj raportu i zakres dat");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        ComboBox<String> reportType = new ComboBox<>();
        reportType.getItems().addAll(
                "Raport sprzedaży",
                "Raport pracowników",
                "Raport zgłoszeń"
        );
        reportType.setPrefWidth(250);

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Data początkowa");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Data końcowa");

        Button generateButton = new Button("Generuj raport");
        generateButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        generateButton.setOnAction(e -> {
            String selected = reportType.getValue();
            LocalDate from = startDatePicker.getValue();
            LocalDate to = endDatePicker.getValue();

            if (selected == null || from == null || to == null) {
                showAlert(Alert.AlertType.WARNING, "Brak danych", "Wybierz typ raportu oraz zakres dat.");
                return;
            }

            showFilterDialogForReport(selected, from, to);
        });

        layout.getChildren().addAll(
                titleLabel,
                reportType,
                new Label("Data od:"), startDatePicker,
                new Label("Data do:"), endDatePicker,
                generateButton
        );

        adminPanel.setCenterPane(layout);
    }

    public void showReportsTablePanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Lista raportów");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<Raport> table = new TableView<>();

        TableColumn<Raport, String> typeCol = new TableColumn<>("Typ");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("typRaportu"));

        TableColumn<Raport, LocalDate> fromCol = new TableColumn<>("Od");
        fromCol.setCellValueFactory(new PropertyValueFactory<>("dataPoczatku"));

        TableColumn<Raport, LocalDate> toCol = new TableColumn<>("Do");
        toCol.setCellValueFactory(new PropertyValueFactory<>("dataZakonczenia"));

        TableColumn<Raport, String> pathCol = new TableColumn<>("Plik");
        pathCol.setCellValueFactory(new PropertyValueFactory<>("sciezkaPliku"));

        table.getColumns().addAll(typeCol, fromCol, toCol, pathCol);
        table.getItems().addAll(raportRepository.pobierzWszystkieRaporty());

        Button deleteBtn = new Button("Usuń raport");
        deleteBtn.setOnAction(e -> {
            Raport selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Brak wyboru", "Wybierz raport do usunięcia.");
                return;
            }
            raportRepository.usunRaport(selected.getId());
            table.getItems().remove(selected);
            showAlert(Alert.AlertType.INFORMATION, "Usunięto", "Raport usunięty.");
        });

        layout.getChildren().addAll(titleLabel, table, deleteBtn);
        adminPanel.setCenterPane(layout);
    }

    public void showIssuesPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label title = new Label("Zgłoszenia techniczne");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<TechnicalIssue> table = new TableView<>();

        TableColumn<TechnicalIssue, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<TechnicalIssue, String> typeCol = new TableColumn<>("Typ");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<TechnicalIssue, LocalDate> dateCol = new TableColumn<>("Data");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateSubmitted"));

        TableColumn<TechnicalIssue, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, typeCol, dateCol, statusCol);
        table.getItems().addAll(technicalIssueRepository.pobierzWszystkieZgloszenia());

        layout.getChildren().addAll(title, table);
        adminPanel.setCenterPane(layout);
    }

    public void showConfigPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label label = new Label("Konfiguracja systemu");
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        CheckBox logsCheckbox = new CheckBox("Włącz logi systemowe");
        logsCheckbox.setSelected(ConfigManager.isLoggingEnabled());

        CheckBox notificationsCheckbox = new CheckBox("Włącz powiadomienia");
        notificationsCheckbox.setSelected(ConfigManager.isNotificationsEnabled());

        Button configurePDF = new Button("Konfiguruj raporty PDF");
        configurePDF.setOnAction(e -> showPDFConfigPanel());

        Button saveButton = new Button("Zapisz");
        saveButton.setOnAction(e -> {
            ConfigManager.setLoggingEnabled(logsCheckbox.isSelected());
            ConfigManager.setNotificationsEnabled(notificationsCheckbox.isSelected());
            showAlert(Alert.AlertType.INFORMATION, "Zapisano", "Ustawienia zostały zachowane.");
        });

        layout.getChildren().addAll(label, logsCheckbox, notificationsCheckbox, configurePDF, saveButton);
        adminPanel.setCenterPane(layout);
    }

    public void showPDFConfigPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Konfiguracja raportów PDF");
        TextField logoField = new TextField();
        logoField.setPromptText("Ścieżka do loga");

        Button saveButton = new Button("Zapisz");
        saveButton.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "Zapisano", "Konfiguracja zapisana."));

        layout.getChildren().addAll(titleLabel, logoField, saveButton);
        adminPanel.setCenterPane(layout);
    }

    public void logout() {
        userRepository.close();
        raportRepository.close();
        technicalIssueRepository.close();
        adminPanel.getPrimaryStage().close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void showFilterDialogForReport(String reportName, LocalDate from, LocalDate to) {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Filtruj: " + reportName);
        dialog.setHeaderText("Podaj dodatkowe parametry");

        ButtonType genType = new ButtonType("Generuj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(genType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Map<String, String> filters = new HashMap<>();
        filters.put("from", from.toString());
        filters.put("to", to.toString());

        int row = 0;
        switch (reportName) {
            case "Raport sprzedaży" -> {
                TextField category = new TextField();
                category.setPromptText("Kategoria produktu");
                grid.add(new Label("Kategoria:"), 0, row);
                grid.add(category, 1, row++);
                dialog.setResultConverter(btn -> {
                    if (btn == genType) {
                        filters.put("category", category.getText());
                        return filters;
                    }
                    return null;
                });
            }
            case "Raport pracowników" -> {
                TextField role = new TextField();
                role.setPromptText("Stanowisko");
                grid.add(new Label("Stanowisko:"), 0, row);
                grid.add(role, 1, row++);
                dialog.setResultConverter(btn -> {
                    if (btn == genType) {
                        filters.put("role", role.getText());
                        return filters;
                    }
                    return null;
                });
            }
            case "Raport zgłoszeń" -> {
                ComboBox<String> statusBox = new ComboBox<>();
                statusBox.getItems().addAll("Nowe", "W trakcie", "Rozwiązane");
                statusBox.setPromptText("Status zgłoszenia");
                grid.add(new Label("Status:"), 0, row);
                grid.add(statusBox, 1, row++);
                dialog.setResultConverter(btn -> {
                    if (btn == genType) {
                        filters.put("status", statusBox.getValue());
                        return filters;
                    }
                    return null;
                });
            }
            default -> dialog.setResultConverter(btn -> btn == genType ? filters : null);
        }

        dialog.getDialogPane().setContent(grid);

        Optional<Map<String, String>> result = dialog.showAndWait();
        result.ifPresent(flt -> {
            try {
                File output = ReportGenerator.generate(reportName, flt);
                showAlert(Alert.AlertType.INFORMATION,
                        "Gotowe",
                        "Raport wygenerowany:\n" + output.getAbsolutePath());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR,
                        "Błąd",
                        "Nie udało się wygenerować raportu:\n" + ex.getMessage());
            }
        });
    }
}
