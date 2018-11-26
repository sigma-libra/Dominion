package client.gui.controller;

import client.service.ClientService;

import client.service.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shared.dto.UserDTO;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

/**
 * Class modelling the controller for the player login window
 */
@Controller
public class LoginController extends FXMLControllerBase {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ClientService clientService;

    private final JoinGameController joinGameController;

    //FXML variables----------------------------------------------------------------------------------------------------
    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @Autowired
    public LoginController(ClientService clientService, JoinGameController joinGameController) {
        super("/fxml/Login.fxml", "Login");

        this.clientService = Objects.requireNonNull(clientService);
        this.joinGameController = Objects.requireNonNull(joinGameController);
    }


    /**
     * Method called when the login button is clicked: get profile from database
     * If found, switch to window to join game as user, else return error message
     */
    @FXML
    public void onLoginButtonClicked() {
        LOG.info("Calling onLoginButtonClicked");

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            String error = "Missing information";
            LOG.error(error);
            showWarning("Please fill out all the fields.", error);
            return;
        }


        try {
            //Authentication:
            clientService.authenticate(username, password);

            UserDTO user = clientService.getUserByUsername(username);

            joinGameController.setWindow(window);
            joinGameController.run(user);
        } catch (ServiceException e) {
            String errorMessage = "Login failed: Check your input";
            LOG.error(errorMessage);
            showError(e, errorMessage);
            return;
        }
    }

}