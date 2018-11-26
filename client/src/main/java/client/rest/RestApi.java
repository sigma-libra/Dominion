package client.rest;


import client.domain.ObservableGame;
import client.domain.impl.ObservableGameImpl;
import client.rest.exception.RestException;
import client.service.exception.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import org.springframework.stereotype.Repository;
import org.springframework.web.client.*;
import shared.dto.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * RestAPI: Connection point to Server
 */
@Repository
public class RestApi {


    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * template for a rest call to send to the server
     */
    private RestTemplate restTemplate;

    /**
     * default server url
     */
    private String url = "http://localhost:8080";

    /**
     * initiates a new RestTemplate
     */
    @Autowired
    public RestApi() {
        this(new RestTemplate());
    }

    /**
     * constructor
     * @param restTemplate rest template to set
     */
    public RestApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     *
     * @return url field
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url url to be set
     */
    public void setUrl(String url) {
        this.url = url;
    }


    /**
     * Utility function used to make a HTTP request.
     *
     * @param content The data to send the server
     * @param typeRef A ParameterizedTypeReference<T> indicating what kind of object will be returned by the server
     * @param requestType The HTTP request type to perform
     * @param url The URL where to send the request
     * @param urlParameters Values to be inserted into the url. The url must have placeholders in curly braces, e.g. "http://foo.bar/?arg={placeholder}"
     * @return the object returned by the server
     * @throws RestException If the server responds with a non-2xx HTTP status code
     */
    private <T> T exchange(Object content, ParameterizedTypeReference<T> typeRef, HttpMethod requestType, String url, Object ... urlParameters) throws RestException, ServiceException {
        HttpEntity<Object> requestEntity = new HttpEntity<>(content);
        return exchange(requestEntity, typeRef, requestType, url, urlParameters);
    }

    /**
     * Utility function used to make a HTTP request carrying an authentication token.
     *
     * @param authToken Your authentication token
     * @param typeRef A ParameterizedTypeReference<T> indicating what kind of object will be returned by the server
     * @param requestType The HTTP request type to perform
     * @param url The URL where to send the request
     * @param urlParameters Values to be inserted into the url. The url must have placeholders in curly braces, e.g. "http://foo.bar/?arg={placeholder}"
     * @return the object returned by the server
     * @throws RestException If the server responds with a non-2xx HTTP status code
     */
    private <T> T authenticated_exchange(String authToken, ParameterizedTypeReference<T> typeRef, HttpMethod requestType, String url, Object ... urlParameters) throws RestException, ServiceException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Token", authToken);
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
        return exchange(requestEntity, typeRef, requestType, url, urlParameters);
    }

    /**
     * Utility function used to make a HTTP request.
     * You should almost never have to call this function directly.
     *
     * @param requestEntity A HttpEntity encapsulating the HTTP headers and the data that will be sent to the server
     * @param typeRef A ParameterizedTypeReference<T> indicating what kind of object will be returned by the server
     * @param requestType The HTTP request type to perform
     * @param url The URL where to send the request
     * @param urlParameters Values to be inserted into the url. The url must have placeholders in curly braces, e.g. "http://foo.bar/?arg={placeholder}"
     * @return the object returned by the server
     * @throws RestException If the server responds with a non-2xx HTTP status code
     */
    private <T> T exchange(HttpEntity requestEntity, ParameterizedTypeReference<T> typeRef, HttpMethod requestType, String url, Object ... urlParameters) throws RestException, ServiceException {
        try {
            ResponseEntity<T> response = restTemplate.exchange(this.url + url, requestType, requestEntity, typeRef, urlParameters);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = e.getResponseBodyAsString();
            try {
                RestException ex = objectMapper.readValue(responseBody, RestException.class);

                if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
                    throw new ServiceException(ex.getMessage());

                if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                    throw new AuthenticationException(ex.getMessage());

                throw ex;
            } catch (IOException ioex) {
                throw new RestException(e.getStatusCode(), responseBody);
            }
        } catch (ResourceAccessException e) {
            throw new ServerException(url + " not found");
        }
    }

    /**
     * Utility function used to make a HTTP GET request.
     *
     * @param returnType A class object indicating what kind of object will be returned by the server
     * @param url The URL where to send the request
     * @param urlParameters Values to be inserted into the url. The url must have placeholders in curly braces, e.g. "http://foo.bar/?arg={placeholder}"
     * @return the object returned by the server
     * @throws RestException If the server responds with a non-2xx HTTP status code
     */
    private <T> T get(Class<T> returnType, String url, Object ... urlParameters) throws RestException, ServiceException {
        ParameterizedTypeReference<T> typeRef = ParameterizedTypeReference.forType(returnType);
        return get(typeRef, url, urlParameters);
    }

    /**
     * Utility function used to make a HTTP GET request.
     *
     * @param typeRef A ParameterizedTypeReference<T> indicating what kind of object will be returned by the server
     * @param url The URL where to send the request
     * @param urlParameters Values to be inserted into the url. The url must have placeholders in curly braces, e.g. "http://foo.bar/?arg={placeholder}"
     * @return the object returned by the server
     * @throws RestException If the server responds with a non-2xx HTTP status code
     */
    private <T> T get(ParameterizedTypeReference<T> typeRef, String url, Object ... urlParameters) throws RestException, ServiceException {
        return exchange(null, typeRef, HttpMethod.GET, url, urlParameters);
    }

    /**
     * Utility function used to make a HTTP POST request.
     *
     * @param content The data to send the server
     * @param returnType A class object indicating what kind of object will be returned by the server
     * @param url The URL where to send the request
     * @param urlParameters Values to be inserted into the url. The url must have placeholders in curly braces, e.g. "http://foo.bar/?arg={placeholder}"
     * @return the object returned by the server
     * @throws RestException If the server responds with a non-2xx HTTP status code
     */
    private <T> T post(Object content, Class<T> returnType, String url, Object ... urlParameters) throws RestException, ServiceException {
        ParameterizedTypeReference<T> typeRef = ParameterizedTypeReference.forType(returnType);
        return post(content, typeRef, url, urlParameters);
    }

    /**
     * Utility function used to make a HTTP POST request.
     *
     * @param content The data to send the server
     * @param typeRef A ParameterizedTypeReference<T> indicating what kind of object will be returned by the server
     * @param url The URL where to send the request
     * @param urlParameters Values to be inserted into the url. The url must have placeholders in curly braces, e.g. "http://foo.bar/?arg={placeholder}"
     * @return the object returned by the server
     * @throws RestException If the server responds with a non-2xx HTTP status code
     */
    private <T> T post(Object content, ParameterizedTypeReference<T> typeRef, String url, Object ... urlParameters) throws RestException, ServiceException {
        return exchange(content, typeRef, HttpMethod.POST, url, urlParameters);
    }

    /**
     * Utility function used to make a HTTP GET request carrying an authentication token.
     *
     * @param authToken Your authentication token
     * @param returnType A class object indicating what kind of object will be returned by the server
     * @param url The URL where to send the request
     * @param urlParameters Values to be inserted into the url. The url must have placeholders in curly braces, e.g. "http://foo.bar/?arg={placeholder}"
     * @return the object returned by the server
     * @throws RestException If the server responds with a non-2xx HTTP status code
     */
    private <T> T authenticated_get(String authToken, Class<T> returnType, String url, Object ... urlParameters) throws RestException, ServiceException {
        ParameterizedTypeReference<T> typeRef = ParameterizedTypeReference.forType(returnType);
        return authenticated_get(authToken, typeRef, url, urlParameters);
    }

    /**
     * Utility function used to make a HTTP GET request carrying an authentication token.
     *
     * @param authToken Your authentication token
     * @param typeRef A ParameterizedTypeReference<T> indicating what kind of object will be returned by the server
     * @param url The URL where to send the request
     * @param urlParameters Values to be inserted into the url. The url must have placeholders in curly braces, e.g. "http://foo.bar/?arg={placeholder}"
     * @return the object returned by the server
     * @throws RestException If the server responds with a non-2xx HTTP status code
     */
    private <T> T authenticated_get(String authToken, ParameterizedTypeReference<T> typeRef, String url, Object ... urlParameters) throws RestException, ServiceException {
        return authenticated_exchange(authToken, typeRef, HttpMethod.GET, url, urlParameters);
    }

    /**
     * Utility function used to make a HTTP POST request carrying an authentication token.
     *
     * @param authToken Your authentication token
     * @param url The URL where to send the request
     * @param urlParameters Values to be inserted into the url. The url must have placeholders in curly braces, e.g. "http://foo.bar/?arg={placeholder}"
     * @throws RestException If the server responds with a non-2xx HTTP status code
     */
    private void authenticated_post(String authToken, String url, Object ... urlParameters) throws RestException, ServiceException {
        authenticated_post(authToken, Void.class, url, urlParameters);
    }

    /**
     * Utility function used to make a HTTP POST request carrying an authentication token.
     *
     * @param authToken Your authentication token
     * @param returnType A class object indicating what kind of object will be returned by the server
     * @param url The URL where to send the request
     * @param urlParameters Values to be inserted into the url. The url must have placeholders in curly braces, e.g. "http://foo.bar/?arg={placeholder}"
     * @return the object returned by the server
     * @throws RestException If the server responds with a non-2xx HTTP status code
     */
    private <T> T authenticated_post(String authToken, Class<T> returnType, String url, Object ... urlParameters) throws RestException, ServiceException {
        ParameterizedTypeReference<T> typeRef = ParameterizedTypeReference.forType(returnType);
        return authenticated_post(authToken, typeRef, url, urlParameters);
    }

    /**
     * Utility function used to make a HTTP POST request carrying an authentication token.
     *
     * @param authToken Your authentication token
     * @param typeRef A ParameterizedTypeReference<T> indicating what kind of object will be returned by the server
     * @param url The URL where to send the request
     * @param urlParameters Values to be inserted into the url. The url must have placeholders in curly braces, e.g. "http://foo.bar/?arg={placeholder}"
     * @return the object returned by the server
     * @throws RestException If the server responds with a non-2xx HTTP status code
     */
    private <T> T authenticated_post(String authToken, ParameterizedTypeReference<T> typeRef, String url, Object ... urlParameters) throws RestException, ServiceException {
        return authenticated_exchange(authToken, typeRef, HttpMethod.POST, url, urlParameters);
    }


    /**
     * authenticate against server and retrieve an authentication token as response
     *
     * @param req Authrequest
     * @return AuthResponseDTO
     * @throws ServerException
     * @throws AuthenticationException
     */
    public AuthResponseDTO authenticate(AuthRequestDTO req) throws ServiceException {
        LOG.debug("Authenticate inside of Client");
        try {
            AuthResponseDTO authResponseDTO = post(req, AuthResponseDTO.class, "/auth");
            assert authResponseDTO != null;
            return authResponseDTO;
        } catch (RestException e) {
            if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                throw new ServerException("Internal error by " + url);
            }
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * Get a user from the database with the given username
     * @param username username
     * @return userDTO
     * @throws ServerException
     * @throws GetUserInfoException
     */
    public UserDTO getUserByUsername(String username) throws ServiceException {
        LOG.debug("Get userDTO by username through rest");
        UserDTO user;
        try {
            user = get(UserDTO.class, "/user/byusername?username={username}", username);
        } catch (RestException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new GetUserInfoException(username, e.getMessage());
            throw new ServerException(e.getMessage());
        }
        assert (user != null) : "userDTO received from server is null";
        return user;
    }

    /**
     * Add a user with given username in database and return the user
     *
     * @param authRequestDTO
     * @return userDTO
     * @throws ServerException
     * @throws UserNotAddedException
     */
    public UserDTO addUser(AuthRequestDTO authRequestDTO) throws ServiceException {
        LOG.debug("Add userDTO in database");

        UserDTO user;
        try {
            user = post(authRequestDTO, UserDTO.class, "/user/add");
        } catch (RestException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT)
                throw new UserAlreadyExistsException(authRequestDTO.getUsername());
            throw new ServerException(e.getMessage());
        }
        assert (user != null) : "userDTO received from server is null";
        return user;
    }

    /**
     * user subscribes to a game
     * @param authToken user's auth token
     * @return ObservableGame
     * @throws ServiceException
     */
    public ObservableGame subscribeToGame(String authToken) throws ServiceException {
        LOG.debug("Calling subscribeToGame");

        ServerSocket serverSocket;
        try{
            serverSocket = new ServerSocket(0);
        } catch (IOException e){
            throw new ServerException("failed to create socket");
        }

        GameStateDTO gameState;
        try {
            gameState = authenticated_post(authToken, GameStateDTO.class, "/game/subscribe?port={port}", serverSocket.getLocalPort());
        } catch (RestException e){
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new ServerException(e.getMessage());
            throw new ServerException(e.getMessage());
        }
        assert (gameState != null) : "GameStateDTO received from server is null";

        Socket socket;
        try{
            socket = serverSocket.accept();
            serverSocket.close();
        } catch (IOException e){
            throw new ServerException("failed to establish socket connection");
        }

        return new ObservableGameImpl(socket, this, authToken, gameState);
    }

    /**
     * Tells server that user is ready for game to start
     *
     * @param authToken user's auth token
     * @throws ServerException

     */
    public void joinGame(String authToken) throws ServiceException {
        LOG.debug("Calling joinGame");

        try {
            authenticated_post(authToken, "/game/join");
        } catch (RestException e){
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                throw new ServerException("Invalid authentication token");
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new ServerException(e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * user leaves the game
     * @param authToken user's auth token
     * @throws ServerException
     */
    public void leaveGame(String authToken) throws ServiceException {
        LOG.debug("Calling leaveGame");

        try {
            authenticated_post(authToken, "/game/leave");
        } catch (RestException e){
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                throw new ServerException("Invalid authentication token");
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new ServerException(e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * Tells server to start the game
     *
     * @param authToken
     * @return
     * @throws ServerException
     * @throws UserNotModifiedException
     */
    public void startGame(String authToken, int [] customActionCards) throws ServiceException {
        LOG.debug("Calling startGame");

        try {
            if(customActionCards != null)
                authenticated_post(authToken, "/game/start?custom=" + customActionCards[0] +
                    "&custom=" + customActionCards[1] +
                    "&custom=" + customActionCards[2] +
                    "&custom=" + customActionCards[3] +
                    "&custom=" + customActionCards[4] +
                    "&custom=" + customActionCards[5] +
                    "&custom=" + customActionCards[6] +
                    "&custom=" + customActionCards[7] +
                    "&custom=" + customActionCards[8] +
                    "&custom=" + customActionCards[9] );
            else
                authenticated_post(authToken, "/game/start?custom=");
        } catch (RestException e){
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new ServerException(e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * Get all the users who joined a game
     * @return
     * @throws ServerException
     * @throws GetUserInfoException
     */
    public Set<UserDTO> getUsersInGame() throws ServiceException {
        LOG.debug("Calling getUsersInGame");
        ParameterizedTypeReference<Set<UserDTO>> typeRef = new ParameterizedTypeReference<>() {};
        try {
            return get(typeRef, "/game/getplayers");
        } catch (RestException e) {
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * Get a game state from the server
     *
     * @return
     * @throws ServerException
     * @throws GetUserInfoException
     */
    public GameStateDTO getGameState(String authToken) throws ServiceException {
        LOG.debug("Get gameDTO by username through rest");
        try {
            return authenticated_get(authToken, GameStateDTO.class, "/game/getgamestate");
        } catch (RestException e){
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new ServerException(e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * user plays a card
     * @param authToken user's auth token
     * @param cardID corresponding card (id)
     * @throws ServerException
     */
    public void playCard(String authToken, int cardID) throws ServiceException {
        try {
            authenticated_post(authToken, "/game/playcard?card={card}", cardID);
        } catch (RestException e){
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new ServerException(e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * user finishes a pending choice effect
     * @param authToken user's auth token
     * @param ids the ids of the selected choices
     * @throws ServerException
     */
    public void finishInteractiveEffect(String authToken, List<Integer> ids) throws ServiceException {
        int[] idArray = ids.stream().mapToInt(i -> i).toArray();
        try {
            authenticated_post(authToken, "/game/finisheffect?choices={ids}", idArray);
        } catch (RestException e){
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ServiceException(e.getMessage());
            }
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * user buys a card from a pile
     * @param authToken user's auth token
     * @param pileID corresponding pile (id)
     * @throws ServerException
     */
    public void buyCard(String authToken, int pileID) throws ServiceException {
        try {
            authenticated_post(authToken, "/game/buycard?pile={pile}", pileID);
        } catch (RestException e){
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new ServerException(e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * user ends the turn
     * @param authToken user's auth token
     * @throws ServerException
     */
    public void endPhase(String authToken) throws ServiceException {
        try {
            authenticated_post(authToken, "/game/endphase");
        } catch (RestException e){
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new ServerException(e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }



    /**
     * Get a statisticsDTO from the server
     *
     * @return
     * @throws ServerException
     * @throws GetUserInfoException
     */
    public StatisticsDTO getStatistics(String authToken) throws ServiceException {
        LOG.debug("Get statisticsDTO by username through rest");
        try {
            return authenticated_get(authToken, StatisticsDTO.class, "/stat/user");
        } catch (RestException e){
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new ServerException(e.getMessage());
            LOG.error("ServerException thrown");
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * sends gameinfo to server
     * @param authToken
     * @throws ServiceException
     */
    public void addGameInfo(String authToken, GameInfoDTO gameInfoDTO) throws ServiceException {
        try {
            authenticated_post(authToken, "/stat/add{gameinfo}", gameInfoDTO);
        } catch (RestException e){
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new ServerException(e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }


    /**
     * Loads a saved game with the given ID
     * @param authToken
     * @param gameID
     */
    public void loadSavedGameState(String authToken, Integer gameID) throws ServerException {
        LOG.info("Calling loadSavedGameState");

        try {
            authenticated_post(authToken, "/game/load?id=" + gameID.intValue());
        } catch (RestException e){
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new ServerException(e.getMessage());
            throw new ServerException(e.getMessage());
        } catch (ServiceException e) {
            throw new ServerException(e.getMessage());
        }

    }

    /**
     * Tells server to save the current game and return its id in database
     * @param authToken
     * @return
     */
    public Integer saveGame(String authToken) throws ServerException {
        LOG.info("Calling saveGame");
        try {
            return authenticated_get(authToken, Integer.class, "/game/save");
        } catch (RestException e){
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new ServerException(e.getMessage());
            throw new ServerException(e.getMessage());
        }catch (ServiceException e) {
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * Gets a list of all the saved game ids with the same players as currently joined
     */
    public List<Integer> loadSavedGameIDs(String authToken) throws ServerException {
        LOG.info("Calling loadSavedGameIDS");
        try {
            return authenticated_get(authToken, List.class, "/game/loadGames");
        } catch (RestException e){
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new ServerException(e.getMessage());
            throw new ServerException(e.getMessage());
        }catch (ServiceException e) {
            throw new ServerException(e.getMessage());
        }

    }
}
