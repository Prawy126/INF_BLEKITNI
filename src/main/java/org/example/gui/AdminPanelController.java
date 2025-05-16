/*
 * Classname: AdminPanelController
 * Version information: 1.1
 * Date: 2025-04-27
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.database.UserRepository;
import org.example.pdflib.ConfigManager;
import org.example.pdflib.ReportGenerator;
import org.example.sys.Employee;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.io.File;
import java.math.BigDecimal;

/**
 * Kontroler odpowiedzialny za obsługę logiki
 * interfejsu administratora w aplikacji GUI.
 */
public class AdminPanelController {

    private final AdminPanel adminPanel;
    private final Stage primaryStage;
    private final UserRepository userRepository;
    private TableView<Employee> tableView;

    /**
     * Konstruktor klasy kontrolera.
     *
     * @param adminPanel główny panel administratora
     */
    public AdminPanelController(AdminPanel adminPanel) {
        this.adminPanel = adminPanel;
        this.primaryStage = adminPanel.getPrimaryStage();
        this.userRepository = new UserRepository();
    }

    /**
     * Wyświetla panel zarządzania użytkownikami.
     */
    public void showUserManagement() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Lista użytkowników");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        tableView = new TableView<>();

        // === Poprawione nazwy pól zgodnie z getterami ===
        TableColumn<Employee, String> nameCol = new TableColumn<>("Imię");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Employee, String> surnameCol = new TableColumn<>("Nazwisko");
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<Employee, Integer> ageCol = new TableColumn<>("Wiek");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<Employee, String> loginCol = new TableColumn<>("Login");
        loginCol.setCellValueFactory(new PropertyValueFactory<>("login"));

        TableColumn<Employee, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Employee, String> stanowiskoCol = new TableColumn<>("Stanowisko");
        stanowiskoCol.setCellValueFactory(new PropertyValueFactory<>("stanowisko"));

        TableColumn<Employee, BigDecimal> zarobkiCol = new TableColumn<>("Zarobki");
        zarobkiCol.setCellValueFactory(new PropertyValueFactory<>("zarobki"));

        tableView.getColumns().addAll(
                nameCol, surnameCol, ageCol,
                loginCol, emailCol, stanowiskoCol, zarobkiCol
        );

        odswiezListePracownikow();

        // === Przyciski ===
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button addUserButton = new Button("Dodaj użytkownika");
        Button editUserButton = new Button("Edytuj użytkownika");
        Button deleteUserButton = new Button("Usuń użytkownika");

        addUserButton.setOnAction(e -> dodajNowegoUzytkownika());
        editUserButton.setOnAction(e -> edytujWybranegoUzytkownika());
        deleteUserButton.setOnAction(e -> usunWybranegoUzytkownika());

        buttonBox.getChildren().addAll(
                addUserButton, editUserButton, deleteUserButton
        );

        layout.getChildren().addAll(titleLabel, tableView, buttonBox);
        adminPanel.setCenterPane(layout);
    }

    /**
     * Formularz edycji wybranego użytkownika.
     */
    private void edytujWybranegoUzytkownika() {
        Employee selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Brak wyboru",
                    "Wybierz użytkownika do edycji."
            );
            return;
        }

        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(20));

        Label titleLabel = new Label("Edytuj użytkownika");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField nameField = new TextField(selected.getName());
        TextField surnameField = new TextField(selected.getSurname());
        TextField loginField = new TextField(selected.getLogin());
        TextField emailField = new TextField(selected.getEmail());

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(
                "Nowe hasło (pozostaw puste, aby nie zmieniać)"
        );

        ComboBox<String> stanowiskoBox = new ComboBox<>();
        stanowiskoBox.getItems().addAll(
                "Kasjer", "Kierownik", "Admin", "Logistyk"
        );
        stanowiskoBox.setValue(selected.getStanowisko());

        TextField ageField = new TextField(
                String.valueOf(selected.getAge())
        );
        TextField salaryField = new TextField(
                String.valueOf(selected.getZarobki())
        );

        Button saveButton = new Button("Zapisz zmiany");
        Button cancelButton = new Button("Anuluj");

        HBox buttons = new HBox(10, saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        formLayout.getChildren().addAll(
                titleLabel, nameField, surnameField,
                loginField, passwordField, emailField,
                stanowiskoBox, ageField, salaryField, buttons
        );

        adminPanel.setCenterPane(formLayout);

        saveButton.setOnAction(e -> {
            try {
                if (nameField.getText().isEmpty()
                        || surnameField.getText().isEmpty()
                        || loginField.getText().isEmpty()
                        || emailField.getText().isEmpty()
                        || stanowiskoBox.getValue() == null
                        || ageField.getText().isEmpty()
                        || salaryField.getText().isEmpty()) {
                    showAlert(
                            Alert.AlertType.WARNING,
                            "Brak danych",
                            "Uzupełnij wszystkie pola (poza hasłem)."
                    );
                    return;
                }

                selected.setName(nameField.getText());
                selected.setSurname(surnameField.getText());
                selected.setLogin(loginField.getText());
                selected.setEmail(emailField.getText());

                if (!passwordField.getText().isEmpty()) {
                    selected.setPassword(passwordField.getText());
                }

                selected.setStanowisko(stanowiskoBox.getValue());
                selected.setAge(
                        Integer.parseInt(ageField.getText())
                );
                selected.setZarobki(
                        new BigDecimal(salaryField.getText())
                );

                userRepository.aktualizujPracownika(selected);

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Sukces",
                        "Dane użytkownika zostały zaktualizowane."
                );
                showUserManagement();

            } catch (NumberFormatException ex) {
                showAlert(
                        Alert.AlertType.ERROR,
                        "Błąd",
                        "Nieprawidłowy format wieku lub zarobków!"
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(
                        Alert.AlertType.ERROR,
                        "Błąd",
                        "Wystąpił błąd podczas zapisywania zmian: "
                                + ex.getMessage()
                );
            }
        });

        cancelButton.setOnAction(e -> showUserManagement());
    }

    /**
     * Pobiera dane z bazy i ładuje do tabeli.
     */
    private void odswiezListePracownikow() {
        tableView.getItems().clear();
        tableView.getItems().addAll(
                userRepository.pobierzWszystkichPracownikow()
        );
    }

    /**
     * Formularz dodawania nowego użytkownika.
     */
    private void dodajNowegoUzytkownika() {
        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(20));

        Label titleLabel = new Label("Dodaj nowego użytkownika");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField nameField = new TextField();
        nameField.setPromptText("Imię");

        TextField surnameField = new TextField();
        surnameField.setPromptText("Nazwisko");

        TextField loginField = new TextField();
        loginField.setPromptText("Login");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Hasło");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        ComboBox<String> stanowiskoBox = new ComboBox<>();
        stanowiskoBox.getItems().addAll(
                "Kasjer", "Kierownik", "Admin", "Logistyk"
        );
        stanowiskoBox.setPromptText("Stanowisko");

        TextField ageField = new TextField();
        ageField.setPromptText("Wiek");

        TextField salaryField = new TextField();
        salaryField.setPromptText("Zarobki (PLN)");

        Button saveButton = new Button("Zapisz");
        Button cancelButton = new Button("Anuluj");

        HBox buttons = new HBox(10, saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        formLayout.getChildren().addAll(
                titleLabel, nameField, surnameField,
                loginField, passwordField, emailField,
                stanowiskoBox, ageField, salaryField, buttons
        );

        adminPanel.setCenterPane(formLayout);

        saveButton.setOnAction(e -> {
            try {
                if (nameField.getText().isEmpty()
                        || surnameField.getText().isEmpty()
                        || loginField.getText().isEmpty()
                        || passwordField.getText().isEmpty()
                        || emailField.getText().isEmpty()
                        || stanowiskoBox.getValue() == null
                        || ageField.getText().isEmpty()
                        || salaryField.getText().isEmpty()) {
                    showAlert(
                            Alert.AlertType.WARNING,
                            "Brak danych",
                            "Uzupełnij wszystkie pola!"
                    );
                    return;
                }

                int wiek = Integer.parseInt(ageField.getText());
                BigDecimal zarobki = new BigDecimal(
                        salaryField.getText()
                );

                Employee nowy = new Employee();
                nowy.setName(nameField.getText());
                nowy.setSurname(surnameField.getText());
                nowy.setLogin(loginField.getText());
                nowy.setPassword(passwordField.getText());
                nowy.setEmail(emailField.getText());
                nowy.setStanowisko(stanowiskoBox.getValue());
                nowy.setAge(wiek);
                nowy.setZarobki(zarobki);

                userRepository.dodajPracownika(nowy);

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Sukces",
                        "Dodano nowego użytkownika!"
                );
                showUserManagement();

            } catch (NumberFormatException ex) {
                showAlert(
                        Alert.AlertType.ERROR,
                        "Błąd",
                        "Nieprawidłowy format wieku lub zarobków!"
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(
                        Alert.AlertType.ERROR,
                        "Błąd",
                        "Nie udało się dodać użytkownika: "
                                + ex.getMessage()
                );
            }
        });

        cancelButton.setOnAction(e -> showUserManagement());
    }

    /**
     * Usuwa zaznaczonego użytkownika.
     */
    private void usunWybranegoUzytkownika() {
        Employee selected = tableView.getSelectionModel()
                .getSelectedItem();
        if (selected != null) {
            try {
                userRepository.usunPracownika(selected);
                odswiezListePracownikow();
                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Sukces",
                        "Usunięto użytkownika!"
                );
            } catch (Exception e) {
                showAlert(
                        Alert.AlertType.ERROR,
                        "Błąd",
                        "Nie udało się usunąć użytkownika: "
                                + e.getMessage()
                );
                e.printStackTrace();
            }
        } else {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Brak wyboru",
                    "Wybierz użytkownika do usunięcia."
            );
        }
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
        logsCheckbox.setSelected(ConfigManager.isLoggingEnabled());

        CheckBox notificationsCheckbox = new CheckBox("Włącz powiadomienia");
        notificationsCheckbox.setSelected(ConfigManager.isNotificationsEnabled());

        Button configurePDF = new Button("Konfiguruj raporty PDF");
        configurePDF.setOnAction(e -> showPDFConfigPanel());

        Button backupButton = new Button("Wykonaj backup bazy danych");
        backupButton.setStyle(
                "-fx-background-color: #27AE60; "
                        + "-fx-text-fill: white;"
        );
        backupButton.setOnAction(e -> performDatabaseBackup());

        Button saveButton = new Button("Zapisz");
        saveButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            // zapis do ConfigManager i pliku app.properties
            ConfigManager.setLoggingEnabled(logsCheckbox.isSelected());
            ConfigManager.setNotificationsEnabled(notificationsCheckbox.isSelected());
            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Zapisano",
                    "Ustawienia zostały zachowane."
            );
        });

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
        sortingComboBox.getItems().addAll(
                "Nazwa", "Data", "Priorytet"
        );

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
            LocalDate to   = endDatePicker.getValue();

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

        layout.getChildren().addAll(
                titleLabel,
                tableView,
                detailsButton
        );

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
    private void showAlert(
            Alert.AlertType type,
            String title,
            String header
    ) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(null);
        alert.showAndWait();
    }
    /**
     * Pokazuje dialog z dodatkowymi filtrami zależnie od raportu.
     */
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

        // domyślne filtry datami
        Map<String, String> filters = new HashMap<>();
        filters.put("from", from.toString());
        filters.put("to",   to.toString());

        int row = 0;
        switch (reportName) {
            case "Raport sprzedaży":
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
                break;

            case "Raport pracowników":
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
                break;

            case "Raport zgłoszeń":
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
                break;

            default:
                dialog.setResultConverter(btn -> btn == genType ? filters : null);
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
