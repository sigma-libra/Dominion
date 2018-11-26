package shared.domain.cards;

import shared.domain.effect.CardEffect;
import shared.domain.engine.GameState;

/**
 * class modeling a playableCard
 */
public abstract class PlayableCard extends Card {

    /**
     * Card Effects
     */
    private CardEffect[] effects;

    /**
     * applies the card's effects
     * @param gameState current/changing gamestate
     */
    public void applyEffects(GameState gameState) {
        gameState.addEffects(effects);
    }

    /**
     *
     * @param effects to be set
     */
    public void setEffects(CardEffect[] effects) {
        this.effects = effects;
    }
}
