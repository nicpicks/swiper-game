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
}
