package client.domain.impl;

import client.domain.ObservableGame;
import client.rest.RestApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.dto.GameStateDTO;

import java.io.DataInputStream;
import java.lang.invoke.MethodHandles;
import java.net.Socket;
import java.util.Objects;


public class ObservableGameImpl extends ObservableGame {

    /**
     * logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Socket socket;
    private RestApi restApi;
    private String authToken;

    /**
     * Constructor
     *
     * @param socket
     * @param restApi
     * @param authToken
     * @param gameState
     */
    public ObservableGameImpl(Socket socket, RestApi restApi, String authToken, GameStateDTO gameState){
        updateGamestate(gameState);

        // listen for updates on the socket and call the callback function when necessary
        this.socket = socket;
        this.restApi = Objects.requireNonNull(restApi);
        this.authToken = authToken;

        Thread gameStateThread = new Thread(() -> listenForNotifications());
        gameStateThread.setDaemon(true);
        gameStateThread.start();
    }


    private void listenForNotifications(){
        try {
            DataInputStream socketStream = new DataInputStream(socket.getInputStream());
            while (true){
                socketStream.readByte();
                LOG.info("Socket received game state update notification from server");
                GameStateDTO gameStateDTO = restApi.getGameState(authToken);
                updateGamestate(gameStateDTO);
            }
        } catch (Exception e) {
            handleException(e);
        }
    }
}
