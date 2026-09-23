import java.util.Scanner;

/**
 * Main runner for the Inertia puzzle game.
 */
public class InertiaTextGame {

    private GameController gameController;
    private Scanner scanner;

    public InertiaTextGame() {
    }

    /**
     * Constructor with scanner.
     *
     * @param scanner the scanner to use.
     */
    public InertiaTextGame(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Returns the game controller.
     *
     * @return the game controller.
     */
    public GameController getGameController() {
        return gameController;
    }

    /**
     * Sets the game controller.
     *
     * @param gameController the game controller.
     */
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * Returns the scanner.
     *
     * @return the scanner.
     */
    public Scanner getScanner() {
        return scanner;
    }

    /**
     * Sets the scanner.
     *
     * @param scanner the scanner.
     */
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Runs the game loop.
     */
    public void run() {
        GameState gameState = gameController.getGameState();
        GameBoardView boardView = gameState.getGameBoardView();

        printStartupMessage();

        while (!gameState.hasWon() && !gameState.hasLost()) {
            boardView.output(true);
            printStatus(gameState);
            printCommands();

            String input = scanner.next().toUpperCase();

            if (input.equals("QUIT")) {
                break;
            } else if (input.equals("UNDO")) {
                boolean undone = gameController.processUndo();
                if (undone) {
                    System.out.println("Move undone.");
                } else {
                    System.out.println("Nothing to undo.");
                }
            } else {
                Direction direction = parseDirection(input);
                if (direction != null) {
                    MoveResult result = gameController.processMove(direction);

                    if (result instanceof Invalid) {
                        System.out.println("Invalid move. Can't move in that direction.");
                    } else if (result instanceof Alive) {
                        Alive alive = (Alive) result;
                        System.out.println("Moved to (" + alive.getNewPosition().getRow() + ", " + alive.getNewPosition().getCol() + ").");
                        if (!alive.getCollectedGems().isEmpty()) {
                            System.out.println("Collected " + alive.getCollectedGems().size() + " gem(s).");
                        }
                        if (!alive.getCollectedExtraLives().isEmpty()) {
                            System.out.println("Found extra life!");
                        }
                    } else if (result instanceof Dead) {
                        System.out.println("Hit a mine! You died.");
                    }
                } else {
                    System.out.println("Unknown command. Use U, D, L, R, Undo, or Quit.");
                }
            }
        }

        if (gameState.hasWon()) {
            System.out.println("Congratulations! You collected all gems!");
            System.out.println("Final score: " + gameState.getScore());
        } else if (gameState.hasLost()) {
            System.out.println("Game over! You ran out of lives.");
            System.out.println("Final score: " + gameState.getScore());
        }
    }

    private void printStartupMessage() {
        System.out.println("Welcome to Inertia!");
        System.out.println("Use U/D/L/R to move, Undo to reverse a move, Quit to exit.");
    }

    private void printStatus(GameState gs) {
        System.out.println("Lives: " + gs.getNumLives());
        System.out.println("Gems: " + gs.getNumGems());
        System.out.println("Score: " + gs.getScore());
        System.out.println("Deaths: " + gs.getNumDeaths());
    }

    private void printCommands() {
        System.out.print("Enter command: ");
    }

    private Direction parseDirection(String input) {
        return switch (input) {
            case "U" -> Direction.UP;
            case "D" -> Direction.DOWN;
            case "L" -> Direction.LEFT;
            case "R" -> Direction.RIGHT;
            default -> null;
        };
    }

    /**
     * Main entry point for the game.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        InertiaTextGame game = new InertiaTextGame();
        game.setScanner(new Scanner(System.in));

        if (args.length > 0) {
            try {
                GameState saved = GameStateSerializer.loadFrom(java.nio.file.Paths.get(args[0]));
                GameController controller = new GameController(saved);
                game.setGameController(controller);
            } catch (Exception e) {
                System.err.println("Error loading save file: " + e.getMessage());
                return;
            }
        } else {
            System.err.println("Please provide a save file as a command line argument.");
            System.err.println("Usage: java InertiaTextGame <savefile.txt>");
            return;
        }

        game.run();
        game.getScanner().close();
    }
}
