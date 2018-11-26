package client.gui.adapter.table;

/**
 * Class whose sole purpose is to format data so a tableview can use it to
 * insert data into its columns, here the player stat table shown during a game
 */
public class PlayerStats {

    private String player;
    private Integer deck;
    private Integer points;

    /**
     * Constructor
     *
     * @param name
     * @param deckSize
     * @param points
     */
    public PlayerStats(String name, Integer deckSize, Integer points) {
        this.player = name;
        this.deck = deckSize;
        this.points = points;
    }

    public String getPlayer() {
        return player;
    }

    public Integer getDeck() {
        return deck;
    }

    public Integer getPoints() {
        return points;
    }



}
