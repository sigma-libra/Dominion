package shared.util;

import shared.domain.cards.Card;
import shared.domain.engine.Player;
import shared.domain.exceptions.InvalidIDException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class LogUtil {

    private static final Character[] vowelArray = {'A', 'E', 'O', 'U', 'I'};
    private static final List<Character> vowels = Arrays.asList(vowelArray);

    /**
     * Creates a string listing some cards, e.g. "3 Cellars, 2 Moats"
     *
     * @param player
     * @param indices
     * @return
     * @throws InvalidIDException
     */
    public static String listHandCards(Player player, int[] indices) throws InvalidIDException {
        LinkedHashMap<String,Integer> counts = new LinkedHashMap<>();
        for (int i=0; i<indices.length; i++){
            int index = indices[i];

            Card card;
            try {
                card = player.getHand().get(index);
            } catch (IndexOutOfBoundsException e){
                throw new InvalidIDException(String.format("#%d is not a valid card id; this player is only holding %d cards", index, player.getHand().size()));
            }
            counts.put(card.getName(), counts.getOrDefault(card.getName(), 0) + 1);
        }

        StringBuilder builder = new StringBuilder();
        for (String cardName : counts.keySet()){
            int count = counts.get(cardName);
            builder.append(String.format(",\n %d %s%s", count, cardName, count==1?"":"s"));
        }

        return builder.toString().substring(2);
    }

    /**
     * Parses the article to a card's name, depending on whether it start with a vowel or not,
     * and returns the name with its article at the front
     *
     * @param card
     * @return
     */
    public static String cardNameWithArticle(Card card) {
        String cardName = card.getName();
        Character firstChar = Character.toUpperCase(cardName.charAt(0));

        if(vowels.contains(firstChar.charValue())) {
            return "an " + cardName;
        }
        else{
            return "a " + cardName;
        }

    }

}
