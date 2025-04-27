/*
 * Classname: Login
 * Version information: 1.0
 * Date: 2025-04-27
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.database.UserRepository;
import org.example.gui.AdminPanel;
import org.example.gui.CashierPanel;
import org.example.gui.HelloApplication;
import org.example.gui.LogisticianPanel;
import org.example.gui.ManagerPanel;

/**
 * Klasa odpowiedzialna za proces logowania użytkownika.
 */
public class Login {

    private static final UserRepository userRepo = new UserRepository();

    /**
     * Próbuje zalogować użytkownika na podstawie podanych danych.
     *
     * @param username nazwa użytkownika
     * @param password hasło użytkownika
     * @param root     korzeń sceny logowania
     */
    public static void attemptLogin(String username, String password, VBox root) {
        try {
            Employee employee = userRepo.znajdzPoLoginieIHasle(username, password);

            if (employee != null) {
                HelloApplication.showAlert(
                        Alert.AlertType.INFORMATION,
                        "Sukces",
                        "Zalogowano pomyślnie!",
                        "Witaj, " + employee.getImie() + "!"
                );

                Stage currentStage = (Stage) root.getScene().getWindow();
                currentStage.close();

                Stage nextStage = new Stage();
                openPanelForEmployee(employee.getStanowisko(), nextStage);

            } else {
                HelloApplication.showAlert(
                        Alert.AlertType.ERROR,
                        "Błąd",
                        "Nieprawidłowe dane logowania!",
                        "Spróbuj ponownie."
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            HelloApplication.showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd",
                    "Wystąpił błąd podczas logowania",
                    e.getMessage()
            );
        }
    }

    /**
     * Otwiera odpowiedni panel w zależności od stanowiska użytkownika.
     *
     * @param stanowisko stanowisko pracownika
     * @param nextStage  nowe okno aplikacji
     */
    private static void openPanelForEmployee(String stanowisko, Stage nextStage) {
        if (stanowisko == null) {
            HelloApplication.showAlert(
                    Alert.AlertType.WARNING,
                    "Brak panelu",
                    "Nieznana rola użytkownika",
                    "Stanowisko nieokreślone"
            );
            return;
        }

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
    }
}
