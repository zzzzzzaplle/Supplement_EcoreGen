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
        GameBoardController boardController = gameState.getGameBoardController();
        MoveResult result = boardController.makeMove(direction);

        if (result instanceof Invalid) {
            // Invalid moves don't change counters and are not pushed to MoveStack
            return result;
        }

        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            // Increment numMoves
            gameState.incrementNumMoves();
            // Apply collected ExtraLife effects
            for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                gameState.increaseNumLives(1);
            }
            // Push to MoveStack
            gameState.getMoveStack().push(result);
            return result;
        }

        if (result instanceof Dead) {
            // Increment numMoves
            gameState.incrementNumMoves();
            // Increment numDeaths
            gameState.incrementNumDeaths();
            // Decrease finite lives by one
            gameState.decrementNumLives();
            // Dead moves are not pushed to MoveStack
            return result;
        }

        return result;
    }

    public boolean processUndo() {
        MoveStack moveStack = gameState.getMoveStack();
        if (moveStack.isEmpty()) {
            return false;
        }

        MoveResult moveResult = moveStack.pop();
        if (moveResult instanceof Alive) {
            Alive alive = (Alive) moveResult;

            // Restore player position and collected Gems/ExtraLives
            GameBoardController boardController = gameState.getGameBoardController();
            boardController.undoMove(alive);

            // Reverse finite ExtraLife gains
            for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                gameState.decreaseNumLives(1);
            }

            return true;
        }

        return false;
    }
}
