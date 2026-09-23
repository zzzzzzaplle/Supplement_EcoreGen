import java.util.Scanner;
import java.util.Objects;

public class InertiaTextGame {

    private GameState gameState;
    private Scanner scanner;

    public InertiaTextGame(GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            GameBoardView view = gameState.getGameBoardView();
            view.output(true);

            System.out.println("Lives: " + gameState.getNumLives());
            System.out.println("Score: " + gameState.getScore());
            System.out.println("Gems remaining: " + gameState.getNumGems());
            System.out.print("Enter move (U/D/L/R/Undo/Quit): ");

            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("QUIT")) {
                break;
            }

            if (input.equals("UNDO")) {
                if (gameState.undo()) {
                    System.out.println("Move undone.");
                } else {
                    System.out.println("Nothing to undo.");
                }
                continue;
            }

            Direction direction = parseDirection(input);
            if (direction == null) {
                System.out.println("Invalid input. Try again.");
                continue;
            }

            MoveResult result = gameState.getGameBoardController().makeMove(direction);

            if (result instanceof Invalid) {
                System.out.println("Invalid move!");
                continue;
            }

            if (result instanceof Valid) {
                gameState.incrementNumMoves();
            }

            if (result instanceof Alive) {
                Alive alive = (Alive) result;
                gameState.getMoveStack().push(alive);
                for (Position pos : alive.getCollectedGems()) {
                    System.out.println("Collected Gem at: " + pos);
                }
                for (Position pos : alive.getCollectedExtraLives()) {
                    gameState.increaseNumLives(1);
                    System.out.println("Collected Extra Life at: " + pos);
                }
            }

            if (result instanceof Dead) {
                Dead dead = (Dead) result;
                gameState.incrementNumMoves();
                gameState.incrementNumDeaths();
                gameState.decrementNumLives();
                System.out.println("Hit a mine! You lost a life.");
            }

            if (gameState.hasWon()) {
                System.out.println("You won! Final score: " + gameState.getScore());
                break;
            }

            if (gameState.hasLost()) {
                System.out.println("Game over! Final score: " + gameState.getScore());
                break;
            }
        }

        scanner.close();
    }

    private Direction parseDirection(String input) {
        switch (input) {
            case "U":
                return Direction.UP;
            case "D":
                return Direction.DOWN;
            case "L":
                return Direction.LEFT;
            case "R":
                return Direction.RIGHT;
            default:
                return null;
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java InertiaTextGame <state-file>");
            return;
        }

        GameState gs;
        try {
            gs = GameStateSerializer.loadFrom(java.nio.file.Paths.get(args[0]));
        } catch (Exception e) {
            System.out.println("Error loading state: " + e.getMessage());
            return;
        }

        InertiaTextGame game = new InertiaTextGame(gs);
        game.run();
    }
}
