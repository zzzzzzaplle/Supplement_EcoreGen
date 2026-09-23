import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class InertiaTextGame {

    public InertiaTextGame() {
    }

    public static void main(String[] args) {
        GameState gameState = null;
        Scanner scanner = new Scanner(System.in);

        if (args.length > 0) {
            Path inputFile = Paths.get(args[0]);
            try {
                gameState = GameStateSerializer.loadFrom(inputFile);
            } catch (Exception e) {
                System.err.println("Failed to load file: " + e.getMessage());
                return;
            }
        } else {
            System.out.println("No game file provided.");
            return;
        }

        GameController controller = new GameController(gameState);
        boolean useUnicode = true;

        gameState.getGameBoardView().output(useUnicode);

        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            String lower = line.toLowerCase();
            if (lower.equals("quit") || lower.equals("q")) {
                System.out.println("Goodbye!");
                break;
            } else if (lower.equals("undo") || lower.equals("u")) {
                boolean undone = controller.processUndo();
                if (!undone) {
                    System.out.println("No move to undo.");
                }
            } else {
                Direction direction = null;
                switch (lower) {
                    case "w":
                    case "up":
                        direction = Direction.UP;
                        break;
                    case "s":
                    case "down":
                        direction = Direction.DOWN;
                        break;
                    case "a":
                    case "left":
                        direction = Direction.LEFT;
                        break;
                    case "d":
                    case "right":
                        direction = Direction.RIGHT;
                        break;
                    default:
                        System.out.println("Unknown command: " + line);
                        continue;
                }

                MoveResult result = controller.processMove(direction);
                if (result instanceof Dead) {
                    System.out.println("You hit a mine! Lives: " + gameState.getNumLives());
                    if (gameState.hasLost()) {
                        System.out.println("You lost!");
                        gameState.getGameBoardView().output(useUnicode);
                        break;
                    }
                } else if (result instanceof Invalid) {
                    System.out.println("Invalid move.");
                } else if (result instanceof Alive) {
                    if (gameState.hasWon()) {
                        System.out.println("You won! Score: " + gameState.getScore());
                        gameState.getGameBoardView().output(useUnicode);
                        break;
                    }
                }
            }

            gameState.getGameBoardView().output(useUnicode);
        }

        scanner.close();
    }
}
