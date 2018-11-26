package client.gui;

import client.gui.controller.CardController;
import javafx.scene.media.AudioClip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;

/**
 * A class calling all the possible sound effects
 */
@Controller
public class SoundEffects {

    protected static String SOUND_EFFECTS_PATH;
    static {
        SOUND_EFFECTS_PATH = CardController.class.getClassLoader().getResource("soundEffects").toString();
    }


    /**
     * when an error happens
     */
    public void playErrorSound() {
        AudioClip audioClip = new AudioClip(SOUND_EFFECTS_PATH + "/windows_error.mp3");
        audioClip.play();
    }

    /**
     * plays a coin sound when money is spent
     */
    public void playCoinSound() {
        AudioClip audioClip = new AudioClip(SOUND_EFFECTS_PATH + "/coin_sound.mp3");
        audioClip.play();
    }

    /**
     * when cards are shuffled and moved
     */
    public void playShuffleSound() {
        AudioClip audioClip = new AudioClip(SOUND_EFFECTS_PATH + "/shuffle_cards_sound.mp3");
        audioClip.play();
    }

    /**
     * when a card is gained
     */
    public void playGainSound() {
        AudioClip audioClip = new AudioClip(SOUND_EFFECTS_PATH + "/gain_sound.mp3");
        audioClip.play();
    }

    /**
     * when a choice window is opened
     */
    public void playOpenChooseSound() {
        AudioClip audioClip = new AudioClip(SOUND_EFFECTS_PATH + "/open_choose_sound.mp3");
        audioClip.play();
    }

    /**
     * when an action card is played
     */
    public void playActionPlaySound() {
        AudioClip audioClip = new AudioClip(SOUND_EFFECTS_PATH + "/play_card_sound.wav");
        audioClip.play();
    }

    /**
     * when the game starts
     */
    public void playStartSound() {
        AudioClip audioClip = new AudioClip(SOUND_EFFECTS_PATH + "/gong_start.aiff");
        audioClip.play();
    }

    /**
     * When the game ends
     */
    public void playEndSound() {
        AudioClip audioClip = new AudioClip(SOUND_EFFECTS_PATH + "/trumpets_end.wav");
        audioClip.play();
    }

    /**
     * When a player joins a game
     */
    public void playJoinSound() {
        AudioClip audioClip = new AudioClip(SOUND_EFFECTS_PATH + "/write_join.wav");
        audioClip.play();
    }
}
