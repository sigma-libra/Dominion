package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.cards.kingdoms.Moat;
import shared.domain.effect.NoPlayerChoosesEffect;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.engine.Supply;
import shared.domain.exceptions.GameException;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class AddCursesToEveryoneExceptCurrentPlayerEffect extends NoPlayerChoosesEffect {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void execute(GameState gameState, Player player) throws GameException {
        LOG.info("execute - AddCursesToEveryoneExceptCurrentPlayerEffect");
        List<Player> players = gameState.getPlayers();
        Supply supply = gameState.getSupply();
        for (Player p : players) {
            //Note: if the player has a Moat card in Hand, they are protected from the attack
            if (p.equals(player) ) {
                continue;
            } else {


                //Note: if the player has a Moat card in Hand, they are protected from the attack
                if(p.getHand().containsInstance(Moat.class)) {
                    gameState.getPlayLog().add(p.getUser().getUserName() + " is protected by a Moat card");
                }else {
                    // curse on supply nr 3
                    Card curse = supply.retrieveCard(3);
                    p.discardCard(curse);
                    gameState.getPlayLog().add(p.getUser().getUserName() + " got a curse card!");
                }
            }
        }
    }
}
