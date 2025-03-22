package org.example.gui;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ManagerPanel {

    private BorderPane root;
    private Stage primaryStage;
    private ManagerPanelController controller;

    public ManagerPanel(Stage stage) {
        this.primaryStage = stage;
        this.controller = new ManagerPanelController(this);

        primaryStage.setTitle("Panel kierownika");

        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: lightblue;");

        controller.showTaskPanel();

        animateFadeIn(root, 800);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void animateFadeIn(BorderPane pane, int duration) {
        FadeTransition fade = new FadeTransition(Duration.millis(duration), pane);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    public void setCenterPane(javafx.scene.layout.Pane pane) {
        root.setCenter(pane);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
