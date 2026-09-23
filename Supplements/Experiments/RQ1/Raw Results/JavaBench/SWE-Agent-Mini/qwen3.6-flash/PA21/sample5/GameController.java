import java.util.List;
import java.util.Objects;

public class GameController {

    private GameState gameState;

    public GameController() {
    }

    public GameController(GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public MoveResult processMove(Direction direction) {
        if (gameState.hasLost()) {
            return null;
        }

        MoveResult result = gameState.getGameBoardController().makeMove(direction);

        if (result instanceof Invalid) {
            // Invalid moves do not change counters
            return result;
        }

        if (result instanceof Dead) {
            // Dead moves increment numMoves and numDeaths, decrease finite lives by one
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            gameState.decreaseNumLives(1);
            // Dead moves are not pushed to the MoveStack
            return result;
        }

        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            // Alive moves increment numMoves
            gameState.incrementNumMoves();

            // Apply collected ExtraLife effects
            for (Position pos : alive.getCollectedExtraLives()) {
                gameState.increaseNumLives(1);
            }

            // Push to MoveStack
            gameState.getMoveStack().push(alive);
            return result;
        }

        return result;
    }

    public boolean processUndo() {
        if (gameState.hasLost()) {
            return false;
        }

        MoveStack moveStack = gameState.getMoveStack();
        if (moveStack.isEmpty()) {
            return false;
        }

        MoveResult lastMove = moveStack.pop();
        if (lastMove instanceof Alive) {
            Alive alive = (Alive) lastMove;

            // Reverse finite ExtraLife gains
            for (Position pos : alive.getCollectedExtraLives()) {
                gameState.decreaseNumLives(1);
            }

            int numRows = gameState.getGameBoard().getNumRows();
            int numCols = gameState.getGameBoard().getNumCols();
            Position oldPosition = alive.getNewPosition();
            Position newPosition;

            // We need to slide the player back by reversing the move
            // Since we don't store the original direction, we need to find the original position
            // The player was at some original position, and slid to oldPosition
            // We need to restore the player to oldPosition and restore gems/lives

            Player player = gameState.getGameBoard().getPlayer();

            // Restore gems
            for (Position gemPos : alive.getCollectedGems()) {
                EntityCell cell = gameState.getGameBoard().getEntityCell(gemPos.getRow(), gemPos.getCol());
                cell.setentity(new Gem());
            }

            // Move player from current position back to oldPosition
            // Player is currently at the last moved position (the end of the alive move)
            // We need to move back to oldPosition
            int currentRow = -1;
            int currentCol = -1;
            for (int r = 0; r < numRows; r++) {
                for (int c = 0; c < numCols; c++) {
                    Cell cell = gameState.getGameBoard().getCell(r, c);
                    if (cell instanceof EntityCell) {
                        EntityCell ec = (EntityCell) cell;
                        if (ec.getEntity() instanceof Player) {
                            currentRow = r;
                            currentCol = c;
                        }
                    }
                }
            }

            // Find the original position by looking at the alive move's position history
            // We need to slide backward from current position to oldPosition
            // This is complex - let's use a simpler approach: we know the end position (oldPosition)
            // and current position (currentRow, currentCol). We need to move player back.

            // Find direction to move back
            int dRow, dCol;
            if (currentRow > oldPosition.getRow()) {
                dRow = -1;
                dCol = 0;
            } else if (currentRow < oldPosition.getRow()) {
                dRow = 1;
                dCol = 0;
            } else if (currentCol > oldPosition.getCol()) {
                dRow = 0;
                dCol = -1;
            } else {
                dRow = 0;
                dCol = 1;
            }

            // Slide player back
            while (true) {
                int nextRow = currentRow + (-dRow);
                int nextCol = currentCol + (-dCol);

                if (nextRow == oldPosition.getRow() && nextCol == oldPosition.getCol()) {
                    // Reached target
                    break;
                }

                if (nextRow < 0 || nextRow >= numRows || nextCol < 0 || nextCol >= numCols) {
                    break;
                }

                Cell nextCell = gameState.getGameBoard().getCell(nextRow, nextCol);
                if (nextCell instanceof Wall) {
                    break;
                }

                currentRow = nextRow;
                currentCol = nextCol;
            }

            // Place player at oldPosition
            EntityCell oldEntityCell = gameState.getGameBoard().getEntityCell(oldPosition.getRow(), oldPosition.getCol());
            oldEntityCell.setentity(player);
            if (oldEntityCell instanceof StopCell) {
                ((StopCell) oldEntityCell).setPlayer(player);
            }

            gameState.getGameBoard().setPlayer(player);

            // Update current position tracking
            // Find where player actually ended up
            for (int r = 0; r < numRows; r++) {
                for (int c = 0; c < numCols; c++) {
                    Cell cell = gameState.getGameBoard().getCell(r, c);
                    if (cell instanceof EntityCell) {
                        EntityCell ec = (EntityCell) cell;
                        if (ec.getEntity() instanceof Player) {
                            alive.setNewPosition(new Position(r, c));
                        }
                    }
                }
            }

            return true;
        }

        return false;
    }
}
