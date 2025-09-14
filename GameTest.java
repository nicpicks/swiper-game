public class GameTest {

	public static void main(String[] args) {
		int failures = 0;
        failures += initBoardTests();
		failures += mergeLeftTests();
		failures += mergeRightTests();
		failures += mergeUpDownTests();
		failures += slideTests();
		failures += gameStatusTests();

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

	private static int slideTests() {
		int failures = 0;
		System.out.println("Running slide tests:");

		// slideLeft
		int[][] board1 = new int[][] {
			{0, 8, 2, 2},
			{4, 2, 0, 2},
			{0, 0, 0, 0},
			{4, 4, 8, 8}
		};
		Game.slideLeft(board1);
		failures += assertArrayEquals("slideLeft row0", new int[] {8, 4, 0, 0}, board1[0]);
		failures += assertArrayEquals("slideLeft row1", new int[] {4, 4, 0, 0}, board1[1]);
		failures += assertArrayEquals("slideLeft row2", new int[] {0, 0, 0, 0}, board1[2]);
		failures += assertArrayEquals("slideLeft row3", new int[] {8, 16, 0, 0}, board1[3]);

		// slideRight
		int[][] board2 = new int[][] {
			{0, 8, 2, 2},
			{4, 2, 0, 2},
			{0, 0, 0, 0},
			{4, 4, 8, 8}
		};
		Game.slideRight(board2);
		failures += assertArrayEquals("slideRight row0", new int[] {0, 0, 8, 4}, board2[0]);
		failures += assertArrayEquals("slideRight row1", new int[] {0, 0, 4, 4}, board2[1]);
		failures += assertArrayEquals("slideRight row2", new int[] {0, 0, 0, 0}, board2[2]);
		failures += assertArrayEquals("slideRight row3", new int[] {0, 0, 8, 16}, board2[3]);

		// slideUp
		int[][] board3 = new int[][] {
			{0, 8, 2, 2},
			{4, 2, 0, 2},
			{0, 0, 0, 0},
			{4, 4, 8, 8}
		};
		Game.slideUp(board3);
		failures += assertArrayEquals("slideUp col0", new int[] {8, 0, 0, 0}, new int[] {board3[0][0], board3[1][0], board3[2][0], board3[3][0]});
		failures += assertArrayEquals("slideUp col1", new int[] {8, 2, 4, 0}, new int[] {board3[0][1], board3[1][1], board3[2][1], board3[3][1]});
		failures += assertArrayEquals("slideUp col2", new int[] {2, 8, 0, 0}, new int[] {board3[0][2], board3[1][2], board3[2][2], board3[3][2]});
		failures += assertArrayEquals("slideUp col3", new int[] {4, 8, 0, 0}, new int[] {board3[0][3], board3[1][3], board3[2][3], board3[3][3]});

		// slideDown
		int[][] board4 = new int[][] {
			{0, 8, 2, 2},
			{4, 2, 0, 2},
			{0, 0, 0, 0},
			{4, 4, 8, 8}
		};
		Game.slideDown(board4);
		failures += assertArrayEquals("slideDown col0", new int[] {0, 0, 0, 8}, new int[] {board4[0][0], board4[1][0], board4[2][0], board4[3][0]});
		failures += assertArrayEquals("slideDown col1", new int[] {0, 8, 2, 4}, new int[] {board4[0][1], board4[1][1], board4[2][1], board4[3][1]});
		failures += assertArrayEquals("slideDown col2", new int[] {0, 0, 2, 8}, new int[] {board4[0][2], board4[1][2], board4[2][2], board4[3][2]});
		failures += assertArrayEquals("slideDown col3", new int[] {0, 0, 4, 8}, new int[] {board4[0][3], board4[1][3], board4[2][3], board4[3][3]});

		System.out.println("slide tests completed. Failures: " + failures);
		return failures;
	}

	private static int gameStatusTests() {
		int failures = 0;
		System.out.println("Running gameStatus tests:");

		int[][] wonBoard = new int[][] {
			{2, 4, 8, 16},
			{2, 4, 8, 16},
			{2, 4, 2048, 16},
			{2, 4, 8, 16}
		};
		failures += assertEquals(
			"gameStatus: WON when a 2048 value exists",
			Game.GameStatus.WON,
			Game.evaluateGameStatus(wonBoard)
		);

		int[][] playingWithEmpty = new int[][] {
			{2, 4, 8, 16},
			{2, 0, 8, 16},
			{2, 4, 32, 16},
			{2, 4, 8, 16}
		};
		failures += assertEquals(
			"gameStatus: PLAYING when an empty cell exists",
			Game.GameStatus.PLAYING,
			Game.evaluateGameStatus(playingWithEmpty)
		);

		int[][] playingWithMergeable = new int[][] {
			{2, 2, 4, 8},
			{16, 32, 64, 128},
			{256, 512, 4, 8},
			{16, 32, 64, 128}
		};
		failures += assertEquals(
			"gameStatus: PLAYING when a merge move is possible",
			Game.GameStatus.PLAYING,
			Game.evaluateGameStatus(playingWithMergeable)
		);

		int[][] lostBoard = new int[][] {
			{2, 4, 8, 16},
			{32, 64, 128, 256},
			{4, 8, 16, 32},
			{64, 128, 256, 512}
		};
		failures += assertEquals(
			"gameStatus: LOST when no empty cells and no possible merges",
			Game.GameStatus.LOST,
			Game.evaluateGameStatus(lostBoard)
		);

		System.out.println("gameStatus tests completed. Failures: " + failures);
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

	private static int mergeRightTests() {
		int failures = 0;
		System.out.println("Running mergeRight tests:");

		failures += assertArrayEquals(
			"mergeRight: [null,8,2,2] -> [0,0,8,4]",
			new int[] {0, 0, 8, 4},
			Game.mergeRight(new int[] {0, 8, 2, 2})
		);


		failures += assertArrayEquals(
			"mergeRight: [4,2,0,2] -> [0,0,4,4]",
			new int[] {0, 0, 4, 4},
			Game.mergeRight(new int[] {4, 2, 0, 2})
		);

		failures += assertArrayEquals(
			"mergeRight: [0,0,0,0] -> [0,0,0,0]",
			new int[] {0, 0, 0, 0},
			Game.mergeRight(new int[] {0, 0, 0, 0})
		);

		failures += assertArrayEquals(
			"mergeRight: [0,0,0,2] -> [0,0,0,2]",
			new int[] {0, 0, 0, 2},
			Game.mergeRight(new int[] {0, 0, 0, 2})
		);

		System.out.println("mergeRight tests completed. Failures: " + failures);
		return failures;
	}

	private static int mergeUpDownTests() {
		int failures = 0;
		System.out.println("Running mergeUp/mergeDown tests:");

		failures += assertArrayEquals(
			"mergeUp: [0,8,2,2] -> [8,4,0,0]",
			new int[] {8, 4, 0, 0},
			Game.mergeUp(new int[] {0, 8, 2, 2})
		);

		failures += assertArrayEquals(
			"mergeUp: [4,2,0,2] -> [4,4,0,0]",
			new int[] {4, 4, 0, 0},
			Game.mergeUp(new int[] {4, 2, 0, 2})
		);

		failures += assertArrayEquals(
			"mergeDown: [0,8,2,2] -> [0,0,8,4]",
			new int[] {0, 0, 8, 4},
			Game.mergeDown(new int[] {0, 8, 2, 2})
		);

		failures += assertArrayEquals(
			"mergeDown: [4,2,0,2] -> [0,0,4,4]",
			new int[] {0, 0, 4, 4},
			Game.mergeDown(new int[] {4, 2, 0, 2})
		);

		System.out.println("mergeUp/mergeDown tests completed. Failures: " + failures);
		return failures;
	}

	private static int assertArrayEquals(String testName, int[] expected, int[] actual) {
		if (!arraysEqual(expected, actual)) {
			System.out.println("[FAIL] " + testName + "\n  expected: " + toString(expected) + "\n  actual:   " + toString(actual));
			return 1;
		}
		return 0;
	}

	private static int assertEquals(String testName, Object expected, Object actual) {
		if (expected == actual) return 0;
		if (expected != null && expected.equals(actual)) return 0;
		System.out.println("[FAIL] " + testName + "\n  expected: " + expected + "\n  actual:   " + actual);
		return 1;
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
