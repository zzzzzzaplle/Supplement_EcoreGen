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

        Position origPosition = playerCell.getPosition();
        Position currentPosition = origPosition;
        int numRows = gameBoard.getNumRows();
        int numCols = gameBoard.getNumCols();
        int dRow = direction.getRowOffset();
        int dCol = direction.getColOffset();

        List<Position> visitedGems = new ArrayList<>();
        List<Position> visitedExtraLives = new ArrayList<>();
        int steps = 0;
        boolean hitMine = false;
        Position minePos = null;

        while (true) {
            Position next = currentPosition.offsetByOrNull(dRow, dCol, numRows, numCols);
            if (next == null) {
                break;
            }

            Cell nextCell = gameBoard.getCell(next.getRow(), next.getCol());
            if (nextCell instanceof Wall) {
                break;
            }

            EntityCell nextEntityCell = (EntityCell) nextCell;
            Entity entity = nextEntityCell.getEntity();

            if (entity instanceof Mine) {
                hitMine = true;
                minePos = next;
                break;
            }

            currentPosition = next;
            steps++;

            if (entity instanceof Gem) {
                visitedGems.add(next);
            } else if (entity instanceof ExtraLife) {
                visitedExtraLives.add(next);
            }

            if (nextCell instanceof StopCell) {
                break;
            }
        }

        if (hitMine) {
            return new Dead(origPosition, minePos);
        }

        if (steps == 0) {
            return new Invalid(origPosition);
        }

        for (Position p : visitedGems) {
            EntityCell ec = gameBoard.getEntityCell(p);
            if (ec != null) {
                ec.setEntity(null);
            }
        }
        for (Position p : visitedExtraLives) {
            EntityCell ec = gameBoard.getEntityCell(p);
            if (ec != null) {
                ec.setEntity(null);
            }
        }

        playerCell.setEntity(null);
        EntityCell newPlayerCell = gameBoard.getEntityCell(currentPosition);
        if (newPlayerCell != null) {
            newPlayerCell.setEntity(player);
        }

        return new Alive(currentPosition, origPosition, visitedGems, visitedExtraLives);
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive)) {
            return;
        }
        Alive alive = (Alive) prevMove;
        Player player = gameBoard.getPlayer();
        if (player == null) {
            return;
        }
        EntityCell currentCell = player.getOwner();
        if (currentCell != null) {
            currentCell.setEntity(null);
        }

        EntityCell originalCell = gameBoard.getEntityCell(alive.getOrigPosition());
        if (originalCell != null) {
            originalCell.setEntity(player);
        }

        for (Position gemPos : alive.getCollectedGems()) {
            EntityCell ec = gameBoard.getEntityCell(gemPos);
            if (ec != null) {
                ec.setEntity(new Gem());
            }
        }
        for (Position lifePos : alive.getCollectedExtraLives()) {
            EntityCell ec = gameBoard.getEntityCell(lifePos);
            if (ec != null) {
                ec.setEntity(new ExtraLife());
            }
        }
    }
}
