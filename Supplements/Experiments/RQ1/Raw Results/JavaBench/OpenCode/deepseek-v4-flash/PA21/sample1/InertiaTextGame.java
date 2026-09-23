import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InertiaTextGame {
    private GameState gameState;
    private GameController gameController;
    private boolean useUnicodeChars;

    public InertiaTextGame() {
    }

    public InertiaTextGame(GameState gameState, boolean useUnicodeChars) {
        this.gameState = gameState;
        this.gameController = new GameController(gameState);
        this.useUnicodeChars = useUnicodeChars;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public boolean isUseUnicodeChars() {
        return useUnicodeChars;
    }

    public void setUseUnicodeChars(boolean useUnicodeChars) {
        this.useUnicodeChars = useUnicodeChars;
    }

    public void run() {
        GameBoardView view = gameState.getGameBoardView();
        view.output(useUnicodeChars);
        System.out.println("Score: " + gameState.getScore());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.equalsIgnoreCase("Quit")) {
                    break;
                }

                if (line.equalsIgnoreCase("Undo")) {
                    boolean undone = gameController.processUndo();
                    if (undone) {
                        view.output(useUnicodeChars);
                        System.out.println("Score: " + gameState.getScore());
                    } else {
                        System.out.println("Cannot undo: no moves to undo.");
                    }
                    continue;
                }

                Direction direction = parseDirection(line);
                if (direction == null) {
                    System.out.println("Unknown command: " + line);
                    continue;
                }

                MoveResult result = gameController.processMove(direction);

                if (result instanceof Invalid) {
                    System.out.println("Cannot move in that direction.");
                    continue;
                }

                if (result instanceof Dead) {
                    view.output(useUnicodeChars);
                    System.out.println("Score: " + gameState.getScore());
                    System.out.println("You died!");
                    if (gameState.hasLost()) {
                        System.out.println("Game Over!");
                        break;
                    }
                    continue;
                }

                if (result instanceof Alive) {
                    view.output(useUnicodeChars);
                    System.out.println("Score: " + gameState.getScore());
                    if (gameState.hasWon()) {
                        System.out.println("You win!");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Direction parseDirection(String input) {
        switch (input.toUpperCase()) {
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

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java InertiaTextGame <input_file> [--unicode]");
            return;
        }

        Path inputPath = Paths.get(args[0]);
        boolean useUnicode = args.length > 1 && args[1].equals("--unicode");

        try {
            GameState gameState = GameStateSerializer.loadFrom(inputPath);
            InertiaTextGame game = new InertiaTextGame(gameState, useUnicode);
            game.run();
        } catch (Exception e) {
            System.out.println("Error loading game: " + e.getMessage());
        }
    }
}
