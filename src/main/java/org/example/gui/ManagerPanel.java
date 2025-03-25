package org.example.gui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ManagerPanel {

    private BorderPane root;
    private Stage primaryStage;
    private ManagerPanelController controller;

    public ManagerPanel(Stage stage) {
        this.primaryStage = stage;
        this.controller = new ManagerPanelController(this);

        primaryStage.setTitle("Panel kierownika");

        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: lightblue;");

        // Lewy panel nawigacyjny
        VBox menu = createMenu();
        root.setLeft(menu);

        controller.showTaskPanel();

        // Animacje
        animateFadeIn(menu, 1000);
        animateSlideDown(menu, 800);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createMenu() {
        // Utwórz kontener na menu
        VBox menu = new VBox(15);
        menu.setPadding(new Insets(20));
        menu.setStyle("-fx-background-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10;");
        menu.setAlignment(Pos.TOP_CENTER);

        // Dodanie logo w lewym górnym rogu
        Image image = new Image(getClass().getResourceAsStream("/logo.png"));
        ImageView logo = new ImageView(image);
        logo.setFitWidth(100);
        logo.setFitHeight(100);
        logo.setPreserveRatio(true);

        // Przycisk dodawania zadania
        Button addTaskButton = createStyledButton("Dodaj zadanie");
        addTaskButton.setOnAction(e -> controller.showAddTaskPanel());

        // Przycisk przypisania pracownika do zadania
        Button assignEmployeeButton = createStyledButton("Przypisz pracownika do zadania");
        assignEmployeeButton.setOnAction(e -> controller.showAssignEmployeeDialog());

        // Przycisk wniosków o nieobecność
        Button absenceButton = createStyledButton("Wnioski o nieobecność");
        absenceButton.setOnAction(e -> controller.showAbsencePanel());

        // Przycisk wylogowania
        Button logoutButton = createStyledButton("Wyloguj się", "#E74C3C");
        logoutButton.setOnAction(e -> controller.logout());

        // Dodanie logo na górze, a następnie przycisków menu
        menu.getChildren().add(logo);
        menu.getChildren().addAll(addTaskButton, assignEmployeeButton, absenceButton, logoutButton);

        return menu;
    }

    private Button createStyledButton(String text) {
        return createStyledButton(text, "#2980B9");
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;");

        // Efekt powiększenia po najechaniu
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        });

        return button;
    }

    private void animateFadeIn(VBox element, int duration) {
        FadeTransition fade = new FadeTransition(Duration.millis(duration), element);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void animateSlideDown(VBox element, int duration) {
        TranslateTransition slide = new TranslateTransition(Duration.millis(duration), element);
        slide.setFromY(-50);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_BOTH);
        slide.play();
    }

    public void setCenterPane(Pane pane) {
        root.setCenter(pane);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
