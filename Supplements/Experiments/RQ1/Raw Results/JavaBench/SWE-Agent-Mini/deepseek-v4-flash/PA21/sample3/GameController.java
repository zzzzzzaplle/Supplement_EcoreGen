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
        GameBoardController controller = gameState.getGameBoardController();
        MoveResult result = controller.makeMove(direction);

        if (result instanceof Invalid) {
            // Invalid moves do not change counters and are not pushed to MoveStack
            return result;
        }

        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            gameState.incrementNumMoves();

            // Apply collected ExtraLife effects
            if (!alive.getCollectedExtraLives().isEmpty()) {
                if (!gameState.hasUnlimitedLives()) {
                    for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                        gameState.increaseNumLives(1);
                    }
                }
            }

            // Push to MoveStack
            gameState.getMoveStack().push(result);
            return result;
        }

        if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            if (!gameState.hasUnlimitedLives()) {
                gameState.decrementNumLives();
            }
            // Dead moves are not pushed to MoveStack
            return result;
        }

        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }

        MoveResult move = gameState.getMoveStack().pop();
        if (move instanceof Alive) {
            Alive alive = (Alive) move;

            // Undo ExtraLife gains
            if (!alive.getCollectedExtraLives().isEmpty()) {
                if (!gameState.hasUnlimitedLives()) {
                    for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                        gameState.decreaseNumLives(1);
                    }
                }
            }

            // Restore board (player position and collected items)
            GameBoardController controller = gameState.getGameBoardController();
            controller.undoMove(move);

            return true;
        }

        return false;
    }
}
