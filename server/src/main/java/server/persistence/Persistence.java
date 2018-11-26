package server.persistence;

import server.persistence.exception.PersistenceException;
import shared.domain.engine.GameState;
import shared.dto.GameInfoDTO;
import shared.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Interface for persistence layer
 */
public interface Persistence {

    /**
     * get a set of all users
     * @return Set of all users saved in DB
     * @throws PersistenceException
     */
    Set<UserDTO> getAllUsers() throws PersistenceException;

    /**
     * get UserDTO by id
     * @param id UserDTO ID
     * @return UserDTO object with corresponding id
     * @throws PersistenceException
     */
    UserDTO getUserById(Integer id) throws PersistenceException;

    /**
     * @param userDTO UserDTO to be saved in database
     * @throws PersistenceException
     */
    UserDTO addUser(UserDTO userDTO) throws PersistenceException;

    /**
     * get user by name do avoid overlaps
     * @param userName
     * @return UserDTO by Name
     * @throws PersistenceException
     */
    UserDTO getUserByUserName(String userName) throws PersistenceException;

    /**
     * get all games for group of users
     * @param users all users that should be partisicpating
     * @return all ids of the games for those users
     * @throws PersistenceException
     */
    List<Integer> getAllGamesForUsers(List<UserDTO> users) throws PersistenceException;

    /**
     * @param gameState to be saved in database
     * @throws PersistenceException
     * @return the id of the gameState in the db
     */
    int addGame(GameState gameState) throws PersistenceException;

    /**
     * gets the game state saved with this id
     * @param id
     * @return the gameState
     * @throws PersistenceException
     */
    GameState getGameStateByID(int id) throws PersistenceException;

    /**
     *
     * @param dto contains players, date, winner
     * @throws PersistenceException exception
     */
    void addGameInfo(GameInfoDTO dto) throws PersistenceException;

    /**
     *
     * @return all game info
     * @throws PersistenceException exception
     */
    ArrayList<GameInfoDTO> getAllGameInfo() throws PersistenceException;


}