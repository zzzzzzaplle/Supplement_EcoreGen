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
            // Invalid moves don't change counters and are not pushed to MoveStack
            return result;
        }

        if (result instanceof Alive) {
            // Alive moves increment numMoves, apply collected ExtraLife effects, pushed to MoveStack
            gameState.incrementNumMoves();
            Alive alive = (Alive) result;
            // Apply collected extra lives
            int extraLifeCount = alive.getCollectedExtraLives().size();
            for (int i = 0; i < extraLifeCount; i++) {
                gameState.increaseNumLives(1);
            }
            gameState.getMoveStack().push(result);
        } else if (result instanceof Dead) {
            // Dead moves increment numMoves and numDeaths, decrease finite lives by one, not pushed to MoveStack
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            gameState.decrementNumLives();
        }

        return result;
    }

    public boolean processUndo() {
        MoveStack moveStack = gameState.getMoveStack();
        if (moveStack.isEmpty()) {
            return false;
        }

        MoveResult top = moveStack.peek();
        if (!(top instanceof Alive)) {
            return false;
        }

        Alive alive = (Alive) moveStack.pop();

        // Restore player position and collected Gems/ExtraLives
        GameBoardController controller = gameState.getGameBoardController();
        controller.undoMove(alive);

        // Reverse finite ExtraLife gains
        int extraLifeCount = alive.getCollectedExtraLives().size();
        for (int i = 0; i < extraLifeCount; i++) {
            gameState.decreaseNumLives(1);
        }

        // Undo does not decrement numMoves
        return true;
    }
}
