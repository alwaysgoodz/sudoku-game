package com.sudoku.model;

/**
 * Represents a hint for the puzzle, demonstrating encapsulation.
 * This class encapsulates hint information with proper access control.
 */
public class Hint {
    private final int row;
    private final int col;
    private final int value;
    private final String explanation;
    
    /**
     * Constructor for Hint
     * @param row The row position of the hint
     * @param col The column position of the hint
     * @param value The suggested value
     * @param explanation The explanation for this hint
     */
    public Hint(int row, int col, int value, String explanation) {
        this.row = row;
        this.col = col;
        this.value = value;
        this.explanation = explanation;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public int getValue() {
        return value;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    @Override
    public String toString() {
        return String.format("Hint: Place %d at position (%d, %d) - %s", 
                           value, row + 1, col + 1, explanation);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Hint hint = (Hint) obj;
        return row == hint.row && col == hint.col && value == hint.value;
    }
    
    @Override
    public int hashCode() {
        return row * 100 + col * 10 + value;
    }
}
