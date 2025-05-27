/*
 * Classname: AdminPanelController
 * Version information: 1.7
 * Date: 2025-05-27
 * Copyright notice: © BŁĘKITNI
 */


package org.example.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;

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
import javafx.stage.Stage;
import org.example.database.EmpTaskRepository;
import org.example.database.TechnicalIssueRepository;
import org.example.database.UserRepository;
import org.example.pdflib.ConfigManager;
import org.example.sys.Employee;
import org.example.sys.TechnicalIssue;
import org.example.sys.PeriodType;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Map;

import org.example.database.AddressRepository;
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
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

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
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się załadować panelu użytkowników");
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
        tableView.setPrefHeight(rowsToShow * tableView.getFixedCellSize() + 30); // +30 na nagłówek
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

                showLoadingIndicator();

                selected.setName(nameField.getText());
                selected.setSurname(surnameField.getText());
                selected.setLogin(loginField.getText());
                selected.setEmail(emailField.getText());
                selected.setAddress(addressComboBox.getValue());
                if (!passwordField.getText().isEmpty()) {
                    selected.setPassword(passwordField.getText());
                }
                selected.setPosition(positionBox.getValue());
                selected.setAge(Integer.parseInt(ageField.getText()));
                selected.setSalary(new BigDecimal(salaryField.getText()));

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
                // sprawdzenie czy nie ma pustych pól
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

                int age = Integer.parseInt(ageField.getText());
                BigDecimal salary = new BigDecimal(salaryField.getText());

                Employee newEmployee = new Employee();
                newEmployee.setName(nameField.getText());
                newEmployee.setSurname(surnameField.getText());
                newEmployee.setLogin(loginField.getText());
                newEmployee.setPassword(passwordField.getText());
                newEmployee.setEmail(emailField.getText());
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
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nieprawidłowy format wieku lub zarobków!");
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
     * Tworzenie węzłów odbywa się na wątku JavaFX – brak konfliktów z toolkitem.
     */
    public void showConfigPanel() {
        if (configPanelView == null) {
            // bez asynchronicznego Task – budowa UI jest lekka
            configPanelView = createConfigPanelView();
        }
        adminPanel.setCenterPane(configPanelView);
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

        // bezpieczne pobieranie wartości z ConfigManager
        boolean loggingEnabled;
        try {
            loggingEnabled       = ConfigManager.isLoggingEnabled();
        } catch (Exception ex) {
            // gdyby plik properties był uszkodzony
            loggingEnabled = false;
        }

        CheckBox logsCheckbox = new CheckBox("Włącz logi systemowe");
        logsCheckbox.setSelected(loggingEnabled);

        Button configurePDF = new Button("Konfiguruj raporty PDF");
        styleAdminButton(configurePDF,"#2980B9");
        configurePDF.setOnAction(e -> showPDFConfigPanel());

        Button backupButton = new Button("Wykonaj backup bazy danych");
        styleAdminButton(backupButton,"#27AE60");
        backupButton.setOnAction(e -> performDatabaseBackup());

        Button saveButton = new Button("Zapisz");
        styleAdminButton(saveButton,"#3498DB");
        saveButton.setOnAction(e -> {
            ConfigManager.setLoggingEnabled(logsCheckbox.isSelected());
            showAlert(Alert.AlertType.INFORMATION, "Zapisano",
                    "Ustawienia zostały zachowane.");
        });

        layout.getChildren().addAll(
                titleLabel,
                logsCheckbox,
                configurePDF,
                backupButton,
                saveButton
        );

        adminPanel.setCenterPane(layout);

        return layout;
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
        styleAdminButton(updateLogoButton,"#2980B9");

        Label sortingLabel = new Label("Sortowanie domyślne:");
        ComboBox<String> sortingComboBox = new ComboBox<>();
        sortingComboBox.getItems().addAll("Nazwa", "Data", "Priorytet");

        Label pathLabel = new Label("Ścieżka zapisu raportów:");
        TextField pathField = new TextField();
        pathField.setPromptText("Np. C:/raporty/");
        pathField.setText(ConfigManager.getReportPath());

        Button saveButton = new Button("Zapisz konfigurację");
        styleAdminButton(saveButton,"#3498DB");

        saveButton.setOnAction(e -> {
            String path = pathField.getText().trim();

            if (path.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Błąd", "Ścieżka nie może być pusta.");
                return;
            }

            File folder = new File(path);
            if (!folder.exists() || !folder.isDirectory()) {
                showAlert(Alert.AlertType.ERROR, "Niepoprawna ścieżka", "Podany folder nie istnieje.");
                return;
            }

            ConfigManager.setReportPath(path);
            showAlert(Alert.AlertType.INFORMATION, "Zapisano", "Ścieżka została zapisana.");
        });

        Button backButton = new Button("Wróć");
        styleAdminButton(backButton,"#E74C3C");
        backButton.setOnAction(e -> showConfigPanel());

        layout.getChildren().addAll(
                titleLabel,
                logoLabel, logoField,
                updateLogoButton,
                sortingLabel, sortingComboBox,
                pathLabel, pathField,
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
     * Otwiera okno dialogowe z filtrami do generowania raportu KPI (StatsRaportGenerator).
     * Umożliwia wybór zakresu dat, stanowisk oraz priorytetów.
     */
    private void showStatsReportDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Raport KPI – filtry");

        // Zakres dat
        DatePicker start = new DatePicker();
        DatePicker end   = new DatePicker();

        // Lista stanowisk – multi‑select
        ListView<String> positionsList = new ListView<>();
        positionsList.setPrefSize(200, 100);
        positionsList.getItems().addAll("Kasjer", "Logistyk", "Pracownik");
        positionsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Lista priorytetów
        ListView<StatsRaportGenerator.Priority> prioList = new ListView<>();
        prioList.setPrefSize(200, 100);
        prioList.getItems().addAll(StatsRaportGenerator.Priority.values());
        prioList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.addRow(0, new Label("Data od:"), start, new Label("Data do:"), end);
        grid.addRow(1, new Label("Stanowiska:"), positionsList);
        grid.addRow(2, new Label("Priorytety:"), prioList);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                generateStatsPDF(start.getValue(), end.getValue(),
                        new ArrayList<>(positionsList.getSelectionModel().getSelectedItems()),
                        new ArrayList<>(prioList.getSelectionModel().getSelectedItems()));
            }
            return null;
        });
        dialog.showAndWait();
    }

    /**
     * Generuje PDF z KPI (StatsRaportGenerator) na podstawie wybranych filtrów.
     *
     * @param from       początek okresu (inclusive)
     * @param to         koniec okresu (inclusive)
     * @param positions  lista nazw stanowisk, które mają się znaleźć w raporcie
     * @param priors     lista priorytetów (HIGH/MEDIUM/LOW), do filtrowania zadań
     */
    private void generateStatsPDF(LocalDate from, LocalDate to,
                                  List<String> positions,
                                  List<StatsRaportGenerator.Priority> priors) {
        try {
            StatsRaportGenerator gen = new StatsRaportGenerator();
            gen.setTaskData(fetchTaskStatsData(from, to));
            String out = ConfigManager.getReportPath() + "/stats-" + System.currentTimeMillis() + ".pdf";
            gen.generateReport(out, to, StatsRaportGenerator.PeriodType.DAILY, positions, priors);
            showAlert(Alert.AlertType.INFORMATION, "Raport wygenerowany", out);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Błąd", ex.getMessage());
        }
    }

    /**
     * Otwiera okno dialogowe z filtrami do generowania raportu zadań (TaskRaportGenerator).
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
     * @param period    domenowy typ okresu (enum PeriodType) – będzie zmapowany na wewnętrzny TaskRaportGenerator.PeriodType
     * @param statuses  lista statusów zadań do uwzględnienia w raporcie
     */
    private void generateTaskPDF(PeriodType period, List<String> statuses) {
        try {
            TaskRaportGenerator gen = new TaskRaportGenerator();

            // 1) przygotowanie danych
            gen.setTaskData(fetchTaskSimpleData(period));

            // 2) mapowanie enum na ten wymagany przez TaskRaportGenerator
            TaskRaportGenerator.PeriodType pdfPeriod = switch (period) {
                case DAILY   -> TaskRaportGenerator.PeriodType.LAST_WEEK;
                case MONTHLY -> TaskRaportGenerator.PeriodType.LAST_MONTH;
                case YEARLY  -> TaskRaportGenerator.PeriodType.LAST_QUARTER;
            };

            // 3) generowanie raportu
            String out = ConfigManager.getReportPath()
                    + "/tasks-" + System.currentTimeMillis() + ".pdf";
            gen.generateReport(out, pdfPeriod, statuses);

            showAlert(Alert.AlertType.INFORMATION,
                    "Raport wygenerowany",
                    out);

        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR,
                    "Błąd",
                    ex.getMessage());
        }
    }

    /**
     * Otwiera okno dialogowe z filtrami do generowania raportu obciążenia (WorkloadReportGenerator).
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
     * @param statuses   lista statusów obciążenia ("Przeciążenie", "Niedociążenie", "Optymalne")
     */
    private void generateWorkloadPDF(LocalDate from, LocalDate to,
                                     List<String> positions,
                                     List<String> statuses) {
        try {
            WorkloadReportGenerator gen = new WorkloadReportGenerator();
            gen.setWorkloadData(fetchWorkloadData(from, to));
            String out = ConfigManager.getReportPath() + "/workload-" + System.currentTimeMillis() + ".pdf";
            gen.generateReport(out, from, to, positions, statuses);
            showAlert(Alert.AlertType.INFORMATION, "Raport wygenerowany", out);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Błąd", ex.getMessage());
        }
    }

    /* ------------------------------------------------------------ */
    /* KPI – StatsRaportGenerator                                   */
    /* ------------------------------------------------------------ */
    private List<StatsRaportGenerator.TaskRecord> fetchTaskStatsData(LocalDate from,
                                                                     LocalDate to) {

        EmpTaskRepository repo = new EmpTaskRepository();

        return repo.getAllTasks().stream()
                .filter(t -> inRange(t.getDate(), from, to))
                .map(t -> {
                    Employee assignee = t.getSingleAssignee();
                    return new StatsRaportGenerator.TaskRecord(
                            t.getName(),                                        // taskName
                            assignee != null ? assignee.getPosition() : "Brak", // position
                            null,                                               // priority – nieużywane
                            toLocalDate(t.getDate()),                           // dueDate
                            null,                                               // completionDate – brak
                            assignee != null ? assignee.getLogin() : "Brak"     // assignee
                    );
                })
                .toList();
    }

    /* ------------------------------------------------------------ */
    /* Raport zadań – TaskRaportGenerator                           */
    /* ------------------------------------------------------------ */
    private List<TaskRaportGenerator.TaskRecord> fetchTaskSimpleData(LocalDate from,
                                                                     LocalDate to) {
        EmpTaskRepository repo = new EmpTaskRepository();

        return repo.getAllTasks().stream()
                .filter(t -> inRange(t.getDate(), from, to))
                .map(t -> {
                    Employee assignee = t.getSingleAssignee();          // może być null
                    return new TaskRaportGenerator.TaskRecord(
                            t.getName(),                                    // taskName
                            toLocalDate(t.getDate()),                       // dueDate
                            null,                                           // completionDate – nieużywane
                            assignee != null ? assignee.getLogin() : "Brak" // assignee
                    );
                })
                .toList();
    }

    /* ─────────  (wrapper na enum)  ───────── */
    private List<TaskRaportGenerator.TaskRecord> fetchTaskSimpleData(PeriodType period) {
        LocalDate end   = LocalDate.now();
        LocalDate start;
        switch (period) {
            case DAILY   -> start = end;
            case MONTHLY -> start = end.with(TemporalAdjusters.firstDayOfMonth());
            case YEARLY  -> start = end.minusYears(1);
            default      -> throw new IllegalArgumentException("Nieznany okres: " + period);
        }
        return fetchTaskSimpleData(start, end);                     // wywołanie wersji bazowej
    }

    /* ------------------------------------------------------------ */
    /* Obciążenie – WorkloadReportGenerator                         */
    /* ------------------------------------------------------------ */
    private List<WorkloadReportGenerator.EmployeeWorkload> fetchWorkloadData(LocalDate from,
                                                                             LocalDate to) {

        EmpTaskRepository repo = new EmpTaskRepository();

        return repo.getAllTasks().stream()
                // 1) tylko taski z co najmniej jednym pracownikiem, czasem zmiany i w zakresie dat
                .filter(t -> !t.getTaskEmployees().isEmpty()
                        && t.getDurationOfTheShift() != null
                        && inRange(t.getDate(), from, to))
                // 2) grupowanie po pierwszym assignee i sumowanie godzin
                .collect(Collectors.groupingBy(
                        EmpTask::getSingleAssignee,
                        Collectors.summingDouble(t -> hours(t.getDurationOfTheShift()))
                ))
                // 3) konwertowanie na rekord raportu
                .entrySet().stream()
                .map(e -> new WorkloadReportGenerator.EmployeeWorkload(
                        e.getKey().getLogin(),       // employeeName
                        e.getKey().getPosition(),    // department
                        e.getValue()                 // totalHours
                ))
                .toList();
    }

    // =====================  METODY POMOCNICZE  ====================
    private static boolean inRange(Date d, LocalDate from, LocalDate to) {
        if (d == null) return false;
        LocalDate ld = toLocalDate(d);
        return (from == null || !ld.isBefore(from))
                && (to   == null || !ld.isAfter(to));
    }

    private static LocalDate toLocalDate(Date d) {
        return d.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
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
                        showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się załadować panelu zgłoszeń")
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
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

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
                periodCombo.setValue(PeriodType.fromDisplay(reportType)); // albo po prostu PeriodType.DAILY

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

        TableColumn<TechnicalIssue, LocalDate> dateCol = new TableColumn<>("Data zgłoszenia");
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
                technicalIssueRepository.close();
                userRepository.close();
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
     * Obsługuje zarówno XAMPP, jak i standardową instalację MySQL na Windows, Linux i Mac OS.
     */
    private void performDatabaseBackup() {
        try {
            String timestamp = java.time.LocalDateTime.now().toString().replace(":", "-");
            String fileName = "stonkadb-backup-" + timestamp + ".sql";

            File backupDir = new File("backups");
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            File outputFile = new File(backupDir, fileName);

            // Wykrywanie systemu operacyjnego
            String os = System.getProperty("os.name").toLowerCase();
            String mysqldumpPath;

            if (os.contains("win")) {
                // Ścieżka dla Windows
                File programFiles = new File("C:\\Program Files\\MySQL");
                File found = searchForMysqlDump(programFiles, "mysqldump.exe");

                if (found != null && found.exists()) {
                    mysqldumpPath = found.getAbsolutePath();
                } else {
                    // Próba z XAMPP
                    File xamppPath = new File("C:\\xampp\\mysql\\bin\\mysqldump.exe");
                    if (xamppPath.exists()) {
                        mysqldumpPath = xamppPath.getAbsolutePath();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Nie znaleziono mysqldump.exe",
                                "Nie znaleziono mysqldump ani w C:\\Program Files\\MySQL, ani w C:\\xampp.");
                        return;
                    }
                }
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                // Ścieżka dla Linux/Unix/Mac
                File[] possiblePaths = {
                        new File("/usr/bin/mysqldump"),
                        new File("/usr/local/bin/mysqldump"),
                        new File("/usr/local/mysql/bin/mysqldump"),
                        new File("/opt/mysql/bin/mysqldump")
                };

                File foundPath = null;
                for (File path : possiblePaths) {
                    if (path.exists()) {
                        foundPath = path;
                        break;
                    }
                }

                if (foundPath != null) {
                    mysqldumpPath = foundPath.getAbsolutePath();
                } else {
                    mysqldumpPath = "mysqldump";
                }
            } else {
                throw new UnsupportedOperationException("Nieobsługiwany system operacyjny: " + os);
            }

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

            String password = org.example.database.ILacz.MYSQL_PASSWORD;
            if (password != null && !password.isEmpty()) {
                Map<String, String> env = pb.environment();
                env.put("MYSQL_PWD", password);
            }

            pb.redirectOutput(outputFile);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                showAlert(Alert.AlertType.INFORMATION, "Backup zakończony",
                        "Plik zapisany:\n" + outputFile.getAbsolutePath());
            } else {
                showAlert(Alert.AlertType.ERROR, "Błąd backupu",
                        "Nie udało się wykonać kopii zapasowej. Kod wyjścia: " + exitCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Wyjątek",
                    "Wystąpił błąd podczas backupu:\n" + e.getMessage());
        }
    }

    /**
     * Przeszukuje rekurencyjnie podany katalog w poszukiwaniu pliku o nazwie targetName.
     * Zatrzymuje się po znalezieniu pierwszego pasującego pliku.
     *
     * @param dir        Katalog początkowy, od którego rozpoczyna się przeszukiwanie
     * @param targetName Nazwa szukanego pliku (np. "mysqldump.exe"), bez względu na wielkość liter
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

    private void openNewAddressWindow(ComboBox<Address> addressComboBox) {
        Stage stage = new Stage();
        stage.setTitle("Dodaj nowy adres");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField town = new TextField();
        town.setPromptText("Miejscowość");

        TextField houseNumber = new TextField();
        houseNumber.setPromptText("Numer domu");

        TextField apartmentNumber = new TextField();
        apartmentNumber.setPromptText("Numer mieszkania (opcjonalnie)");

        TextField zipCode = new TextField();
        zipCode.setPromptText("Kod pocztowy");

        TextField city = new TextField();
        city.setPromptText("Miasto");

        Button saveButton = new Button("Zapisz adres");

        saveButton.setOnAction(e -> {
            // WALIDACJA
            if (town.getText().isEmpty()
                    || houseNumber.getText().isEmpty()
                    || zipCode.getText().isEmpty()
                    || city.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Wszystkie pola (poza numerem mieszkania) muszą być wypełnione.");
                return;
            }

            if (!zipCode.getText().matches("\\d{2}-\\d{3}")) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nieprawidłowy format kodu pocztowego. Poprawny to np. 00-001.");
                return;
            }

            // ZAPIS
            AddressRepository repo = new AddressRepository();
            Address newAddress = new Address();
            newAddress.setTown(town.getText());
            newAddress.setHouseNumber(houseNumber.getText());
            newAddress.setApartmentNumber(apartmentNumber.getText().isEmpty() ? null : apartmentNumber.getText());
            newAddress.setZipCode(zipCode.getText());
            newAddress.setCity(city.getText());

            repo.addAddress(newAddress);

            // Odśwież listę i wybierz newAddress adres
            addressComboBox.getItems().clear();
            addressComboBox.getItems().addAll(repo.getAllAddresses());
            addressComboBox.setValue(newAddress);

            stage.close();
        });

        layout.getChildren().addAll(
                new Label("Nowy adres:"),
                town, houseNumber, apartmentNumber,
                zipCode, city, saveButton
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
}
