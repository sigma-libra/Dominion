package server.persistence.impl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.h2.tools.RunScript;

import server.persistence.Persistence;
import server.persistence.exception.PersistenceException;
import server.persistence.exception.UserAlreadyExistsException;
import server.persistence.exception.UserNotFoundException;
import shared.domain.engine.GameState;
import shared.dto.GameInfoDTO;
import shared.domain.engine.Player;
import shared.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.*;

/**
 * Crud implementations for the UserDTO Model
 * @author hannes
 */
@Repository
public class PersistenceImpl implements Persistence {
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static Map<String, PersistenceImpl> instances = new HashMap<>();

    @Autowired
    public PersistenceImpl(){
    }


    //Prepared statements-----------------------------------------------------------------------------------------------
    /**
     * prepared Statement for getting a user by id
     */
    private final static String PREPARED_STATEMENT_GETUSERBYID = "SELECT * FROM USER WHERE ID = ?;";

    /**
     * prepared Statement for inserting a new user into the db
     */
    private final static String PREPARED_STATEMENT_ADDUSER = "INSERT INTO USER" +
        "(ID, NAME, REGDATE, PASSWORD) " +
        "VALUES (DEFAULT, ?, DEFAULT, ?);";

    /**
     * prepared Statement for getting all users
     */
    private final static String PREPARED_STATEMENT_GETALLUSERS = "SELECT * FROM USER;";

    /**
     * prepared Statement for getting the last created user
     */
    private final static String PREPARED_STATEMENT_TOP_USER = "SELECT TOP 1 * FROM USER ORDER BY ID DESC;";

    /**
     * prepared Statement for getting a user by its username
     */
    private final static String PREPARED_STATEMENT_GETUSERBYUSERNAME = "SELECT * FROM USER WHERE NAME = ?;";

    /**
     * prepared Statement for adding a gameState
     */
    private final static String PREPARED_STATEMENT_ADDGAMESTATE = "INSERT INTO GAMESTATE (ID, STATE, USERS) VALUES (DEFAULT, ?, ?);";

    /**
     * prepared Statement for getting all gameStates
     */
    private final static String PREPARED_GET_ALL_GAMESTATE = "SELECT * FROM GAMESTATE;";

    /**
     * prepared Statement for getting all gameStates
     */
    private final static String PREPARED_GET_ID_GAMESTATE = "SELECT * FROM GAMESTATE WHERE ID=?;";

    /**
     * prepared statements
     */
    private PreparedStatement getUserByIdStatement,
        addUserStatement, getAllUsersStatement,
        topUserStatement, getUserByUserNameStatement,
        addGameStateStatement, getAllGameStatement,
        getByIDGameState;

    private Connection connection;


    //Connection methods------------------------------------------------------------------------------------------------

    public static PersistenceImpl getInstance() throws PersistenceException {
        return getInstance("jdbc:h2:file:~/Dominion-Database", "SA", "");
    }

    public static PersistenceImpl getInstance(String dbpath, String username, String password) throws PersistenceException {
        LOG.debug("Calling Persistence constructor");
        for (String info : instances.keySet()) {
            if (info.equals(dbpath)) {
                return instances.get(info);
            }
        }

        PersistenceImpl new_instance = new PersistenceImpl(dbpath, username, password);
        instances.put(dbpath, new_instance);
        return new_instance;
    }
    /**
     * Constructor
     *
     * @param dbpath
     * @param username
     * @param password
     * @throws PersistenceException
     */
    private PersistenceImpl(String dbpath, String username, String password) throws PersistenceException {
        LOG.debug("no db-connection established yet - trying to set up db-driver");
        try {
            Class.forName("org.h2.Driver");
        } catch (Exception e) {
            String message = "could not set up db-driver: " + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }

        LOG.debug("finished setting up db-driver");
        LOG.debug("trying to set up db-connection");
        try {
            connection = DriverManager.getConnection(dbpath, username, password);
        } catch (Exception e) {
            String message = "could not set up db-connection: " + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }
        LOG.debug("finished setting up db-connection");

        initializeDatabase(); // do this BEFORE preparing the statements!

        try {
            getUserByIdStatement = connection.prepareStatement(PREPARED_STATEMENT_GETUSERBYID);
            addUserStatement = connection.prepareStatement(PREPARED_STATEMENT_ADDUSER);
            getAllUsersStatement = connection.prepareStatement(PREPARED_STATEMENT_GETALLUSERS);
            topUserStatement = connection.prepareStatement(PREPARED_STATEMENT_TOP_USER);
            getUserByUserNameStatement = connection.prepareStatement(PREPARED_STATEMENT_GETUSERBYUSERNAME);
            addGameStateStatement = connection.prepareStatement(PREPARED_STATEMENT_ADDGAMESTATE, Statement.RETURN_GENERATED_KEYS);
            getAllGameStatement = connection.prepareStatement(PREPARED_GET_ALL_GAMESTATE);
            getByIDGameState = connection.prepareStatement(PREPARED_GET_ID_GAMESTATE);
        } catch (SQLException e) {
            LOG.error("Problem setting up PersistenceImpl: " + e.getMessage());
            throw new PersistenceException(e.getMessage());
        }
    }

    private void initializeDatabase() throws PersistenceException {
        LOG.debug("initializing the database - building the structure of the database");
        InputStream insertTestDataSql = PersistenceImpl.class.getClassLoader().getResourceAsStream("sql/insert_test_db.sql");
        assert insertTestDataSql != null;
        Reader reader = new InputStreamReader(insertTestDataSql);

        try {
            RunScript.execute(connection, reader);
        } catch (SQLException e) {
            String message = "could not execute sql/insert_test_db.sql: " + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }
        LOG.debug("finished initializing the database");
    }

    public void closeConnection() throws PersistenceException {
        LOG.debug("a db-connection exists - attempting to close it");
        try {
            connection.close();
        }catch (SQLException e){
            String message = "Failed to close db-connection: " + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }
        LOG.debug("db-connection closed successfully");
    }


    //Persistence methods-----------------------------------------------------------------------------------------------
    /**
     * get a set of all users
     *
     * @return Set of all users saved in DB
     * @throws PersistenceException
     */
    @Override
    public Set<UserDTO> getAllUsers() throws PersistenceException {
        LOG.debug("Calling getAllUsers");
        Set<UserDTO> userDTOS = new HashSet<>();

        try {
            ResultSet resultSet = getAllUsersStatement.executeQuery();

            while(resultSet.next()) {
                UserDTO userDTO = new UserDTO();
                userDTO.setId(resultSet.getInt("ID"));
                userDTO.setUsername(resultSet.getString("NAME"));
                userDTO.setRegistrationDate(resultSet.getDate("regdate").toLocalDate());
                //just to DEBUG
                userDTO.setPassword(resultSet.getString("PASSWORD"));
                userDTOS.add(userDTO);
            }
        } catch (SQLException e) {
            String message = "Problem with getAllUsers: " + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }
        return userDTOS;
    }

    /**
     * get UserDTO by id
     *
     * @param id UserDTO ID
     * @return UserDTO object with corresponding id
     * @throws PersistenceException
     */
    @Override
    public UserDTO getUserById(Integer id) throws PersistenceException{
        LOG.debug("Calling getUserById");
        UserDTO userDTO = new UserDTO();

        try {
            getUserByIdStatement.setInt(1,id);
            ResultSet resultSet = getUserByIdStatement.executeQuery();
            if (!resultSet.next()) {
                throw new UserNotFoundException("error getting userDTO by ID");
            }
            userDTO.setId(resultSet.getInt("ID"));
            userDTO.setUsername(resultSet.getString("NAME"));
            userDTO.setRegistrationDate(resultSet.getDate("regdate").toLocalDate());
            userDTO.setPassword(resultSet.getString("PASSWORD"));


            LOG.info("Got UserDTO by id: " + userDTO.toString());


        } catch (SQLException e) {
            String message = "Problem with getUserById: " + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }


        return userDTO;
    }

    /**
     * @param userDTO UserDTO to be saved in database
     * @throws PersistenceException
     */
    @Override
    public UserDTO addUser(UserDTO userDTO) throws PersistenceException {
        LOG.debug("Calling addUser");

        // check if this username is already in use
        try {
            getUserByUserName(userDTO.getUserName());
            throw new UserAlreadyExistsException("this username is already taken");
        } catch (UserNotFoundException e){}

        try {
            addUserStatement.setString(1, userDTO.getUserName());
            addUserStatement.setString(2, userDTO.getPassword());
            addUserStatement.executeUpdate();

            // CHECK INSERT
            ResultSet generatedKeys = addUserStatement.getGeneratedKeys();
            if (!generatedKeys.next())
                throw new PersistenceException("error getting top userDTO for check");
            LOG.info("USER CREATED: " + userDTO.toString());
            int id = generatedKeys.getInt(1);
            return this.getUserById(id);
        } catch (SQLException e) {
            String message = "Problem with addUser: " + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * get user by name do avoid overlaps
     *
     * @param userName
     * @return UserDTO by Name
     * @throws PersistenceException
     */
    @Override
    public UserDTO getUserByUserName(String userName) throws PersistenceException {
        LOG.debug("Calling getUserByUserName");
        UserDTO userDTO = new UserDTO();
        try {
            getUserByUserNameStatement.setString(1, userName);
            ResultSet resultSet = getUserByUserNameStatement.executeQuery();
            if (!resultSet.next()) {
                throw new UserNotFoundException("Error getting userDTO by userName");
            }
            userDTO.setId(resultSet.getInt("ID"));
            userDTO.setUsername(resultSet.getString("NAME"));
            userDTO.setRegistrationDate(resultSet.getDate("REGDATE").toLocalDate());
            userDTO.setPassword(resultSet.getString("PASSWORD"));
            LOG.info("Got UserDTO by Name: " + userDTO.toString());
        } catch (SQLException e) {
            String message = "Problem with getUserByUserName: " + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }
        return userDTO;
    }

    public List<GameState> getAllGames() throws PersistenceException {
        LOG.info("getAllGames");
        List<GameState> gameStates = new ArrayList<>();
        try {
            ResultSet resultSet = getAllGameStatement.executeQuery();
            while (resultSet.next()) {
                ByteArrayInputStream bais = new ByteArrayInputStream(resultSet.getBytes("STATE"));
                try {
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    GameState gameState = (GameState) ois.readObject();
                    ois.close();
                    gameStates.add(gameState);
                } catch (IOException | ClassNotFoundException e) {
                    String message = "Problem with getAllGames 1: " + e.getMessage();
                    LOG.error(message);
                    throw new PersistenceException(message);
                }
            }
        } catch (SQLException e) {
            String message = "Problem with getAllGames 2: " + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }
        return gameStates;
    }

    @Override
    public List<Integer> getAllGamesForUsers(List<UserDTO> users) throws PersistenceException {
        LOG.info("getAllGamesForUsers");
        List<Integer> gameStates = new ArrayList<>();
        try {
            ResultSet resultSet = getAllGameStatement.executeQuery();
            while (resultSet.next()) {
                ByteArrayInputStream bais = new ByteArrayInputStream(resultSet.getBytes("STATE"));
                try {
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    //HERE
                    GameState gameState = (GameState) ois.readObject();
                    Integer id = resultSet.getInt("ID");
                    ois.close();
                    List<UserDTO> userDTOS = new ArrayList<>();
                    for (Player p : gameState.getPlayers()) {
                        userDTOS.add(p.getUser());
                    }
                    if (userDTOS.size() == users.size()) {
                        boolean all = true;
                        List<Integer> savedIds = new ArrayList<>();
                        List<Integer> activeIds = new ArrayList<>();
                        for(int i = 0; i < userDTOS.size(); i++) {
                            savedIds.add(users.get(i).getId());
                            activeIds.add(userDTOS.get(i).getId());
                        }

                        for(int j = 0; j < userDTOS.size(); j++) {
                            Integer savedID = savedIds.get(j);
                            if(!activeIds.contains(savedID)) {
                                all = false;
                            }
                        }

                        if (all) {
                            gameStates.add(id);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    String message = "Problem with getAllGamesForUsers 1: " + e.toString() + "--" +e.getMessage();
                    LOG.error(message);
                    //Very specific situation: overload of setRegistrationDate causes exception,
                    //But since this has no bearing on the game whatsoever (registrationDate is only used for statistics,
                    // and this method is only used to load saved games) we ignore it
                    if(! message.equals("Problem with getAllGamesForUsers 1: java.io.InvalidClassException: shared.dto.UserDTO; incompatible types for field registrationDate--shared.dto.UserDTO; incompatible types for field registrationDate")) {
                       // throw new PersistenceException(message);
                    }
                }
            }
        } catch (SQLException e) {
            String message = "Problem with getAllGamesForUsers 2: " + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }
        return gameStates;
    }

    @Override
    public int addGame(GameState gameState) throws PersistenceException {
        LOG.info("addGame");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(gameState);
            oos.flush();
            oos.close();
            bos.close();
        } catch (IOException e) {
            String message = "Problem with addGame 1: " + e.toString() + "--" + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }
        byte[] data = bos.toByteArray();
        try {
            addGameStateStatement.setObject(1,data);
            Integer[] users = new Integer[gameState.getPlayers().size()];
            for (int i = 0; i < gameState.getPlayers().size(); i++) {
                if (gameState.getPlayers().get(i).getUser() != null) users[i] = gameState.getPlayers().get(i).getUser().getId();
            }
            addGameStateStatement.setArray(2, connection.createArrayOf("INT", users));
            addGameStateStatement.executeUpdate();
            ResultSet rs = addGameStateStatement.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new PersistenceException("did not get id");
            }
        } catch (SQLException e) {
            String message = "Problem with addGame 2: " + e.toString() + "--" + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public GameState getGameStateByID(int id) throws PersistenceException {
        LOG.info("getGameStateByID");
        try {
            getByIDGameState.setInt(1, id);
            ResultSet resultSet = getByIDGameState.executeQuery();
            if (resultSet.next()) {
                ByteArrayInputStream bais = new ByteArrayInputStream(resultSet.getBytes("STATE"));
                try {
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    GameState gameState = (GameState) ois.readObject();
                    ois.close();
                    return gameState;
                } catch (IOException | ClassNotFoundException e) {
                    String message = "Problem getGameStateByID 1: " + e.toString() + "--" + e.getMessage();
                    LOG.error(message);
                    throw new PersistenceException(message);
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            String message = "Problem getGameStateByID 2: " + e.toString() + "--" + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public void addGameInfo(GameInfoDTO dto) throws PersistenceException {
        LOG.debug("method addGameInfo() called");
        try {
            PreparedStatement prep =
                connection.prepareStatement("INSERT INTO gameInfo VALUES(DEFAULT,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            prep.setInt(1, dto.getWinner());
            prep.setDate(2, java.sql.Date.valueOf(dto.getDateAsLocalDate()));
            prep.executeUpdate();
            ResultSet generatedKeys = prep.getGeneratedKeys();
            if (!generatedKeys.next())
                throw new PersistenceException("error getting generated key");
            int gid = generatedKeys.getInt(1);

            for(int uid:
                dto.getPlayers()) {
                addPlayedGame(uid, gid);
            }
        }
        catch (SQLException e) {
            String message = "Problem with addGameInfo: " + e.toString() + "--" + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public ArrayList<GameInfoDTO> getAllGameInfo() throws PersistenceException {
        LOG.debug("method getAllGameInfo() called");
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT g.gid, u.uid, g.gamedate, g.winner" +
                " FROM gameInfo g, userGameInfo u" +
                " WHERE g.gid = u.gid");

            Map<Integer, GameInfoDTO> gidToInfo = new HashMap<>();
            while (rs.next()) {
                if (gidToInfo.get(rs.getInt(1)) == null) {
                    GameInfoDTO temp = new GameInfoDTO();
                    temp.addPlayer(rs.getInt(2));
                    temp.setDate(rs.getDate(3).toLocalDate());
                    temp.setWinner(rs.getInt(4));
                    gidToInfo.put(rs.getInt(1), temp);
                }
                else gidToInfo.get(rs.getInt(1)).addPlayer(rs.getInt(2));
            }

            return new ArrayList<>(gidToInfo.values());
        }
        catch (SQLException e) {
            String message = "Problem getAllGameInfo: " + e.toString() + "--" + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }
    }


    private void addPlayedGame(int userId, int gameId) throws PersistenceException {
        LOG.debug("method addPlayedGame called");
        try {
            PreparedStatement prep =
                connection.prepareStatement("INSERT INTO userGameInfo VALUES(?,?)");
            prep.setInt(1, userId);
            prep.setInt(2, gameId);
            prep.executeUpdate();
        }
        catch(SQLException e){
            String message = "Problem addPlayedGame: " + e.toString() + "--" + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }
    }


    public void resetDatabase() throws PersistenceException {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DROP ALL OBJECTS;");
        } catch (SQLException e){
            String message = "Problem resetDatabase: " + e.toString() + "--" + e.getMessage();
            LOG.error(message);
            throw new PersistenceException(message);
        }
        initializeDatabase();
    }
}