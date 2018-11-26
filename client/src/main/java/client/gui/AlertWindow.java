package client.gui;

import javafx.scene.control.Alert;

/**
 * Class modelling a popup alert window
 */
public class AlertWindow extends Alert {
    public AlertWindow(AlertType alertType, String title, String header, String message) {
        super(alertType);
        this.setTitle(title);
        this.setHeaderText(header);
        this.setContentText(message);
    }
}
