/*
 * Classname: ManagerPanelController
 * Version information: 1.8
 * Date: 2025-06-03
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.stage.Modality;
import javafx.stage.Stage;

import org.example.database.AbsenceRequestRepository;
import org.example.database.EmpTaskRepository;
import org.example.database.TaskEmployeeRepository;
import org.example.database.UserRepository;

import org.example.sys.AbsenceRequest;
import org.example.sys.Employee;
import org.example.sys.EmpTask;
import org.example.sys.TaskEmployee;

import java.sql.Date;
import java.time.LocalTime;
import java.util.List;

/**
 * Kontroler logiki interfejsu użytkownika dla panelu kierownika.
 */
public class ManagerPanelController {

    private final ManagerPanel managerPanel;
    private final Stage primaryStage;
    private final UserRepository userRepository;
    private final EmpTaskRepository taskRepository;

    /**
     * Konstruktor kontrolera.
     *
     * @param managerPanel główny panel kierownika
     */
    public ManagerPanelController(ManagerPanel managerPanel) {
        this.managerPanel = managerPanel;
        this.primaryStage = managerPanel.getPrimaryStage();
        this.userRepository = new UserRepository();
        this.taskRepository = new EmpTaskRepository();
    }

    /**
     * Wyświetla panel z listą zadań.
     */
    public void showTaskPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label taskLabel = new Label("Lista zadań");
        taskLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<EmpTask> taskTable = new TableView<>();
        taskTable.setMinHeight(200);

        TableColumn<EmpTask, String> nameCol = new TableColumn<>("Zadanie");
        nameCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getName()
                )
        );

        TableColumn<EmpTask, String> dateCol = new TableColumn<>("Termin");
        dateCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDate() != null
                                ? data.getValue().getDate().toString()
                                : "brak daty"
                )
        );

        TableColumn<EmpTask, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getStatus()
                )
        );

        TableColumn<EmpTask, String> employeeCol = new TableColumn<>("Pracownik");
        employeeCol.setCellValueFactory(
                data -> {
                    EmpTask task = data.getValue();
                    if (task.getSingleAssignee() != null) {
                        Employee emp = task.getSingleAssignee();
                        return new javafx.beans.property.SimpleStringProperty(
                                emp.getName() + " " + emp.getSurname()
                        );
                    } else {
                        return new javafx.beans.property.SimpleStringProperty("Brak");
                    }
                }
        );

        taskTable.getColumns().addAll(nameCol, dateCol, statusCol, employeeCol);
        taskTable.getItems().addAll(taskRepository.getAllTasks());

        HBox taskButtons = new HBox(10);
        taskButtons.setAlignment(Pos.CENTER);

        Button addTaskButton = new Button("Dodaj zadanie");
        addTaskButton.setStyle("-fx-background-color: #2980B9; -fx-text-fill: white; -fx-font-weight: bold;");
        addTaskButton.setOnMouseEntered(e -> { addTaskButton.setScaleX(1.1); addTaskButton.setScaleY(1.1); });
        addTaskButton.setOnMouseExited(e -> { addTaskButton.setScaleX(1); addTaskButton.setScaleY(1); });
        addTaskButton.setOnAction(e -> showAddTaskPanel());

        Button assignEmployeeButton = new Button("Przypisz pracownika");
        assignEmployeeButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold;");
        assignEmployeeButton.setOnMouseEntered(e -> { assignEmployeeButton.setScaleX(1.1); assignEmployeeButton.setScaleY(1.1); });
        assignEmployeeButton.setOnMouseExited(e -> { assignEmployeeButton.setScaleX(1); assignEmployeeButton.setScaleY(1); });
        assignEmployeeButton.setOnAction(e -> showAssignEmployeeDialog());

        Button editButton = new Button("Edytuj zadanie");
        editButton.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-font-weight: bold;");
        editButton.setOnMouseEntered(e -> { editButton.setScaleX(1.1); editButton.setScaleY(1.1); });
        editButton.setOnMouseExited(e -> { editButton.setScaleX(1); editButton.setScaleY(1); });

        Button deleteButton = new Button("Archiwizuj zadanie");
        deleteButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setOnMouseEntered(e -> { deleteButton.setScaleX(1.1); deleteButton.setScaleY(1.1); });
        deleteButton.setOnMouseExited(e -> { deleteButton.setScaleX(1); deleteButton.setScaleY(1); });

        editButton.setOnAction(e -> {
            EmpTask selectedTask = taskTable.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                showEditTaskDialog(selectedTask);
            } else {
                showAlert(Alert.AlertType.WARNING, "Błąd", "Wybierz zadanie do edycji.");
            }
        });

        deleteButton.setOnAction(e -> {
            EmpTask selectedTask = taskTable.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                // Użycie miękkiego usuwania zamiast trwałego usunięcia
                boolean success = taskRepository.softDeleteTask(selectedTask);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukces", "Zadanie zostało zarchiwizowane.");
                    showTaskPanel(); // Odświeżenie panelu
                } else {
                    showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się zarchiwizować zadania.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Błąd", "Wybierz zadanie do archiwizacji.");
            }
        });

        taskButtons.getChildren().addAll(
                addTaskButton,
                assignEmployeeButton,
                editButton,
                deleteButton
        );

        layout.getChildren().addAll(
                taskLabel, taskTable, taskButtons
        );

        managerPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla panel dodawania nowego zadania.
     */
    /**
     * Wyświetla panel dodawania nowego zadania.
     */
    public void showAddTaskPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label nameLabel = new Label("Nazwa zadania");
        TextField nameField = new TextField();

        Label descLabel = new Label("Opis");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(4);

        Label statusLabel = new Label("Status");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Nowe", "W trakcie");

        Label dateLabel = new Label("Termin");
        DatePicker deadlinePicker = new DatePicker();

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button backButton = new Button("Wróć");
        backButton.setOnAction(e -> showTaskPanel());

        Button saveButton = new Button("Zapisz");
        saveButton.setStyle("-fx-background-color: #2980B9; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            try {
                String name             = nameField.getText();
                String description      = descriptionArea.getText();
                String status           = statusCombo.getValue();
                Date date               = Date.valueOf(deadlinePicker.getValue());
                LocalTime timeOfShift   = LocalTime.now();

                if (name.isEmpty() || description.isEmpty() || status == null || date == null) {
                    showAlert(Alert.AlertType.WARNING, "Błąd", "Wypełnij wszystkie pola.");
                    return;
                }

                if (date.before(Date.valueOf(java.time.LocalDate.now()))) {
                    showAlert(Alert.AlertType.WARNING, "Błąd", "Termin nie może być w przeszłości.");
                    return;
                }

                EmpTask newTask = new EmpTask(name, date, status, description, timeOfShift);
                taskRepository.addTask(newTask);

                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Zadanie dodane!");
                showTaskPanel();

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Błąd",
                        "Nie udało się dodać zadania: " + ex.getMessage());
            }
        });

        buttonBox.getChildren().addAll(backButton, saveButton);

        layout.getChildren().addAll(
                nameLabel, nameField,
                descLabel, descriptionArea,
                statusLabel, statusCombo,
                dateLabel, deadlinePicker,
                buttonBox
        );

        managerPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla panel z wnioskami o nieobecność.
     */
    public void showAbsencePanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label absenceLabel = new Label("Wnioski o nieobecność");
        absenceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<AbsenceRequest> absenceTable = new TableView<>();
        absenceTable.setMinHeight(300);
        absenceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<AbsenceRequest, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(
                data.getValue().getId()).asObject());
        idColumn.setPrefWidth(50);

        TableColumn<AbsenceRequest, String> typeColumn = new TableColumn<>("Typ wniosku");
        typeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getRequestType()));
        typeColumn.setPrefWidth(150);

        TableColumn<AbsenceRequest, String> employeeColumn = new TableColumn<>("Pracownik");
        employeeColumn.setCellValueFactory(data -> {
            Employee employee = data.getValue().getEmployee();
            if (employee != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        employee.getName() + " " + employee.getSurname());
            } else {
                return new javafx.beans.property.SimpleStringProperty("Nieznany");
            }
        });
        employeeColumn.setPrefWidth(150);

        TableColumn<AbsenceRequest, String> fromDateColumn = new TableColumn<>("Od");
        fromDateColumn.setCellValueFactory(data -> {
            java.util.Date utilStart = data.getValue().getStartDate();
            if (utilStart != null) {
                java.sql.Date sqlStart = new java.sql.Date(utilStart.getTime());
                return new javafx.beans.property.SimpleStringProperty(sqlStart.toString());
            } else {
                return new javafx.beans.property.SimpleStringProperty("Brak daty");
            }
        });
        fromDateColumn.setPrefWidth(100);

        TableColumn<AbsenceRequest, String> toDateColumn = new TableColumn<>("Do");
        toDateColumn.setCellValueFactory(data -> {
            java.util.Date utilEnd = data.getValue().getEndDate();
            if (utilEnd != null) {
                java.sql.Date sqlEnd = new java.sql.Date(utilEnd.getTime());
                return new javafx.beans.property.SimpleStringProperty(sqlEnd.toString());
            } else {
                return new javafx.beans.property.SimpleStringProperty("Brak daty");
            }
        });
        toDateColumn.setPrefWidth(100);

        // Dodanie nowej kolumny statusu
        TableColumn<AbsenceRequest, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getStatus().toString()));
        statusColumn.setPrefWidth(100);

        TableColumn<AbsenceRequest, String> descriptionColumn = new TableColumn<>("Opis");
        descriptionColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDescription()));
        descriptionColumn.setPrefWidth(200);

        absenceTable.getColumns().addAll(
                idColumn, typeColumn, employeeColumn,
                fromDateColumn, toDateColumn, statusColumn, descriptionColumn
        );

        AbsenceRequestRepository absenceRepository = new AbsenceRequestRepository();
        try {
            absenceTable.getItems().addAll(absenceRepository.getAllRequests());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd",
                    "Nie udało się załadować wniosków: " + e.getMessage());
        }

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button approveButton = new Button("Zatwierdź");
        approveButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white;");
        Button rejectButton = new Button("Odrzuć");
        rejectButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
        Button refreshButton = new Button("Odśwież");
        refreshButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

        approveButton.setOnAction(e -> {
            AbsenceRequest selectedRequest = absenceTable.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                try {
                    // Zmiana statusu zamiast modyfikacji opisu
                    selectedRequest.setStatus(AbsenceRequest.RequestStatus.ACCEPTED);
                    absenceRepository.updateRequest(selectedRequest);

                    if (selectedRequest.getRequestType().toLowerCase().contains("chorob")) {
                        Employee employee = selectedRequest.getEmployee();
                        employee.startSickLeave(selectedRequest.getStartDate());
                        userRepository.updateEmployee(employee);
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Sukces", "Wniosek został zatwierdzony.");
                    absenceTable.getItems().setAll(absenceRepository.getAllRequests());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Błąd",
                            "Nie udało się zatwierdzić wniosku: " + ex.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Błąd", "Wybierz wniosek do zatwierdzenia.");
            }
        });

        rejectButton.setOnAction(e -> {
            AbsenceRequest selectedRequest = absenceTable.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                try {
                    // Zmiana statusu zamiast modyfikacji opisu
                    selectedRequest.setStatus(AbsenceRequest.RequestStatus.REJECTED);
                    absenceRepository.updateRequest(selectedRequest);
                    showAlert(Alert.AlertType.INFORMATION, "Sukces", "Wniosek został odrzucony.");
                    absenceTable.getItems().setAll(absenceRepository.getAllRequests());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Błąd",
                            "Nie udało się odrzucić wniosku: " + ex.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Błąd", "Wybierz wniosek do odrzucenia.");
            }
        });

        buttonBox.getChildren().addAll(approveButton, rejectButton);

        primaryStage.setOnHidden(event -> {
        });

        layout.getChildren().addAll(
                absenceLabel, absenceTable, buttonBox
        );

        managerPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla okno przypisywania pracownika do zadania.
     */
    public void showAssignEmployeeDialog() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Przypisz pracownika do zadania");

        VBox dialogLayout = new VBox(15);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.setAlignment(Pos.CENTER);

        Label taskLabel = new Label("Wybierz zadanie:");
        ComboBox<EmpTask> taskComboBox = new ComboBox<>();
        List<EmpTask> allTasks = taskRepository.getAllTasks();
        taskComboBox.setItems(FXCollections.observableArrayList(allTasks));
        taskComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(EmpTask item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        taskComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(EmpTask item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        Label employeeLabel = new Label("Wybierz pracownika:");
        ComboBox<Employee> employeeComboBox = new ComboBox<>();
        List<Employee> allEmployees = userRepository.getAllEmployees();
        List<Employee> workers = allEmployees.stream()
                .filter(emp -> "Pracownik".equalsIgnoreCase(emp.getPosition()))
                .toList();
        employeeComboBox.setItems(FXCollections.observableArrayList(workers));
        employeeComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Employee item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " " + item.getSurname());
            }
        });
        employeeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Employee item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " " + item.getSurname());
            }
        });

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button assignButton = new Button("Przypisz");
        Button cancelButton = new Button("Anuluj");

        assignButton.setOnAction(e -> {
            EmpTask selectedTask = taskComboBox.getValue();
            Employee selectedEmployee = employeeComboBox.getValue();
            if (selectedTask != null && selectedEmployee != null) {
                try {
                    TaskEmployee te = new TaskEmployee(selectedTask, selectedEmployee);
                    TaskEmployeeRepository teRepo = new TaskEmployeeRepository();
                    teRepo.add(te);
                    teRepo.close();

                    showAlert(Alert.AlertType.INFORMATION, "Sukces",
                            "Pracownik został przypisany do zadania.");
                    dialogStage.close();

                    showTaskPanel();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Błąd",
                            "Nie udało się przypisać pracownika: " + ex.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Błąd",
                        "Wybierz zarówno zadanie, jak i pracownika.");
            }
        });

        cancelButton.setOnAction(e -> dialogStage.close());
        buttonBox.getChildren().addAll(assignButton, cancelButton);

        dialogLayout.getChildren().addAll(
                taskLabel, taskComboBox,
                employeeLabel, employeeComboBox,
                buttonBox
        );

        Scene scene = new Scene(dialogLayout, 350, 250);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }


    /**
     * Wyświetla okno dialogowe edycji zadania.
     *
     * @param task zadanie do edycji
     */
    private void showEditTaskDialog(EmpTask task) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Edycja zadania");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Nazwa zadania:");
        TextField nameField = new TextField(task.getName());

        Label descLabel = new Label("Opis:");
        TextArea descArea = new TextArea(task.getDescription());
        descArea.setPrefRowCount(4);

        Label statusLabel = new Label("Status:");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Nowe", "W trakcie", "Zakończone", "Opóźnione");
        statusCombo.setValue(task.getStatus());

        Label dateLabel = new Label("Termin:");
        DatePicker deadlinePicker = new DatePicker();
        if (task.getDate() != null) {
            deadlinePicker.setValue(
                    new java.sql.Date(task.getDate().getTime()).toLocalDate()
            );
        }

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button saveButton = new Button("Zapisz");
        Button cancelButton = new Button("Anuluj");

       saveButton.setOnAction(e -> {
                    try {
                        String name = nameField.getText();
                        String description = descArea.getText();
                        String status = statusCombo.getValue();

                        task.setName(name);
                        task.setDescription(description);
                        task.setStatus(status);

                        java.sql.Date date = null;
                        if (deadlinePicker.getValue() != null) {
                            date = java.sql.Date.valueOf(deadlinePicker.getValue());
                            task.setDate(date);
                        }

                        if (name.isEmpty() || description.isEmpty() || status == null || date == null) {
                            showAlert(Alert.AlertType.WARNING, "Błąd", "Wypełnij wszystkie pola.");
                            return;
                        }

                        if (date.before(Date.valueOf(java.time.LocalDate.now()))) {
                            showAlert(Alert.AlertType.WARNING, "Błąd", "Termin nie może być w przeszłości.");
                            return;
                        }

                taskRepository.updateTask(task);

                showAlert(Alert.AlertType.INFORMATION, "Sukces",
                        "Zadanie zaktualizowane!");
                dialogStage.close();
                showTaskPanel();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Błąd",
                        "Nie udało się zaktualizować zadania: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialogStage.close());
        buttonBox.getChildren().addAll(saveButton, cancelButton);

        layout.getChildren().addAll(
                nameLabel, nameField,
                descLabel, descArea,
                statusLabel, statusCombo,
                dateLabel, deadlinePicker,
                buttonBox
        );

        Scene scene = new Scene(layout, 350, 500);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    /**
     * Wylogowuje użytkownika i zamyka zasoby.
     */
    public void logout() {
        primaryStage.close();
        Stage loginStage = new Stage();
        try {
            new HelloApplication().start(loginStage);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * Wyświetla komunikat typu Alert.
     *
     * @param type   typ alertu
     * @param title  tytuł
     * @param header nagłówek
     */
    private void showAlert(Alert.AlertType type, String title, String header) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(null);
        alert.showAndWait();
    }
}
