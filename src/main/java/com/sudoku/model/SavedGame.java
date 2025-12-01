package com.sudoku.model;

import java.sql.Timestamp;

/**
 * Model class representing a saved game state.
 * Demonstrates encapsulation and data persistence.
 */
public class SavedGame {
    private int id;
    private String playerName;
    private String gameName;
    private String gridData;
    private String originalGrid;
    private int difficulty;
    private long elapsedTime;
    private Timestamp dateSaved;
    
    /**
     * Default constructor for SavedGame
     */
    public SavedGame() {
        this.dateSaved = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Constructor for SavedGame
     * @param playerName Name of the player
     * @param gameName Name given to the saved game
     * @param gridData Current state of the puzzle grid
     * @param originalGrid Original puzzle grid
     * @param difficulty Difficulty level
     * @param elapsedTime Time elapsed when saved
     */
    public SavedGame(String playerName, String gameName, String gridData, 
                    String originalGrid, int difficulty, long elapsedTime) {
        this.playerName = playerName;
        this.gameName = gameName;
        this.gridData = gridData;
        this.originalGrid = originalGrid;
        this.difficulty = difficulty;
        this.elapsedTime = elapsedTime;
        this.dateSaved = new Timestamp(System.currentTimeMillis());
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public String getGameName() {
        return gameName;
    }
    
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
    
    public String getGridData() {
        return gridData;
    }
    
    public void setGridData(String gridData) {
        this.gridData = gridData;
    }
    
    public String getOriginalGrid() {
        return originalGrid;
    }
    
    public void setOriginalGrid(String originalGrid) {
        this.originalGrid = originalGrid;
    }
    
    public int getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    
    public long getElapsedTime() {
        return elapsedTime;
    }
    
    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
    
    public Timestamp getDateSaved() {
        return dateSaved;
    }
    
    public void setDateSaved(Timestamp dateSaved) {
        this.dateSaved = dateSaved;
    }
    
    // Utility methods
    public String getDifficultyString() {
        switch (difficulty) {
            case 1: return "Easy";
            case 2: return "Medium";
            case 3: return "Hard";
            default: return "Unknown";
        }
    }
    
    public String getFormattedElapsedTime() {
        long seconds = elapsedTime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        seconds = seconds % 60;
        minutes = minutes % 60;
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
    
    /**
     * Converts grid data string back to 2D array
     * @return 2D integer array representing the grid
     */
    public int[][] parseGridData() {
        if (gridData == null || gridData.isEmpty()) {
            return new int[9][9];
        }
        
        int[][] grid = new int[9][9];
        String[] values = gridData.split(",");
        
        for (int i = 0; i < values.length && i < 81; i++) {
            int row = i / 9;
            int col = i % 9;
            try {
                grid[row][col] = Integer.parseInt(values[i].trim());
            } catch (NumberFormatException e) {
                grid[row][col] = 0;
            }
        }
        
        return grid;
    }
    
    /**
     * Converts original grid string back to 2D array
     * @return 2D integer array representing the original grid
     */
    public int[][] parseOriginalGrid() {
        if (originalGrid == null || originalGrid.isEmpty()) {
            return new int[9][9];
        }
        
        int[][] grid = new int[9][9];
        String[] values = originalGrid.split(",");
        
        for (int i = 0; i < values.length && i < 81; i++) {
            int row = i / 9;
            int col = i % 9;
            try {
                grid[row][col] = Integer.parseInt(values[i].trim());
            } catch (NumberFormatException e) {
                grid[row][col] = 0;
            }
        }
        
        return grid;
    }
    
    /**
     * Converts a 2D grid array to string format for storage
     * @param grid The 2D grid array
     * @return String representation of the grid
     */
    public static String gridToString(int[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                sb.append(grid[row][col]);
                if (row < 8 || col < 8) {
                    sb.append(",");
                }
            }
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("SavedGame{id=%d, player='%s', name='%s', difficulty=%s, elapsed=%s}",
                           id, playerName, gameName, getDifficultyString(), getFormattedElapsedTime());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        SavedGame savedGame = (SavedGame) obj;
        return id == savedGame.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
