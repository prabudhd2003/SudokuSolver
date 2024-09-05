In this project I have built a Real-Time Sudoku Solver. It is a Java-based application that visually solves Sudoku puzzles using a graphical user interface (GUI). 

https://github.com/user-attachments/assets/c65ad600-cbef-4625-9042-c9bbc37ddf0b

File `MultiSudokuSolver.java` solves Sudoku without UI update.

The program features two different algorithms for solving Sudoku puzzles:

1. **Backtracking:** This is a simple brute-force algorithm that tries every possible number in each cell. If a number leads to a valid solution, it proceeds to the next cell. If not, it backtracks and tries the next possible number.

**Steps:**

      a. Start from the first empty cell.
      
      b. Try all possible numbers (1-9) for that cell.
      
      c. If a number is valid (i.e., does not violate Sudoku constraints), move to the next cell.
      
      d. If no number is valid, backtrack to the previous cell and try the next possibility.

2. **MRV (Minimum Remaining Value):** The MRV algorithm improves upon backtracking by selecting the cell with the fewest possible valid numbers first. This heuristic helps in reducing the search space.

**Steps:**

      a. Scan the entire board to find the empty cell with the fewest possible numbers (the cell with the least remaining possibilities).

      b. Try placing each possible number in that cell.

      c. If a number is valid, move to the next most constrained cell.

      d. If no solution is found, backtrack and try different possibilities for the previous cells.

**Advantages:** More efficient than standard backtracking. Focuses on cells that are harder to solve first, which can speed up the solving process. In testing, MRV was the fastest, taking ~4 seconds to solve puzzles when GUI updates are not performed.

**Performance:** In performance testing, MRV outperformed Backtracking. The approximate solving times (without GUI updates) were as follows:
1. Backtracking: ~35 seconds
2. MRV: ~4 seconds (fastest)

The slowest performance occurs when the GUI is updated during solving, as rendering the board takes additional time.

Lessons Learned:
1. MRV Heuristic is Efficient: The MRV algorithm drastically improves solving times by focusing on the most constrained cells first.
2. Algorithm Selection Matters: Different algorithms perform better on different puzzle configurations. MRV is particularly efficient for challenging puzzles with fewer remaining possibilities in certain cells.
