import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InertiaTextGame {
    private GameState gameState;
    private GameController gameController;

    public InertiaTextGame() {
    }

    public InertiaTextGame(GameState gameState) {
        this.gameState = gameState;
        this.gameController = new GameController(gameState);
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

    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        printBoardAndStatus();

        try {
            String line;
            while (!gameState.hasWon() && !gameState.hasLost() && (line = reader.readLine()) != null) {
                String cmd = line.trim();
                if (cmd.isEmpty()) {
                    continue;
                }

                if (cmd.equalsIgnoreCase("Quit") || cmd.equalsIgnoreCase("Q")) {
                    System.out.println("Quitting game.");
                    return;
                }

                if (cmd.equalsIgnoreCase("Undo")) {
                    boolean success = gameController.processUndo();
                    if (!success) {
                        System.out.println("No move available to undo.");
                    }
                    printBoardAndStatus();
                    continue;
                }

                Direction dir = parseDirection(cmd);
                if (dir == null) {
                    System.out.println("Unknown command: " + cmd);
                    continue;
                }

                MoveResult result = gameController.processMove(dir);
                if (result instanceof Invalid) {
                    System.out.println("Invalid move - blocked by wall or boundary.");
                } else if (result instanceof Dead) {
                    System.out.println("You hit a mine! You died.");
                }

                printBoardAndStatus();
            }

            if (gameState.hasWon()) {
                System.out.println("Congratulations! You collected all the gems!");
                System.out.println("Final Score: " + gameState.getScore());
            } else if (gameState.hasLost()) {
                System.out.println("Game Over! You ran out of lives.");
                System.out.println("Final Score: " + gameState.getScore());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Direction parseDirection(String cmd) {
        switch (cmd.toUpperCase()) {
            case "U":
            case "UP":
            case "W":
                return Direction.UP;
            case "D":
            case "DOWN":
            case "S":
                return Direction.DOWN;
            case "L":
            case "LEFT":
            case "A":
                return Direction.LEFT;
            case "R":
            case "RIGHT":
                return Direction.RIGHT;
            default:
                return null;
        }
    }

    private void printBoardAndStatus() {
        gameState.getGameBoardView().output(false);
        System.out.println("Gems remaining: " + gameState.getNumGems());
        if (gameState.hasUnlimitedLives()) {
            System.out.println("Lives: unlimited");
        } else {
            System.out.println("Lives: " + gameState.getNumLives());
        }
        System.out.println("Moves: " + gameState.getNumMoves()
                + ", Deaths: " + gameState.getNumDeaths()
                + ", Undoes: " + gameState.getMoveStack().getPopCount());
        System.out.println("Score: " + gameState.getScore());
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java InertiaTextGame <map-file>");
            System.exit(1);
        }
        Path inputFile = Paths.get(args[0]);
        GameState state = GameStateSerializer.loadFrom(inputFile);
        InertiaTextGame game = new InertiaTextGame(state);
        game.run();
    }
}
