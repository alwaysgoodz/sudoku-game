package com.sudoku.model;

/**
 * Abstract base class for puzzle games demonstrating abstraction and inheritance.
 * This class provides common functionality for all puzzle types.
 */
public abstract class Puzzle {
    protected int[][] grid;
    protected int size;
    protected boolean isCompleted;
    protected long startTime;
    protected long endTime;
    
    /**
     * Constructor for Puzzle
     * @param size The size of the puzzle grid
     */
    public Puzzle(int size) {
        this.size = size;
        this.grid = new int[size][size];
        this.isCompleted = false;
        this.startTime = System.currentTimeMillis();
    }
    
    // Abstract methods that must be implemented by subclasses
    public abstract boolean isValidMove(int row, int col, int value);
    public abstract boolean solve();
    public abstract void generatePuzzle(int difficulty);
    public abstract Puzzle createCopy();
    
    // Concrete methods providing common functionality
    public int[][] getGrid() {
        return grid.clone();
    }
    
    public void setGrid(int[][] grid) {
        if (grid.length == size && grid[0].length == size) {
            this.grid = grid;
        } else {
            throw new IllegalArgumentException("Grid size must be " + size + "x" + size);
        }
    }
    
    public int getSize() {
        return size;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
        if (completed) {
            this.endTime = System.currentTimeMillis();
        }
    }
    
    public long getElapsedTime() {
        if (endTime > 0) {
            return endTime - startTime;
        }
        return System.currentTimeMillis() - startTime;
    }
    
    public int getValue(int row, int col) {
        if (isValidPosition(row, col)) {
            return grid[row][col];
        }
        throw new IndexOutOfBoundsException("Invalid position: (" + row + ", " + col + ")");
    }
    
    public void setValue(int row, int col, int value) {
        if (isValidPosition(row, col)) {
            grid[row][col] = value;
        } else {
            throw new IndexOutOfBoundsException("Invalid position: (" + row + ", " + col + ")");
        }
    }
    
    protected boolean isValidPosition(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }
    
    public void clearCell(int row, int col) {
        setValue(row, col, 0);
    }
    
    public boolean isEmpty(int row, int col) {
        return getValue(row, col) == 0;
    }
}
