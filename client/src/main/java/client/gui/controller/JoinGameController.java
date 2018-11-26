package client.gui.controller;


import client.gui.AlertWindow;
import client.gui.SoundEffects;
import client.service.ClientService;
import client.domain.ObservableGame;
import client.service.exception.ServiceException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import shared.domain.engine.GamePhase;
import shared.dto.GameStateDTO;
import shared.dto.UserDTO;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Objects;


/**
 * Class modelling a controller for the window to join a game as a logged in player
 */
@Controller
public class JoinGameController extends FXMLControllerBase {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ClientService clientService;

    private final GameTableController gameTableController;

    private final LoadSavedGameController loadSavedGameController;

    private final StatisticsController statisticsController;

    private final GameSettingsController gameSettingsController;

    private SoundEffects soundEffects;

    private UserDTO userDTO;

    private ObservableGame observableGame;

    //FXML variables----------------------------------------------------------------------------------------------------

    @FXML
    private Label usernameField;

    @FXML
    private Label gameMasterField;

    @FXML
    private TableView<UserDTO> joinedPlayersTable;

    @FXML
    private TableColumn<UserDTO, String> joinedPlayersUsernames;

    @FXML
    private Button joinGameButton;


    private int observerID;
    private int exceptionHandlerID;


    @Autowired
    public JoinGameController(ClientService clientService, GameTableController gameTableController,
                              StatisticsController statisticsController, GameSettingsController gameSettingsController,
                              LoadSavedGameController loadSavedGameController, SoundEffects soundEffects) {
        super("/fxml/JoinGame.fxml", "Join Game");
        this.clientService = Objects.requireNonNull(clientService);
        this.gameTableController = Objects.requireNonNull(gameTableController);
        this.statisticsController = Objects.requireNonNull(statisticsController);
        this.gameSettingsController = Objects.requireNonNull(gameSettingsController);
        this.soundEffects = Objects.requireNonNull(soundEffects);
        this.loadSavedGameController = Objects.requireNonNull(loadSavedGameController);
    }

    public void run(UserDTO user){
        this.userDTO = user;

        try {
            observableGame = clientService.subscribeToGameUpdates();
        } catch (ServiceException e){
            showError(e, "Failed to load game information");
            soundEffects.playErrorSound();
            return;
        }

        exceptionHandlerID = observableGame.addExceptionHandler(ex -> handleGameExceptionFromThread(ex));
        observerID = observableGame.addObserver(game -> updateGamestateFromThread(game));

        super.run();
    }

    @Override
    protected void initScene(){
        joinedPlayersUsernames.setCellValueFactory(new PropertyValueFactory<>("userName"));


        usernameField.setText(userDTO.getUserName());
        joinGameButton.setDisable(false);

        updateGamestate();
    }

    protected void handleGameExceptionFromThread(Exception ex){
        Platform.runLater(() -> handleGameException(ex));
    }

    protected void handleGameException(Exception ex){
        LOG.warn("Exception occurred while listening for game state updates: "+ex.getMessage());

        LOG.info("Connection to server lost, exiting...");
        showErrorAndWait(ex, "Connection to server lost");
        closeWindow();
    }

    protected void updateGamestateFromThread(ObservableGame observableGame){
        Platform.runLater(() -> updateGamestate());
    }

    protected void updateGamestate() {
        LOG.info("Updating game state");

        GameStateDTO gameState = observableGame.getGamestate();

        if (gameState == null) {
            AlertWindow alert = new AlertWindow(Alert.AlertType.ERROR, "Gamestate error",
                "Gamestate not properly loaded",
                "Gamestate null?: " + (gameState == null));
            soundEffects.playErrorSound();
            alert.showAndWait();
            return;
        }

        // if the game has started, remove the observer and open a GameWindow
        if (gameState.getPhase() == GamePhase.ONGOING) {
            observableGame.removeObserver(observerID);
            observableGame.removeExceptionHandler(exceptionHandlerID);
//            closeWindow();
            gameTableController.setWindow(window);
            gameTableController.run(observableGame, userDTO, this);
            return;
        }

        if (gameState.getPlayers() == null) {
            AlertWindow alert = new AlertWindow(Alert.AlertType.ERROR, "Gamestate error",
                "Players not properly loaded",
                "Players null?: " + (gameState.getPlayers() == null));
            soundEffects.playErrorSound();
            alert.showAndWait();
            return;
        }

        // extract the latest userDTO for this user from the gamestate
        for (UserDTO player : gameState.getPlayers()) {
            if (player.getId() == userDTO.getId()) {
                userDTO = player;
                break;
            }
        }

        usernameField.setText(userDTO.getUserName());

        if (gameState.getGameMaster() == null) {
            gameMasterField.setText("");
        }else {
            gameMasterField.setText(gameState.getGameMaster().getUserName());
        }

        joinedPlayersTable.getItems().setAll(gameState.getPlayers());
    }

    /**
     * Checks if a player is the game master (first joined)
     */
    protected boolean isGameMaster(){
        GameStateDTO gameState = observableGame.getGamestate();
        return gameState != null && gameState.getGameMaster() != null && gameState.getGameMaster().getId() == userDTO.getId();
    }

    /**
     * Method called when Join Game button is clicked:
     * If there are less than four players, add player to game
     */
    @FXML
    public void onJoinGameButtonClicked() {
        LOG.info("Calling onJoinGameButtonClicked");

        if (observableGame.getGamestate().getPlayers().size() >= 4) {
            LOG.error("Game already has maximum number of players");
            AlertWindow alert = new AlertWindow(Alert.AlertType.ERROR, "Too many players",
                "There are already enough players in this game",
                "Please wait until the current game is over");
            soundEffects.playErrorSound();
            alert.showAndWait();
            return;
        }

        try {
            soundEffects.playJoinSound();
            clientService.joinGame();
        } catch (ServiceException e) {
            String errorMessage = "Problem joining game: ";
            LOG.error(errorMessage + e.getMessage());
            showError(e, errorMessage);
            return;
        }

        joinGameButton.setDisable(true);
    }


    /**
     * Method called when the Start Game button is clicked:
     * If the userDTO was the first to join the game, they are the "Game master" and the game will start
     * else, an error message will be posted
     */
    @FXML
    public void startGameButtonClicked() {
        LOG.info("Calling startGameButtonClicked");

        if (!isGameMaster()) {
            showNonGameMasterErrorMessage("start the game");
            return;
        }

        try {
            clientService.startGame(gameSettingsController.getCustomActionCards());
            soundEffects.playStartSound();
        } catch (ServiceException e){
            showError(e, "Failed to start game");
            return;
        }
    }

    /**
     * Shows the statistics window
     *
     * @throws ServiceException
     */
    @FXML
    public void statisticsLinkClicked() throws ServiceException {

        LOG.info("Calling statisticsLinkClicked");
        statisticsController.run(clientService.getStatistics());
    }

    /**
     * user presses on settings button
     * @param mouseEvent event
     */
    @FXML
    public void onGameSettingsClicked(MouseEvent mouseEvent) {
        LOG.info("user wants to open gamesettings");
        if(!isGameMaster()) {
            showNonGameMasterErrorMessage("adjust the gamesettings");
            return;
        }
        else {
            gameSettingsController.run();

        }
    }

    /**
     * user loads an old saved game from database instead of starting a new one
     */
    @FXML
    public void onLoadSavedGameClicked() {
        LOG.info("user wants to load saved game");
        if(!isGameMaster()) {
            showNonGameMasterErrorMessage("load a saved game");
            return;
        }
        else {
            loadSavedGameController.run();

        }
    }

    /**
     * Opens error window if non-game master tries to do anything (start the game, choose cards, etc..)
     * @param whatITried
     */
    public void showNonGameMasterErrorMessage(String whatITried) {
        LOG.error("Non-game master tried to " + whatITried);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(window);
        alert.setTitle("Non-game master cannot " + whatITried);
        alert.setHeaderText("Only game master can do this");
        alert.setContentText("Please wait for game master to " + whatITried);
        soundEffects.playErrorSound();
        alert.showAndWait();
    }

}