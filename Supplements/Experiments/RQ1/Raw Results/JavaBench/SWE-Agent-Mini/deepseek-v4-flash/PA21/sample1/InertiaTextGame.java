import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InertiaTextGame {

    private final GameState gameState;
    private final GameController gameController;
    private boolean useUnicodeChars;

    public InertiaTextGame() {
        this.gameState = null;
        this.gameController = null;
    }

    public InertiaTextGame(GameState gameState) {
        this.gameState = gameState;
        this.gameController = new GameController(gameState);
        this.useUnicodeChars = true;
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameController getGameController() {
        return gameController;
    }

    public boolean isUseUnicodeChars() {
        return useUnicodeChars;
    }

    public void setUseUnicodeChars(boolean useUnicodeChars) {
        this.useUnicodeChars = useUnicodeChars;
    }

    public void run() {
        GameBoardView view = gameState.getGameBoardView();

        // Print initial board
        view.output(useUnicodeChars);

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
                    } else {
                        System.out.println("Could not undo move");
                    }
                    continue;
                }

                Direction direction = parseDirection(line);
                if (direction == null) {
                    System.out.println("Invalid command");
                    continue;
                }

                MoveResult result = gameController.processMove(direction);
                view.output(useUnicodeChars);

                if (result instanceof Dead) {
                    Dead dead = (Dead) result;
                    System.out.println("Player died at mine on (" +
                            dead.getMinePosition().getRow() + ", " +
                            dead.getMinePosition().getCol() + ")");
                }

                if (gameState.hasWon()) {
                    System.out.println("You win! Score: " + gameState.getScore());
                    break;
                }

                if (gameState.hasLost()) {
                    System.out.println("You lose! Score: " + gameState.getScore());
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Direction parseDirection(String input) {
        switch (input.toUpperCase()) {
            case "U": return Direction.UP;
            case "D": return Direction.DOWN;
            case "L": return Direction.LEFT;
            case "R": return Direction.RIGHT;
            default: return null;
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java InertiaTextGame <filename>");
            return;
        }

        Path inputFile = Paths.get(args[0]);
        try {
            GameState gameState = GameStateSerializer.loadFrom(inputFile);
            InertiaTextGame game = new InertiaTextGame(gameState);
            game.run();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
