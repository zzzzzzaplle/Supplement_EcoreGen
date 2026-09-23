import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

/**
 * Controls movement operations on the game board.
 */
public class GameBoardController {

    private GameBoard gameBoard;

    public GameBoardController() {
    }

    public GameBoardController(GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    /**
     * Gets the game board.
     *
     * @return the game board.
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * Sets the game board.
     *
     * @param gameBoard the game board.
     */
    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    /**
     * Executes a move in the given direction.
     *
     * @param direction the direction to move.
     * @return a MoveResult describing the outcome.
     */
    public MoveResult makeMove(Direction direction) {
        Objects.requireNonNull(direction);

        Player player = gameBoard.getPlayer();
        if (player == null) {
            return new Invalid();
        }

        EntityCell owner = (EntityCell) player.getOwner();
        if (owner == null) {
            return new Invalid();
        }

        Position startPos = owner.getPosition();
        Position currentPos = startPos;
        PositionOffset offset = direction.getOffset();

        Alive alive = new Alive(startPos, startPos);
        List<Position> gemsCollected = alive.getCollectedGems();
        List<Position> livesCollected = alive.getCollectedExtraLives();

        // Slide until we can't continue
        while (true) {
            Position nextPos = currentPos.offsetByOrNull(offset, gameBoard.numRows, gameBoard.numCols);
            if (nextPos == null) {
                // Hit boundary, stop here
                break;
            }

            Cell nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());

            if (nextCell instanceof Wall) {
                // Stop before the wall, at currentPos
                break;
            }

            if (nextCell instanceof EntityCell) {
                EntityCell nextEntityCell = (EntityCell) nextCell;
                Entity entity = nextEntityCell.getEntity();

                if (entity instanceof Mine) {
                    // Dead! Stay at original position, nothing collected
                    Dead dead = new Dead(startPos, nextPos);
                    return dead;
                }

                if (entity instanceof Gem) {
                    // Collect the gem
                    gemsCollected.add(nextPos);
                    // Remove gem from board
                    nextEntityCell.setentity(null);
                    currentPos = nextPos;
                    continue;
                }

                if (entity instanceof ExtraLife) {
                    // Collect the extra life
                    livesCollected.add(nextPos);
                    // Remove extra life from board
                    nextEntityCell.setentity(null);
                    currentPos = nextPos;
                    continue;
                }

                if (entity instanceof Player) {
                    // Already at a stop cell with player, can't move further
                    break;
                }

                // StopCell (maybe with entity set) or empty EntityCell - normal stop
                // Continue sliding through empty EntityCells
                if (nextEntityCell instanceof StopCell) {
                    // Stop exactly on the StopCell
                    currentPos = nextPos;
                    break;
                }

                // Empty EntityCell - continue through it
                currentPos = nextPos;
                continue;
            }

            // Unknown cell type, stop
            break;
        }

        // Check if player actually moved
        if (currentPos.equals(startPos)) {
            return new Invalid(startPos);
        }

        // Remove player from old position
        if (owner != null) {
            owner.setentity(null);
        }

        // Place player at new position
        EntityCell targetCell = gameBoard.getEntityCell(currentPos);
        targetCell.setentity(player);
        gameBoard.setPlayer(player);

        alive.setNewPosition(currentPos);
        return alive;
    }
}
