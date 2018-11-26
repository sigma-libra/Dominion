package client.gui.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Objects;

/**
 * Base class for JavaFX Controllers that load their widgets from an fxml file.
 *
 * Basic usage is as follows:
 *  1) Write a constructor that provides the required `fxmlPath` and `title` parameters
 *  2) If you'd like to initiliaze the widgets after they've been loaded from the fxml file, override the `initScene` method
 *  3) You're done. Call the `run()` method to open the controller's window.
 */
public abstract class FXMLControllerBase extends JavaFXControllerBase {

    private String fxmlPath;


    /**
     * Creates a new instance
     *
     * @param fxmlPath The path to the fxml file that should be loaded
     * @param title The window title to use
     */
    protected FXMLControllerBase(String fxmlPath, String title){
        super(title);
        this.fxmlPath = fxmlPath;
    }

    @Override
    protected Parent createScene() throws IOException {
        // Load the fxml file and create a new stage for the popup dialog.
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(cls -> this);

        try {
            return loader.load();
        } catch (IOException e) {
            throw new IOException(String.format("Failed to load fxml file: %s", e.getMessage()));
        }
    }
}
