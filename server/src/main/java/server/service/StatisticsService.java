package server.service;

import shared.dto.GameInfoDTO;
import server.service.exception.ServiceException;
import shared.dto.StatisticsDTO;

/**
 * service for retrieving statistics
 */
public interface StatisticsService {


    /**
     *
     * @param id id of user
     * @return dto of information corresponding to that user
     * @throws ServiceException if something went wrong
     */
    StatisticsDTO getStatistics(int id) throws ServiceException;

    /**
     *
     * @param dto contains players, date, winner
     * @throws ServiceException if something went wrong
     */
    void addGameInfo(GameInfoDTO dto) throws ServiceException;


}
