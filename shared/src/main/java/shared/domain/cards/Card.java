package shared.domain.cards;

import shared.domain.cards.kingdoms.*;
import shared.domain.cards.treasures.CopperCard;
import shared.domain.cards.treasures.GoldCard;
import shared.domain.cards.treasures.SilverCard;
import shared.domain.cards.victories.*;
import shared.domain.exceptions.InvalidCardTypeID;
import shared.util.BiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Class modelling a Card
 */
public abstract class Card implements Serializable {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * price
     */
    protected Integer price;

    /**
     *
     * @return price (value)
     */
    public Integer getPrice() {
        return price;
    }

    /**
     * map to handle cards/ids
     */
    private static final BiMap<Class<? extends Card>, Integer> ids = new BiMap<>() {{
        put(CopperCard.class, 11);
        put(SilverCard.class, 12);
        put(GoldCard.class, 13);
        put(EstateCard.class, 21);
        put(DuchyCard.class, 22);
        put(ProvinceCard.class, 23);
        put(CurseCard.class, 24);
        put(Artisan.class, 301);
        put(Bandit.class, 302);
        put(Bureaucrat.class, 303);
        put(Cellar.class, 304);
        put(Chapel.class, 305);
        put(Council_Room.class, 306);
        put(Festival.class, 307);
        put(Gardens.class, 308);
        put(Harbinger.class, 309);
        put(Laboratory.class, 310);
        put(Library.class, 311);
        put(Market.class, 312);
        put(Merchant.class, 313);
        put(Militia.class, 314);
        put(Mine.class, 315);
        put(Moat.class, 316);
        put(Moneylender.class, 317);
        put(Poacher.class, 318);
        put(Remodel.class, 319);
        put(Sentry.class, 320);
        put(Smithy.class, 321);
        put(Throne_Room.class, 322);
        put(Vassal.class, 323);
        put(Village.class, 324);
        put(Witch.class, 325);
        put(Workshop.class, 326);
        put(Adventurer.class, 327);
        put(Chancellor.class, 328);
        put(Thief.class, 329);
        put(Woodcutter.class, 330);
    }};

    /**
     * get ID by card/type/class
     * @return id
     */
    public int getID(){
        return ids.get(this.getClass());
    }

    public static <T extends Card> int getID(Class<T> cardType){
        try {
            return ids.get(cardType);
        } catch(NullPointerException e){
            // this should never happen
            LOG.error(String.format("Card %s has no ID", cardType.getName()));
            throw new AssertionError(e);
        }
    }

    /**
     * get class/type by card
     * @param id card id
     * @return card's id
     * @throws InvalidCardTypeID
     */
    public static Class<? extends Card> classFromID(int id) throws InvalidCardTypeID {
        Class<? extends Card> type = ids.getKey(id);
        if (type == null)
            throw new InvalidCardTypeID(id);

        return type;
    }

    /**
     * get card by id
     * @param id
     * @return a new card instance by id
     * @throws InvalidCardTypeID
     */
    public static Card fromID(int id) throws InvalidCardTypeID {
        Class<? extends Card> type = classFromID(id);
        return fromClass(type);
    }

    /**
     * instantiate a new card based on its class
     * @param type
     * @return a new card instance by id
     * @throws InvalidCardTypeID
     */
    public static Card fromClass(Class<? extends Card> type) {
        try {
            Constructor<? extends Card> constructor = type.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e){
            // this should never happen
            LOG.error(String.format("Failed to instantiate Card of type %s: %s", type.getName(), e.getMessage()));
            throw new AssertionError(e);
        }
    }

    /**
     * Returns the card's name
     * Useful for playLog, which needs to print the names of played cards
     *
     * @return the card name
     */
    public String getName() {
        String cardName = this.getClass().getSimpleName();

        //Note: treasure and victory cards contain the word "Card" in their class name, which has to be removed
        int suffix = cardName.indexOf("Card");
        if (suffix >= 0)
            cardName = cardName.substring(0, suffix);

        cardName = cardName.replace('_', ' ');

        return cardName;
    }

    /**
     * equals overrde
     * @param that Object X
     * @return this == that
     */
    @Override
    public boolean equals(Object that) {
        return (that != null) && (that instanceof Card) && (this.getID() == ((Card)that).getID());
    }
}
