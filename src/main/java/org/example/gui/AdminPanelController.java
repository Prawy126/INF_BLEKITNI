/*
 * Classname: AdminPanelController
 * Version information: 1.12
 * Date: 2025-06-07
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;

import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.*;
import org.example.pdflib.ConfigManager;
import org.example.sys.Employee;
import org.example.sys.TechnicalIssue;
import org.example.sys.PeriodType;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import org.example.sys.Address;
import org.example.sys.EmpTask;
import pdf.StatsRaportGenerator;
import pdf.TaskRaportGenerator;
import pdf.WorkloadReportGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Kontroler odpowiedzialny za obsługę logiki
 * interfejsu administratora w aplikacji GUI.
 */
public class AdminPanelController {

    private final AdminPanel adminPanel;
    private final Stage primaryStage;
    private final UserRepository userRepository;
    private TableView<Employee> tableView;
    private final TechnicalIssueRepository technicalIssueRepository;
    private TableView<TechnicalIssue> issuesTableView;

    // Executor do operacji asynchronicznych
    private static final ExecutorService executor =
            Executors.newVirtualThreadPerTaskExecutor();
    private static final Logger logger =
            LogManager.getLogger(AdminPanelController.class);

    // Cache widoków dla lepszej wydajności
    private VBox userManagementView;
    private VBox configPanelView;
    private VBox reportsPanelView;
    private VBox issuesPanelView;

    /**
     * Konstruktor klasy kontrolera.
     *
     * @param adminPanel główny panel administratora
     */
    public AdminPanelController(AdminPanel adminPanel) {
        this.adminPanel = adminPanel;
        this.primaryStage = adminPanel.getPrimaryStage();
        this.userRepository = new UserRepository();
        this.technicalIssueRepository = new TechnicalIssueRepository();

        String configuredPath = ConfigManager.getReportPath();
        if (configuredPath == null || configuredPath.trim().isEmpty()) {
            logger.info("Brak skonfigurowanej ścieżki do raportów – ustawiamy domyślną.");

            // pobierz bieżący katalog roboczy (tam, gdzie uruchomiono aplikację)
            String projectDir = System.getProperty("user.dir");
            logger.debug("Aktualny katalog roboczy: {}", projectDir);

            String defaultReportsDir = projectDir + File.separator + "reports";
            logger.debug("Wybrano domyślny katalog raportów: {}", defaultReportsDir);

            File reportsFolder = new File(defaultReportsDir);
            if (!reportsFolder.exists()) {
                logger.info("Katalog '{}' nie istnieje – próbuję utworzyć.", defaultReportsDir);
                boolean created = reportsFolder.mkdirs();
                if (created) {
                    logger.info("Pomyślnie utworzono katalog: {}", defaultReportsDir);
                } else {
                    logger.error("Nie udało się utworzyć katalogu: {}", defaultReportsDir);
                }
            } else {
                logger.info("Domyślny katalog raportów już istnieje: {}", defaultReportsDir);
            }

            ConfigManager.setReportPath(defaultReportsDir);
            logger.info("Ustawiono ścieżkę do raportów w ConfigManager: {}",
                    defaultReportsDir);
        }
    }

    /**
     * Wyświetla panel zarządzania użytkownikami.
     */
    public void showUserManagement() {
        if (userManagementView == null) {
            // Pokaż wskaźnik ładowania
            showLoadingIndicator();

            // Utwórz widok asynchronicznie
            Task<VBox> task = new Task<>() {
                @Override
                protected VBox call() throws Exception {
                    return createUserManagementView();
                }
            };

            task.setOnSucceeded(e -> {
                userManagementView = task.getValue();
                adminPanel.setCenterPane(userManagementView);
                // Załaduj dane pracowników asynchronicznie
                refreshEmployeeList();
            });

            task.setOnFailed(e -> {
                task.getException().printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Błąd",
                        "Nie udało się załadować panelu użytkowników");
            });

            executor.execute(task);
        } else {
            // Jeśli widok już istnieje, po prostu go pokaż
            adminPanel.setCenterPane(userManagementView);
            // Odśwież dane
            refreshEmployeeList();
        }
    }

    /**
     * Wyświetla wskaźnik ładowania podczas operacji asynchronicznych.
     */
    private void showLoadingIndicator() {
        VBox loadingBox = new VBox(10);
        loadingBox.setAlignment(Pos.CENTER);

        ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(50, 50);

        Label loadingLabel = new Label("Ładowanie...");
        loadingLabel.setStyle("-fx-font-size: 14px;");

        loadingBox.getChildren().addAll(progress, loadingLabel);
        adminPanel.setCenterPane(loadingBox);
    }

    /**
     * Tworzy widok zarządzania użytkownikami.
     *
     * @return VBox zawierający kompletny widok
     */
    private VBox createUserManagementView() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Lista użytkowników");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        configureTableView();

        // Kolumny tabeli
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

        TableColumn<Employee, String> positionCol = new TableColumn<>("Stanowisko");
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));

        TableColumn<Employee, BigDecimal> salaryCol = new TableColumn<>("Zarobki");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salary"));

        tableView.getColumns().addAll(
                nameCol, surnameCol, ageCol,
                loginCol, emailCol, positionCol, salaryCol
        );

        refreshEmployeeList();

        // === Przyciski ===
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button addUserButton = new Button("Dodaj użytkownika");
        Button editUserButton = new Button("Edytuj użytkownika");
        Button deleteUserButton = new Button("Usuń użytkownika");

        styleAdminButton(addUserButton,"#2980B9");
        styleAdminButton(editUserButton,"#3498DB");
        styleAdminButton(deleteUserButton,"#E74C3C");

        addUserButton.setOnAction(e -> addNewUser());
        editUserButton.setOnAction(e -> editSelectedUser());
        deleteUserButton.setOnAction(e -> removeSelectedUser());

        buttonBox.getChildren().addAll(
                addUserButton, editUserButton, deleteUserButton
        );

        layout.getChildren().addAll(titleLabel, tableView, buttonBox);
        return layout;
    }

    /**
     * Konfiguruje TableView dla lepszej wydajności.
     */
    private void configureTableView() {
        // Ustawienie wirtualizacji dla lepszej wydajności
        tableView.setFixedCellSize(25); // Stała wysokość komórek
        tableView.setCache(true);
        tableView.setCacheHint(CacheHint.SPEED);

        // Ograniczenie liczby wierszy ładowanych jednocześnie
        int rowsToShow = 20;
        // +30 na nagłówek
        tableView.setPrefHeight(rowsToShow * tableView.getFixedCellSize() + 30);
    }

    /**
     * Formularz edycji wybranego użytkownika.
     */
    private void editSelectedUser() {
        Employee selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING,
                    "Brak wyboru",
                    "Wybierz użytkownika do edycji.");
            return;
        }

        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(20));

        Label titleLabel = new Label("Edytuj użytkownika");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField nameField = new TextField(selected.getName());
        nameField.setPromptText("Imię");

        TextField surnameField = new TextField(selected.getSurname());
        surnameField.setPromptText("Nazwisko");

        TextField loginField = new TextField(selected.getLogin());
        loginField.setPromptText("Login");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Nowe hasło (pozostaw puste, aby nie zmieniać)");

        TextField emailField = new TextField(selected.getEmail());
        emailField.setPromptText("Email");

        // Adres
        Label addressLabel = new Label("Adres:");
        AddressRepository addressRepository = new AddressRepository();
        ComboBox<Address> addressComboBox = new ComboBox<>();
        addressComboBox.getItems().addAll(addressRepository.getAllAddresses());
        addressComboBox.setValue(selected.getAddress()); // ustawiamy istniejący
        addressComboBox.setPromptText("Wybierz adres");

        Button addNewAddressBtn = new Button("Dodaj nowy adres");
        addNewAddressBtn.setOnAction(e -> openNewAddressWindow(addressComboBox));

        ComboBox<String> positionBox = new ComboBox<>();
        positionBox.getItems().addAll("Kasjer", "Kierownik", "Admin", "Logistyk");
        positionBox.setValue(selected.getPosition());

        TextField ageField = new TextField(String.valueOf(selected.getAge()));
        ageField.setPromptText("Wiek");

        TextField salaryField = new TextField(selected.getSalary().toString());
        salaryField.setPromptText("Zarobki (PLN)");

        Button saveButton = new Button("Zapisz zmiany");
        Button cancelButton = new Button("Anuluj");
        HBox buttons = new HBox(10, saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        formLayout.getChildren().addAll(
                titleLabel,
                nameField,
                surnameField,
                loginField,
                passwordField,
                emailField,
                addressLabel,
                new HBox(10, addressComboBox, addNewAddressBtn),
                positionBox,
                ageField,
                salaryField,
                buttons
        );

        adminPanel.setCenterPane(formLayout);

        saveButton.setOnAction(e -> {
            try {
                // Sprawdzenie, czy wszystkie pola poza hasłem są wypełnione
                if (nameField.getText().isEmpty()
                        || surnameField.getText().isEmpty()
                        || loginField.getText().isEmpty()
                        || emailField.getText().isEmpty()
                        || addressComboBox.getValue() == null
                        || positionBox.getValue() == null
                        || ageField.getText().isEmpty()
                        || salaryField.getText().isEmpty()) {

                    showAlert(Alert.AlertType.WARNING,
                            "Brak danych",
                            "Uzupełnij wszystkie pola (poza hasłem).");
                    return;
                }

                String newLogin = loginField.getText().trim();
                String newEmail = emailField.getText().trim();


                if(!nameField.getText().matches("[A-Za-z]+")) {
                    showAlert(Alert.AlertType.ERROR,
                            "Nieprawidłowe imię",
                            "Imię może zawierać tylko litery.");
                    return;
                }

                if(!surnameField.getText().matches("[A-Za-z]+")) {
                    showAlert(Alert.AlertType.ERROR,
                            "Nieprawidłowe nazwisko",
                            "Nazwisko może zawierać tylko litery.");
                    return;
                }

                // Walidacja loginu: odrzucamy znaki specjalne
                if (!newLogin.matches("[A-Za-z0-9]+")) {
                    showAlert(Alert.AlertType.ERROR,
                            "Nieprawidłowy login",
                            "Login może zawierać tylko litery i cyfry, bez znaków specjalnych.");
                    return;
                }

                // Walidacja e-mailu: e-mail w formacie .+@.+\..+
                if (!newEmail.matches(".+@.+\\..+")) {
                    showAlert(Alert.AlertType.ERROR,
                            "Nieprawidłowy email",
                            "Podaj poprawny adres e-mail (np. user@example.com).");
                    return;
                }

                showLoadingIndicator();

                selected.setName(nameField.getText().trim());
                selected.setSurname(surnameField.getText().trim());
                selected.setLogin(newLogin);
                selected.setEmail(emailField.getText().trim());
                selected.setAddress(addressComboBox.getValue());
                if (!passwordField.getText().isEmpty()) {
                    selected.setPassword(passwordField.getText());
                }
                selected.setPosition(positionBox.getValue());
                selected.setAge(Integer.parseInt(ageField.getText().trim()));
                selected.setSalary(new BigDecimal(salaryField.getText().trim()));

                Task<Void> updateTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        userRepository.updateEmployee(selected);
                        return null;
                    }
                };

                updateTask.setOnSucceeded(evt -> {
                    showAlert(Alert.AlertType.INFORMATION,
                            "Sukces",
                            "Dane użytkownika zostały zaktualizowane.");
                    showUserManagement();
                });

                updateTask.setOnFailed(evt -> {
                    updateTask.getException().printStackTrace();
                    showAlert(Alert.AlertType.ERROR,
                            "Błąd",
                            "Wystąpił błąd podczas zapisywania zmian: "
                                    + updateTask.getException().getMessage());
                    showUserManagement();
                });

                executor.execute(updateTask);

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR,
                        "Błąd",
                        "Nieprawidłowy format wieku lub zarobków!");
                showUserManagement();
            } catch (PasswordException ex) {
                showAlert(Alert.AlertType.ERROR,
                        "Nieprawidłowe hasło",
                        ex.getMessage());
                showUserManagement();
            } catch (SalaryException ex) {
                showAlert(Alert.AlertType.ERROR,
                        "Nieprawidłowe zarobki",
                        ex.getMessage());
                showUserManagement();
            }
        });

        cancelButton.setOnAction(e -> showUserManagement());
    }

    /**
     * Pobiera dane z bazy i ładuje do tabeli asynchronicznie.
     */
    private void refreshEmployeeList() {
        Task<List<Employee>> task = new Task<>() {
            @Override
            protected List<Employee> call() throws Exception {
                return userRepository.getAllEmployees();
            }
        };

        task.setOnSucceeded(e -> {
            tableView.getItems().clear();
            tableView.getItems().addAll(task.getValue());
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            Platform.runLater(() -> showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd",
                    "Nie udało się pobrać listy pracowników"
            ));
        });

        executor.execute(task);
    }

    /**
     * Formularz dodawania nowego użytkownika.
     */
    private void addNewUser() {
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

        // Adres
        Label addressLabel = new Label("Adres:");
        AddressRepository addressRepository = new AddressRepository();
        ComboBox<Address> addressComboBox = new ComboBox<>();
        addressComboBox.getItems().addAll(addressRepository.getAllAddresses());
        addressComboBox.setPromptText("Wybierz istniejący adres");

        Button addNewAddressBtn = new Button("Dodaj nowy adres");
        addNewAddressBtn.setOnAction(e -> openNewAddressWindow(addressComboBox));

        ComboBox<String> positionComboBox = new ComboBox<>();
        positionComboBox.getItems().addAll("Kasjer", "Kierownik", "Admin", "Logistyk");
        positionComboBox.setPromptText("Stanowisko");

        TextField ageField = new TextField();
        ageField.setPromptText("Wiek");

        TextField salaryField = new TextField();
        salaryField.setPromptText("Zarobki (PLN)");

        Button saveButton = new Button("Zapisz");
        Button cancelButton = new Button("Anuluj");

        HBox buttons = new HBox(10, saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        // formLayout
        formLayout.getChildren().addAll(
                titleLabel,
                nameField,
                surnameField,
                loginField,
                passwordField,
                emailField,
                addressLabel,
                new HBox(10, addressComboBox, addNewAddressBtn),
                positionComboBox,
                ageField,
                salaryField,
                buttons
        );

        adminPanel.setCenterPane(formLayout);

        saveButton.setOnAction(e -> {
            try {
                // Sprawdzenie, czy nie ma pustych pól
                if (nameField.getText().isEmpty()
                        || surnameField.getText().isEmpty()
                        || loginField.getText().isEmpty()
                        || passwordField.getText().isEmpty()
                        || emailField.getText().isEmpty()
                        || addressComboBox.getValue() == null
                        || positionComboBox.getValue() == null
                        || ageField.getText().isEmpty()
                        || salaryField.getText().isEmpty()) {

                    showAlert(Alert.AlertType.WARNING, "Brak danych", "Uzupełnij wszystkie pola!");
                    return;
                }

                String loginText = loginField.getText().trim();
                String emailText = emailField.getText().trim();

                if(!nameField.getText().matches("[A-Za-z]+")) {
                    showAlert(Alert.AlertType.ERROR,
                            "Nieprawidłowe imię",
                            "Imię może zawierać tylko litery.");
                    return;
                }

                if(!surnameField.getText().matches("[A-Za-z]+")) {
                    showAlert(Alert.AlertType.ERROR,
                            "Nieprawidłowe nazwisko",
                            "Nazwisko może zawierać tylko litery.");
                    return;
                }

                // Walidacja loginu: odrzucamy znaki specjalne
                if (!loginText.matches("[A-Za-z0-9]+")) {
                    showAlert(Alert.AlertType.ERROR,
                            "Nieprawidłowy login",
                            "Login może zawierać tylko litery i cyfry, bez znaków specjalnych.");
                    return;
                }

                // Walidacja e-mailu: e-mail w formacie .+@.+\..+
                if (!emailText.matches(".+@.+\\..+")) {
                    showAlert(Alert.AlertType.ERROR,
                            "Nieprawidłowy email",
                            "Podaj poprawny adres e-mail (np. user@example.com).");
                    return;
                }

                // NOWA FUNKCJONALNOŚĆ: Sprawdzenie unikalności loginu
                UserRepository checkRepo = new UserRepository();
                Employee existingEmployee = checkRepo.findByLogin(loginText);
                if (existingEmployee != null) {
                    showAlert(Alert.AlertType.ERROR,
                            "Login zajęty",
                            "Użytkownik o loginie '" + loginText +
                                    "' już istnieje w systemie. Wybierz inny login.");
                    return;
                }

                int age = Integer.parseInt(ageField.getText().trim());
                BigDecimal salary = new BigDecimal(salaryField.getText().trim());

                Employee newEmployee = new Employee();
                newEmployee.setName(nameField.getText().trim());
                newEmployee.setSurname(surnameField.getText().trim());
                newEmployee.setLogin(loginText);
                newEmployee.setPassword(passwordField.getText());
                newEmployee.setEmail(emailField.getText().trim());
                newEmployee.setAddress(addressComboBox.getValue());
                newEmployee.setPosition(positionComboBox.getValue());
                newEmployee.setAge(age);
                newEmployee.setSalary(salary);

                Task<Void> addTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        userRepository.addEmployee(newEmployee);
                        return null;
                    }
                };

                addTask.setOnSucceeded(evt -> {
                    showAlert(Alert.AlertType.INFORMATION, "Sukces", "Dodano nowego użytkownika!");
                    showUserManagement();
                });

                addTask.setOnFailed(evt -> {
                    addTask.getException().printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Błąd",
                            "Nie udało się dodać użytkownika: " + addTask.getException().getMessage());
                    showUserManagement();
                });

                executor.execute(addTask);

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Błąd",
                        "Nieprawidłowy format wieku lub zarobków!");
                showUserManagement();
            } catch (PasswordException ex) {
                showAlert(Alert.AlertType.ERROR, "Nieprawidłowe hasło", ex.getMessage());
                showUserManagement();
            } catch (SalaryException ex) {
                showAlert(Alert.AlertType.ERROR, "Nieprawidłowe zarobki", ex.getMessage());
                showUserManagement();
            }
        });

        cancelButton.setOnAction(e -> showUserManagement());
    }

    /**
     * Usuwa zaznaczonego użytkownika asynchronicznie.
     * Zabezpiecza przed usunięciem użytkownika z rolą "root".
     * Zabezpiecza przed usunięciem własnego konta przez administratora.
     * Usuwa zaznaczonego użytkownika (soft-delete) i odświeża tabelę.
     */
    private void removeSelectedUser() {
        Employee selected = tableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Brak wyboru",
                    "Wybierz użytkownika do usunięcia."
            );
            return;
        }

        if (selected.isRoot()) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Niedozwolona operacja",
                    "Nie można usunąć użytkownika z uprawnieniami administratora (root)"
            );
            return;
        }

        // Sprawdź, czy admin próbuje usunąć własne konto
        // Pobierz aktualnie zalogowanego użytkownika
        UserRepository repo = new UserRepository();
        Employee currentUser = repo.getCurrentEmployee();

        if (currentUser != null && currentUser.getId() == selected.getId()) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Niedozwolona operacja",
                    "Nie możesz usunąć własnego konta będąc zalogowanym."
            );
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Potwierdzenie usunięcia");
        confirm.setHeaderText("Czy na pewno chcesz usunąć użytkownika?");
        confirm.setContentText(selected.getName() + " " + selected.getSurname());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userRepository.removeEmployee(selected);
                    refreshEmployeeList(); // ponowne załadowanie aktywnych
                    showAlert(
                            Alert.AlertType.INFORMATION,
                            "Sukces",
                            "Użytkownik został oznaczony jako usunięty."
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Błąd",
                            "Nie udało się usunąć użytkownika: " + e.getMessage()
                    );
                }
            }
        });
    }

    /**
     * Wyświetla panel ustawień konfiguracyjnych systemu.
     * Tworzenie węzłów odbywa się na wątku JavaFX – brak konfliktów
     * z toolkitem.
     */
    public void showConfigPanel() {
        VBox freshConfigView = createConfigPanelView();
        adminPanel.setCenterPane(freshConfigView);
    }

    /**
     * Tworzy widok panelu konfiguracji.
     *
     * @return VBox zawierający kompletny widok
     */
    /**
     * Buduje widok panelu konfiguracji.
     */
    private VBox createConfigPanelView() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Opcje konfiguracyjne");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Przycisk do otwierania folderu z logami
        Button openLogsButton = new Button("Otwórz folder z logami");
        styleAdminButton(openLogsButton, "#9B59B6");
        openLogsButton.setOnAction(e -> openLogsDirectory("logs"));

        Button configurePDF = new Button("Konfiguruj raporty PDF");
        styleAdminButton(configurePDF, "#2980B9");
        configurePDF.setOnAction(e -> showPDFConfigPanel());

        Button backupButton = new Button("Wykonaj backup bazy danych");
        styleAdminButton(backupButton, "#27AE60");
        backupButton.setOnAction(e -> performDatabaseBackup());

        // DODANY PRZYCISK DO EKSPORTU CSV
        Button exportCsvButton = new Button("Eksportuj bazę danych do CSV");
        styleAdminButton(exportCsvButton, "#16A085");  // Inny kolor dla odróżnienia
        exportCsvButton.setOnAction(e -> exportDatabaseToCsv());

        layout.getChildren().addAll(
                titleLabel,
                openLogsButton,
                configurePDF,
                backupButton,
                exportCsvButton  // DODANY PRZYCISK
        );

        return layout;
    }

    private void exportDatabaseToCsv() {

        File folder = new File("backup-csv");
        if (!folder.exists() && !folder.mkdirs()) {
            showAlert(Alert.AlertType.ERROR, "Błąd",
                    "Nie można utworzyć katalogu:\n" + folder.getAbsolutePath());
            return;
        }

        /* ---------- loader ---------- */
        Stage loaderStage = new Stage();
        loaderStage.initOwner(primaryStage);
        loaderStage.initModality(Modality.APPLICATION_MODAL);
        loaderStage.initStyle(StageStyle.UNDECORATED);

        VBox box = new VBox(10, new ProgressIndicator());
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        loaderStage.setScene(new Scene(box));
        loaderStage.setTitle("Eksport CSV – trwa…");
        loaderStage.show();

        /* ---------- zadanie ---------- */
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                logger.info("Rozpoczęcie eksportu CSV do: {}", folder.getAbsolutePath());
                DatabaseBackupExporter.exportAllTablesToCsv(folder.getAbsolutePath());
                return null;
            }
        };

        task.setOnSucceeded(ev -> Platform.runLater(() -> {
            loaderStage.close();
            showAlert(Alert.AlertType.INFORMATION, "Eksport zakończony",
                    "Pliki CSV znajdują się w:\n" + folder.getAbsolutePath());
        }));

        task.setOnFailed(ev -> Platform.runLater(() -> {
            loaderStage.close();
            Throwable ex = task.getException();
            logger.error("Błąd eksportu CSV", ex);
            showAlert(Alert.AlertType.ERROR, "Błąd eksportu",
                    (ex != null) ? ex.getMessage() : "Nieznany błąd");
        }));

        new Thread(task, "CsvExportTask").start();
    }

    public void openLogsDirectory(String path) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;

            if (os.contains("windows")) {
                pb = new ProcessBuilder("explorer", path);
            } else if (os.contains("mac")) {
                pb = new ProcessBuilder("open", path);
            } else if (os.contains("nix") || os.contains("nux") || os.contains("bsd")) {
                pb = new ProcessBuilder("xdg-open", path);
            } else {
                throw new UnsupportedOperationException("Nieobsługiwany system operacyjny");
            }

            pb.start();
        } catch (Exception e) {
            e.printStackTrace();
            // Obsługa błędu - np. wyświetlenie komunikatu użytkownikowi
            showAlert(Alert.AlertType.ERROR, "Błąd",
                    "Nie można otworzyć katalogu logów: " + e.getMessage());
        }
    }

    /**
     * Wyświetla panel konfiguracji plików PDF.
     */
    public void showPDFConfigPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Ustawienia raportów PDF");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label logoLabel = new Label("Logo:");
        TextField logoField = new TextField();
        logoField.setPrefWidth(400);
        logoField.setPromptText("Wybierz plik logo (PNG/JPG)");

        String savedLogo = ConfigManager.getLogoPath();
        if (savedLogo != null && !savedLogo.isBlank()) {
            logoField.setText(savedLogo);
        }

        Button chooseLogoButton = new Button("Wybierz nowe logo");
        styleAdminButton(chooseLogoButton, "#2980B9");
        chooseLogoButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Wybierz plik logo");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Grafika (PNG, JPG)", "*.png", "*.jpg",
                            "*.jpeg")
            );
            File initialDir = new File(System.getProperty("user.dir"));
            if (initialDir.exists()) {
                fileChooser.setInitialDirectory(initialDir);
            }
            File selected = fileChooser.showOpenDialog(primaryStage);
            if (selected != null && selected.isFile()) {
                logoField.setText(selected.getAbsolutePath());
            }
        });

        HBox logoBox = new HBox(10, logoField, chooseLogoButton);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        Label pathLabel = new Label("Ścieżka zapisu raportów:");
        TextField pathField = new TextField();
        pathField.setPrefWidth(400);
        pathField.setPromptText("Wybierz katalog, w którym będą zapisywane raporty");
        pathField.setText(ConfigManager.getReportPath());

        // Wczytaj aktualną ścieżkę z ConfigManager (jeśli jest)
        String savedPath = ConfigManager.getReportPath();
        if (savedPath != null && !savedPath.isBlank()) {
            pathField.setText(new File(savedPath).getAbsolutePath());
        }

        Button choosePathButton = new Button("Wybierz katalog…");
        styleAdminButton(choosePathButton, "#2980B9");
        choosePathButton.setOnAction(e -> {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Wybierz katalog na raporty");
            File initialDir = new File(System.getProperty("user.dir"));
            if (initialDir.exists()) {
                dirChooser.setInitialDirectory(initialDir);
            }
            File selected = dirChooser.showDialog(primaryStage);
            if (selected != null && selected.isDirectory()) {
                pathField.setText(selected.getAbsolutePath());
            }
        });

        HBox pathBox = new HBox(10, pathField, choosePathButton);
        pathBox.setAlignment(Pos.CENTER_LEFT);

        Button saveButton = new Button("Zapisz konfigurację");
        styleAdminButton(saveButton,"#3498DB");

        saveButton.setOnAction(e -> {
            String logo = logoField.getText().trim();
            String path = pathField.getText().trim();

            if (path.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Błąd", "Ścieżka nie może być pusta.");
                return;
            }

            File folder = new File(path);
            if (!folder.exists() || !folder.isDirectory()) {
                showAlert(Alert.AlertType.ERROR, "Niepoprawna ścieżka",
                        "Podany folder nie istnieje.");
                return;
            }

            if (!logo.isEmpty()) {
                File logoFile = new File(logo);
                if (!logoFile.exists() || !logoFile.isFile()) {
                    showAlert(Alert.AlertType.ERROR, "Niepoprawny plik logo",
                            "Podany plik logo nie istnieje.");
                    return;
                }
                // zapisujemy ścieżkę do logo tylko jeśli nie jest pusta
                ConfigManager.setLogoPath(logoFile.getAbsolutePath());
            }
            ConfigManager.setReportPath(path);
            showAlert(Alert.AlertType.INFORMATION, "Zapisano",
                    "Zaktualizowano konfigurację raportów");
        });

        Button backButton = new Button("Wróć");
        styleAdminButton(backButton,"#E74C3C");
        backButton.setOnAction(e -> showConfigPanel());

        layout.getChildren().addAll(
                titleLabel,
                logoLabel, logoBox,
                pathLabel, pathBox,
                saveButton,
                backButton
        );

        adminPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla panel generowania raportów.
     */
    public void showReportsPanel() {
        if (reportsPanelView == null) {
            reportsPanelView = createReportsPanelView();
        }
        adminPanel.setCenterPane(reportsPanelView);
    }

    /**
     * Buduje (synchronnie) widok panelu raportów.
     */
    private VBox createReportsPanelView() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Generowanie raportów");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button statsBtn = new Button("Raport KPI (Statystyki)");
        styleAdminButton(statsBtn,"#2980B9");

        Button taskBtn  = new Button("Raport zadań");
        styleAdminButton(taskBtn,"#3498DB");

        Button loadBtn  = new Button("Raport obciążenia");
        styleAdminButton(loadBtn,"#27AE60");

        statsBtn.setOnAction(e -> showStatsReportDialog());
        taskBtn.setOnAction(e  -> showTaskReportDialog());
        loadBtn.setOnAction(e  -> showWorkloadReportDialog());

        layout.getChildren().addAll(titleLabel, statsBtn, taskBtn, loadBtn);
        return layout;
    }

    /**
     * Otwiera okno dialogowe z filtrami do generowania
     * raportu KPI (StatsRaportGenerator).
     * Umożliwia wybór zakresu dat, stanowisk oraz priorytetów.
     */
    private void showStatsReportDialog() {
        logger.debug("Otwieranie dialogu raportu KPI");
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Raport KPI – filtry");

        // Zakres dat
        DatePicker start = new DatePicker();
        DatePicker end = new DatePicker();

        // Lista stanowisk – multi-select
        ListView<String> positionsList = new ListView<>();
        positionsList.setPrefSize(200, 100);
        positionsList.getItems().addAll("Kasjer", "Logistyk", "Pracownik", "Kierownik");
        positionsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Lista priorytetów
        ListView<StatsRaportGenerator.Priority> prioList = new ListView<>();
        prioList.setPrefSize(200, 100);
        prioList.getItems().addAll(StatsRaportGenerator.Priority.values());
        prioList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Data od:"), start, new Label("Data do:"), end);
        grid.addRow(1, new Label("Stanowiska:"), positionsList);
        grid.addRow(2, new Label("Priorytety:"), prioList);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                LocalDate startDate = start.getValue();
                LocalDate endDate = end.getValue();
                List<String> selectedPositions =
                        new ArrayList<>(positionsList.getSelectionModel().getSelectedItems());
                List<StatsRaportGenerator.Priority> selectedPriorities =
                        new ArrayList<>(prioList.getSelectionModel().getSelectedItems());

                // Logowanie parametrów
                logger.debug("Parametry raportu KPI: startDate={}, endDate={}, selectedPositions={}, selectedPriorities={}",
                        startDate, endDate, selectedPositions, selectedPriorities);

                // Walidacja dat
                if (startDate == null || endDate == null) {
                    logger.warn("Brak wybranych dat: startDate={}, endDate={}", startDate, endDate);
                    showAlert(Alert.AlertType.ERROR,
                            "Błąd", "Proszę wybrać daty początkową i końcową.");
                    return null;
                }

                if (startDate.isAfter(endDate)) {
                    logger.warn("Nieprawidłowy zakres dat: startDate={} jest późniejsze niż endDate={}", startDate, endDate);
                    showAlert(Alert.AlertType.ERROR, "Błąd",
                            "Data początkowa nie może być późniejsza niż końcowa.");
                    return null;
                }

                // Walidacja zakresu jednego dnia
                if (startDate.equals(endDate)) {
                    List<StatsRaportGenerator.TaskRecord> tasks =
                            fetchTaskStatsData(startDate, endDate);
                    boolean hasTasks = tasks.stream().anyMatch(task -> {
                        LocalDate dateToCheck = task.completionDate() !=
                                null ? task.completionDate() : task.dueDate();
                        return dateToCheck != null && dateToCheck.equals(startDate);
                    });
                    if (!hasTasks) {
                        logger.warn("Brak zadań dla wybranego dnia: {}", startDate);
                        showAlert(Alert.AlertType.WARNING, "Brak danych",
                                "Brak zadań dla wybranego dnia: " + startDate);
                        return null;
                    }
                }

                // Ostrzeżenia dla potencjalnych problemów
                if (selectedPositions.isEmpty()) {
                    logger.debug("Nie wybrano żadnych stanowisk – raport uwzględni " +
                            "wszystkie stanowiska");
                }
                if (selectedPriorities.isEmpty()) {
                    logger.debug("Nie wybrano żadnych priorytetów – raport uwzględni " +
                            "wszystkie priorytety");
                }

                try {
                    generateStatsPDF(startDate, endDate, selectedPositions, selectedPriorities);
                    logger.info("Wysłano żądanie generowania raportu KPI");
                } catch (Exception e) {
                    logger.error("Błąd podczas generowania raportu KPI", e);
                    showAlert(Alert.AlertType.ERROR, "Błąd",
                            "Nie udało się wygenerować raportu: " + e.getMessage());
                }
            } else {
                logger.debug("Anulowano generowanie raportu KPI");
            }
            return null;
        });

        logger.debug("Wyświetlanie dialogu raportu KPI");
        dialog.showAndWait();
    }

    /**
     * Generuje PDF z KPI (StatsRaportGenerator) na podstawie wybranych
     * filtrów.
     *
     * @param from       początek okresu (inclusive)
     * @param to         koniec okresu (inclusive)
     * @param positions  lista nazw stanowisk, które mają się znaleźć w
     *                   raporcie
     * @param priors     lista priorytetów (HIGH/MEDIUM/LOW), do filtrowania
     *                   zadań
     */
    /**
     * Generuje PDF z KPI (StatsRaportGenerator) na podstawie wybranych
     * filtrów.
     */
    private void generateStatsPDF(LocalDate from,
                                  LocalDate to,
                                  List<String> positions,
                                  List<StatsRaportGenerator.Priority> priors) {

        String reportPath = ConfigManager.getReportPath();
        if (reportPath == null || reportPath.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR,
                    "Brak ścieżki do raportów",
                    "Najpierw ustaw ścieżkę do zapisywania raportów w panelu " +
                            "konfiguracji ");
            return;
        }

        try {
            logger.debug("Generowanie raportu KPI dla dat: {}–{}, stanowiska: {}, priorytety: {}",
                    from, to, positions, priors);

            StatsRaportGenerator gen   = new StatsRaportGenerator();
            String logoPath = ConfigManager.getLogoPath();
            if (logoPath != null && !logoPath.isBlank()) {
                gen.setLogoPath(logoPath);
            }
            List<StatsRaportGenerator.TaskRecord> taskData = fetchTaskStatsData(from, to);
            logger.info("Pobrano {} zadań dla raportu KPI", taskData.size());

            gen.setTaskData(taskData);

            String out = ConfigManager.getReportPath()
                    + "/stats-" + System.currentTimeMillis() + ".pdf";

            gen.generateReport(out, from, to, positions, priors);

            showAlert(Alert.AlertType.INFORMATION, "Raport wygenerowany", out);

        } catch (Exception ex) {
            logger.error("Błąd podczas generowania raportu KPI", ex);
            showAlert(Alert.AlertType.ERROR, "Błąd", ex.getMessage());
        }
    }

    /**
     * Otwiera okno dialogowe z filtrami do generowania raportu
     * zadań (TaskRaportGenerator).
     * Umożliwia wybór okresu (enum PeriodType) oraz statusów zadań.
     */
    private void showTaskReportDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Raport zadań – filtry");

        // Okres (domenowy enum)
        ComboBox<PeriodType> period = new ComboBox<>();
        period.getItems().addAll(PeriodType.values());

        // Statusy
        ListView<String> statusList = new ListView<>();
        statusList.setPrefSize(200, 100);
        statusList.getItems().addAll("Nowe", "W trakcie", "Zakończone", "Opóźnione");
        statusList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        GridPane g = new GridPane();
        g.setHgap(10); g.setVgap(10);
        g.addRow(0, new Label("Okres:"), period);
        g.addRow(1, new Label("Statusy:"), statusList);

        dialog.getDialogPane().setContent(g);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                generateTaskPDF(
                        period.getValue(),
                        new ArrayList<>(statusList.getSelectionModel().getSelectedItems())
                );
            }
            return null;
        });
        dialog.showAndWait();
    }

    /**
     * Generuje PDF z raportem zadań (TaskRaportGenerator).
     *
     * @param period    domenowy typ okresu (enum PeriodType) – będzie
     *                  zmapowany na wewnętrzny TaskRaportGenerator.PeriodType
     * @param statuses  lista statusów zadań do uwzględnienia w raporcie
     */
    /* dokładnie ta jedna linijka jest kluczowa */
    private void generateTaskPDF(PeriodType period, List<String> statuses) {
        try {
            logger.debug("Generowanie raportu zadań dla okresu: {}, statusy: {}",
                    period, statuses);

            TaskRaportGenerator gen = new TaskRaportGenerator();
            // ustawienie ścieżki do logo:
            String logoPath = ConfigManager.getLogoPath();
            if (logoPath != null && !logoPath.isBlank()) {
                gen.setLogoPath(logoPath);
            }

            // dane z repozytorium
            List<TaskRaportGenerator.TaskRecord> taskData = fetchTaskSimpleData(period);
            logger.info("Pobrano {} zadań dla raportu zadań", taskData.size());

            gen.setTaskData(taskData);

            // mapowanie własnego enum-a na enum generatora
            TaskRaportGenerator.PeriodType pdfPeriod = switch (period) {
                case DAILY   -> TaskRaportGenerator.PeriodType.LAST_WEEK;
                case MONTHLY -> TaskRaportGenerator.PeriodType.LAST_MONTH;
                case YEARLY  -> TaskRaportGenerator.PeriodType.LAST_QUARTER;
            };

            String out = ConfigManager.getReportPath()
                    + "/tasks-" + System.currentTimeMillis() + ".pdf";

            gen.generateReport(out, pdfPeriod, statuses);
            showAlert(Alert.AlertType.INFORMATION, "Raport wygenerowany", out);

        } catch (Exception ex) {
            logger.error("Błąd podczas generowania raportu zadań", ex);
            showAlert(Alert.AlertType.ERROR, "Błąd", ex.getMessage());
        }
    }


    /**
     * Otwiera okno dialogowe z filtrami do generowania
     * raportu obciążenia (WorkloadReportGenerator).
     * Pozwala wybrać zakres dat, stanowiska i statusy obciążenia.
     */
    private void showWorkloadReportDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Raport obciążenia – filtry");

        DatePicker start = new DatePicker();
        DatePicker end   = new DatePicker();

        ListView<String> positionsList = new ListView<>();
        positionsList.setPrefSize(200, 100);
        positionsList.getItems().addAll("Kasjer", "Logistyk", "Pracownik");
        positionsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ListView<String> statusList = new ListView<>();
        statusList.setPrefSize(200, 100);
        statusList.getItems().addAll("Przeciążenie", "Niedociążenie", "Optymalne");
        statusList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        GridPane g = new GridPane();
        g.setHgap(10); g.setVgap(10);
        g.addRow(0, new Label("Data od:"), start, new Label("Data do:"), end);
        g.addRow(1, new Label("Stanowiska:"), positionsList);
        g.addRow(2, new Label("Statusy:"), statusList);

        dialog.getDialogPane().setContent(g);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                // Walidacja dat
                if (start.getValue() != null && end.getValue() != null) {
                    if (start.getValue().isAfter(end.getValue())) {
                        // Wyświetl alert o nieprawidłowym zakresie dat
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Nieprawidłowy zakres dat");
                        alert.setHeaderText("Data początkowa nie może być późniejsza niż data końcowa");
                        alert.setContentText("Proszę poprawić zakres dat.");
                        alert.showAndWait();

                        // Zapobiegaj zamknięciu dialogu
                        return null;
                    }
                }

                // Kontynuuj generowanie raportu, jeśli daty są poprawne
                generateWorkloadPDF(start.getValue(), end.getValue(),
                        new ArrayList<>(positionsList.getSelectionModel().getSelectedItems()),
                        new ArrayList<>(statusList.getSelectionModel().getSelectedItems()));
            }
            return null;
        });
        dialog.showAndWait();
    }

    /**
     * Generuje PDF z raportem obciążenia pracowników (WorkloadReportGenerator).
     *
     * @param from       początek okresu raportowania (inclusive)
     * @param to         koniec okresu raportowania (inclusive)
     * @param positions  lista stanowisk do uwzględnienia
     * @param statuses   lista statusów obciążenia ("Przeciążenie",
     *                   "Niedociążenie", "Optymalne")
     */
    private void generateWorkloadPDF(LocalDate from, LocalDate to,
                                     List<String> positions,
                                     List<String> statuses) {
        try {
            logger.debug("Generowanie raportu obciążenia dla dat: {} do {}, stanowiska: {}, statusy: {}",
                    from, to, positions, statuses);
            WorkloadReportGenerator gen = new WorkloadReportGenerator();
            // ustawienie ścieżki do logo:
            String logoPath = ConfigManager.getLogoPath();
            if (logoPath != null && !logoPath.isBlank()) {
                gen.setLogoPath(logoPath);
            }
            List<WorkloadReportGenerator.EmployeeWorkload> workloadData =
                    fetchWorkloadData(from, to);
            logger.info("Pobrano dane dla {} pracowników dla raportu obciążenia",
                    workloadData.size());
            gen.setWorkloadData(workloadData);
            String out = ConfigManager.getReportPath() + "/workload-" +
                    System.currentTimeMillis() + ".pdf";
            gen.generateReport(out, from, to, positions, statuses);
            showAlert(Alert.AlertType.INFORMATION, "Raport wygenerowany", out);
        } catch (Exception ex) {
            logger.error("Błąd podczas generowania raportu obciążenia", ex);
            showAlert(Alert.AlertType.ERROR, "Błąd", ex.getMessage());
        }
    }

    private List<StatsRaportGenerator.TaskRecord> fetchTaskStatsData(LocalDate from,
                                                                     LocalDate to) {
        logger.debug("Pobieranie danych dla raportu KPI od {} do {}", from, to);
        EmpTaskRepository repo = new EmpTaskRepository();
        List<EmpTask> allTasks = repo.getAllTasksWithEmployeesAndAssignees();
        logger.info("Pobrano {} zadań z repozytorium", allTasks.size());

        // Logowanie szczegółów surowych zadań
        allTasks.forEach(t -> logger.debug("Zadanie surowe: id={}, name={}, date={}, status={}, priority={}, assignee={}",
                t.getId(), t.getName(), toLocalDate(t.getDate()), t.getStatus(),
                t.getPriority(), t.getSingleAssignee() !=
                        null ? t.getSingleAssignee().getLogin() : "Brak"));

        List<StatsRaportGenerator.TaskRecord> filteredTasks = allTasks.stream()
                .filter(t -> inRange(t.getDate(), from, to))
                .map(t -> {
                    Employee assignee = t.getSingleAssignee();
                    StatsRaportGenerator.Priority priority = null;
                    if (t.getPriority() != null) {
                        try {
                            priority = StatsRaportGenerator.Priority.valueOf(t.getPriority().name());
                        } catch (IllegalArgumentException e) {
                            logger.warn("Nie można zmapować priorytetu: {} dla zadania: {}",
                                    t.getPriority(), t.getName());
                        }
                    }
                    StatsRaportGenerator.TaskRecord record = new StatsRaportGenerator.TaskRecord(
                            t.getName(),
                            assignee != null ? assignee.getPosition() : "Brak",
                            priority,
                            toLocalDate(t.getDate()), // dueDate
                            "Zakończone".equals(t.getStatus()) ? toLocalDate(t.getDate()) : null, // completionDate
                            assignee != null ? assignee.getLogin() : "Brak"
                    );
                    // Logowanie każdego utworzonego TaskRecord
                    logger.debug("Utworzono TaskRecord: taskName={}, position={}, priority={}, dueDate={}, completionDate={}, assignee={}",
                            record.taskName(), record.position(), record.priority(),
                            record.dueDate(), record.completionDate(), record.assignee());
                    return record;
                })
                .toList();

        logger.info("Po filtracji dat: {} zadań", filteredTasks.size());
        // Logowanie podsumowania listy TaskRecord
        logger.debug("Podsumowanie TaskRecords: {}", filteredTasks.stream()
                .map(r -> String.format("{taskName=%s, dueDate=%s, completionDate=%s," +
                                " priority=%s, position=%s, assignee=%s}",
                        r.taskName(), r.dueDate(), r.completionDate(), r.priority(),
                        r.position(), r.assignee()))
                .collect(Collectors.joining(", ")));

        return filteredTasks;
    }

    private List<TaskRaportGenerator.TaskRecord> fetchTaskSimpleData(LocalDate from,
                                                                     LocalDate to) {
        logger.debug("Pobieranie danych dla raportu zadań od {} do {}", from, to);

        EmpTaskRepository repo   = new EmpTaskRepository();
        List<EmpTask>     source = repo.getAllTasksWithEmployeesAndAssignees();
        logger.info("Pobrano {} zadań z repozytorium", source.size());

        List<TaskRaportGenerator.TaskRecord> result = source.stream()
                .filter(t -> inRange(t.getDate(), from, to))
                .map(t -> {
                    Employee assignee = t.getSingleAssignee();
                    return new TaskRaportGenerator.TaskRecord(
                            t.getName(),
                            // dueDate
                            toLocalDate(t.getDate()),
                            // completionDate
                            "Zakończone".equals(t.getStatus())
                                    ? toLocalDate(t.getDate())
                                    : null,
                            // assignee
                            assignee != null ? assignee.getLogin() : "Brak",
                            // dbStatus  <- NOWE
                            t.getStatus()
                    );
                })
                .toList();

        logger.info("Po filtracji dat: {} zadań", result.size());
        return result;
    }


    private List<TaskRaportGenerator.TaskRecord> fetchTaskSimpleData(PeriodType period) {

        logger.debug("Pobieranie danych dla raportu zadań dla okresu: {}", period);

        LocalDate end   = LocalDate.now();
        LocalDate start = switch (period) {
            case DAILY   -> end.minusWeeks(1);   // 7 dni
            case MONTHLY -> end.minusMonths(1);  // 1 miesiąc
            case YEARLY  -> end.minusMonths(3);  // 1 kwartał
        };

        return fetchTaskSimpleData(start, end);
    }


    /* ------------------------------------------------------------ */
    /* Obciążenie – WorkloadReportGenerator                         */
    /* ------------------------------------------------------------ */
    private List<WorkloadReportGenerator.EmployeeWorkload> fetchWorkloadData(LocalDate from,
                                                                             LocalDate to) {
        logger.debug("Pobieranie danych dla raportu obciążenia od {} do {}", from, to);
        EmpTaskRepository repo = new EmpTaskRepository();
        List<EmpTask> allTasks = repo.getAllTasksWithEmployeesAndAssignees();
        logger.info("Pobrano {} zadań z repozytorium", allTasks.size());
        List<WorkloadReportGenerator.EmployeeWorkload> workloadData = allTasks.stream()
                .filter(t -> !t.getTaskEmployees().isEmpty()
                        && t.getDurationOfTheShift() != null
                        && inRange(t.getDate(), from, to))
                .collect(Collectors.groupingBy(
                        EmpTask::getSingleAssignee,
                        Collectors.summingDouble(t -> hours(t.getDurationOfTheShift()))
                ))
                .entrySet().stream()
                .map(e -> new WorkloadReportGenerator.EmployeeWorkload(
                        e.getKey().getLogin(),
                        e.getKey().getPosition(),
                        e.getValue()
                ))
                .toList();
        logger.info("Po filtracji i grupowaniu: dane dla {} pracowników",
                workloadData.size());
        return workloadData;
    }

    // =====================  METODY POMOCNICZE  ====================
    private static boolean inRange(Date d, LocalDate from, LocalDate to) {
        if (d == null) return false;
        LocalDate ld = toLocalDate(d);
        return (from == null || !ld.isBefore(from)) && (to == null || !ld.isAfter(to));
    }

    private static LocalDate toLocalDate(Date d) {
        if (d instanceof java.sql.Date) {
            return ((java.sql.Date) d).toLocalDate();
        } else {
            return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    /** LocalTime → liczba godzin, np. 02:30 ⇒ 2.5 */
    private static double hours(LocalTime t) {
        return t.getHour() + t.getMinute() / 60d;
    }


    /**
     * Wyświetla panel zgłoszeń technicznych.
     */
    public void showIssuesPanel() {
        if (issuesPanelView == null) {
            showLoadingIndicator();
            Task<VBox> task = new Task<>() {
                @Override
                protected VBox call() {
                    return createIssuesPanelView();
                }
            };
            task.setOnSucceeded(e -> {
                issuesPanelView = task.getValue();
                // Na FX-thread następuje ustawienie widoku, a następnie odświeżenie danych
                Platform.runLater(() -> {
                    adminPanel.setCenterPane(issuesPanelView);
                    @SuppressWarnings("unchecked")
                    TableView<TechnicalIssue> tbl =
                            (TableView<TechnicalIssue>) issuesPanelView.lookup("#issuesTableView");
                    refreshIssuesTable(tbl);
                });
            });
            task.setOnFailed(e -> {
                task.getException().printStackTrace();
                Platform.runLater(() ->
                        showAlert(Alert.AlertType.ERROR, "Błąd",
                                "Nie udało się załadować panelu zgłoszeń")
                );
            });
            executor.execute(task);
        } else {
            Platform.runLater(() -> {
                adminPanel.setCenterPane(issuesPanelView);
                @SuppressWarnings("unchecked")
                TableView<TechnicalIssue> tbl =
                        (TableView<TechnicalIssue>) issuesPanelView.lookup("#issuesTableView");
                refreshIssuesTable(tbl);
            });
        }
    }

    /**
     * Pokazuje dialog z filtrami dla wskazanego raportu i okresu.
     * Po wybraniu filtrów wywołuje odpowiednią metodę generującą PDF.
     *
     * @param reportType  typ raportu: "KPI", "Zadania" lub "Obciążenie"
     * @param from        początek okresu (inclusive)
     * @param to          koniec okresu (inclusive)
     */
    private void showFilterDialogForReport(String reportType,
                                           LocalDate from,
                                           LocalDate to) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Filtruj raport: " + reportType);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK,
                ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        switch (reportType) {
            case "KPI" -> {
                // --- KPI: daty, stanowiska, priorytety ---
                DatePicker dpFrom = new DatePicker(from);
                DatePicker dpTo   = new DatePicker(to);
                ListView<String> positionsList = new ListView<>(
                        FXCollections.observableArrayList("Kasjer", "Logistyk", "Pracownik")
                );
                positionsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                ListView<StatsRaportGenerator.Priority> prioList = new ListView<>(
                        FXCollections.observableArrayList(StatsRaportGenerator.Priority.values())
                );
                prioList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

                grid.addRow(0, new Label("Data od:"), dpFrom, new Label("Data do:"), dpTo);
                grid.addRow(1, new Label("Stanowiska:"), positionsList);
                grid.addRow(2, new Label("Priorytety:"), prioList);

                dialog.getDialogPane().setContent(grid);
                dialog.setResultConverter(bt -> {
                    if (bt == ButtonType.OK) {
                        generateStatsPDF(
                                dpFrom.getValue(),
                                dpTo.getValue(),
                                new ArrayList<>(positionsList.getSelectionModel().getSelectedItems()),
                                new ArrayList<>(prioList.getSelectionModel().getSelectedItems())
                        );
                    }
                    return null;
                });
            }
            case "Zadania" -> {
                // --- Zadania: okres + statusy ---
                ComboBox<PeriodType> periodCombo = new ComboBox<>();
                periodCombo.getItems().addAll(PeriodType.values());
                // albo po prostu PeriodType.DAILY
                periodCombo.setValue(PeriodType.fromDisplay(reportType));

                ListView<String> statusList = new ListView<>(
                        FXCollections.observableArrayList("Zakończone", "W trakcie", "Opóźnione")
                );
                statusList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

                grid.addRow(0, new Label("Okres:"), periodCombo);
                grid.addRow(1, new Label("Statusy:"), statusList);

                dialog.getDialogPane().setContent(grid);
                dialog.setResultConverter(bt -> {
                    if (bt == ButtonType.OK) {
                        generateTaskPDF(
                                periodCombo.getValue(),
                                new ArrayList<>(statusList.getSelectionModel().getSelectedItems())
                        );
                    }
                    return null;
                });
            }
            case "Obciążenie" -> {
                // --- Obciążenie: daty, stanowiska, rodzaje obciążenia ---
                DatePicker dpFrom = new DatePicker(from);
                DatePicker dpTo   = new DatePicker(to);
                ListView<String> positionsList = new ListView<>(
                        FXCollections.observableArrayList("Kasjer", "Logistyk", "Pracownik")
                );
                positionsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                ListView<String> loadStatusList = new ListView<>(
                        FXCollections.observableArrayList("Przeciążenie", "Niedociążenie", "Optymalne")
                );
                loadStatusList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

                grid.addRow(0, new Label("Data od:"), dpFrom, new Label("Data do:"), dpTo);
                grid.addRow(1, new Label("Stanowiska:"), positionsList);
                grid.addRow(2, new Label("Statusy obciążenia:"), loadStatusList);

                dialog.getDialogPane().setContent(grid);
                dialog.setResultConverter(bt -> {
                    if (bt == ButtonType.OK) {
                        generateWorkloadPDF(
                                dpFrom.getValue(),
                                dpTo.getValue(),
                                new ArrayList<>(positionsList.getSelectionModel().getSelectedItems()),
                                new ArrayList<>(loadStatusList.getSelectionModel().getSelectedItems())
                        );
                    }
                    return null;
                });
            }
            default -> {
                // w razie nieznanego typu
                showAlert(Alert.AlertType.ERROR,
                        "Nieznany typ raportu",
                        "ReportType = " + reportType);
                return;
            }
        }

        dialog.showAndWait();
    }

    /**
     * Tworzy widok panelu zgłoszeń.
     *
     * @return VBox zawierający kompletny widok
     */
    private VBox createIssuesPanelView() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label title = new Label("Lista zgłoszeń technicznych");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<TechnicalIssue> tbl = new TableView<TechnicalIssue>();
        tbl.setId("issuesTableView");
        tbl.setMinHeight(200);

        // kolumny
        TableColumn<TechnicalIssue, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<TechnicalIssue, String> typeCol = new TableColumn<>("Typ");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<TechnicalIssue, LocalDate> dateCol =
                new TableColumn<>("Data zgłoszenia");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateSubmitted"));

        TableColumn<TechnicalIssue, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<String> cb = new ComboBox<>(
                    FXCollections.observableArrayList("Nowe", "W trakcie", "Rozwiązane")
            );
            {
                cb.setOnAction(e -> {
                    TechnicalIssue issue = getTableRow().getItem();
                    if (issue != null) {
                        issue.setStatus(cb.getValue());
                        executor.execute(new Task<>() {
                            @Override protected Void call() {
                                technicalIssueRepository.updateIssue(issue);
                                return null;
                            }
                        });
                    }
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    cb.setValue(getTableRow().getItem().getStatus());
                    setGraphic(cb);
                }
            }
        });

        tbl.getColumns().addAll(idCol, typeCol, dateCol, statusCol);

        // --- przyciski poniżej tabeli ---
        Button details = new Button("Pokaż szczegóły");
        styleAdminButton(details,"#2980B9");
        details.setOnAction(e -> showIssueDetails(tbl));

        Button refresh = new Button("Odśwież listę");
        styleAdminButton(refresh,"#3498DB");
        refresh.setOnAction(e -> refreshIssuesTable(tbl));

        HBox btnBox = new HBox(10, details, refresh);
        btnBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(title, tbl, btnBox);
        return layout;
    }

    /**
     * Odświeża listę zgłoszeń technicznych.
     */
    private void refreshIssuesTable(TableView<TechnicalIssue> tbl) {
        Task<List<TechnicalIssue>> task = new Task<>() {
            @Override protected List<TechnicalIssue> call() {
                return technicalIssueRepository.getAllIssues();
            }
        };
        task.setOnSucceeded(e -> {
            tbl.getItems().setAll(task.getValue());
        });
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            Platform.runLater(() ->
                    showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się pobrać zgłoszeń")
            );
        });
        executor.execute(task);
    }

    /**
     * Wyświetla szczegóły wybranego zgłoszenia.
     */
    private void showIssueDetails(TableView<TechnicalIssue> tbl) {
        TechnicalIssue sel = tbl.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Brak wyboru", "Wybierz zgłoszenie!");
            return;
        }
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Szczegóły zgłoszenia");
        a.setHeaderText("Zgłoszenie ID: " + sel.getId());
        a.setContentText(
                "Typ: " + sel.getType() + "\n" +
                        "Opis: " + sel.getDescription() + "\n" +
                        "Data: " + sel.getDateSubmitted() + "\n" +
                        "Pracownik ID: " + sel.getEmployee().getId() + "\n" +
                        "Status: " + sel.getStatus()
        );
        a.showAndWait();
    }

    /**
     * Wylogowuje użytkownika i uruchamia okno logowania.
     */
    public void logout() {
        // Zamknij połączenia z bazą danych
        Task<Void> closeTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                return null;
            }
        };

        closeTask.setOnSucceeded(e -> {
            primaryStage.close();
            Stage loginStage = new Stage();
            try {
                new HelloApplication().start(loginStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        executor.execute(closeTask);
    }

    /**
     * Wykonuje backup bazy danych MySQL do pliku .sql.
     * Obsługuje zarówno XAMPP, jak i standardową instalację
     * MySQL na Windows, Linux i Mac OS.
     */
    private void performDatabaseBackup() {
        Stage loaderStage = new Stage();
        loaderStage.initOwner(primaryStage);
        loaderStage.initModality(Modality.APPLICATION_MODAL);
        loaderStage.initStyle(StageStyle.UNDECORATED);

        VBox box = new VBox(10, new ProgressIndicator());
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        loaderStage.setScene(new Scene(box));
        loaderStage.setTitle("Backup bazy – trwa…");
        loaderStage.show();

        String os = System.getProperty("os.name").toLowerCase();
        String mysqldumpPath;
        try {
            if (os.contains("win")) {
                File programFiles = new File("C:\\Program Files\\MySQL");
                File found = searchForMysqlDump(programFiles, "mysqldump.exe");
                if (found != null && found.exists()) {
                    mysqldumpPath = found.getAbsolutePath();
                } else {
                    File xampp = new File("C:\\xampp\\mysql\\bin\\mysqldump.exe");
                    if (xampp.exists()) {
                        mysqldumpPath = xampp.getAbsolutePath();
                    } else {
                        loaderStage.close();
                        showAlert(Alert.AlertType.ERROR, "Nie znaleziono mysqldump.exe",
                                "Nie znaleziono mysqldump ani w Program Files, ani w XAMPP.");
                        return;
                    }
                }
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                File[] paths = {
                        new File("/usr/bin/mysqldump"),
                        new File("/usr/local/bin/mysqldump"),
                        new File("/usr/local/mysql/bin/mysqldump"),
                        new File("/opt/mysql/bin/mysqldump")
                };
                File found = null;
                for (File p : paths) if (p.exists()) { found = p; break; }
                mysqldumpPath = (found != null) ? found.getAbsolutePath() : "mysqldump";
            } else {
                loaderStage.close();
                showAlert(Alert.AlertType.ERROR, "Nieobsługiwany system",
                        "System „" + os + "” nie jest obsługiwany.");
                return;
            }
        } catch (Exception ex) {
            loaderStage.close();
            logger.error("Błąd wyszukiwania mysqldump", ex);
            showAlert(Alert.AlertType.ERROR, "Błąd", ex.getMessage());
            return;
        }

        Task<Path> task = new Task<>() {
            @Override
            protected Path call() throws Exception {

                String ts   = java.time.LocalDateTime.now().toString().replace(":", "-");
                String name = "stonkadb-backup-" + ts + ".sql";

                File backupDir = new File("backups");
                if (!backupDir.exists()) backupDir.mkdirs();

                File out = new File(backupDir, name);

                ProcessBuilder pb = new ProcessBuilder(
                        mysqldumpPath,
                        "-u", org.example.database.ILacz.MYSQL_USER,
                        "--password=" + org.example.database.ILacz.MYSQL_PASSWORD,
                        "--routines",
                        "--events",
                        "--triggers",
                        "--single-transaction",
                        "--quick",
                        "--skip-lock-tables",
                        "--add-drop-database",
                        "--add-drop-table",
                        "--complete-insert",
                        "--databases", org.example.database.ILacz.DB_NAME
                );

                String pwd = org.example.database.ILacz.MYSQL_PASSWORD;
                if (pwd != null && !pwd.isEmpty()) pb.environment().put("MYSQL_PWD", pwd);

                pb.redirectOutput(out);
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);

                Process proc = pb.start();
                if (proc.waitFor() != 0)
                    throw new IOException("mysqldump zakończył się nie-zerowym kodem.");

                return out.toPath();
            }
        };

        task.setOnSucceeded(ev -> Platform.runLater(() -> {
            loaderStage.close();
            Path p = task.getValue();
            showAlert(Alert.AlertType.INFORMATION, "Backup zakończony",
                    "Plik zapisano w:\n" + p.toAbsolutePath());
        }));

        task.setOnFailed(ev -> Platform.runLater(() -> {
            loaderStage.close();
            Throwable ex = task.getException();
            logger.error("Błąd backupu", ex);
            showAlert(Alert.AlertType.ERROR, "Błąd backupu",
                    (ex != null) ? ex.getMessage() : "Nieznany błąd");
        }));

        new Thread(task, "BackupTask").start();
    }

    /**
     * Przeszukuje rekurencyjnie podany katalog w poszukiwaniu pliku
     * o nazwie targetName.
     * Zatrzymuje się po znalezieniu pierwszego pasującego pliku.
     *
     * @param dir        Katalog początkowy, od którego rozpoczyna się
     *                   przeszukiwanie
     * @param targetName Nazwa szukanego pliku (np. "mysqldump.exe"),
     *                   bez względu na wielkość liter
     */
    private File searchForMysqlDump(File dir, String targetName) {
        File[] files = dir.listFiles();
        if (files == null) return null;

        for (File file : files) {
            try {
                if (file.isDirectory() && !file.isHidden()) {
                    File result = searchForMysqlDump(file, targetName);
                    if (result != null) return result;
                } else if (file.getName().equalsIgnoreCase(targetName)) {
                    return file;
                }
            } catch (SecurityException ignored) {
            }
        }
        return null;
    }

    /**
     * Wyświetla komunikat w okienku dialogowym.
     *
     * @param type   typ alertu
     * @param title  tytuł okna
     * @param header treść nagłówka
     */
    private void showAlert(Alert.AlertType type, String title, String header) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(null);
            alert.showAndWait();
        });
    }

    /**
     * Otwiera okno dodawania nowego adresu z walidacją pól.
     *
     * @param addressComboBox ComboBox, który należy odświeżyć
     *                        po dodaniu nowego adresu
     */
    private void openNewAddressWindow(ComboBox<Address> addressComboBox) {
        Stage stage = new Stage();
        stage.setTitle("Dodaj nowy adres");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        // --------------- Kontrolki formularza ---------------
        TextField town = new TextField();
        town.setPromptText("Miejscowość");

        TextField houseNumber = new TextField();
        houseNumber.setPromptText("Numer domu (np. 12, 12A, 12B/3)");

        TextField apartmentNumber = new TextField();
        apartmentNumber.setPromptText("Numer mieszkania (opcjonalnie)");

        TextField zipCode = new TextField();
        zipCode.setPromptText("Kod pocztowy (np. 00-123)");

        TextField city = new TextField();
        city.setPromptText("Miasto");

        Button saveButton = new Button("Zapisz adres");
        styleAdminButton(saveButton, "#27AE60");

        // --------------- Obsługa przycisku Zapisz ---------------
        saveButton.setOnAction(e -> {
            String sTown = town.getText().trim();
            String sHouse = houseNumber.getText().trim();
            String sApartment = apartmentNumber.getText().trim();
            String sZip = zipCode.getText().trim();
            String sCity = city.getText().trim();

            // 1. Sprawdzenie, czy wymagane pola nie są puste
            if (sTown.isEmpty() || sHouse.isEmpty() || sZip.isEmpty() || sCity.isEmpty()) {
                showAlert(Alert.AlertType.ERROR,
                        "Błąd",
                        "Wszystkie pola (poza numerem mieszkania) muszą być wypełnione.");
                return;
            }

            // 2. Walidacja formatu Miejscowości i Miasta: tylko litery i spacje
            //    (bez cyfr i znaków specjalnych)
            if (!sTown.matches("[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż\\s\\-]+")) {
                showAlert(Alert.AlertType.ERROR,
                        "Błąd",
                        "Miejscowość może zawierać tylko litery, spacje i myślniki.");
                return;
            }
            if (!sCity.matches("[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż\\s\\-]+")) {
                showAlert(Alert.AlertType.ERROR,
                        "Błąd",
                        "Miasto może zawierać tylko litery, spacje i myślniki.");
                return;
            }

            // 3. Walidacja formatu Numeru domu:
            //    dopuszczamy cyfry, ewentualnie litery (np. "12", "12A") lub
            //    fragment typu "12B" (bez dalszych znaków), maksymalnie dwie litery.
            if (!sHouse.matches("\\d{1,4}[A-Za-z]{0,2}")) {
                showAlert(Alert.AlertType.ERROR,
                        "Błąd",
                        "Numer domu jest nieprawidłowy. Może składać się z cyfr" +
                                " i maksymalnie dwóch liter, np. 12A.");
                return;
            }

            // 4. Walidacja formatu Numeru mieszkania (jeśli podano):
            //    dopuszczamy albo puste, albo tylko cyfry, np. "3" lub "12".
            if (!sApartment.isEmpty() && !sApartment.matches("\\d{1,4}")) {
                showAlert(Alert.AlertType.ERROR,
                        "Błąd",
                        "Numer mieszkania może zawierać tylko cyfry (np. 3).");
                return;
            }

            // 5. Walidacja formatu kodu pocztowego: wzorzec "dd-ddd"
            if (!sZip.matches("\\d{2}-\\d{3}")) {
                showAlert(Alert.AlertType.ERROR,
                        "Błąd",
                        "Nieprawidłowy format kodu pocztowego. Poprawny przykład: 00-001.");
                return;
            }

            // 6. Po przejściu wszystkich walidacji, zapisujemy nowy adres
            AddressRepository repo = new AddressRepository();
            Address newAddress = new Address();
            newAddress.setTown(sTown);
            newAddress.setHouseNumber(sHouse);
            // numer mieszkania ustawiamy tylko jeśli zostało podane
            newAddress.setApartmentNumber(sApartment.isEmpty() ? null : sApartment);
            newAddress.setZipCode(sZip);
            newAddress.setCity(sCity);

            try {
                repo.addAddress(newAddress);
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR,
                        "Błąd zapisu",
                        "Wystąpił problem podczas zapisywania adresu:\n" + ex.getMessage());
                return;
            }

            // 7. Odświeżamy ComboBox z adresami i ustawiamy nowo dodany
            // adres jako wybrany
            List<Address> updatedList = repo.getAllAddresses();
            addressComboBox.getItems().clear();
            addressComboBox.getItems().addAll(updatedList);
            addressComboBox.setValue(newAddress);

            stage.close();
        });

        // Dodajemy etykietę nad polami i układamy wszystkie kontrolki
        // w jednym VBox-ie
        layout.getChildren().addAll(
                new Label("Nowy adres:"),
                town,
                houseNumber,
                apartmentNumber,
                zipCode,
                city,
                saveButton
        );

        stage.setScene(new javafx.scene.Scene(layout));
        stage.show();
    }

    /**
     * Wspólny styl dla przycisków w panelu admina.
     */
    private void styleAdminButton(Button button, String color) {
        button.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;"
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
     * Otwiera eksplorator plików (Windows Explorer lub xdg-open)
     * ustawiony na katalogu projektu.
     */
    private void openProjectDirectory() {
        try {
            String projectDir = System.getProperty("user.dir");
            File dir = new File(projectDir);
            if (!dir.exists()) {
                // W razie czego spróbuj aktualny katalog
                dir = new File(".");
            }

            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;
            if (os.contains("win")) {
                // Windows Explorer
                pb = new ProcessBuilder("explorer", dir.getAbsolutePath());
            } else {
                // Linux / Unix / macOS (zakładamy xdg-open)
                pb = new ProcessBuilder("xdg-open", dir.getAbsolutePath());
            }
            pb.inheritIO();
            pb.start();
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd",
                    "Nie udało się otworzyć katalogu projektu:\n" + ex.getMessage());
        }
    }

}
