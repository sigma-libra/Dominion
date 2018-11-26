package client.service.impl;

import client.domain.ObservableGame;
import client.service.ClientService;
import client.service.exception.ServerException;
import client.service.exception.ServiceException;
import client.service.exception.UserNotModifiedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import shared.dto.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import client.rest.RestApi;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Class to connect client with server
 */
@Service
public class ClientServiceImpl implements ClientService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final RestApi restApi;

    private String authToken = "";


    @Autowired
    public ClientServiceImpl(RestApi restApi) {
        this.restApi = Objects.requireNonNull(restApi);
    }

    /**
     * Retrieves a user's profile based on a given username from server to allow login
     *
     * @param username
     * @return
     */
    @Override
    public UserDTO getUserByUsername(String username) throws ServiceException {
        LOG.debug("Called getUserByUsername");
        UserDTO userDTO = restApi.getUserByUsername(username);
        return userDTO;
    }


    /**
     * Adds a user to the user database in server and returns the user created
     *
     * @param username
     * @return
     */
    @Override
    public UserDTO addUserToDatabase(String username, String password) throws ServiceException {
        LOG.debug("Called addUserToDatabase");
        UserDTO userDTO = restApi.addUser(new AuthRequestDTO(username, password));
        return userDTO;
    }

    @Override
    public ObservableGame subscribeToGameUpdates() throws ServiceException {
        return restApi.subscribeToGame(authToken);
    }

    /**
     * Marks a user as ready for a game (and sets to game master if first user ready)
     * @return
     * @throws UserNotModifiedException
     */
    @Override
    public void joinGame() throws ServiceException {
        LOG.debug("Called joinGame");
        restApi.joinGame(authToken);
    }

    @Override
    public void leaveGame() throws ServiceException {
        LOG.debug("Called leaveGame");
        restApi.leaveGame(authToken);
    }

    /**
     * Starts the game, if all preconditions are met and you're the game master
     * @return
     * @throws ServerException
     */
    @Override
    public void startGame(int [] customActionCards) throws ServiceException {
        LOG.debug("Called startGame");
        restApi.startGame(authToken,customActionCards);
    }

    /**
     * Gets all the users who joined a game
     * @return
     */
    @Override
    public Set<UserDTO> getUsersWhoJoinedGame() throws ServiceException {
        LOG.debug("Called getUsersWhoJoinedGame");
        return restApi.getUsersInGame();
    }

    /**
     * Retrieves a user's game state based on a given username from server to allow login
     *
     * @return
     */
    @Override
    public GameStateDTO getGameState() throws ServiceException {
        LOG.debug("Called getGameStateByUsername");
        GameStateDTO gameState = restApi.getGameState(authToken);
        return gameState;
    }

    /**
     * loads a saved game from Server
     *
     * @param gameID
     * @return
     * @throws ServiceException
     */
    @Override
    public void loadSavedGameState(Integer gameID) throws ServiceException {
        LOG.info("Called loadSavedGameState");
        restApi.loadSavedGameState(authToken, gameID);
    }

    /**
     * saves a game in database
     * @return the id of the saved game
     */
    @Override
    public Integer saveGame() throws ServiceException {
        LOG.info("Called saveGame");
        return restApi.saveGame(authToken);
    }

    @Override
    public List<Integer> loadGameIDs() throws ServerException {
        LOG.info("Called loadGameIDs");
        return restApi.loadSavedGameIDs(authToken);
    }

    @Override
    public void playCard(int cardID) throws ServiceException {
        restApi.playCard(authToken, cardID);
    }

    /**
     * User has made an interactive choice
     * @param ids list of chosen ids
     * @throws ServiceException
     */
    @Override
    public void finishInteractiveEffect(List<Integer> ids) throws ServiceException {
        restApi.finishInteractiveEffect(authToken, ids);
    }

    @Override
    public void buyCard(int pileID) throws ServiceException {
        restApi.buyCard(authToken, pileID);
    }

    @Override
    public void endPhase() throws ServiceException {
        restApi.endPhase(authToken);
    }

    /**
     * authenticate against server and retrieve response (jwtoken)
     *
     * @param username
     * @param password
     * @return AuthResponseDTO
     */
    @Override
    public AuthResponseDTO authenticate(String username, String password) throws ServiceException {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername(username);
        authRequestDTO.setPassword(password);
        AuthResponseDTO authResponseDTO = restApi.authenticate(authRequestDTO);

        // save the token
        authToken = authResponseDTO.getToken();

        return authResponseDTO;
    }

    /**
     * get statisticsDTO
     *
     * @return statisticsDTO
     */
    @Override
    public StatisticsDTO getStatistics() throws ServiceException {
        return restApi.getStatistics(authToken);
    }

    /**
     * sends gameinfo via restapi to server
     *

     * @param gameInfoDTO
     * @throws ServiceException
     */
    @Override
    public void sendGameInfo(GameInfoDTO gameInfoDTO) throws ServiceException {
        restApi.addGameInfo(authToken,gameInfoDTO);
    }
}
