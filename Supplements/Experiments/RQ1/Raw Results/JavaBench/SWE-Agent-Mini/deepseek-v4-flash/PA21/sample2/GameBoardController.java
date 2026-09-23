import java.util.ArrayList;
import java.util.List;

/**
 * Controls moves on the game board.
 */
public class GameBoardController {

    private GameBoard gameBoard;

    public GameBoardController() {
    }

    public GameBoardController(final GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(final GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    /**
     * Makes a move in the specified direction.
     *
     * @param direction The direction to move.
     * @return The result of the move.
     */
    public MoveResult makeMove(final Direction direction) {
        final Player player = gameBoard.getPlayer();
        if (player == null) {
            return new Invalid(null);
        }

        final EntityCell playerCell = player.getOwner();
        if (playerCell == null) {
            return new Invalid(null);
        }

        final Position startPos = playerCell.getPosition();
        final int dRow = direction.getRowOffset();
        final int dCol = direction.getColOffset();

        // Try the first step
        final Position firstStep = startPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
        if (firstStep == null) {
            // Can't even move one step (boundary)
            return new Invalid(startPos);
        }

        Cell firstCell = gameBoard.getCell(firstStep);
        if (firstCell instanceof Wall) {
            // Can't move (wall directly adjacent)
            return new Invalid(startPos);
        }

        // We can move at least one cell. Start sliding.
        Position currentPos = startPos;
        Position nextPos = firstStep;
        boolean hitMine = false;
        Position minePos = null;
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();

        while (nextPos != null) {
            final Cell cell = gameBoard.getCell(nextPos);

            if (cell instanceof Wall) {
                // Stop before the wall - player stays at currentPos
                break;
            }

            if (cell instanceof StopCell) {
                // Stop exactly on stop cell
                currentPos = nextPos;
                break;
            }

            // It's an EntityCell
            final EntityCell entityCell = (EntityCell) cell;
            final Entity entity = entityCell.getEntity();

            if (entity instanceof Mine) {
                hitMine = true;
                minePos = nextPos;
                break;
            }

            // Move to this cell
            currentPos = nextPos;

            if (entity instanceof Gem) {
                collectedGems.add(currentPos);
            } else if (entity instanceof ExtraLife) {
                collectedExtraLives.add(currentPos);
            }

            // Try next step
            nextPos = currentPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
        }

        if (hitMine) {
            // Dead: player stays at original position, nothing is collected
            return new Dead(startPos, startPos, minePos);
        }

        // Move player to final position
        final EntityCell newPlayerCell = (EntityCell) gameBoard.getCell(currentPos);
        playerCell.setEntity(null);
        newPlayerCell.setEntity(player);
        gameBoard.setPlayer(player);

        // Remove collected entities from the board
        for (final Position gemPos : collectedGems) {
            final EntityCell gemCell = (EntityCell) gameBoard.getCell(gemPos);
            gemCell.setEntity(null);
        }
        for (final Position extraLifePos : collectedExtraLives) {
            final EntityCell extraLifeCell = (EntityCell) gameBoard.getCell(extraLifePos);
            extraLifeCell.setEntity(null);
        }

        final Alive aliveResult = new Alive(startPos, currentPos);
        aliveResult.setCollectedGems(collectedGems);
        aliveResult.setCollectedExtraLives(collectedExtraLives);
        return aliveResult;
    }

    /**
     * Undoes a previous move.
     *
     * @param prevMove The move to undo.
     */
    public void undoMove(final MoveResult prevMove) {
        if (prevMove instanceof Alive) {
            final Alive alive = (Alive) prevMove;

            // Get the player's current cell (position after the move)
            final Player player = gameBoard.getPlayer();
            final EntityCell currentPlayerCell = player.getOwner();

            // Move player back to original position
            final Position origPos = alive.getOrigPosition();
            final EntityCell origCell = (EntityCell) gameBoard.getCell(origPos);

            currentPlayerCell.setEntity(null);
            origCell.setEntity(player);
            gameBoard.setPlayer(player);

            // Restore collected gems
            for (final Position gemPos : alive.getCollectedGems()) {
                final EntityCell gemCell = (EntityCell) gameBoard.getCell(gemPos);
                gemCell.setEntity(new Gem());
            }

            // Restore collected extra lives (remove the life gain and restore entities)
            for (final Position extraLifePos : alive.getCollectedExtraLives()) {
                final EntityCell extraLifeCell = (EntityCell) gameBoard.getCell(extraLifePos);
                extraLifeCell.setEntity(new ExtraLife());
            }
        }
    }
}
