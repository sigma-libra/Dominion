package client.gui.controller;

import client.gui.JavaFXImageCache;
import client.gui.SoundEffects;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import shared.domain.cards.Card;

import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;

/**
 * Created by Sahab on 8.12.2017.
 * <p>
 * Holds the deck representation and actions when it is clicked
 */
public class CardController extends ImageView {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    protected static String IMAGES_PATH;
    static {
        try {
            IMAGES_PATH = "file://" + CardController.class.getClassLoader().getResource("images").toURI().getPath();
        } catch (URISyntaxException e){
            LOG.error("Failed to determine path to card images");
        }
    }

    protected static final double WIDTH = 100;

    protected static final double HEIGHT = 160;

    protected static final double BIG_WIDTH = 300;

    protected static final double BIG_HEIGHT = 480;

    protected int cardId;

    private Runnable onClick;

    //Constructors------------------------------------------------------------------------------------------------------

    public CardController(int cardId){
        this(cardId, null);
    }

    public CardController(Card card){
        this(card.getID());
    }

    public CardController(int cardId, Runnable onClick) {
        this.setFitWidth(WIDTH);
        this.setFitHeight(HEIGHT);

        setCard(cardId);

        this.onClick = onClick;
        this.setOnMouseClicked(mouseEvent -> onMouseClick(mouseEvent));
    }


    /**
     * Sets the callback for the onClick event.
     * @param onClick The callback to call when the CardController is clicked (and not disabled)
     */
    public void setOnClick(Runnable onClick){
        this.onClick = onClick;
    }

    /**
     * Called whenever the CardController is clicked.
     * Calls the `onClick` callback function if appropriate.
     * Displays a larger preview of the card on right click.
     *
     * @param mouseEvent
     */
    private void onMouseClick(MouseEvent mouseEvent){
        LOG.info(cardId + " clicked");
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (!this.isDisable() && onClick != null) {
                onClick.run();
            }
        } else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
            //Allows right-click to show a big version of the card images
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.UNDECORATED);

            ImageView imageView = new ImageView();
            imageView.setImage(imageForCard(cardId));
            imageView.setFitHeight(BIG_HEIGHT);
            imageView.setFitWidth(BIG_WIDTH);
            BorderPane bigImagePane = new BorderPane();
            bigImagePane.setCenter(imageView);
            Scene sc = new Scene(bigImagePane, BIG_WIDTH, BIG_HEIGHT);
            popupStage.setScene(sc);

            popupStage.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    popupStage.close();
                }
            });
            popupStage.show();
        }
    }

    /**
     * Gets the card's id
     * @return
     */
    public int getCardId(){
        return cardId;
    }

    /**
     * Sets the card and its image
     * @param cardId
     */
    public void setCard(int cardId) {
        this.cardId = cardId;

        this.setImage(imageForCard(cardId));
    }

    /**
     * Loads the image for the given card
     * @param cardId The card whose image you want to load
     * @return The card image
     */
    protected static Image imageForCard(int cardId){
        String imgPath = String.format("%s/%d.jpg", IMAGES_PATH, cardId);
        return JavaFXImageCache.get(imgPath);
    }

    /**
     * Allows clicking on the card
     */
    public void enable() {
        this.setDisable(false);
        this.setEffect(null);
    }

    /**
     * Forbids clicking on the card
     */
    public void disable() {
        this.setDisable(true);

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-1.0);
        colorAdjust.setBrightness(-0.3);
        this.setEffect(colorAdjust);
    }

}
