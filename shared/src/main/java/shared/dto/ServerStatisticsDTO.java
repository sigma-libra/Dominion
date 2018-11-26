package shared.dto;

import java.time.LocalDate;
import java.util.ArrayList;

public class ServerStatisticsDTO {

    /**
     * when was the first game processed by the server,
     * e.g. "04.01.2017"
     */
    private long firstGamePlayed = 0;

    /**
     * how many user's are registered on this server
     * e.g. 100
     */
    private int totalNumUsers = 0;

    /**
     * how many user's are really active
     * e.g. 70
     */
    private int totalNumActive = 0;

    /**
     * total num of games processed by the server
     */
    private int totalNumGames = 0;

    /**
     * what's the current server version
     */
    private String serverVersion = "";

    /**
     * when was the last game processed by the server
     */
    private long lastGamePlayed = 0;

    /**
     * represents how many games have been processed over time
     */
    private ArrayList<LineChartPairDTO> lineChartGamesProcessed = new ArrayList<>();

    /**
     * represents user activity over time
     */
     private ArrayList<LineChartPairDTO> lineChartActiveUsers = new ArrayList<>();

    public ServerStatisticsDTO() {
    }

    public void setFirstGamePlayed(LocalDate firstGamePlayed) {
        this.firstGamePlayed = firstGamePlayed.toEpochDay();
    }

    public void setTotalNumUsers(int totalNumUsers) {
        this.totalNumUsers = totalNumUsers;
    }

    public void setTotalNumActive(int totalNumActive) {
        this.totalNumActive = totalNumActive;
    }

    public void setTotalNumGames(int totalNumGames) {
        this.totalNumGames = totalNumGames;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public void setLastGamePlayed(LocalDate lastGamePlayed) {
        this.lastGamePlayed = lastGamePlayed.toEpochDay();
    }

    public void setFirstGamePlayed(long firstGamePlayed) {
        this.firstGamePlayed = firstGamePlayed;
    }

    public void setLastGamePlayed(long lastGamePlayed) {
        this.lastGamePlayed = lastGamePlayed;
    }

    public void setLineChartGamesProcessed(ArrayList<LineChartPairDTO> lineChartGamesProcessed) {
        this.lineChartGamesProcessed = lineChartGamesProcessed;
    }

    public void setLineChartActiveUsers(ArrayList<LineChartPairDTO> lineChartActiveUsers) {
        this.lineChartActiveUsers = lineChartActiveUsers;
    }

    public LocalDate getFirstGamePlayedAsLocalDate() {
        return LocalDate.ofEpochDay(firstGamePlayed);
    }

    public int getTotalNumUsers() {
        return totalNumUsers;
    }

    public int getTotalNumActive() {
        return totalNumActive;
    }

    public int getTotalNumGames() {
        return totalNumGames;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public LocalDate getLastGamePlayedAsLocalDate() {
        return LocalDate.ofEpochDay(lastGamePlayed);
    }

    public long getFirstGamePlayed() {
        return firstGamePlayed;
    }

    public long getLastGamePlayed() {
        return lastGamePlayed;
    }

    public ArrayList<LineChartPairDTO> getLineChartGamesProcessed() {
        return lineChartGamesProcessed;
    }

    public ArrayList<LineChartPairDTO> getLineChartActiveUsers() {
        return lineChartActiveUsers;
    }
}
