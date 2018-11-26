package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;
import shared.util.LogUtil;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TrashOrDiscardEffect extends ChooseCardsEffect {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private List<Card> cards;

    //empty constructor for JSON deserialisation
    public TrashOrDiscardEffect(){
    }

    public TrashOrDiscardEffect(List<Card> cards) {
        super(1,1);
        this.cards = new ArrayList<>(cards);
    }

    @Override
    public void updateChoices(GameState gameState, Player player){
        LOG.info("TrashOrDiscardEffect - updateChoices");
        List<Integer> choices = cards.stream().map(c -> c.getID()).collect(Collectors.toList());
        setChoices(choices);
    }

    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {
        LOG.info("execute - DiscardCardsByNbEmptyPileEffect");
        Arrays.sort(arguments);
        for(int i = arguments.length - 1; i >= 0; i--) {
            Card cardToTrash = cards.remove(i);
            gameState.addToTrashPile(cardToTrash);
            gameState.getPlayLog().add(player.getUser().getUserName() + " trashed " + LogUtil.cardNameWithArticle(cardToTrash));
        }

        for(int j = 0; j < cards.size(); j++) {
            Card cardToDiscard = cards.get(j);
            player.getDiscard().add(cardToDiscard);
            gameState.getPlayLog().add(player.getUser().getUserName() + " discarded  " + LogUtil.cardNameWithArticle(cardToDiscard));
        }
    }
}
