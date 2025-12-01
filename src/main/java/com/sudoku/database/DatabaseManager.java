package com.sudoku.database;

import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Singleton class for managing database connections and operations.
 * Demonstrates the Singleton design pattern and encapsulation.
 */
public class DatabaseManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private static DatabaseManager instance;
    private static final String DB_URL = "jdbc:derby:sudokuDB;create=true";
    private Connection connection;
    
    /**
     * Private constructor to prevent direct instantiation (Singleton pattern)
     */
    private DatabaseManager() {
        initializeDatabase();
    }
    
    /**
     * Gets the singleton instance of DatabaseManager
     * @return The singleton instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Initializes the database and creates necessary tables
     */
    private void initializeDatabase() {
        try {
            // Load Derby embedded driver
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            connection = DriverManager.getConnection(DB_URL);
            
            createTables();
            LOGGER.info("Database initialized successfully");
            
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Derby driver not found", e);
            throw new RuntimeException("Database driver not found", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    /**
     * Creates necessary database tables
     */
    private void createTables() throws SQLException {
        createGameHistoryTable();
        createPlayerStatsTable();
        createSavedGamesTable();
    }
    
    private void createGameHistoryTable() throws SQLException {
        String sql = """
            CREATE TABLE game_history (
                id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
                player_name VARCHAR(100) NOT NULL,
                difficulty INTEGER NOT NULL,
                completion_time BIGINT NOT NULL,
                date_played TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                moves_count INTEGER DEFAULT 0,
                hints_used INTEGER DEFAULT 0,
                PRIMARY KEY (id)
            )
            """;
        
        executeCreateTable(sql, "game_history");
    }
    
    private void createPlayerStatsTable() throws SQLException {
        String sql = """
            CREATE TABLE player_stats (
                player_name VARCHAR(100) NOT NULL,
                games_played INTEGER DEFAULT 0,
                games_won INTEGER DEFAULT 0,
                total_time BIGINT DEFAULT 0,
                best_time BIGINT DEFAULT 0,
                average_time BIGINT DEFAULT 0,
                last_played TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY (player_name)
            )
            """;
        
        executeCreateTable(sql, "player_stats");
    }
    
    private void createSavedGamesTable() throws SQLException {
        String sql = """
            CREATE TABLE saved_games (
                id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
                player_name VARCHAR(100) NOT NULL,
                game_name VARCHAR(100) NOT NULL,
                grid_data VARCHAR(200) NOT NULL,
                original_grid VARCHAR(200) NOT NULL,
                difficulty INTEGER NOT NULL,
                elapsed_time BIGINT DEFAULT 0,
                date_saved TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY (id)
            )
            """;
        
        executeCreateTable(sql, "saved_games");
    }
    
    private void executeCreateTable(String sql, String tableName) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            LOGGER.info("Table " + tableName + " created successfully");
        } catch (SQLException e) {
            if (e.getSQLState().equals("X0Y32")) {
                // Table already exists
                LOGGER.info("Table " + tableName + " already exists");
            } else {
                throw e;
            }
        }
    }
    
    /**
     * Gets a database connection
     * @return Database connection
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }
    
    /**
     * Closes the database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOGGER.info("Database connection closed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing database connection", e);
        }
    }
    
    /**
     * Executes a query and returns the result set
     * @param sql The SQL query
     * @param params Parameters for the query
     * @return ResultSet containing query results
     * @throws SQLException if query execution fails
     */
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        setParameters(stmt, params);
        return stmt.executeQuery();
    }
    
    /**
     * Executes an update query (INSERT, UPDATE, DELETE)
     * @param sql The SQL update statement
     * @param params Parameters for the statement
     * @return Number of affected rows
     * @throws SQLException if execution fails
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            setParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Sets parameters for a prepared statement
     * @param stmt The prepared statement
     * @param params Parameters to set
     * @throws SQLException if parameter setting fails
     */
    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
    
    /**
     * Begins a database transaction
     * @throws SQLException if transaction cannot be started
     */
    public void beginTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }
    
    /**
     * Commits the current transaction
     * @throws SQLException if commit fails
     */
    public void commitTransaction() throws SQLException {
        getConnection().commit();
        getConnection().setAutoCommit(true);
    }
    
    /**
     * Rolls back the current transaction
     * @throws SQLException if rollback fails
     */
    public void rollbackTransaction() throws SQLException {
        getConnection().rollback();
        getConnection().setAutoCommit(true);
    }
    
    /**
     * Checks if a table exists in the database
     * @param tableName Name of the table to check
     * @return true if table exists, false otherwise
     */
    public boolean tableExists(String tableName) {
        try {
            DatabaseMetaData meta = getConnection().getMetaData();
            try (ResultSet rs = meta.getTables(null, null, tableName.toUpperCase(), null)) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error checking if table exists: " + tableName, e);
            return false;
        }
    }
}
