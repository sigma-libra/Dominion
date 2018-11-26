package server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.persistence.Persistence;
import server.persistence.impl.PersistenceImpl;
import shared.dto.GameInfoDTO;
import server.persistence.exception.PersistenceException;
import server.service.StatisticsService;
import server.service.exception.ServiceException;
import shared.dto.*;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final Persistence persistence;


    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public StatisticsServiceImpl() throws ServiceException {
        LOG.debug("Calling StatisticsService constructor");
        try {
            persistence = PersistenceImpl.getInstance();
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public StatisticsServiceImpl(Persistence persistence) throws ServiceException {
        LOG.debug("Calling StatisticsService constructor");
        this.persistence = Objects.requireNonNull(persistence);


    }

    @Override
    public StatisticsDTO getStatistics(int id) throws ServiceException {

        LOG.debug("method getStatistics called");

        try {
            ArrayList<GameInfoDTO> allGames = persistence.getAllGameInfo();
            ArrayList<GameInfoDTO> gamesPlayedByUser = gamesPlayedByUser(id, allGames);
            UserDTO currentUser = persistence.getUserById(id);
            Set<UserDTO> allUsers = persistence.getAllUsers();

            UserStatisticsDTO us = getUserStatistics(currentUser, gamesPlayedByUser);
            ServerStatisticsDTO ss = getServerStatistics(allUsers, allGames);
            ArrayList<RankingTableRowDTO> rt = getRankingTable(createUserGamesPairs(allUsers, allGames));

            return new StatisticsDTO(us, ss, rt);

        }
        catch (PersistenceException e) {
            LOG.debug(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void addGameInfo(GameInfoDTO dto) throws ServiceException {

        LOG.debug("method addGameInfo called");
        try {
            persistence.addGameInfo(dto);
        }
        catch (PersistenceException e) {
            LOG.debug(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    private UserStatisticsDTO getUserStatistics(UserDTO currentUser, ArrayList<GameInfoDTO> gamesPlayedByUser) {

        LOG.debug("method getUserStatistics called");
        UserStatisticsDTO us = new UserStatisticsDTO();
        us.setUsername(currentUser.getUserName());
        us.setGamesPlayed(gamesPlayedByUser.size());
        us.setGamesWon(getWonGames(currentUser.getId(), gamesPlayedByUser).size());
        us.setRegistered(currentUser.getRegistrationDateLocalDate());
        us.setStreak(getStreak(currentUser.getId(), gamesPlayedByUser));
        us.setWinLossRatio(getWinLossRatio(currentUser.getId(), gamesPlayedByUser));
        us.setLineChartWinLossRatio(getLineChartWinLossUser(gamesPlayedByUser, currentUser.getId()));
        us.setLineChartGamesPlayed(getLineChartGamesPlayed(gamesPlayedByUser));

        return us;
    }

    private ServerStatisticsDTO getServerStatistics(Set<UserDTO> allUsers, ArrayList<GameInfoDTO> allGames) {

        LOG.debug("method getServerStatistics called");
        ServerStatisticsDTO ss  = new ServerStatisticsDTO();
        allGames.sort(Comparator.comparing(GameInfoDTO::getDateAsLocalDate));
        if(allGames.size() > 0) {
            ss.setFirstGamePlayed(allGames.get(0).getDateAsLocalDate());
            ss.setLastGamePlayed(allGames.get(allGames.size() -1).getDateAsLocalDate());
        }
        ss.setServerVersion("0.0.1");
        ss.setTotalNumActive(getActiveUsers(allGames, LocalDate.now()).size());
        ss.setTotalNumGames(allGames.size());
        ss.setTotalNumUsers(allUsers.size());
        ss.setLineChartGamesProcessed(getLineChartGamesPlayed(allGames));
        ss.setLineChartActiveUsers(getLineChartActiveUsers(allGames));

        return ss;
    }

    private ArrayList<RankingTableRowDTO> getRankingTable(ArrayList<UserGamesPair> pairs) {

        LOG.debug("method getRankingTable called");
        ArrayList<RankingTableRowDTO> rt = new ArrayList<>();

        for(UserGamesPair p :
            pairs) {
            RankingTableRowDTO temp = new RankingTableRowDTO();
            temp.setUserName(p.getUser().getUserName());
            temp.setLastGame(getLastGameDate(p.getGames()));
            temp.setRegisteredSince(p.getUser().getRegistrationDateLocalDate());
            temp.setGamesTotal(p.getGames().size());
            temp.setGamesWon(getWonGames(p.getUser().getId(), p.getGames()).size());
            temp.setActive(isUserActive(p.getGames(), LocalDate.now()));

            rt.add(temp);
        }
        return rt;
    }

    private ArrayList<LineChartPairDTO> getLineChartWinLossUser(ArrayList<GameInfoDTO> games, int uid) {
        LOG.debug("method getLineChartWinLossUser called");
        ArrayList<LineChartPairDTO> chart = new ArrayList<>();
        if (games.size() == 0) return chart;
        ArrayList<GameInfoDTO> gamesToInclude = new ArrayList<>();
        games.sort(Comparator.comparing(GameInfoDTO::getDate));
        long dateLastInserted = games.get(0).getDate() + 1;
        double currentWinLossRatio = 0;
        for (GameInfoDTO g:
            games) {

            //fill in days where no change to the number of played games was made
            while (dateLastInserted + 1 < g.getDate()) {
                chart.add(new LineChartPairDTO(++dateLastInserted, currentWinLossRatio));
            }
            //substitute values for day if more than 1 game per day happened
            if (dateLastInserted == g.getDate()) {
                chart.remove(chart.size() - 1);
            }

            gamesToInclude.add(g);
            chart.add(new LineChartPairDTO(g.getDate(), currentWinLossRatio = getWinLossRatio(uid, gamesToInclude)));
            dateLastInserted = g.getDate();
        }
        return chart;
            }

    private ArrayList<LineChartPairDTO> getLineChartGamesPlayed(ArrayList<GameInfoDTO> games) {
        LOG.debug("method getLineChartGamesPlayed called");
        ArrayList<LineChartPairDTO> chart = new ArrayList<>();
        if (games.size() == 0) return chart;
        games.sort(Comparator.comparing(GameInfoDTO::getDate));
        int playedGames = 0;
        long dateLastInserted = games.get(0).getDate() + 1;
        for (GameInfoDTO g:
            games) {
            //fill in days where no change to the number of played games was made
            while (dateLastInserted + 1 < g.getDate()) {
                chart.add(new LineChartPairDTO(++dateLastInserted, playedGames));
            }
            //substitute values for day if more than 1 game per day happened
            if (dateLastInserted == g.getDate()) {
                chart.remove(chart.size() - 1);
            }

            playedGames++;
            chart.add(new LineChartPairDTO(g.getDate(), playedGames));
            dateLastInserted = g.getDate();
        }
        return chart;
    }

    private ArrayList<LineChartPairDTO> getLineChartActiveUsers(ArrayList<GameInfoDTO> games) {
        LOG.debug("method getWonGames called");
        ArrayList<LineChartPairDTO> chart = new ArrayList<>();

        if (games.size() == 0) return chart;

        games.sort(Comparator.comparing(GameInfoDTO::getDateAsLocalDate));
        LocalDate reference = games.get(0).getDateAsLocalDate();
        LocalDate lastGamePlayed = games.get(games.size() -1).getDateAsLocalDate();

        while (reference.compareTo(lastGamePlayed) <= 0) {

            chart.add(new LineChartPairDTO(reference, getActiveUsers(games, reference).size()));
            reference = reference.plusDays(1);
        }

        return chart;
    }

    private ArrayList getWonGames(int uid, ArrayList<GameInfoDTO> playedGames) {

        LOG.debug("method getWonGames called");
        return playedGames.stream().filter(g -> g.getWinner() == uid).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     *
     * @param allPlayedGames all games played at the server
     * @return all users who played at least one game in the last 30 days
     */
    private Set<Integer> getActiveUsers(ArrayList<GameInfoDTO> allPlayedGames, LocalDate reference) {

        LOG.debug("method getActiveUsers called");
        ArrayList<GameInfoDTO> recentGames =
            allPlayedGames.stream().filter(g -> g.getDateAsLocalDate().compareTo(reference.minusDays(30)) > 0 && g.getDateAsLocalDate().compareTo(reference) <= 0)
                .collect(Collectors.toCollection(ArrayList::new));
        Set<Integer> activeUsers = new HashSet<>();
        for (
            GameInfoDTO g :
            recentGames
            ) {
            activeUsers.addAll(g.getPlayers());
        }
        return activeUsers;
    }

    private boolean isUserActive(ArrayList<GameInfoDTO> gamesPlayedByUser, LocalDate now) {

        LOG.debug("method isUserActive called");
        ArrayList<GameInfoDTO> recentGames =
            gamesPlayedByUser.stream().filter(g -> g.getDateAsLocalDate().compareTo(now.minusDays(30)) > 0 && g.getDateAsLocalDate().compareTo(now) <= 0)
                .collect(Collectors.toCollection(ArrayList::new));
        return recentGames.size() > 0;
    }

    private int getStreak(int uid, ArrayList<GameInfoDTO> gamesPlayedByUser) {

        LOG.debug("method getStreak called");
        gamesPlayedByUser.sort(Comparator.comparing(GameInfoDTO::getDateAsLocalDate));
        Collections.reverse(gamesPlayedByUser);
        int streakCounter = 0;
        for (
            GameInfoDTO g :
            gamesPlayedByUser
            ) {
            if (g.getWinner() == uid) {
                streakCounter++;
            }
            else break;
            }
        return streakCounter;
    }

    private double getWinLossRatio(int uid, ArrayList<GameInfoDTO> gamesPlayed) {

        LOG.debug("method getWinLossRatio called");
        double winCount = 0;
        int lossCount = 0;
        for (GameInfoDTO g:
            gamesPlayed) {
            if (g.getWinner() == uid) winCount++;
            else lossCount++;
        }

        return lossCount == 0 ? winCount : winCount/lossCount;
    }

    private LocalDate getLastGameDate(ArrayList<GameInfoDTO> playedGames) {
        LOG.debug("method getLastGameDate called");
        if (playedGames.size() > 0) {
            playedGames.sort(Comparator.comparing(GameInfoDTO::getDateAsLocalDate));
            return playedGames.get(playedGames.size() -1).getDateAsLocalDate();
        }
        else return LocalDate.ofEpochDay(0);

    }

    private ArrayList<GameInfoDTO> gamesPlayedByUser(int uid, ArrayList<GameInfoDTO> allGames) {
        LOG.debug("method gamesPlayedByUser called");
        return allGames.stream().
            filter(g -> g.getPlayers().contains(uid)).collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<UserGamesPair> createUserGamesPairs
        (Set<UserDTO> users, ArrayList<GameInfoDTO> games) {

        LOG.debug("method createUserGamesPairs called");
        ArrayList<UserGamesPair> pairs = new ArrayList<>();
        for (UserDTO u:
            users) {
            pairs.add(new UserGamesPair(u, gamesPlayedByUser(u.getId(), games)));
        }
        return pairs;
    }
    private class UserGamesPair {
        private UserDTO user = new UserDTO();
        private ArrayList<GameInfoDTO> games = new ArrayList<>();

        private UserGamesPair(UserDTO user, ArrayList<GameInfoDTO> games) {
            this.user = user;
            this.games = games;
        }

        private UserDTO getUser() {
            return user;
        }

        private ArrayList<GameInfoDTO> getGames() {
            return games;
        }
    }
}
