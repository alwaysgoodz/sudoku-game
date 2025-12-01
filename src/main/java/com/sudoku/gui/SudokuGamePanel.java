package com.sudoku.gui;

import com.sudoku.model.SudokuPuzzle;
import com.sudoku.model.Hint;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Main game panel for the Sudoku game.
 * Demonstrates GUI design and event handling.
 */
public class SudokuGamePanel extends JPanel {
    private static final int GRID_SIZE = 9;
    private static final int CELL_SIZE = 58;
    
    // 大厂级配色方案 - Notion/Linear 风格
    private static final Color BG_PRIMARY = new Color(25, 25, 28);               // 主背景
    private static final Color BG_SECONDARY = new Color(32, 32, 36);             // 次级背景
    private static final Color BG_TERTIARY = new Color(40, 40, 45);              // 三级背景
    private static final Color ORIGINAL_CELL_COLOR = new Color(55, 55, 62);      // 原始单元格
    private static final Color USER_CELL_COLOR = new Color(45, 45, 52);          // 用户单元格
    private static final Color SELECTED_CELL_COLOR = new Color(99, 102, 241);    // 选中-紫蓝色(Indigo)
    private static final Color INVALID_CELL_COLOR = new Color(239, 68, 68);      // 无效-红色
    private static final Color HINT_CELL_COLOR = new Color(251, 191, 36);        // 提示-金色
    private static final Color GRID_COLOR = new Color(60, 60, 68);               // 网格线
    private static final Color TEXT_PRIMARY = new Color(250, 250, 250);          // 主文字
    private static final Color TEXT_SECONDARY = new Color(161, 161, 170);        // 次级文字
    private static final Color ACCENT_PRIMARY = new Color(99, 102, 241);         // 主强调色-Indigo
    private static final Color BORDER_COLOR = new Color(63, 63, 70);             // 边框色
    
    private SudokuPuzzle puzzle;
    private JTextField[][] cells;
    private JTextField selectedCell;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private GameController controller;
    private JLabel statusLabel;
    private JLabel timerLabel;
    private Timer gameTimer;
    
    /**
     * Constructor for SudokuGamePanel
     * @param controller The game controller
     */
    public SudokuGamePanel(GameController controller) {
        this.controller = controller;
        this.cells = new JTextField[GRID_SIZE][GRID_SIZE];
        initializePanel();
        createGameTimer();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Create header panel with title and status
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create center panel with grid
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        centerPanel.setBackground(BG_PRIMARY);
        JPanel gridPanel = createGridPanel();
        centerPanel.add(gridPanel);
        add(centerPanel, BorderLayout.CENTER);
        
        // Create control panel
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_SECONDARY);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("SUDOKU");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        // Timer panel
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        timerPanel.setBackground(BG_SECONDARY);
        
        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 20));
        timerLabel.setForeground(ACCENT_PRIMARY);
        
        timerPanel.add(timerLabel);
        
        // Status label
        statusLabel = new JLabel("Welcome! Click 'New Game' to start.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(TEXT_SECONDARY);
        
        JPanel leftPanel = new JPanel(new BorderLayout(0, 4));
        leftPanel.setBackground(BG_SECONDARY);
        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(statusLabel, BorderLayout.SOUTH);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(timerPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createGridPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 2, 2));
        gridPanel.setBackground(BORDER_COLOR);
        gridPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_PRIMARY, 2),
            BorderFactory.createLineBorder(BORDER_COLOR, 1)
        ));
        
        // Create 9 3x3 sub-grids
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                JPanel subGrid = createSubGrid(boxRow, boxCol);
                gridPanel.add(subGrid);
            }
        }
        
        return gridPanel;
    }
    
    private JPanel createSubGrid(int boxRow, int boxCol) {
        JPanel subGrid = new JPanel(new GridLayout(3, 3, 1, 1));
        subGrid.setBackground(GRID_COLOR);
        subGrid.setBorder(BorderFactory.createLineBorder(GRID_COLOR, 1));
        
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int actualRow = boxRow * 3 + row;
                int actualCol = boxCol * 3 + col;
                
                JTextField cell = createCell(actualRow, actualCol);
                cells[actualRow][actualCol] = cell;
                subGrid.add(cell);
            }
        }
        
        return subGrid;
    }
    
    private JTextField createCell(int row, int col) {
        JTextField cell = new JTextField();
        cell.setHorizontalAlignment(JTextField.CENTER);
        cell.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        cell.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        cell.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        cell.setCaretColor(ACCENT_PRIMARY);
        cell.setBackground(USER_CELL_COLOR);
        cell.setForeground(TEXT_PRIMARY);
        
        // Add event listeners
        cell.addActionListener(new CellActionListener(row, col));
        cell.addKeyListener(new CellKeyListener(row, col));
        cell.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                selectCell(row, col);
            }
        });
        
        return cell;
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        controlPanel.setBackground(BG_PRIMARY);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        
        JButton newGameButton = new JButton("New Game");
        JButton hintButton = new JButton("Hint");
        JButton solveButton = new JButton("Solve");
        JButton resetButton = new JButton("Reset");
        JButton saveButton = new JButton("Save");
        JButton loadButton = new JButton("Load");
        
        // Style buttons
        styleButton(newGameButton);
        styleButton(hintButton);
        styleButton(solveButton);
        styleButton(resetButton);
        styleButton(saveButton);
        styleButton(loadButton);
        
        // Add action listeners
        newGameButton.addActionListener(e -> controller.startNewGame());
        hintButton.addActionListener(e -> showHint());
        solveButton.addActionListener(e -> solvePuzzle());
        resetButton.addActionListener(e -> resetPuzzle());
        saveButton.addActionListener(e -> controller.saveGame());
        loadButton.addActionListener(e -> controller.loadGame());
        
        controlPanel.add(newGameButton);
        controlPanel.add(hintButton);
        controlPanel.add(solveButton);
        controlPanel.add(resetButton);
        controlPanel.add(saveButton);
        controlPanel.add(loadButton);
        
        return controlPanel;
    }
    
    private void styleButton(JButton button) {
        // 大厂级按钮样式 - Linear/Notion 风格
        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        button.setForeground(TEXT_SECONDARY);
        button.setBackground(BG_TERTIARY);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        
        // 强制使用自定义UI
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        
        // 精致边框
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        
        button.setPreferredSize(new Dimension(100, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(55, 55, 62));
                button.setForeground(TEXT_PRIMARY);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_PRIMARY, 1),
                    BorderFactory.createEmptyBorder(10, 16, 10, 16)
                ));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(BG_TERTIARY);
                button.setForeground(TEXT_SECONDARY);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(10, 16, 10, 16)
                ));
            }
            
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(ACCENT_PRIMARY);
                button.setForeground(Color.WHITE);
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(55, 55, 62));
                button.setForeground(TEXT_PRIMARY);
            }
        });
    }
    
    
    private void createGameTimer() {
        gameTimer = new Timer(1000, e -> updateTimer());
    }
    
    private void updateTimer() {
        if (puzzle != null && !puzzle.isCompleted()) {
            long elapsed = puzzle.getElapsedTime();
            long seconds = elapsed / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
        }
    }
    
    public void setPuzzle(SudokuPuzzle puzzle) {
        this.puzzle = puzzle;
        updateGrid();
        gameTimer.start();
        statusLabel.setText("Game started. Good luck!");
    }
    
    private void updateGrid() {
        if (puzzle == null) return;
        
        int[][] grid = puzzle.getGrid();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JTextField cell = cells[row][col];
                int value = grid[row][col];
                
                if (value == 0) {
                    cell.setText("");
                } else {
                    cell.setText(String.valueOf(value));
                }
                
                // Set cell appearance based on whether it's original or user-entered
                if (puzzle.isOriginalCell(row, col)) {
                    cell.setBackground(ORIGINAL_CELL_COLOR);
                    cell.setForeground(TEXT_PRIMARY);
                    cell.setEditable(false);
                    cell.setFont(new Font("SF Pro Display", Font.BOLD, 24));
                } else {
                    cell.setBackground(USER_CELL_COLOR);
                    cell.setForeground(ACCENT_PRIMARY);  // Indigo色文字
                    cell.setEditable(true);
                    cell.setFont(new Font("SF Pro Display", Font.BOLD, 24));
                }
            }
        }
    }
    
    private void selectCell(int row, int col) {
        // Clear previous selection
        if (selectedCell != null && selectedRow >= 0 && selectedCol >= 0) {
            if (puzzle.isOriginalCell(selectedRow, selectedCol)) {
                selectedCell.setBackground(ORIGINAL_CELL_COLOR);
            } else {
                selectedCell.setBackground(USER_CELL_COLOR);
            }
        }
        
        // Set new selection
        selectedRow = row;
        selectedCol = col;
        selectedCell = cells[row][col];
        selectedCell.setBackground(SELECTED_CELL_COLOR);
    }
    
    private void showHint() {
        if (puzzle == null) {
            statusLabel.setText("No active game. Start a new game first.");
            return;
        }
        
        Hint hint = puzzle.getHint();
        if (hint != null) {
            int row = hint.getRow();
            int col = hint.getCol();
            
            // Highlight the hint cell temporarily
            JTextField hintCell = cells[row][col];
            Color originalColor = hintCell.getBackground();
            hintCell.setBackground(HINT_CELL_COLOR);
            
            // Show hint message
            String message = String.format("Hint: Try placing %d at row %d, column %d\n%s",
                                          hint.getValue(), row + 1, col + 1, hint.getExplanation());
            JOptionPane.showMessageDialog(this, message, "Hint", JOptionPane.INFORMATION_MESSAGE);
            
            // Restore original color after a delay
            Timer timer = new Timer(3000, e -> hintCell.setBackground(originalColor));
            timer.setRepeats(false);
            timer.start();
            
            statusLabel.setText("Hint: Row " + (row + 1) + ", Column " + (col + 1));
        } else {
            statusLabel.setText("No hints available.");
        }
    }
    
    private void solvePuzzle() {
        if (puzzle == null) {
            statusLabel.setText("No active game. Start a new game first.");
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to solve the puzzle automatically?",
            "Solve Puzzle", JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            if (puzzle.solve()) {
                updateGrid();
                puzzle.setCompleted(true);
                gameTimer.stop();
                statusLabel.setText("Puzzle solved.");
                showCompletionMessage();
            } else {
                statusLabel.setText("Unable to solve puzzle.");
            }
        }
    }
    
    private void resetPuzzle() {
        if (puzzle == null) {
            statusLabel.setText("No active game. Start a new game first.");
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reset the puzzle to its original state?",
            "Reset Puzzle", JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            puzzle.resetToOriginal();
            updateGrid();
            statusLabel.setText("Puzzle reset.");
        }
    }
    
    private void checkCompletion() {
        if (puzzle != null && puzzle.isPuzzleComplete()) {
            puzzle.setCompleted(true);
            gameTimer.stop();
            statusLabel.setText("Congratulations! Puzzle completed.");
            showCompletionMessage();
            controller.onGameCompleted();
        }
    }
    
    private void showCompletionMessage() {
        long completionTime = puzzle.getElapsedTime();
        long seconds = completionTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        
        String timeStr = String.format("%02d:%02d", minutes, seconds);
        String message = String.format("Congratulations!\nYou completed the puzzle in %s!", timeStr);
        
        JOptionPane.showMessageDialog(this, message, "Puzzle Completed!", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }
    
    public void updateStatus(String message) {
        statusLabel.setText(message);
    }
    
    // Inner classes for event handling
    private class CellActionListener implements ActionListener {
        private final int row, col;
        
        public CellActionListener(int row, int col) {
            this.row = row;
            this.col = col;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            selectCell(row, col);
        }
    }
    
    private class CellKeyListener extends KeyAdapter {
        private final int row, col;
        
        public CellKeyListener(int row, int col) {
            this.row = row;
            this.col = col;
        }
        
        @Override
        public void keyTyped(KeyEvent e) {
            if (puzzle == null) return;
            
            char c = e.getKeyChar();
            
            // Handle number input (1-9)
            if (c >= '1' && c <= '9') {
                int value = Character.getNumericValue(c);
                if (puzzle.isValidMove(row, col, value)) {
                    puzzle.setValue(row, col, value);
                    cells[row][col].setText(String.valueOf(value));
                    cells[row][col].setBackground(USER_CELL_COLOR);
                    checkCompletion();
                    statusLabel.setText("Move accepted.");
                } else {
                    cells[row][col].setBackground(INVALID_CELL_COLOR);
                    statusLabel.setText("Invalid move.");
                    
                    // Reset color after a delay
                    Timer timer = new Timer(1000, event -> {
                        if (!puzzle.isOriginalCell(row, col)) {
                            cells[row][col].setBackground(USER_CELL_COLOR);
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
                e.consume(); // Prevent default text field behavior
            }
            // Handle clear/delete (0, space, backspace, delete)
            else if (c == '0' || c == ' ' || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
                if (!puzzle.isOriginalCell(row, col)) {
                    puzzle.clearCell(row, col);
                    cells[row][col].setText("");
                    cells[row][col].setBackground(USER_CELL_COLOR);
                    statusLabel.setText("Cell cleared.");
                }
                e.consume();
            }
            // Block all other characters
            else {
                e.consume();
            }
        }
    }
}
