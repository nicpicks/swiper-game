import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {
	private static int BOARD_SIZE = 4;
	
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
}
