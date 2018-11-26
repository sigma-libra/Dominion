package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.cards.kingdoms.Moat;
import shared.domain.effect.NoPlayerChoosesEffect;
import shared.domain.effect.PlayerChoosesEffect;
import shared.domain.effect.cardaction.impl.PutCardsInDiscardPileAction;
import shared.domain.effect.cardsource.impl.HandCardsSource;
import shared.domain.engine.CardPile;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.engine.Supply;
import shared.domain.exceptions.GameException;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class MilitiaEffect extends NoPlayerChoosesEffect {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void execute(GameState gameState, Player player) throws GameException {
        LOG.info("execute - MilitiaEffect");

        List<Player> players = gameState.getPlayers();
        for (Player p : players) {
            //Note: if the player has a Moat card in Hand, they are protected from the attack
            if (!p.equals(player)) {

                if(p.getHand().containsInstance(Moat.class)) {
                    gameState.getPlayLog().add(p.getUser().getUserName() + " is protected by a Moat card");
                } else {
                    //strip hand down to three cards
                    int nbDiscard = p.getHandSize() - 3;
                    if(nbDiscard > 0) {
                        LOG.info("Discard + " + nbDiscard + " cards");
                        PlayerChoosesEffect effect = new ChooseCardsEffect(new HandCardsSource(), new PutCardsInDiscardPileAction(), nbDiscard, nbDiscard);
                        gameState.addEffect(effect, p);
                    }
                }
            }
        }
    }
}
