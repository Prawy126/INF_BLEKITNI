/*
 * Classname: ManagerPanel
 * Version information: 1.1
 * Date: 2025-06-07
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Panel kierownika, zawiera menu boczne oraz centralny panel widoku.
 * Pozwala nawigować po funkcjach przypisanych do roli kierownika.
 */
public class ManagerPanel {

    private static final Logger logger =
            LogManager.getLogger(ManagerPanel.class);
    private BorderPane root;
    private Stage primaryStage;
    private ManagerPanelController controller;
    private Image logoImage;
    private Button activeButton;

    /**
     * Konstruktor panelu kierownika.
     *
     * @param stage główne okno aplikacji
     */
    public ManagerPanel(Stage stage) {
        logger.info("Tworzenie ManagerPanel dla stage: {}", stage);

        this.primaryStage = stage;
        this.controller = new ManagerPanelController(this);
        logger.debug("Kontroler ManagerPanelController utworzony");

        try {
            logoImage = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/logo.png")
            ));
            logger.debug("Logo załadowane pomyślnie");
            primaryStage.getIcons().add(logoImage);
            logger.debug("Logo dodane jako ikona okna");
        } catch (Exception e) {
            logger.error("Błąd podczas ładowania logo: {}", e.getMessage(), e);
        }

        primaryStage.setTitle("Panel kierownika");
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(450);
        logger.debug("Tytuł okna ustawiony na 'Panel kierownika' z minimalnymi rozmiarami");

        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: lightblue;");
        logger.debug("Główny BorderPane utworzony i skonfigurowany");

        logger.debug("Tworzenie menu nawigacyjnego");
        VBox menu = createMenu();
        root.setLeft(menu);

        logger.debug("Wyświetlanie domyślnego panelu zadań");
        controller.showTaskPanel();

        logger.debug("Uruchamianie animacji menu");
        animateFadeIn(menu, 1000);
        animateSlideDown(menu, 800);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        logger.debug("Scena utworzona i ustawiona");

        logger.info("Wyświetlanie głównego okna panelu kierownika");
        primaryStage.show();
    }

    /**
     * Tworzy panel boczny z przyciskami menu.
     *
     * @return VBox z elementami menu
     */
    private VBox createMenu() {
        logger.debug("Tworzenie menu nawigacyjnego");
        VBox menu = new VBox(15);
        menu.setPadding(new Insets(20));
        menu.setStyle(
                "-fx-background-color: #E0E0E0; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10;"
        );
        menu.setAlignment(Pos.TOP_LEFT);

        Image image = null;
        try {
            image = new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream("/logo.png"))
            );
            logger.debug("Logo dla menu załadowane pomyślnie");
        } catch (Exception e) {
            logger.error("Błąd podczas ładowania logo dla menu: {}", e.getMessage(), e);
        }

        ImageView logo = new ImageView(image);
        if (image != null) {
            logo.setFitWidth(100);
            logo.setFitHeight(100);
            logo.setPreserveRatio(true);
        }

        VBox logoBox = new VBox(logo);
        logoBox.setAlignment(Pos.CENTER);

        Button tasksButton = createStyledButton("Zadania dla pracowników");
        Button absenceButton = createStyledButton("Wnioski o nieobecność");
        Button logoutButton = createStyledButton("Wyloguj się", "#E74C3C");

        tasksButton.setOnAction(e -> {
            logger.debug("Kliknięto przycisk 'Zadania dla pracowników'");
            setActiveButton(tasksButton);
            controller.showTaskPanel();
        });

        absenceButton.setOnAction(e -> {
            logger.debug("Kliknięto przycisk 'Wnioski o nieobecność'");
            setActiveButton(absenceButton);
            controller.showAbsencePanel();
        });

        logoutButton.setOnAction(e -> {
            logger.info("Rozpoczęcie procesu wylogowania");
            controller.logout();
        });

        menu.getChildren().addAll(
                logoBox,
                tasksButton,
                absenceButton,
                logoutButton
        );

        logger.debug("Menu utworzone pomyślnie z {} przyciskami",
                menu.getChildren().size() - 1); // -1 dla logoBox
        setActiveButton(tasksButton);
        return menu;
    }

    /**
     * Tworzy przycisk ze stylowaniem domyślnym (niebieski).
     *
     * @param text tekst przycisku
     * @return przycisk z domyślnym stylem
     */
    private Button createStyledButton(String text) {
        return createStyledButton(text, "#2980B9");
    }

    /**
     * Tworzy przycisk z określonym kolorem tła.
     *
     * @param text  tekst przycisku
     * @param color kolor tła (hex)
     * @return wystylizowany przycisk
     */
    private Button createStyledButton(String text, String color) {
        logger.debug("Tworzenie stylizowanego przycisku: '{}'", text);
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;"
        );

        // Efekt powiększenia po najechaniu
        button.setOnMouseEntered(e -> {
            logger.trace("Najechano na przycisk: '{}'", text);
            ScaleTransition scale = new ScaleTransition(
                    Duration.millis(200), button
            );
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            logger.trace("Zjechano z przycisku: '{}'", text);
            ScaleTransition scale = new ScaleTransition(
                    Duration.millis(200), button
            );
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        });

        return button;
    }

    /**
     * Animacja płynnego pojawiania się elementu.
     */
    private void animateFadeIn(VBox element, int duration) {
        logger.debug("Animowanie efektu fadeIn dla menu");
        FadeTransition fade = new FadeTransition(
                Duration.millis(duration),
                element
        );
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    /**
     * Animacja przesunięcia elementu z góry w dół.
     */
    private void animateSlideDown(VBox element, int duration) {
        logger.debug("Animowanie efektu slideDown dla menu");
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
     * Ustawia centralny panel aplikacji.
     *
     * @param pane widok do wyświetlenia
     */
    public void setCenterPane(Pane pane) {
        logger.debug("Ustawianie nowego panelu centralnego");
        root.setCenter(pane);
    }

    /**
     * Zwraca obiekt Stage przypisany do panelu kierownika.
     *
     * @return główna scena
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Ustawia podany przycisk jako aktywny, resetując styl poprzedniego.
     */
    private void setActiveButton(Button button) {
        String defaultStyle = "-fx-background-color: #2980B9; -fx-text-fill:" +
                " white; -fx-font-weight: bold;";
        String activeStyle  = "-fx-background-color: #1A5276; -fx-text-fill:" +
                " white; -fx-font-weight: bold;";
        if (activeButton != null) {
            activeButton.setStyle(defaultStyle);
        }
        activeButton = button;
        activeButton.setStyle(activeStyle);
    }

}