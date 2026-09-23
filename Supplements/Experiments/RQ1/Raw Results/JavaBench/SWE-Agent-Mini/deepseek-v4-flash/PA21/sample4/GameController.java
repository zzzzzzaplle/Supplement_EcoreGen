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
        GameBoardController controller = gameState.getGameBoardController();
        MoveResult result = controller.makeMove(direction);

        if (result instanceof Invalid) {
            // Invalid moves do not change counters and are not pushed
            return result;
        }

        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            gameState.incrementNumMoves();
            
            // Apply collected ExtraLife effects
            if (alive.getCollectedExtraLives() != null && !alive.getCollectedExtraLives().isEmpty()) {
                gameState.increaseNumLives(alive.getCollectedExtraLives().size());
            }
            
            // Push to MoveStack
            gameState.getMoveStack().push(result);
        } else if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            // Decrease finite lives by one
            gameState.decrementNumLives();
            // Not pushed to MoveStack
        }

        return result;
    }

    public boolean processUndo() {
        MoveStack moveStack = gameState.getMoveStack();
        if (moveStack.isEmpty()) {
            return false;
        }

        MoveResult result = moveStack.pop();
        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            
            // Restore player position to original position
            Position origPos = alive.getOrigPosition();
            Position newPos = alive.getNewPosition();
            
            GameBoard board = gameState.getGameBoard();
            
            // Move player back to original position
            EntityCell newCell = board.getEntityCell(newPos);
            EntityCell origCell = board.getEntityCell(origPos);
            
            Player player = board.getPlayer();
            if (newCell != null && player != null) {
                newCell.setEntity(null);
                origCell.setEntity(player);
            }
            
            // Restore collected Gems
            if (alive.getCollectedGems() != null) {
                for (Position gemPos : alive.getCollectedGems()) {
                    EntityCell gemCell = board.getEntityCell(gemPos);
                    if (gemCell != null) {
                        gemCell.setEntity(new Gem());
                    }
                }
            }
            
            // Restore collected ExtraLives and reverse gains
            if (alive.getCollectedExtraLives() != null) {
                for (Position lifePos : alive.getCollectedExtraLives()) {
                    EntityCell lifeCell = board.getEntityCell(lifePos);
                    if (lifeCell != null) {
                        lifeCell.setEntity(new ExtraLife());
                    }
                }
                // Reverse ExtraLife gains
                gameState.decreaseNumLives(alive.getCollectedExtraLives().size());
            }
            
            return true;
        }
        
        return false;
    }
}
