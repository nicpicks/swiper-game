public class GameTest {

	public static void main(String[] args) {
		int failures = 0;
		failures += initBoardTests();

		if (failures == 0) {
			System.out.println("All tests passed.");
			System.exit(0);
		} else {
			System.out.println("Tests finished with failures: " + failures);
			System.exit(1);
		}
	}

    private static int initBoardTests() {
		int failures = 0;
		System.out.println("Running initBoard tests:");

		for (int i = 0; i < 10; i++) {
			int[][] board = Game.initBoard();
			if (board == null) {
				System.out.println("[FAIL] initBoard returned null");
				failures++;
				continue;
			}
			if (board.length != 4) {
				System.out.println("[FAIL] initBoard board.length != 4 but was " + board.length);
				failures++;
			}
			for (int r = 0; r < board.length; r++) {
				if (board[r] == null || board[r].length != 4) {
					System.out.println("[FAIL] initBoard row " + r + " length != 4");
					failures++;
				}
			}

			int twos = 0;
			boolean onlyZeroOrTwo = true;
			for (int r = 0; r < board.length; r++) {
				for (int c = 0; c < board[r].length; c++) {
					int val = board[r][c];
					if (val == 2) twos++;
					if (val != 0 && val != 2) {
						onlyZeroOrTwo = false;
					}
				}
			}

			if (!onlyZeroOrTwo) {
				System.out.println("[FAIL] initBoard contained values other than 0 or 2");
				failures++;
			}
			if (twos < 1 || twos > 16) {
				System.out.println("[FAIL] initBoard twos count is not between 1 and 16: " + twos);
				failures++;
			}
		}

		System.out.println("initBoard tests completed. Failures: " + failures);
		return failures;
	}
}

