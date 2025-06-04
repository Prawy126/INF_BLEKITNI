/*
 * Classname: AdminPanel
 * Version information: 1.2
 * Date: 2025-06-04
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Klasa reprezentująca główny panel administratora.
 * Tworzy GUI, które zawiera panel nawigacyjny oraz różne widoki
 * zarządzania.
 */
public class AdminPanel {
    private static final Logger logger = LogManager.getLogger(AdminPanel.class);
    private BorderPane root;
    private Stage primaryStage;
    private AdminPanelController controller;
    private Image logoImage;
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * Konstruktor panelu administratora.
     *
     * @param stage główne okno aplikacji
     */
    public AdminPanel(Stage stage) {
        logger.info("Tworzenie AdminPanel dla stage: {}", stage);
        this.primaryStage = stage;
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(450);
        this.controller = new AdminPanelController(this);
        logger.debug("Kontroler AdminPanelController utworzony");

        // Asynchroniczne ładowanie logo
        logger.debug("Rozpoczęcie asynchronicznego ładowania logo");
        loadLogoAsync();

        primaryStage.setTitle("Panel administratora");
        logger.debug("Tytuł okna ustawiony na 'Panel administratora'");

        // Główna struktura układu
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: lightblue;");
        logger.debug("Główny BorderPane utworzony");

        // Lewy panel nawigacyjny - tworzony asynchronicznie
        logger.debug("Rozpoczęcie asynchronicznego tworzenia menu");
        createMenuAsync();

        // Ustawienie sceny
        Scene scene = new Scene(root, 700, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
        logger.info("Scena ustawiona i wyświetlona");

        // Domyślnie wyświetl widok użytkowników - asynchronicznie
        Platform.runLater(() -> {
            logger.debug("Wyświetlanie domyślnego widoku zarządzania użytkownikami");
            controller.showUserManagement();
        });
    }

    /**
     * Metoda uruchamiająca asynchroniczne ładowanie obrazu logo.
     * Po pomyślnym załadowaniu ustawia ikonę okna.
     */
    private void loadLogoAsync() {
        Task<Image> task = new Task<>() {
            @Override
            protected Image call() throws Exception {
                logger.debug("Rozpoczęcie ładowania logo w wątku roboczym");
                try {
                    return new Image(Objects.requireNonNull(
                            getClass().getResourceAsStream("/logo.png")
                    ), 100, 100, true, true);
                } catch (Exception e) {
                    logger.error("Błąd podczas ładowania logo: {}", e.getMessage(), e);
                    throw e;
                }
            }
        };

        task.setOnSucceeded(e -> {
            logoImage = task.getValue();
            logger.debug("Logo załadowane pomyślnie");
            primaryStage.getIcons().add(logoImage);
            logger.debug("Logo dodane jako ikona okna");
        });

        task.setOnFailed(e -> {
            logger.error("Nie udało się załadować logo: {}", task.getException().getMessage(), task.getException());
        });

        executor.execute(task);
        logger.debug("Zadanie ładowania logo przesłane do executor");
    }

    /**
     * Metoda uruchamiająca asynchroniczne tworzenie panelu menu.
     * Po zakończeniu tworzenia następuje dodanie menu do głównego
     * panelu oraz uruchomienie animacji.
     */
    private void createMenuAsync() {
        Task<VBox> task = new Task<>() {
            @Override
            protected VBox call() throws Exception {
                logger.debug("Rozpoczęcie tworzenia menu w wątku roboczym");
                try {
                    return createMenu();
                } catch (Exception e) {
                    logger.error("Błąd podczas tworzenia menu: {}", e.getMessage(), e);
                    throw e;
                }
            }
        };

        task.setOnSucceeded(e -> {
            VBox menu = task.getValue();
            logger.debug("Menu utworzone pomyślnie");
            root.setLeft(menu);
            logger.debug("Menu dodane do głównego panelu");

            // Animacje po załadowaniu menu
            logger.debug("Rozpoczęcie animacji menu");
            animateFadeIn(menu, 1000);
            animateSlideDown(menu, 800);
        });

        task.setOnFailed(e -> {
            logger.error("Nie udało się utworzyć menu: {}", task.getException().getMessage(), task.getException());
        });

        executor.execute(task);
        logger.debug("Zadanie tworzenia menu przesłane do executor");
    }

    /**
     * Tworzy panel menu nawigacyjnego z logiem i przyciskami.
     * Przyciskom przypisane są odpowiednie akcje do wyświetlania
     * różnych widoków panelu administratora.
     *
     * @return VBox zawierające elementy menu
     */
    private VBox createMenu() {
        logger.debug("Tworzenie menu nawigacyjnego");
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle(
                "-fx-background-color: #E0E0E0; "
                        + "-fx-border-radius: 10; "
                        + "-fx-background-radius: 10;"
        );
        menu.setAlignment(Pos.TOP_LEFT);

        // Logo w lewym górnym rogu
        logger.debug("Ładowanie obrazu logo dla menu");
        Image image = new Image(
                Objects.requireNonNull(
                        getClass().getResourceAsStream("/logo.png")
                ), 100, 100, true, true
        );
        ImageView logo = new ImageView(image);
        logo.setFitWidth(100);
        logo.setFitHeight(100);
        logo.setPreserveRatio(true);

        // Przyciski
        logger.debug("Tworzenie przycisków menu");
        Button usersButton = createStyledButton("Użytkownicy");
        usersButton.setOnAction(e -> {
            logger.debug("Kliknięto przycisk 'Użytkownicy'");
            controller.showUserManagement();
        });

        Button configButton = createStyledButton("Konfiguracja");
        configButton.setOnAction(e -> {
            logger.debug("Kliknięto przycisk 'Konfiguracja'");
            controller.showConfigPanel();
        });

        Button reportsButton = createStyledButton("Raporty");
        reportsButton.setOnAction(e -> {
            logger.debug("Kliknięto przycisk 'Raporty'");
            controller.showReportsPanel();
        });

        Button issuesButton = createStyledButton("Zgłoszenia");
        issuesButton.setOnAction(e -> {
            logger.debug("Kliknięto przycisk 'Zgłoszenia'");
            controller.showIssuesPanel();
        });

        Button logoutButton = createStyledButton("Wyloguj", "#E74C3C");
        logoutButton.setOnAction(e -> {
            logger.info("Użytkownik wylogowuje się z panelu administratora");
            controller.logout();
        });

        // Dodanie elementów do menu
        logger.debug("Dodawanie elementów do menu");
        menu.getChildren().add(logo);
        menu.getChildren().addAll(
                usersButton,
                configButton,
                reportsButton,
                issuesButton,
                logoutButton);

        logger.debug("Menu utworzone pomyślnie");
        return menu;
    }

    /**
     * Tworzy przycisk z domyślnym kolorem tła #2980B9.
     *
     * @param text opis wyświetlany na przycisku
     * @return stylizowany obiekt Button
     */
    private Button createStyledButton(String text) {
        return createStyledButton(text, "#2980B9");
    }

    /**
     * Tworzy stylizowany przycisk z określonym kolorem tła.
     * Przyciskowi przypisane są efekty powiększenia przy
     * najechaniu kursorem.
     *
     * @param text  opis wyświetlany na przycisku
     * @param color kolor tła przycisku w formacie hex
     * @return stylizowany obiekt Button
     */
    private Button createStyledButton(String text, String color) {
        logger.debug("Tworzenie stylizowanego przycisku: {}", text);
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color
                + "; -fx-text-fill: white; -fx-font-weight: bold;");

        // Efekt powiększenia przy najechaniu kursorem
        button.setOnMouseEntered(e -> {
            logger.trace("Najechano na przycisk: {}", text);
            button.setScaleX(1.1);
            button.setScaleY(1.1);
        });

        button.setOnMouseExited(e -> {
            logger.trace("Zjechano z przycisku: {}", text);
            button.setScaleX(1);
            button.setScaleY(1);
        });

        return button;
    }

    /**
     * Uruchamia animację fadeIn na przekazanym elemencie.
     * Po zakończeniu animacji wyłącza cache.
     *
     * @param element  element do animacji
     * @param duration czas trwania animacji w milisekundach
     */
    private void animateFadeIn(VBox element, int duration) {
        logger.debug("Animowanie efektu fadeIn dla elementu");
        // Włączenie cache dla lepszej wydajności animacji
        element.setCache(true);
        element.setCacheHint(CacheHint.SPEED);

        FadeTransition fade = new FadeTransition(
                Duration.millis(duration), element
        );
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setOnFinished(e -> {
            logger.trace("Animacja fadeIn zakończona");
            // Wyłączenie cache po zakończeniu animacji
            element.setCache(false);
        });
        fade.play();
    }

    /**
     * Uruchamia animację slideDown na przekazanym elemencie.
     * Po zakończeniu animacji wyłącza cache.
     *
     * @param element  element do animacji
     * @param duration czas trwania animacji w milisekundach
     */
    private void animateSlideDown(VBox element, int duration) {
        logger.debug("Animowanie efektu slideDown dla elementu");
        // Włączenie cache dla lepszej wydajności animacji
        element.setCache(true);
        element.setCacheHint(CacheHint.SPEED);

        TranslateTransition slide = new TranslateTransition(
                Duration.millis(duration), element
        );
        slide.setFromY(-50);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_BOTH);
        slide.setOnFinished(e -> {
            logger.trace("Animacja slideDown zakończona");
            // Wyłączenie cache po zakończeniu animacji
            element.setCache(false);
        });
        slide.play();
    }

    /**
     * Ustawia nowy panel centralny w głównym układzie BorderPane.
     *
     * @param pane panel do ustawienia jako centralny
     */
    public void setCenterPane(javafx.scene.layout.Pane pane) {
        logger.debug("Ustawianie nowego panelu centralnego");
        root.setCenter(pane);
    }

    /**
     * Zwraca główne okno aplikacji (Stage).
     *
     * @return obiekt Stage głównego okna
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
