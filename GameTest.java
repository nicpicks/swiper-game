public class GameTest {

	public static void main(String[] args) {
		int failures = 0;
		failures += mergeLeftTests();
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

	private static int mergeLeftTests() {
		int failures = 0;
		System.out.println("Running mergeLeft tests:");

		failures += assertArrayEquals(
			"mergeLeft: [0,8,2,2] -> [8,4,0,0]",
			new int[] {8, 4, 0, 0},
			Game.mergeLeft(new int[] {0, 8, 2, 2})
		);

		failures += assertArrayEquals(
			"mergeLeft: [4,2,0,2] -> [4,4,0,0]",
			new int[] {4, 4, 0, 0},
			Game.mergeLeft(new int[] {4, 2, 0, 2})
		);

        failures += assertArrayEquals(
			"mergeLeft: [0,0,0,0] -> [0,0,0,0]",
			new int[] {0, 0, 0, 0},
			Game.mergeLeft(new int[] {0, 0, 0, 0})
		);

		failures += assertArrayEquals(
			"mergeLeft: [0,0,0,2] -> [2,0,0,0]",
			new int[] {2, 0, 0, 0},
			Game.mergeLeft(new int[] {0, 0, 0, 2})
		);

		failures += assertArrayEquals(
			"mergeLeft: [4,4,8,8] -> [8,16,0,0]",
			new int[] {8, 16, 0, 0},
			Game.mergeLeft(new int[] {4, 4, 8, 8})
		);

		System.out.println("mergeLeft tests completed. Failures: " + failures);
		return failures;
	}

	private static int assertArrayEquals(String testName, int[] expected, int[] actual) {
		if (!arraysEqual(expected, actual)) {
			System.out.println("[FAIL] " + testName + "\n  expected: " + toString(expected) + "\n  actual:   " + toString(actual));
			return 1;
		}
		return 0;
	}

	private static boolean arraysEqual(int[] a, int[] b) {
		if (a == b) return true;
		if (a == null || b == null) return false;
		if (a.length != b.length) return false;
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) return false;
		}
		return true;
	}

	private static String toString(int[] arr) {
		if (arr == null) return "null";
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) sb.append(',');
			sb.append(arr[i]);
		}
		sb.append(']');
		return sb.toString();
	}
}
