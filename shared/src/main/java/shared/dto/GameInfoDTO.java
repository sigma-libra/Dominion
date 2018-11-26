package shared.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GameInfoDTO {

    //player ids
    private List<Integer> players = new ArrayList<>();
    private long date = 0;
    //player id
    private int winner = -1;

    public GameInfoDTO() {
    }

    public GameInfoDTO(List<Integer> players, long date, int winner) {
        this.players = players;
        this.date = date;
        this.winner = winner;
    }

    public GameInfoDTO(List<Integer> players, LocalDate date, int winner) {
        this.players = players;
        this.winner = winner;
        setDate(date);
    }

    public List<Integer> getPlayers() {
        return players;
    }

    public long getDate() {
        return date;
    }

    public LocalDate getDateAsLocalDate() {
        return LocalDate.ofEpochDay(date);
    }

    public int getWinner() {
        return winner;
    }

    public void setPlayers(List<Integer> players) {
        this.players = players;
    }

    public void addPlayer(int id) {
        players.add(id);
    }

    public void setDate(LocalDate date) {
        this.date = date.toEpochDay();
    }

    public void setDate(long date) {this.date = date;}

    public void setWinner(int winner) {
        this.winner = winner;
    }
}
