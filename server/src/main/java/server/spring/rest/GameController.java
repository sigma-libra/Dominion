package server.spring.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import server.service.AuthService;
import server.service.StatisticsService;
import server.service.UserService;
import server.service.exception.InsufficientPermissionsException;
import server.service.exception.InvalidTokenException;
import server.service.exception.ServiceException;
import server.spring.rest.exception.BadRequestException;
import shared.domain.engine.GamePhase;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;
import shared.dto.GameInfoDTO;
import shared.dto.GameStateDTO;
import shared.dto.UserDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RestController for Dominion Game
 */
@RestController
@RequestMapping("/game")
public class GameController {

    private interface GameActionConsumer {
        void accept(Player player) throws GameException;
    }

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * authentication service
     */

    private final AuthService authService;

    /**
     * service to save/load game
     */
    private final UserService userService;

    /**
     * service to save game stats
     */
    private final StatisticsService statisticsService;

    /**
     * map of subscribers
     */
    private Map<String,Socket> subscribers = new HashMap<>();

    /**
     * map of players
     */
    private Map<String,Player> players = new HashMap<>();

    /**
     * set of users who joined the game
     */
    private Set<UserDTO> joinedUsers = new HashSet<>();

    /**
     * current game state
     */
    private GameState currentGameState = null;

    /**
     * gamemaster (userDTO)
     */
    private UserDTO gameMaster = null;

    @Autowired
    public GameController(AuthService authService, UserService userService, StatisticsService statisticsService) {
        this.authService = Objects.requireNonNull(authService);
        this.userService = Objects.requireNonNull(userService);
        this.statisticsService = Objects.requireNonNull(statisticsService);
    }

    /**
     * handles exception
     * @param ex InvalidTokenException
     */
    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public void handleInvalidTokenException(InvalidTokenException ex)
    {
        LOG.warn("Refusing to handle client request due to invalid authentication token: " + ex.getMessage());
    }

    /**
     * handles exception
     * @param ex InsufficientPermissionsException
     */
    @ExceptionHandler(InsufficientPermissionsException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public void handleInsufficientPermissionsException(InsufficientPermissionsException ex)
    {
        LOG.warn("Refusing to handle client request due to insufficient permissions: " + ex.getMessage());
    }

    /**
     * handles exception
     * @param ex BadRequestException
     */
    @ExceptionHandler(GameException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void handleGameException(BadRequestException ex)
    {
        LOG.warn("Failed to execute your command: " + ex.getMessage());
    }

    /**
     * handles exception
     * @param ex BadRequestException
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void handleBadRequestException(BadRequestException ex)
    {
        LOG.warn("Bad request: " + ex.getMessage());
    }

    /**
     * handles exception
     * @param ex ServiceException
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleServiceException(ServiceException ex)
    {
        LOG.warn("Internal server error: " + ex.getMessage());
    }

    /**
     * runs a task in a thread
     * @param task task assigned to a thread
     */
    private void doInThread(Runnable task){
        Thread thread = new Thread(task);
        thread.start();
    }

    /**
     * retrieves a userdto from a token
     * @param token user's auth token
     * @return userDTO
     * @throws InvalidTokenException
     */
    private UserDTO getSender(String token) throws InvalidTokenException {
        return authService.getUserFromToken(token);
    }

    /**
     * get current gamephase
     * @return GamePhase
     */
    private GamePhase currentGamePhase(){
        if (currentGameState == null)
            return GamePhase.PREPARATION;
        else if (currentGameState.isOver()) {
            int winnerId = currentGameState.getWinner().getUser().getId();
            List<Integer> playerIds = currentGameState.getPlayers().stream().map(p -> p.getUser().getId()).collect(Collectors.toList());

            GameInfoDTO gameInfo = new GameInfoDTO(playerIds, LocalDate.now(), winnerId);
            try {
                statisticsService.addGameInfo(gameInfo);
            } catch (ServiceException e){
                LOG.warn("Failed to update game statistics: "+e.getMessage());
            }
            return GamePhase.OVER;
        }
        else
            return GamePhase.ONGOING;
    }

    /**
     * get a gamestate dto for a user by user's auth token
     * @param token user's auth token
     * @return gameStateDTO
     */
    private GameStateDTO gamestateForUser(String token){

        if (currentGamePhase() == GamePhase.PREPARATION){
            GameStateDTO gameState = new GameStateDTO();

            gameState.setPhase(currentGamePhase());
            gameState.setGameMaster(gameMaster);

            ArrayList<UserDTO> playerlist = new ArrayList<>();
            for (Player p : players.values()) {
                playerlist.add(p.getUser());
            }
            gameState.setPlayers(playerlist);

            return gameState;
        }

        Player player = players.get(token);
        GameStateDTO gameState = currentGameState.toDTO(player);
        gameState.setGameMaster(gameMaster);
        return gameState;
    }

    /**
     * Sends all connected subscribers a notification that something changed and they should load the latest gamestate
     * from the server.
     */
    private void notifyAllSubscribers(){
        for (String token : subscribers.keySet()){
            Socket socket = subscribers.get(token);
            doInThread(() -> notifySubscriber(socket, token));
        }
    }

    /**
     * notifys subsribers to get game update
     * @param socket socket
     */
    private void notifySubscriber(Socket socket, String token){
        try {
            OutputStream stream = socket.getOutputStream();
            stream.write(1);
        } catch (IOException e){
            LOG.warn("Failed to send game update notification to a connected subscriber: " + e.getMessage());
            LOG.warn("Forcefully removing player from the game");
            try {
                leaveGame(token);
            } catch (Exception ex) {
                LOG.error("Failed to remove player from the game: "+ex.getMessage());
            }
        }
    }

    /**
     * Performs an action for the player who sent the request, then notifies all other players that the
     * gamestate has changed.
     *
     * @param token The authentication token of the player who sent the request
     * @param action A function that takes a Player as parameter and performs some action with it.
     * @throws InvalidTokenException
     */
    private void performPlayerAction(String token, GameActionConsumer action) throws InvalidTokenException, GameException {
        Player player = players.get(token);
        if (player == null) {
            throw new InvalidTokenException("authToken doesn't match any of the players in this game");
        }
        action.accept(player);

        // notify all subscribers of the change
        notifyAllSubscribers();
    }

    /**
     * Subscribe to receive notifications when the game state changes.
     *
     * @param token The authentication token of the player who sent the request
     * @param port The port where the client is waiting for a socket connection from the server
     * @param request Automatically passed by the Spring framework. Used to find the player's IP address.
     * @throws ServiceException
     * @throws InvalidTokenException
     */
    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    public @ResponseBody GameStateDTO subscribeToGameUpdates(@RequestHeader(value="Token") String token, @RequestParam int port, HttpServletRequest request) throws ServiceException, BadRequestException {
        LOG.debug("Calling subscribeToGameUpdates");

        // if the game is already started, throw an error
        if (currentGamePhase() != GamePhase.PREPARATION)
            throw new BadRequestException("You can't subscribe to a game that has already started");

        if (subscribers.keySet().contains(token)) {
//            throw new BadRequestException("You have already subscribed to this game");
            // close the previous connection
            Socket socket = subscribers.get(token);
            try {
                socket.close();
            } catch (IOException e){}
        }

        Socket socket;
        try {
            socket = new Socket(request.getRemoteAddr(), port);
            subscribers.put(token, socket);
        } catch (IOException e) {
            throw new ServiceException("failed to establish socket connection");
        }

        return gamestateForUser(token);
    }

    /**
     * Saves the current game
     */
    @RequestMapping(value = "/save", method = RequestMethod.GET)
    public @ResponseBody int saveGame(@RequestHeader(value="Token") String token) throws InvalidTokenException, InsufficientPermissionsException, ServiceException {
        LOG.info("Calling saveGame");

        try {
            return userService.addGame(currentGameState);
        } catch (ServiceException e) {
            throw new ServiceException("failed to save the current game");
        }
    }

    /**
     * Get possible games for current players
     */
    @RequestMapping(value = "/loadGames", method = RequestMethod.GET)
    public @ResponseBody List<Integer> loadGames(@RequestHeader(value="Token") String token) throws InvalidTokenException, InsufficientPermissionsException, ServiceException {
        LOG.info("Calling loadGames");

        UserDTO user = getSender(token);
        if (gameMaster == null || user.getId() != gameMaster.getId())
            throw new InsufficientPermissionsException("Only the game master can save the game");

        try {
            List<UserDTO> users = new ArrayList<>();
            users.addAll(joinedUsers);
            return userService.getAllGamesForUsers(users);
        } catch (ServiceException e) {
            throw new ServiceException("failed to load the saved games");
        }
    }


    /**
     * rest interface to start a game
     * @param token user's auth token
     * @throws InvalidTokenException
     * @throws InsufficientPermissionsException
     * @throws BadRequestException
     */
    @RequestMapping(value= "/load", method = RequestMethod.POST)
    public @ResponseBody
    void loadGame(@RequestHeader(value="Token") String token, @RequestParam("id") int id) throws ServiceException, InvalidTokenException, InsufficientPermissionsException, BadRequestException {
        LOG.info("Calling loadGame");

        // if the game has already started, reject the request
        if (currentGamePhase() != GamePhase.PREPARATION)
            throw new BadRequestException("Cannot start game while a game is already taking place");

        UserDTO user = getSender(token);
        if (gameMaster == null || user.getId() != gameMaster.getId())
            throw new InsufficientPermissionsException("Only the game master can start the game");

        if (players.size() < 2)
            throw new BadRequestException("Cannot start the game with fewer than 2 players");


        currentGameState = userService.getGameStateByID(id);

        HashMap<String, Player> savedPlayerMap = new HashMap<>();
        for (Player p : currentGameState.getPlayers()) {
            savedPlayerMap.put(p.getUser().getUserName(), p);
        }

        Map<String,Player> newPlayerMap = new HashMap<>();

        for (String tok : players.keySet()) {
            Player player = players.get(tok);
            Player savedPlayer = savedPlayerMap.get(player.getUser().getUserName());
            newPlayerMap.put(tok, savedPlayer);
        }

        this.players = new HashMap<>(newPlayerMap);

        // notify all other players that the game has started
        notifyAllSubscribers();
    }


    /**
     * Adds a player to the game.
     *
     * @param token The authentication token of the player who sent the request
     * @throws ServiceException
     * @throws InvalidTokenException
     */
    @RequestMapping(value = "/join", method = RequestMethod.POST)
    public @ResponseBody void joinGame(@RequestHeader(value="Token") String token) throws InvalidTokenException, BadRequestException {
        LOG.debug("Calling joinGame");

        if (players.keySet().contains(token))
            throw new BadRequestException("You have already joined this game");

        // if the game has already started, reject the request
        if (currentGamePhase() != GamePhase.PREPARATION)
           throw new BadRequestException("The game has already been started; it's too late to join it");

        if (players.size() >= 4)
            throw new BadRequestException("This game already has the maximum number of players");

        // the first player becomes the game master
        UserDTO user = authService.getUserFromToken(token);
        joinedUsers.add(user);
        if (gameMaster == null)
            gameMaster = user;

        Player player = new Player(user);
        players.put(token, player);

        //notify the other players that someone joined
        notifyAllSubscribers();
    }

    /**
     * Removes a player from the game.
     *
     * @param token The authentication token of the player who sent the request
     * @throws ServiceException
     * @throws InvalidTokenException
     */
    @RequestMapping(value = "/leave", method = RequestMethod.POST)
    public @ResponseBody void leaveGame(@RequestHeader(value="Token") String token) throws InvalidTokenException, BadRequestException, GameException {
        LOG.debug("Calling leaveGame");

        if (!players.keySet().contains(token))
            throw new BadRequestException("You have not joined this game");

        Player player = players.get(token);

        if (currentGameState != null) {
            currentGameState.removePlayer(player); //this can throw an exception
        }

        joinedUsers.remove(player.getUser());
        players.remove(token);
        subscribers.remove(token);

        notifyAllSubscribers();

        if (players.isEmpty()){

            currentGameState = null;
            gameMaster = null;
        }
    }

    /**
     * rest interface to start a game
     * @param token user's auth token
     * @throws InvalidTokenException
     * @throws InsufficientPermissionsException
     * @throws BadRequestException
     */
    @RequestMapping(value= "/start", method = RequestMethod.POST)
    public @ResponseBody
    void startGame(@RequestHeader(value="Token") String token, @RequestParam("custom") int [] custom) throws InvalidTokenException, InsufficientPermissionsException, BadRequestException {
        LOG.debug("Calling startGame");

        // if the game has already started, reject the request
        if (currentGamePhase() != GamePhase.PREPARATION)
            throw new BadRequestException("Cannot start game while a game is already taking place");

        UserDTO user = getSender(token);
        if (gameMaster == null || user.getId() != gameMaster.getId())
            throw new InsufficientPermissionsException("Only the game master can start the game");

        if (players.size() < 1)
            throw new BadRequestException("Cannot start the game with fewer than 2 players");

        List<Player> playerlist = new ArrayList<>();
        List<String> playLog = new ArrayList<>();
        for (Player p : players.values()) {
            playerlist.add(p);
            playLog.add(p.getUser().getUserName() + " joined");
        }
        Collections.shuffle(playerlist);


        if(custom.length != 10)
            custom = null;
        currentGameState = new GameState(playerlist, playLog, custom);

        // notify all other players that the game has started
        notifyAllSubscribers();
    }

    /**
     * game/getplayers
     * @return all users who joined the game
     */
    @RequestMapping(value = "/getplayers", method = RequestMethod.GET)
    public @ResponseBody Set<UserDTO> getPlayers() throws ServiceException {
        LOG.debug("Calling getPlayers");
        return joinedUsers;
    }

    @RequestMapping(value= "/getgamestate", method = RequestMethod.GET)
    public @ResponseBody
    GameStateDTO getGamestate(@RequestHeader(value="Token") String token) throws InvalidTokenException {
        LOG.debug("Calling getGamestate");
        return gamestateForUser(token);
    }

    @RequestMapping(value= "/playcard", method = RequestMethod.POST)
    public @ResponseBody
    void playCard(@RequestHeader(value="Token") String token, @RequestParam int card) throws InvalidTokenException, BadRequestException, GameException {
        LOG.debug("Calling playCard");

        if (currentGamePhase() != GamePhase.ONGOING)
            throw new BadRequestException("Cannot play cards while the game isn't ongoing");

        performPlayerAction(token, player -> currentGameState.playCard(player, card));
    }

    /**
     * user want to buy a card from a pile
     * @param token user's auth token
     * @param pile the pile the card is bought from
     * @throws InvalidTokenException
     * @throws BadRequestException
     */
    @RequestMapping(value= "/buycard", method = RequestMethod.POST)
    public @ResponseBody
    void buyCard(@RequestHeader(value="Token") String token, @RequestParam int pile) throws InvalidTokenException, BadRequestException, GameException {
        LOG.debug("Calling buyCard");

        if (currentGamePhase() != GamePhase.ONGOING)
            throw new BadRequestException("Cannot buy cards while the game isn't ongoing");

        performPlayerAction(token, player -> currentGameState.buyCard(player, pile));
    }

    @RequestMapping(value= "/finisheffect", method = RequestMethod.POST)
    public @ResponseBody
    void finishInteractiveEffect(@RequestHeader(value="Token") String token, @RequestParam int[] choices) throws InvalidTokenException, BadRequestException, GameException {
        LOG.debug("Calling finishInteractiveEffect");

        if (currentGamePhase() != GamePhase.ONGOING) {
            throw new BadRequestException("Cannot complete effects while the game isn't ongoing");
        }

        performPlayerAction(token, player -> currentGameState.executeEffect(player, choices));
    }



    /**
     * user calls endphase
     * @param token user's auth token
     * @throws InvalidTokenException
     * @throws BadRequestException
     */
    @RequestMapping(value= "/endphase", method = RequestMethod.POST)
    public @ResponseBody
    void endPhase(@RequestHeader(value="Token") String token) throws InvalidTokenException, BadRequestException, GameException {
        LOG.debug("Calling endPhase");

        if (currentGamePhase() != GamePhase.ONGOING) {
            throw new BadRequestException("Cannot perform this action before the game has started");

        }

        performPlayerAction(token, player -> currentGameState.finishPhase(player));
    }

}
