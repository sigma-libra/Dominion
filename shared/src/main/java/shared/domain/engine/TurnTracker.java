package shared.domain.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Objects;

/**
 * turntracker class - responsible for game flow
 * @author Alex
 */
public class TurnTracker implements Serializable {

    /**
     * turn phase enum (action,buy)
     */
    private TurnPhase phase;

    /**
     * refers to the sum value of played treasure cards
     */
    private int credit;

    /**
     * no of actions available
     */
    private int actionsAvailable;

    /**
     * number of purchases available
     */
    private int buysAvailable;

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TurnTracker that = (TurnTracker) o;
        return credit == that.credit &&
            actionsAvailable == that.actionsAvailable &&
            buysAvailable == that.buysAvailable &&
            phase == that.phase;
    }

    @Override
    public int hashCode() {

        return Objects.hash(phase, credit, actionsAvailable, buysAvailable);
    }

    /**
     * initiates a turn
     */
    public TurnTracker() {
        LOG.info("TurnTracker");
        initiateTurn();
    }

    /**
     * method switches turn phase
     * @param currentPlayer needed to eventually execute cleanup-Phase
     * @return method call did end the turn
     */
    public boolean nextPhase(Player currentPlayer) {
        LOG.info("nextPhase");
        if (phase == TurnPhase.ACTION_PHASE) {
            phase = TurnPhase.BUY_PHASE;
            return false;
        }
        else {
            executeCleanupPhase(currentPlayer);
            return true;
        }
    }

    /**
     * player executes the cleanup phase - turntracker initiates the next turn
     * @param currentPlayer player executes phase
     */
    private void executeCleanupPhase(Player currentPlayer) {
        LOG.info("executeCleanupPhase");
        currentPlayer.executeCleanupPhase();
        initiateTurn();
    }

    /**
     * sets up a new turn
     */
    private void initiateTurn() {
        LOG.info("initiateTurn");
        credit = 0;
        actionsAvailable = 1;
        buysAvailable = 1;
        phase = TurnPhase.ACTION_PHASE;
    }

    /**
     * adjust buys available
     * @param number for in/decreasing available buys
     */
    public void addBuysAvailable(int number) {
        LOG.info("addBuysAvailable");
        buysAvailable += number;
    }

    /**
     * adjust actions available
     * @param number for in/decreasing available actions
     */
    public void addActionsAvailable(int number) {
        LOG.info("addActionsAvailable");
        actionsAvailable += number;
    }

    /**
     * adjust credit
     * @param number for in/decreasing available credit
     */
    public void addCredit(int number) {
        LOG.info("addCredit");
        credit += number;
    }

    /**
     *
     * @return phase
     */
    public TurnPhase getPhase() {
        LOG.info("getPhase");
        return phase;
    }

    /**
     *
     * @return credit
     */
    public int getCredit() {
        LOG.info("getCredit");
        return credit;
    }

    /**
     *
     * @return available actions
     */
    public int getActionsAvailable() {
        LOG.info("getActionsAvailable");
        return actionsAvailable;
    }

    /**
     *
     * @return available buys
     */
    public int getBuysAvailable() {
        LOG.info("getBuysAvailable");
        return buysAvailable;
    }

    public void setPhase(TurnPhase phase) {
        this.phase = phase;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public void setActionsAvailable(int actionsAvailable) {
        this.actionsAvailable = actionsAvailable;
    }

    public void setBuysAvailable(int buysAvailable) {
        this.buysAvailable = buysAvailable;
    }

    @Override
    public String toString() {
        return "TurnTracker{" +
            "phase=" + phase +
            ", credit=" + credit +
            ", actionsAvailable=" + actionsAvailable +
            ", buysAvailable=" + buysAvailable +
            '}';
    }
}
