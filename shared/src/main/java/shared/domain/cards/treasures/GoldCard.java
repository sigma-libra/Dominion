package shared.domain.cards.treasures;

import shared.domain.cards.TreasureCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * class modeling a goldcard
 */
public class GoldCard extends TreasureCard {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public GoldCard() {
        super.price = 6;
    }

    @Override
    public int getCoinValue() {
        return 3;
    }
}
