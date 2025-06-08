/*
 * Classname: HelloApplication
 * Version information: 1.6
 * Date: 2025-06-07
 * Copyright notice: © BŁĘKITNI
 */


package org.example.gui;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.Interpolator;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos; 
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.DatabaseErrorHandler;
import org.example.database.EMFProvider;
import org.example.database.UserRepository;
import org.example.sys.ConfigPdf;
import org.example.sys.Employee;
import org.example.sys.Login;
import org.example.sys.PasswordHasher;
import org.example.utils.AppPaths;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Główna klasa uruchamiająca aplikację GUI dla hipermarketu Stonka.
 * Zawiera logikę interfejsu logowania i przekierowywania użytkownika
 * na odpowiedni panel w zależności od roli.
 */
public class HelloApplication extends Application {

    // Flaga wskazująca, czy wystąpił błąd krytyczny
    private static final AtomicBoolean criticalErrorOccurred =
            new AtomicBoolean(false);
    static {
        try {
            // AppPaths zostanie zainicjalizowane automatycznie przez swój statyczny blok
            // i ustawi właściwość app.logs.dir
            System.out.println("Katalog logów aplikacji: " + AppPaths.getLogsDirectory());
        } catch (Exception e) {
            System.err.println("BŁĄD podczas inicjalizacji ścieżek aplikacji: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static final Logger logger =
            LogManager.getLogger(HelloApplication.class);
    private static String errorMessage = "";
    private String resetEmail;

    /**
     * Punkt wejścia do aplikacji JavaFX.
     *
     * @param primaryStage główna scena aplikacji
     */
    public static void showLoginScreen(Stage primaryStage) {
        try {
            logger.debug("Wywołano showLoginScreen");

            // Usuwamy wszystkie handlery zdarzeń z primaryStage
            primaryStage.setOnCloseRequest(null);

            // Tworzenie nowego obiektu HelloApplication
            HelloApplication app = new HelloApplication();

            // Uruchomienie metody start z podanym Stage
            app.start(primaryStage);
        } catch (Exception e) {
            logger.error("BŁĄD w showLoginScreen: " + e.getMessage());
            e.printStackTrace();
            showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd",
                    "Wystąpił błąd podczas wyświetlania ekranu logowania",
                    e.getMessage()
            );
        }
    }

    @Override
    public void start(Stage primaryStage) {
        logger.debug("Rozpoczęcie metody start()");

        if (criticalErrorOccurred.get()) {
            logger.error("Wykryto błąd krytyczny, wyświetlam komunikat o błędzie");
            showDatabaseErrorAndExit(primaryStage, errorMessage);
            return;
        }

        try {
            logger.debug("Inicjalizacja ConfigPdf");
            ConfigPdf configPdf = null;
            try {
                configPdf = new ConfigPdf();
                logger.debug("ConfigPdf zainicjalizowany pomyślnie");
            } catch (Exception e) {
                logger.warn("Błąd podczas inicjalizacji ConfigPdf: {}", e.getMessage(), e);
            }

            logger.debug("Tworzenie głównego kontenera VBox");
            VBox root = new VBox(20);
            root.setAlignment(Pos.CENTER);
            root.setStyle("-fx-background-color: lightblue; -fx-padding: 30;");

            logger.debug("Ładowanie obrazu logo");
            Image image = null;
            try {
                InputStream logoStream = getClass().getResourceAsStream("/logo.png");
                if (logoStream != null) {
                    image = new Image(logoStream);
                    logger.debug("Obraz logo załadowany pomyślnie");
                } else {
                    logger.error("Nie znaleziono pliku logo.png w zasobach");
                    InputStream defaultLogoStream =
                            getClass().getResourceAsStream("/default_logo.png");
                    if (defaultLogoStream != null) {
                        image = new Image(defaultLogoStream);
                        logger.debug("Użyto domyślnego obrazu logo");
                    } else {
                        image = new Image("data:image/png;base64,iVBORw0KGgoAAAANS" +
                                "UhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=");
                        logger.debug("Użyto pustego obrazu logo");
                    }
                }
            } catch (Exception e) {
                logger.error("BŁĄD podczas ładowania logo: {}", e.getMessage(), e);
                image = new Image("data:image/png;base64,iVBORw0KGgoAAAANS" +
                        "UhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=");
                logger.debug("Użyto pustego obrazu logo po błędzie");
            }

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(150);
            imageView.setPreserveRatio(true);

            try {
                primaryStage.getIcons().add(image);
                logger.debug("Dodano ikonę do primaryStage");
            } catch (Exception e) {
                logger.warn("Nie można ustawić ikony aplikacji: {}", e.getMessage());
            }

            logger.debug("Tworzenie etykiet");
            Label titleLabel = new Label("Stonka najlepszy hipermarket");
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            titleLabel.setOpacity(0);

            Label welcomeLabel = new Label("Witamy w Stonce");
            welcomeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
            welcomeLabel.setOpacity(0);

            logger.debug("Tworzenie siatki formularza");
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

            // Pozwól na logowanie klawiszem Enter z pola hasła
            passwordField.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ENTER:
                        logger.debug("ENTER wciśnięty – próba logowania");
                        try {
                            Login.attemptLogin(
                                    loginField.getText(),
                                    passwordField.getText(),
                                    root
                            );
                        } catch (Exception ex) {
                            logger.error("BŁĄD podczas logowania przez ENTER: {}", ex.getMessage(), ex);
                            showAlert(
                                    Alert.AlertType.ERROR,
                                    "Błąd logowania",
                                    "Nie można zalogować",
                                    "Wystąpił błąd podczas próby logowania: " + ex.getMessage()
                            );
                        }
                        break;
                }
            });

            grid.add(loginLabel, 0, 0);
            grid.add(loginField, 1, 0);
            grid.add(passwordLabel, 0, 1);
            grid.add(passwordField, 1, 1);

            logger.debug("Tworzenie przycisków");
            HBox topButtonBox = new HBox(15);
            topButtonBox.setAlignment(Pos.CENTER);

            Button loginButton = new Button("Zaloguj");
            styleButton(loginButton, "#2980B9");
            loginButton.setOnAction(e -> {
                logger.debug("Kliknięto przycisk Zaloguj");
                try {
                    Login.attemptLogin(
                            loginField.getText(),
                            passwordField.getText(),
                            root
                    );
                } catch (Exception ex) {
                    logger.error("BŁĄD podczas logowania: {}", ex.getMessage(), ex);
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Błąd logowania",
                            "Nie można zalogować",
                            "Wystąpił błąd podczas próby logowania: " + ex.getMessage()
                    );
                }
            });

            Button resetPasswordButton = new Button("Resetowanie hasła");
            styleButton(resetPasswordButton, "#F39C12");
            resetPasswordButton.setOnAction(e -> {
                logger.debug("Kliknięto przycisk Resetowanie hasła");
                try {
                    showResetPasswordWindow();
                } catch (Exception ex) {
                    logger.error("BŁĄD podczas otwierania okna resetowania hasła: {}",
                            ex.getMessage(), ex);
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Błąd",
                            "Nie można otworzyć okna resetowania hasła",
                            ex.getMessage()
                    );
                }
            });

            topButtonBox.getChildren().addAll(loginButton, resetPasswordButton);

            HBox bottomButtonBox = new HBox(15);
            bottomButtonBox.setAlignment(Pos.CENTER);

            Button exitButton = new Button("Wyjście");
            styleButton(exitButton, "#E74C3C");
            exitButton.setOnAction(e -> {
                logger.debug("Kliknięto przycisk Wyjście");
                exitApplication();
            });

            bottomButtonBox.getChildren().addAll(exitButton);

            logger.debug("Dodawanie elementów do głównego kontenera");
            root.getChildren().addAll(
                    imageView,
                    titleLabel,
                    welcomeLabel,
                    grid,
                    topButtonBox,
                    bottomButtonBox
            );

            logger.debug("Konfiguracja animacji");
            animateFadeIn(titleLabel, 1000);
            animateFadeIn(welcomeLabel, 1200);
            animateSlideDown(grid, 1000);

            logger.debug("Tworzenie sceny");
            Scene scene = new Scene(root, 600, 500);

            logger.debug("Konfiguracja primaryStage");
            primaryStage.setScene(scene);
            primaryStage.setTitle("Stonka - Logowanie");
            primaryStage.setMinWidth(700);
            primaryStage.setMinHeight(450);

            logger.debug("Wywoływanie primaryStage.show()");
            primaryStage.show();
            logger.debug("primaryStage.show() wykonane");

        } catch (Exception e) {
            logger.error("BŁĄD KRYTYCZNY w start(): {}", e.getMessage(), e);
            showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd",
                    "Wystąpił błąd podczas uruchamiania aplikacji",
                    e.getMessage()
            );
            Platform.exit();
        }

        logger.debug("Koniec metody start()");
    }

    /**
     * Wyświetla komunikat o błędzie bazy danych i zamyka aplikację.
     *
     * @param stage główna scena aplikacji
     * @param message komunikat błędu
     */
    private void showDatabaseErrorAndExit(Stage stage, String message) {
        logger.debug("Wyświetlanie komunikatu o błędzie bazy danych");

        VBox errorBox = new VBox(20);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setPadding(new Insets(30));
        errorBox.setStyle("-fx-background-color: #FFEBEE;");

        Label errorTitle = new Label("Błąd połączenia z bazą danych");
        errorTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        errorTitle.setStyle("-fx-text-fill: #C62828;");

        Label errorDetails =
                new Label("Nie można uruchomić aplikacji z powodu problemów z bazą danych:");
        errorDetails.setFont(Font.font("Arial", 14));

        TextArea errorText = new TextArea(message);
        errorText.setEditable(false);
        errorText.setWrapText(true);
        errorText.setPrefHeight(100);
        errorText.setStyle("-fx-control-inner-background: #FFEBEE;" +
                " -fx-border-color: #C62828;");

        Label instructionLabel = new Label("Sprawdź czy:");
        instructionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        VBox instructionBox = new VBox(5);
        instructionBox.getChildren().addAll(
                new Label("• Serwer bazy danych jest uruchomiony"),
                new Label("• Dane dostępowe do bazy są poprawne"),
                new Label("• Baza danych istnieje i jest dostępna"),
                new Label("• Firewall nie blokuje połączenia")
        );

        Button exitButton = new Button("Zamknij aplikację");
        exitButton.setStyle("-fx-background-color: #C62828; -fx-text-fill: white;");
        exitButton.setOnAction(e -> Platform.exit());

        errorBox.getChildren().addAll(
                errorTitle,
                errorDetails,
                errorText,
                instructionLabel,
                instructionBox,
                exitButton
        );

        Scene errorScene = new Scene(errorBox, 600, 450);
        stage.setScene(errorScene);
        stage.setTitle("Stonka - Błąd krytyczny");


        logger.debug("Wyświetlanie okna błędu bazy danych");
        stage.show();
    }

    /**
     * Wyświetla komunikat typu Alert.
     */
    public static void showAlert(
            Alert.AlertType type,
            String title,
            String header,
            String content
    ) {
        logger.debug("Wyświetlanie alertu: {} - {}", title, header);
        try {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        } catch (Exception e) {
            logger.error("BŁĄD podczas wyświetlania alertu: {}", e.getMessage(), e);
            logger.error("ALERT [{}]: {} - {}\n{}", type, title, header, content);
        }
    }

    /**
     * Animuje płynne pojawienie się napisu.
     */
    private void animateFadeIn(Label label, int duration) {
        logger.debug("Animacja fadeIn dla: {}", label.getText());
        try {
            FadeTransition fade = new FadeTransition(
                    Duration.millis(duration),
                    label
            );
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        } catch (Exception e) {
            logger.warn("Błąd animacji fadeIn: \" + e.getMessage()");
            // Ustaw widoczność bez animacji
            label.setOpacity(1);
        }
    }

    /**
     * Animuje przesunięcie formularza w dół.
     */
    private void animateSlideDown(GridPane grid, int duration) {
        logger.debug("Animacja slideDown dla formularza");
        try {
            TranslateTransition slide = new TranslateTransition(
                    Duration.millis(duration),
                    grid
            );
            slide.setFromY(-50);
            slide.setToY(0);
            slide.setInterpolator(Interpolator.EASE_BOTH);
            slide.play();
        } catch (Exception e) {
            logger.warn("Błąd animacji slideDown: \" + e.getMessage()");
            // Ustaw pozycję bez animacji
            grid.setTranslateY(0);
        }
    }

    /**
     * Nadaje styl oraz animacje przyciskowi.
     */
    private void styleButton(Button button, String color) {
        logger.debug("Stylizacja przycisku: {}", button.getText());
        try {
            button.setStyle(
                    "-fx-background-color: " + color + "; "
                            + "-fx-text-fill: black; "
                            + "-fx-font-weight: bold;"
            );
            button.setOnMouseEntered(e -> {
                try {
                    ScaleTransition scale = new ScaleTransition(
                            Duration.millis(200),
                            button
                    );
                    scale.setToX(1.1);
                    scale.setToY(1.1);
                    scale.play();
                } catch (Exception ex) {
                    System.err.println("OSTRZEŻENIE: Błąd animacji przycisku (mouseEntered): "
                            + ex.getMessage());
                }
            });
            button.setOnMouseExited(e -> {
                try {
                    ScaleTransition scale = new ScaleTransition(
                            Duration.millis(200),
                            button
                    );
                    scale.setToX(1);
                    scale.setToY(1);
                    scale.play();
                } catch (Exception ex) {
                    System.err.println("OSTRZEŻENIE: Błąd animacji przycisku (mouseExited): "
                            + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("OSTRZEŻENIE: Błąd podczas stylizacji przycisku: "
                    + e.getMessage());
            // Ustaw podstawowy styl bez animacji
            button.setStyle("-fx-background-color: " + color + ";");
        }
    }

    /**
     * Zamyka aplikację po potwierdzeniu użytkownika.
     */
    private void exitApplication() {
        logger.debug("Próba zamknięcia aplikacji");
        try {
            Alert confirmExit = new Alert(Alert.AlertType.CONFIRMATION);
            confirmExit.setTitle("Potwierdzenie wyjścia");
            confirmExit.setHeaderText("Czy na pewno chcesz wyjść?");
            confirmExit.setContentText("Zmiany mogą nie zostać zapisane.");

            confirmExit.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    System.out.println("DEBUG: Zamykanie aplikacji potwierdzone");
                    Platform.exit();
                } else {
                    System.out.println("DEBUG: Anulowano zamykanie aplikacji");
                }
            });
        } catch (Exception e) {
            System.err.println("BŁĄD podczas zamykania aplikacji: " + e.getMessage());
            e.printStackTrace();
            // Zamknij aplikację mimo błędu
            Platform.exit();
        }
    }

    /**
     * Wyświetla okno resetowania hasła.
     */
    private void showResetPasswordWindow() {
        logger.debug("Otwieranie okna resetowania hasła");

        Stage resetStage = new Stage();
        resetStage.setTitle("Resetowanie hasła");

        VBox resetLayout = new VBox(10);
        resetLayout.setPadding(new Insets(20));
        resetLayout.setAlignment(Pos.CENTER);

        Label emailLabel = new Label("Podaj swój email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        Button sendCodeButton = new Button("Wyślij kod odzyskiwania");
        sendCodeButton.setOnAction(e -> {
            System.out.println("DEBUG: Próba wysłania kodu resetowania hasła");
            String email = emailField.getText();
            if (email.isEmpty()) {
                System.out.println("DEBUG: Brak adresu email");
                HelloApplication.showAlert(
                        Alert.AlertType.ERROR,
                        "Błąd",
                        "Brak adresu email",
                        "Proszę podać adres e-mail, na który ma zostać wysłany kod."
                );
            } else {
                this.resetEmail = email;
                try {
                    System.out.println("DEBUG: Wysyłanie kodu resetowania na email: " + email);
                    Login.sendResetCode(email);
                    resetStage.close();
                    showVerificationWindow();
                } catch (Exception ex) {
                    System.err.println("BŁĄD podczas wysyłania kodu resetowania: "
                            + ex.getMessage());
                    ex.printStackTrace();
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Błąd",
                            "Nie można wysłać kodu resetowania",
                            ex.getMessage()
                    );
                }
            }
        });

        resetLayout.getChildren().addAll(emailLabel, emailField, sendCodeButton);

        Scene resetScene = new Scene(resetLayout, 300, 200);
        resetStage.setScene(resetScene);

        System.out.println("DEBUG: Wyświetlanie okna resetowania hasła");
        resetStage.show();
    }

    /**
     * Wyświetla okno weryfikacji kodu resetującego.
     */
    public void showVerificationWindow() {
        logger.debug("Otwieranie okna weryfikacji kodu");

        Stage verificationStage = new Stage();
        verificationStage.setTitle("Weryfikacja kodu");
        verificationStage.setOnCloseRequest(e -> {
            logger.debug("Okno weryfikacji zamknięte");
        });

        VBox verificationLayout = new VBox(10);
        verificationLayout.setPadding(new Insets(20));
        verificationLayout.setAlignment(Pos.CENTER);
        verificationLayout.setStyle("-fx-background-color: #f0f0f0;");

        Label titleLabel = new Label("Weryfikacja kodu");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.DARKBLUE);

        Label instructionLabel = new Label("Podaj 6-znakowy kod," +
                " który otrzymałeś na adres:");
        Label emailLabel = new Label(this.resetEmail);
        emailLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        emailLabel.setTextFill(Color.DARKSLATEGRAY);

        // Pole tekstowe umożliwiające wpisanie alfanumerycznego
        // kodu (maks. 6 znaków)
        TextField codeField = new TextField();
        codeField.setPromptText("Wprowadź kod weryfikacyjny");
        codeField.setMaxWidth(150);
        codeField.setTextFormatter(new TextFormatter<>(new DefaultStringConverter(),
                "", change -> {
            // Pozwól na dowolne znaki, ograniczając długość do 6
            return change.getControlNewText().length() <= 6 ? change : null;
        }));

        Button verifyButton = new Button("Zweryfikuj");
        verifyButton.setStyle("-fx-background-color: #4CAF50;" +
                " -fx-text-fill: white; -fx-font-weight: bold;");
        verifyButton.setOnAction(e -> {
            logger.debug("Próba weryfikacji kodu");
            String code = codeField.getText().trim();

            if (code.isEmpty()) {
                showAlert(Alert.AlertType.ERROR,
                        "Błąd",
                        "Brak kodu",
                        "Proszę wprowadzić kod weryfikacyjny.");
                return;
            }

            if (code.length() != 6) {
                showAlert(Alert.AlertType.ERROR,
                        "Błąd",
                        "Nieprawidłowy kod",
                        "Kod musi składać się z 6 znaków.");
                return;
            }

            if (Login.verifyResetCode(this.resetEmail, code)) {
                logger.info("Kod weryfikacyjny poprawny");
                verificationStage.close();
                showNewPasswordWindow(this.resetEmail);
            } else {
                logger.warn("Niepoprawny kod weryfikacyjny");
                showAlert(Alert.AlertType.ERROR,
                        "Błąd",
                        "Niepoprawny kod",
                        "Podany kod jest nieprawidłowy lub wygasł. Spróbuj ponownie.");
            }
        });

        Button resendButton = new Button("Wyślij kod ponownie");
        resendButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        resendButton.setOnAction(e -> {
            logger.info("Wysyłanie kodu ponownie do: {}", this.resetEmail);
            Login.sendResetCode(this.resetEmail);
            showAlert(Alert.AlertType.INFORMATION,
                    "Kod wysłany",
                    "Nowy kod został wysłany",
                    "Sprawdź swoją skrzynkę pocztową.");
        });

        HBox buttonBox = new HBox(10, verifyButton, resendButton);
        buttonBox.setAlignment(Pos.CENTER);

        verificationLayout.getChildren().addAll(
                titleLabel,
                instructionLabel,
                emailLabel,
                codeField,
                buttonBox
        );

        Scene verificationScene = new Scene(verificationLayout, 350, 250);
        verificationStage.setScene(verificationScene);
        verificationStage.show();
    }


    private void showNewPasswordWindow(String userEmail) {
        logger.debug("Otwieranie okna ustawiania nowego hasła dla: {}", userEmail);

        Stage passwordStage = new Stage();
        passwordStage.setTitle("Ustaw nowe hasło");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label newPasswordLabel = new Label("Nowe hasło:");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Wprowadź nowe hasło");

        Label repeatPasswordLabel = new Label("Powtórz hasło:");
        PasswordField repeatPasswordField = new PasswordField();
        repeatPasswordField.setPromptText("Powtórz nowe hasło");

        Button submitButton = new Button("Zapisz nowe hasło");
        submitButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white;");
        submitButton.setOnAction(e -> {
            logger.debug("Próba zapisania nowego hasła");
            String newPass = newPasswordField.getText();
            String repeatPass = repeatPasswordField.getText();

            if (newPass.isEmpty() || repeatPass.isEmpty()) {
                logger.warn("Puste pola hasła");
                showAlert(Alert.AlertType.ERROR, "Błąd", "Puste pola",
                        "Proszę wypełnić oba pola hasła.");
            } else if (!newPass.equals(repeatPass)) {
                logger.warn("Hasła nie są zgodne");
                showAlert(Alert.AlertType.ERROR, "Błąd", "Hasła nie są zgodne",
                        "Upewnij się, że oba hasła są identyczne.");
            } else if (newPass.length() < 8) {
                logger.warn("Hasło za krótkie: {} znaków", newPass.length());
                showAlert(Alert.AlertType.ERROR, "Błąd", "Hasło za krótkie",
                        "Hasło musi mieć co najmniej 8 znaków.");
            } else {
                logger.info("Walidacja hasła pomyślna");

                UserRepository userRepo = new UserRepository();
                try {
                    Employee employee = userRepo.findByEmail(userEmail).stream()
                            .filter(emp -> !emp.isDeleted())
                            .findFirst()
                            .orElse(null);

                    if (employee == null) {
                        showAlert(Alert.AlertType.ERROR,
                                "Błąd",
                                "Nie znaleziono użytkownika",
                                "Adres email nie istnieje w systemie.");
                        return;
                    }

                    String hashedPassword = PasswordHasher.hashPassword(newPass, employee.getId());
                    boolean success = userRepo.updatePasswordByEmail(userEmail, hashedPassword);

                    if (success) {
                        logger.info("Hasło zaktualizowane pomyślnie");
                        showAlert(Alert.AlertType.INFORMATION,
                                "Sukces",
                                "Hasło zmienione",
                                "Twoje hasło zostało zaktualizowane.");
                        passwordStage.close();
                    } else {
                        showAlert(Alert.AlertType.ERROR,
                                "Błąd",
                                "Problem z aktualizacją",
                                "Nie udało się zaktualizować hasła.");
                    }
                } catch (Exception ex) {
                    logger.error("Błąd podczas aktualizacji hasła: {}", ex.getMessage(), ex);
                    showAlert(Alert.AlertType.ERROR,
                            "Błąd",
                            "Problem techniczny",
                            "Wystąpił błąd podczas aktualizacji hasła: " + ex.getMessage());
                } finally {
                }
            }
        });

        layout.getChildren().addAll(
                newPasswordLabel, newPasswordField,
                repeatPasswordLabel, repeatPasswordField,
                submitButton
        );

        Scene scene = new Scene(layout, 300, 250);
        passwordStage.setScene(scene);
        passwordStage.show();
    }

    /**
     * Główna metoda uruchamiająca aplikację.
     */
    public static void main(String[] args) {
        logger.info("Rozpoczęcie metody main()");

        // Globalny handler wyjątków - łapie wszystkie niezłapane wyjątki
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logger.fatal("NIEZŁAPANY WYJĄTEK w wątku {}: {}",
                    thread.getName(), throwable.getMessage(), throwable);

            // Sprawdź czy wyjątek lub jego przyczyna jest związana z bazą danych
            if (isDataAccessException(throwable)) {
                DatabaseErrorHandler.showDatabaseError(
                        throwable,
                        "Nieoczekiwany błąd bazy danych",
                        "Wystąpił nieoczekiwany błąd podczas operacji na bazie danych",
                        false
                );
            } else {
                // Dla innych wyjątków pokazujemy standardowy alert
                Platform.runLater(() -> {
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Nieoczekiwany błąd",
                            "Wystąpił nieoczekiwany błąd",
                            throwable.getMessage()
                    );
                });
            }
        });

        try {
            logger.info("Próba inicjalizacji bazy danych");
            try {
                org.example.database.DatabaseInitializer.initialize();
                logger.info("Baza danych zainicjalizowana pomyślnie");
            } catch (Exception e) {
                logger.error("BŁĄD podczas inicjalizacji bazy danych: {}", e.getMessage(), e);

                // Użyj nowego handlera błędów bazy danych
                DatabaseErrorHandler.showDatabaseError(
                        e,
                        "Błąd inicjalizacji bazy danych",
                        "Nie można zainicjalizować bazy danych",
                        true  // Krytyczny błąd - zamyka aplikację
                );
                return; // Zakończ metodę main, aplikacja zostanie zamknięta przez handler
            }

            // Migracja haseł
            try {
                logger.info("Rozpoczynam migrację haseł pracowników");
                UserRepository userRepo = new UserRepository();
                List<Employee> all = userRepo.getAllEmployees();
                for (Employee emp : all) {
                    String raw = emp.getPassword();
                    if (raw != null && !raw.matches("[0-9a-f]{64}")) {
                        String hashed = PasswordHasher.hashPassword(raw, emp.getId());
                        emp.setPassword(hashed);
                        userRepo.updateEmployee(emp);
                        logger.debug("Zhashowano i zaktualizowano pracownika id={}", emp.getId());
                    }
                }
                logger.info("Migracja haseł zakończona");
            } catch (Exception ex) {
                logger.error("BŁĄD podczas migracji haseł: {}", ex.getMessage(), ex);

                // Użyj nowego handlera błędów bazy danych
                DatabaseErrorHandler.showDatabaseError(
                        ex,
                        "Błąd migracji haseł",
                        "Wystąpił błąd podczas migracji haseł",
                        false  // Niekrytyczny błąd - daje opcję kontynuacji
                );
            }

            Platform.setImplicitExit(true);
            logger.info("Uruchamianie aplikacji JavaFX");
            launch(args);
            logger.info("Aplikacja JavaFX zakończona");

        } catch (Exception e) {
            logger.fatal("BŁĄD KRYTYCZNY w main(): {}", e.getMessage(), e);

            // Użyj nowego handlera błędów
            if (isDataAccessException(e)) {
                DatabaseErrorHandler.showDatabaseError(
                        e,
                        "Błąd krytyczny bazy danych",
                        "Wystąpił krytyczny błąd bazy danych",
                        true  // Krytyczny błąd - zamyka aplikację
                );
            } else {
                Platform.runLater(() -> {
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Błąd krytyczny",
                            "Wystąpił krytyczny błąd podczas uruchamiania aplikacji",
                            e.getMessage()
                    );
                    Platform.exit();
                });
            }
        }

        logger.info("Koniec metody main()");
        EMFProvider.close();
    }

    /**
     * Sprawdza czy wyjątek jest związany z dostępem do bazy danych.
     */
    private static boolean isDataAccessException(Throwable throwable) {
        // Sprawdź czy sam wyjątek jest SQLException
        if (throwable instanceof SQLException) {
            return true;
        }

        // Sprawdź czy przyczyna jest SQLException
        Throwable cause = throwable.getCause();
        if (cause instanceof SQLException) {
            return true;
        }

        // Sprawdź nazwę klasy, czasami mogą być używane
        // inne wyjątki dla błędów bazy danych
        String className = throwable.getClass().getName();
        return className.contains("SQL") ||
                className.contains("Database") ||
                className.contains("Connection") ||
                className.contains("Persistence") ||
                className.contains("JPA");
    }
}