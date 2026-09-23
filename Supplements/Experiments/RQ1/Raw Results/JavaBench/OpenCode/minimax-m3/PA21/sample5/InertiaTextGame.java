import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class InertiaTextGame {

    private GameState gameState;
    private GameController gameController;
    private Scanner scanner;
    private boolean useUnicode;

    public InertiaTextGame() {
        this.scanner = new Scanner(System.in);
        this.useUnicode = false;
    }

    public InertiaTextGame(GameState gameState) {
        this.gameState = gameState;
        this.gameController = new GameController(gameState);
        this.scanner = new Scanner(System.in);
        this.useUnicode = false;
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

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean getUseUnicode() {
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
        while (true) {
            gameState.getGameBoardView().output(useUnicode);
            System.out.println("Moves: " + gameState.getNumMoves()
                    + ", Lives: " + (gameState.hasUnlimitedLives() ? "Inf" : gameState.getNumLives())
                    + ", Gems left: " + gameState.getNumGems()
                    + ", Deaths: " + gameState.getNumDeaths()
                    + ", Score: " + gameState.getScore());
            if (gameState.hasWon()) {
                System.out.println("You won!");
                return;
            }
            if (gameState.hasLost()) {
                System.out.println("You lost!");
                return;
            }
            System.out.print("Enter command (U/D/L/R/Undo/Quit): ");
            if (!scanner.hasNextLine()) {
                return;
            }
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }
            String command = input.toLowerCase();
            if (command.equals("quit") || command.equals("q")) {
                return;
            }
            if (command.equals("undo")) {
                boolean undone = gameController.processUndo();
                if (!undone) {
                    System.out.println("No move to undo.");
                }
                continue;
            }
            Direction direction = parseDirection(command);
            if (direction == null) {
                System.out.println("Unknown command: " + input);
                continue;
            }
            MoveResult result = gameController.processMove(direction);
            if (result instanceof Invalid) {
                System.out.println("Invalid move.");
            } else if (result instanceof Dead) {
                System.out.println("You hit a mine!");
            }
        }
    }

    private Direction parseDirection(String command) {
        switch (command) {
            case "u":
            case "up":
                return Direction.UP;
            case "d":
            case "down":
                return Direction.DOWN;
            case "l":
            case "left":
                return Direction.LEFT;
            case "r":
            case "right":
                return Direction.RIGHT;
            default:
                return null;
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java InertiaTextGame <map-file>");
            return;
        }
        Path inputFile = Paths.get(args[0]);
        try {
            GameState state = GameStateSerializer.loadFrom(inputFile);
            InertiaTextGame game = new InertiaTextGame(state);
            game.run();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + args[0]);
        }
    }
}
