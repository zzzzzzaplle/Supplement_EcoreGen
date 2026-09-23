import java.util.Scanner;

public class InertiaTextGame {

    public InertiaTextGame() {
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        GameState gameState = new GameState();
        GameController controller = new GameController(gameState);
        GameBoardView view = gameState.getGameBoardView();

        System.out.println("Inertia - Enter a direction (U, D, L, R), Undo, or Quit");
        view.output(false);

        boolean running = true;
        while (running) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            String cmd = line.toUpperCase();
            switch (cmd) {
                case "U":
                case "UP": {
                    MoveResult result = controller.processMove(Direction.UP);
                    handleResult(result, gameState, view);
                    break;
                }
                case "D":
                case "DOWN": {
                    MoveResult result = controller.processMove(Direction.DOWN);
                    handleResult(result, gameState, view);
                    break;
                }
                case "L":
                case "LEFT": {
                    MoveResult result = controller.processMove(Direction.LEFT);
                    handleResult(result, gameState, view);
                    break;
                }
                case "R":
                case "RIGHT": {
                    MoveResult result = controller.processMove(Direction.RIGHT);
                    handleResult(result, gameState, view);
                    break;
                }
                case "UNDO": {
                    boolean ok = controller.processUndo();
                    if (!ok) {
                        System.out.println("No moves to undo.");
                    } else {
                        System.out.println("Undid last move.");
                    }
                    view.output(false);
                    break;
                }
                case "QUIT":
                case "Q":
                case "EXIT": {
                    running = false;
                    break;
                }
                default: {
                    System.out.println("Unknown command. Use U, D, L, R, Undo, or Quit.");
                    break;
                }
            }

            if (gameState.hasWon()) {
                System.out.println("You won! Score: " + gameState.getScore());
                running = false;
            } else if (gameState.hasLost()) {
                System.out.println("You lost! Score: " + gameState.getScore());
                running = false;
            }
        }

        System.out.println("Final Score: " + gameState.getScore());
        scanner.close();
    }

    private void handleResult(MoveResult result, GameState gameState, GameBoardView view) {
        if (result instanceof Invalid) {
            System.out.println("Invalid move.");
        } else if (result instanceof Dead) {
            System.out.println("You died!");
        } else if (result instanceof Alive) {
            Alive alive = (Alive) result;
            if (!alive.getCollectedGems().isEmpty()) {
                System.out.println("Collected " + alive.getCollectedGems().size() + " gem(s).");
            }
            if (!alive.getCollectedExtraLives().isEmpty()) {
                System.out.println("Collected " + alive.getCollectedExtraLives().size() + " extra life/lives.");
            }
        }
        view.output(false);
        System.out.println("Gems remaining: " + gameState.getNumGems()
                + ", Lives: " + (gameState.hasUnlimitedLives() ? "Unlimited" : gameState.getNumLives())
                + ", Moves: " + gameState.getNumMoves()
                + ", Deaths: " + gameState.getNumDeaths()
                + ", Score: " + gameState.getScore());
    }

    public static void main(String[] args) {
        InertiaTextGame game = new InertiaTextGame();
        game.run();
    }
}
