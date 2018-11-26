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
 * Second effect called by thief: reveal the top two cards of a victim, and if there are treasure cards,
 * the player can choose to gain one, or else call a trash effect
 */
public class ThiefGainCardEffect extends ChooseCardsEffect {

    protected int playerIndex;
    protected Player victim;
    protected List<Card> revealedCards;
    protected List<Card> treasureCards;

    //Constructor for JSON serialisation
    public ThiefGainCardEffect(){}

    public ThiefGainCardEffect(int playerIndex){
        super(0, 1);

        this.playerIndex = playerIndex;
    }

    @Override
    public void updateChoices(GameState gameState, Player player){

        victim = gameState.getPlayers().get(playerIndex);

        if(victim.getDeckSize() < 2) {
            victim.refreshDeck();
        }

        revealedCards = victim.getDeck().drawUpToNCards(2);
        for(Card c: revealedCards) {
            gameState.getPlayLog().add(victim.getUser().getUserName() + " revealed " + LogUtil.cardNameWithArticle(c));
        }

        treasureCards = revealedCards.stream().filter(c -> c instanceof TreasureCard).collect(Collectors.toList());
        setCardChoices(treasureCards);
    }

    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {
        if(arguments.length > 0) {   //a treasure card was chosen to keep
            Card chosen = treasureCards.get(arguments[0]);
            revealedCards.remove(chosen);
            player.getHand().add(chosen);
            gameState.getPlayLog().add(player.getUser().getUserName() + " stole " + LogUtil.cardNameWithArticle(chosen)
            + "\n from " + victim.getUser().getUserName());

        } else {
            if(!treasureCards.isEmpty()) {  //if there were cards to take, but player didn't, he can trash one instead
                CardEffect effect = new ThiefTrashEffect(playerIndex, revealedCards, treasureCards);
                gameState.addEffect(effect, player);
                return;
            }
        }

        //discard all other cards
        for(Card c: revealedCards) {
            victim.discardCard(c);
            gameState.getPlayLog().add(victim.getUser().getUserName() + " was forced to discard \n "
                + LogUtil.cardNameWithArticle(c));
        }
    }
}
