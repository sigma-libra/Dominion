package CardTests;

import shared.domain.cards.treasures.CopperCard;
import org.junit.Test;
import shared.domain.cards.Card;
import shared.domain.cards.treasures.SilverCard;
import shared.domain.exceptions.InvalidCardTypeID;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class CardTest {

    @Test
    public void testGetID() {
        Card card = new SilverCard();
        int id = card.getID();
        assertThat(id, is(12));
    }

    @Test
    public void testCardFromID() {
        Class<? extends Card> card_class = SilverCard.class;
        int id = 12;

        Card card = null;
        try {
            card = Card.fromID(id);
        } catch (InvalidCardTypeID invalidCardTypeID) {
            invalidCardTypeID.printStackTrace();
        }
        assertEquals(card_class, card.getClass());
    }

    @Test
    public void testCardEquality() {
        Card card1 = new SilverCard();
        Card card2 = new SilverCard();

        assertThat(card1, is(card2));
    }

    @Test
    public void testCardInequality() {
        Card card1 = new SilverCard();
        Card card2 = new CopperCard();

        assertThat(card1, is(not(card2)));
    }
}
