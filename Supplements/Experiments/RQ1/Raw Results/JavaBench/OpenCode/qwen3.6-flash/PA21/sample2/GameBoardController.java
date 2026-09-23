import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameBoardController {
    private GameBoard gameBoard;

    public GameBoardController() {
        this.gameBoard = null;
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
        Position startPos = findPlayerPosition();
        if (startPos == null) {
            return new Invalid();
        }

        Position originalPos = startPos;
        int dRow = direction.getRowOffset();
        int dCol = direction.getColOffset();
        Position currentPos = startPos;

        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();

        while (true) {
            Position nextPos = currentPos.offsetBy(dRow, dCol);
            if (nextPos == null) {
                break;
            }

            Cell nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());

            if (nextCell instanceof Wall) {
                break;
            }

            if (nextPos.getRow() < 0 || nextPos.getRow() >= gameBoard.getNumRows() ||
                nextPos.getCol() < 0 || nextPos.getCol() >= gameBoard.getNumCols()) {
                break;
            }

            currentPos = nextPos;

            if (nextCell instanceof EntityCell) {
                EntityCell entityCell = (EntityCell) nextCell;
                Entity entity = entityCell.getEntity();

                if (entity instanceof Gem) {
                    collectedGems.add(nextPos);
                    entityCell.setEntity(null);
                } else if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(nextPos);
                    entityCell.setEntity(null);
                } else if (entity instanceof Mine) {
                    EntityCell startCell = gameBoard.getEntityCell(originalPos);
                    if (startCell != null) {
                        Entity player = startCell.getEntity();
                        entityCell.setEntity(player);
                        startCell.setEntity(null);
                    }
                    return new Dead(nextPos);
                }
            }

            if (nextCell instanceof StopCell) {
                break;
            }
        }

        if (currentPos.equals(originalPos)) {
            return new Invalid();
        }

        EntityCell startCell = gameBoard.getEntityCell(originalPos);
        EntityCell currentCell = gameBoard.getEntityCell(currentPos);
        Entity player = startCell.getEntity();

        startCell.setEntity(null);
        currentCell.setEntity(player);

        return new Alive(collectedGems, collectedExtraLives);
    }

    private Position findPlayerPosition() {
        for (int r = 0; r < gameBoard.getNumRows(); r++) {
            for (int c = 0; c < gameBoard.getNumCols(); c++) {
                Cell cell = gameBoard.getCell(r, c);
                if (cell instanceof EntityCell) {
                    Entity entity = ((EntityCell) cell).getEntity();
                    if (entity instanceof Player) {
                        return new Position(r, c);
                    }
                }
            }
        }
        return null;
    }

    public void undoMove(MoveResult prevMove) {
    }
}
