package client.gui.controller;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.springframework.stereotype.Controller;
import shared.domain.cards.Card;
import shared.domain.effect.impl.RevealCardsEffect;
import shared.domain.engine.Player;

/**
 * Controller to reveal cards from other players
 */
@Controller
public class RevealCardsController extends JavaFXControllerBase {
    protected RevealCardsEffect effect;

    protected HBox playersHBox;

    public RevealCardsController(){
        super("Cards revealed");
    }

    @Override
    protected Parent createScene(){
        VBox parent = new VBox();
        parent.setSpacing(15);
        parent.setAlignment(Pos.CENTER);
        parent.setStyle("-fx-background-color: #EED4B9;");

        playersHBox = new HBox();
        playersHBox.setSpacing(25);

        Button okButton = new Button("Ok");
        okButton.setOnAction(e -> closeWindow());

        parent.getChildren().addAll(playersHBox, okButton);
        return parent;
    }

    @Override
    protected void initScene(){
        playersHBox.getChildren().clear();

        for (Player player : effect.getMap().keySet()){
            VBox vbox = new VBox();
            vbox.setSpacing(15);

            Label label = new Label(String.format("\n%s revealed these cards:", player.getUser().getUserName()));
            label.setFont(new Font(18));

            FlowPane flowPane = new FlowPane();
            for (Card card : effect.getMap().get(player))
                flowPane.getChildren().add(new CardController(card));

            vbox.getChildren().addAll(label, flowPane);
            playersHBox.getChildren().add(vbox);
        }
    }

    public void run(RevealCardsEffect effect){
        this.effect = effect;
        super.run();
    }
}
