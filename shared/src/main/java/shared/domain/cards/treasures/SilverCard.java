package shared.domain.cards.treasures;


import shared.domain.cards.Card;
import shared.domain.cards.TreasureCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.kingdoms.Merchant;
import shared.domain.engine.CardPile;
import shared.domain.engine.GameState;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * Class modelling a Silver Card
 */
public class SilverCard extends TreasureCard {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public SilverCard() {
        super.price = 3;
    }

    @Override
    public int getCoinValue(){
        return 2;
    }

    /**
     * Override needed in case of Merchant card, which gives first silver card played an extra
     * credit of 1
     * @param gameState
     */
    @Override
    public void applyEffects(GameState gameState) {
        gameState.getTurnTracker().addCredit(getCoinValue());

        //Check if merchant card was played and if this is the first silver card it encountered
        CardPile cardsPlayedBefore = gameState.getCurrentPlayer().getCardsPlayedThisTurn();
        int nbMerchantCards = 0;
        for(Card playedCard: cardsPlayedBefore) {
            if(Merchant.class.isInstance(playedCard) && !((Merchant) playedCard).isAlreadyAddedCredit()) {
                gameState.getTurnTracker().addCredit(1);
                ((Merchant) playedCard).setAlreadyAddedCredit(true);
            }
        }
    }
}

