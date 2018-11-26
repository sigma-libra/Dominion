package EngineTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.engine.Player;
import shared.domain.engine.TurnPhase;
import shared.domain.engine.TurnTracker;

import java.lang.invoke.MethodHandles;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TurnTrackerTest {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private TurnTracker turnTracker;
    private Player player0;
    private Player player1;
    private Player player2;
    private Player player3;

    @Before
    public void setUpTracker() {
        LOG.info("setUpTracker");
        turnTracker = new TurnTracker();
        player0 = new Player();
        player1 = new Player();
        player2 = new Player();
        player3 = new Player();
    }

    @Test
    public void testNextPhaseOnePlayer() {
        LOG.info("testNextPhaseOnePlayer");
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
    }

    @Test
    public void testNextPhaseTwoPlayers() {
        LOG.info("testNextPhaseTwoPlayers");
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player1);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player1);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
    }

    @Test
    public void testNextPhaseThreePlayers() {
        LOG.info("testNextPhaseThreePlayers");
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player1);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player1);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player2);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player2);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
    }

    @Test
    public void testNextPhaseFourPlayers() {
        LOG.info("testNextPhaseFourPlayers");
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player1);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player1);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player2);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player2);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player3);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player3);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
    }

    @Test
    public void testNextPhaseFourPlayersMultipleTurns() {
        LOG.info("testNextPhaseFourPlayersMultipleTurns");
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player1);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player1);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player2);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player2);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player3);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player3);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player1);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player1);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player2);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player2);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player3);
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        turnTracker.nextPhase(player3);
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
    }

    @Test
    public void testAddBuysAvailable() {
        LOG.info("testAddBuysAvailable");
        assertThat(turnTracker.getBuysAvailable(), is(1));
        turnTracker.addBuysAvailable(1);
        assertThat(turnTracker.getBuysAvailable(), is(2));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getBuysAvailable(), is(2));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getBuysAvailable(), is(1));
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
    }

    @Test
    public void testAddActionsAvailable() {
        LOG.info("testAddActionsAvailable");
        assertThat(turnTracker.getActionsAvailable(), is(1));
        turnTracker.addActionsAvailable(1);
        assertThat(turnTracker.getActionsAvailable(), is(2));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getActionsAvailable(), is(2));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getActionsAvailable(), is(1));
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
    }

    @Test
    public void testAddCredit() {
        LOG.info("testAddCredit");
        assertThat(turnTracker.getCredit(), is(0));
        turnTracker.addCredit(1);
        assertThat(turnTracker.getCredit(), is(1));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getCredit(), is(1));
        turnTracker.nextPhase(player0);
        assertThat(turnTracker.getCredit(), is(0));
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
    }

    @Test
    public void testEquality() {
        LOG.info("testEquality");
        TurnTracker t1 = new TurnTracker();
        TurnTracker t2 = new TurnTracker();
        Assert.assertEquals(t1, t2);
    }
}
