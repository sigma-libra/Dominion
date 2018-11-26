package shared.domain.effect.impl;

import shared.domain.effect.CardEffect;
import shared.domain.effect.NoPlayerChoosesEffect;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

public class EachOtherPlayerEffect extends NoPlayerChoosesEffect {
    CardEffect effect;

    public EachOtherPlayerEffect(CardEffect effect){
        this.effect = effect;
    }

    @Override
    public void execute(GameState gameState, Player player) throws GameException {
        for (Player p : gameState.getPlayers()){
            if (p != player) {
                gameState.addEffect(effect, p);
            }
        }
    }
}
