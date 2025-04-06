/*
 * Classname: HelloApplication
 * Version information: 1.0
 * Date: 2025-04-06
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.sys.Admin;
import org.example.sys.Employee;
import org.example.sys.Menager;

import java.util.Objects;

/**
 * Główna klasa uruchamiająca aplikację GUI dla hipermarketu Stonka.
 * Zawiera logikę interfejsu logowania i przekierowywania użytkownika
 * na odpowiedni panel w zależności od roli.
 */
public class HelloApplication extends Application {

    // Przykładowe dane logowania użytkowników
    Admin admin = new Admin("Jan", "Kowalski", 40,"admin", "admin");
    Employee employee = new Employee(
            "Jan", "Nowak",25, "Kraków",
            "kasjer", "kasjer", "kasjer", "IT", "Developer", 5000
    );
    Menager menager = new Menager(
            "Jan", "Waiderko",40, "Rzeszów",
            "kierownik", "kierownik", "kierownik", "IT", "Developer", 5000
    );

    /**
     * Punkt wejścia do aplikacji JavaFX.
     *
     * @param primaryStage główna scena aplikacji
     */
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: lightblue; -fx-padding: 30;");

        Image image = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/logo.png")
        ));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);
        primaryStage.getIcons().add(image);

        Label titleLabel = new Label("Stonka najlepszy hipermarket");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setOpacity(0);

        Label welcomeLabel = new Label("Witamy w Stonce");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
        welcomeLabel.setOpacity(0);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setTranslateY(-50);

        Label loginLabel = new Label("Login");
        TextField loginField = new TextField();
        loginField.setPromptText("Tutaj podaj login");
        loginField.setStyle("-fx-background-color: #FFD966; -fx-padding: 5;");

        Label passwordLabel = new Label("Hasło");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Tutaj podaj hasło");
        passwordField.setStyle("-fx-background-color: #FFD966; -fx-padding: 5;");

        grid.add(loginLabel, 0, 0);
        grid.add(loginField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);

        HBox topButtonBox = new HBox(15);
        topButtonBox.setAlignment(Pos.CENTER);

        Button loginButton = new Button("Zaloguj");
        styleButton(loginButton, "#2980B9");
        loginButton.setOnAction(e -> handleLogin(
                loginField.getText(),
                passwordField.getText(),
                root
        ));

        Button resetPasswordButton = new Button("Resetowanie hasła");
        styleButton(resetPasswordButton, "#F39C12");
        resetPasswordButton.setOnAction(e -> showResetPasswordWindow());

        topButtonBox.getChildren().addAll(loginButton, resetPasswordButton);

        HBox bottomButtonBox = new HBox(15);
        bottomButtonBox.setAlignment(Pos.CENTER);

        Button cvButton = new Button("Złóż CV");
        styleButton(cvButton, "#1F618D");

        Button exitButton = new Button("Wyjście");
        styleButton(exitButton, "#E74C3C");
        exitButton.setOnAction(e -> exitApplication());

        bottomButtonBox.getChildren().addAll(cvButton, exitButton);

        root.getChildren().addAll(
                imageView,
                titleLabel,
                welcomeLabel,
                grid,
                topButtonBox,
                bottomButtonBox
        );

        animateFadeIn(titleLabel, 1000);
        animateFadeIn(welcomeLabel, 1200);
        animateSlideDown(grid, 1000);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stonka - Logowanie");
        primaryStage.show();
    }

    /**
     * Obsługuje logikę logowania i przekierowuje na odpowiedni panel.
     *
     * @param enteredUsername login
     * @param enteredPassword hasło
     * @param root kontener GUI
     */
    private void handleLogin(
            String enteredUsername,
            String enteredPassword,
            VBox root
    ) {
        if (enteredUsername.equals(admin.getEmail())
                && enteredPassword.equals(admin.getPassword())) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Sukces",
                    "Zalogowano pomyślnie!",
                    "Witaj, " + enteredUsername + "!"
            );

            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();
            Stage adminStage = new Stage();
            new AdminPanel(adminStage);

        } else if (enteredUsername.equals(employee.getEmail())
                && enteredPassword.equals(employee.getEmail())) {

            // FIXME: Walidacja może być nie poprawna
            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Sukces",
                    "Zalogowano pomyślnie!",
                    "Witaj, " + enteredUsername + "!"
            );

            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();
            Stage cashierStage = new Stage();
            new CashierPanel(cashierStage);

        } else if (enteredUsername.equals(menager.getEmail())
                && enteredPassword.equals(menager.getPassword())) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Sukces",
                    "Zalogowano pomyślnie!",
                    "Witaj, " + enteredUsername + "!"
            );

            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();
            Stage managerStage = new Stage();
            new ManagerPanel(managerStage);

        } else {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd",
                    "Nieprawidłowe dane logowania!",
                    "Spróbuj ponownie."
            );
        }
    }

    /**
     * Wyświetla komunikat typu Alert.
     */
    private void showAlert(
            Alert.AlertType type,
            String title,
            String header,
            String content
    ) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Animuje płynne pojawienie się napisu.
     */
    private void animateFadeIn(Label label, int duration) {
        FadeTransition fade = new FadeTransition(
                Duration.millis(duration),
                label
        );
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    /**
     * Animuje przesunięcie formularza w dół.
     */
    private void animateSlideDown(GridPane grid, int duration) {
        TranslateTransition slide = new TranslateTransition(
                Duration.millis(duration),
                grid
        );
        slide.setFromY(-50);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_BOTH);
        slide.play();
    }

    /**
     * Nadaje styl oraz animacje przyciskowi.
     */
    private void styleButton(Button button, String color) {
        button.setStyle(
                "-fx-background-color: " + color + "; "
                        + "-fx-text-fill: black; "
                        + "-fx-font-weight: bold;"
        );
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
    }

    /**
     * Zamyka aplikację po potwierdzeniu użytkownika.
     */
    private void exitApplication() {
        Alert confirmExit = new Alert(Alert.AlertType.CONFIRMATION);
        confirmExit.setTitle("Potwierdzenie wyjścia");
        confirmExit.setHeaderText("Czy na pewno chcesz wyjść?");
        confirmExit.setContentText("Zmiany mogą nie zostać zapisane.");

        confirmExit.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Platform.exit();
            }
        });
    }

    /**
     * Wyświetla okno resetowania hasła.
     */
    private void showResetPasswordWindow() {
        Stage resetStage = new Stage();
        resetStage.setTitle("Resetowanie hasła");

        VBox resetLayout = new VBox(10);
        resetLayout.setPadding(new Insets(20));
        resetLayout.setAlignment(Pos.CENTER);

        Label emailLabel = new Label("Podaj swój email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        Button sendCodeButton = new Button("Wyślij kod odzyskiwania");
        sendCodeButton.setOnAction(e -> handleSendResetCode(
                emailField.getText()
        ));

        resetLayout.getChildren().addAll(emailLabel, emailField, sendCodeButton);

        Scene resetScene = new Scene(resetLayout, 300, 200);
        resetStage.setScene(resetScene);
        resetStage.show();
    }

    /**
     * Obsługuje wysyłkę kodu do resetowania hasła.
     */
    private void handleSendResetCode(String email) {
        showAlert(
                Alert.AlertType.INFORMATION,
                "Kod wysłany",
                "Kod odzyskiwania został wysłany na email",
                "Proszę sprawdzić swoją skrzynkę."
        );
    }

    /**
     * Główna metoda uruchamiająca aplikację.
     */
    public static void main(String[] args) {
        org.example.database.DatabaseInitializer.initialize();
        launch(args);
    }
}