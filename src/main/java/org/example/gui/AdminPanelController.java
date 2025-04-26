/*
 * Classname: AdminPanelController
 * Version information: 1.0
 * Date: 2025-04-06
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.sys.Employee;
import org.example.wyjatki.PasswordException;

import java.math.BigDecimal;

/**
 * Kontroler odpowiedzialny za obsługę logiki interfejsu
 * administratora w aplikacji GUI.
 */
public class AdminPanelController {

    private final AdminPanel adminPanel;
    private final Stage primaryStage;

    /**
     * Konstruktor klasy kontrolera.
     *
     * @param adminPanel Główny panel administratora
     */
    public AdminPanelController(AdminPanel adminPanel) {
        this.adminPanel = adminPanel;
        this.primaryStage = adminPanel.getPrimaryStage();
    }

    /**
     * Wyświetla panel zarządzania użytkownikami.
     */
    public void showUserManagement() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Lista użytkowników");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<Employee> tableView = new TableView<>();

// Kolumny tabeli
        TableColumn<Employee, String> nameCol = new TableColumn<>("Imię");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("imie"));

        TableColumn<Employee, String> surnameCol = new TableColumn<>("Nazwisko");
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));

        TableColumn<Employee, Integer> ageCol = new TableColumn<>("Wiek");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("wiek"));

        TableColumn<Employee, String> loginCol = new TableColumn<>("Login");
        loginCol.setCellValueFactory(new PropertyValueFactory<>("login"));

        TableColumn<Employee, String> stanowiskoCol = new TableColumn<>("Stanowisko");
        stanowiskoCol.setCellValueFactory(new PropertyValueFactory<>("stanowisko"));

        TableColumn<Employee, BigDecimal> zarobkiCol = new TableColumn<>("Zarobki");
        zarobkiCol.setCellValueFactory(new PropertyValueFactory<>("zarobki"));


        // Dodanie kolumn do tabeli
        tableView.getColumns().addAll(
                nameCol,
                surnameCol,
                ageCol,
                loginCol,
                stanowiskoCol,
                zarobkiCol
        );

        // Przykładowe dane testowe
        try {
            Employee emp1 = new Employee();
            emp1.setImie("Jan");
            emp1.setNazwisko("Nowak");
            emp1.setWiek(25);
            emp1.setLogin("kasjer123");
            emp1.setHaslo("haslo123");
            emp1.setStanowisko("kasjer");
            emp1.setZarobki(new BigDecimal("5000"));

            Employee emp2 = new Employee();
            emp2.setImie("Anna");
            emp2.setNazwisko("Kowalska");
            emp2.setWiek(30);
            emp2.setLogin("admin123");
            emp2.setHaslo("haslo123");
            emp2.setStanowisko("admin");
            emp2.setZarobki(new BigDecimal("6000"));

            tableView.getItems().addAll(emp1, emp2);
        } catch (Exception e) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd",
                    "Nie można dodać przykładowych danych do tabeli."
            );
            e.printStackTrace();
        }

        // Przycisk dodawania/usuwania
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button addUserButton = new Button("Dodaj użytkownika");
        Button deleteUserButton = new Button("Usuń użytkownika");

        buttonBox.getChildren().addAll(addUserButton, deleteUserButton);
        layout.getChildren().addAll(titleLabel, tableView, buttonBox);

        adminPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla panel ustawień konfiguracyjnych systemu.
     */
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
        backupButton.setStyle(
                "-fx-background-color: #27AE60; "
                        + "-fx-text-fill: white;"
        );
        backupButton.setOnAction(e -> performDatabaseBackup());

        Button saveButton = new Button("Zapisz");
        saveButton.setStyle(
                "-fx-background-color: #3498DB; "
                        + "-fx-text-fill: white;"
        );

        layout.getChildren().addAll(
                titleLabel,
                logsCheckbox,
                notificationsCheckbox,
                configurePDF,
                backupButton,
                saveButton
        );

        adminPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla panel konfiguracji plików PDF.
     */
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

        layout.getChildren().addAll(
                titleLabel,
                logoLabel,
                logoField,
                updateLogoButton,
                sortingLabel,
                sortingComboBox,
                backButton
        );

        adminPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla panel generowania raportów.
     */
    public void showReportsPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Wybierz rodzaj raportu");

        ComboBox<String> reportType = new ComboBox<>();
        reportType.getItems().addAll(
                "Raport sprzedaży",
                "Raport pracowników",
                "Raport zgłoszeń"
        );
        reportType.setPrefWidth(200);

        Label dateLabel = new Label("Wybierz zakres dat");
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Data początkowa");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Data końcowa");

        Button generateButton = new Button("Generuj raport");
        generateButton.setStyle(
                "-fx-background-color: #3498DB;"
                        + " -fx-text-fill: white;"
        );

        layout.getChildren().addAll(
                titleLabel,
                reportType,
                dateLabel,
                startDatePicker,
                endDatePicker,
                generateButton
        );

        adminPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla panel zgłoszeń.
     */
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

    /**
     * Wylogowuje użytkownika i uruchamia okno logowania.
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

    /**
     * Symuluje wykonanie backupu bazy danych.
     */
    private void performDatabaseBackup() {
        showAlert(
                Alert.AlertType.INFORMATION,
                "Backup",
                "Backup bazy danych został wykonany pomyślnie!"
        );
        System.out.println("Backup bazy danych wykonany!");
    }

    /**
     * Wyświetla komunikat w okienku dialogowym.
     *
     * @param type   typ alertu
     * @param title  tytuł okna
     * @param header treść nagłówka
     */
    private void showAlert(Alert.AlertType type, String title, String header) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(null);
        alert.showAndWait();
    }
}
