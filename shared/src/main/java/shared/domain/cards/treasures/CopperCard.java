package shared.domain.cards.treasures;


import shared.domain.cards.TreasureCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * Class modelling a Copper Card
 */
public class CopperCard extends TreasureCard {

    /**
     * Logger
     */

    public CopperCard() {
        super.price = 0;
    }

    public int getCoinValue(){
        return 1;
    }

}
