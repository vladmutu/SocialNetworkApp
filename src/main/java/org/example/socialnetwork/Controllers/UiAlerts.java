package org.example.socialnetwork.Controllers;

import javafx.scene.control.Alert;

public final class UiAlerts {
    private UiAlerts() {
    }

    public static void info(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void error(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

