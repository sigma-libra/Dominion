package server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.persistence.exception.PersistenceException;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * H2 Database handler
 */
public class DBHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static Connection con = null;

    /**
     * Open connection to database
     * @throws PersistenceException
     */
    public static void openConnection() throws PersistenceException {
        try {
            LOG.debug("open DB connection");
            Class.forName("org.h2.Driver");
            con = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error(e.getMessage());
            throw new PersistenceException(e.getMessage());
        }
    }

    /**
     * Get connection to database
     * @return
     */
    public static Connection getConnection(){
        return con;
    }

    /**
     * Close connection to database
     * @throws PersistenceException
     */
    public static void closeConnection() throws PersistenceException {
        try {
            LOG.debug("close DB connection");
            con.close();
            con=null;
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            throw new PersistenceException(e.getMessage());
        }
    }
}
