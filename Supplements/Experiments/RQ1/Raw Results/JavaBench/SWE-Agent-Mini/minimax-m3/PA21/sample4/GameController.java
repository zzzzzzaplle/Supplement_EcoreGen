import java.util.List;

public class GameController {
    private GameState gameState;

    public GameController() {
    }

    public GameController(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public MoveResult processMove(Direction direction) {
        GameBoardController gbc = gameState.getGameBoardController();
        MoveResult result = gbc.makeMove(direction);

        if (result instanceof Invalid) {
            return result;
        } else if (result instanceof Alive) {
            gameState.incrementNumMoves();
            Alive alive = (Alive) result;
            List<Position> lives = alive.getCollectedExtraLives();
            for (int i = 0; i < lives.size(); i++) {
                gameState.increaseNumLives(1);
            }
            gameState.getMoveStack().push(result);
        } else if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            if (!gameState.hasUnlimitedLives()) {
                gameState.decrementNumLives();
            }
        }

        return result;
    }

    public boolean processUndo() {
        MoveStack stack = gameState.getMoveStack();
        if (stack.isEmpty()) {
            return false;
        }
        MoveResult move = stack.pop();
        if (move instanceof Alive) {
            Alive alive = (Alive) move;
            gameState.getGameBoardController().undoMove(move);
            // Reverse extra life gains
            for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                if (!gameState.hasUnlimitedLives()) {
                    gameState.decrementNumLives();
                }
            }
            return true;
        }
        return false;
    }
}
