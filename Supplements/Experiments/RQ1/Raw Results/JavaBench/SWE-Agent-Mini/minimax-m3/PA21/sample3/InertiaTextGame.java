import java.nio.file.Path;
import java.util.Scanner;

public class InertiaTextGame {

    public InertiaTextGame() {
    }

    public void run(Path inputFile) {
        try {
            GameState gameState = GameStateSerializer.loadFrom(inputFile);
            GameController controller = new GameController(gameState);
            Scanner scanner = new Scanner(System.in);

            gameState.getGameBoardView().output(true);
            System.out.println("Lives: " + (gameState.hasUnlimitedLives() ? "unlimited" : gameState.getNumLives()));

            while (true) {
                if (gameState.hasWon()) {
                    System.out.println("You won! Score: " + gameState.getScore());
                    break;
                }
                if (gameState.hasLost()) {
                    System.out.println("You lost! Score: " + gameState.getScore());
                    break;
                }

                System.out.print("Enter command (U/D/L/R, Undo, Quit): ");
                String line = scanner.nextLine().trim();

                if (line.equalsIgnoreCase("Quit")) {
                    break;
                } else if (line.equalsIgnoreCase("Undo")) {
                    boolean ok = controller.processUndo();
                    if (!ok) {
                        System.out.println("Nothing to undo.");
                    }
                } else {
                    Direction dir = parseDirection(line);
                    if (dir == null) {
                        System.out.println("Invalid command.");
                        continue;
                    }
                    MoveResult result = controller.processMove(dir);
                    if (result instanceof Invalid) {
                        System.out.println("Invalid move.");
                    }
                }

                gameState.getGameBoardView().output(true);
                System.out.println("Score: " + gameState.getScore() + " Lives: " + (gameState.hasUnlimitedLives() ? "unlimited" : gameState.getNumLives()) + " Gems left: " + gameState.getNumGems());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private Direction parseDirection(String s) {
        switch (s.toUpperCase()) {
            case "U": return Direction.UP;
            case "D": return Direction.DOWN;
            case "L": return Direction.LEFT;
            case "R": return Direction.RIGHT;
            default: return null;
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java InertiaTextGame <inputfile>");
            return;
        }
        InertiaTextGame game = new InertiaTextGame();
        game.run(Path.of(args[0]));
    }
}
