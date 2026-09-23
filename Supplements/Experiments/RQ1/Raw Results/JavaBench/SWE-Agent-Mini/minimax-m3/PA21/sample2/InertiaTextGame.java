import java.util.Scanner;

public class InertiaTextGame {
    private GameController gameController;
    private boolean useUnicodeChars;

    public InertiaTextGame() {
        this.gameController = new GameController();
        this.useUnicodeChars = true;
    }

    public InertiaTextGame(GameController gameController) {
        this.gameController = gameController;
        this.useUnicodeChars = true;
    }

    public InertiaTextGame(GameController gameController, boolean useUnicodeChars) {
        this.gameController = gameController;
        this.useUnicodeChars = useUnicodeChars;
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public boolean isUseUnicodeChars() {
        return useUnicodeChars;
    }

    public void setUseUnicodeChars(boolean useUnicodeChars) {
        this.useUnicodeChars = useUnicodeChars;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        printBoard();
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
            if (lower.equals("quit") || lower.equals("q") || lower.equals("exit")) {
                System.out.println("Goodbye!");
                break;
            }
            if (lower.equals("undo") || lower.equals("u")) {
                boolean ok = gameController.processUndo();
                if (!ok) {
                    System.out.println("No moves to undo.");
                }
                printBoard();
                continue;
            }
            Direction dir = parseDirection(lower);
            if (dir == null) {
                System.out.println("Unknown command: " + line);
                continue;
            }
            MoveResult result = gameController.processMove(dir);
            if (result instanceof Invalid) {
                System.out.println("Invalid move.");
            } else if (result instanceof Dead) {
                Dead d = (Dead) result;
                System.out.println("You died at " + d.getMinePosition());
            } else if (result instanceof Alive) {
                Alive a = (Alive) result;
                if (!a.getCollectedGems().isEmpty()) {
                    System.out.println("Collected " + a.getCollectedGems().size() + " gem(s).");
                }
                if (!a.getCollectedExtraLives().isEmpty()) {
                    System.out.println("Collected " + a.getCollectedExtraLives().size() + " extra life(s).");
                }
            }
            printBoard();
            GameState state = gameController.getGameState();
            if (state.hasWon()) {
                System.out.println("You won! Score: " + state.getScore());
                break;
            }
            if (state.hasLost()) {
                System.out.println("You lost. Score: " + state.getScore());
                break;
            }
        }
        scanner.close();
    }

    private Direction parseDirection(String cmd) {
        switch (cmd) {
            case "up":
            case "u":
            case "w":
                return Direction.UP;
            case "down":
            case "d":
            case "s":
                return Direction.DOWN;
            case "left":
            case "l":
            case "a":
                return Direction.LEFT;
            case "right":
            case "r":
            case "d2":
                return Direction.RIGHT;
            default:
                return null;
        }
    }

    private void printBoard() {
        gameController.getGameState().getGameBoardView().output(useUnicodeChars);
        GameState state = gameController.getGameState();
        System.out.println("Gems: " + state.getNumGems() + " | Lives: " + (state.hasUnlimitedLives() ? "unlimited" : state.getNumLives()) + " | Moves: " + state.getNumMoves() + " | Score: " + state.getScore());
    }
}
