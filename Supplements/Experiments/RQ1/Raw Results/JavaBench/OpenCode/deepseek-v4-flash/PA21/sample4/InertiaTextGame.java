import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

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

    public void run() {
        GameState gameState = gameController.getGameState();
        GameBoardView view = gameState.getGameBoardView();
        Scanner scanner = new Scanner(System.in);

        while (!gameState.hasWon() && !gameState.hasLost()) {
            view.output(useUnicode);
            System.out.println("Score: " + gameState.getScore());
            System.out.print("Enter move (U/D/L/R/Undo/Quit): ");

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("Quit")) {
                System.out.println("Goodbye!");
                return;
            }

            if (input.equalsIgnoreCase("Undo")) {
                boolean undone = gameController.processUndo();
                if (undone) {
                    System.out.println("Move undone.");
                } else {
                    System.out.println("Nothing to undo.");
                }
                continue;
            }

            Direction direction = parseDirection(input);
            if (direction == null) {
                System.out.println("Invalid input. Use U, D, L, R, Undo, or Quit.");
                continue;
            }

            MoveResult result = gameController.processMove(direction);

            if (result instanceof Invalid) {
                System.out.println("Invalid move - cannot move in that direction.");
            } else if (result instanceof Dead) {
                System.out.println("You hit a mine! Lost a life.");
                if (gameState.hasLost()) {
                    System.out.println("Game Over! You have no lives left.");
                }
            } else if (result instanceof Alive) {
                Alive alive = (Alive) result;
                if (!alive.getCollectedGems().isEmpty()) {
                    System.out.println("Collected " + alive.getCollectedGems().size() + " gem(s)!");
                }
                if (!alive.getCollectedExtraLives().isEmpty()) {
                    System.out.println("Collected " + alive.getCollectedExtraLives().size() + " extra life(ves)!");
                }
                if (gameState.hasWon()) {
                    System.out.println("You Win! Final score: " + gameState.getScore());
                }
            }
        }

        if (gameState.hasWon()) {
            view.output(useUnicode);
            System.out.println("Final Score: " + gameState.getScore());
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
            System.out.println("Usage: java InertiaTextGame <board-file> [--ascii]");
            return;
        }

        Path inputFile = Paths.get(args[0]);
        boolean useUnicode = true;

        for (String arg : args) {
            if (arg.equals("--ascii")) {
                useUnicode = false;
            }
        }

        try {
            GameState gameState = GameStateSerializer.loadFrom(inputFile);
            GameController gameController = new GameController(gameState);
            InertiaTextGame game = new InertiaTextGame(gameController, useUnicode);
            game.run();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + inputFile);
        }
    }
}
