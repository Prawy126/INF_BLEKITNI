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
