package shared.domain.cards.victories;

import shared.domain.cards.VictoryCard;

/**
 * class modeling a gardens card
 *
 * (note: technically a "pseudo-victory card" (or kingdom-victory hybrid), bringing 1 extra point for
 * every 10 cards at the end of the game, but grouped with the action cards)
 * -> implemented in player's "getVictoryPointsFrom" method along with the real victory cards
 */
public class Gardens extends VictoryCard {

    public Gardens() {
        super.price = 4;
    }

    /**
     * Function imitating a victory card by adding victory points, in this case 1 point for
     * ever 10 cards owned by player
     *
     * @param nbCards owned by player
     * @return nb points added by gardens card
     */
    public int getVictoryPoints(int nbCards) {
        return nbCards/10;
    }

    @Override
    public int getVictoryPoints() {
        return 0;
    }
}
