import java.util.Scanner;

public class InertiaTextGame {
    private GameState gameState;
    private GameController gameController;
    private GameBoardView gameBoardView;

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

    public GameBoardView getGameBoardView() {
        return gameBoardView;
    }

    public void setGameBoardView(GameBoardView gameBoardView) {
        this.gameBoardView = gameBoardView;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        gameBoardView = gameState.getGameBoardView();

        while (true) {
            gameBoardView.output(true);

            if (gameState.hasWon()) {
                System.out.println("You win! Score: " + gameState.getScore());
                break;
            }
            if (gameState.hasLost()) {
                System.out.println("You lose! Score: " + gameState.getScore());
                break;
            }

            System.out.print("Enter command: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("QUIT")) {
                break;
            } else if (input.equals("UNDO")) {
                boolean undone = gameController.processUndo();
                if (!undone) {
                    System.out.println("No moves to undo.");
                }
            } else if (input.equals("U")) {
                MoveResult result = gameController.processMove(Direction.UP);
                if (result instanceof Invalid) {
                    System.out.println("Invalid move.");
                }
            } else if (input.equals("D")) {
                MoveResult result = gameController.processMove(Direction.DOWN);
                if (result instanceof Invalid) {
                    System.out.println("Invalid move.");
                }
            } else if (input.equals("L")) {
                MoveResult result = gameController.processMove(Direction.LEFT);
                if (result instanceof Invalid) {
                    System.out.println("Invalid move.");
                }
            } else if (input.equals("R")) {
                MoveResult result = gameController.processMove(Direction.RIGHT);
                if (result instanceof Invalid) {
                    System.out.println("Invalid move.");
                }
            } else {
                System.out.println("Unknown command.");
            }
        }

        scanner.close();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java InertiaTextGame <input_file>");
            return;
        }

        try {
            GameState gameState = GameStateSerializer.loadFrom(java.nio.file.Paths.get(args[0]));
            InertiaTextGame game = new InertiaTextGame(gameState);
            game.run();
        } catch (Exception e) {
            System.out.println("Error loading game: " + e.getMessage());
        }
    }
}
