package org.example.sys;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.gui.*;
import org.example.gui.HelloApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void attemptLogin(
            String username,
            String password,
            VBox root)
    {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/StonkaDB", "root", "")) {

            String query = "SELECT * FROM Pracownicy WHERE Login = ? AND Haslo = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String stanowisko = rs.getString("Stanowisko");

                        HelloApplication.showAlert(
                                Alert.AlertType.INFORMATION,
                                "Sukces",
                                "Zalogowano pomyślnie!",
                                "Witaj, " + rs.getString("Imie") + "!"
                        );

                        Stage currentStage = (Stage) root.getScene().getWindow();
                        currentStage.close();
                        Stage nextStage = new Stage();

                        switch (stanowisko.toLowerCase()) {
                            case "admin":
                                new AdminPanel(nextStage);
                                break;
                            case "kierownik":
                                new ManagerPanel(nextStage);
                                break;
                            case "kasjer":
                                new CashierPanel(nextStage);
                                break;
                            case "logistyk":
                                new LogisticianPanel(nextStage);
                                break;
                            default:
                                HelloApplication.showAlert(
                                        Alert.AlertType.WARNING,
                                        "Brak panelu",
                                        "Nieznana rola użytkownika",
                                        "Stanowisko: " + stanowisko
                                );
                                break;
                        }
                    } else {
                        HelloApplication.showAlert(
                                Alert.AlertType.ERROR,
                                "Błąd",
                                "Nieprawidłowe dane logowania!",
                                "Spróbuj ponownie."
                        );
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            HelloApplication.showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd",
                    "Wystąpił błąd połączenia",
                    e.getMessage()
            );
        }
    }

    /**
     * Funkcja do wysyłania kodu odzyskiwania hasła.
     * Generuje losowy kod i wysyła go na podany adres e-mail.
     *
     * @param email Adres e-mail użytkownika
     */
    public static void sendResetCode(String email) {
        String resetCode = generateRandomCode(6);
        executor.submit(() -> {
            try {
                sendEmail(email, resetCode);
                Platform.runLater(() -> {
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    HelloApplication.showAlert(
                            Alert.AlertType.ERROR,
                            "Błąd wysyłania e-maila",
                            "Wystąpił problem z wysyłką e-maila",
                            e.getMessage()
                    );
                });
            }
        });
    }

    /**
     * Funkcja generująca losowy kod o podanej długości.
     *
     * @param length Długość generowanego kodu
     * @return Generowany kod
     */
    private static String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }

        return code.toString();
    }

    /**
     * Funkcja symulująca wysyłanie e-maila.
     * W prawdziwej aplikacji należy użyć np. JavaMail API.
     *
     * @param email    Adres e-mail odbiorcy
     * @param resetCode Kod do resetowania hasła
     */
    private static void sendEmail(String email, String resetCode) {
        System.out.println("Wysyłanie e-maila do: " + email);
        System.out.println("Kod: " + resetCode);
    }
}
