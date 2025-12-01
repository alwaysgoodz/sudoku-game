package com.sudoku;

import com.sudoku.gui.SudokuMainFrame;
import com.sudoku.database.DatabaseManager;
import javax.swing.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Main application class for the Sudoku game.
 * Entry point that initializes the application and handles startup.
 */
public class SudokuGameApplication {
    private static final Logger LOGGER = Logger.getLogger(SudokuGameApplication.class.getName());
    
    /**
     * Main method - application entry point
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not set system look and feel", e);
        }
        
        // 设置对话框按钮为英文
        UIManager.put("OptionPane.yesButtonText", "Yes");
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.cancelButtonText", "Cancel");
        UIManager.put("OptionPane.okButtonText", "OK");
        
        // Initialize application on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                initializeApplication();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize application", e);
                showErrorDialog("Failed to start application: " + e.getMessage());
                System.exit(1);
            }
        });
    }
    
    /**
     * Initializes the application components
     */
    private static void initializeApplication() {
        LOGGER.info("Starting Sudoku Game Application...");
        
        // Initialize database
        try {
            DatabaseManager.getInstance();
            LOGGER.info("Database initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database", e);
            showErrorDialog("Database initialization failed: " + e.getMessage());
            return;
        }
        
        // Create and show main window
        try {
            SudokuMainFrame mainFrame = new SudokuMainFrame();
            mainFrame.setVisible(true);
            mainFrame.showWelcomeDialog();
            
            LOGGER.info("Application started successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create main window", e);
            showErrorDialog("Failed to create main window: " + e.getMessage());
        }
    }
    
    /**
     * Shows an error dialog to the user
     * @param message Error message to display
     */
    private static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, 
            message, 
            "Application Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
