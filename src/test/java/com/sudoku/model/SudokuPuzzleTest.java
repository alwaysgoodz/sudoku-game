package com.sudoku.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SudokuPuzzle class.
 * Demonstrates comprehensive testing of core game logic.
 */
class SudokuPuzzleTest {
    
    private SudokuPuzzle puzzle;
    private int[][] validGrid;
    private int[][] invalidGrid;
    
    @BeforeEach
    void setUp() {
        puzzle = new SudokuPuzzle();
        
        // Create a valid complete Sudoku grid for testing
        validGrid = new int[][] {
            {5, 3, 4, 6, 7, 8, 9, 1, 2},
            {6, 7, 2, 1, 9, 5, 3, 4, 8},
            {1, 9, 8, 3, 4, 2, 5, 6, 7},
            {8, 5, 9, 7, 6, 1, 4, 2, 3},
            {4, 2, 6, 8, 5, 3, 7, 9, 1},
            {7, 1, 3, 9, 2, 4, 8, 5, 6},
            {9, 6, 1, 5, 3, 7, 2, 8, 4},
            {2, 8, 7, 4, 1, 9, 6, 3, 5},
            {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };
        
        // Create an invalid grid (duplicate 5 in first row)
        invalidGrid = new int[][] {
            {5, 3, 4, 6, 7, 8, 9, 1, 5}, // Invalid: two 5s
            {6, 7, 2, 1, 9, 5, 3, 4, 8},
            {1, 9, 8, 3, 4, 2, 5, 6, 7},
            {8, 5, 9, 7, 6, 1, 4, 2, 3},
            {4, 2, 6, 8, 5, 3, 7, 9, 1},
            {7, 1, 3, 9, 2, 4, 8, 5, 6},
            {9, 6, 1, 5, 3, 7, 2, 8, 4},
            {2, 8, 7, 4, 1, 9, 6, 3, 5},
            {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };
    }
    
    @Test
    @DisplayName("Test puzzle initialization with default constructor")
    void testDefaultConstructor() {
        assertNotNull(puzzle);
        assertEquals(9, puzzle.getSize());
        assertFalse(puzzle.isCompleted());
        
        // Check that grid is initially empty
        int[][] grid = puzzle.getGrid();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                assertEquals(0, grid[row][col], 
                    "Cell at (" + row + ", " + col + ") should be empty");
            }
        }
    }
    
    @Test
    @DisplayName("Test puzzle initialization with valid grid")
    void testConstructorWithValidGrid() {
        SudokuPuzzle validPuzzle = new SudokuPuzzle(validGrid);
        assertNotNull(validPuzzle);
        assertEquals(9, validPuzzle.getSize());
        
        int[][] resultGrid = validPuzzle.getGrid();
        assertArrayEquals(validGrid, resultGrid);
    }
    
    @Test
    @DisplayName("Test constructor with invalid grid size throws exception")
    void testConstructorWithInvalidGridSize() {
        int[][] smallGrid = new int[5][5];
        
        assertThrows(IllegalArgumentException.class, () -> {
            new SudokuPuzzle(smallGrid);
        }, "Should throw exception for invalid grid size");
    }
    
    @Test
    @DisplayName("Test valid move validation")
    void testValidMoveValidation() {
        // Create a puzzle with some empty cells
        int[][] partialGrid = new int[9][9];
        partialGrid[0][0] = 5; // Place 5 at (0,0)
        
        SudokuPuzzle partialPuzzle = new SudokuPuzzle(partialGrid);
        
        // Test valid moves
        assertTrue(partialPuzzle.isValidMove(0, 1, 3), 
            "Should allow placing 3 at (0,1)");
        assertTrue(partialPuzzle.isValidMove(1, 0, 6), 
            "Should allow placing 6 at (1,0)");
        
        // Test invalid moves
        assertFalse(partialPuzzle.isValidMove(0, 1, 5), 
            "Should not allow placing 5 at (0,1) - conflicts with row");
        assertFalse(partialPuzzle.isValidMove(1, 0, 5), 
            "Should not allow placing 5 at (1,0) - conflicts with column");
        assertFalse(partialPuzzle.isValidMove(1, 1, 5), 
            "Should not allow placing 5 at (1,1) - conflicts with 3x3 box");
    }
    
    @Test
    @DisplayName("Test invalid move validation with out of bounds")
    void testInvalidMoveOutOfBounds() {
        assertFalse(puzzle.isValidMove(-1, 0, 5), 
            "Should reject negative row");
        assertFalse(puzzle.isValidMove(0, -1, 5), 
            "Should reject negative column");
        assertFalse(puzzle.isValidMove(9, 0, 5), 
            "Should reject row >= 9");
        assertFalse(puzzle.isValidMove(0, 9, 5), 
            "Should reject column >= 9");
        assertFalse(puzzle.isValidMove(0, 0, 0), 
            "Should reject value 0");
        assertFalse(puzzle.isValidMove(0, 0, 10), 
            "Should reject value > 9");
    }
    
    @Test
    @DisplayName("Test puzzle generation creates valid puzzle")
    void testPuzzleGeneration() {
        puzzle.generatePuzzle(1); // Easy difficulty
        
        // Check that puzzle is valid
        assertTrue(puzzle.isValid(), "Generated puzzle should be valid");
        assertTrue(puzzle.isSolvable(), "Generated puzzle should be solvable");
        
        // Check that some cells are empty (it's a puzzle, not complete)
        int[][] grid = puzzle.getGrid();
        int emptyCells = 0;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (grid[row][col] == 0) {
                    emptyCells++;
                }
            }
        }
        assertTrue(emptyCells > 0, "Puzzle should have empty cells");
    }
    
    @Test
    @DisplayName("Test puzzle solving functionality")
    void testPuzzleSolving() {
        // Create a simple puzzle with one missing number
        int[][] almostComplete = validGrid.clone();
        for (int i = 0; i < almostComplete.length; i++) {
            almostComplete[i] = validGrid[i].clone();
        }
        almostComplete[8][8] = 0; // Remove last number
        
        SudokuPuzzle solvablePuzzle = new SudokuPuzzle(almostComplete);
        
        assertTrue(solvablePuzzle.solve(), "Should be able to solve the puzzle");
        assertTrue(solvablePuzzle.isValid(), "Solved puzzle should be valid");
        assertEquals(9, solvablePuzzle.getValue(8, 8), 
            "Last cell should be filled with correct value");
    }
    
    @Test
    @DisplayName("Test hint functionality")
    void testHintFunctionality() {
        // Create a puzzle with one obvious move
        int[][] hintGrid = new int[9][9];
        // Fill most of first row except last cell
        for (int i = 0; i < 8; i++) {
            hintGrid[0][i] = i + 1;
        }
        
        SudokuPuzzle hintPuzzle = new SudokuPuzzle(hintGrid);
        Hint hint = hintPuzzle.getHint();
        
        assertNotNull(hint, "Should provide a hint");
        assertEquals(0, hint.getRow(), "Hint should be for first row");
        assertEquals(8, hint.getCol(), "Hint should be for last column");
        assertEquals(9, hint.getValue(), "Hint should suggest value 9");
    }
    
    @Test
    @DisplayName("Test puzzle completion detection")
    void testPuzzleCompletionDetection() {
        SudokuPuzzle completePuzzle = new SudokuPuzzle(validGrid);
        assertTrue(completePuzzle.isPuzzleComplete(), 
            "Valid complete grid should be detected as complete");
        
        SudokuPuzzle invalidPuzzle = new SudokuPuzzle(invalidGrid);
        assertFalse(invalidPuzzle.isPuzzleComplete(), 
            "Invalid grid should not be detected as complete");
    }
    
    @Test
    @DisplayName("Test puzzle validation")
    void testPuzzleValidation() {
        SudokuPuzzle validPuzzle = new SudokuPuzzle(validGrid);
        assertTrue(validPuzzle.isValid(), "Valid grid should pass validation");
        
        SudokuPuzzle invalidPuzzle = new SudokuPuzzle(invalidGrid);
        assertFalse(invalidPuzzle.isValid(), "Invalid grid should fail validation");
    }
    
    @Test
    @DisplayName("Test puzzle copy functionality")
    void testPuzzleCopy() {
        puzzle.generatePuzzle(2);
        SudokuPuzzle copy = puzzle.createCopy();
        
        assertNotNull(copy, "Copy should not be null");
        assertNotSame(puzzle, copy, "Copy should be a different object");
        assertArrayEquals(puzzle.getGrid(), copy.getGrid(), 
            "Copy should have same grid values");
        assertEquals(puzzle.getDifficulty(), copy.getDifficulty(), 
            "Copy should have same difficulty");
    }
    
    @Test
    @DisplayName("Test cell value operations")
    void testCellValueOperations() {
        // Test setting and getting values
        puzzle.setValue(0, 0, 5);
        assertEquals(5, puzzle.getValue(0, 0), "Should retrieve set value");
        
        // Test clearing cell
        puzzle.clearCell(0, 0);
        assertEquals(0, puzzle.getValue(0, 0), "Cleared cell should be 0");
        assertTrue(puzzle.isEmpty(0, 0), "Cleared cell should be empty");
        
        // Test bounds checking
        assertThrows(IndexOutOfBoundsException.class, () -> {
            puzzle.getValue(-1, 0);
        }, "Should throw exception for invalid position");
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            puzzle.setValue(9, 0, 5);
        }, "Should throw exception for invalid position");
    }
}
