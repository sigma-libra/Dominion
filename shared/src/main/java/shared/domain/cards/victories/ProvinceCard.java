package shared.domain.cards.victories;

import shared.domain.cards.VictoryCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * class modeling a provicecard
 */
public class ProvinceCard extends VictoryCard {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public ProvinceCard() {
        super.price = 8;
    }

    @Override
    public int getVictoryPoints() {
        return 6;
    }
}
