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
        int numRows = gameBoard.getNumRows();
        int numCols = gameBoard.getNumCols();
        int startRow = -1;
        int startCol = -1;

        // Find player position
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = gameBoard.getCell(r, c);
                if (cell instanceof EntityCell) {
                    EntityCell ec = (EntityCell) cell;
                    if (ec.getEntity() instanceof Player) {
                        startRow = r;
                        startCol = c;
                    }
                }
            }
        }

        if (startRow == -1) {
            return new Invalid(new Position(0, 0));
        }

        Position originalPos = new Position(startRow, startCol);
        int dRow = direction.getRowOffset();
        int dCol = direction.getColOffset();

        int currentRow = startRow;
        int currentCol = startCol;
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        boolean moved = false;

        // Slide step by step
        while (true) {
            int nextRow = currentRow + dRow;
            int nextCol = currentCol + dCol;

            // Check boundary
            if (nextRow < 0 || nextRow >= numRows || nextCol < 0 || nextCol >= numCols) {
                // Hit boundary - stop
                break;
            }

            Cell nextCell = gameBoard.getCell(nextRow, nextCol);

            // Check wall
            if (nextCell instanceof Wall) {
                // Stop before wall, don't enter
                break;
            }

            // Check mine
            if (nextCell instanceof EntityCell) {
                EntityCell ec = (EntityCell) nextCell;
                if (ec.getEntity() instanceof Mine) {
                    // Hit mine - dead
                    Dead dead = new Dead(originalPos, new Position(nextRow, nextCol));
                    return dead;
                }
            }

            // Move to next cell
            moved = true;
            currentRow = nextRow;
            currentCol = nextCol;

            // Check what's on the cell
            Cell currentCell = gameBoard.getCell(currentRow, currentCol);
            if (currentCell instanceof EntityCell) {
                EntityCell ec = (EntityCell) currentCell;
                if (ec.getEntity() instanceof Gem) {
                    collectedGems.add(new Position(currentRow, currentCol));
                    ec.setentity(null);
                } else if (ec.getEntity() instanceof ExtraLife) {
                    collectedExtraLives.add(new Position(currentRow, currentCol));
                    ec.setentity(null);
                }
            }

            // Check if StopCell - stop
            if (currentCell instanceof StopCell) {
                break;
            }
        }

        // If we never moved, return Invalid
        if (!moved) {
            return new Invalid(originalPos);
        }

        // Move player to new position
        Position newPos = new Position(currentRow, currentCol);
        Player player = gameBoard.getPlayer();

        // Remove player from original position
        EntityCell originalEntityCell = gameBoard.getEntityCell(originalPos);
        originalEntityCell.setentity(null);

        // Set player at new position
        EntityCell newEntityCell = gameBoard.getEntityCell(newPos);
        newEntityCell.setentity(player);
        // Update player position reference via the cell's position
        // The gameBoard tracks player, but if it's a StopCell we need special handling
        if (newEntityCell instanceof StopCell) {
            ((StopCell) newEntityCell).setPlayer(player);
        }

        gameBoard.setPlayer(player);

        Alive alive = new Alive(newPos);
        alive.setCollectedGems(collectedGems);
        alive.setCollectedExtraLives(collectedExtraLives);
        return alive;
    }

    public void undoMove(MoveResult prevMove) {
        if (prevMove instanceof Alive) {
            // Not implemented here, handled by GameStateController
        }
    }
}
