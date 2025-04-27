package org.example.sys;

import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.database.UserRepository;
import org.example.gui.*;

public class Login {

    private static final UserRepository userRepo = new UserRepository();

    public static void attemptLogin(
            String username,
            String password,
            VBox root)
    {
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

                String stanowisko = employee.getStanowisko().toLowerCase();

                switch (stanowisko) {
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
}
