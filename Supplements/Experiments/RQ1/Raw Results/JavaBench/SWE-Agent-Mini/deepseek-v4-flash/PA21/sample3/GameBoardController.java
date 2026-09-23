import java.util.ArrayList;
import java.util.List;

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
            return new Invalid();
        }

        EntityCell playerCell = player.getOwner();
        if (playerCell == null) {
            return new Invalid();
        }

        Position currentPos = playerCell.getPosition();
        Position origPos = new Position(currentPos.getRow(), currentPos.getCol());

        int dRow = direction.getRowOffset();
        int dCol = direction.getColOffset();

        // Check if first step is blocked
        Position nextPos = currentPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
        if (nextPos == null) {
            return new Invalid(origPos);
        }

        Cell nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());
        if (nextCell instanceof Wall) {
            return new Invalid(origPos);
        }

        // Start sliding
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        boolean hitMine = false;
        Position minePos = null;

        Position slidePos = currentPos;
        Position newPlayerPos = null;

        while (true) {
            Position candidatePos = slidePos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
            if (candidatePos == null) {
                // Hit boundary - stop at current position
                newPlayerPos = slidePos;
                break;
            }

            Cell candidateCell = gameBoard.getCell(candidatePos.getRow(), candidatePos.getCol());

            if (candidateCell instanceof Wall) {
                // Stop before the wall
                newPlayerPos = slidePos;
                break;
            }

            // Can move into this cell
            slidePos = candidatePos;

            if (candidateCell instanceof StopCell) {
                // Stop on StopCell
                newPlayerPos = slidePos;
                break;
            }

            if (candidateCell instanceof EntityCell) {
                EntityCell ec = (EntityCell) candidateCell;
                Entity entity = ec.getEntity();
                if (entity instanceof Mine) {
                    hitMine = true;
                    minePos = new Position(slidePos.getRow(), slidePos.getCol());
                    newPlayerPos = origPos; // Stay at original position
                    break;
                }
                if (entity instanceof Gem) {
                    collectedGems.add(new Position(slidePos.getRow(), slidePos.getCol()));
                }
                if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(new Position(slidePos.getRow(), slidePos.getCol()));
                }
            }
        }

        if (slidePos.getRow() == currentPos.getRow() && slidePos.getCol() == currentPos.getCol()) {
            // Didn't move at all
            return new Invalid(origPos);
        }

        if (hitMine) {
            Dead dead = new Dead(origPos, origPos, minePos);
            return dead;
        }

        // Alive move - execute the move
        // Remove player from current cell
        playerCell.setEntity(null);

        // Move player to new position
        EntityCell targetCell = (EntityCell) gameBoard.getCell(newPlayerPos.getRow(), newPlayerPos.getCol());
        targetCell.setEntity(player);

        // Remove collected gems and extra lives
        for (Position gemPos : collectedGems) {
            EntityCell gemCell = (EntityCell) gameBoard.getCell(gemPos.getRow(), gemPos.getCol());
            gemCell.setEntity(null);
        }
        for (Position lifePos : collectedExtraLives) {
            EntityCell lifeCell = (EntityCell) gameBoard.getCell(lifePos.getRow(), lifePos.getCol());
            lifeCell.setEntity(null);
        }

        Alive alive = new Alive(newPlayerPos, origPos);
        alive.getCollectedGems().addAll(collectedGems);
        alive.getCollectedExtraLives().addAll(collectedExtraLives);
        return alive;
    }

    public void undoMove(MoveResult prevMove) {
        if (prevMove instanceof Alive) {
            Alive alive = (Alive) prevMove;

            // Get player's current position (newPosition)
            Position newPos = alive.getNewPosition();
            Position origPos = alive.getOrigPosition();

            // Move player back
            EntityCell currentCell = (EntityCell) gameBoard.getCell(newPos.getRow(), newPos.getCol());
            Player player = gameBoard.getPlayer();
            currentCell.setEntity(null);

            EntityCell origCell = (EntityCell) gameBoard.getCell(origPos.getRow(), origPos.getCol());
            origCell.setEntity(player);

            // Restore collected gems
            for (Position gemPos : alive.getCollectedGems()) {
                EntityCell gemCell = (EntityCell) gameBoard.getCell(gemPos.getRow(), gemPos.getCol());
                gemCell.setEntity(new Gem());
            }

            // Restore collected extra lives
            for (Position lifePos : alive.getCollectedExtraLives()) {
                EntityCell lifeCell = (EntityCell) gameBoard.getCell(lifePos.getRow(), lifePos.getCol());
                lifeCell.setEntity(new ExtraLife());
            }
        }
    }
}
