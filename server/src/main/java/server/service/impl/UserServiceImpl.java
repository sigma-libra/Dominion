package server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import server.service.UserService;
import server.service.exception.ServiceException;
import server.service.exception.UserAlreadyExistsException;
import server.service.exception.UserNotFoundException;
import shared.domain.engine.GameState;
import shared.dto.UserDTO;
import server.persistence.Persistence;
import server.persistence.impl.PersistenceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import server.persistence.exception.PersistenceException;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final Persistence persistence;

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    /**
     * default constructor
     * @throws PersistenceException
     */

    @Autowired
    public UserServiceImpl() throws ServiceException {
        LOG.debug("Calling UserService constructor");
        try {
            persistence = PersistenceImpl.getInstance();
        } catch (PersistenceException e) {
           throw new ServiceException(e.getMessage());
        }
    }


    public UserServiceImpl(Persistence userPersistence){
        LOG.debug("Calling UserService constructor");
        this.persistence = Objects.requireNonNull(userPersistence);
    }

    /**
     * adds a user for saving
     *
     * @param userName
     */
    @Override
    public UserDTO addUser(String userName, String password) throws ServiceException {
        LOG.debug("Calling addUser");

        try {
            try {
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                return persistence.addUser(new UserDTO(userName, passwordEncoder.encode(password)));
            } catch (server.persistence.exception.UserAlreadyExistsException e) {
                throw new UserAlreadyExistsException(e.getMessage());
            }
        } catch (PersistenceException e1) {
            throw new ServiceException(e1.getMessage());
        }
    }

    /**
     * get UserDTO by ID
     *
     * @param id
     * @return corresponding UserDTO
     * @throws ServiceException
     */
    @Override
    public UserDTO getUserByID(Integer id) throws ServiceException {
        LOG.debug("Calling getUserByID");
        try {
            return persistence.getUserById(id);
        } catch (server.persistence.exception.UserNotFoundException e){
            throw new UserNotFoundException(e.getMessage());
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * get UserDTO by username
     *
     * @param username
     * @return corresponding UserDTO
     * @throws ServiceException
     */
    @Override
    public UserDTO getUserByUsername(String username) throws ServiceException {
        LOG.debug("Calling getUserByUsername");
        try {
            return persistence.getUserByUserName(username);
        } catch (server.persistence.exception.UserNotFoundException e){
            throw new UserNotFoundException(e.getMessage());
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * get all users saved in DB
     *
     * @return a set of Users
     * @throws ServiceException
     */
    @Override
    public Set<UserDTO> getAllUsers() throws ServiceException {
        LOG.debug("Calling getAllUsers");
        try {
            return persistence.getAllUsers();
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<Integer> getAllGamesForUsers(List<UserDTO> users) throws ServiceException {
        LOG.debug("getAllGamesForUsers");
        if (users == null) throw new ServiceException("no users given");
        try {
            return persistence.getAllGamesForUsers(users);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public int addGame(GameState gameState) throws ServiceException {
        LOG.debug("addGame");
        if (gameState == null) throw new ServiceException("no gameState to save");
        try {
            return persistence.addGame(gameState);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public GameState getGameStateByID(int id) throws ServiceException {
        LOG.debug("getGameStateByID");
        if (id < 0) throw new ServiceException("invalid id");
        try {
            return persistence.getGameStateByID(id);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        LOG.debug("Calling getUserByUsername");
        try {
            return persistence.getUserByUserName(s);
        } catch (server.persistence.exception.UserNotFoundException e){
            throw new UsernameNotFoundException(e.getMessage());
        } catch (PersistenceException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
