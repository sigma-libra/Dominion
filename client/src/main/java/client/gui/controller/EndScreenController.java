package client.gui.controller;

import client.gui.adapter.table.PlayerStats;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import shared.dto.GameStateDTO;
import shared.dto.UserDTO;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the end screen after a game is finished
 */
@Controller
public class EndScreenController extends FXMLControllerBase {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private JoinGameController joinGameController;

    private GameTableController gameTableController;

    private UserDTO userDTO;

    private GameStateDTO gameStateDTO;

    private int playerindex;

    @FXML
    private Label success_lable;
    @FXML
    private TableView<PlayerStats> points_table;
    @FXML
    private TableColumn<PlayerStats, String> points_table_name;
    @FXML
    private TableColumn<PlayerStats, Integer> points_table_points;

    /**
     * Creates a new instance
     */
    @Autowired
    protected EndScreenController() {
        super("/fxml/EndScreen.fxml", "End of Game");
        LOG.info("EndScreenController");
    }

    @Override
    protected void initScene(){
        LOG.info("initScene -- Endscreen");
        List<UserDTO> players = gameStateDTO.getPlayers();
        String[] usernames = players.stream().map(p -> p.getUserName()).toArray(String[]::new);
        List<PlayerStats> playerStatsList = new ArrayList<>();
        for (int i = 0; i < gameStateDTO.getPlayers().size(); i++) {
            PlayerStats player = new PlayerStats(
                usernames[i],
                gameStateDTO.getDeckSizes()[i],
                gameStateDTO.getVictoryPoints()[i]);

            playerStatsList.add(player);
        }
        LOG.info(points_table.toString());
        LOG.info(points_table_name.toString());
        LOG.info(points_table_points.toString());
        String[] places = {"FIRST", "SECOND", "THIRD", "FOURTH"};
        points_table_name.setCellValueFactory(new PropertyValueFactory<>("player"));
        points_table_points.setCellValueFactory(new PropertyValueFactory<>("Points"));
        points_table.setItems(FXCollections.observableList(playerStatsList));
        int above = 0;
        int player_points = gameStateDTO.getVictoryPoints()[playerindex];
        for (int i = 0; i < gameStateDTO.getPlayers().size(); i++) {
            if (i != playerindex) {
                if (gameStateDTO.getVictoryPoints()[i] > player_points) {
                    above ++;
                }
            }
        }
        success_lable.setText(places[above]);
    }

    public void run(GameStateDTO gameStateDTO, int playerindex, JoinGameController joinGameController, UserDTO userDTO, GameTableController gameTableController) {
        LOG.info("run");
        this.gameStateDTO = gameStateDTO;
        this.playerindex = playerindex;
        this.joinGameController = joinGameController;
        this.gameTableController = gameTableController;
        this.userDTO = userDTO;

        if (gameStateDTO.getPlayers().size() == 1)
            showNotification("All other players left the game. You win by default.", "You win!");

        super.run();
    }

    /**
     * Closes client after okay is clicked
     */
    @FXML
    public void handleOk() {
        LOG.info("handleOk");
        gameTableController.leaveGame();
        Stage stage = (Stage) points_table.getScene().getWindow();
        stage.close();
    }
}
