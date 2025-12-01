package com.sudoku.gui;

import com.sudoku.model.SudokuPuzzle;
import com.sudoku.model.GameHistory;
import com.sudoku.model.SavedGame;
import com.sudoku.database.GameDAO;
import javax.swing.*;
import java.util.List;

/**
 * Controller class that manages game logic and coordinates between GUI and data layers.
 * Demonstrates the MVC pattern and separation of concerns.
 */
public class GameController {
    private SudokuGamePanel gamePanel;
    private GameDAO gameDAO;
    private SudokuPuzzle currentPuzzle;
    private String currentPlayerName;
    private int movesCount;
    private int hintsUsed;
    
    /**
     * Constructor for GameController
     */
    public GameController() {
        this.gameDAO = new GameDAO();
        this.movesCount = 0;
        this.hintsUsed = 0;
        this.currentPlayerName = "Player"; // Default name
    }
    
    /**
     * Sets the game panel
     * @param gamePanel The game panel to control
     */
    public void setGamePanel(SudokuGamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
    
    /**
     * Sets the current player name
     * @param playerName Name of the current player
     */
    public void setPlayerName(String playerName) {
        if (playerName != null && !playerName.trim().isEmpty()) {
            this.currentPlayerName = playerName.trim();
        }
    }
    
    /**
     * Starts a new game with specified difficulty
     */
    public void startNewGame() {
        // Show difficulty selection dialog
        String[] difficulties = {"Easy", "Medium", "Hard"};
        String selected = (String) JOptionPane.showInputDialog(
            gamePanel,
            "Select difficulty level:",
            "New Game",
            JOptionPane.QUESTION_MESSAGE,
            null,
            difficulties,
            difficulties[0]
        );
        
        if (selected != null) {
            int difficulty = getDifficultyLevel(selected);
            startNewGame(difficulty);
        }
    }
    
    /**
     * Starts a new game with specified difficulty level
     * @param difficulty Difficulty level (1=Easy, 2=Medium, 3=Hard)
     */
    public void startNewGame(int difficulty) {
        currentPuzzle = new SudokuPuzzle();
        currentPuzzle.generatePuzzle(difficulty);
        
        // Reset counters
        movesCount = 0;
        hintsUsed = 0;
        
        // Update the game panel
        if (gamePanel != null) {
            gamePanel.setPuzzle(currentPuzzle);
            gamePanel.updateStatus("New " + getDifficultyString(difficulty) + " game started!");
        }
    }
    
    /**
     * Called when a game is completed
     */
    public void onGameCompleted() {
        if (currentPuzzle == null) return;
        
        // Create game history record
        GameHistory gameHistory = new GameHistory(
            currentPlayerName,
            currentPuzzle.getDifficulty(),
            currentPuzzle.getElapsedTime(),
            movesCount,
            hintsUsed
        );
        
        // Save to database
        boolean saved = gameDAO.saveGameHistory(gameHistory);
        if (saved) {
            gamePanel.updateStatus("Game completed and saved to history!");
        } else {
            gamePanel.updateStatus("Game completed! (Warning: Could not save to database)");
        }
        
        // Show completion statistics
        showCompletionStats(gameHistory);
    }
    
    /**
     * Shows completion statistics dialog
     * @param gameHistory The completed game history
     */
    private void showCompletionStats(GameHistory gameHistory) {
        String message = String.format(
            "Congratulations, %s!\n\n" +
            "Difficulty: %s\n" +
            "Completion Time: %s\n" +
            "Moves Made: %d\n" +
            "Hints Used: %d",
            currentPlayerName,
            gameHistory.getDifficultyString(),
            gameHistory.getFormattedTime(),
            gameHistory.getMovesCount(),
            gameHistory.getHintsUsed()
        );
        
        JOptionPane.showMessageDialog(gamePanel, message, "Game Completed!", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Saves the current game
     */
    public void saveGame() {
        if (currentPuzzle == null) {
            JOptionPane.showMessageDialog(gamePanel, "No active game to save.", 
                                        "Save Game", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (currentPuzzle.isCompleted()) {
            JOptionPane.showMessageDialog(gamePanel, "Cannot save a completed game.", 
                                        "Save Game", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get save name from user
        String saveName = JOptionPane.showInputDialog(gamePanel, 
            "Enter a name for this saved game:", "Save Game", JOptionPane.QUESTION_MESSAGE);
            
        if (saveName != null && !saveName.trim().isEmpty()) {
            // Create saved game object
            SavedGame savedGame = new SavedGame(
                currentPlayerName,
                saveName.trim(),
                SavedGame.gridToString(currentPuzzle.getGrid()),
                SavedGame.gridToString(currentPuzzle.getGrid()), // This should be original grid
                currentPuzzle.getDifficulty(),
                currentPuzzle.getElapsedTime()
            );
            
            // Save to database
            boolean saved = gameDAO.saveGame(savedGame);
            if (saved) {
                gamePanel.updateStatus("Game saved successfully as '" + saveName + "'");
            } else {
                JOptionPane.showMessageDialog(gamePanel, "Failed to save game. Please try again.", 
                                            "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Loads a saved game
     */
    public void loadGame() {
        // Get list of saved games for current player
        List<SavedGame> savedGames = gameDAO.getSavedGames(currentPlayerName);
        
        if (savedGames.isEmpty()) {
            JOptionPane.showMessageDialog(gamePanel, "No saved games found for " + currentPlayerName, 
                                        "Load Game", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create selection dialog
        String[] gameNames = savedGames.stream()
            .map(game -> String.format("%s (%s - %s)", 
                game.getGameName(), 
                game.getDifficultyString(), 
                game.getFormattedElapsedTime()))
            .toArray(String[]::new);
            
        String selected = (String) JOptionPane.showInputDialog(
            gamePanel,
            "Select a game to load:",
            "Load Game",
            JOptionPane.QUESTION_MESSAGE,
            null,
            gameNames,
            gameNames[0]
        );
        
        if (selected != null) {
            // Find the selected game
            int selectedIndex = -1;
            for (int i = 0; i < gameNames.length; i++) {
                if (gameNames[i].equals(selected)) {
                    selectedIndex = i;
                    break;
                }
            }
            
            if (selectedIndex >= 0) {
                SavedGame savedGame = savedGames.get(selectedIndex);
                loadSavedGame(savedGame);
            }
        }
    }
    
    /**
     * Loads a specific saved game
     * @param savedGame The saved game to load
     */
    private void loadSavedGame(SavedGame savedGame) {
        try {
            // Create new puzzle with saved data
            int[][] gridData = savedGame.parseGridData();
            int[][] originalGrid = savedGame.parseOriginalGrid();
            
            currentPuzzle = new SudokuPuzzle(originalGrid);
            currentPuzzle.setGrid(gridData);
            
            // Reset counters (could be enhanced to save/load these too)
            movesCount = 0;
            hintsUsed = 0;
            
            // Update game panel
            if (gamePanel != null) {
                gamePanel.setPuzzle(currentPuzzle);
                gamePanel.updateStatus("Game '" + savedGame.getGameName() + "' loaded successfully!");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(gamePanel, 
                "Error loading saved game: " + e.getMessage(), 
                "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Shows player statistics
     */
    public void showPlayerStats() {
        var stats = gameDAO.getPlayerStats(currentPlayerName);
        if (stats == null) {
            JOptionPane.showMessageDialog(gamePanel, 
                "No statistics found for " + currentPlayerName, 
                "Player Statistics", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String message = String.format(
            "Player Statistics for %s\n\n" +
            "Games Played: %d\n" +
            "Games Won: %d\n" +
            "Win Rate: %.1f%%\n" +
            "Best Time: %s\n" +
            "Average Time: %s\n" +
            "Total Play Time: %s",
            stats.getPlayerName(),
            stats.getGamesPlayed(),
            stats.getGamesWon(),
            stats.getWinRate(),
            stats.getFormattedBestTime(),
            stats.getFormattedAverageTime(),
            stats.getFormattedTotalTime()
        );
        
        JOptionPane.showMessageDialog(gamePanel, message, "Player Statistics", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Shows game history
     */
    public void showGameHistory() {
        List<GameHistory> history = gameDAO.getGameHistory(currentPlayerName, 10);
        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(gamePanel, 
                "No game history found for " + currentPlayerName, 
                "Game History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder message = new StringBuilder("Recent Games for " + currentPlayerName + ":\n\n");
        for (GameHistory game : history) {
            message.append(String.format("%s - %s in %s\n", 
                game.getDifficultyString(), 
                game.getFormattedTime(),
                game.getDatePlayed().toString().substring(0, 16)));
        }
        
        JOptionPane.showMessageDialog(gamePanel, message.toString(), "Game History", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Shows leaderboard
     */
    public void showLeaderboard() {
        var topPlayers = gameDAO.getTopPlayers(10);
        if (topPlayers.isEmpty()) {
            JOptionPane.showMessageDialog(gamePanel, "No leaderboard data available.", 
                                        "Leaderboard", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder message = new StringBuilder("Top Players (Best Times):\n\n");
        for (int i = 0; i < topPlayers.size(); i++) {
            var player = topPlayers.get(i);
            message.append(String.format("%d. %s - %s\n", 
                i + 1, 
                player.getPlayerName(), 
                player.getFormattedBestTime()));
        }
        
        JOptionPane.showMessageDialog(gamePanel, message.toString(), "Leaderboard", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Increments the moves counter
     */
    public void incrementMoves() {
        movesCount++;
    }
    
    /**
     * Increments the hints counter
     */
    public void incrementHints() {
        hintsUsed++;
    }
    
    /**
     * Gets the current puzzle
     * @return Current puzzle instance
     */
    public SudokuPuzzle getCurrentPuzzle() {
        return currentPuzzle;
    }
    
    /**
     * Gets the current player name
     * @return Current player name
     */
    public String getCurrentPlayerName() {
        return currentPlayerName;
    }
    
    /**
     * Converts difficulty string to level
     * @param difficulty Difficulty string
     * @return Difficulty level (1-3)
     */
    private int getDifficultyLevel(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy": return 1;
            case "medium": return 2;
            case "hard": return 3;
            default: return 1;
        }
    }
    
    /**
     * Converts difficulty level to string
     * @param level Difficulty level
     * @return Difficulty string
     */
    private String getDifficultyString(int level) {
        switch (level) {
            case 1: return "Easy";
            case 2: return "Medium";
            case 3: return "Hard";
            default: return "Easy";
        }
    }
    
    /**
     * Cleanup method to be called when application closes
     */
    public void cleanup() {
        if (gamePanel != null) {
            gamePanel.stopTimer();
        }
    }
}
