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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class AdminPanel {

    private BorderPane root;
    private Stage primaryStage;
    private AdminPanelController controller;

    public AdminPanel(Stage stage) {
        this.primaryStage = stage;
        this.controller = new AdminPanelController(this);

        primaryStage.setTitle("Panel administratora");

        // Główna struktura układu
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: lightblue;"); // Dopasowanie do ekranu logowania

        // Lewy panel nawigacyjny
        VBox menu = createMenu();
        root.setLeft(menu);

        // Domyślnie wyświetl widok użytkowników
        controller.showUserManagement();

        // Animacje
        animateFadeIn(menu, 1000);
        animateSlideDown(menu, 800);

        // Scena
        Scene scene = new Scene(root, 700, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10;");
        menu.setAlignment(Pos.TOP_LEFT);

        // Dodanie logo w lewym górnym rogu
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/logo.png")));
        ImageView logo = new ImageView(image);
        logo.setFitWidth(100);  // Ustawiamy odpowiednią szerokość logo
        logo.setFitHeight(100); // Ustawiamy odpowiednią wysokość logo
        logo.setPreserveRatio(true); // Zachowanie proporcji

        // Przycisk użytkowników
        Button usersButton = createStyledButton("Użytkownicy");
        usersButton.setOnAction(e -> controller.showUserManagement());

        Button configButton = createStyledButton("Konfiguracja");
        configButton.setOnAction(e -> controller.showConfigPanel());

        Button reportsButton = createStyledButton("Raporty");
        reportsButton.setOnAction(e -> controller.showReportsPanel());

        Button issuesButton = createStyledButton("Zgłoszenia");
        issuesButton.setOnAction(e -> controller.showIssuesPanel());

        Button logoutButton = createStyledButton("Wyloguj", "#E74C3C"); // Czerwony przycisk
        logoutButton.setOnAction(e -> controller.logout());

        // Dodanie logo na górze, a następnie przycisków menu
        menu.getChildren().add(logo);
        menu.getChildren().addAll(usersButton, configButton, reportsButton, issuesButton, logoutButton);

        return menu;
    }

    private Button createStyledButton(String text) {
        return createStyledButton(text, "#2980B9"); // Domyślnie niebieski
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

    public void setCenterPane(javafx.scene.layout.Pane pane) {
        root.setCenter(pane);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
