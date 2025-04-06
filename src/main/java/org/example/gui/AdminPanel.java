/*
 * Classname: AdminPanel
 * Version information: 1.0
 * Date: 2025-04-06
 * Copyright notice: © BŁĘKITNI
 */

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

/**
 * Klasa reprezentująca główny panel administratora.
 * Tworzy GUI, które zawiera panel nawigacyjny oraz różne widoki zarządzania.
 */
public class AdminPanel {

    private BorderPane root;
    private Stage primaryStage;
    private AdminPanelController controller;

    /**
     * Tworzy nową instancję panelu administratora.
     *
     * @param stage Główna scena aplikacji
     */
    public AdminPanel(Stage stage) {
        this.primaryStage = stage;
        this.controller = new AdminPanelController(this);

        primaryStage.setTitle("Panel administratora");

        // Główna struktura układu
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: lightblue;");

        // Lewy panel nawigacyjny
        VBox menu = createMenu();
        root.setLeft(menu);

        // Domyślnie wyświetl widok użytkowników
        controller.showUserManagement();

        // Animacje po uruchomieniu
        animateFadeIn(menu, 1000);
        animateSlideDown(menu, 800);

        // Ustawienie sceny
        Scene scene = new Scene(root, 700, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Tworzy panel menu z przyciskami nawigacyjnymi i logo.
     *
     * @return VBox zawierający przyciski i logo
     */
    private VBox createMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle(
                "-fx-background-color: #E0E0E0; "
                        + "-fx-border-radius: 10; "
                        + "-fx-background-radius: 10;"
        );
        menu.setAlignment(Pos.TOP_LEFT);

        // Logo w lewym górnym rogu
        Image image = new Image(
                Objects.requireNonNull(
                        getClass().getResourceAsStream("/logo.png")
                )
        );
        ImageView logo = new ImageView(image);
        logo.setFitWidth(100);
        logo.setFitHeight(100);
        logo.setPreserveRatio(true);

        // Przycisk zarządzania użytkownikami
        Button usersButton = createStyledButton("Użytkownicy");
        usersButton.setOnAction(e -> controller.showUserManagement());

        // Przycisk konfiguracji
        Button configButton = createStyledButton("Konfiguracja");
        configButton.setOnAction(e -> controller.showConfigPanel());

        // Przycisk raportów
        Button reportsButton = createStyledButton("Raporty");
        reportsButton.setOnAction(e -> controller.showReportsPanel());

        // Przycisk zgłoszeń
        Button issuesButton = createStyledButton("Zgłoszenia");
        issuesButton.setOnAction(e -> controller.showIssuesPanel());

        // Przycisk wylogowania
        Button logoutButton = createStyledButton("Wyloguj", "#E74C3C");
        logoutButton.setOnAction(e -> controller.logout());

        // Dodanie elementów do menu
        menu.getChildren().add(logo);
        menu.getChildren().addAll(
                usersButton,
                configButton,
                reportsButton,
                issuesButton,
                logoutButton);

        return menu;
    }

    /**
     * Tworzy przycisk z domyślnym kolorem.
     *
     * @param text Tekst wyświetlany na przycisku
     * @return Stylizowany przycisk
     */
    private Button createStyledButton(String text) {
        return createStyledButton(text, "#2980B9"); // domyślny: niebieski
    }

    /**
     * Tworzy stylizowany przycisk z określonym kolorem.
     *
     * @param text  Tekst na przycisku
     * @param color Kolor tła przycisku
     * @return Stylizowany przycisk
     */
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color
                + "; -fx-text-fill: white; -fx-font-weight: bold;");

        // Efekt powiększenia przy najechaniu kursorem
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(
                    Duration.millis(200),
                    button
            );
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(
                    Duration.millis(200),
                    button
            );
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        });

        return button;
    }

    /**
     * Animacja zanikania (fade in) dla danego elementu.
     *
     * @param element  Element do animacji
     * @param duration Czas trwania animacji w milisekundach
     */
    private void animateFadeIn(VBox element, int duration) {
        FadeTransition fade = new FadeTransition(
                Duration.millis(duration)
                ,element
        );
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    /**
     * Animacja przesunięcia w dół (slide down) dla danego elementu.
     *
     * @param element  Element do animacji
     * @param duration Czas trwania animacji w milisekundach
     */
    private void animateSlideDown(VBox element, int duration) {
        TranslateTransition slide = new TranslateTransition(
                Duration.millis(duration),
                element
        );
        slide.setFromY(-50);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_BOTH);
        slide.play();
    }

    /**
     * Ustawia zawartość środkowego panelu aplikacji.
     *
     * @param pane Nowy panel do wyświetlenia
     */
    public void setCenterPane(javafx.scene.layout.Pane pane) {
        root.setCenter(pane);
    }

    /**
     * Zwraca główną scenę aplikacji.
     *
     * @return Obiekt Stage przypisany do aplikacji
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
