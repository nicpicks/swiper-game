import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

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
		System.out.println("Welcome to 2048! Use W/A/S/D to move, H=hint, Q=quit.");
		runInteractive();
	}

	static void printBoard(int[][] board) {
		System.out.println("+----+----+----+----+");
		for (int r = 0; r < BOARD_SIZE; r++) {
			for (int c = 0; c < BOARD_SIZE; c++) {
				String cell = board[r][c] == 0 ? "." : Integer.toString(board[r][c]);
				System.out.printf("|%4s", cell);
			}
			System.out.println("|");
			System.out.println("+----+----+----+----+");
		}
	}

	static void runInteractive() {
		int[][] board = initBoard();
		printBoard(board);
		Scanner scanner = new Scanner(System.in);
		
		while (true) {
			GameStatus status = evaluateGameStatus(board);

			if (status == GameStatus.WON) {
				System.out.println("You won!");
				break;
			} else if (status == GameStatus.LOST) {
				System.out.println("Game over!");
				break;
			}

			System.out.print("Move (W/A/S/D, H=hint, Q=quit): ");
			String line = scanner.nextLine();
			if (line == null || line.isEmpty()) continue;
			char ch = Character.toLowerCase(line.charAt(0));
			if (ch == 'q') {
				System.out.println("Quitting. Bye!");
				break;
			}

			boolean changed = false;
			switch (ch) {
				case 'w': changed = moveUp(board); break;
				case 'a': changed = moveLeft(board); break;
				case 's': changed = moveDown(board); break;
				case 'd': changed = moveRight(board); break;
				case 'h': {
					Character ai = suggestBestMove(board);
					if (ai != null) {
						System.out.println("AI hint: " + Character.toUpperCase(ai));
					} else {
						System.out.println("AI hint unavailable (is Ollama running?).");
					}
					break;
				}
				default:
					System.out.println("Invalid input. Only use W/A/S/D, H or Q.");
			}

			if (changed) {
				printBoard(board);
			} else if (ch == 'w' || ch == 'a' || ch == 's' || ch == 'd') {
				System.out.println("No tiles moved. Try a different direction.");
			}
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

	private static Character suggestBestMove(int[][] board) {
		try {
			String endpoint = System.getenv("OLLAMA_HOST");
			if (endpoint == null || endpoint.isEmpty()) {
				endpoint = "http://localhost:11434";
			}
			String model = System.getenv("OLLAMA_MODEL");
			if (model == null || model.isEmpty()) {
				model = "2048-move";
			}

			StringBuilder sb = new StringBuilder();
			for (int r = 0; r < BOARD_SIZE; r++) {
				for (int c = 0; c < BOARD_SIZE; c++) {
					if (board[r][c] == 0) sb.append('.'); else sb.append(board[r][c]);
					if (c < BOARD_SIZE - 1) sb.append('\t');
				}
				sb.append('\n');
			}
			String prompt = sb.toString();
			String modelJson = "\"" + model.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
			String promptJson = "\"" + prompt
				.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\t", "\\t")
				+ "\"";
			String requestBody = "{" +
				"\"model\":" + modelJson + "," +
				"\"prompt\":" + promptJson + "," +
				"\"stream\":false," +
				"\"options\":{\"num_predict\":1}" +
			"}";

			HttpClient client = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(5))
				.build();
			HttpRequest req = HttpRequest.newBuilder()
				.uri(URI.create(endpoint + "/api/generate"))
				.timeout(Duration.ofSeconds(15))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();
			HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

			if (resp.statusCode() < 200 || resp.statusCode() >= 300) return null;

			String body = resp.body();
			Character move = extractMoveFromResponse(body);
			return move;
		} catch (Exception e) {
			return null;
		}
	}

	private static Character extractMoveFromResponse(String body) {
		String text = body;
		int key = body.indexOf("\"response\":");
		if (key != -1) {
			int start = body.indexOf('"', key + 11);
			if (start != -1) {
				int end = body.indexOf('"', start + 1);
				if (end != -1) text = body.substring(start + 1, end);
			}
		}
		text = text.trim();
		if (!text.isEmpty()) {
			char ch = Character.toLowerCase(text.charAt(0));
			if (ch == 'w' || ch == 'a' || ch == 's' || ch == 'd') return ch;
		}
		return null;
	}
}
