package com.sudoku.model;

/**
 * Interface demonstrating abstraction for solvable puzzles.
 * This interface defines the contract for puzzle solving capabilities.
 */
public interface Solvable {
    /**
     * Attempts to solve the puzzle automatically
     * @return true if the puzzle was solved, false otherwise
     */
    boolean solve();
    
    /**
     * Checks if the current puzzle state is solvable
     * @return true if the puzzle can be solved, false otherwise
     */
    boolean isSolvable();
    
    /**
     * Gets a hint for the next move
     * @return a Hint object containing the suggested move, or null if no hint available
     */
    Hint getHint();
    
    /**
     * Validates the current state of the puzzle
     * @return true if the current state is valid, false otherwise
     */
    boolean isValid();
}
