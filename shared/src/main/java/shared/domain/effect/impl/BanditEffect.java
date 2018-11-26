package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.cards.kingdoms.Moat;
import shared.domain.cards.treasures.GoldCard;
import shared.domain.cards.treasures.SilverCard;
import shared.domain.effect.NoPlayerChoosesEffect;
import shared.domain.effect.PlayerChoosesEffect;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;
import shared.util.LogUtil;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * Class performing the unique effects of the bandit card:
 *
 * All the other players
 * 1) Show the top two cards of their deck
 * 2) If one of the revealed cards is a silver of gold, trash it
 * 3) Discard the rest
 *
 */
public class BanditEffect extends NoPlayerChoosesEffect {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void execute(GameState gameState, Player player) throws GameException {
        LOG.info("execute - BanditEffect");

        List<Player> players = gameState.getPlayers();
        for (Player p : players) {
            if (!p.equals(player)) {

                //Note: if the player has a Moat card in Hand, they are protected from the attack
                if(p.getHand().containsInstance(Moat.class)) {
                    gameState.getPlayLog().add(p.getUser().getUserName() + " is protected by a Moat card");
                }else {
                    //refresh deck
                    if (p.getDeckSize() < 2) {
                        p.refreshDeck();
                    }

                    //Reveal top two cards of deck
                    Card top1 = p.getDeck().removeAndReturnTopCard();
                    Card top2 = p.getDeck().removeAndReturnTopCard();

                    String reveal = p.getUser().getUserName() + " reveals "
                        + LogUtil.cardNameWithArticle(top1) +
                        "\n and " + LogUtil.cardNameWithArticle(top2);

                    gameState.getPlayLog().add(reveal);

                    //Check the types
                    boolean top1Removable = (top1 instanceof SilverCard || top1 instanceof GoldCard);
                    boolean top2Removable = (top2 instanceof SilverCard || top2 instanceof GoldCard);

                    if (top1Removable && top2Removable) {    //if both are possible, make player choose
                        List<Card> topCards = new ArrayList<>();
                        topCards.add(top1);
                        topCards.add(top2);
                        PlayerChoosesEffect effect = new TrashOrDiscardEffect(topCards);
                        gameState.addEffect(effect, p);
                    } else if (top1Removable) { //if only one possible, trash it directly
                        gameState.addToTrashPile(top1);
                        p.getDiscard().add(top2);
                        gameState.getPlayLog().add(p.getUser().getUserName()
                            + " trashed " + LogUtil.cardNameWithArticle(top1));
                        gameState.getPlayLog().add(p.getUser().getUserName()
                            + " discarded " + LogUtil.cardNameWithArticle(top2));

                    } else if (top2Removable) {
                        gameState.addToTrashPile(top2);
                        p.getDiscard().add(top1);
                        gameState.getPlayLog().add(p.getUser().getUserName()
                            + " trashed " + LogUtil.cardNameWithArticle(top2));
                        gameState.getPlayLog().add(p.getUser().getUserName()
                            + " discarded "+ LogUtil.cardNameWithArticle(top1));

                    } else { //if neither possible, discard both
                        p.getDiscard().add(top1);
                        p.getDiscard().add(top2);
                        gameState.getPlayLog().add(p.getUser().getUserName()
                            + " discarded "+LogUtil.cardNameWithArticle(top1));
                        gameState.getPlayLog().add(p.getUser().getUserName()
                            + " discarded "+ LogUtil.cardNameWithArticle(top2));
                    }
                }
            }
        }
    }
}
