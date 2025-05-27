/*
 * Classname: EmployeePanelController
 * Version information: 1.3
 * Date: 2025-05-27
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Kontroler odpowiedzialny za logikę widoku panelu pracownika.
 * Obsługuje interakcje związane z zadaniami, zgłoszeniami oraz wylogowaniem.
 */
public class EmployeePanelController {

    private final EmployeePanel employeePanel;
    private final Stage primaryStage;

    /**
     * Konstruktor klasy kontrolera.
     *
     * @param employeePanel główny panel pracownika
     */
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
        taskLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;"); // jak w AdminPanel

        TableView<String> taskTable = new TableView<>();
        taskTable.setMinHeight(200);
        taskTable.setPlaceholder(new Label("Brak przypisanych zadań."));

        Button updateStatusButton = new Button("Zaktualizuj status");
        updateStatusButton.setStyle(
                "-fx-background-color: #2980B9; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;"
        );

        updateStatusButton.setOnMouseEntered(e -> {
            updateStatusButton.setScaleX(1.1);
            updateStatusButton.setScaleY(1.1);
        });
        updateStatusButton.setOnMouseExited(e -> {
            updateStatusButton.setScaleX(1);
            updateStatusButton.setScaleY(1);
        });

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button reportProblemButton = new Button("Zgłoś problem");
        reportProblemButton.setStyle(
                "-fx-background-color: #2980B9; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;"
        );
        reportProblemButton.setOnMouseEntered(e -> {
            reportProblemButton.setScaleX(1.1);
            reportProblemButton.setScaleY(1.1);
        });
        reportProblemButton.setOnMouseExited(e -> {
            reportProblemButton.setScaleX(1);
            reportProblemButton.setScaleY(1);
        });
        reportProblemButton.setOnAction(e -> showReportIssueWindow());

        Button logoutButton = new Button("Wyloguj się");
        logoutButton.setStyle(
                "-fx-background-color: #E74C3C; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;"
        );
        logoutButton.setOnMouseEntered(e -> {
            logoutButton.setScaleX(1.1);
            logoutButton.setScaleY(1.1);
        });
        logoutButton.setOnMouseExited(e -> {
            logoutButton.setScaleX(1);
            logoutButton.setScaleY(1);
        });
        logoutButton.setOnAction(e -> logout());

        buttonBox.getChildren().addAll(updateStatusButton, reportProblemButton, logoutButton);

        layout.getChildren().addAll(taskLabel, taskTable, buttonBox);
        employeePanel.setCenterPane(layout);
    }

    /**
     * Wyświetla okno do zgłoszenia problemu przez pracownika.
     * Użytkownik może opisać problem i przypisać mu kategorię.
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
        submitButton.setOnMouseEntered(e -> {
            submitButton.setScaleX(1.1);
            submitButton.setScaleY(1.1);
        });
        submitButton.setOnMouseExited(e -> {
            submitButton.setScaleX(1);
            submitButton.setScaleY(1);
        });
        submitButton.setOnAction(e -> {
            String desc = descriptionArea.getText();
            String category = categoryBox.getValue();

            if (desc.isEmpty() || category == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Błąd");
                alert.setHeaderText("Uzupełnij wszystkie pola");
                alert.setContentText("Aby zgłosić problem, wpisz opis i wybierz kategorię.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Zgłoszono");
                alert.setHeaderText("Zgłoszenie zostało przyjęte");
                alert.setContentText("Dziękujemy za przesłanie zgłoszenia.");
                alert.showAndWait();
                issueStage.close();
            }
        });

        layout.getChildren().addAll(
                descLabel,
                descriptionArea,
                categoryLabel,
                categoryBox,
                submitButton
        );
        Scene scene = new Scene(layout, 350, 300);
        issueStage.setScene(scene);
        issueStage.show();
    }

    /**
     * Wylogowuje użytkownika i uruchamia ponownie ekran logowania.
     * Zamyka bieżące okno i uruchamia nowe z aplikacją startową.
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
