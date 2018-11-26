package server.spring.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import server.service.AuthService;
import server.service.StatisticsService;
import server.service.exception.InvalidTokenException;
import server.service.exception.ServiceException;
import shared.dto.GameInfoDTO;
import shared.dto.StatisticsDTO;
import shared.dto.UserDTO;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

/**
 * Rest controller for statistics
 */
@RestController
@RequestMapping("/stat")
public class StatisticsController {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * user service
     */
    private final StatisticsService statisticsService;

    private final AuthService authService;


    /**
     * Constructor
     * @param statisticsService statistics service
     */
    @Autowired
    public StatisticsController(StatisticsService statisticsService, AuthService authService) {
        this.statisticsService = Objects.requireNonNull(statisticsService);
        this.authService = Objects.requireNonNull(authService);
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public @ResponseBody
    StatisticsDTO getStatistics(@RequestHeader(value="Token") String token) throws ServiceException, InvalidTokenException {
        LOG.debug("Calling getStatistics");

        UserDTO user = getSender(token);
        return statisticsService.getStatistics(user.getId());
    }

    /**
     * retrieves a userdto from a token
     * @param token user's auth token
     * @return userDTO
     * @throws InvalidTokenException
     */
    private UserDTO getSender(String token) throws InvalidTokenException {
        return authService.getUserFromToken(token);
    }


    /**
     * adds gameinfo
     * @param token auth token (header)
     * @param gameInfoDTO (gameinfo sent to server)
     * @throws ServiceException
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody void addGameInfo(@RequestHeader(value="Token") String token, @RequestParam("gameinfo") GameInfoDTO gameInfoDTO) throws ServiceException {
        LOG.info("Calling addGameInfo");
        statisticsService.addGameInfo(gameInfoDTO);
    }

}
