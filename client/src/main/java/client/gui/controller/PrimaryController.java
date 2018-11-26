package client.gui.controller;

import client.rest.RestApi;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Optional;

/**
 * Class implementing controller for the primary window allowing the client to run the game
 */
@Controller
public class PrimaryController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Stage primaryStage;

    @FXML
    private Hyperlink url;

    private final RestApi restApi;

    private final NewPlayerController newPlayerController;
    private final LoginController loginController;

    @Autowired
    public PrimaryController(NewPlayerController newPlayerController, LoginController loginController,
                             RestApi restApi) {
        this.restApi = Objects.requireNonNull(restApi);
        this.newPlayerController = Objects.requireNonNull(newPlayerController);
        this.loginController = Objects.requireNonNull(loginController);
    }

    public PrimaryController setPrimaryWindow(Stage stage) {
        this.primaryStage = stage;
        return this;
    }

    @FXML
    private void initialize() {
        this.url.setText(restApi.getUrl());
    }

    /**
     * Opens a window to log in
     */
    @FXML
    private void loginAsReturningButton() {
        LOG.info("Called loginAsReturningButton");

        this.primaryStage.hide();

        this.loginController.run();
    }

    /**
     * Opens a window to register
     */
    @FXML
    private void loginAsNewPlayerButton() {
        LOG.info("Called loginAsNewPlayerButton");

        this.primaryStage.hide();

        newPlayerController.run();
    }

    /**
     * Sets the server used based on the given url
     */
    @FXML
    private void setServerClicked() {
        TextInputDialog dialog = new TextInputDialog(restApi.getUrl());
        dialog.setTitle("Set Server Address");
        dialog.setHeaderText("New Address");
        dialog.setContentText("Please enter server address");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            restApi.setUrl(result.get());
            this.url.setText(restApi.getUrl());
        }
    }
}