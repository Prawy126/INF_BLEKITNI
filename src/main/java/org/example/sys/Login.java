/*
 * Classname: Login
 * Version information: 1.0
 * Date: 2025-04-22
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.gui.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login {

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
}

