/*
 * Classname: Login
 * Version information: 1.4
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.database.ILacz;
import org.example.database.UserRepository;
import org.example.gui.*;
import javax.mail.MessagingException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

// Importy Log4j2
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Klasa odpowiedzialna za logikę logowania użytkowników do systemu.
 * Zawiera metody do obsługi logowania, wysyłania kodów resetujących oraz zarządzania sesjami użytkowników.
 */
public class Login implements ILacz {

    private static final Logger logger = LogManager.getLogger(Login.class);

    private static final ExecutorService executor =
            Executors.newVirtualThreadPerTaskExecutor();
    private static final UserRepository userRepository = new UserRepository();

    /**
     * Próbuje zalogować użytkownika na podstawie podanego loginu i hasła.
     *
     * @param username nazwa użytkownika
     * @param password hasło użytkownika
     * @param root     główny kontener aplikacji
     */
    public static void attemptLogin(String username, String password, VBox root) {
        logger.debug("Rozpoczęto próbę logowania dla użytkownika: {}", username);

        if (username.isBlank() || password.isBlank()) {
            logger.warn("Brak danych logowania");
            showAlert(Alert.AlertType.WARNING, "Brak danych", "Proszę wypełnić wszystkie pola");
            return;
        }

        Task<Employee> loginTask = new Task<>() {
            @Override
            protected Employee call() throws Exception {
                UserRepository repo = new UserRepository();
                Employee user = repo.findByLogin(username);
                repo.close();
                if (user == null) {
                    logger.warn("Nie znaleziono użytkownika o loginie: {}", username);
                    throw new Exception("Nie znaleziono użytkownika o podanym loginie");
                }
                boolean correct = PasswordHasher.verifyPassword(user.getPassword(), password, user.getId());
                if (!correct) {
                    logger.warn("Nieprawidłowe hasło dla użytkownika: {}", username);
                    throw new Exception("Nieprawidłowe hasło");
                }
                logger.info("Użytkownik '{}' został pomyślnie zalogowany", username);
                return user;
            }
        };

        setupTaskHandlers(loginTask, root);
        executor.execute(loginTask);
    }

    /**
     * Tworzy zadanie do logowania użytkownika.
     *
     * @param username nazwa użytkownika
     * @param password hasło użytkownika
     * @param root     główny kontener aplikacji
     * @return zadanie logowania
     */
    private static Task<Employee> createLoginTask(String username, String password, VBox root) {
        logger.trace("Tworzenie zadania logowania dla użytkownika: {}", username);
        return new Task<>() {
            @Override
            protected Employee call() throws Exception {
                return userRepository.findByLoginAndPassword(username, password);
            }
        };
    }

    /**
     * Ustawia obsługę zdarzeń dla zadania logowania.
     *
     * @param task zadanie logowania
     * @param root główny kontener aplikacji
     */
    private static void setupTaskHandlers(Task<Employee> task, VBox root) {
        logger.debug("Ustawianie handlerów dla zadania logowania");

        task.setOnSucceeded(e -> {
            Employee employee = task.getValue();
            if (employee != null) {
                handleLoginSuccess(employee, root);
            } else {
                Platform.runLater(() -> {
                    logger.warn("Nie znaleziono użytkownika lub wystąpił błąd logowania");
                    showAlert(Alert.AlertType.ERROR, "Błąd logowania",
                            "Nieprawidłowy login lub hasło. Spróbuj ponownie.");
                });
            }
        });

        task.setOnFailed(e -> handleLoginFailure(task.getException()));
    }

    /**
     * Obsługuje pomyślne zalogowanie użytkownika.
     *
     * @param employee obiekt pracownika
     * @param root     główny kontener aplikacji
     */
    private static void handleLoginSuccess(Employee employee, VBox root) {
        logger.info("Zalogowano użytkownika: {} {}", employee.getName(), employee.getSurname());

        if (employee == null) {
            Platform.runLater(() -> {
                logger.error("Próbowano zalogować pustego użytkownika");
                showAlert(Alert.AlertType.ERROR, "Błąd logowania",
                        "Wystąpił błąd podczas logowania. Spróbuj ponownie.");
            });
            return;
        }

        Platform.runLater(() -> {
            UserRepository.setLoggedInEmployee(employee.getId());
            showSuccessAlert(employee);
            redirectToProperPanel(employee.getPosition().toLowerCase(), root);
        });
    }

    /**
     * Wyświetla komunikat o pomyślnym zalogowaniu.
     *
     * @param employee obiekt pracownika
     */
    private static void showSuccessAlert(Employee employee) {
        logger.debug("Wyświetlono alert sukcesu dla użytkownika: {}", employee.getLogin());
        HelloApplication.showAlert(
                Alert.AlertType.INFORMATION,
                "Sukces",
                "Zalogowano pomyślnie!",
                "Witaj, " + employee.getName() + "!"
        );
    }

    /**
     * Przekierowuje użytkownika do odpowiedniego panelu w zależności od jego stanowiska.
     *
     * @param position stanowisko użytkownika
     * @param root     główny kontener aplikacji
     */
    private static void redirectToProperPanel(String position, VBox root) {
        logger.info("Przekierowanie użytkownika do panelu: {}", position);

        Platform.runLater(() -> {
            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();
            Stage nextStage = new Stage();

            if ("root".equalsIgnoreCase(position)) {
                new AdminPanel(nextStage);
            } else {
                switch (position.toLowerCase()) {
                    case "admin" -> new AdminPanel(nextStage);
                    case "kierownik" -> new ManagerPanel(nextStage);
                    case "kasjer" -> {
                        CashierPanel cashierPanel = new CashierPanel(nextStage);
                        cashierPanel.getController().resetReportGeneratedFlag();
                        if (cashierPanel.getController().isDailyReportGeneratedToday()) {
                            cashierPanel.getController().markReportAsGenerated();
                        }
                    }
                    case "logistyk" -> new LogisticianPanel(nextStage);
                    case "pracownik" -> new EmployeePanel(nextStage);
                    default -> showUnknownPositionAlert(position);
                }
            }
        });
    }

    /**
     * Obsługuje błąd logowania.
     *
     * @param exception wyjątek związany z logowaniem
     */
    private static void handleLoginFailure(Throwable exception) {
        logger.error("Błąd logowania: {}", exception.getMessage(), exception);

        Platform.runLater(() -> {
            if (exception instanceof SecurityException) {
                showAlert(Alert.AlertType.ERROR, "Błąd logowania", exception.getMessage());
            } else {
                logError(exception);
                showAlert(Alert.AlertType.ERROR, "Błąd połączenia", "Problem z połączeniem do systemu");
            }
        });
    }

    /**
     * Loguje błąd do konsoli.
     *
     * @param exception wyjątek do zalogowania
     */
    private static void logError(Throwable exception) {
        logger.error("Szczegóły błędu logowania:", exception);
    }

    /**
     * Wysyła kod resetujący na podany adres email.
     *
     * @param email adres email, na który ma zostać wysłany kod
     */
    public static void sendResetCode(String email) {
        logger.info("Wysyłanie kodu resetującego na e-mail: {}", email);

        if (!EmailValidator.isValid(email)) {
            logger.warn("Nieprawidłowy e-mail: {}", email);
            showAlert(Alert.AlertType.ERROR, "Nieprawidłowy email", "Podaj poprawny adres email");
            return;
        }

        executor.execute(() -> {
            try {
                EmailSender.sendResetEmail(email);
                logger.info("Kod resetujący wysłany na e-mail: {}", email);
                showSuccessAlert("Kod resetujący wysłany", "Sprawdź swoją skrzynkę pocztową");
            } catch (Exception e) {
                logger.error("Nie można wysłać kodu resetowego do {}: {}", email, e.getMessage(), e);
                handleEmailError((MessagingException) e);
            }
        });
    }

    /**
     * Generuje losowy kod o podanej długości.
     *
     * @param length długość kodu
     * @return losowy kod
     */
    public static String generateRandomCode(int length) {
        logger.trace("Generowanie losowego kodu o długości: {}", length);
        return new Random().ints(length, 0, 36)
                .mapToObj(i -> i < 10 ? String.valueOf(i) : String.valueOf((char) (i + 55)))
                .collect(Collectors.joining());
    }

    /**
     * Obsługuje błąd wysyłania emaila.
     *
     * @param e wyjątek związany z wysyłaniem emaila
     */
    private static void handleEmailError(MessagingException e) {
        logger.error("Błąd wysyłania e-maila z kodem resetowym: {}", e.getMessage(), e);
    }

    /**
     * Wyświetla komunikat o błędzie.
     *
     * @param type    typ alertu
     * @param title   tytuł alertu
     * @param message treść alertu
     */
    private static void showAlert(Alert.AlertType type, String title, String message) {
        logger.debug("Wyświetlono alert: {}, tytuł: '{}', wiadomość: '{}'", type, title, message);
        Platform.runLater(() ->
                HelloApplication.showAlert(type, title, message, ""));
    }

    /**
     * Wyświetla komunikat o sukcesie.
     *
     * @param title   tytuł alertu
     * @param content treść alertu
     */
    private static void showSuccessAlert(String title, String content) {
        logger.info("Wyświetlono alert sukcesu: '{}', wiadomość: '{}'", title, content);
        Platform.runLater(() ->
                HelloApplication.showAlert(Alert.AlertType.INFORMATION, title, content, ""));
    }

    /**
     * Wyświetla komunikat o nieznanym stanowisku.
     *
     * @param position stanowisko użytkownika
     */
    private static void showUnknownPositionAlert(String position) {
        logger.warn("Nieznana rola użytkownika: {}", position);
        HelloApplication.showAlert(
                Alert.AlertType.WARNING,
                "Brak panelu",
                "Nieznana rola użytkownika",
                "Stanowisko: " + position
        );
    }

    /**
     * Weryfikuje kod resetujący dla podanego adresu email.
     *
     * @param email adres email użytkownika
     * @param code  kod weryfikacyjny
     * @return true jeśli kod jest poprawny, false w przeciwnym razie
     */
    public static boolean verifyResetCode(String email, String code) {
        logger.debug("Weryfikacja kodu resetującego dla email: {}", email);

        if (email == null || email.isBlank() || code == null || code.isBlank()) {
            logger.warn("Nieprawidłowe dane wejściowe do weryfikacji kodu");
            return false;
        }

        if (!EmailValidator.isValid(email)) {
            logger.warn("Nieprawidłowy format email: {}", email);
            return false;
        }

        try {
            // Sprawdź czy kod jest prawidłowy
            return EmailSender.verifyResetCode(email, code);
        } catch (Exception e) {
            logger.error("Błąd podczas weryfikacji kodu dla {}: {}", email, e.getMessage(), e);
            return false;
        }
    }
}