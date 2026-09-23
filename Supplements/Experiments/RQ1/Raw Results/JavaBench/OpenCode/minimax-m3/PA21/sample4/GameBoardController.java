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
        EntityCell playerCell = player.getOwner();
        Position origPosition = playerCell.getPosition();
        PositionOffset offset = direction.getOffset();

        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        Position current = origPosition;
        boolean moved = false;
        boolean mineHit = false;
        Position minePos = null;

        while (true) {
            Position next = current.offsetByOrNull(offset, gameBoard.getNumRows(), gameBoard.getNumCols());
            if (next == null) {
                break;
            }
            Cell nextCell = gameBoard.getCell(next.getRow(), next.getCol());
            if (nextCell instanceof Wall) {
                break;
            }
            if (nextCell instanceof StopCell) {
                moved = true;
                current = next;
                break;
            }
            if (nextCell instanceof EntityCell) {
                Entity entity = ((EntityCell) nextCell).getEntity();
                if (entity instanceof Mine) {
                    mineHit = true;
                    minePos = next;
                    break;
                } else if (entity instanceof Gem) {
                    collectedGems.add(next);
                } else if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(next);
                }
            }
            current = next;
            moved = true;
        }

        if (!moved && !mineHit) {
            return new Invalid(origPosition);
        }

        if (mineHit) {
            return new Dead(origPosition, origPosition, minePos);
        }

        EntityCell newCell = gameBoard.getEntityCell(current);
        newCell.setEntity(player);
        playerCell.setEntity(null);
        player.setOwner(newCell);

        for (Position p : collectedGems) {
            gameBoard.getEntityCell(p).setEntity(null);
        }
        for (Position p : collectedExtraLives) {
            gameBoard.getEntityCell(p).setEntity(null);
        }

        return new Alive(current, origPosition, collectedGems, collectedExtraLives);
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive)) {
            return;
        }
        Alive alive = (Alive) prevMove;
        Player player = gameBoard.getPlayer();
        Position orig = alive.getOrigPosition();
        Position newPos = alive.getNewPosition();

        EntityCell newCell = gameBoard.getEntityCell(newPos);
        newCell.setEntity(null);

        EntityCell origCell = gameBoard.getEntityCell(orig);
        origCell.setEntity(player);
        player.setOwner(origCell);

        for (Position p : alive.getCollectedGems()) {
            gameBoard.getEntityCell(p).setEntity(new Gem());
        }
        for (Position p : alive.getCollectedExtraLives()) {
            gameBoard.getEntityCell(p).setEntity(new ExtraLife());
        }
    }
}
