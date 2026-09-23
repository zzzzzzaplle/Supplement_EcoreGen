import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InertiaTextGame {

    private GameController gameController;
    private GameState gameState;
    private boolean useUnicode;

    public InertiaTextGame() {
    }

    public InertiaTextGame(GameController gameController, boolean useUnicode) {
        this.gameController = gameController;
        this.gameState = gameController.getGameState();
        this.useUnicode = useUnicode;
    }

    public void run() {
        GameBoardView view = gameState.getGameBoardView();
        view.output(useUnicode);
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
                        System.out.println("Undo successful.");
                    } else {
                        System.out.println("Nothing to undo.");
                    }
                } else if (line.equalsIgnoreCase("U")) {
                    processMove(Direction.UP);
                } else if (line.equalsIgnoreCase("D")) {
                    processMove(Direction.DOWN);
                } else if (line.equalsIgnoreCase("L")) {
                    processMove(Direction.LEFT);
                } else if (line.equalsIgnoreCase("R")) {
                    processMove(Direction.RIGHT);
                } else {
                    System.out.println("Unknown command: " + line);
                }

                view.output(useUnicode);
                System.out.println("Score: " + gameState.getScore());

                if (gameState.hasWon()) {
                    System.out.println("You win!");
                    break;
                }
                if (gameState.hasLost()) {
                    System.out.println("You lose!");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMove(Direction direction) {
        MoveResult result = gameController.processMove(direction);
        if (result instanceof Invalid) {
            System.out.println("Invalid move.");
        } else if (result instanceof Dead) {
            System.out.println("Hit a mine!");
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java InertiaTextGame <boardfile> [--unix]");
            return;
        }

        boolean useUnicode = true;
        if (args.length >= 2 && args[1].equals("--unix")) {
            useUnicode = false;
        }

        try {
            Path path = Paths.get(args[0]);
            GameState gameState = GameStateSerializer.loadFrom(path);
            GameController gameController = new GameController(gameState);
            InertiaTextGame game = new InertiaTextGame(gameController, useUnicode);
            game.run();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
