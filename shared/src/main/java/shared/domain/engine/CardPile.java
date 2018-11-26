package shared.domain.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.exceptions.EmptyCardDeckException;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.*;

/**
 * Class modelling a deck of cards
 */
public class CardPile implements Collection<Card>, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private List<Card> cards;

    public CardPile(){
        this.cards = new ArrayList<>();
    }

    public CardPile(List<Card> cards) {
        this.cards = cards;
    }

    /**
     * shuffles the deck
     */
    public void shuffle(){
        LOG.info("shuffle");
        if(cards.isEmpty())
            return;

        Collections.shuffle(cards);
    }

    /**
     * Empties the deck
     */
    public void clear(){
        LOG.info("clear");
        this.cards.clear();
    }

    /**
     * Checks if the deck has any cards
     *
     * @return true if no cards in deck, else false
     */
    public boolean isEmpty(){
        LOG.info("isEmpty");
        return this.cards.isEmpty();
    }

    /**
     * Gives the number of cards in a Deck
     * @return the number of cards
     */
    public int nbCards() {
        LOG.info("nbCards");
        return cards.size();
    }

    /**
     * Returns the cards at a certain position in the deck (0 being the top)
     * without removing it
     *
     * @param position
     * @return the cards at a certain position in the deck
     */
    public Card get(int position) throws IndexOutOfBoundsException {
        LOG.info("get");
        return cards.get(position);
    }

    /**
     * Returns the cards at the top of the deck
     * without removing it
     *
     * @return the cards at a certain position in the deck
     */
    public Card getTopCard() {
        LOG.info("getTopCard");
        return cards.get(0);
    }

    /**
     * Removes the top card from the pile and returns it
     *
     * @return
     */
    public Card removeAndReturnTopCard() {
        LOG.info("removeAndReturnTopCard");
        return cards.remove(0);
    }


    /**
     * Adds a cards to the top of the deck
     * @param card
     */
    @Override
    public boolean add(Card card) {
        LOG.info("add");
        cards.add(0, card);
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Card> cards){
        LOG.info("addAll");
        return this.cards.addAll(cards);
    }

    /**
     * putXOnTop - takes x cards and puts them in the order in which they are being passed on top of
     * the current cards deck <code>deck</code>
     *
     * @param putOnTop deck of cards to be put on top of the deck
     * @param x the number of cards to move
     *
     * @return returns the new version of <code>deck</code>
     */
    public void putXCardsOnTopOfDeck(CardPile putOnTop, int x) throws EmptyCardDeckException {
        LOG.info("putXCardsOnTopOfDeck");
        List<Card> cardsToMove = putOnTop.drawCards(x);
        cardsToMove.addAll(cards);
        setCards(cardsToMove);
    }

    /**
     *
     * putXOnTop - takes x cards and puts them in the order in which they are being passed on top of
     * the current cards deck <code>deck</code>
     *
     * @param putOnTop (ArrayList)
     * @param x
     * @return returns the new version of <code>deck</code>
     */
    public void putXCardsOnTopOfDeck(List<Card> putOnTop, Integer x) throws EmptyCardDeckException {
        LOG.info("putXCardsOnTopOfDeck");
        putXCardsOnTopOfDeck(new CardPile(putOnTop), x);
    }

    /**
     * putXOnBottom - takes x cards and puts them in the order in which they are being passed on bottom of
     * the current cards deck <code>deck</code>
     *
     * @param putOnBottom deck of cards to be put on bottom of the deck
     * @param x the number of cards to move
     *
     * @return returns the new version of <code>deck</code>
     */
    public void putXCardsOnBottomOfDeck(CardPile putOnBottom, Integer x) throws EmptyCardDeckException {
        LOG.info("putXCardsOnBottomOfDeck");
        List<Card> cardsToMove = putOnBottom.drawCards(x);
        cards.addAll(cardsToMove);
    }

    /**
     * putXSomewhere - takes x cards and puts them in random order in random places  of the current cards
     * deck <code>deck</code>
     *
     * @param x the number of cards to move
     *
     * @param putSomewhere deck of cards to be shuffeled into the <code>deck</code>
     *
     * @return returns the new version of <code>deck</code>
     */
    public void putXCardsSomewhereInDeck(CardPile putSomewhere, Integer x) throws EmptyCardDeckException {
        LOG.info("putXCardsSomewhereInDeck");
        List<Card> cardsToMove = putSomewhere.drawCards(x);
        cards.addAll(cardsToMove);
        shuffle();
    }

    /**
     * removeAll - takes x cards and removes them from the deck, if in the deck one of those cards
     * occurs more than once the first occurence gets removed
     *
     *
     * @param removeFromDeck list of cards to be removed from the deck
     *
     * @return returns the new version of <code>deck</code>
     */
    @Override
    public boolean removeAll(Collection<?> removeFromDeck) {
        LOG.info("removeAll");
        return cards.removeAll(removeFromDeck);
    }

    /**
     * removes the card at the given index from the pile
     * @param index the index of the card to remove
     * @return the removed card
     * @throws IndexOutOfBoundsException
     */
    public Card remove(int index) throws IndexOutOfBoundsException {
        LOG.info("remove");
        return cards.remove(index);
    }

    @Override
    public boolean remove(Object o){
        LOG.info("remove");
        return cards.remove(o);
    }


    /**
     * Iterator to look through the cards deck
     * @return an iterator
     */
    public Iterator<Card> iterator() {
        LOG.info("iterator");
        return cards.iterator();
    }

    /**
     * Gets the cards in the deck as a list
     *
     * @return the cards in the deck as a list
     */
    public List<Card> getCards(){
        LOG.info("getCards");
        return cards;
    }

    /**
     * Replaces the cards in the deck with new ones
     * @param cards
     */
    public void setCards(Iterable<Card> cards) {
        LOG.info("setCards");
        clear();
        for (Card card : cards){
            this.cards.add(card);
        }
    }

    /**
     * Picks up a cards from a position of the deck (and removes it)
     *
     * @return the cards at given position of the deck
     * @throws EmptyCardDeckException
     */
    public Card drawCardAt(int position) throws EmptyCardDeckException {
        LOG.info("drawCardAt");
        if (cards.isEmpty() || cards.size() <= position) {
            throw new EmptyCardDeckException("No cards left here to drawCards in this deck");
        }
        Card card = cards.get(position);
        cards.remove(position);
        return card;
    }


    /**
     * Picks up a cards from the top of the deck (and removes it)
     *
     * @return the top cards of the deck
     * @throws EmptyCardDeckException
     */
    public Card drawCard() throws EmptyCardDeckException {
        LOG.info("drawCard");
        return drawCardAt(0);
    }

    /**
     * Draws the number first cards of the deck
     *
     * @param number
     * @return the number first cards of the deck
     */
    public List<Card> drawCards(int number) throws EmptyCardDeckException{
        LOG.info("drawCards");
        if(this.nbCards() < number) {
            throw new EmptyCardDeckException("Not enough cards left to drawCards");
        }
        List<Card> drawn_cards = new ArrayList<>();

        for(int i = 0; i < number; i++) {
            drawn_cards.add(drawCard());
        }
        return drawn_cards;
    }

    public List<Card> drawUpToNCards(int n){
        List<Card> drawn_cards = new ArrayList<>();

        for(int i = 0; i < n; i++) {
            try {
                drawn_cards.add(drawCard());
            } catch (EmptyCardDeckException e){
                break;
            }
        }
        return drawn_cards;
    }

    /**
     * Check whether a card is in this pile
     * @param card the card to search for
     * @return true if the card can be found in this pile
     */
    @Override
    public boolean contains(Object card) {
        LOG.info("contains");
        return cards.contains(card);
    }

    @Override
    public boolean containsAll(Collection<?> c){
        LOG.info("containsAll");
        return cards.containsAll(c);
    }

    /**
     * Return the number of cards on this pile
     * @return the size of the pile
     */
    public int size() {
        LOG.info("size");
        return cards.size();
    }

    @Override
    public Card[] toArray() {
        LOG.info("toArray");
        return cards.toArray(new Card[0]);
    }

    @Override
    public <T> T[] toArray(T[] arr){
        LOG.info("toArray");
        return cards.toArray(arr);
    }

    @Override
    public boolean retainAll(Collection<?> c){
        LOG.info("retainAll");
        int i = 0;
        while (i < size()){
            Card card = get(i);
            if (c.contains(card))
                i++;
            else
                remove(i);
        }
        return true;
    }

    /**
     * Get a cardPile of all the cards of a given type in the pile
     *
     * @param cardType
     * @param <C> the type (extention of Card)
     * @return a new CardPile with just the cards matching the cardType
     */
    public <C extends Card> CardPile subSetOfType(Class<C> cardType) {
        LOG.info("subSetOfType");
        List<Card> subsetOfType = new ArrayList<>();
        for(Card c: cards) {
            if(cardType.isInstance(c)) {
                subsetOfType.add(c);
            }
        }
        return new CardPile(subsetOfType);

    }

    /**
     * Checks if a pile contains a card of a specific type
     * @param cardType
     * @param <C>
     * @return
     */
    public <C extends Card> boolean containsInstance(Class<C> cardType) {
        for(Card cardInPile: cards) {
            if(cardType.isInstance(cardInPile)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardPile cards1 = (CardPile) o;
        if (this.cards.size() != cards1.getCards().size()) return false;
        for (int i = 0; i < this.cards.size(); i++) {
            if (!this.cards.get(i).getClass().equals(cards1.getCards().get(i).getClass())) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cards);
    }
}
