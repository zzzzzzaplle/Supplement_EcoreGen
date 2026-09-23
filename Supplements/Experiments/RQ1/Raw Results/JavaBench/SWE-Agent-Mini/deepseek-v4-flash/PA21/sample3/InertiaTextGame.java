import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

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
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java InertiaTextGame <input_file>");
            return;
        }

        try {
            GameState gameState = GameStateSerializer.loadFrom(Path.of(args[0]));
            InertiaTextGame game = new InertiaTextGame(gameState);
            game.run();
        } catch (Exception e) {
            System.out.println("Error loading game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            // Print board
            GameBoardView view = gameState.getGameBoardView();
            view.output(true);
            System.out.println();

            // Print score
            System.out.println("Score: " + gameState.getScore());
            System.out.println("Lives: " + (gameState.hasUnlimitedLives() ? "\u221E" : gameState.getNumLives()));
            System.out.println("Moves: " + gameState.getNumMoves());
            System.out.println("Deaths: " + gameState.getNumDeaths());

            // Check win/lose
            if (gameState.hasWon()) {
                System.out.println("You win!");
                break;
            }
            if (gameState.hasLost()) {
                System.out.println("You lose!");
                break;
            }

            System.out.print("Enter command (U/D/L/R, Undo, Quit): ");
            try {
                String input = reader.readLine();
                if (input == null) {
                    break;
                }

                input = input.trim();

                if (input.equalsIgnoreCase("Quit")) {
                    break;
                } else if (input.equalsIgnoreCase("Undo")) {
                    boolean undone = gameController.processUndo();
                    if (!undone) {
                        System.out.println("No moves to undo.");
                    }
                } else if (input.equalsIgnoreCase("U")) {
                    gameController.processMove(Direction.UP);
                } else if (input.equalsIgnoreCase("D")) {
                    gameController.processMove(Direction.DOWN);
                } else if (input.equalsIgnoreCase("L")) {
                    gameController.processMove(Direction.LEFT);
                } else if (input.equalsIgnoreCase("R")) {
                    gameController.processMove(Direction.RIGHT);
                } else {
                    System.out.println("Unknown command: " + input);
                }
            } catch (IOException e) {
                System.out.println("Error reading input: " + e.getMessage());
                break;
            }
        }
    }
}
