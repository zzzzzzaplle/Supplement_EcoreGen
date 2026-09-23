import java.util.List;
import java.util.ArrayList;

public class GameBoardController {
    private GameBoard gameBoard;

    public GameBoardController() {
    }

    public GameBoardController(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public MoveResult makeMove(Direction direction) {
        Player player = gameBoard.getPlayer();
        if (player == null) {
            return new Invalid(null);
        }

        EntityCell playerCell = player.getOwner();
        if (playerCell == null) {
            return new Invalid(null);
        }

        Position currentPos = playerCell.getPosition();
        int dRow = direction.getRowOffset();
        int dCol = direction.getColOffset();

        // Try first step
        Position nextPos = currentPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
        if (nextPos == null) {
            return new Invalid(currentPos);
        }

        Cell nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());
        if (nextCell instanceof Wall) {
            return new Invalid(currentPos);
        }

        // We can slide at least one step, start sliding
        Position origPosition = new Position(currentPos.getRow(), currentPos.getCol());
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        Position slidePos = nextPos;

        while (true) {
            Cell cell = gameBoard.getCell(slidePos.getRow(), slidePos.getCol());
            
            if (cell instanceof Wall) {
                // Stop before wall - slidePos is the wall, so we need to go back one
                Position stopPos = slidePos.offsetBy(-dRow, -dCol);
                // Move player to stopPos
                EntityCell stopCell = (EntityCell) gameBoard.getCell(stopPos.getRow(), stopPos.getCol());
                playerCell.setEntity(null);
                stopCell.setEntity(player);
                return new Alive(stopPos, origPosition);
            }

            if (cell instanceof StopCell) {
                // Stop exactly on StopCell
                EntityCell stopCell = (EntityCell) cell;
                // Check if there's a mine on the stop cell? No, StopCell can only contain Player
                playerCell.setEntity(null);
                stopCell.setEntity(player);
                Alive result = new Alive(slidePos, origPosition);
                result.setCollectedGems(collectedGems);
                result.setCollectedExtraLives(collectedExtraLives);
                return result;
            }

            // Must be EntityCell
            EntityCell ec = (EntityCell) cell;
            Entity entity = ec.getEntity();

            if (entity instanceof Mine) {
                // Dead - return to original position, don't collect anything
                Dead result = new Dead(origPosition, origPosition, slidePos);
                return result;
            }

            if (entity instanceof Gem) {
                collectedGems.add(new Position(slidePos.getRow(), slidePos.getCol()));
                ec.setEntity(null);
            } else if (entity instanceof ExtraLife) {
                collectedExtraLives.add(new Position(slidePos.getRow(), slidePos.getCol()));
                ec.setEntity(null);
            }

            // Try next step
            Position tryNext = slidePos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
            if (tryNext == null) {
                // Hit boundary - stop here
                EntityCell stopCell = ec;
                playerCell.setEntity(null);
                stopCell.setEntity(player);
                Alive result = new Alive(slidePos, origPosition);
                result.setCollectedGems(collectedGems);
                result.setCollectedExtraLives(collectedExtraLives);
                return result;
            }

            Cell nextTryCell = gameBoard.getCell(tryNext.getRow(), tryNext.getCol());
            if (nextTryCell instanceof Wall) {
                // Stop here before the wall
                EntityCell stopCell = ec;
                playerCell.setEntity(null);
                stopCell.setEntity(player);
                Alive result = new Alive(slidePos, origPosition);
                result.setCollectedGems(collectedGems);
                result.setCollectedExtraLives(collectedExtraLives);
                return result;
            }

            slidePos = tryNext;
        }
    }

    public void undoMove(MoveResult prevMove) {
        // Implementation will be in GameController
    }
}
