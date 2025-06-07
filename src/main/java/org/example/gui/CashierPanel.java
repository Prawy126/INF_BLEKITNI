/*
 * Classname: CashierPanel
 * Version information: 1.3
 * Date: 2025-06-07
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import java.util.Optional;

/**
 * Klasa reprezentująca graficzny interfejs kasjera.
 * Zawiera przyciski nawigacyjne i animacje wejścia.
 */
public class CashierPanel {

    private static final Logger logger = LogManager.getLogger(CashierPanel.class);
    private BorderPane root;
    private Stage primaryStage;
    private CashierPanelController controller;
    private Image logoImage;
    private Button activeButton;

    public CashierPanel(Stage primaryStage) {
        logger.info("Tworzenie CashierPanel dla stage: {}", primaryStage);

        this.primaryStage = primaryStage;
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        this.controller = new CashierPanelController(this);
        logger.debug("Kontroler CashierPanelController utworzony");

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

        primaryStage.setTitle("Panel kasjera");
        logger.debug("Tytuł okna ustawiony na 'Panel kasjera'");

        // Główna struktura układu
        root = new BorderPane();
        root.setPadding(new Insets(10));
        logger.debug("Główny BorderPane utworzony");

        // Lewy panel nawigacyjny
        logger.debug("Tworzenie menu nawigacyjnego");
        VBox menu = createMenu();
        root.setLeft(menu);

        // Domyślnie wyświetl ekran sprzedaży
        logger.debug("Wyświetlanie domyślnego ekranu sprzedaży");
        controller.showSalesScreen();

        // Animacje pojawiania się menu
        logger.debug("Uruchamianie animacji menu");
        animateFadeIn(menu, 1000);
        animateSlideDown(menu, 800);

        // Tworzenie sceny
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        logger.debug("Scena ustawiona");

        // Obsługa próby zamknięcia okna - używamy metod z kontrolera
        primaryStage.setOnCloseRequest(event -> {
            logger.info("Próba zamknięcia okna panelu kasjera");

            // Sprawdź, czy raport został wygenerowany w bieżącej sesji
            // lub wcześniej dzisiaj
            logger.debug("Sprawdzanie statusu raportu dziennego");
            boolean reportGenerated = controller.isReportGeneratedInCurrentSession() ||
                    controller.isDailyReportGeneratedToday();

            if (!reportGenerated) {
                logger.warn("Raport dzienny nie został wygenerowany - zapobieganie zamknięciu");
                event.consume(); // Zapobiega zamknięciu okna

                // Wyświetl alert z pytaniem
                logger.debug("Wyświetlanie alertu ostrzegawczego");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Uwaga");
                alert.setHeaderText("Nie wygenerowano raportu dziennego");
                alert.setContentText("Czy na pewno chcesz zamknąć aplikację bez" +
                        " wygenerowania raportu dziennego?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Użytkownik potwierdził zamknięcie - usuń handler i zamknij
                    logger.info("Użytkownik potwierdził zamknięcie bez raportu");
                    primaryStage.setOnCloseRequest(null);
                    Platform.exit();
                } else {
                    logger.info("Użytkownik anulował zamknięcie");
                }
            } else {
                logger.debug("Raport dzienny został wygenerowany - zezwalanie na zamknięcie");
            }
        });

        logger.info("Wyświetlanie głównego okna panelu kasjera");
        primaryStage.show();
    }

    private VBox createMenu() {
        logger.debug("Tworzenie menu nawigacyjnego");
        VBox menu = new VBox(15);
        menu.setPadding(new Insets(20));
        menu.setStyle(
                "-fx-background-color: #E0E0E0; "
                        + "-fx-border-radius: 10; "
                        + "-fx-background-radius: 10;"
        );
        menu.setAlignment(Pos.TOP_LEFT);

        // Logo w lewym górnym rogu
        Image image = null;
        try {
            image = new Image(getClass().getResourceAsStream("/logo.png"));
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

        // Przycisk ekranu sprzedaży
        Button salesButton = createStyledButton("Ekran sprzedaży");
        salesButton.setOnAction(e -> {
            logger.debug("Kliknięto przycisk 'Ekran sprzedaży'");
            setActiveButton(salesButton);
            controller.showSalesScreen();
        });

        // Przycisk raportów sprzedaży
        Button reportsButton = createStyledButton("Raporty sprzedaży");
        reportsButton.setOnAction(e -> {
            logger.debug("Kliknięto przycisk 'Raporty sprzedaży'");
            setActiveButton(reportsButton);
            controller.showSalesReportsPanel();
        });

        // Przycisk zamknięcia zmiany
        Button closeShiftButton = createStyledButton("Zamknięcie zmiany");
        closeShiftButton.setOnAction(e -> {
            logger.debug("Kliknięto przycisk 'Zamknięcie zmiany'");
            setActiveButton(closeShiftButton);
            controller.showCloseShiftPanel();
        });

        // Przycisk zgłoszenia awarii
        Button issueReportButton = createStyledButton("Zgłoszenie awarii");
        issueReportButton.setOnAction(e -> {
            logger.debug("Kliknięto przycisk 'Zgłoszenie awarii'");
            setActiveButton(issueReportButton);
            controller.showIssueReportPanel();
        });

        // Przycisk wniosku o nieobecność
        Button absenceButton = createStyledButton("Złóż wniosek o nieobecność");
        absenceButton.setOnAction(e -> {
            logger.debug("Kliknięto przycisk 'Złóż wniosek o nieobecność'");
            setActiveButton(absenceButton);
            controller.showAbsenceRequestForm();
        });

        // Przycisk wylogowania (czerwony)
        Button logoutButton = createStyledButton("Wyloguj się", "#E74C3C");
        logoutButton.setOnAction(e -> {
            logger.info("Rozpoczęcie procesu wylogowania");
            // Używamy flagi z kontrolera zamiast lokalnej flagi
            controller.logout();
        });

        menu.getChildren().add(logo);
        menu.getChildren().addAll(
                salesButton,
                reportsButton,
                closeShiftButton,
                issueReportButton,
                absenceButton,
                logoutButton
        );

        setActiveButton(salesButton);
        logger.debug("Menu nawigacyjne utworzone pomyślnie");
        return menu;
    }

    /**
     * Tworzy stylizowany przycisk z domyślnym niebieskim kolorem.
     *
     * @param text etykieta przycisku
     * @return gotowy przycisk
     */
    Button createStyledButton(String text) {
        return createStyledButton(text, "#2980B9");
    }

    /**
     * Tworzy stylizowany przycisk z podanym kolorem.
     *
     * @param text  etykieta przycisku
     * @param color kolor tła przycisku
     * @return gotowy przycisk
     */
    Button createStyledButton(String text, String color) {
        logger.debug("Tworzenie stylizowanego przycisku: '{}'", text);
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: " + color + "; "
                        + "-fx-text-fill: white; "
                        + "-fx-font-weight: bold;"
        );

        // Animacja powiększenia po najechaniu
        button.setOnMouseEntered(e -> {
            logger.trace("Najechano na przycisk: '{}'", text);
            ScaleTransition scale = new ScaleTransition(
                    Duration.millis(200),
                    button
            );
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            logger.trace("Zjechano z przycisku: '{}'", text);
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
     * Animuje płynne pojawienie się elementu.
     *
     * @param element  element do animacji
     * @param duration czas trwania w milisekundach
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
     * Animuje przesunięcie elementu z góry w dół.
     *
     * @param element  element do animacji
     * @param duration czas trwania w milisekundach
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
     * Ustawia środkowy panel aplikacji.
     *
     * @param pane element do wyświetlenia w centrum
     */
    public void setCenterPane(Pane pane) {
        logger.debug("Ustawianie nowego panelu centralnego");
        root.setCenter(pane);
    }

    /**
     * Zwraca główny Stage przypisany do aplikacji.
     *
     * @return główna scena aplikacji
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public CashierPanelController getController() {
        return this.controller;
    }

    /**
     * Resetuje handler zamknięcia okna.
     */
    public void resetCloseRequestHandler() {
        logger.debug("Resetowanie handlera zamknięcia okna");
        primaryStage.setOnCloseRequest(null);
    }

    /**
     * Ustawia przycisk jako aktywny, przywracając styl poprzedniego
     * i nadając ciemniejszy odcień nowemu.
     */
    private void setActiveButton(Button button) {
        String defaultStyle = "-fx-background-color: #2980B9;" +
                " -fx-text-fill: white; -fx-font-weight: bold;";
        String activeStyle  = "-fx-background-color: #1A5276;" +
                " -fx-text-fill: white; -fx-font-weight: bold;";
        if (activeButton != null) {
            activeButton.setStyle(defaultStyle);
        }
        activeButton = button;
        activeButton.setStyle(activeStyle);
    }
}