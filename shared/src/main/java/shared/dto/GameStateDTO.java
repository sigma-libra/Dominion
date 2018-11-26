package shared.dto;

/**
 * @Author Alex
 */

import shared.domain.effect.PlayerChoosesEffect;
import shared.domain.engine.GamePhase;
import shared.domain.engine.TurnPhase;

import java.util.List;

/**
 * Transfers relevant data of the game state to clients. Each client should receive its own server.dto,
 * because of security purposes (players should not know about the content of other players hands).
 */

public class GameStateDTO {

    /** supplyPilesSizes
     *
     * indices of piles:
     * 0 = province
     * 1 = duchy
     * 2 = estate
     * 3 = curse
     * 4 = gold
     * 5 = silver
     * 6 = copper
     * 7-16 = kingdom cards
     */

    //values that shouldn't change (move later to different dto?)
    private int[] actionCardIds = null; //Action cards used in game

    private int[] handCardIds = null; // Position is equivalent to the representation in game state
    private int[] supplyPilesSizes = null; // size of piles
    private int[] deckSizes = null; //size of decks
    private int[] handSizes = null; // number of cards in each players hand
    private int[] victoryPoints = null; // points per player
    private int[] playableCardsIndices = null;
    private int[] buyablePilesIndices = null;
    private PlayerChoosesEffect pendingChoiceEffect = null;
    private GamePhase phase = null;
    private TurnPhase turnPhase = null;

    private List<UserDTO> players = null;
    private UserDTO gameMaster = null;

    private int currentlyPlaying;
    private int currentCredit;
    private int actionCount;
    private int buyCount;
    private int drawSize;
    private int discardSize;

    private List<String> gamePlayLog;   //list of plays that have taken place so far

    public GameStateDTO() {
    }


    public int[] getActionCardIds() {
        return actionCardIds;
    }

    public void setActionCardIds(int[] actionCardIds) {
        this.actionCardIds = actionCardIds;
    }

    public int[] getHandCardIds() {
        return handCardIds;
    }

    public void setHandCardIds(int[] handCardIds) {
        this.handCardIds = handCardIds;
    }

    public int[] getSupplyPilesSizes() {
        return supplyPilesSizes;
    }

    public void setSupplyPilesSizes(int[] supplyPilesSizes) {
        this.supplyPilesSizes = supplyPilesSizes;
    }

    public int[] getDeckSizes() {
        return deckSizes;
    }

    public void setDeckSizes(int[] deckSizes) {
        this.deckSizes = deckSizes;
    }

    public int[] getHandSizes() {
        return handSizes;
    }

    public void setHandSizes(int[] handSizes) {
        this.handSizes = handSizes;
    }

    public int[] getVictoryPoints() {
        return victoryPoints;
    }

    public void setVictoryPoints(int[] victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public int[] getPlayableCardsIndices() {
        return playableCardsIndices;
    }

    public void setPlayableCardsIndices(int[] playableCardsIndices) {
        this.playableCardsIndices = playableCardsIndices;
    }

    public int[] getBuyablePilesIndices() {
        return buyablePilesIndices;
    }

    public void setBuyablePilesIndices(int[] buyablePilesIndices) {
        this.buyablePilesIndices = buyablePilesIndices;
    }

    public PlayerChoosesEffect getPendingChoiceEffect() {
        return pendingChoiceEffect;
    }

    public void setPendingChoiceEffect(PlayerChoosesEffect pendingChoiceEffect) {
        this.pendingChoiceEffect = pendingChoiceEffect;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public TurnPhase getTurnPhase() {
        return turnPhase;
    }

    public void setTurnPhase(TurnPhase turnPhase) {
        this.turnPhase = turnPhase;
    }

    public List<UserDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<UserDTO> players) {
        this.players = players;
    }

    public UserDTO getGameMaster() {
        return gameMaster;
    }

    public int getDrawSize() {
        return drawSize;
    }

    public void setDrawSize(int drawSize) {
        this.drawSize = drawSize;
    }

    public int getDiscardSize() {
        return discardSize;
    }

    public void setDiscardSize(int discardSize) {
        this.discardSize = discardSize;
    }

    public void setGameMaster(UserDTO gameMaster) {
        this.gameMaster = gameMaster;
    }

    public int getCurrentlyPlaying() {
        return currentlyPlaying;
    }

    public void setCurrentlyPlaying(int currentlyPlaying) {
        this.currentlyPlaying = currentlyPlaying;
    }

    public int getCurrentCredit() {
        return currentCredit;
    }

    public void setCurrentCredit(int currentCredit) {
        this.currentCredit = currentCredit;
    }

    public List<String> getGamePlayLog() { return gamePlayLog; }

    public void setGamePlayLog(List<String> gamePlayLog) { this.gamePlayLog = gamePlayLog; }

    public int getActionCount() {
        return actionCount;
    }

    public void setActionCount(int actionCount) {
        this.actionCount = actionCount;
    }

    public int getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(int buyCount) {
        this.buyCount = buyCount;
    }
}
