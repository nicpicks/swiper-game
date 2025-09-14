import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {
	private static final int BOARD_SIZE = 4;
	private static final int SPAWN_FOUR_PERCENT = 10;

	static enum GameStatus {
		PLAYING,
		WON,
		LOST
	}
	
	static int[][] initBoard() {
		int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
		Random rand = new Random();
		
		int totalCells = BOARD_SIZE * BOARD_SIZE;
		int noOfTwosToInsert = 1 + rand.nextInt(totalCells);
		List<Integer> boardIndexes = new ArrayList<>();
		for (int i = 0; i < totalCells; i++) {
			boardIndexes.add(i);
		}
		
		Collections.shuffle(boardIndexes, rand);
		
		for (int i = 0; i < noOfTwosToInsert; i++) {
			int index = boardIndexes.get(i);
			int row = index / BOARD_SIZE;
			int col = index % BOARD_SIZE;
			board[row][col] = 2;
		}
		return board;
	}
	
	public static void main(String[] args) {
		System.out.println("Welcome to 2048!");
		int[][] board = initBoard();
		for (int[] row : board) {
			for (int val : row) {
				System.out.print(val + " ");
			}
			System.out.println();
		}
	}

	static int[] mergeLeft(int[] row) {
		int[] temp = new int[row.length];
		int index = 0;
		for (int i = 0; i < row.length; i++) {
			if (row[i] != 0) {
				temp[index++] = row[i];
			}
		}
		
		for (int i = 0; i < temp.length -1; i++) {
			if (temp[i] != 0 && temp[i] == temp[i+1]) {
				temp[i] *= 2;
				temp[i+1] = 0;
			}
		}
		
		
		int[] result = new int[row.length];
		index = 0;
		for (int i = 0; i < temp.length; i++) {
			if (temp[i] != 0) {
				result[index++] = temp[i];
			}
		}
		return result;
	}

	static int[] mergeRight(int[] row) {
		int[] temp = new int[row.length];
		int index = row.length - 1;
		for (int i = row.length - 1; i >= 0; i--) {
			if (row[i] != 0) {
				temp[index--] = row[i];
			}
		}

		for (int i = temp.length - 1; i > 0; i--) {
			if (temp[i] != 0 && temp[i] == temp[i - 1]) {
				temp[i] *= 2;
				temp[i - 1] = 0;
			}
		}

		int[] result = new int[row.length];
		index = row.length - 1;
		for (int i = temp.length - 1; i >= 0; i--) {
			if (temp[i] != 0) {
				result[index--] = temp[i];
			}
		}
		return result;
	}

	static int[] mergeUp(int[] column) {
		return mergeLeft(column);
	}

	static int[] mergeDown(int[] column) {
		return mergeRight(column);
	}

	static boolean slideLeft(int[][] board) {
		boolean boardChanged = false;
		for (int r = 0; r < BOARD_SIZE; r++) {
			int[] merged = mergeLeft(board[r]);
			if (!Arrays.equals(board[r], merged)) {
				board[r] = merged;
				boardChanged = true;
			}
		}
		return boardChanged;
	}

	static boolean slideRight(int[][] board) {
		boolean boardChanged = false;
		for (int r = 0; r < BOARD_SIZE; r++) {
			int[] merged = mergeRight(board[r]);
			if (!Arrays.equals(board[r], merged)) {
				board[r] = merged;
				boardChanged = true;
			}
		}
		return boardChanged;
	}

	static boolean slideUp(int[][] board) {
		boolean boardChanged = false;
		for (int c = 0; c < BOARD_SIZE; c++) {
			int[] column = new int[BOARD_SIZE];
			for (int r = 0; r < BOARD_SIZE; r++) {
				column[r] = board[r][c];
			}

			int[] merged = mergeUp(column);

			if (!Arrays.equals(column, merged)) {
				for (int r = 0; r < BOARD_SIZE; r++) board[r][c] = merged[r];
				boardChanged = true;
			}
		}
		return boardChanged;
	}

	static boolean slideDown(int[][] board) {
		boolean boardChanged = false;
		for (int c = 0; c < BOARD_SIZE; c++) {
			int[] column = new int[BOARD_SIZE];
			for (int r = 0; r < BOARD_SIZE; r++) {
				column[r] = board[r][c];
			}

			int[] merged = mergeDown(column);

			if (!Arrays.equals(column, merged)) {
				for (int r = 0; r < BOARD_SIZE; r++) board[r][c] = merged[r];
				boardChanged = true;
			}
		}
		return boardChanged;
	}

	static boolean spawnRandomTile(int[][] board, Random rand) {
		List<Integer> empty = new ArrayList<>();
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				if (board[row][col] == 0) {
					empty.add(row * BOARD_SIZE + col);
				}
			}
		}
		if (empty.isEmpty()) return false;

		int pick = empty.get(rand.nextInt(empty.size()));
		int row = pick / BOARD_SIZE;
		int col = pick % BOARD_SIZE;
		
		int value = (rand.nextInt(100) < SPAWN_FOUR_PERCENT) ? 4 : 2;

		board[row][col] = value;
		return true;
	}

	static boolean spawnRandomTile(int[][] board) {
		return spawnRandomTile(board, new Random());
	}

	static boolean moveLeft(int[][] board) {
		boolean changed = slideLeft(board);
		if (changed) spawnRandomTile(board);
		return changed;
	}

	static boolean moveRight(int[][] board) {
		boolean changed = slideRight(board);
		if (changed) spawnRandomTile(board);
		return changed;
	}

	static boolean moveUp(int[][] board) {
		boolean changed = slideUp(board);
		if (changed) spawnRandomTile(board);
		return changed;
	}

	static boolean moveDown(int[][] board) {
		boolean changed = slideDown(board);
		if (changed) spawnRandomTile(board);
		return changed;
	}

	static GameStatus evaluateGameStatus(int[][] board) {
		if (hasValue(board, 2048)) {
			return GameStatus.WON;
		}
		if (isEmpty(board) || canMerge(board)) {
			return GameStatus.PLAYING;
		}
		return GameStatus.LOST;
	}

	private static boolean hasValue(int[][] board, int value) {
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				if (board[row][col] == value) return true;
			}
		}
		return false;
	}

	private static boolean isEmpty(int[][] board) {
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				if (board[row][col] == 0) return true;
			}
		}
		return false;
	}

	private static boolean canMerge(int[][] board) {
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				int val = board[row][col];
				if (val == 0) continue;
				
				// for merge right
				if (col + 1 < BOARD_SIZE && board[row][col + 1] == val) return true;
				
				// for merge down
				if (row + 1 < BOARD_SIZE && board[row + 1][col] == val) return true;
			}
		}
		return false;
	}
}
