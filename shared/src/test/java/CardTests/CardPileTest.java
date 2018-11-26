package CardTests;

import shared.domain.cards.Card;
import shared.domain.engine.CardPile;
import shared.domain.cards.kingdoms.Cellar;
import shared.domain.cards.treasures.CopperCard;
import shared.domain.cards.treasures.GoldCard;
import shared.domain.cards.treasures.SilverCard;
import shared.domain.cards.victories.EstateCard;
import shared.domain.exceptions.EmptyCardDeckException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CardPileTest {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private CardPile deck1;
    private CardPile deck2;
    private CardPile deck3;

    Card card1;
    Card card2;
    Card card3;
    Card card4;
    Card card5;
    Card card6;

    List<Card> list1;
    List<Card> list2;

    @Before
    public void createDecks() {
        LOG.debug("Creating test decks");
        deck1 = new CardPile();
        deck2 = new CardPile();
        deck3 = new CardPile();

        card1 = new CopperCard();
        card2 = new SilverCard();
        card3 = new GoldCard();
        card4 = new EstateCard();
        card5 = new Cellar();
        card6 = new EstateCard();

        list1 = new ArrayList<>();
        list1.add(card1);
        list1.add(card2);
        list1.add(card3);

        deck1.setCards(list1);

        list2 = new ArrayList<>();
        list2.add(card4);
        list2.add(card5);

        deck2.setCards(list2);
    }

    @Test
    public void createPileFromList_ShouldContainCardsInCorrectOrder(){
        List<Card> cards = new ArrayList<>();
        cards.add(new CopperCard());
        cards.add(new SilverCard());

        CardPile pile = new CardPile(cards);

        assertThat(pile.getCards(), is(cards));
    }

//    /**
//     * Checks that shuffle works well enough to prevent the same cards from being at the top
//     * twice in a row
//     */
//    @Test
//    public void shuffleWorks() {
//        Card topCardBefore = deck1.getTopCard();
//        deck1.shuffle();
//        Card topCardAfter1 = deck1.getTopCard();
//        deck1.shuffle();
//        Card topCardAfter2 = deck1.getTopCard();
//        deck1.shuffle();
//        Card topCardAfter3 = deck1.getTopCard();
//
//        assertFalse(topCardBefore.equals(topCardAfter1)
//            && topCardAfter1.equals(topCardAfter2)
//            && topCardBefore.equals(topCardAfter2)
//            && topCardAfter3.equals(topCardAfter1)
//            && topCardAfter3.equals(topCardAfter2)
//            && topCardAfter3.equals(topCardBefore));
//    }

    /**
     * Tests that adding a cards puts it at the top of the deck
     */
    @Test
    public void addCardWorks() {
        deck1.add(card6);
        try {
            assertThat(deck1.drawCard(), is(card6));
        } catch (EmptyCardDeckException emptyCardDeckException) {
            Assert.fail("This deck should not be empty.");
        }
    }

    /**
     *
     */
    @Test
    public void putXCardsOnTopWorks() {
        try {
            deck1.putXCardsOnTopOfDeck(deck2, 2);
        } catch (EmptyCardDeckException e) {
            Assert.fail("This deck should not be empty");
        }


        try {
            Card top = deck1.drawCard();
            Card second = deck1.drawCard();
            assertThat(top, is(card4));
            assertThat(second, is(card5));
        } catch (EmptyCardDeckException e) {
            Assert.fail("This deck should not be empty");
        }
    }

    @Test
    public void putXCardsOnBottomWorks() {
        try {
            deck1.putXCardsOnBottomOfDeck(deck2, 2);
        } catch (EmptyCardDeckException e) {
            Assert.fail("This deck should not be empty");
        }


        try {
            Card top = deck1.drawCard();
            Card bottom = deck1.drawCardAt(deck1.nbCards()-1);
            assertThat(top, is(card1));
            assertThat(bottom, is(card5));
        } catch (EmptyCardDeckException e) {
            Assert.fail("This deck should not be empty");
        }
    }

    @Test
    public void putXCardsSomewhereWorks() {
        try {
            deck1.putXCardsSomewhereInDeck(deck2, 2);
        } catch (EmptyCardDeckException e) {
            Assert.fail("This deck should not be empty");
        }

        assertTrue(deck1.getCards().contains(card4)
                && deck1.getCards().contains(card5));
    }

    @Test(expected = EmptyCardDeckException.class)
    public void emptyDeckThrowsException() throws EmptyCardDeckException {
        deck3.drawCard();
    }

}
