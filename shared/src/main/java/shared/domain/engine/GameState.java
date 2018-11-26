package shared.domain.engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.effect.CardEffect;
import shared.domain.effect.NoPlayerChoosesEffect;
import shared.domain.effect.PlayerChoosesEffect;
import shared.domain.exceptions.GameException;
import shared.domain.exceptions.NotYourTurnException;
import shared.dto.GameStateDTO;
import shared.dto.UserDTO;
import shared.util.LogUtil;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class modeling the GameState
 * @author Alex
 */
public class GameState implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final long serialVersionUID = -168907654722821473L;

    /**
     * no of piles on table
     */
    private final int NB_STACKS = 17;

    /**
     * current dominion players
     */
    private List<Player> players;

    /**
     * player index whos turn is
     */
    private int currentPlayerIndex;

    /**
     * dominion cards/piles to play with
     */
    private Supply supply;

    /**
     * array of action cards
     */
    private int[] actionCards;

    /**
     * get whos turn is with the help of turnTracker
     */
    private TurnTracker turnTracker;

    /**
     * trashpile
     */
    private List<Card> trashPile;

    /**
     * cards played in currnet turn
     */

    /**
     * stack of pending effects
     */
    private Map<Player,Stack<CardEffect>> pendingEffects;

    /**
     * List of all the plays so far in text form
     */
    private List<String> playLog;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameState gameState = (GameState) o;
        return NB_STACKS == gameState.NB_STACKS &&
            currentPlayerIndex == gameState.currentPlayerIndex &&
            Objects.equals(players, gameState.players) &&
            Objects.equals(supply, gameState.supply) &&
            Arrays.equals(actionCards, gameState.actionCards) &&
            Objects.equals(turnTracker, gameState.turnTracker) &&
            Objects.equals(trashPile, gameState.trashPile) &&
            Objects.equals(pendingEffects, gameState.pendingEffects) &&
            Objects.equals(playLog, gameState.playLog);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(NB_STACKS, players, currentPlayerIndex, supply, turnTracker, trashPile, pendingEffects, playLog);
        result = 31 * result + Arrays.hashCode(actionCards);
        return result;
    }

    public GameState(List<Player> players) {
        this(players, new ArrayList<>());
    }

    /**
     * calls constructor with supply(playercount)
     *
     * @param players players which are going to play
     */
    public GameState(List<Player> players, List<String> playLog) {
        this(players, new Supply(players.size(),null), playLog);
    }


    /**
     * calls constructor with supply(playercount, actioncards defined)
     *
     * @param players players which are going to play
     */
    public GameState(List<Player> players, List<String> playLog, int [] customActionCardIds) {
        this(players, new Supply(players.size(), customActionCardIds), playLog);
    }



    /**
     * @param players players which are going to play
     * @param supply  cards/piles
     */
    public GameState(List<Player> players, Supply supply, List<String> playLog) {
        LOG.info("GameState");
        this.players = players;
        this.supply = supply;
        this.playLog = playLog;
        this.turnTracker = new TurnTracker();
        this.setActionCards(supply.getUsedActionCards());
        trashPile = new ArrayList<>();

        pendingEffects = new HashMap<>();
        for (Player p : players) {
            pendingEffects.put(p, new Stack<>());
        }
    }

    public void removePlayer(Player player) throws GameException {
        int index = 0;
        for (Player p : players){
            if (p == player)
                break;
            index++;
        }

        // if it's currently this player's turn, end his turn before we remove him
        if (index == currentPlayerIndex) {
            while(!turnTracker.nextPhase(player)){}
        }

        if (!players.remove(player))
            throw new GameException("That player is not participating in this game");

        if (index < currentPlayerIndex)
            currentPlayerIndex--;
        else if (currentPlayerIndex >= players.size())
            currentPlayerIndex = 0;

        playLog.add(String.format("%s has left the game", player.getUser().getUserName()));
    }

    /**
     * current player buys a card
     *
     * @param pileNo pile the player is buying from
     */
    public void buyCard(Player player, int pileNo) throws GameException {
        LOG.info("buyCard");
        if (player != getCurrentPlayer()) {
            throw new NotYourTurnException();
        }

        Card card = supply.getCard(pileNo);
        player.buyCard(pileNo, supply, turnTracker);


        playLog.add(String.format("%s bought a %s card", player.getUser().getUserName(), card.getName()));

    }

    /**
     * current player plays a card
     *
     * @param position Position of card in hand
     */
    public void playCard(Player player, int position) throws GameException {
        LOG.info("playCard");
        if (player != getCurrentPlayer())
            throw new NotYourTurnException();

        Card card = player.getHand().get(position);

        player.playCard(position, this);
        playLog.add(String.format("%s played a %s card", player.getUser().getUserName(), card.getName()));


        executePendingEffects();
    }

    /**
     * method executes pending effect
     *
     * @param arguments Contains choices the player made in regard to the pending effect.
     */
    public void executeEffect(Player player, int[] arguments) throws GameException {
        LOG.info("executeEffect");

        Stack<CardEffect> stack = pendingEffects.get(player);

        if (stack.isEmpty()) {
            throw new GameException("No effect is queued for you to execute");
        }

        PlayerChoosesEffect effect = (PlayerChoosesEffect)stack.pop();
        effect.execute(this, player, arguments);

        executePendingEffects();
    }

    /**
     * finishes a player's turn
     */
    public void finishPhase(Player player) throws GameException {
        LOG.info("finishPhase");
        if (player.getUser().getId() != getCurrentPlayer().getUser().getId()) {
            throw new NotYourTurnException();
        }

        String playerName = player.getUser().getUserName();
        if (turnTracker.nextPhase(player)) {
            currentPlayerIndex = (currentPlayerIndex == (players.size() - 1)) ? 0 : (currentPlayerIndex + 1);
            playLog.add(playerName + " finished their turn");
            playLog.add(getCurrentPlayer().getUser().getUserName() + " started their turn");
        }
    }

    /**
     * Makes a player trash cards and writes it into the game log
     * @param player the affected player
     * @param indices indices of the cards to trash in the player's hand
     * @throws GameException
     */
    public void trashCards(Player player, int[] indices) throws GameException {
        LOG.info("trashCards");
        Arrays.sort(indices);

        if(indices.length > 0) {
            String message = String.format("%s trashed %s", player.getUser().getUserName(), LogUtil.listHandCards(player, indices));
            playLog.add(message);
        }

        for(int i=indices.length-1; i>=0; i--){
            int index = indices[i];
            player.getHand().remove(index);
        }


    }

    /**
     * Makes a player discard cards and writes it into the game log
     * @param player the affected player
     * @param indices indices of the cards to discard in the player's hand
     * @throws GameException
     */
    public void discardCards(Player player, int[] indices) throws GameException {
        LOG.info("discardCards");
        Arrays.sort(indices);

        if(indices.length > 0) {
            String message = String.format("%s discarded %s", player.getUser().getUserName(), LogUtil.listHandCards(player, indices));
            playLog.add(message);
        }

        for (int i = indices.length - 1; i >= 0; i--) {
            int index = indices[i];
            player.discardCard(index);
        }

    }

    /**
     * Makes a player return cards to draw and writes it into the game log
     * @param player the affected player
     * @param indices indices of the cards to return to deck in the player's hand
     * @throws GameException
     */
    public void returnCardsToDeck(Player player, int[] indices) throws GameException {
        LOG.info("returnCardsToDeck");
        Arrays.sort(indices);

        if(indices.length > 0) {
            String message = String.format("%s returned to deck %s", player.getUser().getUserName(), LogUtil.listHandCards(player, indices));
            playLog.add(message);
        }

        for (int i = indices.length - 1; i >= 0; i--) {
            int index = indices[i];
            player.returnCardToDeck(index);
        }

    }

    /**
     * Makes a player draw cards and writes it into the game log
     * @param player the affected player
     * @param howMany the number of cards to draw
     * @throws GameException
     */
    public void drawCards(Player player, int howMany) throws GameException {
        LOG.info("drawCards");
        player.drawCards(howMany);

        playLog.add(String.format("%s drew %d card%s", player.getUser().getUserName(), howMany, howMany==1?"":"s"));
    }

    /**
     * executes the queued CardEffects until the queue is empty or an interactive effect is found
     * @throws GameException
     */
    private void executePendingEffects() throws GameException {
        LOG.info("executePendingEffects");

        for (Player player : players) {
            Stack<CardEffect> stack = pendingEffects.get(player);
            while (!stack.isEmpty()) {
                CardEffect effect = stack.peek();

                if (effect instanceof NoPlayerChoosesEffect) {
                    ((NoPlayerChoosesEffect) stack.pop()).execute(this, player);
                } else {
                    ((PlayerChoosesEffect) effect).updateChoices(this, player);
                    break;
                }
            }
        }
    }

    public GameStateDTO toDTO(Player player){
        LOG.info("toDTO");
        LOG.info("Size of players: " + players.size());
        for(int i = 0; i < players.size(); i++) {
            if(player.getUser().getId() == players.get(i).getUser().getId()) {
                return toDTO(i);
            }
        }
        return null;
    }

    /**
     * Converts the current gamestate to a gamestateDTO
     * @param playerIndex player index
     * @return GameStateDTO
     */
    public GameStateDTO toDTO(int playerIndex) {
        LOG.info("toDTO");

        GameStateDTO dto = new GameStateDTO();

        Player player;
        try {
            player = players.get(playerIndex);
        } catch (IndexOutOfBoundsException e){
            player = null;
        }

        dto.setPhase(isOver() ? GamePhase.OVER : GamePhase.ONGOING);

        dto.setCurrentlyPlaying(currentPlayerIndex);

        List<UserDTO> users = players.stream().map(p -> p.getUser()).collect(Collectors.toList());
        dto.setPlayers(users);

        dto.setCurrentCredit(turnTracker.getCredit());
        dto.setActionCount(turnTracker.getActionsAvailable());
        dto.setBuyCount(turnTracker.getBuysAvailable());
        dto.setTurnPhase(turnTracker.getPhase());


        //Set action cards
        dto.setActionCardIds(actionCards);

        //Set sizes of tabletop piles
        int[] supplyPilesSizes = new int[NB_STACKS];
        for (int i = 0; i < NB_STACKS; i++) {
            supplyPilesSizes[i]= supply.pileSize(i);
        }
        dto.setSupplyPilesSizes(supplyPilesSizes);

        //st deck sizes (number of cards in possession of a player)
        int[] deckSizes = new int[players.size()];


        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            deckSizes[i] = p.getDeckSize() + p.getHandSize() + p.getDiscardSize() + p.getCardsPlayedThisTurn().size();
        }
        dto.setDeckSizes(deckSizes);

        //Set hand sizes
        int[] handSizes = new int[players.size()];
        for (int i = 0; i < players.size(); i++) {
            handSizes[i] = players.get(i).getHandSize();
        }
        dto.setHandSizes(handSizes);

        //Set victory points
        dto.setVictoryPoints(victoryPointsOfAll());

        //Set playable
        if (playerIndex == currentPlayerIndex) {
            dto.setPlayableCardsIndices(player.playableCardsIndices(turnTracker));
            dto.setBuyablePilesIndices(supply.buyablePilesIndices(turnTracker));
        }

        if (player != null) {
            //set hand
            dto.setHandCardIds(player.getHandCardIds());
            dto.setDrawSize(player.getDeckSize());
            dto.setDiscardSize(player.getDiscardSize());

            //set effects
            Stack<CardEffect> queuedEffects = pendingEffects.get(player);
            if (!queuedEffects.empty())
                dto.setPendingChoiceEffect((PlayerChoosesEffect) queuedEffects.peek());
        }

        dto.setGamePlayLog(playLog);

        return dto;
    }

    /**
     *
     * @return array of actioncards
     */
    public int[] getActionCards() {
        LOG.info("getActionCards");
        return actionCards;
    }

    /**
     *
     * @param actionCards array of actioncards to be set
     */
    public void setActionCards(int[] actionCards) {
        LOG.info("setActionCards");
        this.actionCards = actionCards;
    }

    /**
     * get victorypoints of all players
     * @return array of victory points, index .. player
     */
    private int[] victoryPointsOfAll() {
        LOG.info("victoryPointsOfAll");
        int[] points = new int[players.size()];
        for (int i = 0; i < players.size(); i++){
            points[i] = players.get(i).getVictoryPoints();
        }
        return points;
    }

    /**
     * is the game over
     * @return true if any endcondiction is satisfied
     */
    public boolean isOver() {
        LOG.info("isOver");
        if (players.size() <= 1)
            return true;

        return supply.isEndConditionSatisfied();
    }

    /**
     *
     * @return turntracker
     */
    public TurnTracker getTurnTracker() {
        LOG.info("getTurnTracker");
        return turnTracker;
    }

    /**
     *
     * @return players
     */
    public List<Player> getPlayers() {
        LOG.info("getPlayers");
        return players;
    }

    /**
     *
     * @return supply
     */
    public Supply getSupply() {
        LOG.info("getSupply");
        return supply;
    }

    /**
     * Adds card to trash pile
     * @param c
     */
    public void addToTrashPile(Card c) {
        LOG.info("addToTrashPile");
        assert c != null;
        trashPile.add(c);
    }

    /**
     * adds effects to stack
     * @param effects
     */
    public void addEffects(CardEffect[] effects) {
        LOG.info("addEffects");
        addEffects(effects, getCurrentPlayer());
    }

    public void addEffects(CardEffect[] effects, Player player){
        Stack<CardEffect> stack = pendingEffects.get(player);
        for (int i = effects.length-1; i >= 0; i--) {
            stack.push(effects[i]);
        }
    }

    public void addEffect(CardEffect effect, Player player){
        Stack<CardEffect> stack = pendingEffects.get(player);
        stack.push(effect);
    }


    /**
     * get current player's index
     * @return index of current player
     */
    public int getCurrentPlayerIndex() {
        LOG.info("getCurrentPlayerIndex");
        return currentPlayerIndex;
    }

    /**
     * get current player
     * @return Player whos turn is
     */
    public Player getCurrentPlayer() {
        LOG.info("getCurrentPlayer");
        return players.get(currentPlayerIndex);
    }

    /**
     * Get the play log
     * @return the list of plays in text form
     */
    public List<String> getPlayLog() {
        LOG.info("getPlayLog");
        return playLog;
    }

    /**
     * Get the next queued CardEffect
     * @return the next queued CardEffect
     */
    public CardEffect getPendingEffect(Player player) {
        LOG.info("getPendingEffect");
        Stack<CardEffect> stack = pendingEffects.get(player);
        return stack.isEmpty() ? null : stack.peek();
    }

    public CardEffect getPendingEffect() {
        return getPendingEffect(getCurrentPlayer());
    }

    /**
     * Get all queued CardEffects
     * @return a stack of queued CardEffects
     */
    public Map<Player,Stack<CardEffect>> getPendingEffects() {
        LOG.info("getPendingEffect");
        return pendingEffects;
    }

    public Stack<CardEffect> getPendingEffects(Player player) {
        LOG.info("getPendingEffect");
        return pendingEffects.get(player);
    }

    /**
     * Set the list of plays
     *
     * @param playLog
     */
    public void setPlayLog(List<String> playLog) {
        LOG.info("setPlayLog");
        this.playLog = playLog;
    }


    /**
     * Returns the currently highest scoring player
     */
    public Player getWinner() {
        LOG.info("getWinner");
        Map<Player,Integer> victoryPoints = new HashMap<>();
        Map<Player,Integer> treasurePoints = new HashMap<>();

        for (Player player : players) {
            victoryPoints.put(player, player.getVictoryPoints());
            treasurePoints.put(player, player.getTreasurePoints());
        }

        Comparator<Player> comparator = Comparator.comparing(p -> victoryPoints.get(p));
        comparator = comparator.thenComparing(Comparator.comparing(p -> treasurePoints.get(p)));

        LOG.info(players.toString());

        Player winner = Collections.max(players, comparator);
        return winner;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public void setTurnTracker(TurnTracker turnTracker) {
        this.turnTracker = turnTracker;
    }
}
