import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InertiaTextGame {

    private GameController gameController;
    private boolean useUnicode;

    public InertiaTextGame() {
    }

    public InertiaTextGame(GameController gameController, boolean useUnicode) {
        this.gameController = gameController;
        this.useUnicode = useUnicode;
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

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java InertiaTextGame <input_file> [--unicode]");
            System.exit(1);
        }

        Path inputFile = Paths.get(args[0]);
        boolean useUnicode = args.length > 1 && "--unicode".equals(args[1]);

        try {
            GameState gameState = GameStateSerializer.loadFrom(inputFile);
            GameController controller = new GameController(gameState);
            InertiaTextGame game = new InertiaTextGame(controller, useUnicode);
            game.run();
        } catch (Exception e) {
            System.out.println("Error loading game: " + e.getMessage());
            System.exit(1);
        }
    }

    public void run() {
        GameState gameState = gameController.getGameState();
        GameBoardView view = gameState.getGameBoardView();

        System.out.println("Score: " + gameState.getScore());
        view.output(useUnicode);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                if (gameState.hasWon()) {
                    System.out.println("You won!");
                    break;
                }
                if (gameState.hasLost()) {
                    System.out.println("You lost!");
                    break;
                }

                System.out.print("Enter command: ");
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                line = line.trim().toLowerCase();

                if ("quit".equals(line)) {
                    break;
                } else if ("undo".equals(line)) {
                    boolean undone = gameController.processUndo();
                    if (undone) {
                        System.out.println("Undo successful");
                    } else {
                        System.out.println("Nothing to undo");
                    }
                } else if ("u".equals(line)) {
                    MoveResult result = gameController.processMove(Direction.UP);
                    handleMoveResult(result);
                } else if ("d".equals(line)) {
                    MoveResult result = gameController.processMove(Direction.DOWN);
                    handleMoveResult(result);
                } else if ("l".equals(line)) {
                    MoveResult result = gameController.processMove(Direction.LEFT);
                    handleMoveResult(result);
                } else if ("r".equals(line)) {
                    MoveResult result = gameController.processMove(Direction.RIGHT);
                    handleMoveResult(result);
                } else {
                    System.out.println("Unknown command: " + line);
                }

                System.out.println("Score: " + gameState.getScore());
                view.output(useUnicode);

                if (gameState.getMoveStack().peek() instanceof Alive) {
                    System.out.println("Alive");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleMoveResult(MoveResult result) {
        if (result instanceof Invalid) {
            System.out.println("Cannot move in that direction");
        } else if (result instanceof Dead) {
            System.out.println("You hit a mine!");
        } else if (result instanceof Alive) {
            System.out.println("Moved successfully");
        }
    }
}
