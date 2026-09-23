import java.util.Scanner;

public class InertiaTextGame {
    private GameState gameState;
    private GameController gameController;
    private Scanner scanner;

    public InertiaTextGame() {
    }

    public InertiaTextGame(GameState gameState) {
        this.gameState = gameState;
        this.gameController = new GameController(gameState);
        this.scanner = new Scanner(System.in);
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

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        boolean useUnicode = true;
        while (true) {
            if (gameState.getGameBoardView() != null) {
                gameState.getGameBoardView().output(useUnicode);
            }

            if (gameState.hasWon()) {
                System.out.println("You won!");
                break;
            }
            if (gameState.hasLost()) {
                System.out.println("You lost!");
                break;
            }

            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String lower = line.toLowerCase();
            if (lower.equals("quit") || lower.equals("q")) {
                break;
            } else if (lower.equals("undo") || lower.equals("u")) {
                gameController.processUndo();
            } else if (lower.equals("up") || lower.equals("u")) {
                gameController.processMove(Direction.UP);
            } else if (lower.equals("down") || lower.equals("d")) {
                gameController.processMove(Direction.DOWN);
            } else if (lower.equals("left") || lower.equals("l")) {
                gameController.processMove(Direction.LEFT);
            } else if (lower.equals("right") || lower.equals("r")) {
                gameController.processMove(Direction.RIGHT);
            } else {
                char c = line.charAt(0);
                switch (c) {
                    case 'U': case 'u':
                        gameController.processMove(Direction.UP);
                        break;
                    case 'D': case 'd':
                        gameController.processMove(Direction.DOWN);
                        break;
                    case 'L': case 'l':
                        gameController.processMove(Direction.LEFT);
                        break;
                    case 'R': case 'r':
                        gameController.processMove(Direction.RIGHT);
                        break;
                    default:
                        System.out.println("Unknown command: " + line);
                }
            }
        }
    }

    public static void main(String[] args) {
        // Simple default setup
        int numRows = 5;
        int numCols = 5;
        Cell[][] board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                board[r][c] = new EntityCell(new Position(r, c));
            }
        }
        // Place player
        ((EntityCell) board[0][0]).setEntity(new Player());
        // Place gem
        ((EntityCell) board[4][4]).setEntity(new Gem());

        GameBoard gameBoard = new GameBoard(numRows, numCols, board);
        GameState gameState = new GameState(gameBoard);
        InertiaTextGame game = new InertiaTextGame(gameState);
        game.run();
    }
}
