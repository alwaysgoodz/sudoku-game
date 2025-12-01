package com.sudoku.gui;

import com.sudoku.model.SudokuPuzzle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import java.awt.*;

/**
 * Unit tests for SudokuGamePanel class.
 * Tests GUI components and user interactions.
 */
class SudokuGamePanelTest {
    
    private GameController controller;
    private SudokuGamePanel gamePanel;
    
    @BeforeEach
    void setUp() {
        controller = new GameController();
        gamePanel = new SudokuGamePanel(controller);
        controller.setGamePanel(gamePanel);
    }
    
    @Test
    @DisplayName("Test game panel initialization")
    void testGamePanelInitialization() {
        assertNotNull(gamePanel, "Game panel should not be null");
        assertTrue(gamePanel instanceof JPanel, "Game panel should be a JPanel");
    }
    
    @Test
    @DisplayName("Test game panel has correct layout")
    void testGamePanelLayout() {
        LayoutManager layout = gamePanel.getLayout();
        assertTrue(layout instanceof BorderLayout, "Game panel should use BorderLayout");
    }
    
    @Test
    @DisplayName("Test game panel background color")
    void testGamePanelBackgroundColor() {
        Color bgColor = gamePanel.getBackground();
        assertNotNull(bgColor, "Background color should not be null");
        // Dark theme background
        assertTrue(bgColor.getRed() < 50, "Background should be dark");
        assertTrue(bgColor.getGreen() < 50, "Background should be dark");
        assertTrue(bgColor.getBlue() < 50, "Background should be dark");
    }
    
    @Test
    @DisplayName("Test game panel has child components")
    void testGamePanelHasComponents() {
        Component[] components = gamePanel.getComponents();
        assertTrue(components.length > 0, "Game panel should have child components");
    }
    
    @Test
    @DisplayName("Test setting puzzle updates panel")
    void testSetPuzzle() {
        SudokuPuzzle puzzle = new SudokuPuzzle();
        puzzle.generatePuzzle(1);
        
        // Should not throw exception
        assertDoesNotThrow(() -> gamePanel.setPuzzle(puzzle));
    }
    
    @Test
    @DisplayName("Test update status method")
    void testUpdateStatus() {
        String testMessage = "Test status message";
        
        // Should not throw exception
        assertDoesNotThrow(() -> gamePanel.updateStatus(testMessage));
    }
    
    @Test
    @DisplayName("Test stop timer method")
    void testStopTimer() {
        // Should not throw exception
        assertDoesNotThrow(() -> gamePanel.stopTimer());
    }
    
    @Test
    @DisplayName("Test game panel preferred size")
    void testGamePanelPreferredSize() {
        Dimension preferredSize = gamePanel.getPreferredSize();
        assertNotNull(preferredSize, "Preferred size should not be null");
        assertTrue(preferredSize.width > 0, "Width should be positive");
        assertTrue(preferredSize.height > 0, "Height should be positive");
    }
    
    @Test
    @DisplayName("Test game controller integration")
    void testControllerIntegration() {
        // Test that controller can start a new game
        assertDoesNotThrow(() -> controller.startNewGame());
    }
}
