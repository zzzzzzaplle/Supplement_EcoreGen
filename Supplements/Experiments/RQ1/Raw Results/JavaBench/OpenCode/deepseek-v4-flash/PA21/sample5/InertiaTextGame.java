import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class InertiaTextGame {
    private GameState gameState;
    private GameController gameController;
    private boolean useUnicode;

    public InertiaTextGame() {
        this.useUnicode = true;
    }

    public InertiaTextGame(GameState gameState) {
        this.gameState = gameState;
        this.gameController = new GameController(gameState);
        this.useUnicode = true;
    }

    public void run() {
        if (gameState == null) {
            System.out.println("No game state loaded.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            GameBoardView view = gameState.getGameBoardView();
            view.output(useUnicode);
            System.out.println("Score: " + gameState.getScore());

            if (gameState.hasWon()) {
                System.out.println("You win!");
                break;
            }

            if (gameState.hasLost()) {
                System.out.println("You lost!");
                break;
            }

            System.out.print("Enter command (U/D/L/R/Undo/Quit): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("Quit")) {
                break;
            }

            if (input.equalsIgnoreCase("Undo")) {
                boolean undone = gameController.processUndo();
                if (!undone) {
                    System.out.println("Nothing to undo.");
                }
                continue;
            }

            Direction direction = null;
            switch (input.toUpperCase()) {
                case "U":
                    direction = Direction.UP;
                    break;
                case "D":
                    direction = Direction.DOWN;
                    break;
                case "L":
                    direction = Direction.LEFT;
                    break;
                case "R":
                    direction = Direction.RIGHT;
                    break;
                default:
                    System.out.println("Unknown command: " + input);
                    continue;
            }

            MoveResult result = gameController.processMove(direction);
            if (result instanceof Invalid) {
                System.out.println("Invalid move.");
            } else if (result instanceof Dead) {
                System.out.println("You hit a mine!");
            }
        }

        scanner.close();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java InertiaTextGame <boardfile>");
            return;
        }

        Path inputFile = Paths.get(args[0]);
        try {
            GameState gameState = GameStateSerializer.loadFrom(inputFile);
            InertiaTextGame game = new InertiaTextGame(gameState);
            game.run();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + inputFile);
        } catch (RuntimeException e) {
            System.out.println("Error loading game: " + e.getMessage());
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        this.gameController = new GameController(gameState);
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public boolean isUseUnicode() {
        return useUnicode;
    }

    public void setUseUnicode(boolean useUnicode) {
        this.useUnicode = useUnicode;
    }
}
