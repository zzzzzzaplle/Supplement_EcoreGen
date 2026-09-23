import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main runner for the Inertia text game.
 * Handles STDIN loop and parsing user commands like U, D, Undo, or Quit.
 */
public class InertiaTextGame {

    private final GameState gameState;
    private final GameController gameController;

    public InertiaTextGame() {
        this.gameState = null;
        this.gameController = null;
    }

    public InertiaTextGame(final GameState gameState) {
        this.gameState = gameState;
        this.gameController = new GameController(gameState);
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameController getGameController() {
        return gameController;
    }

    /**
     * Runs the game loop.
     */
    public void run() {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        final GameBoardView view = gameState.getGameBoardView();

        // Print initial board
        view.output(true);

        while (true) {
            try {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }

                final String trimmed = line.trim();

                if ("Quit".equalsIgnoreCase(trimmed)) {
                    break;
                }

                if ("Undo".equalsIgnoreCase(trimmed)) {
                    final boolean undone = gameController.processUndo();
                    if (undone) {
                        view.output(true);
                    } else {
                        System.out.println("No moves to undo.");
                    }
                    continue;
                }

                final Direction direction = parseDirection(trimmed);
                if (direction == null) {
                    System.out.println("Unknown command: " + trimmed);
                    continue;
                }

                final MoveResult result = gameController.processMove(direction);

                if (result instanceof Invalid) {
                    System.out.println("Cannot move in that direction.");
                } else {
                    view.output(true);

                    if (result instanceof Dead) {
                        System.out.println("You hit a mine!");
                    }

                    if (gameState.hasWon()) {
                        System.out.println("You won! Final score: " + gameState.getScore());
                        break;
                    }

                    if (gameState.hasLost()) {
                        System.out.println("You lost! Final score: " + gameState.getScore());
                        break;
                    }
                }

            } catch (final IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
                break;
            }
        }
    }

    /**
     * Parses a direction string to a Direction enum value.
     *
     * @param input The input string.
     * @return The Direction, or null if not recognized.
     */
    private static Direction parseDirection(final String input) {
        switch (input.toUpperCase()) {
            case "U":
            case "UP":
                return Direction.UP;
            case "D":
            case "DOWN":
                return Direction.DOWN;
            case "L":
            case "LEFT":
                return Direction.LEFT;
            case "R":
            case "RIGHT":
                return Direction.RIGHT;
            default:
                return null;
        }
    }

    /**
     * Main entry point.
     *
     * @param args Command line arguments. First argument is the path to a game state file.
     */
    public static void main(final String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java InertiaTextGame <game-file>");
            return;
        }

        final Path gameFile = Paths.get(args[0]);
        try {
            final GameState gameState = GameStateSerializer.loadFrom(gameFile);
            final InertiaTextGame game = new InertiaTextGame(gameState);
            game.run();
        } catch (final Exception e) {
            System.err.println("Error loading game: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
