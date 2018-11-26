package server.persistence;

import org.junit.*;
import server.persistence.exception.PersistenceException;
import server.persistence.exception.UserAlreadyExistsException;
import server.persistence.exception.UserNotFoundException;
import server.persistence.impl.PersistenceImpl;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.engine.TurnPhase;
import shared.domain.engine.TurnTracker;
import shared.domain.exceptions.GameException;
import shared.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;

public class PersistenceImplTest {

    private static String TEST_DB_PATH = "jdbc:h2:file:~/Dominion-TEST-Database";

    private static String TEST_DB_USER = "test";
    private static String TEST_DB_PASSWORD = "test";

    private static PersistenceImpl persistence;

    private static UserDTO user1 = new UserDTO(1, "user1");
    private static UserDTO user2 = new UserDTO(2, "user2");

    @BeforeClass
    public static void beforeClass() throws PersistenceException {
        persistence = PersistenceImpl.getInstance(TEST_DB_PATH, TEST_DB_USER, TEST_DB_PASSWORD);
    }

    @Before
    public void before() throws PersistenceException {
        persistence.resetDatabase();
    }

    @AfterClass
    public static void afterClass() throws PersistenceException {
        persistence.closeConnection();
    }

    @Test
    public void addUser_ShouldWork() throws PersistenceException {
        persistence.addUser(user1);
        persistence.addUser(user2);
    }

    @Test
    public void addUserWithDuplicateName_ShouldFail() throws PersistenceException {
        persistence.addUser(user1);

        UserDTO user = new UserDTO(user1.getId()+1, user1.getUserName());
        try {
            persistence.addUser(user);
            Assert.fail("Inserting user with duplicate username didn't throw exception");
        } catch (UserAlreadyExistsException e){}
    }

    @Test
    public void getAllUsers_ShouldReturnUsers() throws PersistenceException {
        persistence.addUser(user1);
        persistence.addUser(user2);

        Set<UserDTO> users = persistence.getAllUsers();
        Assert.assertThat(users.size(), is(2));
        Assert.assertThat(users, containsInAnyOrder(user1, user2));
    }

    @Test
    public void getUserById_ShouldReturnUser() throws PersistenceException {
        persistence.addUser(user1);

        UserDTO user = persistence.getUserById(user1.getId());
        Assert.assertEquals(user1, user);
    }

    @Test(expected = UserNotFoundException.class)
    public void getNonexistentUserById_ShouldThrow() throws PersistenceException {
        UserDTO user = persistence.getUserById(user1.getId());
    }

    @Test
    public void getUserByUsername_ShouldReturnUser() throws PersistenceException {
        persistence.addUser(user1);

        UserDTO user = persistence.getUserByUserName(user1.getUserName());
        Assert.assertEquals(user1, user);
    }

    @Test(expected = UserNotFoundException.class)
    public void getNonexistentUserByUsername_ShouldThrow() throws PersistenceException {
        UserDTO user = persistence.getUserByUserName(user1.getUserName());
    }

    @Test
    public void addGameStateTest() throws PersistenceException {
        List<Player> players = new ArrayList<>();
        Player player1 = new Player();
        Player player2 = new Player();
        players.add(player1);
        players.add(player2);
        GameState gameState = new GameState(players);
        Assert.assertEquals(persistence.addGame(gameState),1);
    }

    @Test
    public void getAllGameStateTest() throws PersistenceException {
        Assert.assertThat(persistence.getAllGames().size(), is(0));
        List<Player> players = new ArrayList<>();
        Player player1 = new Player();
        Player player2 = new Player();
        players.add(player1);
        players.add(player2);
        GameState gameState = new GameState(players);
        persistence.addGame(gameState);
        Assert.assertThat(persistence.getAllGames().size(), is(1));
        GameState loaded = persistence.getAllGames().get(0);
        // check if gameStates are the same
        Assert.assertThat(loaded.getPlayers().size(), is(players.size()));
        Assert.assertEquals(loaded.getTurnTracker(), gameState.getTurnTracker());
        Assert.assertEquals(loaded.getSupply(), gameState.getSupply());
        Assert.assertEquals(loaded.getPlayers().get(0), gameState.getPlayers().get(0));
        Assert.assertEquals(loaded.getCurrentPlayerIndex(), gameState.getCurrentPlayerIndex());
        for (int i = 0; i < loaded.getActionCards().length; i++) {
            Assert.assertEquals(loaded.getActionCards()[i], gameState.getActionCards()[i]);
        }
    }

    @Test
    public void getAllGameStateTestMultiple() throws PersistenceException {
        List<Player> players1 = new ArrayList<>();
        Player player1 = new Player();
        Player player2 = new Player();
        players1.add(player1);
        players1.add(player2);
        List<Player> players2 = new ArrayList<>();
        Player player3 = new Player();
        Player player4 = new Player();
        Player player5 = new Player();
        players2.add(player3);
        players2.add(player4);
        players2.add(player5);
        GameState gameState1 = new GameState(players1);
        GameState gameState2 = new GameState(players2);
        persistence.addGame(gameState1);
        Assert.assertEquals(persistence.getAllGames().size(), 1);
        persistence.addGame(gameState1);
        Assert.assertEquals(persistence.getAllGames().size(), 2);
        persistence.addGame(gameState2);
        Assert.assertEquals(persistence.getAllGames().size(), 3);
        List<GameState> loaded = persistence.getAllGames();
        // check if gameStates are the same
        Assert.assertThat(loaded.get(0).getPlayers().size(), is(players1.size()));
        Assert.assertEquals(loaded.get(0).getTurnTracker(), gameState1.getTurnTracker());
        Assert.assertEquals(loaded.get(0).getSupply(), gameState1.getSupply());
        Assert.assertEquals(loaded.get(0).getPlayers().get(0), gameState1.getPlayers().get(0));
        Assert.assertEquals(loaded.get(0).getPlayers().get(1), gameState1.getPlayers().get(1));
        Assert.assertEquals(loaded.get(0).getCurrentPlayerIndex(), gameState1.getCurrentPlayerIndex());
        for (int i = 0; i < loaded.get(0).getActionCards().length; i++) {
            Assert.assertEquals(loaded.get(0).getActionCards()[i], gameState1.getActionCards()[i]);
        }
        Assert.assertThat(loaded.get(1).getPlayers().size(), is(players1.size()));
        Assert.assertEquals(loaded.get(1).getTurnTracker(), gameState1.getTurnTracker());
        Assert.assertEquals(loaded.get(1).getSupply(), gameState1.getSupply());
        Assert.assertEquals(loaded.get(1).getPlayers().get(0), gameState1.getPlayers().get(0));
        Assert.assertEquals(loaded.get(1).getPlayers().get(1), gameState1.getPlayers().get(1));
        Assert.assertEquals(loaded.get(1).getCurrentPlayerIndex(), gameState1.getCurrentPlayerIndex());
        for (int i = 0; i < loaded.get(1).getActionCards().length; i++) {
            Assert.assertEquals(loaded.get(1).getActionCards()[i], gameState1.getActionCards()[i]);
        }
        Assert.assertThat(loaded.get(2).getPlayers().size(), is(players2.size()));
        Assert.assertEquals(loaded.get(2).getTurnTracker(), gameState2.getTurnTracker());
        Assert.assertEquals(loaded.get(2).getSupply(), gameState2.getSupply());
        Assert.assertEquals(loaded.get(2).getPlayers().get(0), gameState2.getPlayers().get(0));
        Assert.assertEquals(loaded.get(2).getPlayers().get(1), gameState2.getPlayers().get(1));
        Assert.assertEquals(loaded.get(2).getPlayers().get(2), gameState2.getPlayers().get(2));
        Assert.assertEquals(loaded.get(2).getCurrentPlayerIndex(), gameState2.getCurrentPlayerIndex());
        for (int i = 0; i < loaded.get(2).getActionCards().length; i++) {
            Assert.assertEquals(loaded.get(2).getActionCards()[i], gameState2.getActionCards()[i]);
        }
    }

    @Test
    public void getAllGameStateTestChanges() throws PersistenceException {
        List<Player> players = new ArrayList<>();
        Player player1 = new Player();
        Player player2 = new Player();
        players.add(player1);
        players.add(player2);
        GameState gameState = new GameState(players);
        gameState.getTurnTracker().nextPhase(gameState.getCurrentPlayer());
        persistence.addGame(gameState);
        Assert.assertThat(persistence.getAllGames().size(), is(1));
        GameState loaded = persistence.getAllGames().get(0);
        // check if gameStates are the same
        Assert.assertThat(loaded.getPlayers().size(), is(players.size()));
        Assert.assertEquals(loaded.getTurnTracker(), gameState.getTurnTracker());
        Assert.assertEquals(loaded.getTurnTracker().getPhase(), TurnPhase.BUY_PHASE);
        Assert.assertEquals(loaded.getSupply(), gameState.getSupply());
        //Assert.assertEquals(loaded.getPlayers().get(0), gameState.getPlayers().get(0));
        Player player1Loaded = loaded.getPlayers().get(0);
        Assert.assertEquals(loaded.getCurrentPlayerIndex(), gameState.getCurrentPlayerIndex());
        for (int i = 0; i < loaded.getActionCards().length; i++) {
            Assert.assertEquals(loaded.getActionCards()[i], gameState.getActionCards()[i]);
        }
    }

    @Test
    public void saveLoadTest() throws PersistenceException, GameException {
        List<Player> players = new ArrayList<>();
        Player player1 = new Player(new UserDTO("u1"));
        Player player2 = new Player(new UserDTO("u2"));
        players.add(player1);
        players.add(player2);
        GameState gameState = new GameState(players);

        TurnTracker turnTracker = gameState.getTurnTracker();

        gameState.finishPhase(gameState.getCurrentPlayer());
        turnTracker.addCredit(9);
        gameState.buyCard(gameState.getCurrentPlayer(), 2);
        gameState.finishPhase(gameState.getCurrentPlayer());

        gameState.finishPhase(gameState.getCurrentPlayer());

        persistence.addGame(gameState);
        GameState loaded = persistence.getGameStateByID(1);

        Assert.assertTrue(loaded.getTurnTracker().getPhase().equals(TurnPhase.BUY_PHASE));
        Assert.assertTrue(gameState.getCurrentPlayerIndex() == loaded.getCurrentPlayerIndex());
        Assert.assertTrue(gameState.getPlayers().size() == loaded.getPlayers().size());
        Assert.assertTrue(gameState.getPlayers().get(0).getUser().getId() == loaded.getPlayers().get(0).getUser().getId());
        Assert.assertTrue(gameState.getPlayers().get(1).getUser().getId() == loaded.getPlayers().get(1).getUser().getId());
        Assert.assertTrue(gameState.getCurrentPlayer().getUser().getId() == loaded.getCurrentPlayer().getUser().getId());
        for (int i = 0; i < gameState.getPlayers().get(0).getHandSize(); i++) {
            Assert.assertTrue(gameState.getPlayers().get(0).getHand().get(i).getClass().equals(loaded.getPlayers().get(0).getHand().get(i).getClass()));
        }
        for (int i = 0; i < gameState.getPlayers().get(1).getHandSize(); i++) {
            Assert.assertTrue(gameState.getPlayers().get(1).getHand().get(i).getClass().equals(loaded.getPlayers().get(1).getHand().get(i).getClass()));
        }
    }
}
