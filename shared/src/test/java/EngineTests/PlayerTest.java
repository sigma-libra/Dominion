package EngineTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.cards.kingdoms.*;
import shared.domain.cards.treasures.CopperCard;
import shared.domain.cards.treasures.GoldCard;
import shared.domain.cards.treasures.SilverCard;
import shared.domain.cards.victories.*;
import shared.domain.engine.*;
import shared.domain.exceptions.GameException;
import shared.dto.UserDTO;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class PlayerTest {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Player player0;
    private Player player1;
    private TurnTracker turnTracker;
    private Supply supply;

    @Before
    public void setUpPlayer() {
        LOG.info("setUpPlayer");
        player0 = new Player(new UserDTO("Player0"));
        player1 = new Player(new UserDTO("Player1"));
        turnTracker = new TurnTracker();
        Stack<Card> cardStack0 = new Stack<>();
        cardStack0.push(new ProvinceCard());
        cardStack0.push(new ProvinceCard());
        cardStack0.push(new ProvinceCard());
        Stack<Card> cardStack1 = new Stack<>();
        cardStack1.push(new DuchyCard());
        cardStack1.push(new DuchyCard());
        cardStack1.push(new DuchyCard());
        Stack<Card> cardStack2 = new Stack<>();
        cardStack2.push(new EstateCard());
        cardStack2.push(new EstateCard());
        cardStack2.push(new EstateCard());
        cardStack2.push(new EstateCard());
        cardStack2.push(new EstateCard());
        Stack<Card> cardStack3 = new Stack<>();
        cardStack3.push(new CurseCard());
        cardStack3.push(new CurseCard());
        cardStack3.push(new CurseCard());
        Stack<Card> cardStack4 = new Stack<>();
        cardStack4.push(new GoldCard());
        cardStack4.push(new GoldCard());
        cardStack4.push(new GoldCard());
        cardStack4.push(new GoldCard());
        cardStack4.push(new GoldCard());
        Stack<Card> cardStack5 = new Stack<>();
        cardStack5.push(new SilverCard());
        cardStack5.push(new SilverCard());
        cardStack5.push(new SilverCard());
        cardStack5.push(new SilverCard());
        Stack<Card> cardStack6 = new Stack<>();
        cardStack6.push(new CopperCard());
        cardStack6.push(new CopperCard());
        cardStack6.push(new CopperCard());
        cardStack6.push(new CopperCard());
        Stack<Card> cardStack7 = new Stack<>();
        cardStack7.push(new Cellar());
        cardStack7.push(new Cellar());
        Stack<Card> cardStack8 = new Stack<>();
        cardStack8.push(new Chapel());
        Stack<Card> cardStack9 = new Stack<>();
        cardStack9.push(new Council_Room());
        cardStack9.push(new Council_Room());
        cardStack9.push(new Council_Room());
        Stack<Card> cardStack10 = new Stack<>();
        cardStack10.push(new Feast());
        cardStack10.push(new Feast());
        cardStack10.push(new Feast());
        Stack<Card> cardStack11 = new Stack<>();
        cardStack11.push(new Chapel());
        Stack<Card> cardStack12 = new Stack<>();
        cardStack12.push(new Festival());
        cardStack12.push(new Festival());
        cardStack12.push(new Festival());
        Stack<Card> cardStack13 = new Stack<>();
        cardStack13.push(new Harbinger());
        cardStack13.push(new Harbinger());
        Stack<Card> cardStack14 = new Stack<>();
        cardStack14.push(new Laboratory());
        cardStack14.push(new Laboratory());
        Stack<Card> cardStack15 = new Stack<>();
        cardStack15.push(new Gardens());
        cardStack15.push(new Gardens());
        cardStack15.push(new Gardens());
        cardStack15.push(new Gardens());
        cardStack15.push(new Gardens());
        Stack<Card> cardStack16 = new Stack<>();
        cardStack16.push(new Merchant());
        cardStack16.push(new Merchant());
        cardStack16.push(new Merchant());
        cardStack16.push(new Merchant());
        ArrayList<Stack<Card>> cardPiles0 = new ArrayList<>();
        cardPiles0.add(cardStack0);
        cardPiles0.add(cardStack1);
        cardPiles0.add(cardStack2);
        cardPiles0.add(cardStack3);
        cardPiles0.add(cardStack4);
        cardPiles0.add(cardStack5);
        cardPiles0.add(cardStack6);
        cardPiles0.add(cardStack7);
        cardPiles0.add(cardStack8);
        cardPiles0.add(cardStack9);
        cardPiles0.add(cardStack10);
        cardPiles0.add(cardStack11);
        cardPiles0.add(cardStack12);
        cardPiles0.add(cardStack13);
        cardPiles0.add(cardStack14);
        cardPiles0.add(cardStack15);
        cardPiles0.add(cardStack16);
        supply = new Supply(cardPiles0);

        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
    }

    @Test
    public void getInitialSizesTest() {
        LOG.info("getInitialSizesTest");
        LOG.info("" + player0.getDeckSize());
        LOG.info("" + player0.getHandSize());
        assertThat(player0.getDeckSize(), is(5));
        assertThat(player0.getHandSize(), is(5));
        assertThat(player1.getDeckSize(), is(5));
        assertThat(player1.getHandSize(), is(5));
    }

    @Test(expected = GameException.class)
    public void buyCardTest0() throws GameException {
        LOG.info("buyCardTest");
        player1.buyCard(2, supply, turnTracker);
    }

    @Test(expected = GameException.class)
    public void buyCardTest1() throws GameException {
        LOG.info("buyCardTest");
        turnTracker.nextPhase(player0);
        player1.buyCard(2, supply, turnTracker);
    }

    @Test
    public void buyCardTest2() throws GameException {
        LOG.info("buyCardTest");
        turnTracker.nextPhase(player0);
        turnTracker.addCredit(2);
        assertThat(player0.getDiscard().size(), is(0));
        player0.buyCard(2, supply, turnTracker);
        assertThat(turnTracker.getBuysAvailable(), is(0));
        assertThat(turnTracker.getCredit(), is(0));
        assertThat(player0.getDiscard().size(), is(1));
        assertEquals(player0.getDiscard().get(0).getClass(), EstateCard.class);
    }

    @Test
    public void testIsPlayable() {
        LOG.info("testIsPlayable");
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        for (int i = 0; i < player0.getHand().size(); i++) {
            assertFalse(player0.isPlayable(i, turnTracker));
        }
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        for (int i = 0; i < player0.getHand().size(); i++) {
            if (player0.getHand().get(i).getClass().equals(CopperCard.class)) {
                assertTrue(player0.isPlayable(i, turnTracker));
            } else {
                assertFalse(player0.isPlayable(i, turnTracker));
            }
        }
    }

    @Test
    public void testIsPlayableCards() {
        LOG.info("testIsPlayableCards");
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        assertThat(player0.playableCardsIndices(turnTracker).length, is(0));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        ArrayList<Integer> l = new ArrayList<>();
        for (int i = 0; i < player0.getHand().size(); i++) {
            if (player0.getHand().get(i).getClass().equals(CopperCard.class)) {
                l.add(i);
            }
        }
        assertThat(player0.playableCardsIndices(turnTracker).length, is(l.size()));
        int[] p = player0.playableCardsIndices(turnTracker);
        for (int i = 0; i < p.length; i++) {
            assertTrue(l.contains(p[i]));
        }
    }

    @Test
    public void testPlayCard() {
        LOG.info("testPlayCardAndBuyCard");
        // turn 1 player0
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        assertThat(player0.playableCardsIndices(turnTracker).length, is(0));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        assertTrue(player0.playableCardsIndices(turnTracker).length > 0);
        for (int i = 0; i < player0.playableCardsIndices(turnTracker).length; i++) {
            LOG.info(player0.playableCardsIndices(turnTracker)[i] + " - " + player0.getHand().get(i).toString());
        }
        for (Card c : player0.getHand()) {
            LOG.info(c.toString());
        }
        /*int[] x = supply.buyablePilesIndices(turnTracker);
        for (int i = 0; i < x.length; i++) {
            LOG.info("" + x[i]);
        }*/
        // turn 1 player1
    }

    @Test
    public void testNameOfCardInHandPosition() {
        List<String> initialHandCardNames = new ArrayList<>();
        for(int i = 0; i < player0.getHandSize(); i++) {
            initialHandCardNames.add(player0.getHand().get(i).getName());
        }

        assertTrue(initialHandCardNames.contains("Copper") || initialHandCardNames.contains("Estate"));
    }

    @Test
    public void getVictoryPointsTest() {
        Assert.assertEquals(player0.getVictoryPoints(), 3);
        player0.getDiscard().add(new Gardens());
        Assert.assertEquals(player0.getVictoryPoints(), 4);
    }
}