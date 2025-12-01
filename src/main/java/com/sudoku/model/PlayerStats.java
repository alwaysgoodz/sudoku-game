package com.sudoku.model;

import java.sql.Timestamp;

/**
 * Model class representing player statistics.
 * Demonstrates encapsulation and data management.
 */
public class PlayerStats {
    private String playerName;
    private int gamesPlayed;
    private int gamesWon;
    private long totalTime;
    private long bestTime;
    private long averageTime;
    private Timestamp lastPlayed;
    
    /**
     * Constructor for PlayerStats
     * @param playerName Name of the player
     */
    public PlayerStats(String playerName) {
        this.playerName = playerName;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.totalTime = 0;
        this.bestTime = 0;
        this.averageTime = 0;
        this.lastPlayed = new Timestamp(System.currentTimeMillis());
    }
    
    // Getters and Setters
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public int getGamesPlayed() {
        return gamesPlayed;
    }
    
    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }
    
    public int getGamesWon() {
        return gamesWon;
    }
    
    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }
    
    public long getTotalTime() {
        return totalTime;
    }
    
    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
        updateAverageTime();
    }
    
    public long getBestTime() {
        return bestTime;
    }
    
    public void setBestTime(long bestTime) {
        this.bestTime = bestTime;
    }
    
    public long getAverageTime() {
        return averageTime;
    }
    
    public void setAverageTime(long averageTime) {
        this.averageTime = averageTime;
    }
    
    public Timestamp getLastPlayed() {
        return lastPlayed;
    }
    
    public void setLastPlayed(Timestamp lastPlayed) {
        this.lastPlayed = lastPlayed;
    }
    
    // Business logic methods
    public void incrementGamesPlayed() {
        this.gamesPlayed++;
        this.lastPlayed = new Timestamp(System.currentTimeMillis());
    }
    
    public void incrementGamesWon() {
        this.gamesWon++;
    }
    
    public void addTime(long gameTime) {
        this.totalTime += gameTime;
        updateAverageTime();
    }
    
    private void updateAverageTime() {
        if (gamesWon > 0) {
            this.averageTime = totalTime / gamesWon;
        }
    }
    
    public double getWinRate() {
        if (gamesPlayed == 0) {
            return 0.0;
        }
        return (double) gamesWon / gamesPlayed * 100.0;
    }
    
    public String getFormattedBestTime() {
        return formatTime(bestTime);
    }
    
    public String getFormattedAverageTime() {
        return formatTime(averageTime);
    }
    
    public String getFormattedTotalTime() {
        return formatTime(totalTime);
    }
    
    private String formatTime(long timeInMillis) {
        if (timeInMillis == 0) {
            return "N/A";
        }
        
        long seconds = timeInMillis / 1000;
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
    
    @Override
    public String toString() {
        return String.format("PlayerStats{name='%s', played=%d, won=%d, winRate=%.1f%%, bestTime=%s}",
                           playerName, gamesPlayed, gamesWon, getWinRate(), getFormattedBestTime());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        PlayerStats that = (PlayerStats) obj;
        return playerName != null ? playerName.equals(that.playerName) : that.playerName == null;
    }
    
    @Override
    public int hashCode() {
        return playerName != null ? playerName.hashCode() : 0;
    }
}
