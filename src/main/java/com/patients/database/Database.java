package com.patients.database;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class uses  <a href="https://github.com/brettwooldridge/HikariCP">HikariCP</a> as the database connection pool
 */
public class Database {

    private final static Logger LOGGER = LogManager.getLogger(Database.class);

    // jvm option -DdbUrl
    public static final String DB_URL = "dbUrl";
    // jvm option -DdbUsername
    public static final String DB_USERNAME = "dbUsername";
    // jvm option -DdbPassword
    public static final String DB_PASSWORD = "dbPassword";

    // HikariCP DataSource
    private final static HikariDataSource ds;

    // Using static block for singleton
    static {
        ds = new HikariDataSource(); // new the DS object
        ds.setJdbcUrl(System.getProperty(DB_URL)); // url
        ds.setUsername(System.getProperty(DB_USERNAME)); // username
        ds.setPassword(System.getProperty(DB_PASSWORD)); // password
    }

    /**
     * Directly returns a MySQL JDBC connection from the pool. Can be used everywhere.
     * @return java.sql.Connection, the JDBC connection
     * @throws SQLException any JDBC excpetion
     */
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    /**
     * Helper function that can be used in main to make sure JVM options are provided
     * It will directly exit if any of the JVM options for database are not provided
     */
    public static void validateSystemProperties() {
        String dbUrl = System.getProperty(DB_URL);
        String dbUsername = System.getProperty(DB_USERNAME);
        String dbPassword = System.getProperty(DB_PASSWORD);
        if (Strings.isEmpty(dbUrl) || Strings.isBlank(dbUrl)) {
            LOGGER.log(Level.ERROR, "System Property " + DB_URL + " is not provided");
            System.exit(1);
        }
        if (Strings.isEmpty(dbUsername) || Strings.isBlank(dbUsername)) {
            LOGGER.log(Level.ERROR, "System Property " + DB_USERNAME + " is not provided");
            System.exit(1);
        }
        if (Strings.isEmpty(dbPassword) || Strings.isBlank(dbPassword)) {
            LOGGER.log(Level.ERROR, "System Property " + DB_PASSWORD +  " is not provided");
            System.exit(1);
        }
    }
}
