package shared.dto;

import java.util.ArrayList;

public class StatisticsDTO
{

    public StatisticsDTO(UserStatisticsDTO userStatisticsDTO, ServerStatisticsDTO serverStatisticsDTO,
                         ArrayList<RankingTableRowDTO> rankingTable) {
        this.userStatisticsDTO = userStatisticsDTO;
        this.serverStatisticsDTO = serverStatisticsDTO;
        this.rankingTable = rankingTable;
    }
    public StatisticsDTO(){}
    private UserStatisticsDTO userStatisticsDTO = new UserStatisticsDTO();

    private ServerStatisticsDTO serverStatisticsDTO = new ServerStatisticsDTO();

    ArrayList<RankingTableRowDTO> rankingTable = new ArrayList<>();

    public UserStatisticsDTO getUserStatisticsDTO() {
        return userStatisticsDTO;
    }

    public ServerStatisticsDTO getServerStatisticsDTO() {
        return serverStatisticsDTO;
    }

    public ArrayList<RankingTableRowDTO> getRankingTable() {
        return rankingTable;
    }
}
