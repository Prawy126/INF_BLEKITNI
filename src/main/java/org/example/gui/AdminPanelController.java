package org.example.gui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.sys.Employee;

public class AdminPanelController {

    private final AdminPanel adminPanel;
    private final Stage primaryStage;

    public AdminPanelController(AdminPanel adminPanel) {
        this.adminPanel = adminPanel;
        this.primaryStage = adminPanel.getPrimaryStage();
    }

    public void showUserManagement() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Lista użytkowników");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<Employee> tableView = new TableView<>();

        TableColumn<Employee, String> nameCol = new TableColumn<>("Imię");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Employee, Integer> ageCol = new TableColumn<>("Wiek");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<Employee, String> cityCol = new TableColumn<>("Adres");
        cityCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Employee, String> emCol = new TableColumn<>("Login");
        emCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Employee, String> empCol = new TableColumn<>("Rodzaj konta");
        empCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        TableColumn<Employee, String> depCol = new TableColumn<>("Oddział");
        depCol.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<Employee, String> posCol = new TableColumn<>("Stanowisko");
        posCol.setCellValueFactory(new PropertyValueFactory<>("position"));

        TableColumn<Employee, Integer> salCol = new TableColumn<>("Wynagrodzenie");
        salCol.setCellValueFactory(new PropertyValueFactory<>("salary")); // poprawione

        tableView.getColumns().addAll(nameCol, ageCol, cityCol, emCol, empCol, depCol, posCol, salCol);
        tableView.getItems().addAll(
                new Employee("Jan", "Nowak",25, "Kraków", "kasjer", "kasjer", "kasjer", "IT", "Developer", 5000),
                new Employee("Łukasz", "Banan",30, "Warszawa", "admin", "admin", "admin", "IT", "Developer", 5000),
                new Employee("Krzysztof", "Kępa",35, "Gdańsk", "manager", "manager", "manager", "IT", "Developer", 5000)
        );

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button addUserButton = new Button("Dodaj użytkownika");
        Button deleteUserButton = new Button("Usuń użytkownika");

        buttonBox.getChildren().addAll(addUserButton, deleteUserButton);
        layout.getChildren().addAll(titleLabel, tableView, buttonBox);

        adminPanel.setCenterPane(layout);
    }

    public void showConfigPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Opcje konfiguracyjne");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        CheckBox logsCheckbox = new CheckBox("Włącz logi systemowe");
        logsCheckbox.setSelected(true);

        CheckBox notificationsCheckbox = new CheckBox("Włącz powiadomienia");
        notificationsCheckbox.setSelected(true);

        Button configurePDF = new Button("Konfiguruj raporty PDF");
        configurePDF.setOnAction(e -> showPDFConfigPanel());

        Button backupButton = new Button("Wykonaj backup bazy danych");
        backupButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white;");
        backupButton.setOnAction(e -> performDatabaseBackup());

        Button saveButton = new Button("Zapisz");
        saveButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

        layout.getChildren().addAll(titleLabel, logsCheckbox, notificationsCheckbox, configurePDF, backupButton, saveButton);
        adminPanel.setCenterPane(layout);
    }

    public void showPDFConfigPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Aktualizacja loga hipermarketu");
        Label logoLabel = new Label("Logo:");
        TextField logoField = new TextField();

        Button updateLogoButton = new Button("Aktualizuj logo");

        Label sortingLabel = new Label("Sortowanie domyślne:");
        ComboBox<String> sortingComboBox = new ComboBox<>();
        sortingComboBox.getItems().addAll("Nazwa", "Data", "Priorytet");

        Button backButton = new Button("Wróć");
        backButton.setOnAction(e -> showConfigPanel());

        layout.getChildren().addAll(titleLabel, logoLabel, logoField, updateLogoButton, sortingLabel, sortingComboBox, backButton);
        adminPanel.setCenterPane(layout);
    }

    public void showReportsPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Wybierz rodzaj raportu");
        ComboBox<String> reportType = new ComboBox<>();
        reportType.getItems().addAll("Raport sprzedaży", "Raport pracowników", "Raport zgłoszeń");
        reportType.setPrefWidth(200);

        Label dateLabel = new Label("Wybierz zakres dat");

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Data początkowa");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Data końcowa");

        Button generateButton = new Button("Generuj raport");
        generateButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

        layout.getChildren().addAll(titleLabel, reportType, dateLabel, startDatePicker, endDatePicker, generateButton);
        adminPanel.setCenterPane(layout);
    }

    public void showIssuesPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Lista zgłoszeń");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<String> tableView = new TableView<>();
        tableView.setMinHeight(200);

        Button detailsButton = new Button("Szczegóły zgłoszenia");

        layout.getChildren().addAll(titleLabel, tableView, detailsButton);
        adminPanel.setCenterPane(layout);
    }

    public void logout() {
        primaryStage.close();

        Stage loginStage = new Stage();
        try {
            new HelloApplication().start(loginStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performDatabaseBackup() {
        showAlert(Alert.AlertType.INFORMATION, "Backup", "Backup bazy danych został wykonany pomyślnie!");
        System.out.println("Backup bazy danych wykonany!");
    }

    private void showAlert(Alert.AlertType type, String title, String header) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(null);
        alert.showAndWait();
    }
}
