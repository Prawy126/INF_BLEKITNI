package org.example.database;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Globalny handler do wyświetlania błędów bazy danych.
 */
public class DatabaseErrorHandler {
    private static final Logger logger = LogManager.getLogger(DatabaseErrorHandler.class);

    /**
     * Wyświetla okno dialogowe z błędem bazy danych.
     * Ta metoda może być wywoływana z dowolnego miejsca w aplikacji.
     */
    public static void showDatabaseError(Throwable exception, String title, String header, boolean isCritical) {
        logger.error("{}: {}", header, exception.getMessage(), exception);

        // Upewnij się, że kod UI jest wykonywany w wątku JavaFX
        if (Platform.isFxApplicationThread()) {
            showErrorDialog(exception, title, header, isCritical);
        } else {
            Platform.runLater(() -> showErrorDialog(exception, title, header, isCritical));
        }
    }

    /**
     * Tworzy i wyświetla okno dialogowe z błędem.
     */
    private static void showErrorDialog(Throwable exception, String title, String header, boolean isCritical) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);

        // Stwórz szczegółowy komunikat błędu
        String errorDetails = formatErrorDetails(exception);

        // Dodaj szczegóły błędu w rozwijalnym obszarze
        Label label = new Label("Szczegóły błędu:");

        TextArea textArea = new TextArea(errorDetails);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        // Dodaj pełny stack trace w dodatkowym obszarze
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);

        TextArea stackTraceArea = new TextArea(sw.toString());
        stackTraceArea.setEditable(false);
        stackTraceArea.setWrapText(true);
        stackTraceArea.setMaxWidth(Double.MAX_VALUE);
        stackTraceArea.setMaxHeight(Double.MAX_VALUE);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        expContent.add(new Label("Stack trace:"), 0, 2);
        expContent.add(stackTraceArea, 0, 3);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane.setVgrow(stackTraceArea, Priority.ALWAYS);
        GridPane.setHgrow(stackTraceArea, Priority.ALWAYS);

        alert.getDialogPane().setExpandableContent(expContent);

        // Dodaj podstawowy komunikat
        if (exception instanceof SQLException) {
            alert.setContentText("Wystąpił błąd podczas komunikacji z bazą danych. " +
                    "Sprawdź czy serwer bazy danych jest uruchomiony i dostępny.");
        } else {
            alert.setContentText("Wystąpił nieoczekiwany błąd podczas komunikacji z bazą danych.");
        }

        // Dla krytycznych błędów
        if (isCritical) {
            alert.setContentText(alert.getContentText() + "\nAplikacja zostanie zamknięta po zamknięciu tego okna.");
            alert.showAndWait();
            Platform.exit();
        } else {
            // Dla niekrytycznych błędów, daj opcję kontynuacji lub zamknięcia
            ButtonType continueButton = new ButtonType("Kontynuuj");
            ButtonType exitButton = new ButtonType("Zamknij aplikację");

            alert.getButtonTypes().setAll(continueButton, exitButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == exitButton) {
                Platform.exit();
            }
        }
    }

    /**
     * Formatuje szczegóły błędu w zależności od typu wyjątku.
     */
    private static String formatErrorDetails(Throwable exception) {
        if (exception instanceof SQLException) {
            return formatSQLException((SQLException) exception);
        } else {
            return "Typ błędu: " + exception.getClass().getName() +
                    "\nKomunikat: " + exception.getMessage();
        }
    }

    /**
     * Formatuje SQLException do bardziej czytelnej postaci.
     */
    private static String formatSQLException(SQLException ex) {
        StringBuilder sb = new StringBuilder();
        sb.append("Błąd SQL: ").append(ex.getMessage()).append("\n");
        sb.append("Kod błędu: ").append(ex.getErrorCode()).append("\n");
        sb.append("Stan SQL: ").append(ex.getSQLState()).append("\n\n");

        // Dodaj informacje o przyczynie błędu
        Throwable cause = ex.getCause();
        if (cause != null) {
            sb.append("Przyczyna: ").append(cause.getMessage()).append("\n");
        }

        // Dodaj sugestie rozwiązania problemu
        sb.append("\nMożliwe rozwiązania:\n");

        // Sugestie w zależności od kodu błędu
        switch (ex.getErrorCode()) {
            case 0:
                sb.append("- Sprawdź, czy serwer bazy danych jest uruchomiony\n");
                sb.append("- Sprawdź ustawienia połączenia (host, port)\n");
                break;
            case 1045:
                sb.append("- Nieprawidłowa nazwa użytkownika lub hasło\n");
                sb.append("- Sprawdź uprawnienia użytkownika bazy danych\n");
                break;
            case 1049:
                sb.append("- Baza danych nie istnieje\n");
                sb.append("- Sprawdź nazwę bazy danych\n");
                break;
            default:
                sb.append("- Sprawdź logi serwera bazy danych\n");
                sb.append("- Skontaktuj się z administratorem systemu\n");
                sb.append("- Sprawdź czy tabele istnieją i mają odpowiednią strukturę\n");
        }

        return sb.toString();
    }
}
