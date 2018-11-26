package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.cards.VictoryCard;
import shared.domain.effect.PlayerChoosesEffect;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RevealCardsEffect extends PlayerChoosesEffect {

    private HashMap<Player, List<Card>> map;

    public RevealCardsEffect(HashMap<Player, List<Card>> map) {
        this.map = map;
    }

    @Override
    public void execute(GameState gameState, Player player, int[] arguments) throws GameException {
        // Nothing to execute
    }

    public HashMap<Player, List<Card>> getMap(){
        return map;
    }
}
