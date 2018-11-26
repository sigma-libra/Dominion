package CardTests;

import org.junit.Test;
import shared.domain.cards.Card;
import shared.domain.cards.PlayableCard;
import shared.domain.cards.kingdoms.Cellar;
import shared.domain.effect.impl.AddActionsEffect;
import shared.domain.effect.impl.DiscardThenDrawEffect;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class PlayableCardTest {

    private Player makePlayer(Class<? extends Card>... handCards){
        Player player = new Player(new UserDTO("TEST_PLAYER"));

        List<Card> hand = new ArrayList<>();
        for (int i=0; i<handCards.length; i++)
            hand.add(Card.fromClass(handCards[i]));
        player.setHand(hand);

        return player;
    }

    private GameState makeGameState(Player... players){
        List<Player> playerList = new ArrayList<>();
        for (int i=0; i<players.length; i++)
            playerList.add(players[i]);

        GameState gameState = new GameState(playerList);
        return gameState;
    }

    @Test
    public void applyEffects_ShouldAddEffects(){
        GameState gameState = makeGameState(makePlayer());
        PlayableCard card = new Cellar();

        card.applyEffects(gameState);
        assertNotNull(gameState.getPendingEffect());
    }

    @Test
    public void applyEffects_ShouldAddEffectsInCorrectOrder(){
        GameState gameState = makeGameState(makePlayer());
        Player player = gameState.getCurrentPlayer();
        PlayableCard card = new Cellar();

        card.applyEffects(gameState);
        assertThat(gameState.getPendingEffects(player).pop(), instanceOf(AddActionsEffect.class));
        assertThat(gameState.getPendingEffects(player).pop(), instanceOf(DiscardThenDrawEffect.class));
    }
}
