package shared.domain.effect.impl;

import shared.domain.cards.kingdoms.Moat;
import shared.domain.effect.CardEffect;
import shared.domain.effect.NoPlayerChoosesEffect;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;
import shared.util.LogUtil;

import java.util.List;

/**
 * Effect to initialize Thief actions: call thief effects on each unprotected player
 */
public class ThiefEffect extends NoPlayerChoosesEffect {
    @Override
    public void execute(GameState gameState, Player player) throws GameException {
        List<Player> players = gameState.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if (p.getUser().getId() != player.getUser().getId()) {
                if (p.getHand().containsInstance(Moat.class)) {
                    gameState.getPlayLog().add(p.getUser().getUserName() + " is protected by a Moat");
                } else {
                    CardEffect effect = new ThiefGainCardEffect(i);
                    gameState.addEffect(effect, player);
                }
            }
        }
    }
}
