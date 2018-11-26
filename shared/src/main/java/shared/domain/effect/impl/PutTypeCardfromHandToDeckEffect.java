package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;
import shared.util.LogUtil;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PutTypeCardfromHandToDeckEffect<C extends Card> extends ChooseCardsEffect{

    private Class<C> type;

    private List<Card> typeCards = new ArrayList<>();

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    //empty constructor required for Jackson deserialization
    public PutTypeCardfromHandToDeckEffect(){
        LOG.info("PutTypeCardfromHandToDeckEffect");
    }

    public PutTypeCardfromHandToDeckEffect(int nbCards, Class<C> type) {
        super(nbCards, nbCards);
        this.type = type;
        this.typeCards = new ArrayList<>();
        LOG.info("PutTypeCardfromHandToDeckEffect");
    }


    @Override
    public void updateChoices(GameState gameState, Player player){
        typeCards.clear();
        LOG.info("updateChoices");
        for(Card c: player.getHand().getCards()) {
            if(type.isInstance(c)) {
                typeCards.add(c);
            }
        }
        setCardChoices(typeCards);
    }

    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {
        LOG.info("errorchecked_execute - PutTypeCardfromHandToDeckEffect");
        for(int i = 0; i < arguments.length; i++) {
            Card c = typeCards.get(arguments[0]);
            player.getDeck().add(c);
            player.getHand().remove(c);
            String message = player.getUser().getUserName() + " moves to deck " + LogUtil.cardNameWithArticle(c);
            gameState.getPlayLog().add(message);
        }


    }
}
