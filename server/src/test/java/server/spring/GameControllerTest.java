package server.spring;


import org.junit.*;
import server.service.AuthService;
import server.service.exception.InsufficientPermissionsException;
import server.service.exception.InvalidTokenException;
import server.service.exception.ServiceException;
import server.service.impl.StatisticsServiceImpl;
import server.service.impl.UserServiceImpl;
import server.spring.rest.GameController;
import server.spring.rest.exception.BadRequestException;
import shared.dto.AuthRequestDTO;
import shared.dto.AuthResponseDTO;
import shared.dto.UserDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



public class GameControllerTest {

    private static UserDTO user1 = new UserDTO(1, "user1", "a");
    private static UserDTO user2 = new UserDTO(2, "user2", "b");

    private final AuthService authService = mock(AuthService.class);
    private final HttpServletRequest servlet = mock(HttpServletRequest.class);

    private GameController gameController = null;

    /**
     * 1 309); //Harbinger
     * 2 310); //Laboratory
     * 3 311); //Library
     * 4 312); //Market
     * 5 313); //Merchant
     * 6 314); //Militia
     * 7 315); //Mine
     * 8 316); //Moat
     * 9 317); //Moneylender
     *10 318); //Poacher
     */
    private int[] customActionCards = new int[]{309,310,311,312,313,314,315,316,317,318};

    /**
     * just 9 action cards in place
     */
    private int[] wrongCustomActionCards = new int[]{309,310,311,312,313,314,315,316,317,318,319};

    @Before
    public void configureMock() throws InvalidTokenException, ServiceException {

        this.gameController = new GameController(authService, new UserServiceImpl(), mock(StatisticsServiceImpl.class));

        // initialize the mocked AuthServiceImpl with some users
        UserDTO[] users = new UserDTO[] {user1, user2};
        for (int i=0; i<users.length; i++) {
            UserDTO user = users[i];
            AuthRequestDTO authRequestDTO = new AuthRequestDTO(user.getUserName(), user.getPassword());
            String token = Integer.toString(i+1);
            AuthResponseDTO authResponseDTO = new AuthResponseDTO(token);

            when(authService.authenticate(authRequestDTO)).thenReturn(authResponseDTO);
            when(authService.getUserFromToken(token)).thenReturn(user);
        }
    }

    private static ServerSocket serverSocket = null;
    private static List<Socket> subscriberSockets = new ArrayList<>();

    @BeforeClass
    public static void beforeClass() throws IOException {
        serverSocket = new ServerSocket(0);
    }

    @AfterClass
    public static void afterClass() throws IOException {
        serverSocket.close();
    }

    private Socket subscribeToGame(String token) throws IOException, InvalidTokenException, ServiceException, BadRequestException {
        when(servlet.getRemoteAddr()).thenReturn("localhost");
        gameController.subscribeToGameUpdates(token, serverSocket.getLocalPort(), servlet);

        Socket socket = serverSocket.accept();
        subscriberSockets.add(socket);
        return socket;
    }

    @Test
    public void gameControllerComponentTest() throws Exception {
        Set<UserDTO> players = new HashSet<>();

        // check the initial game state
        Assert.assertThat("Game should start with 0 players", gameController.getPlayers(), is(new HashSet<>()));

        // add a player to the game
        gameController.joinGame("1");
        players.add(user1);
        Assert.assertThat("GameController should list the player who just joined the game", gameController.getPlayers(), is(players));

        // try to add the same player to the game again
        try {
            gameController.joinGame("1");
            Assert.fail("GameController didn't throw exception when the same user joined the game twice");
        } catch (BadRequestException e){}

        // try to play a card before the game has started
        try {
            gameController.playCard("1", 0);
            Assert.fail("gameController didn't throw exception when player played card before the game started");
        } catch (BadRequestException e){}

        // try to end phase before the game has started
        try {
            gameController.endPhase("1");
            Assert.fail("gameController didn't throw exception when player ended the turn phase before the game started");
        } catch (BadRequestException e){}

        // add another player to the game
        gameController.joinGame("2");
        players.add(user2);
        Assert.assertThat("GameController should list all players who joined the game", gameController.getPlayers(), is(players));

        // try to start the game as a non-gamemaster
        try {
            gameController.startGame("2",new int[10]);
            Assert.fail("gameController didn't throw exception when non-gamemaster tried to start the game");
        } catch (InsufficientPermissionsException e){}

        // start the game for real
        try {
            gameController.startGame("1",new int[10]);
        } catch (InsufficientPermissionsException e){
            Assert.fail("gameController refused to let the game master start the game");
            return;
        }

        // try to start the game again
        try {
            gameController.startGame("1",new int [10]);
            Assert.fail("gameController didn't throw exception when game master started game a 2nd time");
        } catch (BadRequestException e){}
    }


    @Test
    public void gameControllerComponentTest_with_customActionCards() throws Exception {
        Set<UserDTO> players = new HashSet<>();

        // check the initial game state
        Assert.assertThat("Game should start with 0 players", gameController.getPlayers(), is(new HashSet<>()));

        // add a player to the game
        gameController.joinGame("1");
        players.add(user1);
        Assert.assertThat("GameController should list the player who just joined the game", gameController.getPlayers(), is(players));

        // try to add the same player to the game again
        try {
            gameController.joinGame("1");
            Assert.fail("GameController didn't throw exception when the same user joined the game twice");
        } catch (BadRequestException e){}


        try {
            gameController.playCard("1", 0);
            Assert.fail("gameController didn't throw exception when player played card before the game started");
        } catch (BadRequestException e){}

        // try to end phase before the game has started
        try {
            gameController.endPhase("1");
            Assert.fail("gameController didn't throw exception when player ended the turn phase before the game started");
        } catch (BadRequestException e){}

        // add another player to the game
        gameController.joinGame("2");
        players.add(user2);
        Assert.assertThat("GameController should list all players who joined the game", gameController.getPlayers(), is(players));

        // try to start the game as a non-gamemaster
        try {
            gameController.startGame("2",customActionCards);
            Assert.fail("gameController didn't throw exception when non-gamemaster tried to start the game");
        } catch (InsufficientPermissionsException e){}

        // start the game for real
        try {
            gameController.startGame("1",customActionCards);
        } catch (InsufficientPermissionsException e){
            Assert.fail("gameController refused to let the game master start the game");
            return;
        }


        // try to start the game again
        try {
            gameController.startGame("1",customActionCards);
            Assert.fail("gameController didn't throw exception when game master started game a 2nd time");
        } catch (BadRequestException e){}
    }

    @Test
    public void gameControllerComponentTest_with_wonrgCustomActionCards() throws Exception {
        //Tests should go through since a wong number of custom actioncards do not affect the default (randomized) game setup
        Set<UserDTO> players = new HashSet<>();

        // check the initial game state
        Assert.assertThat("Game should start with 0 players", gameController.getPlayers(), is(new HashSet<>()));

        // add a player to the game
        gameController.joinGame("1");
        players.add(user1);
        Assert.assertThat("GameController should list the player who just joined the game", gameController.getPlayers(), is(players));

        // try to add the same player to the game again
        try {
            gameController.joinGame("1");
            Assert.fail("GameController didn't throw exception when the same user joined the game twice");
        } catch (BadRequestException e){}


        try {
            gameController.playCard("1", 0);
            Assert.fail("gameController didn't throw exception when player played card before the game started");
        } catch (BadRequestException e){}

        // try to end phase before the game has started
        try {
            gameController.endPhase("1");
            Assert.fail("gameController didn't throw exception when player ended the turn phase before the game started");
        } catch (BadRequestException e){}

        // add another player to the game
        gameController.joinGame("2");
        players.add(user2);
        Assert.assertThat("GameController should list all players who joined the game", gameController.getPlayers(), is(players));

        // try to start the game as a non-gamemaster
        try {
            gameController.startGame("2",wrongCustomActionCards);
            Assert.fail("gameController didn't throw exception when non-gamemaster tried to start the game");
        } catch (InsufficientPermissionsException e){}

        // start the game for real
        try {
            gameController.startGame("1",wrongCustomActionCards);
        } catch (InsufficientPermissionsException e){
            Assert.fail("gameController refused to let the game master start the game");
            return;
        }

        // try to start the game again
        try {
            gameController.startGame("1",wrongCustomActionCards);
            Assert.fail("gameController didn't throw exception when game master started game a 2nd time");
        } catch (BadRequestException e){}
    }


}
