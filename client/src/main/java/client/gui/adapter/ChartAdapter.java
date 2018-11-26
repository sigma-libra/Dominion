package client.gui.adapter;

import javafx.scene.chart.XYChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import shared.dto.LineChartPairDTO;


import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;

/**
 * class to generate series for charts
 */
public class ChartAdapter {

    public ChartAdapter () {}

    /**
     * creates series for win/lose linechart
     * @return chart series
     */
    public XYChart.Series generateWinLoseSeries(ArrayList<LineChartPairDTO> listOfChartPairDTOs) {
        XYChart.Series series = new XYChart.Series();
        series.setName("W/L-Ratio");

        for(LineChartPairDTO lineChartPairDTO : listOfChartPairDTOs) {

            series.getData().add(new XYChart.Data(LocalDate.ofEpochDay(lineChartPairDTO.getX()).toString(),
                lineChartPairDTO.getY()));

        }
        return series;
    }

    /**
     * generate series of active users
     *
     * @param listOfChartPairDTOs
     * @return
     */
    public XYChart.Series generateActiveUsers(ArrayList<LineChartPairDTO> listOfChartPairDTOs) {
        XYChart.Series series = new XYChart.Series();
        series.setName("Number of Users");
        for(LineChartPairDTO lineChartPairDTO : listOfChartPairDTOs) {
            series.getData().add(new XYChart.Data(LocalDate.ofEpochDay(lineChartPairDTO.getX()).toString(),
                lineChartPairDTO.getY()));
        }
        return series;

    }

    /**
     * Generates series of processed games
     *
     * @param listOfChartPairDTOs
     * @return
     */
    public XYChart.Series generateProcessedGames(ArrayList<LineChartPairDTO> listOfChartPairDTOs) {
        XYChart.Series series = new XYChart.Series();
        series.setName("Number of Games");
        for(LineChartPairDTO lineChartPairDTO : listOfChartPairDTOs) {
            series.getData().add(new XYChart.Data(LocalDate.ofEpochDay(lineChartPairDTO.getX()).toString(),
                lineChartPairDTO.getY()));
        }
        return series;

    }

    /**
     * Generated series of games played by given player
     * @param lineChartGamesPlayed
     * @return
     */
    public XYChart.Series generatePersonalGamesOverTime(ArrayList<LineChartPairDTO> lineChartGamesPlayed) {
        XYChart.Series series = new XYChart.Series();
        series.setName("Number of Games");
        for(LineChartPairDTO lineChartPairDTO : lineChartGamesPlayed) {
            series.getData().add(new XYChart.Data(LocalDate.ofEpochDay(lineChartPairDTO.getX()).toString(),
                lineChartPairDTO.getY()));
        }
        return series;
    }
}
