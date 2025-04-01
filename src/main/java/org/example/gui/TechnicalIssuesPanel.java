package org.example.gui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TechnicalIssuesPanel extends VBox {

    private TextField issueTitleField;
    private ComboBox<String> issueTypeCombo;
    private ComboBox<String> priorityCombo;
    private TextArea issueDescriptionArea;
    private ListView<String> issuesList;
    private ObservableList<String> submittedIssues;

    public TechnicalIssuesPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        initializeUI();
    }

    private void initializeUI() {
        Label titleLabel = new Label("Zgłaszanie problemów technicznych");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        issueTitleField = new TextField();
        issueTitleField.setPromptText("Tytuł problemu");

        issueTypeCombo = new ComboBox<>();
        issueTypeCombo.getItems().addAll(
                "Sprzęt komputerowy",
                "Oprogramowanie",
                "Kasa fiskalna",
                "Sieć/WiFi",
                "Inne"
        );
        issueTypeCombo.setPromptText("Kategoria problemu");

        priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll(
                "Niski",
                "Średni",
                "Wysoki",
                "Krytyczny"
        );
        priorityCombo.setPromptText("Priorytet");

        issueDescriptionArea = new TextArea();
        issueDescriptionArea.setPromptText("Opisz problem szczegółowo...");
        issueDescriptionArea.setPrefHeight(200);

        Button submitButton = new Button("Zgłoś problem");
        submitButton.setOnAction(e -> submitTechnicalIssue());

        // Lista zgłoszonych problemów
        submittedIssues = FXCollections.observableArrayList();
        issuesList = new ListView<>(submittedIssues);
        issuesList.setPrefHeight(200);

        Button refreshButton = new Button("Odśwież listę");
        refreshButton.setOnAction(e -> refreshIssuesList());

        HBox buttonsBox = new HBox(10, submitButton, refreshButton);

        getChildren().addAll(
                titleLabel,
                new Label("Tytuł:"),
                issueTitleField,
                new Label("Kategoria:"),
                issueTypeCombo,
                new Label("Priorytet:"),
                priorityCombo,
                new Label("Opis:"),
                issueDescriptionArea,
                buttonsBox,
                new Separator(),
                new Label("Twoje zgłoszenia:"),
                issuesList
        );
    }

    private void submitTechnicalIssue() {
        String title = issueTitleField.getText();
        String type = issueTypeCombo.getValue();
        String priority = priorityCombo.getValue();
        String description = issueDescriptionArea.getText();

        if (title.isEmpty() || type == null || priority == null || description.isEmpty()) {
            showAlert("Brakujące dane", "Wypełnij wszystkie pola formularza");
            return;
        }

        String issueEntry = String.format("[%s] %s - %s (%s)",
                java.time.LocalDate.now(), title, type, priority);

        submittedIssues.add(issueEntry);

        // Wyczyść formularz po zgłoszeniu
        issueTitleField.clear();
        issueTypeCombo.getSelectionModel().clearSelection();
        priorityCombo.getSelectionModel().clearSelection();
        issueDescriptionArea.clear();

        showAlert("Zgłoszenie przyjęte", "Problem został zgłoszony do działu IT");
    }

    private void refreshIssuesList() {
        // Tutaj można dodać logikę odświeżania listy z serwera/bazy danych
        System.out.println("Odświeżanie listy zgłoszeń...");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}