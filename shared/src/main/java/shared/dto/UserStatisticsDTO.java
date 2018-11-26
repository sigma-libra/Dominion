package shared.dto;

import java.time.LocalDate;
import java.util.ArrayList;

public class UserStatisticsDTO {


    private String username = "";

    /**
     * user is registered since, as epoch days
     */
    private long registered = 0;

    /**
     * games a user played in total, e.g. 7
     */
    private int gamesPlayed = 0;

    /**
     * how many games a user won, e.g. 3
     */
    private int gamesWon = 0;

    /**
     * a user's current win/lose ratio, e.g. 0.11
     */
    private double winLossRatio = 0;

    /**
     * a user's current streak, e.g +3 or -4
     */
    private int streak = 0;

    private ArrayList<LineChartPairDTO> lineChartWinLossRatio = new ArrayList<>();

    private ArrayList<LineChartPairDTO> lineChartGamesPlayed = new ArrayList<>();

    public UserStatisticsDTO() {
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRegistered(LocalDate registered) {
        this.registered = registered.toEpochDay();
    }

    public void setRegistered(long registered) {
        this.registered = registered;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public void setWinLossRatio(double winLossRatio) {
        this.winLossRatio = winLossRatio;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public void setLineChartWinLossRatio(ArrayList<LineChartPairDTO> winLossOverTime) {
        this.lineChartWinLossRatio = winLossOverTime;
    }

    public void setLineChartGamesPlayed(ArrayList<LineChartPairDTO> lineChartGamesPlayed) {
        this.lineChartGamesPlayed = lineChartGamesPlayed;
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getRegistrationDateAsLocalDate() {
        return LocalDate.ofEpochDay(registered);
    }

    public long getRegistered() {
        return registered;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public double getWinLossRatio() {
        return winLossRatio;
    }

    public int getStreak() {
        return streak;
    }

    public ArrayList<LineChartPairDTO> getLineChartWinLossRatio() {
        return lineChartWinLossRatio;
    }

    public ArrayList<LineChartPairDTO> getLineChartGamesPlayed() {
        return lineChartGamesPlayed;
    }
}
