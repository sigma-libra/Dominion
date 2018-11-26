package shared.domain.cards.victories;

import shared.domain.cards.VictoryCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * class modeling a cursecard
 */
public class CurseCard extends VictoryCard {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public CurseCard() {
        super.price = 0;
    }

    @Override
    public int getVictoryPoints() {
        return -1;
    }
}
