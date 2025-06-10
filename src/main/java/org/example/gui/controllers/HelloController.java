/*
 * Classname: HelloController
 * Version information: 1.1
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}