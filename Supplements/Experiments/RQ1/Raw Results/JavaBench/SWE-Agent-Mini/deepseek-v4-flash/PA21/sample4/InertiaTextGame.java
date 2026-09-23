import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    public void run() {
        if (gameState == null) {
            System.out.println("No game state loaded.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                // Display board
                GameBoardView view = gameState.getGameBoardView();
                view.output(useUnicode);
                System.out.println();

                // Show score
                System.out.println("Score: " + gameState.getScore());
                System.out.println("Moves: " + gameState.getNumMoves());
                System.out.println("Deaths: " + gameState.getNumDeaths());
                if (gameState.hasUnlimitedLives()) {
                    System.out.println("Lives: Unlimited");
                } else {
                    System.out.println("Lives: " + gameState.getNumLives());
                }
                System.out.println("Gems remaining: " + gameState.getNumGems());
                System.out.println();

                // Check win/lose
                if (gameState.hasWon()) {
                    System.out.println("You win! Congratulations!");
                    break;
                }
                if (gameState.hasLost()) {
                    System.out.println("Game Over! You have no lives left.");
                    break;
                }

                // Read command
                System.out.print("Enter command (U/D/L/R, Undo, Quit): ");
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                line = line.trim();

                if (line.equalsIgnoreCase("Quit")) {
                    System.out.println("Goodbye!");
                    break;
                } else if (line.equalsIgnoreCase("Undo")) {
                    boolean undone = gameController.processUndo();
                    if (undone) {
                        System.out.println("Move undone.");
                    } else {
                        System.out.println("No moves to undo.");
                    }
                } else if (line.equalsIgnoreCase("U")) {
                    MoveResult result = gameController.processMove(Direction.UP);
                    handleMoveResult(result);
                } else if (line.equalsIgnoreCase("D")) {
                    MoveResult result = gameController.processMove(Direction.DOWN);
                    handleMoveResult(result);
                } else if (line.equalsIgnoreCase("L")) {
                    MoveResult result = gameController.processMove(Direction.LEFT);
                    handleMoveResult(result);
                } else if (line.equalsIgnoreCase("R")) {
                    MoveResult result = gameController.processMove(Direction.RIGHT);
                    handleMoveResult(result);
                } else {
                    System.out.println("Unknown command: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
        }
    }

    private void handleMoveResult(MoveResult result) {
        if (result instanceof Invalid) {
            System.out.println("Invalid move! Cannot move in that direction.");
        } else if (result instanceof Dead) {
            System.out.println("You hit a mine and died!");
        } else if (result instanceof Alive) {
            Alive alive = (Alive) result;
            if (alive.getCollectedGems() != null && !alive.getCollectedGems().isEmpty()) {
                System.out.println("Collected " + alive.getCollectedGems().size() + " gem(s)!");
            }
            if (alive.getCollectedExtraLives() != null && !alive.getCollectedExtraLives().isEmpty()) {
                System.out.println("Collected " + alive.getCollectedExtraLives().size() + " extra life(ves)!");
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java InertiaTextGame <board-file>");
            return;
        }

        try {
            Path path = Paths.get(args[0]);
            GameState gameState = GameStateSerializer.loadFrom(path);
            InertiaTextGame game = new InertiaTextGame(gameState);
            game.run();
        } catch (Exception e) {
            System.err.println("Error loading game: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
