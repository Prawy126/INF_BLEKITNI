package org.example.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminPanel extends Application {

    private BorderPane root;
    private Stage primaryStage;
    private AdminPanelController controller;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.controller = new AdminPanelController(this);

        primaryStage.setTitle("Panel administratora");

        // Główna struktura układu
        root = new BorderPane();
        root.setPadding(new Insets(10));

        // Lewy panel nawigacyjny
        VBox menu = createMenu();
        root.setLeft(menu);

        // Domyślnie wyświetl widok użytkowników
        controller.showUserManagement();

        // Scena
        Scene scene = new Scene(root, 700, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #E0E0E0;");
        menu.setAlignment(Pos.TOP_LEFT);

        Button usersButton = new Button("Użytkownicy");
        usersButton.setOnAction(e -> controller.showUserManagement());

        Button configButton = new Button("Konfiguracja");
        configButton.setOnAction(e -> controller.showConfigPanel());

        Button reportsButton = new Button("Raporty");
        reportsButton.setOnAction(e -> controller.showReportsPanel());

        Button issuesButton = new Button("Zgłoszenia");
        issuesButton.setOnAction(e -> controller.showIssuesPanel());

        Button logoutButton = new Button("Wyloguj");
        logoutButton.setOnAction(e -> controller.logout());

        menu.getChildren().addAll(usersButton, configButton, issuesButton, reportsButton, logoutButton);
        return menu;
    }

    public void setCenterPane(javafx.scene.layout.Pane pane) {
        root.setCenter(pane);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
