import java.util.*;

public class SudokuBoard {
	private int[][] board;
	private int solves = 0;
	private ArrayList<int[]> coordinates;
	private ArrayList<Integer> numbers;
	
	public SudokuBoard() {
		// the board itself is a 2D array of integers
		board = new int[4][4];
		
		// a list of all valid numbers is created to help randomize population order
		numbers = new ArrayList<Integer>();
		for (int i = 1; i <= 4; i++) {
			numbers.add(i);
		}
		
		// a list of all the coordinates is created to help populate the board randomly
		coordinates = new ArrayList<int[]>();
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				int[] s = {x, y};
				coordinates.add(s);
			}
		}
		
		generateBoard();
	}
	
	private void generateBoard() {
		// First Fill the board randomly with valid numbers
		Collections.shuffle(coordinates);
		populate(0);
		
		// Then empty the board until the state with the fewest numbers for a single valid solution is reached
		Collections.shuffle(coordinates);
		unpopulate();
	}
	
	public String toString() {
		// Returns a string representation of the board
		String s = "";
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				int t = board[x][y];
				if (x == 2) s += "|";
				if (x == 0 && y == 2) s += "--+--\n";
				if (t == 0) s += ".";
				else s += t;
			}
			s += "\n";
		}
		return s;
	}
	
	private int[][] copyBoard() {
		// Creates a copy of the current board
		int[][] outBoard = new int[4][4];
		for (int i = 0; i < 4; i++) {
			System.arraycopy(board[i], 0, outBoard[i], 0, 4);
		}
		return outBoard;
	}
	
	private boolean checkMultSolve(int[][] board, int step) {
		// Checks whether there are multiple possible solutions to the puzzle at the current state
		
		if (step == 0) solves = 0;
		if (step == 16) {
			// we've found one solution but we keep going in search for more
			solves++;
			if (solves > 1) {
				return true;
			}
			else return false;
		}
		else {
			int x = step % 4;
			int y = step / 4;
			
			// if the current coordinate already has a number, keep going
			if (board[x][y] != 0) {
				if (checkMultSolve(board, step + 1)) return true;
			}
			// if it doesn't, check for validity 
			else {
				for (int n = 1; n <= 4; n++) {
					if (checkValid(board, x, y, n)) {
						board[x][y] = n;
						if (checkMultSolve(board, step + 1)) return true;
						else board[x][y] = 0;
					}
				}
			}	
			return false;
		}
	}
	
	private boolean checkValid(int[][] board, int x, int y, int c) {
		// Check whether c can exist happily in specified coordinates
		
		// Check columns and rows (size 4)
		for (int i = 0; i < 4; i++) {
			if (board[x][i] == c) return false;
			if (board[i][y] == c) return false;
		}
		
		// check quadrant (size 2)
		int xx = x - x % 2;
		int yy = y - y % 2;
		
		for (int m = 0; m < 2; m++) {
			for (int k = 0; k < 2; k++) {
				if (board[xx + m][yy + k] == c) return false;
			}
		}
		return true;
	}

	private boolean populate(int step) {
		// Randomly puts valid numbers in the square until it's full
		
		// 16 steps means all 16 squares have been filled properly
		if (step == 16) {
			return true;
		}
		else {
			int[] coord = coordinates.get(step);
			int x = coord[0];
			int y = coord[1];
			
			// shuffle numbers to make sure it's random
			Collections.shuffle(numbers);
			for (int n = 0; n < 4; n++) {
				if (checkValid(board, x, y, numbers.get(n))) {
					board[x][y] = numbers.get(n);
					if (populate(step + 1)) return true;
					else board[x][y] = 0;
				}
			}
			return false;
		}
	}
	
	private void unpopulate() {
		// Removes numbers at random, readding them if the puzzle turns out to have multiple solutions
		
		// Shuffle the coordinates for random positions
		Collections.shuffle(coordinates);
		
		for (int step = 0; step < 16; step++) {
			int[] coord = coordinates.get(step);
			int x = coord[0];
			int y = coord[1];
			
			// we store the value to make sure we can reintroduce it if things don't work out
			int t = board[x][y];
			
			// Remove the number and check for multiple solutions, readd if multiple solutions are found
			board[x][y] = 0;
			if (checkMultSolve(copyBoard(), 0)) {
				board[x][y] = t;
			}	
		}
	}
	
	public static void main(String args[]) {
		SudokuBoard b = new SudokuBoard();
		System.out.println(b);
	}
}