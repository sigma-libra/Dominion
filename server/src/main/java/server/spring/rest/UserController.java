package server.spring.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.service.exception.UserAlreadyExistsException;
import server.service.exception.UserNotFoundException;
import server.service.*;
import server.service.exception.InvalidTokenException;
import server.service.exception.ServiceException;
import shared.dto.AuthRequestDTO;
import shared.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;
import java.util.*;

/**
 * REST controller for userpersistence
 */

@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * user service
     */
    private final UserService userService;

    /**
     * authentication service
     */
    private final AuthService authService;

    /**
     * Constructor
     * @param userService
     * @param authService
     */
    @Autowired
    public UserController(UserService userService, AuthService authService) {
        this.userService = Objects.requireNonNull(userService);
        this.authService = Objects.requireNonNull(authService);
    }


    /**
     * handles exception
     * @param ex UserNotFoundException
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public void handleUserNotFoundException(UserNotFoundException ex)
    {
        LOG.warn("Error handling client request: " + ex.getMessage());
    }

    /**
     * handles exception
     * @param ex UserAlreadyExistsException
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public void handleUserAlreadyExistsException(UserAlreadyExistsException ex)
    {
        LOG.warn("Error handling client request: " + ex.getMessage());
    }

    /**
     * handles exception
     * @param ex ServiceException
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleServiceException(ServiceException ex)
    {
        LOG.warn("Error handling client request: " + ex.getMessage());
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
     * user/all
     * @return all users
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public @ResponseBody Set<UserDTO> getAllUsers() throws ServiceException {
        LOG.debug("Calling getAllUsers");
        return userService.getAllUsers();
    }

    /**
     * user/byid
     * @param id user's id
     * @return corresponding user
     */
    @RequestMapping(value = "/byid", method = RequestMethod.GET)
    public @ResponseBody UserDTO getUserByID(@RequestParam("id") int id) throws ServiceException {
        LOG.debug("Calling getUserByID");
        return userService.getUserByID(id);
    }


    /**
     * user/byusername
     * @param username user's username
     * @return corresponding user
     */
    @RequestMapping(value = "/byusername", method = RequestMethod.GET)
    public @ResponseBody UserDTO getUserByUsername(@RequestParam("username") String username) throws ServiceException {
        LOG.debug("Calling getUserByUsername");
        return userService.getUserByUsername(username);
    }

    /**
     * user/add
     * @param authRequestDTO
     * @return the new user or null
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<UserDTO> addUser(@RequestBody AuthRequestDTO authRequestDTO) throws ServiceException {
        LOG.debug("Calling addUser");
        UserDTO user = userService.addUser(authRequestDTO.getUsername(), authRequestDTO.getPassword());
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
