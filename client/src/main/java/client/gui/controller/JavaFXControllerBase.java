package client.gui.controller;

import client.gui.SoundEffects;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * Base class for JavaFX GUI Controllers.
 *
 * Encapsulates some commonly used features like creating windows and showing message dialogs.
 */
public abstract class JavaFXControllerBase {

    protected static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    protected String title;
    protected Parent widget = null;
    protected Scene scene = null;
    protected Stage window = null;

    private SoundEffects soundEffects;

    /**
     * Instatiate a new Controller and set the window title it will use.
     * @param title The window title
     */
    protected JavaFXControllerBase(String title){
        this.title = title;
        this.soundEffects = new SoundEffects();
    }

    /**
     * Creates a window (Stage) for this controller
     */
    protected void createWindow(){
        // setup window
        window = new Stage();
        window.initModality(Modality.WINDOW_MODAL);

        setWindow(window);
    }

    /**
     * Sets an existing window (Stage) to use when the controller is run
     *
     * @param window The window to use
     */
    public void setWindow(Stage window){
        window.setTitle(title);
        window.setOnCloseRequest(event -> LOG.debug(String.format("\"%s\" window closed", title)));

        this.window = window;
    }

    /**
     * Retrieves the window (Stage) used by this controller.
     * This will be null until a window is created with createWindow() or one is assigned with setWindow().
     *
     * @return The window used by this controller
     */
    public Stage getWindow(){
        return window;
    }

    /**
     * Closes the controller's window
     */
    public void closeWindow(){
        this.window.close();
    }

    /**
     * Creates a new window if no window is assigned to the controller yet
     */
    protected void initWindow(){
        if (window == null) {
            createWindow();
        }
    }

    /**
     * Opens the GUI associated with this controller.
     * If no window has been set with `setWindow()`, a new window is opened.
     */
    protected void run(){
        initWindow();

        if (widget == null) {
            try {
                widget = createScene();
            } catch (Exception e) {
                LOG.error("Failed to create widget: {}", e.getMessage());
                showError(e, "Failed to create widget");
                return;
            }

            scene = new Scene(widget);
        }

        initScene();

        window.setScene(scene);

        // Show the dialog and wait until the user closes it
        if (!window.isShowing()) {
            window.showAndWait();
        }
    }

    /**
     * Called when the controller is run for the first time.
     * Should create and return the widget (Node) that'll be displayed in the window.
     *
     * @return The widget to show in this Controller's window
     * @throws Exception
     */
    protected abstract Parent createScene() throws Exception;

    /**
     * Called after the widgets have been loaded from the fxml file.
     * Override this method to initialize your widgets.
     */
    protected void initScene(){}

    /**
     * Opens an error dialog.
     *
     * @param ex The error to display
     * @param title The title for the error dialog
     */
    public void showError(Exception ex, String title) {
        showAlert(ex.getMessage(), "Error", title, Alert.AlertType.ERROR, false);
    }

    /**
     * Show an error popup
     *
     * @param ex
     * @param title
     */
    public void showErrorAndWait(Exception ex, String title) {
        showAlert(ex.getMessage(), "Error", title, Alert.AlertType.ERROR, true);
    }

    /**
     * Show a notification popup
     */
    public void showNotification(String message, String title) {
        showAlert(message, title, title, Alert.AlertType.INFORMATION, false);
    }

    /**
     * Opens a warning dialog.
     *
     * @param message The warning message to display
     * @param title The title for the dialog
     */
    public void showWarning(String message, String title) {
        showAlert(message, title, title, Alert.AlertType.WARNING, false);
    }

    /**
     * Show an alert popup
     *
     * @param message
     * @param title
     * @param header
     * @param alertType
     * @param wait
     */
    private void showAlert(String message, String title, String header, Alert.AlertType alertType, boolean wait){
        soundEffects.playErrorSound();
        Alert alert = new Alert(alertType);
        alert.initOwner(this.window);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        if (wait)
            alert.showAndWait();
        else
            alert.show();
    }

    /**
     * hides the window
     */
    public void hideWindow() {
        this.window.hide();
    }
}
