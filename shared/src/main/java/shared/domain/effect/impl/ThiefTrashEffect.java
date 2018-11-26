package shared.domain.effect.impl;

import shared.domain.cards.Card;
import shared.domain.cards.TreasureCard;
import shared.domain.effect.CardEffect;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;
import shared.util.LogUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Third effect called by thief: if there are treasure cards revealed in a victim's hand that player
 * doesn't want (see ThiefGainCardEffect) player can force victim to trash one instead
 *
 */
public class ThiefTrashEffect extends ChooseCardsEffect {

    protected List<Card> revealedCards;
    protected List<Card> treasureCards;
    protected Player victim;
    protected int victimIndex;

    //Constructor for JSON serialisation
    public ThiefTrashEffect(){}

    public ThiefTrashEffect(int victimIndex, List<Card> revealedCards, List<Card> treasureCards) {
        super(0, 1);
        this.revealedCards = revealedCards;
        this.treasureCards = treasureCards;
        this.victimIndex = victimIndex;
    }

    @Override
    public void updateChoices(GameState gameState, Player player){

        victim = gameState.getPlayers().get(victimIndex);

        setCardChoices(treasureCards);
    }

    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {

        if(arguments.length > 0) {  // card chosen to trash
            Card chosen = treasureCards.get(arguments[0]);
            revealedCards.remove(chosen);
            gameState.addToTrashPile(chosen);
            gameState.getPlayLog().add(victim.getUser().getUserName() + " was forced to trash \n " +
                LogUtil.cardNameWithArticle(chosen));
        }

        //discard all other cards
        for(Card c: revealedCards) {
            victim.discardCard(c);
            gameState.getPlayLog().add(victim.getUser().getUserName() + " was forced to discard \n " +
                LogUtil.cardNameWithArticle(c));
        }
    }
}
