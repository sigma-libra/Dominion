package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.cards.treasures.CopperCard;
import shared.domain.cards.treasures.GoldCard;
import shared.domain.cards.treasures.SilverCard;
import shared.domain.effect.NoPlayerChoosesEffect;
import shared.domain.engine.CardPile;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.EmptyCardDeckException;
import shared.domain.exceptions.GameException;
import shared.util.LogUtil;

import java.lang.invoke.MethodHandles;
import java.util.Stack;

/**
 * Reveal cards from the deck until it reveals 2 Treasure cards.
 * Puts those Treasure cards into hand deck and discards the other revealed cards.
 */
public class DiscardUntilTwoTreasureCards extends NoPlayerChoosesEffect {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public DiscardUntilTwoTreasureCards() {
        LOG.info("DiscardUntilTwoTreasureCards");
    }

    @Override
    public void execute(GameState gameState, Player player) throws GameException {
        LOG.info("execute - DiscardUntilTwoTreasureCards");
        int treasureCards = 0;
        CardPile deck = player.getDeck();

        try {
            while (treasureCards < 2) {
                Card card = deck.drawCard();

                String reveal = player.getUser().getUserName() + " reveals "
                    + LogUtil.cardNameWithArticle(card);
                gameState.getPlayLog().add(reveal);

                if (card.equals(new CopperCard()) || card.equals(new SilverCard()) || card.equals(new GoldCard())) {
                    player.getHand().add(card);
                    treasureCards++;
                } else {
                    player.discardCard(card);
                    gameState.getPlayLog().add(player.getUser().getUserName()
                        + " discarded "+ LogUtil.cardNameWithArticle(card));
                }
            }
        } catch (EmptyCardDeckException e) {
            // No more cards in deck
        }
    }
}
