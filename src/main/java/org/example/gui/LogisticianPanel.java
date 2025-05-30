/*
 * Classname: LogisticianPanel
 * Version information: 1.1
 * Date: 2025-04-11
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Klasa reprezentująca panel logistyka w aplikacji GUI.
 */
public class LogisticianPanel {

    private static final Logger logger = LogManager.getLogger(LogisticianPanel.class);
    private BorderPane root;
    private Stage primaryStage;
    private LogisticianPanelController controller;
    private Image logoImage;

    /**
     * Konstruktor klasy LogisticianPanel.
     *
     * @param primaryStage główna scena przypisana do panelu
     */
    public LogisticianPanel(Stage primaryStage) {
        logger.info("Tworzenie LogisticianPanel dla stage: {}", primaryStage);

        this.primaryStage = primaryStage;
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(450);
        this.controller = new LogisticianPanelController(this);
        logger.debug("Kontroler LogisticianPanelController utworzony");

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

        primaryStage.setTitle("Panel logistyka");
        logger.debug("Tytuł okna ustawiony na 'Panel logistyka'");

        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: lightblue;");
        logger.debug("Główny BorderPane utworzony i skonfigurowany");

        logger.debug("Tworzenie menu nawigacyjnego");
        VBox menu = createMenu();
        root.setLeft(menu);

        logger.debug("Wyświetlanie domyślnego widoku raportów magazynowych");
        controller.showInventoryReports(); // domyślny widok

        logger.debug("Uruchamianie animacji menu");
        animateFadeIn(menu, 1000);
        animateSlideDown(menu, 800);

        Scene scene = new Scene(root, 700, 450);
        primaryStage.setScene(scene);
        logger.debug("Scena utworzona i ustawiona");

        logger.info("Wyświetlanie głównego okna panelu logistyka");
        primaryStage.show();
    }

    /**
     * Tworzy menu boczne z logo i przyciskami.
     */
    private VBox createMenu() {
        logger.debug("Tworzenie menu nawigacyjnego");
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setAlignment(Pos.TOP_LEFT);
        menu.setStyle("-fx-background-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10;");

        // logo
        Image image = null;
        try {
            image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/logo.png")
            ));
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

        // przyciski
        Button inventoryButton = createStyledButton("Zarządzanie magazynem");
        inventoryButton.setOnAction(e -> {
            logger.debug("Kliknięto przycisk 'Zarządzanie magazynem'");
            controller.showInventoryManagement();
        });

        Button ordersButton = createStyledButton("Zamówienia");
        ordersButton.setOnAction(e -> {
            logger.debug("Kliknięto przycisk 'Zamówienia'");
            controller.showOrdersPanel();
        });

        Button reportsButton = createStyledButton("Raporty magazynowe");
        reportsButton.setOnAction(e -> {
            logger.debug("Kliknięto przycisk 'Raporty magazynowe'");
            controller.showInventoryReports();
        });

        Button closeShiftBtn   = createStyledButton("Zamknij zmianę", "#E67E22");
        closeShiftBtn.setOnAction(e -> controller.showCloseShiftPanel());

        Button absenceButton = createStyledButton("Złóż wniosek o nieobecność");
        absenceButton.setOnAction(e -> {
            logger.debug("Kliknięto przycisk 'Złóż wniosek o nieobecność'");
            controller.showAbsenceRequestForm();
        });


        Button logoutButton = createStyledButton("Wyloguj", "#E74C3C");
        logoutButton.setOnAction(e -> {
            logger.info("Rozpoczęcie procesu wylogowania");
            controller.logout();
        });

        menu.getChildren().addAll(logo, inventoryButton, ordersButton, reportsButton, absenceButton, closeShiftBtn,  logoutButton);
        logger.debug("Menu utworzone pomyślnie z {} przyciskami", menu.getChildren().size() - 1); // -1 dla logo

        return menu;
    }

    /**
     * Stylizuje przyciski jak w panelu admina.
     */
    private Button createStyledButton(String text) {
        return createStyledButton(text, "#2980B9");
    }

    private Button createStyledButton(String text, String color) {
        logger.debug("Tworzenie stylizowanego przycisku: '{}'", text);
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color
                + "; -fx-text-fill: white; -fx-font-weight: bold;");

        button.setOnMouseEntered(e -> {
            logger.trace("Najechano na przycisk: '{}'", text);
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            logger.trace("Zjechano z przycisku: '{}'", text);
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        });

        return button;
    }

    private void animateFadeIn(VBox element, int duration) {
        logger.debug("Animowanie efektu fadeIn dla menu");
        FadeTransition fade = new FadeTransition(Duration.millis(duration), element);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void animateSlideDown(VBox element, int duration) {
        logger.debug("Animowanie efektu slideDown dla menu");
        TranslateTransition slide = new TranslateTransition(Duration.millis(duration), element);
        slide.setFromY(-50);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_BOTH);
        slide.play();
    }

    public void setCenterPane(javafx.scene.layout.Pane pane) {
        logger.debug("Ustawianie nowego panelu centralnego");
        root.setCenter(pane);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}