/*
 * Classname: AlterBox
 * Version information: 1.1
 * Date: 2025-06-04
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui.elements;

import javafx.scene.control.Alert;

/**
 * Klasa pomocnicza wyświetlająca okno dialogowe typu Alert.
 */
public class AlterBox {

    /**
     * Wyświetla okno dialogowe z informacyjnym komunikatem.
     *
     * @param title   tytuł okna alertu
     * @param message treść wyświetlana w oknie
     */
    public static void display(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
