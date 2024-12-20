import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class SudokuGame extends JFrame {
    private static final int SIZE = 9;
    private JTextField[][] cells = new JTextField[SIZE][SIZE];
    private int[][] solution = new int[SIZE][SIZE];
    private int[][] puzzle = new int[SIZE][SIZE];
    private int lives = 3;
    private JLabel livesLabel;

    public SudokuGame() {
        setTitle("Sudoku Game");
        setSize(600, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        livesLabel = new JLabel("Lives: " + lives);
        livesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(livesLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(SIZE, SIZE));

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                final int r = row;
                final int c = col;

                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 20));

                int top = (row % 3 == 0) ? 2 : 1;
                int left = (col % 3 == 0) ? 2 : 1;
                int bottom = (row == SIZE - 1) ? 2 : 1;
                int right = (col == SIZE - 1) ? 2 : 1;
                cells[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));

                cells[row][col].addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        checkCellValue(r, c);
                        if (isPuzzleComplete()) {
                            JOptionPane.showMessageDialog(SudokuGame.this, "Congratulations! You've solved the Sudoku puzzle!");
                        }
                    }

                    @Override
                    public void keyTyped(KeyEvent e) {
                        char ch = e.getKeyChar();
                        if (!Character.isDigit(ch) || ch == '0' || cells[r][c].getText().length() >= 1) {
                            e.consume();
                        }
                    }
                });

                gridPanel.add(cells[row][col]);
            }
        }

        JButton solveButton = new JButton("Solve Puzzle");
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                completeBoard();
            }
        });

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateSudoku();
                updateCells();
                lives = 3;
                updateLivesLabel();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(solveButton);
        buttonPanel.add(newGameButton);

        add(gridPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        generateSudoku();
        setVisible(true);
    }

    private void generateSudoku() {
        fillGrid();
        createPuzzle(40);
        updateCells();
    }

    private void fillGrid() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= SIZE; i++) {
            numbers.add(i);
        }
        fill(0, 0, numbers);
    }

    private boolean fill(int row, int col, List<Integer> numbers) {
        if (row == SIZE - 1 && col == SIZE) {
            return true;
        }
        if (col == SIZE) {
            row++;
            col = 0;
        }

        Collections.shuffle(numbers);
        for (int num : numbers) {
            if (isSafe(row, col, num)) {
                solution[row][col] = num;
                if (fill(row, col + 1, numbers)) {
                    return true;
                }
                solution[row][col] = 0;
            }
        }
        return false;
    }

    private boolean isSafe(int row, int col, int num) {
        for (int x = 0; x < SIZE; x++) {
            if (solution[row][x] == num || solution[x][col] == num) {
                return false;
            }
        }

        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (solution[startRow + i][startCol + j] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    private void createPuzzle(int count) {
        for (int row = 0; row < SIZE; row++) {
            System.arraycopy(solution[row], 0, puzzle[row], 0, SIZE);
        }

        Random rand = new Random();
        while (count > 0) {
            int row = rand.nextInt(SIZE);
            int col = rand.nextInt(SIZE);
            if (puzzle[row][col] != 0) {
                puzzle[row][col] = 0;
                count--;
            }
        }
    }

    private void updateCells() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (puzzle[row][col] != 0) {
                    cells[row][col].setText(String.valueOf(puzzle[row][col]));
                    cells[row][col].setEditable(false);
                    cells[row][col].setBackground(new Color(224, 255, 255));
                } else {
                    cells[row][col].setText("");
                    cells[row][col].setEditable(true);
                    cells[row][col].setBackground(Color.WHITE);
                }
            }
        }
    }

    private void checkCellValue(int row, int col) {
        if (puzzle[row][col] != 0) {
            return;
        }

        String text = cells[row][col].getText().trim();
        if (!text.isEmpty()) {
            try {
                int num = Integer.parseInt(text);
                if (num >= 1 && num <= 9) {
                    if (num == solution[row][col]) {
                        cells[row][col].setBackground(Color.WHITE);
                    } else {
                        cells[row][col].setBackground(Color.RED);
                        lives--;
                        updateLivesLabel();
                        if (lives <= 0) {
                            gameOver();
                        }
                    }
                }
            } catch (NumberFormatException e) {
                cells[row][col].setBackground(Color.WHITE);
            }
        } else {
            cells[row][col].setBackground(Color.WHITE);
        }
    }

    private void updateLivesLabel() {
        livesLabel.setText("Lives: " + lives);
    }

    private boolean isPuzzleComplete() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String text = cells[row][col].getText().trim();
                if (text.isEmpty() || Integer.parseInt(text) != solution[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void completeBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (puzzle[row][col] != 0) {
                    cells[row][col].setText(String.valueOf(solution[row][col]));
                    cells[row][col].setEditable(false);
                    cells[row][col].setBackground(new Color(224, 255, 255));
                } else {
                    cells[row][col].setText(String.valueOf(solution[row][col]));
                    cells[row][col].setBackground(Color.WHITE);
                    cells[row][col].setEditable(false);
                }
            }
        }
        JOptionPane.showMessageDialog(this, "The puzzle has been solved for you.");
    }

    private void gameOver() {
        JOptionPane.showMessageDialog(this, "Game Over! You've run out of lives.");

        int option = JOptionPane.showConfirmDialog(this, "Would you like to play again?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            lives = 3;
            updateLivesLabel();
            generateSudoku();
            updateCells();
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SudokuGame::new);
    }
}
