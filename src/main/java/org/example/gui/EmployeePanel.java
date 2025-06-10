/*
 * Classname: EmployeePanel
 * Version information: 1.2
 * Date: 2025-06-07
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class EmployeePanel {

    private static final Logger logger = LogManager.getLogger(
            EmployeePanel.class);
    private BorderPane root;
    private Stage primaryStage;
    private EmployeePanelController controller;
    private Image logoImage;

    public EmployeePanel(Stage stage) {
        logger.info("Tworzenie EmployeePanel dla stage: {}", stage);

        this.primaryStage = stage;
        this.controller = new EmployeePanelController(this);
        logger.debug("Kontroler EmployeePanelController utworzony");

        try {
            logoImage = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/logo.png")
            ));
            logger.debug("Logo załadowane pomyślnie");
            primaryStage.getIcons().add(logoImage);
            logger.debug("Logo dodane jako ikona okna");
        } catch (Exception e) {
            logger.error("Błąd podczas ładowania logo: {}",
                    e.getMessage(), e);
        }

        primaryStage.setTitle("Panel pracownika");
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(450);
        logger.debug("Tytuł okna ustawiony na 'Panel pracownika' z" +
                " minimalnymi rozmiarami");

        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: lightblue;");
        logger.debug("Główny BorderPane utworzony i skonfigurowany");

        logger.debug("Wyświetlanie domyślnego panelu pracownika");
        controller.showEmployeePanel();

        Scene scene = new Scene(root, 700, 500);
        primaryStage.setScene(scene);
        logger.debug("Scena utworzona i ustawiona");

        logger.info("Wyświetlanie głównego okna panelu pracownika");
        primaryStage.show();
    }

    public void setCenterPane(javafx.scene.layout.Pane pane) {
        logger.debug("Ustawianie nowego panelu centralnego");
        root.setCenter(pane);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}