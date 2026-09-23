public class GameController {
    private final GameState gameState;

    public GameController(final GameState gameState) {
        this.gameState = gameState;
    }

    public MoveResult processMove(final Direction direction) {
        final GameBoardController controller = gameState.getGameBoardController();
        final MoveResult result = controller.makeMove(direction);

        if (result instanceof Alive) {
            final Alive alive = (Alive) result;
            for (final Position gemPos : alive.getCollectedGems()) {
            }
            gameState.incrementNumMoves();
            gameState.getMoveStack().push(result);

            for (final Position extraLifePos : alive.getCollectedExtraLives()) {
                gameState.increaseNumLives(1);
            }
        } else if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            gameState.decrementNumLives();
        }

        return result;
    }

    public boolean processUndo() {
        final MoveStack stack = gameState.getMoveStack();
        if (stack.isEmpty()) {
            return false;
        }

        final MoveResult prevMove = stack.pop();
        final GameBoardController controller = gameState.getGameBoardController();
        controller.undoMove(prevMove);

        if (prevMove instanceof Alive) {
            final Alive alive = (Alive) prevMove;
            for (final Position gemPos : alive.getCollectedGems()) {
                final EntityCell cell = gameState.getGameBoard().getEntityCell(gemPos);
                cell.setEntity(new Gem());
            }
            for (final Position extraLifePos : alive.getCollectedExtraLives()) {
                final EntityCell cell = gameState.getGameBoard().getEntityCell(extraLifePos);
                cell.setEntity(new ExtraLife());
            }
        }

        return true;
    }
}
