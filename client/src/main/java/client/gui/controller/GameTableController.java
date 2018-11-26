package client.gui.controller;

import client.domain.ObservableGame;
import client.gui.AlertWindow;
import client.gui.SoundEffects;
import client.gui.adapter.table.PlayerStats;
import client.service.ClientService;
import client.service.exception.ServiceException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import shared.domain.cards.Card;
import shared.domain.cards.TreasureCard;
import shared.domain.effect.PlayerChoosesEffect;
import shared.domain.effect.impl.ChooseCardsEffect;
import shared.domain.effect.impl.RevealCardsEffect;
import shared.domain.engine.GamePhase;
import shared.domain.engine.TurnPhase;
import shared.domain.exceptions.InvalidCardTypeID;
import shared.dto.GameStateDTO;
import shared.dto.UserDTO;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.IntStream;

/**
* Controller for a TableTop
*/
@Controller
public class GameTableController extends FXMLControllerBase {

    private static final int PADDING = 10;

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private GameStateDTO gameStateDTO;

    private SoundEffects soundEffects;

    /**
     * For each supply pile, there is a controller for the top card (or a series for the hand)
     *
     * supply pile indices
     * indices of piles:
     * 0 = province
     * 1 = duchy
     * 2 = estate
     * 3 = curse
     * 4 = gold
     * 5 = silver
     * 6 = copper
     * 7-16 = kingdom cards
     */

    //Tabletop decks
    private LinkedHashMap<Integer, CardController> kingdomCardPilesTopRow = new LinkedHashMap<>();
    private LinkedHashMap<Integer, CardController> kingdomCardPilesBottomRow = new LinkedHashMap<>();

    private LinkedHashMap<Integer, CardController> victoryCardPiles = new LinkedHashMap<>();
    private LinkedHashMap<Integer, CardController> treasureCardsPiles = new LinkedHashMap<>();

    private LinkedHashMap<Integer, CardController> trashPile = new LinkedHashMap<>();

    //Hand decks
    // A list is needed because there are identical Cards in hand
    private List<CardController> handCards = new ArrayList<>();
    private List<CardController> playedCards = new ArrayList<>();
    private LinkedHashMap<Integer, CardController> discardCardsPile = new LinkedHashMap<>();
    private LinkedHashMap<Integer, CardController> drawCardsPile = new LinkedHashMap<>();

    private JoinGameController joinGameController;

    private ObservableGame observableGame;
    private UserDTO user;
    private int observerID;
    private int exceptionHandlerID;
    private int playerIndex;
    private String[] usernames;

    private final ClientService clientService;
    private final ChooseCardsController chooseCardsController;
    private final RevealCardsController revealCardsController;
    private final EndScreenController endScreenController;

    private final String playingColor = "-fx-background-color: #CFB53B;";
    private final String notPlayingColor = "-fx-background-color: #006400";

    //FXML values

    //Tabletop card rows
    @FXML private Pane VictoryCardsPane;
    @FXML private Pane TreasureCardsPane;
    @FXML private Pane ActionCardsPane1;
    @FXML private Pane ActionCardsPane2;

    //Positions of players indicated
    @FXML private Text bottomUsername;
    @FXML private Pane bottomNameTag;

    @FXML private Text leftUsername;
    @FXML private Pane leftNameTag;

    @FXML private Text topUsername;
    @FXML private Pane topNameTag;

    @FXML private Text rightUsername;
    @FXML private Pane rightNameTag;

    //private info
    @FXML private Text turnIndicator;
    @FXML private Text coinIndicator;
    @FXML private Text phaseIndicator;
    @FXML private Button endPhaseButton;
    @FXML private Text actionCountIndicator;
    @FXML private Text buyCountIndicator;

    //public info
    @FXML TableView<PlayerStats> PlayerStatsTable;
    @FXML private TableColumn<PlayerStats, String> playerNamesColumn;
    @FXML private TableColumn<PlayerStats, Integer> playerDeckSizesColumn;
    @FXML private TableColumn<PlayerStats, Integer> playerPointsColumn;


    //Private card rows
    @FXML private Pane DiscardPane;
    @FXML private Pane HandPane;
    @FXML private Pane DrawPane;
    @FXML private Pane playedCardsPane;

    //Pile size indicators
    @FXML private Text VictoryCard1Size;
    @FXML private Text VictoryCard2Size;
    @FXML private Text VictoryCard3Size;
    @FXML private Text VictoryCard4Size;

    @FXML private Text TreasureCard1Size;
    @FXML private Text TreasureCard2Size;
    @FXML private Text TreasureCard3Size;

    @FXML private Text KingdomCard1Size;
    @FXML private Text KingdomCard2Size;
    @FXML private Text KingdomCard3Size;
    @FXML private Text KingdomCard4Size;
    @FXML private Text KingdomCard5Size;
    @FXML private Text KingdomCard6Size;
    @FXML private Text KingdomCard7Size;
    @FXML private Text KingdomCard8Size;
    @FXML private Text KingdomCard9Size;
    @FXML private Text KingdomCard10Size;

    @FXML private Text DrawPileSize;
    @FXML private Text DiscardPileSize;

    @FXML private TextArea PlayLog;


    @Autowired
    public GameTableController(ClientService clientService, ChooseCardsController chooseCardsController, RevealCardsController revealCardsController, SoundEffects soundEffects, EndScreenController endScreenController){
        super("/fxml/Game.fxml", "Dominion Game");

        this.clientService = Objects.requireNonNull(clientService);
        this.chooseCardsController = Objects.requireNonNull(chooseCardsController);
        this.revealCardsController = Objects.requireNonNull(revealCardsController);
        this.soundEffects = Objects.requireNonNull(soundEffects);
        this.endScreenController = Objects.requireNonNull(endScreenController);
    }

    public void run(ObservableGame observableGame, UserDTO user, JoinGameController joinGameController){
        this.observableGame = observableGame;
        this.user = user;

        this.joinGameController = joinGameController;

        exceptionHandlerID = observableGame.addExceptionHandler(ex -> handleGameExceptionFromThread(ex));
        observerID = observableGame.addObserver(game -> updateGameStateFromThread(game));
        run();
    }

    @Override
    protected void initScene(){
        getWindow().setOnCloseRequest(e -> leaveGame());

        kingdomCardPilesTopRow.clear();
        kingdomCardPilesBottomRow.clear();
        victoryCardPiles.clear();
        treasureCardsPiles.clear();
        trashPile.clear();
        handCards.clear();
        playedCards.clear();
        discardCardsPile.clear();
        drawCardsPile.clear();

        GameStateDTO gameState = observableGame.getGamestate();
        if (gameState != null) {
            updateGameState(gameState);
        }
    }

    /**
     * Handles a game exception stemming from server or a different client
     * @param ex
     */
    protected void handleGameExceptionFromThread(Exception ex){
        Platform.runLater(() -> handleGameException(ex));
    }

    /**
     * Handles an exception
     *
     * @param ex
     */
    protected void handleGameException(Exception ex){
        LOG.warn("Exception occurred while listening for game state updates: "+ex.getMessage());

        LOG.info("Connection to server lost, exiting...");
        showErrorAndWait(ex, "Connection to server lost");
        closeWindow();
    }

    /**
     * Informs the server that the user left the game.
     * Called when the window is closed.
     */
    protected void leaveGame() {
        observableGame.removeObserver(observerID);
        try {
            clientService.leaveGame();
        } catch (ServiceException e){
            LOG.warn("Failed to properly disconnect from the game: " + e.getMessage());
        }
    }

    /**
     * Updates the gamestate based on a notification from server
     *
     * @param observableGame
     */
    protected void updateGameStateFromThread(ObservableGame observableGame) {
        Platform.runLater(() -> updateGameState(observableGame.getGamestate()));
    }

    /**
     * Instantiates a new CardController displaying a certain card, and connects an appropriate callback function to
     * its onClick event.
     * If `isHand` is true, the callback will send a playCard command to the server. Otherwise, it'll send a
     * buyCard command to the server.
     * `pileID` is the ID that will be sent to the server when the card is clicked.
     * `clickableIndices` is a list of playable/buyable cards. If this list doesn't contain `pileID`, the CardController
     * won't be clickable.
     *
     * @param cardID The ID of the Card to display
     * @param isHand Whether this CardController is used for one of the cards in the player's hand
     * @param pileID The ID to send to the server when the card is clicked
     * @param clickableIndices A list of playable/buyable cards, as received from the server
     * @return An appropriate CardController
     */
    private CardController makeClickableCard(int cardID, boolean isHand, int pileID, int[] clickableIndices){
        boolean clickable = clickableIndices != null &&
                            IntStream.of(clickableIndices).anyMatch(x -> x == pileID);

        Runnable onClick = () -> {
            try {
                if (isHand) {
                    clientService.playCard(pileID);
                    playedCards.add(new CardController(cardID));

                    try {
                        Card card = Card.fromID(cardID);
                        if(card instanceof TreasureCard) {
                            soundEffects.playCoinSound();
                        } else {
                            soundEffects.playActionPlaySound();
                        }
                    } catch (InvalidCardTypeID invalidCardTypeID) {
                        showError(invalidCardTypeID, "Invalid card type");
                    }
                    soundEffects.playCoinSound();
                }
                else {
                    clientService.buyCard(pileID);
                    soundEffects.playGainSound();
                }
            } catch (ServiceException e){
                showError(e, "Failed to perform action");
            }
        };
        CardController controller = new CardController(cardID, onClick);
        if (!clickable)
            Platform.runLater(() -> controller.disable());

        return controller;
    }

    /**
     * Updates the GUI to display the latest game state.
     * Called whenever the game state changes.
     *
     * @param gameState The current game state
     */
    protected void updateGameState(GameStateDTO gameState) {
        LOG.trace("Calling updateGameState");

        this.gameStateDTO = gameState;

        if (gameState == null) {
            AlertWindow alert = new AlertWindow(Alert.AlertType.ERROR, "Gamestate error",
                "Gamestate not properly loaded",
                "Gamestate null?: " + (gameState == null));
            soundEffects.playErrorSound();
            alert.showAndWait();
            return;
        }

        List<UserDTO> players = gameState.getPlayers();

        usernames = players.stream().map(p -> p.getUsername()).toArray(String[]::new);

        playerIndex = -1;
        for(int i = 0; i < players.size(); i++) {
            if(players.get(i).getId() == user.getId()) {
                playerIndex = i;
                break;
            }
        }
        if (playerIndex >= players.size())
            playerIndex = 0;

        setNames();


        int[] supplyPiles = gameState.getSupplyPilesSizes();
        int[] actionCardIDs = gameState.getActionCardIds();

        if (supplyPiles == null || supplyPiles.length < 17) {
            LOG.error("Supply piles not correctly sent from server");
            AlertWindow alert = new AlertWindow(Alert.AlertType.ERROR, "Gamestate error",
                "Cards on table top not fully initialized",
                "supplyPile null?: " + (supplyPiles == null));
            soundEffects.playErrorSound();
            alert.showAndWait();
            return;
        }
        if (actionCardIDs == null || actionCardIDs.length < 10) {
            LOG.error("ActionCardIds not correctly sent from server");
            AlertWindow alert = new AlertWindow(Alert.AlertType.ERROR, "Gamestate error",
                "Action card indexes not fully initialized",
                "ActionCardIDs null?: " + (actionCardIDs == null));
            soundEffects.playErrorSound();
            alert.showAndWait();
            return;
        }

        for (int i = 0; i < 17; i++) {
            switch (i) {
                case 0: //Province card
                    if (supplyPiles[i] == 0) {
                        victoryCardPiles.put(23, new CardController(0));
                    } else {
                        victoryCardPiles.put(23, makeClickableCard(23, false, i, gameStateDTO.getBuyablePilesIndices()));
                    }
                    VictoryCard1Size.setText(String.valueOf(supplyPiles[i]));
                    break;
                case 1: //Duchy
                    if (supplyPiles[i] == 0) {
                        victoryCardPiles.put(22, new CardController(0));
                    } else {
                        victoryCardPiles.put(22, makeClickableCard(22, false, i, gameStateDTO.getBuyablePilesIndices()));
                    }
                    VictoryCard2Size.setText(String.valueOf(supplyPiles[i]));
                    break;
                case 2: //Estate
                    if (supplyPiles[i] == 0) {
                        victoryCardPiles.put(21, new CardController(0));
                    } else {
                        victoryCardPiles.put(21, makeClickableCard(21, false, i, gameStateDTO.getBuyablePilesIndices()));
                    }
                    VictoryCard3Size.setText(String.valueOf(supplyPiles[i]));
                    break;
                case 3: //Curse
                    if (supplyPiles[i] == 0) {
                        victoryCardPiles.put(24, new CardController(0));
                    } else {
                        victoryCardPiles.put(24, makeClickableCard(24, false, i, gameStateDTO.getBuyablePilesIndices()));
                    }
                    VictoryCard4Size.setText(String.valueOf(supplyPiles[i]));
                    break;
                case 4: //gold
                    if (supplyPiles[i] == 0) {
                        treasureCardsPiles.put(13, new CardController(0));
                    } else {
                        treasureCardsPiles.put(13, makeClickableCard(13, false, i, gameStateDTO.getBuyablePilesIndices()));
                    }
                    TreasureCard1Size.setText(String.valueOf(supplyPiles[i]));
                    break;
                case 5: //silver
                    if (supplyPiles[i] == 0) {
                        treasureCardsPiles.put(12, new CardController(0));
                    } else {
                        treasureCardsPiles.put(12, makeClickableCard(12, false, i, gameStateDTO.getBuyablePilesIndices()));
                    }
                    TreasureCard2Size.setText(String.valueOf(supplyPiles[i]));
                    break;
                case 6: //copper
                    if (supplyPiles[i] == 0) {
                        treasureCardsPiles.put(11, new CardController(0));
                    } else {
                        treasureCardsPiles.put(11, makeClickableCard(11, false, i, gameStateDTO.getBuyablePilesIndices()));
                    }
                    TreasureCard3Size.setText(String.valueOf(supplyPiles[i]));
                    break;
                default: // action cards
                    int index = i - 7;
                    int actionCardID = actionCardIDs[index];
                    if (i < 12) {
                        if (supplyPiles[i] == 0) {
                            kingdomCardPilesTopRow.put(actionCardID, new CardController(0));
                        } else {
                            kingdomCardPilesTopRow.put(actionCardID, makeClickableCard(actionCardID, false, i, gameStateDTO.getBuyablePilesIndices()));
                        }
                    } else {
                        if (supplyPiles[i] == 0) {
                            kingdomCardPilesBottomRow.put(actionCardID, new CardController(0));
                        } else {
                            kingdomCardPilesBottomRow.put(actionCardID, makeClickableCard(actionCardID, false, i, gameStateDTO.getBuyablePilesIndices()));
                        }
                    }
                    break;

            }

            KingdomCard1Size.setText(String.valueOf(supplyPiles[7]));
            KingdomCard2Size.setText(String.valueOf(supplyPiles[8]));
            KingdomCard3Size.setText(String.valueOf(supplyPiles[9]));
            KingdomCard4Size.setText(String.valueOf(supplyPiles[10]));
            KingdomCard5Size.setText(String.valueOf(supplyPiles[11]));
            KingdomCard6Size.setText(String.valueOf(supplyPiles[12]));
            KingdomCard7Size.setText(String.valueOf(supplyPiles[13]));
            KingdomCard8Size.setText(String.valueOf(supplyPiles[14]));
            KingdomCard9Size.setText(String.valueOf(supplyPiles[15]));
            KingdomCard10Size.setText(String.valueOf(supplyPiles[16]));
        }

        handCards.clear();
        int[] handCardFromServer = gameStateDTO.getHandCardIds();
        if (handCardFromServer != null) {
            for (int i = 0; i < handCardFromServer.length; i++) {
                Integer cardID = handCardFromServer[i];
                handCards.add(makeClickableCard(cardID, true, i, gameStateDTO.getPlayableCardsIndices()));
            }
        }

        setUpDrawAndDiscard();

        printPointTable();

        refreshAllPanes();

        //set turn info
        printTurnInfo();

        printPlayLog();
        PlayLog.selectEnd();

        if (gameStateDTO.getPhase().equals(GamePhase.OVER)) {
            LOG.info("Game is over");
            leaveGame();

            soundEffects.playEndSound();
            endScreenController.setWindow(getWindow());
            endScreenController.run(gameStateDTO, playerIndex, joinGameController, user, this);
            return;
        }

        runInteractiveEffect();
    }



    /**
     * Removes all CardControllers from the GUI and then adds the CardControllers
     * from the LinkedHashMaps in their place.
     */
    private void refreshAllPanes() {
        LOG.debug("Calling refreshAllPanes");
        refreshPane(VictoryCardsPane, victoryCardPiles);
        refreshPane(TreasureCardsPane, treasureCardsPiles);
        refreshPane(ActionCardsPane1, kingdomCardPilesTopRow);
        refreshPane(ActionCardsPane2, kingdomCardPilesBottomRow);
        refreshPane(HandPane, handCards);
        refreshPane(DiscardPane, discardCardsPile);
        refreshPane(DrawPane, drawCardsPile);
        refreshPane(playedCardsPane, playedCards);
    }


    /**
     * Refreshes a pane based on the new values in decks array
     */
    private void refreshPane(Pane pane, HashMap<Integer, CardController> decks) {
        pane.getChildren().clear();
        int xPosition = 0;
        for (CardController cardController : decks.values()) {
            pane.getChildren().add(cardController);
            cardController.setLayoutX(xPosition * (cardController.getFitWidth() + PADDING));
            xPosition++;
        }
    }

    /**
     * Refreshes a pane based on the new values in decks array
     */
    private void refreshPane (Pane pane, List <CardController> decks){
        pane.getChildren().clear();
        int xPosition = 0;
        for (CardController cardController : decks) {
            pane.getChildren().add(cardController);
            cardController.setLayoutX(xPosition * (cardController.getFitWidth() + PADDING));
            xPosition++;
        }
    }


    /**
     * Stops player from being able to click anything, for example if it's not their turn
     */
    private void disableCardClicking() {
        for (CardController controller : victoryCardPiles.values()) {
            controller.disable();
        }
        for (CardController controller : treasureCardsPiles.values()) {
            controller.disable();
        }
        for (CardController controller : kingdomCardPilesTopRow.values()) {
            controller.disable();
        }
        for (CardController controller : kingdomCardPilesBottomRow.values()) {
            controller.disable();
        }
        for (CardController controller : drawCardsPile.values()) {
            controller.disable();
        }
        for (CardController controller : discardCardsPile.values()) {
            controller.disable();
        }
        for (CardController controller : handCards) {
            controller.disable();
        }
        for (CardController controller : playedCards) {
            controller.disable();
        }
    }

    private void enableCardClicking() {
        for(CardController controller: victoryCardPiles.values()) {
            controller.enable();
        }
        for(CardController controller: treasureCardsPiles.values()) {
            controller.enable();
        }
        for(CardController controller: kingdomCardPilesTopRow.values()) {
            controller.enable();
        }
        for(CardController controller: kingdomCardPilesBottomRow.values()) {
            controller.enable();
        }
        for(CardController controller: drawCardsPile.values()) {
            controller.enable();
        }
        for(CardController controller: discardCardsPile.values()) {
            controller.enable();
        }
        for(CardController controller: handCards) {
            controller.enable();
        }
        for(CardController controller: playedCards) {
            controller.enable();
        }
    }

    public GameStateDTO getGameStateDTO() {
        return gameStateDTO;
    }

    public void setGameStateDTO(GameStateDTO gameStateDTO) {
        this.gameStateDTO = gameStateDTO;
    }

    public ClientService getClientService() {
        return clientService;
    }


    /**
     * Ends a gamePhase (Ex: Action phase -> Buy phase)
     */
    @FXML
    public void endPhaseButtonClicked() {
        LOG.info("Calling endPhaseButtonClicked");
        try {
            clientService.endPhase();
            playedCards.clear();
            soundEffects.playShuffleSound();
            PlayLog.selectEnd();
        } catch (ServiceException e) {
            LOG.error("Problem ending phase: " + e.getMessage());
            AlertWindow alert = new AlertWindow(Alert.AlertType.ERROR, "Gamestate error",
                "Problem ending phase",
                "Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Saves a game and shows a window with its saved ID
     */
    @FXML
    public void onSaveGameClicked() {
        LOG.info("Calling onSaveGameClicked");
        try {
            Integer savedID = clientService.saveGame();
            AlertWindow alert = new AlertWindow(Alert.AlertType.INFORMATION, "Game saved",
                "Current gamestate was saved with ID: " + savedID.toString(),
                "It can be loaded from the lobby next time everyone is here!");
            alert.showAndWait();
        } catch (ServiceException e) {
            LOG.error("Problem saving game: " + e.getMessage());
            AlertWindow alert = new AlertWindow(Alert.AlertType.ERROR, "Gamestate error",
                "Problem saving game",
                "Error: " + e.getMessage());
            alert.showAndWait();
        }
    }


    /**
     * Prints the names of the players on the tabletop at their appropriate
     * place and highlights the one currently playing
     */
    private void setNames() {
        int currently_playing = gameStateDTO.getCurrentlyPlaying();

        int nbPlayers = gameStateDTO.getPlayers().size();

        switch (nbPlayers) {
            case 1:
                bottomNameTag.setStyle(playingColor);
                bottomUsername.setText(user.getUsername());
                break;
            case 2:
                //set color of name box
                if(currently_playing == playerIndex) {
                    topNameTag.setStyle(notPlayingColor);
                    bottomNameTag.setStyle(playingColor);
                } else {
                    topNameTag.setStyle(playingColor);
                    bottomNameTag.setStyle(notPlayingColor);
                }

                //set names
                topUsername.setText(usernames[1 - playerIndex]);
                bottomUsername.setText(user.getUsername());
                leftUsername.setText("");
                rightUsername.setText("");

                break;
            case 3:
                if(currently_playing == playerIndex) {
                    topNameTag.setStyle(notPlayingColor);
                    bottomNameTag.setStyle(playingColor);
                    leftNameTag.setStyle(notPlayingColor);
                } else if(currently_playing == ((playerIndex + 1)%3)) {
                    topNameTag.setStyle(notPlayingColor);
                    bottomNameTag.setStyle(notPlayingColor);
                    leftNameTag.setStyle(playingColor);
                } else if (currently_playing == ((playerIndex + 2)%3)) {
                    topNameTag.setStyle(playingColor);
                    bottomNameTag.setStyle(notPlayingColor);
                    leftNameTag.setStyle(notPlayingColor);
                }

                topUsername.setText(usernames[(playerIndex + 2) % 3]);
                bottomUsername.setText(user.getUsername());
                leftUsername.setText(usernames[(playerIndex + 1) % 3]);
                rightUsername.setText("");

                break;
            case 4:

                if(currently_playing == ((playerIndex + 1)%4)) {
                    topNameTag.setStyle(notPlayingColor);
                    bottomNameTag.setStyle(notPlayingColor);
                    leftNameTag.setStyle(playingColor);
                    rightNameTag.setStyle(notPlayingColor);
                }

                if(currently_playing == ((playerIndex + 2)%4)) {
                    topNameTag.setStyle(playingColor);
                    bottomNameTag.setStyle(notPlayingColor);
                    leftNameTag.setStyle(notPlayingColor);
                    rightNameTag.setStyle(notPlayingColor);
                }

                if(currently_playing == ((playerIndex + 3)%4)) {
                    topNameTag.setStyle(notPlayingColor);
                    bottomNameTag.setStyle(notPlayingColor);
                    leftNameTag.setStyle(notPlayingColor);
                    rightNameTag.setStyle(playingColor);
                }

                topUsername.setText(usernames[(playerIndex + 2) % 4]);
                bottomUsername.setText(user.getUsername());
                leftUsername.setText(usernames[(playerIndex + 1) % 4]);
                rightUsername.setText(usernames[(playerIndex + 3) % 4]);

                break;
        }
    }

    /**
     * Draws a card on draw and discard piles depending on whether it's empty or not
     */
    private void setUpDrawAndDiscard() {
        //Draw and discard
        DrawPileSize.setText(String.valueOf(gameStateDTO.getDrawSize()));
        DiscardPileSize.setText(String.valueOf(gameStateDTO.getDiscardSize()));

        drawCardsPile.put(1, new CardController(gameStateDTO.getDrawSize() == 0 ? 0 : 1));
        discardCardsPile.put(2, new CardController(gameStateDTO.getDiscardSize() == 0 ? 0 : 2));
    }

    /**
     * Prints the table with the player names and their point numbers
     */
    private void printPointTable() {
        List<PlayerStats> playerStatsList = new ArrayList<>();
        for (int i = 0; i < usernames.length; i++) {
            PlayerStats player = new PlayerStats(
                usernames[i],
                gameStateDTO.getDeckSizes()[i],
                gameStateDTO.getVictoryPoints()[i]);

            playerStatsList.add(player);
        }

        playerNamesColumn.setCellValueFactory(new PropertyValueFactory<>("player"));
        playerDeckSizesColumn.setCellValueFactory(new PropertyValueFactory<>("Deck"));
        playerPointsColumn.setCellValueFactory(new PropertyValueFactory<>("Points"));
        PlayerStatsTable.setItems(FXCollections.observableList(playerStatsList));
    }

    /**
     * Prints information on the GUI about whose turn it is,
     * and if it's this player's turn, the coin supply, number of actions and buys available and the phase
     */
    private void printTurnInfo() {

        turnIndicator.setText("Turn: " + usernames[gameStateDTO.getCurrentlyPlaying()]);

        if (gameStateDTO.getCurrentlyPlaying() == playerIndex) {
            coinIndicator.setText("Coin supply: " + gameStateDTO.getCurrentCredit());
            if(gameStateDTO.getTurnPhase() == TurnPhase.ACTION_PHASE) {
                actionCountIndicator.setText("Action count: " + gameStateDTO.getActionCount());
                buyCountIndicator.setText("Buy count: 0");
            } else {
                actionCountIndicator.setText("Action count: 0");
                buyCountIndicator.setText("Buy count: " + gameStateDTO.getBuyCount());
            }
            phaseIndicator.setText(String.format("Phase: %s", gameStateDTO.getTurnPhase() == TurnPhase.ACTION_PHASE? "Action Phase":"Buy Phase"));
            enableCardClicking();
            endPhaseButton.setDisable(false);
        } else {
            coinIndicator.setText("Coin supply: 0");
            actionCountIndicator.setText("Action count: 0");
            buyCountIndicator.setText("Buy count: 0");
            phaseIndicator.setText("Phase: Waiting");
            disableCardClicking();
            endPhaseButton.setDisable(true);
        }
    }

    /**
     * Prints the content of the playLog to the window
     */
    private void printPlayLog() {
        StringBuilder textToPrint = new StringBuilder("Plays: ");
        List<String> playLog = gameStateDTO.getGamePlayLog();
        for(String play: playLog) {
            textToPrint.append("\n " + play);
        }
        PlayLog.setText(textToPrint.toString());
        PlayLog.selectEnd();
    }

    /**
     * Runs an interactive effect if the player has one queued
     */
    private void runInteractiveEffect() {
        PlayerChoosesEffect interactiveEffect = gameStateDTO.getPendingChoiceEffect();

        if (interactiveEffect != null){
            if (interactiveEffect instanceof ChooseCardsEffect)
                chooseCardsController.run((ChooseCardsEffect)interactiveEffect);
            else if (interactiveEffect instanceof RevealCardsEffect)
                revealCardsController.run((RevealCardsEffect)interactiveEffect);
            else {
                Exception ex = new Exception(String.format("Received unknown interactive effect of type \"%s\" from the server", interactiveEffect.getClass().getName()));
                showError(ex, "Unknown CardEffect received");
            }
        }
        PlayLog.selectEnd();
    }

}
