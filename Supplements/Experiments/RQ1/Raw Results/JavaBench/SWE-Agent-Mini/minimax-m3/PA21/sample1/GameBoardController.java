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
        // Find the player's current position
        Position playerPos = null;
        for (int r = 0; r < gameBoard.getNumRows(); r++) {
            for (int c = 0; c < gameBoard.getNumCols(); c++) {
                Cell cell = gameBoard.getCell(r, c);
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e instanceof Player) {
                        playerPos = new Position(r, c);
                        break;
                    }
                }
            }
            if (playerPos != null) break;
        }

        if (playerPos == null) {
            return new Invalid();
        }

        Position origPosition = playerPos;
        int dRow = direction.getRowOffset();
        int dCol = direction.getColOffset();
        Position currentPos = playerPos;
        Position minePos = null;
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();

        // Track items collected along the way (but only if no mine is hit)
        // First pass: check the entire slide path
        Position probePos = currentPos;
        while (true) {
            Position next = probePos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
            if (next == null) {
                break; // boundary
            }
            Cell nextCell = gameBoard.getCell(next.getRow(), next.getCol());
            if (nextCell instanceof Wall) {
                break; // wall
            }
            if (nextCell instanceof StopCell) {
                // stop on the stopcell
                if (probePos.equals(playerPos)) {
                    // blocked on first step
                    return new Invalid();
                }
                // Move the player to the stop cell
                EntityCell fromCell = (EntityCell) gameBoard.getCell(currentPos.getRow(), currentPos.getCol());
                EntityCell toCell = (StopCell) nextCell;
                toCell.setEntity(fromCell.getEntity());
                fromCell.setEntity(null);
                Alive alive = new Alive(next, origPosition);
                alive.setCollectedGems(collectedGems);
                alive.setCollectedExtraLives(collectedExtraLives);
                return alive;
            }
            if (nextCell instanceof EntityCell) {
                Entity e = ((EntityCell) nextCell).getEntity();
                if (e instanceof Mine) {
                    minePos = next;
                    // Board unchanged, player remains at original position
                    return new Dead(origPosition, origPosition, minePos);
                }
                if (e instanceof Gem) {
                    collectedGems.add(new Position(next.getRow(), next.getCol()));
                } else if (e instanceof ExtraLife) {
                    collectedExtraLives.add(new Position(next.getRow(), next.getCol()));
                }
            }
            probePos = next;
        }

        // Reached boundary or wall - check if any movement occurred
        if (probePos.equals(playerPos)) {
            // Check whether the very first cell is a wall (not collected in the loop because we break before moving)
            Position firstStep = playerPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
            if (firstStep == null) {
                return new Invalid();
            }
            Cell firstCell = gameBoard.getCell(firstStep.getRow(), firstStep.getCol());
            if (firstCell instanceof Wall) {
                return new Invalid();
            }
        }

        // Move the player to probePos
        Position finalPos = probePos;
        EntityCell fromCell = (EntityCell) gameBoard.getCell(playerPos.getRow(), playerPos.getCol());
        EntityCell toCell = (EntityCell) gameBoard.getCell(finalPos.getRow(), finalPos.getCol());
        toCell.setEntity(fromCell.getEntity());
        fromCell.setEntity(null);

        // Remove collected gems and extra lives from the board
        for (Position gemPos : collectedGems) {
            EntityCell ec = (EntityCell) gameBoard.getCell(gemPos.getRow(), gemPos.getCol());
            ec.setEntity(null);
        }
        for (Position lifePos : collectedExtraLives) {
            EntityCell ec = (EntityCell) gameBoard.getCell(lifePos.getRow(), lifePos.getCol());
            ec.setEntity(null);
        }

        Alive alive = new Alive(finalPos, origPosition);
        alive.setCollectedGems(collectedGems);
        alive.setCollectedExtraLives(collectedExtraLives);
        return alive;
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive)) {
            return;
        }
        Alive alive = (Alive) prevMove;
        // Move player back to origPosition
        EntityCell currentCell = (EntityCell) gameBoard.getCell(alive.getNewPosition().getRow(), alive.getNewPosition().getCol());
        EntityCell origCell = (EntityCell) gameBoard.getCell(alive.getOrigPosition().getRow(), alive.getOrigPosition().getCol());
        origCell.setEntity(currentCell.getEntity());
        currentCell.setEntity(null);

        // Restore collected gems and extra lives
        for (Position gemPos : alive.getCollectedGems()) {
            EntityCell ec = (EntityCell) gameBoard.getCell(gemPos.getRow(), gemPos.getCol());
            ec.setEntity(new Gem());
        }
        for (Position lifePos : alive.getCollectedExtraLives()) {
            EntityCell ec = (EntityCell) gameBoard.getCell(lifePos.getRow(), lifePos.getCol());
            ec.setEntity(new ExtraLife());
        }
    }
}
