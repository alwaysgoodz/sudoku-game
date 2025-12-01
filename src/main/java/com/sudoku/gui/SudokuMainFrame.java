package com.sudoku.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application window for the Sudoku game.
 * Demonstrates GUI design and window management.
 */
public class SudokuMainFrame extends JFrame {
    private GameController controller;
    private SudokuGamePanel gamePanel;
    
    /**
     * Constructor for SudokuMainFrame
     */
    public SudokuMainFrame() {
        initializeFrame();
        createComponents();
        setupMenuBar();
        setupEventHandlers();
    }
    
    private void initializeFrame() {
        setTitle("Sudoku");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(new Color(25, 25, 28));
        
        // Center the window on screen
        setLocationRelativeTo(null);
        
        // Set application icon (if available)
        try {
            // You can add an icon here if you have one
            // setIconImage(ImageIO.read(getClass().getResource("/icon.png")));
        } catch (Exception e) {
            // Icon not found, continue without it
        }
    }
    
    private void createComponents() {
        // Create controller and game panel
        controller = new GameController();
        gamePanel = new SudokuGamePanel(controller);
        controller.setGamePanel(gamePanel);
        
        // Set up layout
        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        
        // Pack to fit components
        pack();
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(32, 32, 36));
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(63, 63, 70)));
        menuBar.setOpaque(true);
        
        // Game Menu
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setMnemonic('G');
        gameMenu.setForeground(new Color(161, 161, 170));
        gameMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gameMenu.setOpaque(true);
        
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.setMnemonic('N');
        newGameItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        newGameItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        newGameItem.addActionListener(e -> controller.startNewGame());
        
        JMenuItem saveGameItem = new JMenuItem("Save Game");
        saveGameItem.setMnemonic('S');
        saveGameItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        saveGameItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        saveGameItem.addActionListener(e -> controller.saveGame());
        
        JMenuItem loadGameItem = new JMenuItem("Load Game");
        loadGameItem.setMnemonic('L');
        loadGameItem.setAccelerator(KeyStroke.getKeyStroke("ctrl L"));
        loadGameItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loadGameItem.addActionListener(e -> controller.loadGame());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('x');
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        exitItem.addActionListener(e -> exitApplication());
        
        gameMenu.add(newGameItem);
        gameMenu.addSeparator();
        gameMenu.add(saveGameItem);
        gameMenu.add(loadGameItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        
        // Player Menu
        JMenu playerMenu = new JMenu("Player");
        playerMenu.setMnemonic('P');
        playerMenu.setForeground(new Color(161, 161, 170));
        playerMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        playerMenu.setOpaque(true);
        
        JMenuItem changePlayerItem = new JMenuItem("Change Player");
        changePlayerItem.setMnemonic('C');
        changePlayerItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        changePlayerItem.addActionListener(e -> changePlayer());
        
        JMenuItem statsItem = new JMenuItem("Statistics");
        statsItem.setMnemonic('t');
        statsItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statsItem.addActionListener(e -> controller.showPlayerStats());
        
        JMenuItem historyItem = new JMenuItem("Game History");
        historyItem.setMnemonic('H');
        historyItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        historyItem.addActionListener(e -> controller.showGameHistory());
        
        JMenuItem leaderboardItem = new JMenuItem("Leaderboard");
        leaderboardItem.setMnemonic('b');
        leaderboardItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        leaderboardItem.addActionListener(e -> controller.showLeaderboard());
        
        playerMenu.add(changePlayerItem);
        playerMenu.addSeparator();
        playerMenu.add(statsItem);
        playerMenu.add(historyItem);
        playerMenu.add(leaderboardItem);
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        helpMenu.setForeground(new Color(161, 161, 170));
        helpMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        helpMenu.setOpaque(true);
        
        JMenuItem rulesItem = new JMenuItem("Game Rules");
        rulesItem.setMnemonic('R');
        rulesItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rulesItem.addActionListener(e -> showGameRules());
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic('A');
        aboutItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        aboutItem.addActionListener(e -> showAbout());
        
        helpMenu.add(rulesItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);
        
        // Add menus to menu bar
        menuBar.add(gameMenu);
        menuBar.add(playerMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void setupEventHandlers() {
        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }
    
    private void changePlayer() {
        String newPlayer = JOptionPane.showInputDialog(this, 
            "Enter player name:", "Change Player", JOptionPane.QUESTION_MESSAGE);
            
        if (newPlayer != null && !newPlayer.trim().isEmpty()) {
            controller.setPlayerName(newPlayer.trim());
            gamePanel.updateStatus("Player changed to: " + newPlayer.trim());
            setTitle("Sudoku Game - " + newPlayer.trim());
        }
    }
    
    private void showGameRules() {
        String rules = """
            Sudoku Game Rules:
            
            1. Fill the 9×9 grid with digits 1-9
            2. Each row must contain all digits 1-9 exactly once
            3. Each column must contain all digits 1-9 exactly once
            4. Each 3×3 sub-grid must contain all digits 1-9 exactly once
            
            How to Play:
            • Click on a cell to select it
            • Type a number (1-9) to enter it
            • Press 0, Space, or Backspace to clear a cell
            • Use the Hint button if you're stuck
            • Save your progress anytime
            
            Difficulty Levels:
            • Easy: Fewer numbers removed (40 cells)
            • Medium: Moderate challenge (50 cells)
            • Hard: Maximum challenge (60 cells)
            
            Good luck and have fun!
            """;
            
        JTextArea textArea = new JTextArea(rules);
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        textArea.setBackground(getBackground());
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Game Rules", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showAbout() {
        String about = """
            Sudoku Game
            Version 1.0
            
            Developed for PDC (Programming Design and Construction) Course
            
            Features:
            • Multiple difficulty levels
            • Save/Load game functionality
            • Player statistics tracking
            • Game history
            • Leaderboard
            • Hint system
            • Auto-solve capability
            
            Technologies Used:
            • Java 21
            • Swing GUI
            • Apache Derby Database
            • Maven Build System
            • JUnit Testing
            
            This project demonstrates:
            • Object-Oriented Programming concepts
            • GUI design with Swing
            • Database integration with JDBC
            • MVC architecture pattern
            • Error handling and validation
            • Unit testing
            
            © 2024 - Educational Project
            """;
            
        JTextArea textArea = new JTextArea(about);
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        textArea.setBackground(getBackground());
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 350));
        
        JOptionPane.showMessageDialog(this, scrollPane, "About Sudoku Game", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exitApplication() {
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit?",
            "Exit Application",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (option == JOptionPane.YES_OPTION) {
            // Cleanup resources
            controller.cleanup();
            
            // Close database connections
            try {
                com.sudoku.database.DatabaseManager.getInstance().closeConnection();
            } catch (Exception e) {
                // Log error but don't prevent exit
                System.err.println("Error closing database: " + e.getMessage());
            }
            
            // Exit application
            System.exit(0);
        }
    }
    
    /**
     * Shows the welcome dialog and gets player name
     */
    public void showWelcomeDialog() {
        String playerName = JOptionPane.showInputDialog(this,
            "Welcome to Sudoku!\nPlease enter your name:",
            "Welcome",
            JOptionPane.QUESTION_MESSAGE);
            
        if (playerName != null && !playerName.trim().isEmpty()) {
            controller.setPlayerName(playerName.trim());
            setTitle("Sudoku Game - " + playerName.trim());
            gamePanel.updateStatus("Welcome, " + playerName.trim() + "! Start a new game to begin.");
        } else {
            // Use default name if none provided
            controller.setPlayerName("Player");
            setTitle("Sudoku Game - Player");
            gamePanel.updateStatus("Welcome! Start a new game to begin.");
        }
    }
}
