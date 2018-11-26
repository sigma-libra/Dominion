package shared.domain.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.cards.treasures.CopperCard;
import shared.domain.cards.treasures.GoldCard;
import shared.domain.cards.treasures.SilverCard;
import shared.domain.cards.victories.CurseCard;
import shared.domain.cards.victories.DuchyCard;
import shared.domain.cards.victories.EstateCard;
import shared.domain.cards.victories.ProvinceCard;
import shared.domain.exceptions.InvalidCardTypeID;
import shared.domain.exceptions.InvalidIDException;
import shared.util.AvailableActionCardIds;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.*;

/**
 * class modeling game supply
 *
 * @author Alex
 */
public class Supply implements Serializable {

    /**
     * indices of piles:
     * 0 = province
     * 1 = duchy
     * 2 = estate
     * 3 = curse
     * 4 = gold
     * 5 = silver
     * 6 = copper
     * 7-16 = kingdom cards
     */

    private List<Stack<Card>> cardPiles;

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * ids from action cards
     */
    private int[] usedActionCards = new int[10];

    /**
     * List of all actionCards
     */
    private ArrayList<Integer> workingActionCardIds = new ArrayList<Integer>();

    /**
     * helping list
     */
    private ArrayList<Integer> randomList = new ArrayList<>();

    private boolean useRandomizedSetup = true;

    /**
     * number of actioncards which will be used to play with
     */
    private static int numberOfActionCardsPlayed = 10;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supply supply = (Supply) o;
        for (int i = 0; i < this.cardPiles.size(); i ++) {
            if (this.cardPiles.get(i).size() != supply.cardPiles.get(i).size()) return false;
            if (! this.cardPiles.get(i).peek().getClass().equals(supply.cardPiles.get(i).peek().getClass())) return false;
        }
        return useRandomizedSetup == supply.useRandomizedSetup &&
            Arrays.equals(usedActionCards, supply.usedActionCards) &&
            Objects.equals(workingActionCardIds, supply.workingActionCardIds) &&
            Objects.equals(randomList, supply.randomList);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(cardPiles, workingActionCardIds, randomList, useRandomizedSetup);
        result = 31 * result + Arrays.hashCode(usedActionCards);
        return result;
    }

    /**
     * initiates supply regarding the number of PLayers
     *
     * @param numPlayers number of players
     */





    public Supply(int numPlayers, int[] customActionCards) {


        if (customActionCards == null || customActionCards[0] < 300)
            workingActionCardIds = AvailableActionCardIds.get();
        else {
            for (int i=0;i<10;i++)
                workingActionCardIds.add(customActionCards[i]);
        }


        Stack<Card> provinceCards = new Stack<>();
        Stack<Card> duchyCards = new Stack<>();
        Stack<Card> estateCards = new Stack<>();
        Stack<Card> curseCards = new Stack<>();

        Stack<Card> goldCards = new Stack<>();
        Stack<Card> silverCards = new Stack<>();
        Stack<Card> copperCards = new Stack<>();

        /**
         * indices of piles:
         * 0 = province
         * 1 = duchy
         * 2 = estate
         * 3 = curse
         * 4 = gold
         * 5 = silver
         * 6 = copper
         * 7-16 = kingdom cards
         */
        Stack<Card>[] initialPiles = new Stack[17];

        //Silver cards: always start with 40
        for (int i = 0; i < 40; i++) {
            silverCards.push(new SilverCard());
        }
        initialPiles[5] = silverCards;

        //Gold cards: always start with 30
        for (int i = 0; i < 30; i++) {
            goldCards.push(new GoldCard());
        }
        initialPiles[4] = goldCards;

        //Action cards: always start with ten random chosen piles of ten cards
        Random random = new Random();
        randomList = new ArrayList<>(workingActionCardIds); //all action cards in list, one per stack
        int randomIndex = 0;
        for (int j = 0; j < 10; j++) {   //for each of the 10 stacks one unique type of card

            Stack<Card> actionCardPile = new Stack<>();


            randomIndex = random.nextInt(randomList.size());
            usedActionCards[j] = randomList.get(randomIndex);
            for (int i = 0; i < numberOfActionCardsPlayed; i++) {   //in each stack push 10 cards
                Card randomActionCard = null;
                try {

                    randomActionCard = Card.fromID(randomList.get(randomIndex));
                    actionCardPile.push(randomActionCard);


                } catch (InvalidCardTypeID invalidCardTypeID) {
                    actionCardPile.clear();
                    i = 0;
                    randomIndex = random.nextInt(randomList.size());
                }
            }

            initialPiles[j + 7] = actionCardPile;
            //without repetitions
            randomList.remove(randomIndex);
        }

        if (numPlayers < 3) {

            //8 victory cards
            for (int i = 0; i < 8; i++) {
                provinceCards.push(new ProvinceCard());
                duchyCards.push(new DuchyCard());
                estateCards.push(new EstateCard());
            }

            //10 curse cards
            for (int i = 0; i < 10; i++) {
                curseCards.push(new CurseCard());
            }

            //46 copper cards (60 - 2*7)
            for (int i = 0; i < 46; i++) {
                copperCards.push(new CopperCard());
            }

        } else if (numPlayers == 3) {

            //12 victory cards
            for (int i = 0; i < 12; i++) {
                provinceCards.push(new ProvinceCard());
                duchyCards.push(new DuchyCard());
                estateCards.push(new EstateCard());
            }

            //20 curse cards
            for (int i = 0; i < 20; i++) {
                curseCards.push(new CurseCard());
            }

            //39 copper cards (60 - 3*7)
            for (int i = 0; i < 39; i++) {
                copperCards.push(new CopperCard());
            }

        } else {

            //12 victory cards
            for (int i = 0; i < 12; i++) {
                provinceCards.push(new ProvinceCard());
                duchyCards.push(new DuchyCard());
                estateCards.push(new EstateCard());
            }

            //30 curse cards
            for (int i = 0; i < 30; i++) {
                curseCards.push(new CurseCard());
            }

            //32 copper cards (60 - 4*7)
            for (int i = 0; i < 32; i++) {
                copperCards.push(new CopperCard());
            }

        }

        initialPiles[0] = provinceCards;
        initialPiles[1] = duchyCards;
        initialPiles[2] = estateCards;
        initialPiles[3] = curseCards;
        initialPiles[6] = copperCards;

        this.cardPiles = new ArrayList<>(Arrays.asList(initialPiles));
    }

    /**
     * initiates cardpiles
     *
     * @param cardPiles
     */
    public Supply(List<Stack<Card>> cardPiles) {
        LOG.info("Supply");
        this.cardPiles = cardPiles;
    }


    /**
     * checks if any endcondition is satisfied
     *
     * @return true if there's any endcondition satisfied
     */
    public boolean isEndConditionSatisfied() {
        LOG.info("isEndConditionSatisfied");
        if (cardPiles.get(0).empty()) return true;
        int emptyPilesCount = 0;
        for (int i = 1; i <= 16; i++) {
            if (cardPiles.get(i).empty()) emptyPilesCount++;
        }
        return emptyPilesCount >= 3;
    }

    /**
     * retrieve a card from corresponding pile
     *
     * @param pileNo corresponding pile
     * @return Card from pile
     */
    public Card retrieveCard(int pileNo) {
        LOG.info("retrieveCard");
        return cardPiles.get(pileNo).empty() ? null : cardPiles.get(pileNo).pop();
    }

    /**
     * retrieve a card with a certain ID from the correct pile
     *
     * @param cardId the card's id
     * @return Card from pile
     */
    public Card retrieveCardById(int cardId) throws InvalidIDException {
        LOG.info("retrieveCardById");
        for (Stack<Card> pile : getPiles()) {
            if (pile.isEmpty())
                continue;

            Card card = pile.peek();
            if (card.getID() == cardId)
                return pile.pop();
        }

        throw new InvalidIDException(String.format("No card with id %d can be bought", cardId));
    }

    /**
     * retrieve a card from corresponding pile, but without removing the card from the pile
     *
     * @param pileNo corresponding pile
     * @return Card from pile
     */
    public Card getCard(int pileNo) {
        LOG.info("getCard");
        return cardPiles.get(pileNo).peek();
    }

    /**
     * checks if a card from a pile is buyable
     *
     * @param pileNo      pile
     * @param turnTracker checks if a purchase is possible
     * @return true if a card is buyable
     */
    public boolean isBuyable(int pileNo, TurnTracker turnTracker) {
        LOG.info("isBuyable");
        assert (pileNo >= 0 && pileNo <= 16);
        return turnTracker.getPhase() == TurnPhase.BUY_PHASE && !cardPiles.get(pileNo).isEmpty() &&
            cardPiles.get(pileNo).peek().getPrice() <= turnTracker.getCredit() && turnTracker.getBuysAvailable() > 0 && !isEndConditionSatisfied();
    }

    /**
     * get all piles from which cards can be bought
     *
     * @param turnTracker checks if a card of pile x is buyable
     * @return array of piles from which cards can be bought
     */
    public int[] buyablePilesIndices(TurnTracker turnTracker) {
        LOG.info("buyablePilesIndices");
        ArrayList<Integer> temp = new ArrayList<>();
        for (int pileNo = 0; pileNo < 17; pileNo++) {
            if (isBuyable(pileNo, turnTracker)) temp.add(pileNo);
        }
        return temp.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * get current pileSize from corresponding pile
     *
     * @param pileNo corresponding pile
     * @return pile size
     */
    public int pileSize(int pileNo) {
        LOG.info("pileSize");
        assert (pileNo >= 0 && pileNo <= 16);
        return cardPiles.get(pileNo).size();
    }

    /**
     * get a card's price from pile x
     *
     * @param pileNo corresponding pile to get the card's price from
     * @return price
     */
    public int getPrice(int pileNo) {
        LOG.info("getPrice");
        assert (pileNo >= 0 && pileNo <= 16);
        return cardPiles.get(pileNo).isEmpty() ? 0 : cardPiles.get(pileNo).peek().getPrice();
    }

    public List<Stack<Card>> getPiles() {
        LOG.info("getPiles");
        return cardPiles;
    }

    /**
     * get array of  used action card ids
     *
     * @return classic action card ids
     */
    public int[] getUsedActionCards() {

        return usedActionCards;


    }

    /**
     * set array of used action card ids
     *
     * @param classicActionCardIds classic action card ids to be set
     */
    public void setUsedActionCards(int[] classicActionCardIds) {
        this.usedActionCards = classicActionCardIds;
    }

}
