package shared.domain.effect.impl;

import shared.domain.cards.Card;
import shared.domain.cards.VictoryCard;
import shared.domain.cards.kingdoms.Moat;
import shared.domain.effect.NoPlayerChoosesEffect;
import shared.domain.effect.PlayerChoosesEffect;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;
import shared.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BureaucratEffect extends NoPlayerChoosesEffect {

    @Override
    public void execute(GameState gameState, Player player) throws GameException {
        HashMap<Player, List<Card>> map = new HashMap<>();
        for (Player p : gameState.getPlayers()) {
            if (!p.equals(player)) {
                if (p.getHand().containsInstance(Moat.class)) {
                    gameState.getPlayLog().add(p.getUser().getUserName() + " is protected by a Moat card");
                } else {
                    List<Card> hand = p.getHand().getCards();
                    List<Card> victoryCards = new ArrayList<>();
                    for(Card card: hand) {
                        if(card instanceof VictoryCard) {
                            victoryCards.add(card);
                        }
                    }

                    if(victoryCards.isEmpty()) {
                        String noCardMessage = p.getUser().getUserName() + " has no victory cards in hand";
                        gameState.getPlayLog().add(noCardMessage);
                    }
                    else if(victoryCards.size() == 1) {
                        String oneCard = p.getUser().getUserName() + " moved " +
                            LogUtil.cardNameWithArticle(victoryCards.get(0)) + " to deck";
                        gameState.getPlayLog().add(oneCard);
                        p.getHand().remove(victoryCards.get(0));
                        p.getDeck().add(victoryCards.get(0));
                    } else {
                        PlayerChoosesEffect effect = new PutTypeCardfromHandToDeckEffect<>(1, VictoryCard.class);
                        gameState.addEffect(effect, p);
                    }
                }
            }
        }

    }

}
