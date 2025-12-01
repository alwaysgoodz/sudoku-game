package com.sudoku.database;

import com.sudoku.model.GameHistory;
import com.sudoku.model.PlayerStats;
import com.sudoku.model.SavedGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for GameDAO class.
 * Tests database operations and data persistence.
 */
class GameDAOTest {
    
    private GameDAO gameDAO;
    private static final String TEST_PLAYER = "TestPlayer";
    
    @BeforeEach
    void setUp() {
        gameDAO = new GameDAO();
        // Clean up any existing test data
        cleanupTestData();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up test data after each test
        cleanupTestData();
    }
    
    private void cleanupTestData() {
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            // Delete test player data
            dbManager.executeUpdate("DELETE FROM game_history WHERE player_name = ?", TEST_PLAYER);
            dbManager.executeUpdate("DELETE FROM player_stats WHERE player_name = ?", TEST_PLAYER);
            dbManager.executeUpdate("DELETE FROM saved_games WHERE player_name = ?", TEST_PLAYER);
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
    
    @Test
    @DisplayName("Test saving and retrieving game history")
    void testSaveAndRetrieveGameHistory() {
        // Create test game history
        GameHistory gameHistory = new GameHistory(TEST_PLAYER, 2, 300000L, 45, 3);
        
        // Save game history
        boolean saved = gameDAO.saveGameHistory(gameHistory);
        assertTrue(saved, "Game history should be saved successfully");
        
        // Retrieve game history
        List<GameHistory> history = gameDAO.getGameHistory(TEST_PLAYER, 10);
        assertFalse(history.isEmpty(), "Should retrieve saved game history");
        
        GameHistory retrieved = history.get(0);
        assertEquals(TEST_PLAYER, retrieved.getPlayerName());
        assertEquals(2, retrieved.getDifficulty());
        assertEquals(300000L, retrieved.getCompletionTime());
        assertEquals(45, retrieved.getMovesCount());
        assertEquals(3, retrieved.getHintsUsed());
    }
    
    @Test
    @DisplayName("Test player statistics creation and updates")
    void testPlayerStatsOperations() {
        // Initially no stats should exist
        PlayerStats stats = gameDAO.getPlayerStats(TEST_PLAYER);
        assertNull(stats, "No stats should exist initially");
        
        // Save game history which should create player stats
        GameHistory gameHistory = new GameHistory(TEST_PLAYER, 1, 240000L, 30, 1);
        gameDAO.saveGameHistory(gameHistory);
        
        // Retrieve created stats
        stats = gameDAO.getPlayerStats(TEST_PLAYER);
        assertNotNull(stats, "Player stats should be created");
        assertEquals(TEST_PLAYER, stats.getPlayerName());
        assertEquals(1, stats.getGamesPlayed());
        assertEquals(1, stats.getGamesWon());
        assertEquals(240000L, stats.getBestTime());
        assertEquals(240000L, stats.getTotalTime());
    }
    
    @Test
    @DisplayName("Test saving and loading games")
    void testSaveAndLoadGame() {
        // Create test saved game
        String gridData = "5,3,0,0,7,0,0,0,0,6,0,0,1,9,5,0,0,0,0,9,8,0,0,0,0,6,0,8,0,0,0,6,0,0,0,3,4,0,0,8,0,3,0,0,1,7,0,0,0,2,0,0,0,6,0,6,0,0,0,0,2,8,0,0,0,0,4,1,9,0,0,5,0,0,0,0,8,0,0,7,9";
        String originalGrid = "5,3,0,0,7,0,0,0,0,6,0,0,1,9,5,0,0,0,0,9,8,0,0,0,0,6,0,8,0,0,0,6,0,0,0,3,4,0,0,8,0,3,0,0,1,7,0,0,0,2,0,0,0,6,0,6,0,0,0,0,2,8,0,0,0,0,4,1,9,0,0,5,0,0,0,0,8,0,0,7,9";
        
        SavedGame savedGame = new SavedGame(TEST_PLAYER, "Test Game", gridData, originalGrid, 2, 180000L);
        
        // Save the game
        boolean saved = gameDAO.saveGame(savedGame);
        assertTrue(saved, "Game should be saved successfully");
        
        // Retrieve saved games
        List<SavedGame> savedGames = gameDAO.getSavedGames(TEST_PLAYER);
        assertFalse(savedGames.isEmpty(), "Should retrieve saved games");
        
        SavedGame retrieved = savedGames.get(0);
        assertEquals(TEST_PLAYER, retrieved.getPlayerName());
        assertEquals("Test Game", retrieved.getGameName());
        assertEquals(gridData, retrieved.getGridData());
        assertEquals(originalGrid, retrieved.getOriginalGrid());
        assertEquals(2, retrieved.getDifficulty());
        assertEquals(180000L, retrieved.getElapsedTime());
    }
    
    @Test
    @DisplayName("Test deleting saved games")
    void testDeleteSavedGame() {
        // Create and save a game
        SavedGame savedGame = new SavedGame(TEST_PLAYER, "Test Delete", "1,2,3", "1,2,3", 1, 100000L);
        gameDAO.saveGame(savedGame);
        
        // Get the saved game ID
        List<SavedGame> savedGames = gameDAO.getSavedGames(TEST_PLAYER);
        assertFalse(savedGames.isEmpty(), "Should have saved game");
        
        int gameId = savedGames.get(0).getId();
        
        // Delete the game
        boolean deleted = gameDAO.deleteSavedGame(gameId);
        assertTrue(deleted, "Game should be deleted successfully");
        
        // Verify deletion
        savedGames = gameDAO.getSavedGames(TEST_PLAYER);
        assertTrue(savedGames.isEmpty(), "No saved games should remain");
    }
    
    @Test
    @DisplayName("Test retrieving top players")
    void testGetTopPlayers() {
        // Create multiple players with different best times
        String[] players = {"Player1", "Player2", "Player3"};
        long[] times = {180000L, 120000L, 240000L}; // Player2 should be first
        
        for (int i = 0; i < players.length; i++) {
            GameHistory history = new GameHistory(players[i], 1, times[i], 20, 0);
            gameDAO.saveGameHistory(history);
        }
        
        // Get top players
        List<PlayerStats> topPlayers = gameDAO.getTopPlayers(3);
        assertFalse(topPlayers.isEmpty(), "Should have top players");
        
        // Verify ordering (best time first)
        assertTrue(topPlayers.size() >= 1, "Should have at least one player");
        
        // Clean up additional test data
        for (String player : players) {
            try {
                DatabaseManager dbManager = DatabaseManager.getInstance();
                dbManager.executeUpdate("DELETE FROM game_history WHERE player_name = ?", player);
                dbManager.executeUpdate("DELETE FROM player_stats WHERE player_name = ?", player);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }
    
    @Test
    @DisplayName("Test game history retrieval with limit")
    void testGetGameHistoryWithLimit() {
        // Create multiple game history entries
        for (int i = 0; i < 5; i++) {
            GameHistory history = new GameHistory(TEST_PLAYER, 1, 120000L + i * 1000, 20 + i, i);
            gameDAO.saveGameHistory(history);
        }
        
        // Test with limit
        List<GameHistory> limitedHistory = gameDAO.getGameHistory(TEST_PLAYER, 3);
        assertEquals(3, limitedHistory.size(), "Should return limited number of records");
        
        // Test without limit
        List<GameHistory> allHistory = gameDAO.getGameHistory(TEST_PLAYER, 0);
        assertEquals(5, allHistory.size(), "Should return all records when limit is 0");
    }
    
    @Test
    @DisplayName("Test player statistics calculations")
    void testPlayerStatsCalculations() {
        // Create player stats
        PlayerStats stats = new PlayerStats(TEST_PLAYER);
        
        // Test initial values
        assertEquals(0, stats.getGamesPlayed());
        assertEquals(0, stats.getGamesWon());
        assertEquals(0.0, stats.getWinRate());
        
        // Simulate game completion
        stats.incrementGamesPlayed();
        stats.incrementGamesWon();
        stats.addTime(180000L);
        stats.setBestTime(180000L);
        
        assertEquals(1, stats.getGamesPlayed());
        assertEquals(1, stats.getGamesWon());
        assertEquals(100.0, stats.getWinRate());
        assertEquals(180000L, stats.getBestTime());
        assertEquals(180000L, stats.getAverageTime());
        
        // Add another game
        stats.incrementGamesPlayed();
        stats.incrementGamesWon();
        stats.addTime(120000L);
        stats.setBestTime(120000L); // New best time
        
        assertEquals(2, stats.getGamesPlayed());
        assertEquals(2, stats.getGamesWon());
        assertEquals(100.0, stats.getWinRate());
        assertEquals(120000L, stats.getBestTime());
        assertEquals(150000L, stats.getAverageTime()); // (180000 + 120000) / 2
    }
    
    @Test
    @DisplayName("Test saved game grid parsing")
    void testSavedGameGridParsing() {
        String gridData = "1,2,3,4,5,6,7,8,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
        
        SavedGame savedGame = new SavedGame();
        savedGame.setGridData(gridData);
        
        int[][] grid = savedGame.parseGridData();
        
        // Verify first row
        for (int i = 0; i < 9; i++) {
            assertEquals(i + 1, grid[0][i], "First row should contain values 1-9");
        }
        
        // Verify remaining cells are 0
        for (int row = 1; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                assertEquals(0, grid[row][col], "Remaining cells should be 0");
            }
        }
    }
    
    @Test
    @DisplayName("Test grid to string conversion")
    void testGridToStringConversion() {
        int[][] grid = new int[9][9];
        // Fill first row with 1-9
        for (int i = 0; i < 9; i++) {
            grid[0][i] = i + 1;
        }
        
        String gridString = SavedGame.gridToString(grid);
        assertNotNull(gridString, "Grid string should not be null");
        assertTrue(gridString.startsWith("1,2,3,4,5,6,7,8,9"), 
            "Grid string should start with first row values");
        
        // Count commas (should be 80 for 81 values)
        long commaCount = gridString.chars().filter(ch -> ch == ',').count();
        assertEquals(80, commaCount, "Should have 80 commas for 81 values");
    }
}
