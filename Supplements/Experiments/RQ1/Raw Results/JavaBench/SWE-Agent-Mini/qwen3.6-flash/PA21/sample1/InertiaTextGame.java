import java.util.Scanner;

/**
 * Main runner for the Inertia game, handling STDIN loop and user commands.
 */
public class InertiaTextGame {

    /**
     * Main entry point for the game.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Load initial game state or create default
        GameState gameState = createDefaultGameState();
        GameController controller = new GameController(gameState);

        System.out.println("=== Inertia Puzzle Game ===");

        while (true) {
            // Display the game board
            gameState.getGameBoardView().output(false);

            // Display game stats
            System.out.println("Lives: " + gameState.getNumLives());
            System.out.println("Gems: " + gameState.getNumGems());
            System.out.println("Moves: " + gameState.getNumMoves());
            System.out.println("Score: " + gameState.getScore());

            // Check win/lose conditions
            if (gameState.hasWon()) {
                System.out.println("Congratulations! You won!");
                break;
            }
            if (gameState.hasLost()) {
                System.out.println("Game Over! You lost all lives!");
                break;
            }

            // Read user command
            System.out.print("Move (U/D/L/R), Undo, or Quit: ");
            String command = scanner.nextLine().trim().toUpperCase();

            if (command.equals("QUIT")) {
                System.out.println("Goodbye!");
                break;
            } else if (command.equals("UNDO")) {
                boolean undone = controller.processUndo();
                if (undone) {
                    System.out.println("Move undone.");
                } else {
                    System.out.println("Nothing to undo.");
                }
            } else {
                Direction direction = parseDirection(command);
                if (direction != null) {
                    MoveResult result = controller.processMove(direction);

                    if (result instanceof Invalid) {
                        System.out.println("Cannot move in that direction!");
                    } else if (result instanceof Dead) {
                        System.out.println("You hit a mine and died!");
                    } else if (result instanceof Alive) {
                        Alive alive = (Alive) result;
                        System.out.println("Move successful!");
                        if (!alive.getCollectedGems().isEmpty()) {
                            System.out.println("Collected " + alive.getCollectedGems().size() + " gem(s).");
                        }
                        if (!alive.getCollectedExtraLives().isEmpty()) {
                            System.out.println("Collected " + alive.getCollectedExtraLives().size() + " extra life(s)!");
                        }
                    }
                } else {
                    System.out.println("Invalid command. Use U, D, L, R, Undo, or Quit.");
                }
            }
        }

        scanner.close();
    }

    /**
     * Creates a default game state for testing.
     *
     * @return A default GameState.
     */
    private static GameState createDefaultGameState() {
        int numRows = 8;
        int numCols = 8;
        Cell[][] board = createDefaultBoard(numRows, numCols);

        // Find player position to set it properly
        Player player = null;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof StopCell) {
                    StopCell sc = (StopCell) cell;
                    if (sc.getEntity() instanceof Player) {
                        player = (Player) sc.getEntity();
                    }
                }
            }
        }

        GameBoard gameBoard = new GameBoard(numRows, numCols, board, player);
        return new GameState(gameBoard, 3); // 3 lives
    }

    /**
     * Creates a default board layout.
     */
    private static Cell[][] createDefaultBoard(int numRows, int numCols) {
        Cell[][] board = new Cell[numRows][numCols];

        // Fill with walls
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (r == 0 || r == numRows - 1 || c == 0 || c == numCols - 1) {
                    board[r][c] = new Wall(new Position(r, c));
                } else {
                    board[r][c] = new EntityCell(new Position(r, c));
                }
            }
        }

        // Add some stop cells
        board[2][2] = new StopCell(new Position(2, 2));
        board[2][5] = new StopCell(new Position(2, 5));
        board[5][2] = new StopCell(new Position(5, 2));
        board[5][5] = new StopCell(new Position(5, 5));

        // Add player at position (1, 1)
        board[1][1] = new StopCell(new Position(1, 1), new Player());

        // Add gems
        board[1][4] = new EntityCell(new Position(1, 4), new Gem());
        board[4][1] = new EntityCell(new Position(4, 1), new Gem());
        board[4][4] = new EntityCell(new Position(4, 4), new Gem());
        board[3][3] = new EntityCell(new Position(3, 3), new Gem());

        // Add a mine
        board[3][1] = new EntityCell(new Position(3, 1), new Mine());

        // Add an extra life
        board[1][6] = new EntityCell(new Position(1, 6), new ExtraLife());

        return board;
    }

    /**
     * Parses a direction command string.
     *
     * @param command The command string.
     * @return The corresponding Direction, or null if not recognized.
     */
    private static Direction parseDirection(String command) {
        switch (command) {
            case "U":
                return Direction.UP;
            case "D":
                return Direction.DOWN;
            case "L":
                return Direction.LEFT;
            case "R":
                return Direction.RIGHT;
            default:
                return null;
        }
    }
}
