package org.example.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ManagerPanelController {

    private final ManagerPanel managerPanel;
    private final Stage primaryStage;

    public ManagerPanelController(ManagerPanel managerPanel) {
        this.managerPanel = managerPanel;
        this.primaryStage = managerPanel.getPrimaryStage();
    }

    public void showTaskPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label taskLabel = new Label("Lista zadań");
        TableView taskTable = new TableView();
        taskTable.setMinHeight(200);

        Button addTaskButton = new Button("Dodaj zadanie");
        addTaskButton.setOnAction(e -> showAddTaskPanel());

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

        layout.getChildren().addAll(taskLabel, taskTable, addTaskButton, recruitLabel, recruitmentList, recruitButtons, logoutButton);
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

    public void logout() {
        primaryStage.close();

        Stage loginStage = new Stage();
        try {
            new HelloApplication().start(loginStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
