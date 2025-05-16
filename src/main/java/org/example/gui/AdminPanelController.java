package org.example.gui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.example.database.RaportRepository;
import org.example.database.TechnicalIssueRepository;
import org.example.database.UserRepository;
import org.example.sys.Employee;
import org.example.sys.Raport;
import org.example.sys.TechnicalIssue;

import java.time.LocalDate;
import java.util.Optional;

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
        // Można zaimplementować podobnie jak showReportsTablePanel
        showAlert(Alert.AlertType.INFORMATION, "Info", "Zarządzanie użytkownikami - do zaimplementowania.");
    }

    public void showReportsPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Wybierz rodzaj raportu");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        ComboBox<String> reportType = new ComboBox<>();
        reportType.getItems().addAll("Raport sprzedaży", "Raport pracowników", "Raport zgłoszeń");
        reportType.setPromptText("Wybierz typ");

        DatePicker startDate = new DatePicker();
        startDate.setPromptText("Data początkowa");

        DatePicker endDate = new DatePicker();
        endDate.setPromptText("Data końcowa");

        Button generateBtn = new Button("Generuj raport");
        generateBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        generateBtn.setOnAction(e -> {
            String type = reportType.getValue();
            LocalDate from = startDate.getValue();
            LocalDate to = endDate.getValue();

            if (type == null || from == null || to == null) {
                showAlert(Alert.AlertType.WARNING, "Brak danych", "Uzupełnij wszystkie pola.");
                return;
            }

            Optional<Employee> optUser = userRepository.pobierzWszystkichPracownikow().stream().findFirst();
            if (optUser.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Brak pracownika", "Brak pracownika do przypisania.");
                return;
            }

            TextInputDialog dialog = new TextInputDialog("raport_" + from + "_" + to);
            dialog.setTitle("Nazwa pliku");
            dialog.setHeaderText("Podaj nazwę pliku PDF:");
            dialog.setContentText("Nazwa:");

            dialog.showAndWait().ifPresent(name -> {
                String path = "raporty/" + name + ".pdf";
                Raport raport = new Raport(type, from, to, optUser.get(), path);
                raportRepository.dodajRaport(raport);
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Raport wygenerowany i zapisany.");
            });
        });

        layout.getChildren().addAll(titleLabel, reportType,
                new Label("Zakres dat:"), startDate, endDate, generateBtn);

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

        layout.getChildren().addAll(label,
                new Label("Funkcja w trakcie implementacji..."));

        adminPanel.setCenterPane(layout);
    }

    public void logout() {
        userRepository.close();
        raportRepository.close();
        technicalIssueRepository.close();
        adminPanel.getPrimaryStage().close();
        // Tu można dodać restart aplikacji lub powrót do logowania
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
