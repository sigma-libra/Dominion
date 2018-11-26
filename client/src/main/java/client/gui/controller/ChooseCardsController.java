package client.gui.controller;

import client.gui.SoundEffects;
import client.service.ClientService;
import client.service.exception.ServiceException;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.springframework.stereotype.Controller;
import shared.domain.effect.cardaction.impl.PutCardsInDiscardPileAction;
import shared.domain.effect.cardaction.impl.PutCardsOnDeckAction;
import shared.domain.effect.cardaction.impl.TrashCardsAction;
import shared.domain.effect.impl.*;

import java.util.*;


/**
 * Controller that lets the player select cards for a SelectCardsEffect.
 */
@Controller
public class ChooseCardsController extends JavaFXControllerBase {

    private static Effect unselectedEffect = new ColorAdjust(0, -0.5, -0.3, 0);
    private static Effect selectedEffect = null;

    private ClientService clientService;
    private SoundEffects soundEffects;

    private VBox parent;
    private Label effectDescriptionLabel;
    private FlowPane cardsPane;
    private Button okButton;

    private ChooseCardsEffect effect;

    private Set<CardController> selectedCards = new HashSet<>();

    /**
     * Constructor
     *
     * @param clientService
     * @param soundEffects
     */
    public ChooseCardsController(ClientService clientService, SoundEffects soundEffects){
        super("Card selection");
        this.clientService = Objects.requireNonNull(clientService);
        this.soundEffects = Objects.requireNonNull(soundEffects);
    }

    @Override
    protected Parent createScene(){
        parent = new VBox();
        parent.setSpacing(15);
        parent.setAlignment(Pos.CENTER);
        parent.setStyle("-fx-background-color: #EED4B9;");

        effectDescriptionLabel = new Label();
        effectDescriptionLabel.setFont(new Font(18));

        cardsPane = new FlowPane();
        cardsPane.setVgap(8);
        cardsPane.setHgap(4);

        okButton = new Button("Accept");
        okButton.setOnAction(e -> acceptCards());

        parent.getChildren().addAll(effectDescriptionLabel, cardsPane, okButton);
        return parent;
    }

    @Override
    protected void initScene(){
        cardsPane.getChildren().clear();
        soundEffects.playOpenChooseSound();
        if(effect.getChoices().size() == 0 ||
            (effect instanceof DiscardCardsByNbEmptyPileEffect && effect.getUpTo() == 0)) {
            effectDescriptionLabel.setText("Sorry, nothing to choose: just press accept");
        } else {
            effectDescriptionLabel.setText(effectToString(effect));

            for (int cardID : effect.getChoices()) {
                CardController cardController = makeCardController(cardID);
                cardsPane.getChildren().add(cardController);
            }
        }
    }

    /**
     * run a card's effect
     *
     * @param effect
     */
    public void run(ChooseCardsEffect effect){
        this.effect = effect;

        super.run();
    }

    /**
     * Creates a new CardController that selects/deselects a card when clicked
     * @param cardID The card to display on the CardController
     * @return A suitable new CardController
     */
    private CardController makeCardController(int cardID){
        CardController cardController = new CardController(cardID);
        cardController.setEffect(unselectedEffect);
        cardController.setOnClick(() -> onCardClicked(cardController));
        return cardController;
    }

    /**
     * Callback function for when a CardController is clicked.
     * Selects or deselects the clicked card.
     *
     * @param cardController The CardController that was clicked
     */
    private void onCardClicked(CardController cardController){
        if (selectedCards.contains(cardController)) {
            selectedCards.remove(cardController);
            cardController.setEffect(unselectedEffect);
        } else {
            selectedCards.add(cardController);
            cardController.setEffect(selectedEffect);
        }
    }

    /**
     * Called when the player clicks the Ok-button.
     * Checks if the current selection is valid, and sends a finishInteractiveEffect command to the server.
     */
    private void acceptCards(){
        List<Integer> cards = new ArrayList<>();
        int i = 0;
        for (Node cardController : cardsPane.getChildren()){
            if (selectedCards.contains(cardController)) {
                cards.add(i);
            }
            i+=1;
        }


        if (cards.size() < effect.getFrom() && effect.getFrom() > 0 && cards.size() < effect.getChoices().size()){
            LOG.error("Too few cards selected: " + cards.size());
            showWarning(String.format("You must select at least %d cards", effect.getFrom()), "Not enough cards selected");
            return;
        }
        if (cards.size() > effect.getUpTo() && effect.getUpTo() >= 0){
            LOG.error("Too many cards selected: " + cards.size());
            showWarning(String.format("You must select at most %d cards", effect.getUpTo()), "Too many cards selected");
            return;
        }

        try {
            clientService.finishInteractiveEffect(cards);
        } catch (ServiceException e){
            LOG.error(String.format("Error sending chosen cards to server: %s", e.getMessage()));
            showError(e, "Error sending chosen cards to server");
            return;
        }
        soundEffects.playOpenChooseSound();
        closeWindow();
    }

    /**
     * Creates a useful description for a ChooseCardsEffect.
     *
     * @param effect The effect to describe
     * @return human-readable instructions for the effect
     */
    private static String effectToString(ChooseCardsEffect effect){
        StringBuilder builder = new StringBuilder();

        int from = effect.getFrom();
        int upTo = effect.getUpTo();

        if(effect instanceof SentryReturnToDeckEffect) {
            return "Select which card should go on deck first";
        }

        builder.append("Choose ");
        if (from <= 0 && upTo <= 0) {
            builder.append("any number of cards");
        } else if (from == upTo) {
            if (from == 1) {
                builder.append("1 card");
            } else {
                builder.append(from);
                builder.append(" cards");
            }
        } else if (from <= 0) {
            builder.append(String.format("up to %s cards", upTo));
        } else if (upTo < 0) {
            builder.append(String.format("at least %s cards", from));
        } else {
            builder.append(from);
            builder.append(" to ");
            builder.append(upTo);
            builder.append(" cards");
        }

        if (effect instanceof TrashTargetCardsEffect ||
            effect instanceof SentryTrashEffect ||
            effect instanceof TrashOrDiscardEffect ||
            effect instanceof ThiefTrashEffect ||
            (effect instanceof ChooseCardsEffect && effect.getAction() instanceof TrashCardsAction)) {
            builder.append(" to trash");
        }

        else if(effect instanceof DiscardCardsByNbEmptyPileEffect ||
                effect instanceof DiscardThenDrawEffect ||
                effect instanceof SentryDiscardEffect ||
            (effect instanceof ChooseCardsEffect && effect.getAction() instanceof PutCardsInDiscardPileAction)) {
            builder.append(" to discard");
        }

        else if(effect instanceof GainTargetCardToHandEffect ||
                effect instanceof LibraryEffect ||
                effect instanceof ThiefGainCardEffect) {
            builder.append(" to gain to hand");
        }
        else if(effect instanceof VassalEffect) {
            builder.append(" to play");
        }
        else if (effect instanceof PutTargetCardFromHandOnTopOfDeckEffect ||
            (effect instanceof ChooseCardsEffect && effect.getAction() instanceof PutCardsOnDeckAction)) {
            builder.append(" to put on top of the deck");

        } else if(effect instanceof PutTypeCardfromHandToDeckEffect) {
            builder.append(" to reveal and deck");
        }

        return builder.toString();
    }
}
