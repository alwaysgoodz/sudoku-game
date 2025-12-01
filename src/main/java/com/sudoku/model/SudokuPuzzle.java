package com.sudoku.model;

import java.util.*;

/**
 * Concrete implementation of Sudoku puzzle, demonstrating inheritance and polymorphism.
 * This class extends Puzzle and implements Solvable interface.
 */
public class SudokuPuzzle extends Puzzle implements Solvable {
    private static final int SUDOKU_SIZE = 9;
    private static final int BOX_SIZE = 3;
    private int[][] originalGrid;
    private int difficulty;
    
    /**
     * Constructor for SudokuPuzzle
     */
    public SudokuPuzzle() {
        super(SUDOKU_SIZE);
        this.originalGrid = new int[SUDOKU_SIZE][SUDOKU_SIZE];
    }
    
    /**
     * Constructor with initial grid
     * @param initialGrid The initial puzzle grid
     */
    public SudokuPuzzle(int[][] initialGrid) {
        super(SUDOKU_SIZE);
        if (initialGrid.length != SUDOKU_SIZE || initialGrid[0].length != SUDOKU_SIZE) {
            throw new IllegalArgumentException("Grid must be 9x9 for Sudoku");
        }
        setGrid(initialGrid);
        this.originalGrid = deepCopy(initialGrid);
    }
    
    @Override
    public boolean isValidMove(int row, int col, int value) {
        if (!isValidPosition(row, col) || value < 1 || value > 9) {
            return false;
        }
        
        // Check if the cell is part of the original puzzle
        if (originalGrid[row][col] != 0) {
            return false;
        }
        
        // Temporarily place the value to check validity
        int originalValue = grid[row][col];
        grid[row][col] = value;
        
        boolean valid = isValidSudokuState(row, col, value);
        
        // Restore original value
        grid[row][col] = originalValue;
        
        return valid;
    }
    
    private boolean isValidSudokuState(int row, int col, int value) {
        // Check row
        for (int c = 0; c < SUDOKU_SIZE; c++) {
            if (c != col && grid[row][c] == value) {
                return false;
            }
        }
        
        // Check column
        for (int r = 0; r < SUDOKU_SIZE; r++) {
            if (r != row && grid[r][col] == value) {
                return false;
            }
        }
        
        // Check 3x3 box
        int boxRow = (row / BOX_SIZE) * BOX_SIZE;
        int boxCol = (col / BOX_SIZE) * BOX_SIZE;
        
        for (int r = boxRow; r < boxRow + BOX_SIZE; r++) {
            for (int c = boxCol; c < boxCol + BOX_SIZE; c++) {
                if ((r != row || c != col) && grid[r][c] == value) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    @Override
    public boolean solve() {
        return solveSudoku(0, 0);
    }
    
    private boolean solveSudoku(int row, int col) {
        if (row == SUDOKU_SIZE) {
            return true; // Solved
        }
        
        int nextRow = (col == SUDOKU_SIZE - 1) ? row + 1 : row;
        int nextCol = (col == SUDOKU_SIZE - 1) ? 0 : col + 1;
        
        if (grid[row][col] != 0) {
            return solveSudoku(nextRow, nextCol);
        }
        
        for (int value = 1; value <= 9; value++) {
            if (isValidSudokuState(row, col, value)) {
                grid[row][col] = value;
                
                if (solveSudoku(nextRow, nextCol)) {
                    return true;
                }
                
                grid[row][col] = 0; // Backtrack
            }
        }
        
        return false;
    }
    
    @Override
    public void generatePuzzle(int difficulty) {
        this.difficulty = difficulty;
        
        // Clear the grid
        for (int i = 0; i < SUDOKU_SIZE; i++) {
            Arrays.fill(grid[i], 0);
        }
        
        // Fill diagonal boxes first
        fillDiagonalBoxes();
        
        // Fill remaining cells
        solveSudoku(0, 0);
        
        // Remove cells based on difficulty
        removeCells(difficulty);
        
        // Save the original grid
        this.originalGrid = deepCopy(grid);
    }
    
    private void fillDiagonalBoxes() {
        for (int box = 0; box < SUDOKU_SIZE; box += BOX_SIZE) {
            fillBox(box, box);
        }
    }
    
    private void fillBox(int row, int col) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Collections.shuffle(numbers);
        
        int index = 0;
        for (int r = row; r < row + BOX_SIZE; r++) {
            for (int c = col; c < col + BOX_SIZE; c++) {
                grid[r][c] = numbers.get(index++);
            }
        }
    }
    
    private void removeCells(int difficulty) {
        int cellsToRemove;
        switch (difficulty) {
            case 1: cellsToRemove = 40; break; // Easy
            case 2: cellsToRemove = 50; break; // Medium
            case 3: cellsToRemove = 60; break; // Hard
            default: cellsToRemove = 45; break;
        }
        
        Random random = new Random();
        int removed = 0;
        
        while (removed < cellsToRemove) {
            int row = random.nextInt(SUDOKU_SIZE);
            int col = random.nextInt(SUDOKU_SIZE);
            
            if (grid[row][col] != 0) {
                int backup = grid[row][col];
                grid[row][col] = 0;
                
                // Check if puzzle still has unique solution
                if (hasUniqueSolution()) {
                    removed++;
                } else {
                    grid[row][col] = backup; // Restore if no unique solution
                }
            }
        }
    }
    
    private boolean hasUniqueSolution() {
        int[][] tempGrid = deepCopy(grid);
        int solutions = countSolutions(tempGrid, 0, 0);
        return solutions == 1;
    }
    
    private int countSolutions(int[][] tempGrid, int row, int col) {
        if (row == SUDOKU_SIZE) {
            return 1;
        }
        
        int nextRow = (col == SUDOKU_SIZE - 1) ? row + 1 : row;
        int nextCol = (col == SUDOKU_SIZE - 1) ? 0 : col + 1;
        
        if (tempGrid[row][col] != 0) {
            return countSolutions(tempGrid, nextRow, nextCol);
        }
        
        int solutions = 0;
        for (int value = 1; value <= 9 && solutions < 2; value++) {
            if (isValidPlacement(tempGrid, row, col, value)) {
                tempGrid[row][col] = value;
                solutions += countSolutions(tempGrid, nextRow, nextCol);
                tempGrid[row][col] = 0;
            }
        }
        
        return solutions;
    }
    
    private boolean isValidPlacement(int[][] tempGrid, int row, int col, int value) {
        // Check row
        for (int c = 0; c < SUDOKU_SIZE; c++) {
            if (tempGrid[row][c] == value) return false;
        }
        
        // Check column
        for (int r = 0; r < SUDOKU_SIZE; r++) {
            if (tempGrid[r][col] == value) return false;
        }
        
        // Check box
        int boxRow = (row / BOX_SIZE) * BOX_SIZE;
        int boxCol = (col / BOX_SIZE) * BOX_SIZE;
        
        for (int r = boxRow; r < boxRow + BOX_SIZE; r++) {
            for (int c = boxCol; c < boxCol + BOX_SIZE; c++) {
                if (tempGrid[r][c] == value) return false;
            }
        }
        
        return true;
    }
    
    @Override
    public boolean isSolvable() {
        int[][] tempGrid = deepCopy(grid);
        return solveSudokuTemp(tempGrid, 0, 0);
    }
    
    private boolean solveSudokuTemp(int[][] tempGrid, int row, int col) {
        if (row == SUDOKU_SIZE) return true;
        
        int nextRow = (col == SUDOKU_SIZE - 1) ? row + 1 : row;
        int nextCol = (col == SUDOKU_SIZE - 1) ? 0 : col + 1;
        
        if (tempGrid[row][col] != 0) {
            return solveSudokuTemp(tempGrid, nextRow, nextCol);
        }
        
        for (int value = 1; value <= 9; value++) {
            if (isValidPlacement(tempGrid, row, col, value)) {
                tempGrid[row][col] = value;
                if (solveSudokuTemp(tempGrid, nextRow, nextCol)) {
                    return true;
                }
                tempGrid[row][col] = 0;
            }
        }
        
        return false;
    }
    
    @Override
    public Hint getHint() {
        // Find the first empty cell that can be filled with only one value
        for (int row = 0; row < SUDOKU_SIZE; row++) {
            for (int col = 0; col < SUDOKU_SIZE; col++) {
                if (grid[row][col] == 0) {
                    List<Integer> possibleValues = getPossibleValues(row, col);
                    if (possibleValues.size() == 1) {
                        return new Hint(row, col, possibleValues.get(0), 
                                      "Only possible value for this cell");
                    }
                }
            }
        }
        
        // If no obvious hint, find a cell with minimum possibilities
        int minPossibilities = 10;
        int hintRow = -1, hintCol = -1;
        
        for (int row = 0; row < SUDOKU_SIZE; row++) {
            for (int col = 0; col < SUDOKU_SIZE; col++) {
                if (grid[row][col] == 0) {
                    List<Integer> possibleValues = getPossibleValues(row, col);
                    if (possibleValues.size() < minPossibilities) {
                        minPossibilities = possibleValues.size();
                        hintRow = row;
                        hintCol = col;
                    }
                }
            }
        }
        
        if (hintRow != -1) {
            List<Integer> possibleValues = getPossibleValues(hintRow, hintCol);
            if (!possibleValues.isEmpty()) {
                return new Hint(hintRow, hintCol, possibleValues.get(0), 
                              "Try this value (one of " + possibleValues.size() + " possibilities)");
            }
        }
        
        return null;
    }
    
    private List<Integer> getPossibleValues(int row, int col) {
        List<Integer> possible = new ArrayList<>();
        for (int value = 1; value <= 9; value++) {
            if (isValidSudokuState(row, col, value)) {
                possible.add(value);
            }
        }
        return possible;
    }
    
    @Override
    public boolean isValid() {
        for (int row = 0; row < SUDOKU_SIZE; row++) {
            for (int col = 0; col < SUDOKU_SIZE; col++) {
                int value = grid[row][col];
                if (value != 0) {
                    grid[row][col] = 0; // Temporarily remove to check
                    if (!isValidSudokuState(row, col, value)) {
                        grid[row][col] = value; // Restore
                        return false;
                    }
                    grid[row][col] = value; // Restore
                }
            }
        }
        return true;
    }
    
    @Override
    public SudokuPuzzle createCopy() {
        SudokuPuzzle copy = new SudokuPuzzle();
        copy.grid = deepCopy(this.grid);
        copy.originalGrid = deepCopy(this.originalGrid);
        copy.difficulty = this.difficulty;
        copy.isCompleted = this.isCompleted;
        copy.startTime = this.startTime;
        copy.endTime = this.endTime;
        return copy;
    }
    
    private int[][] deepCopy(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }
    
    public boolean isOriginalCell(int row, int col) {
        return originalGrid[row][col] != 0;
    }
    
    public boolean isPuzzleComplete() {
        for (int row = 0; row < SUDOKU_SIZE; row++) {
            for (int col = 0; col < SUDOKU_SIZE; col++) {
                if (grid[row][col] == 0) {
                    return false;
                }
            }
        }
        return isValid();
    }
    
    public int getDifficulty() {
        return difficulty;
    }
    
    public void resetToOriginal() {
        this.grid = deepCopy(originalGrid);
        this.isCompleted = false;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
    }
}
