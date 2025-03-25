package org.example.gui;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
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

import java.io.File;
import java.util.Objects;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Główne tło
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: lightblue; -fx-padding: 30;");

        // **Dodanie obrazka**
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/logo.png")));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(150); // Dostosuj szerokość
        imageView.setPreserveRatio(true);
        primaryStage.getIcons().add(image);

        // Nagłówek
        Label titleLabel = new Label("Stonka najlepszy hipermarket");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setOpacity(0); // Na start ukryte

        Label welcomeLabel = new Label("Witamy w Stonce");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
        welcomeLabel.setOpacity(0); // Na start ukryte

        // Kontener na pola logowania
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setTranslateY(-50); // Pozycja startowa przed animacją

        // Login
        Label loginLabel = new Label("Login");
        TextField loginField = new TextField();
        loginField.setPromptText("Tutaj podaj login");
        loginField.setStyle("-fx-background-color: #FFD966; -fx-padding: 5;");

        // Hasło
        Label passwordLabel = new Label("Hasło");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Tutaj podaj hasło");
        passwordField.setStyle("-fx-background-color: #FFD966; -fx-padding: 5;");

        // Dodanie pól do siatki
        grid.add(loginLabel, 0, 0);
        grid.add(loginField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);

        // Przyciski
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button exitButton = new Button("Wyjście");
        styleButton(exitButton, "#E74C3C");
        exitButton.setOnAction(e -> exitApplication());

        Button loginButton = new Button("Zaloguj");
        styleButton(loginButton, "#2980B9");
        loginButton.setOnAction(e -> handleLogin(loginField.getText(), passwordField.getText(), root));

        Button cvButton = new Button("Złóż CV");
        styleButton(cvButton, "#1F618D");

        buttonBox.getChildren().addAll(exitButton, loginButton, cvButton);

        // Układ główny
        root.getChildren().addAll(imageView, titleLabel, welcomeLabel, grid, buttonBox);

        // Animacje
        animateFadeIn(titleLabel, 1000);
        animateFadeIn(welcomeLabel, 1200);
        animateSlideDown(grid, 1000);

        // Ustawienia sceny
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stonka - Logowanie");
        primaryStage.show();
    }

    Admin admin = new Admin("Jan", "Kowalski", "admin", "admin");
    Employee employee = new Employee("Jan", 25, "Kraków", "kasjer", "kasjer", "kasjer", "IT", "Developer", 5000);
    Menager menager = new Menager("Jan", 40, "Rzeszów","kierownik", "kierownik", "kierownik", "IT", "Developer", 5000);

    private void handleLogin(String enteredUsername, String enteredPassword, VBox root) {
        if (enteredUsername.equals(admin.getEmail()) && enteredPassword.equals(admin.getPassword())) {
            showAlert(Alert.AlertType.INFORMATION, "Sukces", "Zalogowano pomyślnie!", "Witaj, " + enteredUsername + "!");
            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();
            Stage adminStage = new Stage();
            new AdminPanel(adminStage);

        } else if (enteredUsername.equals(employee.getEmail()) && enteredPassword.equals(employee.getEmail())) {
            showAlert(Alert.AlertType.INFORMATION, "Sukces", "Zalogowano pomyślnie!", "Witaj, " + enteredUsername + "!");
            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();
            Stage cashierStage = new Stage();
            new CashierPanel(cashierStage);

        } else if (enteredUsername.equals(menager.getEmail()) && enteredPassword.equals(menager.getPassword())) {
            showAlert(Alert.AlertType.INFORMATION, "Sukces", "Zalogowano pomyślnie!", "Witaj, " + enteredUsername + "!");
            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();
            Stage managerStage = new Stage();
            new ManagerPanel(managerStage);

        } else {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nieprawidłowe dane logowania!", "Spróbuj ponownie.");
        }
    }

    // Funkcja do wyświetlania komunikatów
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Animacja zanikania i pojawiania
    private void animateFadeIn(Label label, int duration) {
        FadeTransition fade = new FadeTransition(Duration.millis(duration), label);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    // Animacja przesuwania w dół
    private void animateSlideDown(GridPane grid, int duration) {
        TranslateTransition slide = new TranslateTransition(Duration.millis(duration), grid);
        slide.setFromY(-50);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_BOTH);
        slide.play();
    }

    // Efekt powiększenia przycisku po najechaniu
    private void styleButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: black; -fx-font-weight: bold;");
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
    }

    // Funkcja zamykania aplikacji
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

    public static void main(String[] args) {
        launch(args);
    }
}