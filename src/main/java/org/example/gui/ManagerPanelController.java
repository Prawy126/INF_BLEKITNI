package org.example.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ManagerPanelController {

    private final ManagerPanel managerPanel;
    private final Stage primaryStage;
    private TableView<String> taskTable;
    private TableView<String> absenceTable;

    public ManagerPanelController(ManagerPanel managerPanel) {
        this.managerPanel = managerPanel;
        this.primaryStage = managerPanel.getPrimaryStage();
    }

    public void showTaskPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label taskLabel = new Label("Lista zadań");
        taskTable = new TableView<>();
        taskTable.setMinHeight(200);

        Button addTaskButton = new Button("Dodaj zadanie");
        addTaskButton.setOnAction(e -> showAddTaskPanel());

        Button assignEmployeeButton = new Button("Przypisz pracownika do zadania");
        assignEmployeeButton.setOnAction(e -> showAssignEmployeeDialog());

        Button absenceButton = new Button("Wnioski o nieobecność");
        absenceButton.setOnAction(e -> showAbsencePanel());

        Label recruitLabel = new Label("Panel rekrutacji");
        ListView<String> recruitmentList = new ListView<>();
        recruitmentList.getItems().addAll("Jan Kowalski - CV.pdf", "Anna Nowak - CV.pdf");

        HBox recruitButtons = new HBox(10);
        recruitButtons.setAlignment(Pos.CENTER);
        Button inviteButton = new Button("Zaproszenie na rozmowę");
        Button rejectButton = new Button("Odrzuć aplikację");

        recruitButtons.getChildren().addAll(inviteButton, rejectButton);

        Button logoutButton = new Button("Wyloguj się");
        logoutButton.setOnAction(e -> logout());

        layout.getChildren().addAll(taskLabel, taskTable, addTaskButton, assignEmployeeButton, absenceButton, recruitLabel, recruitmentList, recruitButtons, logoutButton);
        managerPanel.setCenterPane(layout);
    }

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
        statusCombo.getItems().addAll("Nowe", "W toku", "Zakończone");

        Label priorityLabel = new Label("Priorytet");
        ComboBox<String> priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll("Niski", "Średni", "Wysoki");

        Label employeeLabel = new Label("Pracownik");
        ComboBox<String> employeeCombo = new ComboBox<>();
        employeeCombo.getItems().addAll("Anna Nowak", "Jan Kowalski");

        Label dateLabel = new Label("Termin");
        DatePicker deadlinePicker = new DatePicker();

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button backButton = new Button("Wróć");
        backButton.setOnAction(e -> showTaskPanel());

        Button saveButton = new Button("Zapisz");
        saveButton.setStyle("-fx-background-color: #2980B9; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            taskTable.getItems().add(nameField.getText());
            showTaskPanel();
        });

        buttonBox.getChildren().addAll(backButton, saveButton);

        layout.getChildren().addAll(
                nameLabel, nameField,
                descLabel, descriptionArea,
                statusLabel, statusCombo,
                priorityLabel, priorityCombo,
                employeeLabel, employeeCombo,
                dateLabel, deadlinePicker,
                buttonBox
        );

        managerPanel.setCenterPane(layout);
    }

    public void showAbsencePanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label absenceLabel = new Label("Wnioski o nieobecność");
        absenceTable = new TableView<>();
        absenceTable.setMinHeight(200);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button approveButton = new Button("Zatwierdź");
        Button rejectButton = new Button("Odrzuć");

        buttonBox.getChildren().addAll(approveButton, rejectButton);

        Button backButton = new Button("Wróć");
        backButton.setOnAction(e -> showTaskPanel());

        layout.getChildren().addAll(absenceLabel, absenceTable, buttonBox, backButton);
        managerPanel.setCenterPane(layout);
    }

    public void showAssignEmployeeDialog() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Przypisz pracownika do zadania");

        VBox dialogLayout = new VBox(15);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.setAlignment(Pos.CENTER);

        Label taskLabel = new Label("Wybierz zadanie:");
        ComboBox<String> taskComboBox = new ComboBox<>();
        taskComboBox.getItems().addAll("Zadanie 1", "Zadanie 2", "Zadanie 3");

        Label employeeLabel = new Label("Wybierz pracownika:");
        ComboBox<String> employeeComboBox = new ComboBox<>();
        employeeComboBox.getItems().addAll("Anna Nowak", "Jan Kowalski", "Marek Wiśniewski");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button assignButton = new Button("Przypisz");
        assignButton.setOnAction(e -> {
            String selectedTask = taskComboBox.getValue();
            String selectedEmployee = employeeComboBox.getValue();

            if (selectedTask != null && selectedEmployee != null) {
                System.out.println("Przypisano: " + selectedEmployee + " do " + selectedTask);
                dialogStage.close();
            } else {
                showAlert(Alert.AlertType.WARNING, "Błąd", "Wybierz zarówno zadanie, jak i pracownika.");
            }
        });

        Button cancelButton = new Button("Anuluj");
        cancelButton.setOnAction(e -> dialogStage.close());

        buttonBox.getChildren().addAll(assignButton, cancelButton);

        dialogLayout.getChildren().addAll(taskLabel, taskComboBox, employeeLabel, employeeComboBox, buttonBox);

        Scene scene = new Scene(dialogLayout, 300, 200);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
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

    private void showAlert(Alert.AlertType type, String title, String header) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(null);
        alert.showAndWait();
    }
}
