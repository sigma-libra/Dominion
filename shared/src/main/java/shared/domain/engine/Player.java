package shared.domain.engine;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.*;
import shared.domain.cards.kingdoms.Merchant;
import shared.domain.cards.treasures.CopperCard;
import shared.domain.cards.victories.EstateCard;
import shared.domain.cards.victories.Gardens;
import shared.domain.exceptions.EmptyCardDeckException;
import shared.domain.exceptions.GameException;
import shared.domain.exceptions.InvalidIDException;
import shared.dto.UserDTO;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * class modeling a dominion player
 * @author Alex
 */
public class Player implements Serializable {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * player's hand
     */
    private CardPile hand;

    /**
     * player's discard pile
     */
    private CardPile discard;

    /**
     * player's deck
     */
    private CardPile deck;
    private UserDTO user;

    private CardPile cardsPlayedThisTurn;


    /**
     * ?
     */
    public Player() {
        this(null);
    }

    /**
     * initiates a new player
     * @param user user becomes a player
     */
    public Player(UserDTO user){
        LOG.info("Player");
        this.user = user;
        hand = new CardPile();
        discard = new CardPile();
        deck = new CardPile();
        cardsPlayedThisTurn = new CardPile();

        for (int i = 0; i < 3; i++) {
            deck.add(new EstateCard());
        }
        for (int i = 0; i < 7; i++) {
            deck.add(new CopperCard());
        }

        deck.shuffle();
        drawCards(5);
    }

    /**
     * get userDTO
     * @return user
     */
    public UserDTO getUser(){
        LOG.info("UserDTO");
        return user;
    }

    /**
     * player buys a card
     * @param pileNo from pile x
     * @param supply supply (decks/cards)
     * @param turnTracker checks if buyable
     */
    public void buyCard(int pileNo, Supply supply, TurnTracker turnTracker) throws GameException {
        LOG.info("buyCard");
        if (!supply.isBuyable(pileNo, turnTracker)) {
            throw new GameException("You can't buy this card right now");
        }

        turnTracker.addCredit(supply.getPrice(pileNo)*-1);
        turnTracker.addBuysAvailable(-1);
        discard.add(supply.retrieveCard(pileNo));
    }

    /**
     * checks if card is playable
     * @param position position of card in hand
     * @param turnTracker checks if card is playable
     * @return bool if card is playable
     */
    public boolean isPlayable(int position, TurnTracker turnTracker) {
        LOG.info("isPlayable");
        Card card = hand.get(position);
        return isPlayable(card, turnTracker);
    }

    public boolean isPlayable(Card card, TurnTracker turnTracker) {
        LOG.info("isPlayable");
        if (!(card instanceof PlayableCard))
            return false;

        if (turnTracker.getPhase() == TurnPhase.BUY_PHASE) {
            return card instanceof TreasureCard;
        } else {
            return card instanceof KingdomCard && turnTracker.getActionsAvailable() > 0;
        }
    }

    /**
     * player plays a card
     * @param position card's position in hand
     * @param gameState changing gamestate
     */
    public void playCard(int position, GameState gameState) throws InvalidIDException {
        LOG.info("playCard");

        Card card;
        try {
            card = hand.get(position);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidIDException(String.format("%d is not a valid hand card id", position));
        }

        if (!isPlayable(position, gameState.getTurnTracker())) {
            throw new InvalidIDException(String.format("You can't play card #%d right now", position));
        }

        gameState.getTurnTracker().addActionsAvailable(-1);

        ((PlayableCard) hand.remove(position)).applyEffects(gameState);
        cardsPlayedThisTurn.add(card);
    }

    /**
     * Adds a card in a given position in hand to discard pile
     * @param position
     * @throws InvalidIDException
     */
    public void discardCard(int position) throws InvalidIDException {
        LOG.info("discardCard");
        Card card;
        try {
            card = hand.remove(position);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidIDException(String.format("Cannot discard card #%d; player is only holding %d cards", position, hand.size()));
        }
        discard.add(card);
    }

    /**
     * Adds a card to the discard pile
     * @param card
     * @throws InvalidIDException
     */
    public void discardCard(Card card) throws InvalidIDException {
        LOG.info("discardCard");
        discard.add(card);
    }

    /**
     * Adds a card in a given position in hand to draw pile
     * @param position
     * @throws InvalidIDException
     */
    public void returnCardToDeck(int position) throws InvalidIDException {
        LOG.info("returnCardToDeck");
        Card card;
        try {
            card = hand.remove(position);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidIDException(String.format("Cannot move card #%d; player is only holding %d cards", position, hand.size()));
        }
        deck.add(card);
    }



    /**
     * get player's vps summed up
     * @return victory points
     */
    public int getVictoryPoints() {
        LOG.info("getVictoryPoints");
        int points = 0;
        points += getVictoryPointsFrom(hand);
        points += getVictoryPointsFrom(deck);
        points += getVictoryPointsFrom(discard);
        return points;
    }

    /**
     * helping method to get vps from each pile
     * @param list pile
     * @return vps/pile
     *
     * Note: might need to be static
     */
    public int getVictoryPointsFrom(Iterable<Card> list) {
        LOG.info("getVictoryPointsFrom");
        int points = 0;
        for (Card c: list) {
            if(c instanceof Gardens) {
                //gardens gives 1 point for every 10 cards
                int nbCards = hand.size() + deck.size() + discard.size();
                points += ((Gardens) c).getVictoryPoints(nbCards);
            }
             else if (c instanceof VictoryCard) {
                points += ((VictoryCard) c).getVictoryPoints();
            }
        }
        return points;
    }

    /**
     * get player's tps summed up
     * @return treasure points
     */
    public int getTreasurePoints() {
        int points = 0;
        points += getTreasurePointsFrom(hand);
        points += getTreasurePointsFrom(deck);
        points += getTreasurePointsFrom(discard);
        return points;
    }

    /**
     * helping method to get tps from each pile
     * @param list pile
     * @return tps/pile
     *
     * Note: might need to be static
     */
    private static int getTreasurePointsFrom(Iterable<Card> list) {
        int points = 0;
        for (Card c: list) {
            if (c instanceof TreasureCard)
                points += ((TreasureCard) c).getCoinValue();
        }
        return points;
    }


    /**
     * player draws <code>howMany</code> cards
     * @param howMany how many cards a player draws
     */
    public void drawCards(int howMany) {
        LOG.info("drawCards");

        List<Card> cards;
        try {
            cards = deck.drawCards(howMany);
        } catch (EmptyCardDeckException e){
            //draw all the cards in the deck
            cards = deck.getCards();
            howMany -= cards.size();
            hand.addAll(cards);
            deck.clear();

            refreshDeck();

            //draw the remaining cards
            try {
                cards = deck.drawCards(howMany);
            } catch (EmptyCardDeckException ex){
                //if there aren't enough cards, just draw them all
                cards = new ArrayList<>(deck.getCards()); //create a copy of the list
                deck.clear();
            }
        }

        hand.addAll(cards);
    }

    /**
     * Moves the cards from discard to draw
     */
    public void refreshDeck() {
        //turn the discard pile into the deck
        LOG.info("Refresh deck");
        deck.setCards(discard);
        deck.shuffle();
        discard.clear();
    }

    /**
     * Discards the top card from deck
     */
    public void discardCardFromDeck() {
        LOG.info("discardCardFromDeck");
        if(!deck.isEmpty()) {
            Card card = deck.getTopCard();
            deck.remove(card);
            discard.add(card);
        }
    }

    /**
     * executes cleanup phase
     * (Note: Merchant cards have values depending on the turn, so they must be reset during cleanup)
     */
    public void executeCleanupPhase() {
        LOG.info("executeCleanupPhase");
        discard.addAll(hand);
        for(Card playedCard: cardsPlayedThisTurn) {
            if(Merchant.class.isInstance(playedCard)) {
                ((Merchant)playedCard).setAlreadyAddedCredit(false);
            }
        }
        discard.addAll(cardsPlayedThisTurn);
        cardsPlayedThisTurn.clear();
        hand.clear();
        drawCards(5);
    }

    /**
     *
     * @return current hand size
     */
    public int getHandSize() {
        LOG.info("getHandSize");
        return hand.size();
    }


    /**
     *
     * @return current deck size
     */
    public int getDeckSize() {
        LOG.info("getDeckSize");
        return deck.size();
    }

    /**
     *
     * @return current discard size
     */
    public int getDiscardSize() {
        LOG.info("getDiscardSize");
        return discard.size();
    }

    /**
     *
     * @param deck deck to be set
     */
    public void setDeck(Iterable<Card> deck) {
        LOG.info("setDeck");
        this.deck.setCards(deck);
    }

    /**
     * get hand card's ids
     * @return hand card's ids
     */
    public int[] getHandCardIds() {
        LOG.info("getHandCardIds");
        int[] ids = new int[hand.size()];
        for (int i = 0; i < hand.size(); i++) {
            ids[i] = hand.get(i).getID();
        }
        return ids;
    }

    /**
     * get all playable cards
     * @param turnTracker checks if card is playable
     * @return indices of playable cards
     */
    public int[] playableCardsIndices(TurnTracker turnTracker) {
        LOG.info("playableCardsIndices");
        ArrayList<Integer> temp = new ArrayList<>();
        for (int position = hand.size()-1; position >= 0; position--) {
            if (isPlayable(position, turnTracker))
                temp.add(position);
        }
        return temp.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     *
     * @return current hand
     */
    public CardPile getHand() {
        LOG.info("getHand");
        return hand;
    }

    // for testing
    public CardPile getDiscard() {
        LOG.info("getDiscard");
        return discard;
    }

    // for testing
    public CardPile getDeck() {
        LOG.info("getDeck");
        return deck;
    }

    // for testing
    public void setHand(Iterable<Card> hand) {
        LOG.info("setHand");
        this.hand.setCards(hand);
    }
    public void setDiscardPile(Iterable<Card> cards) {
        LOG.info("setDiscardPile");
        this.discard.setCards(cards);
    }

    public CardPile getCardsPlayedThisTurn() {
        LOG.info("getCardsPlayedThisTurn");
        return cardsPlayedThisTurn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == null || o == null) return false;
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(hand, player.hand) &&
            Objects.equals(discard, player.discard) &&
            Objects.equals(deck, player.deck) &&
            (user == null || player.user == null || Objects.equals(user, player.user)) &&
            Objects.equals(cardsPlayedThisTurn, player.cardsPlayedThisTurn);
    }

    /*@Override
    public int hashCode() {

        return Objects.hash(hand, discard, deck, user, cardsPlayedThisTurn);
    }*/
}
