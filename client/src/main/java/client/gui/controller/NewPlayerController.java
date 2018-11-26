package client.gui.controller;


import client.service.ClientService;
import client.service.exception.ServiceException;
import client.service.exception.UserAlreadyExistsException;
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
 * Class modelling the controller for the window to add a new player profile before a game starts
 */
@Controller
public class NewPlayerController extends FXMLControllerBase {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ClientService clientService;

    private final JoinGameController joinGameController;

    //FXML variables----------------------------------------------------------------------------------------------------
    @FXML
    private TextField newUserNameField;

    @FXML
    private TextField newPassword1;

    @FXML
    private TextField newPassword2;

    @Autowired
    public NewPlayerController(ClientService clientService, JoinGameController joinGameController){
        super("/fxml/NewPlayer.fxml", "Create account");
        this.clientService = Objects.requireNonNull(clientService);
        this.joinGameController = Objects.requireNonNull(joinGameController);
    }

    /**
     * Method to save a new player's profile when the Save button is clicked
     * Check inputs and database:
     * If the username is already used, error message
     * else switch to window to join game as new player
     */
    @FXML
    public void onSaveNewProfileButtonClicked() {
        LOG.info("Calling onSaveNewProfileButtonClicked");

        String userName = newUserNameField.getText();
        String password = newPassword1.getText();
        String passwordCopy = newPassword2.getText();

        if (userName == null || userName.isEmpty()) {
            String error = "No new username given";
            LOG.error(error);
            showWarning("Please give a username.", error);
            return;
        }

        if (password == null || password.isEmpty()) {
            String error = "Password must not be left blank";
            LOG.error(error);
            showWarning("Please enter a password.", error);
            return;
        }


        if (!password.equals(passwordCopy)) {
            String error = "Passwords don't match";
            LOG.error(error);
            showWarning("Please enter matching passwords.", error);
            return;
        }


        UserDTO userDTO;
        try {
            userDTO = clientService.addUserToDatabase(userName, password);
            clientService.authenticate(userName, password);
            LOG.debug("User added: allowing access to games");
            joinGameController.setWindow(window);
            joinGameController.run(userDTO);
        } catch (UserAlreadyExistsException e) {
            showWarning("Please choose a different user name.", "This user name is already in use.");
            return;
        } catch (ServiceException e) {
            String errorMessage = "Problem creating new user: " + e.getMessage();
            LOG.error(errorMessage);
            showError(e, "Failed to create account");
            return;
        }

    }
}
