package org.example.gui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import java.time.LocalDate;

public class ReportsPanel extends VBox {

    private ComboBox<String> reportTypeCombo;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private TextArea reportOutput;

    public ReportsPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        initializeUI();
    }

    private void initializeUI() {
        Label titleLabel = new Label("Panel Raportów");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        reportTypeCombo = new ComboBox<>();
        reportTypeCombo.getItems().addAll(
                "Sprzedaż dzienna",
                "Sprzedaż miesięczna",
                "Najpopularniejsze produkty",
                "Stan magazynowy",
                "Rotacja towaru"
        );
        reportTypeCombo.setPromptText("Wybierz typ raportu");

        startDatePicker = new DatePicker();
        endDatePicker = new DatePicker();

        HBox dateRangeBox = new HBox(10, new Label("Od:"), startDatePicker, new Label("Do:"), endDatePicker);

        Button generateButton = new Button("Generuj raport");
        generateButton.setOnAction(e -> generateReport());

        Button exportButton = new Button("Eksportuj do PDF");
        exportButton.setOnAction(e -> exportToPDF());

        HBox buttonsBox = new HBox(10, generateButton, exportButton);

        reportOutput = new TextArea();
        reportOutput.setEditable(false);
        reportOutput.setPrefHeight(500);

        getChildren().addAll(
                titleLabel,
                new Label("Typ raportu:"),
                reportTypeCombo,
                dateRangeBox,
                buttonsBox,
                new Separator(),
                reportOutput
        );
    }

    private void generateReport() {
        String reportType = reportTypeCombo.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        // Tutaj logika generowania raportu
        String reportContent = "Wygenerowano raport: " + reportType + "\n";
        reportContent += "Zakres dat: " + startDate + " - " + endDate + "\n\n";
        reportContent += "Przykładowe dane raportu...\n";
        reportContent += "--------------------------------\n";
        reportContent += "Produkt 1: 120 szt.\n";
        reportContent += "Produkt 2: 85 szt.\n";
        reportContent += "Produkt 3: 42 szt.\n";

        reportOutput.setText(reportContent);
    }

    private void exportToPDF() {
        // Tutaj logika eksportu do PDF
        System.out.println("Eksportowanie raportu do PDF...");
    }
}