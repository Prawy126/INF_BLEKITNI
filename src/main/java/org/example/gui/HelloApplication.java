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

import java.io.File;
import java.util.Objects;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Główne tło
        VBox root = new VBox(20); // Zwiększamy odstęp między sekcjami
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

        // Dodanie pól do siatki (przeniesienie etykiet nad pola)
        grid.add(loginLabel, 0, 0);
        grid.add(loginField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);

        // Przyciski logowania i resetowania hasła
        HBox topButtonBox = new HBox(15);
        topButtonBox.setAlignment(Pos.CENTER);

        Button loginButton = new Button("Zaloguj");
        styleButton(loginButton, "#2980B9");
        loginButton.setOnAction(e -> handleLogin(loginField.getText(), passwordField.getText(), root));

        Button resetPasswordButton = new Button("Resetowanie hasła");
        styleButton(resetPasswordButton, "#F39C12");
        resetPasswordButton.setOnAction(e -> showResetPasswordWindow());

        topButtonBox.getChildren().addAll(loginButton, resetPasswordButton);

        // Przyciski "Złóż CV" i "Wyjście"
        HBox bottomButtonBox = new HBox(15);
        bottomButtonBox.setAlignment(Pos.CENTER);

        Button cvButton = new Button("Złóż CV");
        styleButton(cvButton, "#1F618D");

        Button exitButton = new Button("Wyjście");
        styleButton(exitButton, "#E74C3C");
        exitButton.setOnAction(e -> exitApplication());

        bottomButtonBox.getChildren().addAll(cvButton, exitButton);

        // Układ główny
        root.getChildren().addAll(imageView, titleLabel, welcomeLabel, grid, topButtonBox, bottomButtonBox);

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

    // Wyświetlanie formularza składania CV
    private void showCVForm(Stage primaryStage) {
        // Tworzenie nowego okna
        Stage cvStage = new Stage();
        cvStage.setTitle("Formularz składania CV");

        // Układ formularza
        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-padding: 20;");
        vbox.setAlignment(Pos.CENTER);

        // Pola formularza
        TextField nameField = new TextField();
        nameField.setPromptText("Imię i nazwisko");

        TextField emailField = new TextField();
        emailField.setPromptText("E-mail");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Numer telefonu");

        TextArea experienceField = new TextArea();
        experienceField.setPromptText("Doświadczenie zawodowe");
        experienceField.setPrefRowCount(4);

        Button submitButton = new Button("Wyślij CV");
        submitButton.setOnAction(e -> {
            String email = emailField.getText();
            String phone = phoneField.getText();

            // Walidacja e-maila
            if (!isEmailValid(email)) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nieprawidłowy format e-maila!", "Spróbuj ponownie.");
                return;
            }

            // Walidacja numeru telefonu
            if (!isPhoneNumberValid(phone)) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nieprawidłowy numer telefonu!", "Spróbuj ponownie.");
                return;
            }

            // Akcja składania CV
            showAlert(Alert.AlertType.INFORMATION, "Sukces", "CV zostało wysłane!", "Dziękujemy za przesłanie CV.");
            cvStage.close();
        });

        // Dodanie wszystkich elementów do formularza
        vbox.getChildren().addAll(nameField, emailField, phoneField, experienceField, submitButton);

        // Tworzenie i ustawienie sceny
        Scene cvScene = new Scene(vbox, 400, 400);
        cvStage.setScene(cvScene);
        cvStage.show();
    }

    // Walidacja e-maila
    private boolean isEmailValid(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    // Walidacja numeru telefonu (numer może zawierać spacje)
    private boolean isPhoneNumberValid(String phoneNumber) {
        String phoneRegex = "^(\\+48\\s?)?\\d{3}\\s?\\d{3}\\s?\\d{3}$";
        return phoneNumber.matches(phoneRegex);
    }

    // Okno resetowania hasła
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
        sendCodeButton.setOnAction(e -> handleSendResetCode(emailField.getText()));

        resetLayout.getChildren().addAll(emailLabel, emailField, sendCodeButton);

        Scene resetScene = new Scene(resetLayout, 300, 200);
        resetStage.setScene(resetScene);
        resetStage.show();
    }

    private void handleSendResetCode(String email) {
        // W tym miejscu można dodać logikę wysyłania kodu resetującego hasło
        showAlert(Alert.AlertType.INFORMATION, "Kod wysłany", "Kod odzyskiwania został wysłany na email", "Proszę sprawdzić swoją skrzynkę.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
