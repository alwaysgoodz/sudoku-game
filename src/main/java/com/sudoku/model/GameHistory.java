package com.sudoku.model;

import java.sql.Timestamp;

/**
 * Model class representing a completed game's history.
 * Demonstrates encapsulation with private fields and public accessors.
 */
public class GameHistory {
    private int id;
    private String playerName;
    private int difficulty;
    private long completionTime;
    private Timestamp datePlayed;
    private int movesCount;
    private int hintsUsed;
    
    /**
     * Constructor for GameHistory
     * @param playerName Name of the player
     * @param difficulty Difficulty level (1=Easy, 2=Medium, 3=Hard)
     * @param completionTime Time taken to complete in milliseconds
     * @param movesCount Number of moves made
     * @param hintsUsed Number of hints used
     */
    public GameHistory(String playerName, int difficulty, long completionTime, 
                      int movesCount, int hintsUsed) {
        this.playerName = playerName;
        this.difficulty = difficulty;
        this.completionTime = completionTime;
        this.movesCount = movesCount;
        this.hintsUsed = hintsUsed;
        this.datePlayed = new Timestamp(System.currentTimeMillis());
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
    
    public int getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    
    public long getCompletionTime() {
        return completionTime;
    }
    
    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }
    
    public Timestamp getDatePlayed() {
        return datePlayed;
    }
    
    public void setDatePlayed(Timestamp datePlayed) {
        this.datePlayed = datePlayed;
    }
    
    public int getMovesCount() {
        return movesCount;
    }
    
    public void setMovesCount(int movesCount) {
        this.movesCount = movesCount;
    }
    
    public int getHintsUsed() {
        return hintsUsed;
    }
    
    public void setHintsUsed(int hintsUsed) {
        this.hintsUsed = hintsUsed;
    }
    
    public String getDifficultyString() {
        switch (difficulty) {
            case 1: return "Easy";
            case 2: return "Medium";
            case 3: return "Hard";
            default: return "Unknown";
        }
    }
    
    public String getFormattedTime() {
        long seconds = completionTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    @Override
    public String toString() {
        return String.format("GameHistory{player='%s', difficulty=%s, time=%s, moves=%d, hints=%d}",
                           playerName, getDifficultyString(), getFormattedTime(), movesCount, hintsUsed);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        GameHistory that = (GameHistory) obj;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
