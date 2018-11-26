package shared.domain.effect.impl;

import shared.domain.cards.Card;
import shared.domain.cards.KingdomCard;
import shared.domain.cards.PlayableCard;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;
import shared.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Effect for special vassal card: discards top of deck and gives player option to play it if it's a kingdom
 */
public class VassalEffect extends ChooseCardsEffect{

    private Card topCard;

    public VassalEffect() {
        this.setFrom(0);
        this.setUpTo(1);
    }

    @Override
    public void updateChoices(GameState gameState, Player player){

        if(player.getDeck().isEmpty()) {
            player.refreshDeck();
        }

        topCard = player.getDeck().getTopCard();
        player.getDeck().remove(topCard);

        if(topCard instanceof KingdomCard) {
            List<Card> topCardList = new ArrayList<>();
            topCardList.add(topCard);
            setCardChoices(topCardList);
        }
    }

    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {
        if(arguments.length == 1) {
            ((PlayableCard)topCard).applyEffects(gameState);
            player.getCardsPlayedThisTurn().add(topCard);
            gameState.getPlayLog().add(player.getUser().getUserName() + " played " + LogUtil.cardNameWithArticle(topCard));
        } else {
            player.getDiscard().add(topCard);
        }
    }
}
