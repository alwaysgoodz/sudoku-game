package com.sudoku.util;

import com.sudoku.exception.SudokuException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationUtils class.
 * Tests input validation functionality.
 */
class ValidationUtilsTest {
    
    @Test
    @DisplayName("Test valid grid position does not throw exception")
    void testValidGridPosition() {
        assertDoesNotThrow(() -> ValidationUtils.validatePosition(0, 0));
        assertDoesNotThrow(() -> ValidationUtils.validatePosition(8, 8));
        assertDoesNotThrow(() -> ValidationUtils.validatePosition(4, 4));
    }
    
    @Test
    @DisplayName("Test invalid grid position throws exception - negative values")
    void testInvalidGridPositionNegative() {
        assertThrows(SudokuException.class, () -> ValidationUtils.validatePosition(-1, 0));
        assertThrows(SudokuException.class, () -> ValidationUtils.validatePosition(0, -1));
        assertThrows(SudokuException.class, () -> ValidationUtils.validatePosition(-1, -1));
    }
    
    @Test
    @DisplayName("Test invalid grid position throws exception - out of bounds")
    void testInvalidGridPositionOutOfBounds() {
        assertThrows(SudokuException.class, () -> ValidationUtils.validatePosition(9, 0));
        assertThrows(SudokuException.class, () -> ValidationUtils.validatePosition(0, 9));
        assertThrows(SudokuException.class, () -> ValidationUtils.validatePosition(10, 10));
    }
    
    @Test
    @DisplayName("Test valid cell value does not throw exception")
    void testValidCellValue() {
        assertDoesNotThrow(() -> ValidationUtils.validateCellValue(0));
        assertDoesNotThrow(() -> ValidationUtils.validateCellValue(1));
        assertDoesNotThrow(() -> ValidationUtils.validateCellValue(9));
    }
    
    @Test
    @DisplayName("Test invalid cell value throws exception")
    void testInvalidCellValue() {
        assertThrows(SudokuException.class, () -> ValidationUtils.validateCellValue(-1));
        assertThrows(SudokuException.class, () -> ValidationUtils.validateCellValue(10));
        assertThrows(SudokuException.class, () -> ValidationUtils.validateCellValue(100));
    }
    
    @Test
    @DisplayName("Test valid player name does not throw exception")
    void testValidPlayerName() {
        assertDoesNotThrow(() -> ValidationUtils.validatePlayerName("Player1"));
        assertDoesNotThrow(() -> ValidationUtils.validatePlayerName("John"));
        assertDoesNotThrow(() -> ValidationUtils.validatePlayerName("Player_123"));
    }
    
    @Test
    @DisplayName("Test invalid player name throws exception - null or empty")
    void testInvalidPlayerNameEmpty() {
        assertThrows(SudokuException.class, () -> ValidationUtils.validatePlayerName(null));
        assertThrows(SudokuException.class, () -> ValidationUtils.validatePlayerName(""));
        assertThrows(SudokuException.class, () -> ValidationUtils.validatePlayerName("   "));
    }
    
    @Test
    @DisplayName("Test invalid player name throws exception - too long")
    void testInvalidPlayerNameTooLong() {
        String longName = "A".repeat(101);
        assertThrows(SudokuException.class, () -> ValidationUtils.validatePlayerName(longName));
    }
    
    @Test
    @DisplayName("Test valid game name does not throw exception")
    void testValidGameName() {
        assertDoesNotThrow(() -> ValidationUtils.validateGameName("My Game"));
        assertDoesNotThrow(() -> ValidationUtils.validateGameName("Game_1"));
    }
    
    @Test
    @DisplayName("Test invalid game name throws exception")
    void testInvalidGameName() {
        assertThrows(SudokuException.class, () -> ValidationUtils.validateGameName(null));
        assertThrows(SudokuException.class, () -> ValidationUtils.validateGameName(""));
    }
    
    @Test
    @DisplayName("Test valid difficulty does not throw exception")
    void testValidDifficulty() {
        assertDoesNotThrow(() -> ValidationUtils.validateDifficulty(1));
        assertDoesNotThrow(() -> ValidationUtils.validateDifficulty(2));
        assertDoesNotThrow(() -> ValidationUtils.validateDifficulty(3));
    }
    
    @Test
    @DisplayName("Test invalid difficulty throws exception")
    void testInvalidDifficulty() {
        assertThrows(SudokuException.class, () -> ValidationUtils.validateDifficulty(0));
        assertThrows(SudokuException.class, () -> ValidationUtils.validateDifficulty(4));
        assertThrows(SudokuException.class, () -> ValidationUtils.validateDifficulty(-1));
    }
    
    @Test
    @DisplayName("Test isNullOrEmpty method")
    void testIsNullOrEmpty() {
        assertTrue(ValidationUtils.isNullOrEmpty(null));
        assertTrue(ValidationUtils.isNullOrEmpty(""));
        assertTrue(ValidationUtils.isNullOrEmpty("   "));
        assertFalse(ValidationUtils.isNullOrEmpty("test"));
    }
    
    @Test
    @DisplayName("Test sanitizeString method")
    void testSanitizeString() {
        assertNull(ValidationUtils.sanitizeString(null));
        assertEquals("test", ValidationUtils.sanitizeString("  test  "));
        assertEquals("hello world", ValidationUtils.sanitizeString("hello   world"));
    }
}
