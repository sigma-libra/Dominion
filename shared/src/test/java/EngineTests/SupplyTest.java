package EngineTests;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.cards.kingdoms.*;
import shared.domain.cards.treasures.CopperCard;
import shared.domain.cards.treasures.GoldCard;
import shared.domain.cards.treasures.SilverCard;
import shared.domain.cards.victories.*;
import shared.domain.engine.*;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Stack;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SupplyTest {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Stack<Card> cardStack0;
    private Stack<Card> cardStack1;
    private Stack<Card> cardStack2;
    private Stack<Card> cardStack3;
    private Stack<Card> cardStack4;
    private Stack<Card> cardStack5;
    private Stack<Card> cardStack6;
    private Stack<Card> cardStack7;
    private Stack<Card> cardStack8;
    private Stack<Card> cardStack9;
    private Stack<Card> cardStack10;
    private Stack<Card> cardStack11;
    private Stack<Card> cardStack12;
    private Stack<Card> cardStack13;
    private Stack<Card> cardStack14;
    private Stack<Card> cardStack15;
    private Stack<Card> cardStack16;
    private Stack<Card> cardStack17;

    // has some cards in every stack
    private ArrayList<Stack<Card>> cardPiles0;
    // has empty province - otherwise some in every stack
    private ArrayList<Stack<Card>> cardPiles1;
    // has empty stack in stack 10 (count starting from 0)
    private ArrayList<Stack<Card>> cardPiles2;

    private Supply supply0;
    private Supply supply1;
    private Supply supply2;

    private TurnTracker turnTracker;

    private Player player;

    @Before
    public void createSupply() {
        cardStack0 = new Stack<>();
        cardStack0.push(new ProvinceCard());
        cardStack0.push(new ProvinceCard());
        cardStack0.push(new ProvinceCard());
        cardStack1 = new Stack<>();
        cardStack1.push(new DuchyCard());
        cardStack1.push(new DuchyCard());
        cardStack1.push(new DuchyCard());
        cardStack2 = new Stack<>();
        cardStack2.push(new EstateCard());
        cardStack2.push(new EstateCard());
        cardStack2.push(new EstateCard());
        cardStack2.push(new EstateCard());
        cardStack2.push(new EstateCard());
        cardStack3 = new Stack<>();
        cardStack3.push(new CurseCard());
        cardStack3.push(new CurseCard());
        cardStack3.push(new CurseCard());
        cardStack4 = new Stack<>();
        cardStack4.push(new GoldCard());
        cardStack4.push(new GoldCard());
        cardStack4.push(new GoldCard());
        cardStack4.push(new GoldCard());
        cardStack4.push(new GoldCard());
        cardStack5 = new Stack<>();
        cardStack5.push(new SilverCard());
        cardStack5.push(new SilverCard());
        cardStack5.push(new SilverCard());
        cardStack5.push(new SilverCard());
        cardStack6 = new Stack<>();
        cardStack6.push(new CopperCard());
        cardStack6.push(new CopperCard());
        cardStack7 = new Stack<>();
        cardStack7 = new Stack<>();
        cardStack7.push(new Cellar());
        cardStack7.push(new Cellar());
        cardStack8 = new Stack<>();
        cardStack8.push(new Chapel());
        cardStack9 = new Stack<>();
        cardStack9.push(new Council_Room());
        cardStack9.push(new Council_Room());
        cardStack9.push(new Council_Room());
        cardStack10 = new Stack<>();
        cardStack10.push(new Feast());
        cardStack10.push(new Feast());
        cardStack10.push(new Feast());
        cardStack11 = new Stack<>();
        cardStack11.push(new Artisan());
        cardStack12 = new Stack<>();
        cardStack12.push(new Festival());
        cardStack12.push(new Festival());
        cardStack12.push(new Festival());
        cardStack13 = new Stack<>();
        cardStack13.push(new Harbinger());
        cardStack13.push(new Harbinger());
        cardStack14 = new Stack<>();
        cardStack14.push(new Laboratory());
        cardStack14.push(new Laboratory());
        cardStack15 = new Stack<>();
        cardStack15.push(new Gardens());
        cardStack15.push(new Gardens());
        cardStack15.push(new Gardens());
        cardStack15.push(new Gardens());
        cardStack15.push(new Gardens());
        cardStack16 = new Stack<>();
        cardStack16.push(new Merchant());
        cardStack16.push(new Merchant());
        cardStack16.push(new Merchant());
        cardStack16.push(new Merchant());
        cardStack17 = new Stack<>();

        cardPiles0 = new ArrayList<>();
        cardPiles0.add(cardStack0);
        cardPiles0.add(cardStack1);
        cardPiles0.add(cardStack2);
        cardPiles0.add(cardStack3);
        cardPiles0.add(cardStack4);
        cardPiles0.add(cardStack5);
        cardPiles0.add(cardStack6);
        cardPiles0.add(cardStack7);
        cardPiles0.add(cardStack8);
        cardPiles0.add(cardStack9);
        cardPiles0.add(cardStack10);
        cardPiles0.add(cardStack11);
        cardPiles0.add(cardStack12);
        cardPiles0.add(cardStack13);
        cardPiles0.add(cardStack14);
        cardPiles0.add(cardStack15);
        cardPiles0.add(cardStack16);

        cardPiles1 = new ArrayList<>();
        cardPiles1.add(cardStack17);
        cardPiles1.add(cardStack1);
        cardPiles1.add(cardStack2);
        cardPiles1.add(cardStack3);
        cardPiles1.add(cardStack4);
        cardPiles1.add(cardStack5);
        cardPiles1.add(cardStack6);
        cardPiles1.add(cardStack7);
        cardPiles1.add(cardStack8);
        cardPiles1.add(cardStack9);
        cardPiles1.add(cardStack10);
        cardPiles1.add(cardStack11);
        cardPiles1.add(cardStack12);
        cardPiles1.add(cardStack13);
        cardPiles1.add(cardStack14);
        cardPiles1.add(cardStack15);
        cardPiles1.add(cardStack16);

        cardPiles2 = new ArrayList<>();
        cardPiles2.add(cardStack0);
        cardPiles2.add(cardStack1);
        cardPiles2.add(cardStack2);
        cardPiles2.add(cardStack3);
        cardPiles2.add(cardStack4);
        cardPiles2.add(cardStack5);
        cardPiles2.add(cardStack6);
        cardPiles2.add(cardStack7);
        cardPiles2.add(cardStack8);
        cardPiles2.add(cardStack9);
        cardPiles2.add(cardStack17);
        cardPiles2.add(cardStack11);
        cardPiles2.add(cardStack12);
        cardPiles2.add(cardStack13);
        cardPiles2.add(cardStack14);
        cardPiles2.add(cardStack15);
        cardPiles2.add(cardStack16);

        supply0 = new Supply(cardPiles0);
        supply1 = new Supply(cardPiles1);
        supply2 = new Supply(cardPiles2);

        turnTracker = new TurnTracker();
        player = new Player();
    }

    @Test
    public void isEndConditionSatisfiedTest() {
        assertFalse(supply0.isEndConditionSatisfied());
        assertTrue(supply1.isEndConditionSatisfied());
        assertFalse(supply2.isEndConditionSatisfied());
        while (cardStack11.size() > 0) {
            cardStack11.pop();
        }
        assertFalse(supply2.isEndConditionSatisfied());
        while (cardStack12.size() > 0) {
            cardStack12.pop();
        }
        assertTrue(supply2.isEndConditionSatisfied());
    }

    @Test
    public void isBuyableTest() {
        assertFalse(supply1.isBuyable(3, turnTracker));
        assertThat(turnTracker.getPhase(), is(TurnPhase.ACTION_PHASE));
        turnTracker.nextPhase(player);
        assertTrue(supply1.isEndConditionSatisfied());
        assertThat(turnTracker.getPhase(), is(TurnPhase.BUY_PHASE));
        assertThat(turnTracker.getBuysAvailable(), is(1));
        assertNotNull(supply1);
        assertFalse(supply1.isBuyable(3, turnTracker));
        assertTrue(supply0.isBuyable(3, turnTracker));
        assertFalse(supply0.isBuyable(7, turnTracker));
        turnTracker.addCredit(1);
        assertFalse(supply0.isBuyable(7, turnTracker));
        turnTracker.addCredit(1);
        assertTrue(supply0.isBuyable(7, turnTracker));
    }

    @Test
    public void retrieveCardTest() {
        assertNull(supply1.retrieveCard(0));
        assertEquals(supply1.retrieveCard(1).getClass(), DuchyCard.class);
    }

    @Test
    public void buyablePilesIndicesTest() {
        assertThat(supply0.buyablePilesIndices(turnTracker).length, is(0));
        turnTracker.nextPhase(player);
        assertThat(supply0.buyablePilesIndices(turnTracker).length, is(2));
        turnTracker.addCredit(2);
        assertThat(supply0.buyablePilesIndices(turnTracker).length, is(5));
        assertThat(supply1.buyablePilesIndices(turnTracker).length, is(0));
    }

    @Test
    public void cardPileSizeTest() {
        assertThat(supply0.pileSize(9), is(3));
        assertThat(supply1.pileSize(0), is(0));
        assertThat(supply0.pileSize(12), is(3));
    }

    @Test
    public void getPriceTest() {
        assertThat(supply0.getPrice(0), is(8));
        assertThat(supply1.getPrice(0), is(0));
    }

    @Test
    public void getCardNameTest() {
        assertThat(supply0.retrieveCard(4).getName(), is("Gold"));
        assertThat(supply0.retrieveCard(8).getName(), is("Chapel"));
    }
}
