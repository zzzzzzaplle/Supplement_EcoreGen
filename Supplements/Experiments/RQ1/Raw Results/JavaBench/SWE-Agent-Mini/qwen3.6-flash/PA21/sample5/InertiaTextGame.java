import java.util.Scanner;
import java.util.Objects;

public class InertiaTextGame {

    private GameState gameState;
    private GameController gameController;
    private Scanner scanner;

    public InertiaTextGame(GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
        this.gameController = new GameController(gameState);
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            gameController.getGameState().getGameBoardView().output(true);
            System.out.println("Lives: " + getLivesDisplay());
            System.out.println("Score: " + gameController.getGameState().getScore());
            System.out.println("Gems left: " + gameController.getGameState().getNumGems());

            if (gameController.getGameState().hasWon()) {
                System.out.println("You win!");
                break;
            }

            if (gameController.getGameState().hasLost()) {
                System.out.println("You lose!");
                break;
            }

            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("Quit")) {
                break;
            } else if (input.equalsIgnoreCase("Undo")) {
                gameController.processUndo();
            } else if (input.equalsIgnoreCase("U")) {
                MoveResult result = gameController.processMove(Direction.UP);
                handleMoveResult(result);
            } else if (input.equalsIgnoreCase("D")) {
                MoveResult result = gameController.processMove(Direction.DOWN);
                handleMoveResult(result);
            } else if (input.equalsIgnoreCase("L")) {
                MoveResult result = gameController.processMove(Direction.LEFT);
                handleMoveResult(result);
            } else if (input.equalsIgnoreCase("R")) {
                MoveResult result = gameController.processMove(Direction.RIGHT);
                handleMoveResult(result);
            }
        }
        scanner.close();
    }

    private String getLivesDisplay() {
        if (gameController.getGameState().hasUnlimitedLives()) {
            return "Integer.MAX_VALUE";
        }
        return String.valueOf(gameController.getGameState().getNumLives());
    }

    private void handleMoveResult(MoveResult result) {
        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            if (alive.getCollectedGems().size() > 0) {
                System.out.println("Collected " + alive.getCollectedGems().size() + " gems.");
            }
            if (alive.getCollectedExtraLives().size() > 0) {
                System.out.println("Collected " + alive.getCollectedExtraLives().size() + " extra lives.");
            }
        } else if (result instanceof Dead) {
            Dead dead = (Dead) result;
            System.out.println("You hit a mine and died.");
        } else if (result instanceof Invalid) {
            // No output for invalid moves
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: InertiaTextGame <input-file>");
            return;
        }

        try {
            GameState gameState = GameStateSerializer.loadFrom(new java.io.File(args[0]).toPath());
            InertiaTextGame game = new InertiaTextGame(gameState);
            game.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
