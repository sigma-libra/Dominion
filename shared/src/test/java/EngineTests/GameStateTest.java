package EngineTests;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.cards.kingdoms.*;

import shared.domain.cards.treasures.CopperCard;
import shared.domain.cards.treasures.GoldCard;
import shared.domain.cards.treasures.SilverCard;
import shared.domain.cards.victories.DuchyCard;
import shared.domain.cards.victories.EstateCard;
import shared.domain.cards.victories.ProvinceCard;
import shared.domain.effect.impl.DiscardThenDrawEffect;
import shared.domain.engine.*;
import shared.domain.exceptions.GameException;
import shared.domain.exceptions.InvalidCardTypeID;
import shared.domain.exceptions.NotYourTurnException;
import shared.dto.UserDTO;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GameStateTest {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private GameState gameState;
    private Player player0;
    private Player player1;
    private TurnTracker turnTracker;

    /**
     * prepare customgame test
     */
    private GameState customGameState;

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
    private Card harbingerCard = new Harbinger();
    private Card laboratoryCard = new Laboratory();
    private Card libraryCard = new Library();
    private Card marketCard = new Market();
    private Card merchantCard = new Merchant();
    private Card militiaCard = new Militia();
    private Card mineCard = new Mine();
    private Card moatCard = new Moat();
    private Card moneylenderCard = new Moneylender();
    private Card poacherCard = new Poacher();

    private ArrayList<Card> listOfUsedCustomCards = new ArrayList<>();







    @Before
    public void setUp() {
        LOG.info("setUp");
        player0 = new Player(new UserDTO("Player0"));
        player1 = new Player(new UserDTO("Player1"));
        List<Player> playerList = new ArrayList<>();
        playerList.add(player0);
        playerList.add(player1);
        gameState = new GameState(playerList, new ArrayList<>());
        turnTracker = gameState.getTurnTracker();
        gameState.getPlayLog().clear();
        customGameState = new GameState(playerList,new ArrayList<>(),customActionCards);
        customGameState.getPlayLog().clear();

        listOfUsedCustomCards.add(harbingerCard);
        listOfUsedCustomCards.add(laboratoryCard);
        listOfUsedCustomCards.add(libraryCard);
        listOfUsedCustomCards.add(marketCard);
        listOfUsedCustomCards.add(merchantCard);
        listOfUsedCustomCards.add(militiaCard);
        listOfUsedCustomCards.add(mineCard);
        listOfUsedCustomCards.add(moneylenderCard);
        listOfUsedCustomCards.add(moatCard);
        listOfUsedCustomCards.add(poacherCard);


    }

    private static List<Card> makeCardList(Class<? extends Card>... cards){
        LOG.info("makeCardList");
        return Arrays.stream(cards).map(t -> Card.fromClass(t)).collect(Collectors.toList());
    }

    private static Player makePlayer(Class<? extends Card>... handCards){
        LOG.info("makePlayer");
        Player player = new Player(new UserDTO("TEST_PLAYER"));
        player.setDeck(new ArrayList<>());

        player.setHand(makeCardList(handCards));
        return player;
    }

    private static GameState makeGameState(Player... players){
        LOG.info("makeGameState");
        List<Player> playerList = new ArrayList<>();
        for (int i=0; i<players.length; i++)
            playerList.add(players[i]);

        GameState gameState = new GameState(playerList);
        return gameState;
    }

    private static void comparePile(CardPile pile, Class<? extends Card>... cards){
        LOG.info("comparePile");
        assertThat(pile.getCards(), is(makeCardList(cards)));
    }

    @Test
    public void testPlayerSwitch() throws GameException {
        LOG.info("testPlayerSwitch");
        assertThat(gameState.getCurrentPlayer().getUser().getUserName(), is("Player0"));
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        gameState.finishPhase(gameState.getCurrentPlayer());
        assertThat(gameState.getCurrentPlayer().getUser().getUserName(), is("Player0"));
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        gameState.finishPhase(gameState.getCurrentPlayer());
        assertThat(gameState.getCurrentPlayer().getUser().getUserName(), is("Player1"));
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        gameState.finishPhase(gameState.getCurrentPlayer());
        assertThat(gameState.getCurrentPlayer().getUser().getUserName(), is("Player1"));
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        gameState.finishPhase(gameState.getCurrentPlayer());
    }

    @Test(expected = GameException.class)
    public void testBuyCard0() throws GameException {
        LOG.info("testBuyCard0");
        gameState.buyCard(gameState.getCurrentPlayer(), 5);
    }

    @Test(expected = GameException.class)
    public void testBuyCard1() throws GameException {
        LOG.info("testBuyCard1");
        turnTracker.nextPhase(gameState.getCurrentPlayer());
        gameState.buyCard(gameState.getCurrentPlayer(), 5);
    }

    @Test(expected = GameException.class)
    public void testBuyCard2() throws GameException {
        LOG.info("testBuyCard2");
        gameState.finishPhase(gameState.getCurrentPlayer());
        turnTracker.addCredit(2);
        gameState.buyCard(gameState.getCurrentPlayer(), 5);
    }

    @Test
    public void testBuyCard3() throws GameException {
        LOG.info("testBuyCard3");
        gameState.finishPhase(gameState.getCurrentPlayer());
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.addCredit(3);
        assertThat(player0.getDiscard().size(), is(0));
        gameState.buyCard(gameState.getCurrentPlayer(), 5);
        assertThat(player0.getDiscard().size(), is(1));
        assertThat(player0.getHand().size(), is(5));
        assertThat(player0.getDeck().size(), is(5));
        assertThat(player1.getDiscard().size(), is(0));
        assertThat(player1.getHand().size(), is(5));
        assertThat(player1.getDeck().size(), is(5));
        assertThat(gameState.getPlayLog().get(gameState.getPlayLog().size() - 1), is("Player0 bought a Silver card"));
    }

    @Test
    public void testBuyCard4() throws GameException {
        LOG.info("testBuyCard3");
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        assertThat(gameState.getCurrentPlayer().getUser().getUserName(), is("Player0"));
        gameState.finishPhase(gameState.getCurrentPlayer());
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.addCredit(3);
        gameState.buyCard(gameState.getCurrentPlayer(), 5);
        assertThat(gameState.getPlayLog().get(gameState.getPlayLog().size() - 1), is("Player0 bought a Silver card"));
        assertThat(player0.getDiscard().size(), is(1));
        assertThat(player0.getHand().size(), is(5));
        assertThat(player0.getDeck().size(), is(5));
        assertThat(player1.getDiscard().size(), is(0));
        assertThat(player1.getHand().size(), is(5));
        assertThat(player1.getDeck().size(), is(5));
        gameState.finishPhase(gameState.getCurrentPlayer());
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        assertThat(gameState.getCurrentPlayer().getUser().getUserName(), is("Player1"));
        assertThat(player0.getDiscard().size(), is(6));
        gameState.finishPhase(gameState.getCurrentPlayer());
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.addCredit(9);
        gameState.buyCard(gameState.getCurrentPlayer(), 4);
        assertThat(player1.getDiscard().size(), is(1));
        assertThat(player1.getHand().size(), is(5));
        assertThat(player1.getDeck().size(), is(5));
        turnTracker.addBuysAvailable(1);
        gameState.buyCard(gameState.getCurrentPlayer(), 5);
        assertThat(player1.getDiscard().size(), is(2));
        assertThat(player1.getHand().size(), is(5));
        assertThat(player1.getDeck().size(), is(5));
        gameState.finishPhase(gameState.getCurrentPlayer());
        assertThat(player1.getDiscard().size(), is(7));
    }

    @Test(expected = GameException.class)
    public void testPlayCard0() throws GameException {
        LOG.info("testPlayCard0");
        gameState.playCard(gameState.getCurrentPlayer(), 0);
    }

    @Test
    public void testPlayCard1() throws GameException {
        LOG.info("testPlayCard1");
        Player player = makePlayer(CopperCard.class, CopperCard.class, CopperCard.class, CopperCard.class, Laboratory.class);
        GameState gameState = makeGameState(player);

        assertThat(gameState.getTurnTracker().getActionsAvailable(), is(1));
        gameState.playCard(gameState.getCurrentPlayer(), 4);
        assertThat(gameState.getCurrentPlayer().getCardsPlayedThisTurn().size(), is(1));
        assertThat(gameState.getTurnTracker().getActionsAvailable(), is(1));
    }

    @Test(expected = NotYourTurnException.class)
    public void testPlayCard2() throws GameException {
        LOG.info("testPlayCard2");
        gameState.playCard(player1, 0);
    }

    @Test
    public void testPlayCellar() throws GameException {
        LOG.info("testPlayCellar");
        Player player = makePlayer(Cellar.class);
        GameState gameState = makeGameState(player);

        gameState.playCard(player, 0);
        assertThat(gameState.getPendingEffect(player), instanceOf(DiscardThenDrawEffect.class));
    }

    @Test
    public void testCustomGame() throws InvalidCardTypeID {
        LOG.info("testCustomGame");
        Supply supply = customGameState.getSupply();
        for(int i=0;i<10;i++) {
            assertThat(listOfUsedCustomCards.contains(Card.fromID(supply.getUsedActionCards()[i])), is(true));
        }

    }

    @Test
    public void testGetWinnerByVictoryPoints() {
        Player player1 = makePlayer(DuchyCard.class);
        Player player2 = makePlayer(ProvinceCard.class);
        GameState gameState = makeGameState(player1, player2);

        assertThat(gameState.getWinner(), is(player2));
    }

    @Test
    public void testGetWinnerByTreasurePoints() {
        Player player1 = makePlayer(ProvinceCard.class, GoldCard.class);
        Player player2 = makePlayer(ProvinceCard.class, CopperCard.class);
        GameState gameState = makeGameState(player1, player2);

        assertThat(gameState.getWinner(), is(player1));
    }

}
