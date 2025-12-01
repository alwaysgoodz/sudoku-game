package com.sudoku.util;

import com.sudoku.exception.SudokuException;

/**
 * Utility class for validation operations.
 * Demonstrates utility pattern and input validation.
 */
public final class ValidationUtils {
    
    // Private constructor to prevent instantiation
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Validates Sudoku grid position
     * @param row Row index
     * @param col Column index
     * @throws SudokuException if position is invalid
     */
    public static void validatePosition(int row, int col) throws SudokuException {
        if (row < 0 || row >= 9) {
            throw new SudokuException(
                SudokuException.ErrorCode.INVALID_POSITION,
                "Row must be between 0 and 8, got: " + row
            );
        }
        if (col < 0 || col >= 9) {
            throw new SudokuException(
                SudokuException.ErrorCode.INVALID_POSITION,
                "Column must be between 0 and 8, got: " + col
            );
        }
    }
    
    /**
     * Validates Sudoku cell value
     * @param value Cell value to validate
     * @throws SudokuException if value is invalid
     */
    public static void validateCellValue(int value) throws SudokuException {
        if (value < 0 || value > 9) {
            throw new SudokuException(
                SudokuException.ErrorCode.INVALID_CELL_VALUE,
                "Cell value must be between 0 and 9, got: " + value
            );
        }
    }
    
    /**
     * Validates Sudoku grid size
     * @param grid Grid to validate
     * @throws SudokuException if grid size is invalid
     */
    public static void validateGridSize(int[][] grid) throws SudokuException {
        if (grid == null) {
            throw new SudokuException(
                SudokuException.ErrorCode.INVALID_GRID_SIZE,
                "Grid cannot be null"
            );
        }
        
        if (grid.length != 9) {
            throw new SudokuException(
                SudokuException.ErrorCode.INVALID_GRID_SIZE,
                "Grid must have 9 rows, got: " + grid.length
            );
        }
        
        for (int i = 0; i < grid.length; i++) {
            if (grid[i] == null) {
                throw new SudokuException(
                    SudokuException.ErrorCode.INVALID_GRID_SIZE,
                    "Grid row " + i + " cannot be null"
                );
            }
            if (grid[i].length != 9) {
                throw new SudokuException(
                    SudokuException.ErrorCode.INVALID_GRID_SIZE,
                    "Grid row " + i + " must have 9 columns, got: " + grid[i].length
                );
            }
        }
    }
    
    /**
     * Validates all cell values in a grid
     * @param grid Grid to validate
     * @throws SudokuException if any cell value is invalid
     */
    public static void validateGridValues(int[][] grid) throws SudokuException {
        validateGridSize(grid);
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                validateCellValue(grid[row][col]);
            }
        }
    }
    
    /**
     * Validates player name
     * @param playerName Player name to validate
     * @throws SudokuException if player name is invalid
     */
    public static void validatePlayerName(String playerName) throws SudokuException {
        if (playerName == null || playerName.trim().isEmpty()) {
            throw new SudokuException(
                SudokuException.ErrorCode.VALIDATION_ERROR,
                "Player name cannot be null or empty"
            );
        }
        
        if (playerName.trim().length() > 100) {
            throw new SudokuException(
                SudokuException.ErrorCode.VALIDATION_ERROR,
                "Player name cannot exceed 100 characters"
            );
        }
        
        // Check for invalid characters
        if (!playerName.matches("^[a-zA-Z0-9\\s_-]+$")) {
            throw new SudokuException(
                SudokuException.ErrorCode.VALIDATION_ERROR,
                "Player name contains invalid characters. Only letters, numbers, spaces, underscores, and hyphens are allowed."
            );
        }
    }
    
    /**
     * Validates difficulty level
     * @param difficulty Difficulty level to validate
     * @throws SudokuException if difficulty is invalid
     */
    public static void validateDifficulty(int difficulty) throws SudokuException {
        if (difficulty < 1 || difficulty > 3) {
            throw new SudokuException(
                SudokuException.ErrorCode.VALIDATION_ERROR,
                "Difficulty must be 1 (Easy), 2 (Medium), or 3 (Hard), got: " + difficulty
            );
        }
    }
    
    /**
     * Validates game name for saving
     * @param gameName Game name to validate
     * @throws SudokuException if game name is invalid
     */
    public static void validateGameName(String gameName) throws SudokuException {
        if (gameName == null || gameName.trim().isEmpty()) {
            throw new SudokuException(
                SudokuException.ErrorCode.VALIDATION_ERROR,
                "Game name cannot be null or empty"
            );
        }
        
        if (gameName.trim().length() > 100) {
            throw new SudokuException(
                SudokuException.ErrorCode.VALIDATION_ERROR,
                "Game name cannot exceed 100 characters"
            );
        }
        
        // Check for invalid characters that might cause database issues
        if (gameName.contains("'") || gameName.contains("\"") || gameName.contains(";")) {
            throw new SudokuException(
                SudokuException.ErrorCode.VALIDATION_ERROR,
                "Game name contains invalid characters (quotes or semicolons)"
            );
        }
    }
    
    /**
     * Validates time value
     * @param time Time value in milliseconds
     * @throws SudokuException if time is invalid
     */
    public static void validateTime(long time) throws SudokuException {
        if (time < 0) {
            throw new SudokuException(
                SudokuException.ErrorCode.VALIDATION_ERROR,
                "Time cannot be negative, got: " + time
            );
        }
        
        // Reasonable upper limit (24 hours in milliseconds)
        long maxTime = 24 * 60 * 60 * 1000L;
        if (time > maxTime) {
            throw new SudokuException(
                SudokuException.ErrorCode.VALIDATION_ERROR,
                "Time exceeds reasonable limit (24 hours), got: " + time
            );
        }
    }
    
    /**
     * Validates move count
     * @param moves Number of moves
     * @throws SudokuException if move count is invalid
     */
    public static void validateMoveCount(int moves) throws SudokuException {
        if (moves < 0) {
            throw new SudokuException(
                SudokuException.ErrorCode.VALIDATION_ERROR,
                "Move count cannot be negative, got: " + moves
            );
        }
        
        // Reasonable upper limit
        if (moves > 10000) {
            throw new SudokuException(
                SudokuException.ErrorCode.VALIDATION_ERROR,
                "Move count exceeds reasonable limit, got: " + moves
            );
        }
    }
    
    /**
     * Validates hint count
     * @param hints Number of hints used
     * @throws SudokuException if hint count is invalid
     */
    public static void validateHintCount(int hints) throws SudokuException {
        if (hints < 0) {
            throw new SudokuException(
                SudokuException.ErrorCode.VALIDATION_ERROR,
                "Hint count cannot be negative, got: " + hints
            );
        }
        
        // Reasonable upper limit
        if (hints > 81) {
            throw new SudokuException(
                SudokuException.ErrorCode.VALIDATION_ERROR,
                "Hint count exceeds maximum possible (81), got: " + hints
            );
        }
    }
    
    /**
     * Checks if a string is null or empty
     * @param str String to check
     * @return true if string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Sanitizes a string for database storage
     * @param input Input string
     * @return Sanitized string
     */
    public static String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        
        // Trim whitespace and replace multiple spaces with single space
        return input.trim().replaceAll("\\s+", " ");
    }
}
