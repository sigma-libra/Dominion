package server.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import server.service.exception.ServiceException;
import shared.domain.engine.GameState;
import shared.dto.UserDTO;

import java.util.List;
import java.util.Set;

/**
 * service for handling a user
 */
public interface UserService extends UserDetailsService {

    /**
     * adds a user for saving
     * @param userName
     */
    UserDTO addUser(String userName, String password) throws ServiceException;

    /**
     * get UserDTO by ID
     * @param id
     * @return corresponding UserDTO
     * @throws ServiceException
     */
    UserDTO getUserByID(Integer id) throws ServiceException;

    /**
     * get UserDTO by username
     *
     * @param username
     * @return corresponding UserDTO
     * @throws ServiceException
     */
    UserDTO getUserByUsername(String username) throws ServiceException;

    /**
     * get all users saved in DB
     * @return a set of Users
     * @throws ServiceException
     */
    Set<UserDTO> getAllUsers() throws ServiceException;

    /**
     * get all games for a paticular group of users in the DB
     * @param users all users that should be partisicpating
     * @return all IDs of the games for this user
     * @throws ServiceException
     */
    List<Integer> getAllGamesForUsers(List<UserDTO> users) throws ServiceException;

    /**
     * get GameState saved with certain ID in db
     *
     * @param id
     * @return the gamestate
     * @throws ServiceException
     */
    GameState getGameStateByID(int id) throws ServiceException;

    /**
     * saves a gameState in the DB
     * @param gameState to be saved in the DB
     * @throws ServiceException
     * @return the id with wich the gameState was saved in the DB
     */
    int addGame(GameState gameState) throws ServiceException;
}