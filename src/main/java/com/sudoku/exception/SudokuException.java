package com.sudoku.exception;

/**
 * Custom exception class for Sudoku-specific errors.
 * Demonstrates custom exception handling and error management.
 */
public class SudokuException extends Exception {
    
    private final ErrorCode errorCode;
    
    /**
     * Enumeration of possible error codes
     */
    public enum ErrorCode {
        INVALID_GRID_SIZE("Invalid grid size"),
        INVALID_CELL_VALUE("Invalid cell value"),
        INVALID_POSITION("Invalid cell position"),
        PUZZLE_UNSOLVABLE("Puzzle is unsolvable"),
        DATABASE_ERROR("Database operation failed"),
        SAVE_LOAD_ERROR("Save/Load operation failed"),
        VALIDATION_ERROR("Validation failed");
        
        private final String description;
        
        ErrorCode(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Constructor with error code and message
     * @param errorCode The specific error code
     * @param message Detailed error message
     */
    public SudokuException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * Constructor with error code, message, and cause
     * @param errorCode The specific error code
     * @param message Detailed error message
     * @param cause The underlying cause
     */
    public SudokuException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * Gets the error code
     * @return The error code
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    /**
     * Gets a user-friendly error message
     * @return User-friendly error message
     */
    public String getUserMessage() {
        return errorCode.getDescription() + ": " + getMessage();
    }
    
    @Override
    public String toString() {
        return String.format("SudokuException[%s]: %s", errorCode, getMessage());
    }
}
