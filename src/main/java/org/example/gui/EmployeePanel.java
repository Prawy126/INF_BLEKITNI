package org.example.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class EmployeePanel {

    private BorderPane root;
    private Stage primaryStage;
    private EmployeePanelController controller;

    public EmployeePanel(Stage stage) {
        this.primaryStage = stage;
        this.controller = new EmployeePanelController(this);

        primaryStage.setTitle("Panel pracownika");

        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: lightblue;");

        controller.showEmployeePanel();

        Scene scene = new Scene(root, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setCenterPane(javafx.scene.layout.Pane pane) {
        root.setCenter(pane);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
