package LogUtilTests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import shared.domain.cards.Card;
import shared.domain.cards.kingdoms.Mine;
import shared.domain.cards.treasures.CopperCard;
import shared.domain.cards.treasures.GoldCard;
import shared.domain.cards.treasures.SilverCard;
import shared.domain.cards.victories.EstateCard;
import shared.domain.engine.Player;
import shared.domain.exceptions.InvalidIDException;
import shared.util.LogUtil;

public class PlayLogUtilTest {

    private Player player;

    @Before
    public void createPlayerWithHand() {
        this.player = new Player();
        player.getHand().clear();
        player.getHand().add(new Mine());
        player.getHand().add(new CopperCard());
        player.getHand().add(new SilverCard());
        player.getHand().add(new EstateCard());
    }

    @Test
    public void listHandCardsWorks() {

        int[] indices = {0, 3};

        try {
            String listedCards = LogUtil.listHandCards(player, indices);
            Assert.assertEquals(" 1 Estate,\n 1 Mine", listedCards);
        } catch (InvalidIDException e) {
            Assert.fail("Did not expect this exception to be thrown: " + e.getMessage());
        }
    }

    @Test
    public void tooLargeIndexInListCardsThrowsException() {

        int[] indices = {2, 4};

        String listedCards;

        try {
            listedCards = LogUtil.listHandCards(player, indices);
        } catch (InvalidIDException e) {
            Assert.assertEquals("#4 is not a valid card id; this player is only holding 4 cards", e.getMessage());
            return;
        }

        Assert.fail("Did not expect call to listHandCards to succeed with index 4/ "+player.getHandSize() +" and list: " + listedCards);
    }

    @Test
    public void listHandCardsWorksOnMultipleCards() {
        player.getHand().add(new GoldCard());
        player.getHand().add(new GoldCard());

        int[] indices = {0, 1, 3};

        try {
            String listedCards = LogUtil.listHandCards(player, indices);
            Assert.assertEquals(" 2 Golds,\n 1 Silver", listedCards);
        } catch (InvalidIDException e) {
            Assert.fail("Did not expect exception with message: " + e.getMessage());
        }
    }


    @Test
    public void cardWithArticleReturnsAnForVowel() {
        Card vowelCard = new EstateCard();
        String result = LogUtil.cardNameWithArticle(vowelCard);
        Assert.assertEquals("an Estate", result);
    }

    @Test
    public void cardWithArticleReturnsAForNonVowel() {
        Card vowelCard = new Mine();
        String result = LogUtil.cardNameWithArticle(vowelCard);
        Assert.assertEquals("a Mine", result);
    }

    @After
    public void removeHand() {
        player.getHand().clear();
        player = null;
    }
}
