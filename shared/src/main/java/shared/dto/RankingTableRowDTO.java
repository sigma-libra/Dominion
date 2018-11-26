package shared.dto;

import java.time.LocalDate;

public class RankingTableRowDTO {

    public RankingTableRowDTO() {
    }

    /**
     * RankingTable: Table Column for username
     */
    private String userName = "";

    /**
     * RankingTable: Table Column for no of games won
     */
    private int gamesWon = -1;

    /**
     * RankingTable: Table Column for number games total
     */
    private int gamesTotal = -1;

    /**
     * RankingTable: Table Column user is active(flag)
     */
    private boolean isActive = false;

    /**
     * RankingTable: Table Column for last game played
     */
    private long lastGame = 0;

    /**
     * RankingTable: Table Column for user registered since
     */
    private long registeredSince = 0;

    public String getUserName() {
        return userName;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public int getGamesTotal() {
        return gamesTotal;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDate getLastGameAsLocalDate() {
        return LocalDate.ofEpochDay(lastGame);
    }

    public LocalDate getRegisteredSinceAsLocalDate() {
        return LocalDate.ofEpochDay(registeredSince);
    }

    public long getLastGame() {
        return lastGame;
    }

    public long getRegisteredSince() {
        return registeredSince;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public void setGamesTotal(int gamesTotal) {
        this.gamesTotal = gamesTotal;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setLastGame(LocalDate lastGame) {
        this.lastGame = lastGame.toEpochDay();
    }

    public void setRegisteredSince(LocalDate registeredSince) {
        this.registeredSince = registeredSince.toEpochDay();

    }

    public void setLastGame(long lastGame) {
        this.lastGame = lastGame;
    }

    public void setRegisteredSince(long registeredSince) {
        this.registeredSince = registeredSince;
    }
}
