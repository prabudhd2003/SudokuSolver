import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class RealTimeSudokuSolver {

    private static final int GRID_SIZE = 9;
    private static JTextField[][] textFields = new JTextField[GRID_SIZE][GRID_SIZE];
    private static JFrame frame = new JFrame("Sudoku Solver by Prabudhd");
    private static JComboBox<String> algorithmChoice;
    private static JButton solveButton;
    private static int[][] board = new int[GRID_SIZE][GRID_SIZE];
    private static boolean[][] isPredefined = new boolean[GRID_SIZE][GRID_SIZE]; 

    public static void main(String[] args) {
        setupGUI();
    }

    private static void setupGUI() {
        frame.setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        textFields = new JTextField[GRID_SIZE][GRID_SIZE];

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                textFields[row][col] = new JTextField();
                textFields[row][col].setHorizontalAlignment(JTextField.CENTER);
                gridPanel.add(textFields[row][col]);
            }
        }

        String[] algorithms = {"Backtracking", "MRV (Minimum Remaining Value)"};
        algorithmChoice = new JComboBox<>(algorithms);

        solveButton = new JButton("Solve");
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solveSudoku();
            }
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(algorithmChoice);
        controlPanel.add(solveButton);

        frame.add(gridPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        loadPredefinedPuzzle();
    }

    private static void loadPredefinedPuzzle() {
        int[][] predefinedBoard = {
            {7, 0, 2, 0, 5, 0, 6, 0, 0},
            {0, 0, 0, 0, 0, 3, 0, 0, 0},
            {1, 0, 0, 0, 0, 9, 5, 0, 0},
            {8, 0, 0, 0, 0, 0, 0, 9, 0},
            {0, 4, 3, 0, 0, 0, 7, 5, 0},
            {0, 9, 0, 0, 0, 0, 0, 0, 8},
            {0, 0, 9, 7, 0, 0, 0, 0, 5},
            {0, 0, 0, 2, 0, 0, 0, 0, 0},
            {0, 0, 7, 0, 4, 0, 2, 0, 3}
        };
        updateBoard(predefinedBoard);
    }

    private static void updateBoard(int[][] newBoard) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                board[row][col] = newBoard[row][col];
                textFields[row][col].setText(newBoard[row][col] == 0 ? "" : String.valueOf(newBoard[row][col]));

                if (newBoard[row][col] != 0) {
                    textFields[row][col].setForeground(Color.BLUE); 
                    textFields[row][col].setEditable(false);       
                    isPredefined[row][col] = true;                  
                } else {
                    textFields[row][col].setForeground(Color.BLACK); 
                    textFields[row][col].setEditable(true);          
                    isPredefined[row][col] = false;
                }
            }
        }
    }

    private static void solveSudoku() {
        String selectedAlgorithm = (String) algorithmChoice.getSelectedItem();

        new SwingWorker<Boolean, Void>() {
            long startTime, endTime;

            @Override
            protected Boolean doInBackground() {
                startTime = System.currentTimeMillis();

                boolean solved = false;
                switch (selectedAlgorithm) {
                    case "Backtracking":
                        solved = solveWithBacktracking();
                        break;
                    case "MRV (Minimum Remaining Value)":
                        solved = solveWithMRV();
                        break;
                }

                endTime = System.currentTimeMillis();
                return solved;
            }

            @Override
            protected void done() {
                try {
                    boolean solved = get();
                    if (solved) {
                        long elapsedTime = endTime - startTime;
                        double elapsedTimeInSeconds = elapsedTime / 1000.0;
                        JOptionPane.showMessageDialog(frame, "Solved in " + String.format("%.2f", elapsedTimeInSeconds) + " seconds with " + selectedAlgorithm);
                    } else {
                        JOptionPane.showMessageDialog(frame, "No solution found with " + selectedAlgorithm);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private static boolean solveWithBacktracking() {
        return solveBoard(board);
    }

    private static boolean solveWithMRV() {
        return solveWithMRVHelper(board);
    }

    private static boolean solveWithMRVHelper(int[][] board) {
        int[] cell = findMRVCell(board);
        if (cell == null) return true;

        int row = cell[0], col = cell[1];
        for (int num = 1; num <= GRID_SIZE; num++) {
            if (isValidPlacement(board, num, row, col)) {
                board[row][col] = num;
                updateGUI();
                sleepForUI();
                if (solveWithMRVHelper(board)) return true;
                board[row][col] = 0;
                updateGUI();
                sleepForUI();
            }
        }
        return false;
    }

    private static int[] findMRVCell(int[][] board) {
        int minOptions = GRID_SIZE + 1;
        int[] result = null;

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] == 0) {
                    Set<Integer> possibleNumbers = getPossibleNumbers(board, row, col);
                    if (possibleNumbers.size() < minOptions) {
                        minOptions = possibleNumbers.size();
                        result = new int[]{row, col};
                    }
                }
            }
        }
        return result;
    }

    private static Set<Integer> getPossibleNumbers(int[][] board, int row, int col) {
        Set<Integer> possibleNumbers = new HashSet<>();
        for (int num = 1; num <= GRID_SIZE; num++) {
            if (isValidPlacement(board, num, row, col)) {
                possibleNumbers.add(num);
            }
        }
        return possibleNumbers;
    }
    
    private static boolean solveBoard(int[][] board) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= GRID_SIZE; num++) {
                        if (isValidPlacement(board, num, row, col)) {
                            board[row][col] = num;
                            updateGUI();
                            sleepForUI();

                            if (solveBoard(board)) {
                                return true;
                            }

                            board[row][col] = 0;
                            updateGUI();
                            sleepForUI();
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static void sleepForUI() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidPlacement(int[][] board, int number, int row, int col) {
        return !isNumberInRow(board, number, row) &&
               !isNumberInColumn(board, number, col) &&
               !isNumberInBox(board, number, row, col);
    }

    private static boolean isNumberInRow(int[][] board, int number, int row) {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[row][i] == number) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNumberInColumn(int[][] board, int number, int col) {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[i][col] == number) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNumberInBox(int[][] board, int number, int row, int col) {
        int boxRow = row - row % 3;
        int boxCol = col - col % 3;
        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                if (board[i][j] == number) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void updateGUI() {
        SwingUtilities.invokeLater(() -> {
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    textFields[row][col].setText(board[row][col] == 0 ? "" : String.valueOf(board[row][col]));
                    if (isPredefined[row][col]) {
                        textFields[row][col].setForeground(Color.BLUE);
                    } else {
                        textFields[row][col].setForeground(Color.BLACK);
                    }
                }
            }
        });
    }
}
