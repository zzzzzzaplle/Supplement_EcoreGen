import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.Objects;

public class InertiaTextGame {

    private static GameState gameState;

    public static void main(String[] args) {
        try {
            if (args.length >= 1) {
                Path filePath = Paths.get(args[0]);
                if (Files.exists(filePath)) {
                    gameState = GameStateSerializer.loadFrom(filePath);
                    System.out.println("Game loaded from file: " + args[0]);
                } else {
                    System.err.println("File not found: " + args[0]);
                    Cell[][] emptyBoard = new Cell[0][0];
                    gameState = new GameState(new GameBoard(0, 0, emptyBoard));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading game state: " + e.getMessage());
        }

        if (gameState == null) {
            Cell[][] emptyBoard = new Cell[0][0];
            gameState = new GameState(new GameBoard(0, 0, emptyBoard));
        }

        GameController controller = new GameController(gameState);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Welcome to Inertia! Type 'help' for commands.");

        while (true) {
            if (gameState.hasWon()) {
                System.out.println("Congratulations! You won the game!");
                break;
            }
            if (gameState.hasLost()) {
                System.out.println("Game Over! You ran out of lives.");
                break;
            }

            try {
                System.out.print("> ");
                String command = reader.readLine();
                if (command == null) {
                    break;
                }
                command = command.trim();

                if (command.isEmpty()) {
                    continue;
                }

                switch (command.toUpperCase()) {
                    case "QUIT":
                    case "Q":
                        System.out.println("Quit? Type Y to confirm:");
                        String confirm = reader.readLine();
                        if (confirm != null && confirm.trim().equalsIgnoreCase("Y")) {
                            System.out.println("Goodbye!");
                            return;
                        }
                        break;
                    case "EXIT":
                        System.out.println("Goodbye!");
                        return;
                    case "HELP":
                        System.out.println("Commands:");
                        System.out.println("  U, D, L, R - Move Up, Down, Left, Right");
                        System.out.println("  Undo - Undo the last move");
                        System.out.println("  Save <filename> - Save the game state");
                        System.out.println("  Score - Show current score");
                        System.out.println("  Quit - Exit the game");
                        break;
                    case "SCORE":
                        System.out.println("Score: " + gameState.getScore());
                        System.out.println("Lives: " + gameState.getNumLives());
                        System.out.println("Moves: " + gameState.getNumMoves());
                        System.out.println("Deaths: " + gameState.getNumDeaths());
                        System.out.println("Gems remaining: " + gameState.getNumGems());
                        break;
                    case "UNDO":
                        if (controller.processUndo()) {
                            System.out.println("Move undone.");
                        } else {
                            System.out.println("Nothing to undo.");
                        }
                        break;
                    case "SAVE":
                        Path savePath;
                        if (args.length >= 1) {
                            savePath = Paths.get(args[0]).resolveSibling(
                                    Paths.get(args[0]).getFileName() + ".sav");
                        } else {
                            savePath = Paths.get("save.sav");
                        }
                        try {
                            GameStateSerializer.writeTo(gameState, savePath);
                            System.out.println("Game saved to: " + savePath);
                        } catch (FileAlreadyExistsException e) {
                            System.out.println("File already exists: " + savePath);
                        } catch (Exception e) {
                            System.out.println("Error saving game: " + e.getMessage());
                        }
                        break;
                    case "U":
                        processDirection(controller, Direction.UP);
                        break;
                    case "D":
                        processDirection(controller, Direction.DOWN);
                        break;
                    case "L":
                        processDirection(controller, Direction.LEFT);
                        break;
                    case "R":
                        processDirection(controller, Direction.RIGHT);
                        break;
                    default:
                        System.out.println("Unknown command: " + command + ". Type 'help' for commands.");
                        break;
                }

                // Display current game board
                gameState.getGameBoardView().output(false);

            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
                break;
            }
        }
    }

    private static void processDirection(GameController controller, Direction direction) {
        MoveResult result = controller.processMove(direction);

        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            if (alive.getCollectedGems().isEmpty() && alive.getCollectedExtraLives().isEmpty()) {
                System.out.println("Moved " + direction + ".");
            } else if (!alive.getCollectedGems().isEmpty() && !alive.getCollectedExtraLives().isEmpty()) {
                System.out.println("Moved " + direction + ". Collected " +
                        alive.getCollectedGems().size() + " gem(s) and " +
                        alive.getCollectedExtraLives().size() + " extra life(s)!");
            } else if (!alive.getCollectedGems().isEmpty()) {
                System.out.println("Moved " + direction + ". Collected " +
                        alive.getCollectedGems().size() + " gem(s)!");
            } else if (!alive.getCollectedExtraLives().isEmpty()) {
                System.out.println("Moved " + direction + ". Collected an extra life!");
            }
        } else if (result instanceof Dead) {
            Dead dead = (Dead) result;
            Position minePos = dead.getMinePosition();
            System.out.println("Hit a mine at (" + minePos.getRow() + ", " + minePos.getCol() + ")!");
            System.out.println("You have " + (gameState.hasUnlimitedLives() ? "INFINITE" : String.valueOf(gameState.getNumLives())) + " life(s) remaining.");
        } else if (result instanceof Invalid) {
            System.out.println("Could not move " + direction + ".");
        }
    }
}
