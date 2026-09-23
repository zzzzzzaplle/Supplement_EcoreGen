import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameBoardController {

    private GameBoard gameBoard;

    public GameBoardController() {
    }

    public GameBoardController(GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public MoveResult makeMove(Direction direction) {
        Objects.requireNonNull(direction);

        Player player = gameBoard.getPlayer();
        EntityCell startCell = player.getOwner();
        Position startPosition = startCell.getPosition();
        Position currentPosition = startPosition;

        List<Position> passedGemPositions = new ArrayList<>();
        List<Position> passedExtraLifePositions = new ArrayList<>();

        PositionOffset offset = direction.getOffset();
        int numRows = gameBoard.getNumRows();
        int numCols = gameBoard.getNumCols();

        while (true) {
            Position nextPosition = currentPosition.offsetByOrNull(offset, numRows, numCols);
            if (nextPosition == null) {
                break;
            }
            Cell nextCell = gameBoard.getCell(nextPosition.getRow(), nextPosition.getCol());
            if (nextCell instanceof Wall) {
                break;
            }
            EntityCell entityCell = (EntityCell) nextCell;
            Entity entity = entityCell.getEntity();

            if (entity instanceof Mine) {
                return new Dead(startPosition, nextPosition);
            }

            if (entity instanceof Gem) {
                passedGemPositions.add(nextPosition);
            } else if (entity instanceof ExtraLife) {
                passedExtraLifePositions.add(nextPosition);
            }

            currentPosition = nextPosition;

            if (nextCell instanceof StopCell) {
                break;
            }
        }

        if (currentPosition.equals(startPosition)) {
            return new Invalid(startPosition);
        }

        for (Position pos : passedGemPositions) {
            EntityCell ec = gameBoard.getEntityCell(pos);
            ec.setEntity(null);
        }
        for (Position pos : passedExtraLifePositions) {
            EntityCell ec = gameBoard.getEntityCell(pos);
            ec.setEntity(null);
        }

        startCell.setEntity(null);
        EntityCell newOwner = gameBoard.getEntityCell(currentPosition);
        newOwner.setEntity(player);

        return new Alive(currentPosition, startPosition, passedGemPositions, passedExtraLifePositions);
    }

    public void undoMove(MoveResult prevMove) {
        Objects.requireNonNull(prevMove);
        if (!(prevMove instanceof Alive)) {
            return;
        }
        Alive alive = (Alive) prevMove;
        Player player = gameBoard.getPlayer();

        EntityCell currentOwner = player.getOwner();
        currentOwner.setEntity(null);
        EntityCell newOwner = gameBoard.getEntityCell(alive.getOrigPosition());
        newOwner.setEntity(player);

        for (Position pos : alive.getCollectedGems()) {
            EntityCell ec = gameBoard.getEntityCell(pos);
            ec.setEntity(new Gem());
        }
        for (Position pos : alive.getCollectedExtraLives()) {
            EntityCell ec = gameBoard.getEntityCell(pos);
            ec.setEntity(new ExtraLife());
        }
    }
}
