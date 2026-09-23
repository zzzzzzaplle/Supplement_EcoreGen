import java.util.List;
import java.util.ArrayList;
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
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    public MoveResult makeMove(Direction direction) {
        Objects.requireNonNull(direction);

        Position playerPos = getPlayerPosition();
        int numRows = gameBoard.getNumRows();
        int numCols = gameBoard.getNumCols();
        Position origPosition = new Position(playerPos.getRow(), playerPos.getCol());

        PositionOffset offset = direction.getOffset();
        int dRow = offset.getDRow();
        int dCol = offset.getDCol();

        int firstRow = origPosition.getRow() + dRow;
        int firstCol = origPosition.getCol() + dCol;
        if (firstRow < 0 || firstRow >= numRows || firstCol < 0 || firstCol >= numCols) {
            return new Invalid(origPosition);
        }

        if (gameBoard.getCell(firstRow, firstCol) instanceof Wall) {
            return new Invalid(origPosition);
        }

        int currentRow = firstRow;
        int currentCol = firstCol;
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        boolean hitMine = false;
        Position minePos = null;

        Cell origCell = gameBoard.getCell(origPosition.getRow(), origPosition.getCol());
        if (origCell instanceof EntityCell) {
            ((EntityCell) origCell).setEntity(null);
        }

        while (true) {
            Cell cell = gameBoard.getCell(currentRow, currentCol);

            if (cell instanceof Wall) {
                currentRow -= dRow;
                currentCol -= dCol;
                break;
            } else if (cell instanceof EntityCell && ((EntityCell) cell).getEntity() instanceof Mine) {
                Cell mineCell = (EntityCell) cell;
                minePos = new Position(currentRow, currentCol);
                hitMine = true;
                ((EntityCell) origCell).setEntity(gameBoard.getPlayer());
                break;
            } else if (cell instanceof EntityCell && ((EntityCell) cell).getEntity() instanceof Gem) {
                collectedGems.add(new Position(currentRow, currentCol));
                ((EntityCell) cell).setEntity(null);
            } else if (cell instanceof EntityCell && ((EntityCell) cell).getEntity() instanceof ExtraLife) {
                collectedExtraLives.add(new Position(currentRow, currentCol));
                ((EntityCell) cell).setEntity(null);
            }

            Cell newCell = gameBoard.getCell(currentRow, currentCol);
            if (newCell instanceof EntityCell) {
                ((EntityCell) newCell).setEntity(gameBoard.getPlayer());
            }

            if (cell instanceof StopCell) {
                break;
            }

            int nextRow = currentRow + dRow;
            int nextCol = currentCol + dCol;
            if (nextRow < 0 || nextRow >= numRows || nextCol < 0 || nextCol >= numCols) {
                break;
            }
            currentRow = nextRow;
            currentCol = nextCol;
        }

        if (hitMine) {
            Dead dead = new Dead(origPosition);
            dead.setMinePosition(minePos);
            return dead;
        }

        Position finalPosition = new Position(currentRow, currentCol);
        if (finalPosition.equals(origPosition)) {
            ((EntityCell) gameBoard.getCell(origPosition.getRow(), origPosition.getCol())).setEntity(gameBoard.getPlayer());
            return new Invalid(origPosition);
        }

        Alive alive = new Alive(origPosition, finalPosition);
        alive.setCollectedGems(collectedGems);
        alive.setCollectedExtraLives(collectedExtraLives);
        return alive;
    }

    public void undoMove(MoveResult prevMove) {
        Objects.requireNonNull(prevMove);
        if (!(prevMove instanceof Valid)) {
            return;
        }

        Valid validMove = (Valid) prevMove;
        Position origPosition = validMove.getOrigPosition();
        Position newPostion = validMove.getNewPosition();
        List<Position> collectedGems = validMove.getCollectedGems();
        List<Position> collectedExtraLives = validMove.getCollectedExtraLives();

        Player player = gameBoard.getPlayer();
        if (player != null) {
            Cell currentCell = gameBoard.getCell(newPostion.getRow(), newPostion.getCol());
            if (currentCell instanceof EntityCell) {
                ((EntityCell) currentCell).setEntity(null);
            }
            Cell origCell = gameBoard.getCell(origPosition.getRow(), origPosition.getCol());
            if (origCell instanceof EntityCell) {
                ((EntityCell) origCell).setEntity(player);
            }
        }

        for (Position pos : collectedGems) {
            Cell cell = gameBoard.getCell(pos.getRow(), pos.getCol());
            if (cell instanceof EntityCell) {
                ((EntityCell) cell).setEntity(new Gem());
            }
        }

        for (Position pos : collectedExtraLives) {
            Cell cell = gameBoard.getCell(pos.getRow(), pos.getCol());
            if (cell instanceof EntityCell) {
                ((EntityCell) cell).setEntity(new ExtraLife());
            }
        }
    }

    private Position getPlayerPosition() {
        for (int r = 0; r < gameBoard.getNumRows(); r++) {
            for (int c = 0; c < gameBoard.getNumCols(); c++) {
                Cell cell = gameBoard.getCell(r, c);
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e instanceof Player) return new Position(r, c);
                }
            }
        }
        throw new IllegalStateException("Player not found on the board");
    }
}
