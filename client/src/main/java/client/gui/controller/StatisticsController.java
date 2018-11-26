package client.gui.controller;

import client.gui.SoundEffects;
import client.gui.adapter.ChartAdapter;
import client.service.ClientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import shared.dto.LineChartPairDTO;
import shared.dto.RankingTableRowDTO;
import shared.dto.StatisticsDTO;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * FXML Controller for Statistics Window
 */
@Controller
public class StatisticsController extends FXMLControllerBase implements Initializable {
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private final ClientService clientService;

    private final SoundEffects soundEffects;
    @FXML
    public LineChart linechart_personal_games_played_over_time;

    /**
     * ChartAdapter
     */
    private ChartAdapter chartAdapter;


    /**
     * username, e.g. "Joe"
     */
    @FXML
    public Label statistics_username;

    /**
     * user is registered since, e.g. "07.01.2018"
     */
    @FXML
    public Label statistics_registered;

    /**
     * games a user played in total, e.g. 7
     */
    @FXML
    public Label statistics_games_played;

    /**
     * how many games a user won, e.g. 3
     */
    @FXML
    public Label statistics_games_won;

    /**
     * a user's current win/lose ratio, e.g. 0.11
     */
    @FXML
    public Label statistics_current_w_l;

    /**
     * a user's current streak, e.g +3 or -4
     */
    @FXML
    public Label statistics_current_streak;

    /**
     * PieChart for representing all games a user played and wins
     */
    @FXML
    public PieChart pie_statistics_personal;

    /**
     * linechart to represent a the user's form and history
     */
    @FXML
    public LineChart linechart_statistics_personal_form;

    /**
     * a user's best Month, e.g. "01-2018"
     */
    @FXML
    public Label statistics_best_month;

    /**
     * a user's worst Month, e.g. "07-2017"
     */
    @FXML
    public Label statistics_worst_month;

    /**
     * representing the user's trend
     * e.g. in january the user has a w/l of 0.11 and
     * in february w/l of 0.27, the user's trend is positive -> "0.16"
     */
    @FXML
    public Label statistics_trend;




    // Server Statisic fields
    /**
     * when was the first game processed by the server,
     * e.g. "04.01.2017"
     */
    @FXML
    public Label statistics_first_game_played;

    /**
     * how many user's are registered on this server
     * e.g. 100
     */
    @FXML
    public Label statistics_total_num_users;

    /**
     * how many user's are really active
     * e.g. 70
     */
    @FXML
    public Label statistics_total_num_active;

    /**
     * total num of games processed by the server
     */
    @FXML
    public Label statistics_total_num_games;

    /**
     * what's the current server version
     */
    @FXML
    public Label statistics_server_version;

    /**
     * when was the last game processed by the server
     */
    @FXML
    public Label statistics_last_game_played;

    /**
     * represents how many games have been processed over time
     */
    @FXML
    public LineChart linechart_statistics_games;

    /**
     * represents user activity over time
     */
    @FXML
    public LineChart linechart_statistics_active;

    /**
     * Ranking Table
     */
    @FXML
    public TableView table_ranking;

    /**
     * RankingTable: Table Column for username
     */
    @FXML
    public TableColumn ranking_tc_user;

    /**
     * RankingTable: Table Column for no of games won
     */
    @FXML
    public TableColumn ranking_tc_games_won;

    /**
     * RankingTable: Table Column for number games total
     */
    @FXML
    public TableColumn ranking_tc_games_total;

    /**
     * RankingTable: Table Column user is active(flag)
     */
    @FXML
    public TableColumn ranking_tc_active;

    /**
     * RankingTable: Table Column for last game played
     */
    @FXML
    public TableColumn ranking_tc_last_game;

    /**
     * RankingTable: Table Column for user registered since
     */
    @FXML
    public TableColumn ranking_tc_registered_since;




    private StatisticsDTO statisticsDTO;

    /**
     * Autowired constructor for the controller (generated by Spring)
     */
    @Autowired
    public StatisticsController(ClientService clientService, SoundEffects soundEffects) {
        super("/fxml/Statistics.fxml", "Statistics");
        this.clientService = Objects.requireNonNull(clientService);
        this.soundEffects = Objects.requireNonNull(soundEffects);


    }

    /**
     * Open Statistics Window using given user
     *
     * @param statisticsDTO To get personalized statistics, the statisticsDTO is mandatory.
     */
    public void run(StatisticsDTO statisticsDTO){
        LOG.debug("opening statistics window");
        this.statisticsDTO = Objects.requireNonNull(statisticsDTO);

        super.run();
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        /**
         * client data
         */

        loadPersonalData();
        loadServerData();
        loadRankingData();

    }


    /**
     * fills ranking table
     */
    private void loadRankingData() {
        ObservableList<RankingTableRowDTO> observableList = FXCollections.observableArrayList(statisticsDTO.getRankingTable());

        ranking_tc_user.setCellValueFactory(
            new PropertyValueFactory<RankingTableRowDTO,String>("userName")
        );

        ranking_tc_games_won.setCellValueFactory(
            new PropertyValueFactory<RankingTableRowDTO,Integer>("gamesWon")
        );

        ranking_tc_games_total.setCellValueFactory(
            new PropertyValueFactory<RankingTableRowDTO,Integer>("gamesTotal")
        );

        ranking_tc_last_game.setCellValueFactory(
            new PropertyValueFactory<RankingTableRowDTO,LocalDate>("lastGameAsLocalDate")
        );

        ranking_tc_registered_since.setCellValueFactory(
            new PropertyValueFactory<RankingTableRowDTO,LocalDate>("registeredSinceAsLocalDate")
        );

        // set data and sort table
        table_ranking.setItems(observableList);
        table_ranking.getSortOrder().add(ranking_tc_games_won);





    }


    /**
     * converts a long localdate to a string localdate
     * @param date
     * @return
     */
    private String toLocalDate(long date) {
        return LocalDate.ofEpochDay(date).toString();
    }

    /**
     * loads any data for server tab
     */
    private void loadServerData() {

        statistics_server_version.setText(statisticsDTO.getServerStatisticsDTO().getServerVersion());
        statistics_total_num_active.setText(String.valueOf(statisticsDTO.getServerStatisticsDTO().getTotalNumActive()));
        statistics_total_num_games.setText(String.valueOf(statisticsDTO.getServerStatisticsDTO().getTotalNumGames()));
        statistics_total_num_users.setText(String.valueOf(statisticsDTO.getServerStatisticsDTO().getTotalNumUsers()));
        statistics_first_game_played.setText(statisticsDTO.getServerStatisticsDTO().getFirstGamePlayedAsLocalDate().toString());
        statistics_last_game_played.setText(statisticsDTO.getServerStatisticsDTO().getLastGamePlayedAsLocalDate().toString());

        ArrayList<LineChartPairDTO> listOfChartPairDTOs = statisticsDTO.getServerStatisticsDTO().getLineChartActiveUsers();
        linechart_statistics_active.getData().add(new ChartAdapter().generateActiveUsers(listOfChartPairDTOs));


        ArrayList<LineChartPairDTO> listOfChartPairDTOGames = statisticsDTO.getServerStatisticsDTO().getLineChartGamesProcessed();
        linechart_statistics_games.getData().add(new ChartAdapter().generateProcessedGames(listOfChartPairDTOGames));



    }

    /**
     * loads any data for user tab
     */
    private void loadPersonalData() {
        statistics_username.setText(statisticsDTO.getUserStatisticsDTO().getUsername());
        statistics_games_played.setText(String.valueOf(statisticsDTO.getUserStatisticsDTO().getGamesPlayed()));
        statistics_games_won.setText(String.valueOf(statisticsDTO.getUserStatisticsDTO().getGamesWon()));
        statistics_current_streak.setText(String.valueOf(statisticsDTO.getUserStatisticsDTO().getStreak()));
        formatGreenRed(statistics_current_streak,statisticsDTO.getUserStatisticsDTO().getStreak());

        statistics_current_w_l.setText(new DecimalFormat("0.00").format(statisticsDTO.getUserStatisticsDTO().getWinLossRatio()));
        statistics_registered.setText(String.valueOf(statisticsDTO.getUserStatisticsDTO().getRegistrationDateAsLocalDate()));

        // pie chart (games/won)
        ObservableList<PieChart.Data> winLosePieChartData = FXCollections.observableArrayList(
            new PieChart.Data("games lost",(statisticsDTO.getUserStatisticsDTO().getGamesPlayed() - statisticsDTO.getUserStatisticsDTO().getGamesWon())),
            new PieChart.Data("games won",statisticsDTO.getUserStatisticsDTO().getGamesWon()));
        pie_statistics_personal.setLegendVisible(false);
        pie_statistics_personal.setData(winLosePieChartData);


        linechart_statistics_personal_form.getData().
            add(new ChartAdapter().generateWinLoseSeries(statisticsDTO.getUserStatisticsDTO().getLineChartWinLossRatio()));

        // line chart (games played)
        linechart_personal_games_played_over_time.getData().add(new ChartAdapter().generatePersonalGamesOverTime(statisticsDTO.getUserStatisticsDTO().getLineChartGamesPlayed()));



    }

    /**
     * sets text and label color depending if value is pos or neg, color green / red.
     * @param label
     * @param value
     */
    private void formatGreenRed(Label label, Integer value) {
        String text = "";
        label.setTextFill(Color.BLACK);
        if (value > 0) {
            statistics_current_streak.setTextFill(Color.GREEN);
            text += "+";
        }
        if (value < 0)
            statistics_current_streak.setTextFill(Color.GREEN);
        label.setText(text + value.toString());
    }
}
