package client.service;

import client.domain.ObservableGame;
import client.service.exception.ServerException;
import client.service.exception.ServiceException;
import shared.dto.*;

import java.util.List;
import java.util.Set;

/**
 * Interface to connect client with server
 */
public interface ClientService {

    /**
     * Retrieves a player's profile based on a given username from server to allow login
     *
     * @param username
     * @return
     */
    UserDTO getUserByUsername(String username) throws ServiceException;

    /**
     * Adds a user to the user database in server and returns the user created
     *
     * @param username
     * @return
     */

    UserDTO addUserToDatabase(String username, String password) throws ServiceException;

    ObservableGame subscribeToGameUpdates() throws ServiceException;

    /**
     * Marks a user as ready for a game (and sets to game master if first user ready)
     *
     * @return
     * @throws ServiceException
     */
    void joinGame() throws ServiceException;

    /**
     * user leaves a game
     * @throws ServiceException
     */
    void leaveGame() throws ServiceException;

    /**
     * gamemaster starts a game
     * @throws ServiceException
     */
    void startGame(int [] customActionCards) throws ServiceException;


    /**
     * Gets all the users who joined a game
     *
     * @return set of userDTOs
     */
    Set<UserDTO> getUsersWhoJoinedGame() throws ServiceException;

    /**
     * Retrieves a player's game state based on a given username from server
     *
     * @return GameStateDTO
     */
    GameStateDTO getGameState() throws ServiceException;


    /**
     * authenticate against server and retrieve response (jwtoken)
     *
     * @param username
     * @param password
     * @return AuthResponseDTO
     */
    AuthResponseDTO authenticate(String username, String password) throws ServiceException;

    /**
     * User wants to play a card
     * @param cardID corresponding cardID
     * @throws ServiceException
     */
    void playCard(int cardID) throws ServiceException;

    /**
     * User has made an interactive choice
     * @param ids list of chosen ids
     * @throws ServiceException
     */
    void finishInteractiveEffect(List<Integer> ids) throws ServiceException;

    /**
     * user wants to buy a card from a pile
     * @param pileID corresponding pile iD
     * @throws ServiceException
     */
    void buyCard(int pileID) throws ServiceException;

    /**
     * user wants to end the game phase
     * @throws ServiceException
     */
    void endPhase() throws ServiceException;

    /**
     * loads a saved game from Server
     *
     * @param gameID
     * @return
     * @throws ServiceException
     */
    void loadSavedGameState(Integer gameID) throws ServiceException;

    /**
     * saves a game in database
     * @return the id of the saved game
     */
    Integer saveGame() throws ServiceException;

    /**
     * Loads ids of all the saved games with the users currently joined
     * @return
     */
    List<Integer> loadGameIDs() throws ServerException;

    /**
     * sends gameinfo via restapi to server
     *

     * @param gameInfoDTO
     * @throws ServiceException
     */
    void sendGameInfo(GameInfoDTO gameInfoDTO) throws ServiceException;

    /**
     * get statisticsDTO
     *
     * @return statisticsDTO
     */
    StatisticsDTO getStatistics() throws ServiceException;
}
