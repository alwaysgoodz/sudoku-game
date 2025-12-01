package com.sudoku.database;

import com.sudoku.model.GameHistory;
import com.sudoku.model.PlayerStats;
import com.sudoku.model.SavedGame;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Data Access Object for game-related database operations.
 * Demonstrates encapsulation and separation of concerns.
 */
public class GameDAO {
    private static final Logger LOGGER = Logger.getLogger(GameDAO.class.getName());
    private final DatabaseManager dbManager;
    
    public GameDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Saves a completed game to the history
     * @param gameHistory The game history to save
     * @return true if saved successfully, false otherwise
     */
    public boolean saveGameHistory(GameHistory gameHistory) {
        String sql = """
            INSERT INTO game_history (player_name, difficulty, completion_time, moves_count, hints_used)
            VALUES (?, ?, ?, ?, ?)
            """;
        
        try {
            int rowsAffected = dbManager.executeUpdate(sql,
                gameHistory.getPlayerName(),
                gameHistory.getDifficulty(),
                gameHistory.getCompletionTime(),
                gameHistory.getMovesCount(),
                gameHistory.getHintsUsed()
            );
            
            if (rowsAffected > 0) {
                updatePlayerStats(gameHistory);
                LOGGER.info("Game history saved for player: " + gameHistory.getPlayerName());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving game history", e);
        }
        return false;
    }
    
    /**
     * Updates player statistics after a game
     * @param gameHistory The completed game history
     */
    private void updatePlayerStats(GameHistory gameHistory) throws SQLException {
        PlayerStats stats = getPlayerStats(gameHistory.getPlayerName());
        
        if (stats == null) {
            // Create new player stats
            stats = new PlayerStats(gameHistory.getPlayerName());
        }
        
        stats.incrementGamesPlayed();
        stats.incrementGamesWon();
        stats.addTime(gameHistory.getCompletionTime());
        
        if (stats.getBestTime() == 0 || gameHistory.getCompletionTime() < stats.getBestTime()) {
            stats.setBestTime(gameHistory.getCompletionTime());
        }
        
        savePlayerStats(stats);
    }
    
    /**
     * Retrieves player statistics
     * @param playerName Name of the player
     * @return PlayerStats object or null if not found
     */
    public PlayerStats getPlayerStats(String playerName) {
        String sql = "SELECT * FROM player_stats WHERE player_name = ?";
        
        try (ResultSet rs = dbManager.executeQuery(sql, playerName)) {
            if (rs.next()) {
                PlayerStats stats = new PlayerStats(rs.getString("player_name"));
                stats.setGamesPlayed(rs.getInt("games_played"));
                stats.setGamesWon(rs.getInt("games_won"));
                stats.setTotalTime(rs.getLong("total_time"));
                stats.setBestTime(rs.getLong("best_time"));
                stats.setAverageTime(rs.getLong("average_time"));
                stats.setLastPlayed(rs.getTimestamp("last_played"));
                return stats;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving player stats", e);
        }
        return null;
    }
    
    /**
     * Saves or updates player statistics
     * @param stats The player statistics to save
     * @return true if saved successfully, false otherwise
     */
    public boolean savePlayerStats(PlayerStats stats) {
        // Derby不支持MERGE，使用先查询再INSERT或UPDATE的方式
        PlayerStats existing = getPlayerStats(stats.getPlayerName());
        
        try {
            int rowsAffected;
            if (existing == null) {
                // 插入新记录
                String insertSql = """
                    INSERT INTO player_stats (player_name, games_played, games_won, total_time, best_time, average_time)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;
                rowsAffected = dbManager.executeUpdate(insertSql,
                    stats.getPlayerName(),
                    stats.getGamesPlayed(),
                    stats.getGamesWon(),
                    stats.getTotalTime(),
                    stats.getBestTime(),
                    stats.getAverageTime()
                );
            } else {
                // 更新现有记录
                String updateSql = """
                    UPDATE player_stats SET 
                        games_played = ?,
                        games_won = ?,
                        total_time = ?,
                        best_time = ?,
                        average_time = ?,
                        last_played = CURRENT_TIMESTAMP
                    WHERE player_name = ?
                    """;
                rowsAffected = dbManager.executeUpdate(updateSql,
                    stats.getGamesPlayed(),
                    stats.getGamesWon(),
                    stats.getTotalTime(),
                    stats.getBestTime(),
                    stats.getAverageTime(),
                    stats.getPlayerName()
                );
            }
            
            LOGGER.info("Player stats saved for: " + stats.getPlayerName());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving player stats", e);
            return false;
        }
    }
    
    /**
     * Saves a game in progress
     * @param savedGame The game to save
     * @return true if saved successfully, false otherwise
     */
    public boolean saveGame(SavedGame savedGame) {
        String sql = """
            INSERT INTO saved_games (player_name, game_name, grid_data, original_grid, difficulty, elapsed_time)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try {
            int rowsAffected = dbManager.executeUpdate(sql,
                savedGame.getPlayerName(),
                savedGame.getGameName(),
                savedGame.getGridData(),
                savedGame.getOriginalGrid(),
                savedGame.getDifficulty(),
                savedGame.getElapsedTime()
            );
            
            LOGGER.info("Game saved: " + savedGame.getGameName());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving game", e);
            return false;
        }
    }
    
    /**
     * Loads a saved game
     * @param gameId The ID of the saved game
     * @return SavedGame object or null if not found
     */
    public SavedGame loadGame(int gameId) {
        String sql = "SELECT * FROM saved_games WHERE id = ?";
        
        try (ResultSet rs = dbManager.executeQuery(sql, gameId)) {
            if (rs.next()) {
                SavedGame savedGame = new SavedGame();
                savedGame.setId(rs.getInt("id"));
                savedGame.setPlayerName(rs.getString("player_name"));
                savedGame.setGameName(rs.getString("game_name"));
                savedGame.setGridData(rs.getString("grid_data"));
                savedGame.setOriginalGrid(rs.getString("original_grid"));
                savedGame.setDifficulty(rs.getInt("difficulty"));
                savedGame.setElapsedTime(rs.getLong("elapsed_time"));
                savedGame.setDateSaved(rs.getTimestamp("date_saved"));
                return savedGame;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading game", e);
        }
        return null;
    }
    
    /**
     * Gets all saved games for a player
     * @param playerName Name of the player
     * @return List of saved games
     */
    public List<SavedGame> getSavedGames(String playerName) {
        List<SavedGame> savedGames = new ArrayList<>();
        String sql = "SELECT * FROM saved_games WHERE player_name = ? ORDER BY date_saved DESC";
        
        try (ResultSet rs = dbManager.executeQuery(sql, playerName)) {
            while (rs.next()) {
                SavedGame savedGame = new SavedGame();
                savedGame.setId(rs.getInt("id"));
                savedGame.setPlayerName(rs.getString("player_name"));
                savedGame.setGameName(rs.getString("game_name"));
                savedGame.setGridData(rs.getString("grid_data"));
                savedGame.setOriginalGrid(rs.getString("original_grid"));
                savedGame.setDifficulty(rs.getInt("difficulty"));
                savedGame.setElapsedTime(rs.getLong("elapsed_time"));
                savedGame.setDateSaved(rs.getTimestamp("date_saved"));
                savedGames.add(savedGame);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving saved games", e);
        }
        return savedGames;
    }
    
    /**
     * Deletes a saved game
     * @param gameId The ID of the game to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteSavedGame(int gameId) {
        String sql = "DELETE FROM saved_games WHERE id = ?";
        
        try {
            int rowsAffected = dbManager.executeUpdate(sql, gameId);
            LOGGER.info("Saved game deleted: " + gameId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting saved game", e);
            return false;
        }
    }
    
    /**
     * Gets game history for a player
     * @param playerName Name of the player
     * @param limit Maximum number of records to return
     * @return List of game history records
     */
    public List<GameHistory> getGameHistory(String playerName, int limit) {
        List<GameHistory> history = new ArrayList<>();
        String sql = "SELECT * FROM game_history WHERE player_name = ? ORDER BY date_played DESC";
        
        if (limit > 0) {
            sql += " FETCH FIRST " + limit + " ROWS ONLY";
        }
        
        try (ResultSet rs = dbManager.executeQuery(sql, playerName)) {
            while (rs.next()) {
                GameHistory gameHistory = new GameHistory(
                    rs.getString("player_name"),
                    rs.getInt("difficulty"),
                    rs.getLong("completion_time"),
                    rs.getInt("moves_count"),
                    rs.getInt("hints_used")
                );
                gameHistory.setId(rs.getInt("id"));
                gameHistory.setDatePlayed(rs.getTimestamp("date_played"));
                history.add(gameHistory);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving game history", e);
        }
        return history;
    }
    
    /**
     * Gets the top players by best time
     * @param limit Number of top players to return
     * @return List of player statistics
     */
    public List<PlayerStats> getTopPlayers(int limit) {
        List<PlayerStats> topPlayers = new ArrayList<>();
        String sql = """
            SELECT * FROM player_stats 
            WHERE best_time > 0 
            ORDER BY best_time ASC 
            FETCH FIRST ? ROWS ONLY
            """;
        
        try (ResultSet rs = dbManager.executeQuery(sql, limit)) {
            while (rs.next()) {
                PlayerStats stats = new PlayerStats(rs.getString("player_name"));
                stats.setGamesPlayed(rs.getInt("games_played"));
                stats.setGamesWon(rs.getInt("games_won"));
                stats.setTotalTime(rs.getLong("total_time"));
                stats.setBestTime(rs.getLong("best_time"));
                stats.setAverageTime(rs.getLong("average_time"));
                stats.setLastPlayed(rs.getTimestamp("last_played"));
                topPlayers.add(stats);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving top players", e);
        }
        return topPlayers;
    }
}
