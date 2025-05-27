/*
 * Classname: EmployeePanelController
 * Version information: 1.4
 * Date: 2025-05-27
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.database.EmpTaskRepository;
import org.example.database.TaskEmployeeRepository;
import org.example.database.UserRepository;
import org.example.sys.EmpTask;
import org.example.sys.TaskEmployee;
import org.example.sys.Employee;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Kontroler odpowiedzialny za logikę widoku panelu pracownika.
 * Obsługuje interakcje związane z zadaniami, zgłoszeniami oraz wylogowaniem.
 */
public class EmployeePanelController {

    private final EmployeePanel employeePanel;
    private final Stage primaryStage;

    public EmployeePanelController(EmployeePanel employeePanel) {
        this.employeePanel = employeePanel;
        this.primaryStage = employeePanel.getPrimaryStage();
    }

    /**
     * Wyświetla główny panel pracownika z listą zadań i przyciskami akcji.
     */
    public void showEmployeePanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        Label taskLabel = new Label("Lista zadań");
        taskLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<EmpTask> taskTable = new TableView<>();
        taskTable.setMinHeight(200);
        taskTable.setPlaceholder(new Label("Brak przypisanych zadań."));

        TableColumn<EmpTask, String> nameCol = new TableColumn<>("Zadanie");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<EmpTask, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<EmpTask, String> dateCol = new TableColumn<>("Termin");
        dateCol.setCellValueFactory(data -> {
            var d = data.getValue().getDate();
            return new javafx.beans.property.SimpleStringProperty(d != null ? d.toString() : "brak");
        });

        taskTable.getColumns().addAll(nameCol, statusCol, dateCol);

        // załaduj zadania przypisane bieżącemu pracownikowi
        Employee current = new UserRepository().getCurrentEmployee();
        if (current != null) {
            int empId = current.getId();
            TaskEmployeeRepository teRepo = new TaskEmployeeRepository();
            EmpTaskRepository taskRepo = new EmpTaskRepository();

            List<EmpTask> tasks = teRepo.findByEmployee(empId).stream()
                    .map(te -> taskRepo.findTaskById(te.getId().getTaskId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            taskTable.setItems(FXCollections.observableArrayList(tasks));
        }

        Button updateStatusButton = new Button("Zaktualizuj status");
        updateStatusButton.setStyle(
                "-fx-background-color: #2980B9; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;"
        );
        updateStatusButton.setOnMouseEntered(e -> { updateStatusButton.setScaleX(1.1); updateStatusButton.setScaleY(1.1); });
        updateStatusButton.setOnMouseExited(e -> { updateStatusButton.setScaleX(1); updateStatusButton.setScaleY(1); });
        updateStatusButton.setOnAction(e -> {
            EmpTask selected = taskTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Wybierz zadanie do edycji statusu.", ButtonType.OK);
                a.showAndWait();
                return;
            }
            ChoiceDialog<String> dialog = new ChoiceDialog<>(selected.getStatus(), "Nowe", "W trakcie", "Zakończone");
            dialog.setTitle("Zaktualizuj status");
            dialog.setHeaderText("Wybierz nowy status:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newStatus -> {
                selected.setStatus(newStatus);
                new EmpTaskRepository().updateTask(selected);
                taskTable.refresh();
            });
        });

        Button reportProblemButton = new Button("Zgłoś problem");
        reportProblemButton.setStyle(
                "-fx-background-color: #2980B9; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;"
        );
        reportProblemButton.setOnMouseEntered(e -> { reportProblemButton.setScaleX(1.1); reportProblemButton.setScaleY(1.1); });
        reportProblemButton.setOnMouseExited(e -> { reportProblemButton.setScaleX(1); reportProblemButton.setScaleY(1); });
        reportProblemButton.setOnAction(e -> showReportIssueWindow());

        Button logoutButton = new Button("Wyloguj się");
        logoutButton.setStyle(
                "-fx-background-color: #E74C3C; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;"
        );
        logoutButton.setOnMouseEntered(e -> { logoutButton.setScaleX(1.1); logoutButton.setScaleY(1.1); });
        logoutButton.setOnMouseExited(e -> { logoutButton.setScaleX(1); logoutButton.setScaleY(1); });
        logoutButton.setOnAction(e -> logout());

        HBox buttonBox = new HBox(10, updateStatusButton, reportProblemButton, logoutButton);
        buttonBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(taskLabel, taskTable, buttonBox);
        employeePanel.setCenterPane(layout);
    }

    /**
     * Wyświetla okno do zgłoszenia problemu przez pracownika.
     */
    public void showReportIssueWindow() {
        Stage issueStage = new Stage();
        issueStage.setTitle("Panel pracownika - Zgłoszenie problemu");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        Label descLabel = new Label("Opis problemu");
        descLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(4);

        Label categoryLabel = new Label("Kategoria");
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Techniczny", "Zadanie", "Inny");

        Button submitButton = new Button("Wyślij");
        submitButton.setStyle(
                "-fx-background-color: #2980B9; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;"
        );
        submitButton.setOnMouseEntered(e -> { submitButton.setScaleX(1.1); submitButton.setScaleY(1.1); });
        submitButton.setOnMouseExited(e -> { submitButton.setScaleX(1); submitButton.setScaleY(1); });
        submitButton.setOnAction(e -> {
            String desc = descriptionArea.getText();
            String category = categoryBox.getValue();
            if (desc.isEmpty() || category == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "Aby zgłosić problem, wpisz opis i wybierz kategorię.", ButtonType.OK);
                alert.setTitle("Błąd");
                alert.setHeaderText("Uzupełnij wszystkie pola");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Dziękujemy za przesłanie zgłoszenia.", ButtonType.OK);
                alert.setTitle("Zgłoszono");
                alert.setHeaderText("Zgłoszenie zostało przyjęte");
                alert.showAndWait();
                issueStage.close();
            }
        });

        layout.getChildren().addAll(
                descLabel, descriptionArea,
                categoryLabel, categoryBox,
                submitButton
        );
        issueStage.setScene(new Scene(layout, 350, 300));
        issueStage.show();
    }

    /**
     * Wylogowuje użytkownika i uruchamia ekran logowania.
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
}
