package org.example.gui;

import javafx.stage.Stage;

public class ClientPanel {
    private final Stage primaryStage;
    private final ClientPanelController controller;

    public ClientPanel(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.controller = new ClientPanelController(this);
        primaryStage.setTitle("Panel klienta - Panel zakupowy");

        controller.showClientPanel();
        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
