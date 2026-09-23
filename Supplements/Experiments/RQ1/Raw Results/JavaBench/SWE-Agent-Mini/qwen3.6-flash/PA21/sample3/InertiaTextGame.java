import java.util.Scanner;

/**
 * Main runner for the Inertia text-based game.
 */
public class InertiaTextGame {
    private GameController gameController;
    private boolean running;

    /**
     * Creates a new InertiaTextGame.
     */
    public InertiaTextGame() {
    }

    /**
     * Creates an InertiaTextGame with the specified game controller.
     */
    public InertiaTextGame(GameController gameController) {
        this.gameController = gameController;
        this.running = true;
    }

    /**
     * Runs the game loop.
     */
    public void run() {
        if (gameController == null) {
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (running) {
            GameState gameState = gameController.getGameState();

            System.out.println("Lives: " + gameState.getStoredNumLives());
            System.out.println("Score: " + gameState.getScore());
            System.out.println("Gems Remaining: " + gameState.getNumGems());
            System.out.println(gameState.getGameBoardView());

            System.out.print("Enter command (U/D/L/R/Undo/Quit): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("Q") || input.equals("QUIT")) {
                running = false;
            } else if (input.equals("UNDO")) {
                if (gameController.processUndo()) {
                    System.out.println("Undid last move.");
                } else {
                    System.out.println("Nothing to undo.");
                }
            } else if (input.equals("U")) {
                handleMove(Direction.UP);
            } else if (input.equals("D")) {
                handleMove(Direction.DOWN);
            } else if (input.equals("L")) {
                handleMove(Direction.LEFT);
            } else if (input.equals("R")) {
                handleMove(Direction.RIGHT);
            } else {
                System.out.println("Unknown command: " + input);
            }

            if (gameState.hasWon()) {
                System.out.println("Congratulations! You won!");
                running = false;
            } else if (gameState.hasLost()) {
                System.out.println("Game Over! You ran out of lives!");
                running = false;
            }
        }

        scanner.close();
    }

    private void handleMove(Direction direction) {
        MoveResult result = gameController.processMove(direction);

        if (result instanceof Valid) {
            System.out.println("Moved from (" + result.getNewPosition().getRow() + "," + result.getNewPosition().getCol() + ")");
        } else if (result instanceof Invalid) {
            System.out.println("Cannot move in that direction.");
        } else if (result instanceof Dead) {
            System.out.println("Ouch! You hit a mine!");
        }
    }

    public static void main(String[] args) {
        // Default game setup for demonstration
        Position p1 = new Position(0, 0);
        Cell[][] board = new Cell[3][3];
        board[0][0] = new Wall(p1);
        board[0][1] = new Wall(new Position(0, 1));
        board[0][2] = new Wall(new Position(0, 2));
        board[1][0] = new Wall(new Position(1, 0));
        board[1][1] = new StopCell(new Position(1, 1), new Player());
        board[1][2] = new StopCell(new Position(1, 2));
        board[2][0] = new EntityCell(new Position(2, 0), new Gem());
        board[2][1] = new EntityCell(new Position(2, 1), new Mine());
        board[2][2] = new EntityCell(new Position(2, 2), new Gem());

        GameBoard gameBoard = new GameBoard(3, 3, board);
        GameState gameState = new GameState(gameBoard, 3);
        GameController controller = new GameController(gameState);

        InertiaTextGame game = new InertiaTextGame(controller);
        game.run();
    }
}
