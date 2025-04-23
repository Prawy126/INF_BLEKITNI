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

/**
 * Klasa reprezentująca graficzny interfejs kasjera.
 * Zawiera przyciski nawigacyjne i animacje wejścia.
 */
public class CashierPanel {

    private BorderPane root;
    private Stage primaryStage;
    private CashierPanelController controller;

    /**
     * Tworzy nowy panel kasjera i wyświetla domyślny widok sprzedaży.
     *
     * @param primaryStage główne okno aplikacji
     */
    public CashierPanel(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.controller = new CashierPanelController(this);
        primaryStage.setTitle("Panel kasjera");

        // Główna struktura układu
        root = new BorderPane();
        root.setPadding(new Insets(10));

        // Lewy panel nawigacyjny
        VBox menu = createMenu();
        root.setLeft(menu);

        // Domyślnie wyświetl ekran sprzedaży
        controller.showSalesScreen();

        // Animacje pojawiania się menu
        animateFadeIn(menu, 1000);
        animateSlideDown(menu, 800);

        // Tworzenie sceny
        Scene scene = new Scene(root, 700, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Tworzy panel nawigacyjny z przyciskami po lewej stronie.
     *
     * @return VBox z przyciskami nawigacyjnymi
     */
    private VBox createMenu() {
        VBox menu = new VBox(15);
        menu.setPadding(new Insets(20));
        menu.setStyle(
                "-fx-background-color: #E0E0E0; "
                        + "-fx-border-radius: 10; "
                        + "-fx-background-radius: 10;"
        );
        menu.setAlignment(Pos.TOP_LEFT);

        // Logo w lewym górnym rogu
        Image image = new Image(getClass().getResourceAsStream("/logo.png"));
        ImageView logo = new ImageView(image);
        logo.setFitWidth(100);
        logo.setFitHeight(100);
        logo.setPreserveRatio(true);

        // Przycisk ekranu sprzedaży
        Button salesButton = createStyledButton("Ekran sprzedaży");
        salesButton.setOnAction(e -> controller.showSalesScreen());

        // Przycisk raportów sprzedaży
        Button reportsButton = createStyledButton("Raporty sprzedaży");
        reportsButton.setOnAction(e -> controller.showSalesReportsPanel());

        // Przycisk zamknięcia zmiany
        Button closeShiftButton = createStyledButton("Zamknięcie zmiany");
        closeShiftButton.setOnAction(e -> controller.showCloseShiftPanel());

        // Przycisk zgłoszenia awarii
        Button issueReportButton = createStyledButton("Zgłoszenie awarii");
        issueReportButton.setOnAction(e -> controller.showIssueReportPanel());

        // Przycisk wniosku o nieobecność
        Button absenceButton = createStyledButton("Złóż wniosek o nieobecność");
        absenceButton.setOnAction(e -> controller.showAbsenceRequestForm());

        // Przycisk wylogowania (czerwony)
        Button logoutButton = createStyledButton("Wyloguj się", "#E74C3C");
        logoutButton.setOnAction(e -> controller.logout());

        menu.getChildren().add(logo);
        menu.getChildren().addAll(
                salesButton,
                reportsButton,
                closeShiftButton,
                issueReportButton,
                absenceButton,
                logoutButton
        );

        return menu;
    }

    /**
     * Tworzy stylizowany przycisk z domyślnym niebieskim kolorem.
     *
     * @param text etykieta przycisku
     * @return gotowy przycisk
     */
    private Button createStyledButton(String text) {
        return createStyledButton(text, "#2980B9");
    }

    /**
     * Tworzy stylizowany przycisk z podanym kolorem.
     *
     * @param text  etykieta przycisku
     * @param color kolor tła przycisku
     * @return gotowy przycisk
     */
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: " + color + "; "
                        + "-fx-text-fill: white; "
                        + "-fx-font-weight: bold;"
        );

        // Animacja powiększenia po najechaniu
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
     * Animuje płynne pojawienie się elementu.
     *
     * @param element  element do animacji
     * @param duration czas trwania w milisekundach
     */
    private void animateFadeIn(VBox element, int duration) {
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
}