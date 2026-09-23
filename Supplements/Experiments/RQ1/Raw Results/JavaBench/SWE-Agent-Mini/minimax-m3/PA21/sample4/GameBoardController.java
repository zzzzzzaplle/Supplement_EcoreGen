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
        // Find player position
        Position playerPos = null;
        Player player = null;
        for (int r = 0; r < gameBoard.getNumRows(); r++) {
            for (int c = 0; c < gameBoard.getNumCols(); c++) {
                Cell cell = gameBoard.getCell(r, c);
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e instanceof Player) {
                        playerPos = new Position(r, c);
                        player = (Player) e;
                        break;
                    }
                }
            }
            if (playerPos != null) break;
        }

        if (playerPos == null || player == null) {
            return new Invalid(null);
        }

        Position origPosition = new Position(playerPos.getRow(), playerPos.getCol());
        PositionOffset offset = direction.getOffset();
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();

        Position current = new Position(playerPos.getRow(), playerPos.getCol());
        Position next = current.offsetByOrNull(offset, gameBoard.getNumRows(), gameBoard.getNumCols());

        // Check first step - if blocked, Invalid
        if (next == null) {
            return new Invalid(origPosition);
        }

        Cell nextCell = gameBoard.getCell(next.getRow(), next.getCol());
        if (nextCell instanceof Wall) {
            return new Invalid(origPosition);
        }

        // Start sliding
        Position lastValidPos = current;
        while (next != null) {
            Cell cell = gameBoard.getCell(next.getRow(), next.getCol());
            if (cell instanceof Wall) {
                break;
            }
            if (cell instanceof StopCell) {
                lastValidPos = next;
                break;
            }
            if (cell instanceof EntityCell) {
                Entity e = ((EntityCell) cell).getEntity();
                if (e instanceof Mine) {
                    // Dead: board remains unchanged
                    Dead dead = new Dead(origPosition, origPosition, next);
                    return dead;
                }
                if (e instanceof Gem) {
                    collectedGems.add(new Position(next.getRow(), next.getCol()));
                } else if (e instanceof ExtraLife) {
                    collectedExtraLives.add(new Position(next.getRow(), next.getCol()));
                }
            }
            lastValidPos = next;
            current = next;
            next = current.offsetByOrNull(offset, gameBoard.getNumRows(), gameBoard.getNumCols());
        }

        // Successful alive move
        // Remove player from original position
        Cell origCell = gameBoard.getCell(origPosition.getRow(), origPosition.getCol());
        if (origCell instanceof EntityCell) {
            ((EntityCell) origCell).setEntity(null);
        }
        // Remove collected gems
        for (Position p : collectedGems) {
            Cell c = gameBoard.getCell(p.getRow(), p.getCol());
            if (c instanceof EntityCell) {
                ((EntityCell) c).setEntity(null);
            }
        }
        // Remove collected extra lives
        for (Position p : collectedExtraLives) {
            Cell c = gameBoard.getCell(p.getRow(), p.getCol());
            if (c instanceof EntityCell) {
                ((EntityCell) c).setEntity(null);
            }
        }
        // Place player at destination
        Cell destCell = gameBoard.getCell(lastValidPos.getRow(), lastValidPos.getCol());
        if (destCell instanceof EntityCell) {
            ((EntityCell) destCell).setEntity(player);
            player.setOwner((EntityCell) destCell);
        }

        Alive alive = new Alive(lastValidPos, origPosition, collectedGems, collectedExtraLives);
        return alive;
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive)) {
            return;
        }
        Alive alive = (Alive) prevMove;
        Position orig = alive.getOrigPosition();
        Position dest = alive.getNewPosition();

        // Find and remove player from dest
        Cell destCell = gameBoard.getCell(dest.getRow(), dest.getCol());
        Player player = null;
        if (destCell instanceof EntityCell) {
            player = (Player) ((EntityCell) destCell).getEntity();
            ((EntityCell) destCell).setEntity(null);
        }

        // Restore player at orig
        Cell origCell = gameBoard.getCell(orig.getRow(), orig.getCol());
        if (origCell instanceof EntityCell && player != null) {
            ((EntityCell) origCell).setEntity(player);
            player.setOwner((EntityCell) origCell);
        }

        // Restore gems
        for (Position p : alive.getCollectedGems()) {
            Cell c = gameBoard.getCell(p.getRow(), p.getCol());
            if (c instanceof EntityCell) {
                ((EntityCell) c).setEntity(new Gem());
            }
        }
        // Restore extra lives
        for (Position p : alive.getCollectedExtraLives()) {
            Cell c = gameBoard.getCell(p.getRow(), p.getCol());
            if (c instanceof EntityCell) {
                ((EntityCell) c).setEntity(new ExtraLife());
            }
        }
    }
}
