package shared.domain.cards.victories;

import shared.domain.cards.VictoryCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * class modeling a duchycard
 */
public class DuchyCard extends VictoryCard {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public DuchyCard() {
        super.price = 5;
    }

    @Override
    public int getVictoryPoints() {
        return 3;
    }
}
