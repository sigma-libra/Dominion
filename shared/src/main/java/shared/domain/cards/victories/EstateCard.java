package shared.domain.cards.victories;


import shared.domain.cards.VictoryCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * Class modelling an Estate Card
 */
public class EstateCard extends VictoryCard {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public EstateCard() {
        super.price = 2;
    }

    @Override
    public int getVictoryPoints() {
        return 1;
    }
}
