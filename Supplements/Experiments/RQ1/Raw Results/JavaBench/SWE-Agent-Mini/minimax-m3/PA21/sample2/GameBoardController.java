import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameBoardController {
    private GameBoard gameBoard;

    public GameBoardController() {
        this.gameBoard = new GameBoard();
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
        Objects.requireNonNull(direction);
        Objects.requireNonNull(gameBoard);

        // Find the player position
        Position playerPos = findPlayer();
        if (playerPos == null) {
            return new Invalid();
        }

        PositionOffset offset = direction.getOffset();
        int dRow = offset.getDRow();
        int dCol = offset.getDCol();

        // First step check - is movement possible?
        Position firstStep = playerPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
        if (firstStep == null) {
            return new Invalid();
        }
        Cell firstCell = gameBoard.getCell(firstStep.getRow(), firstStep.getCol());
        if (firstCell instanceof Wall) {
            return new Invalid();
        }

        // Now slide step by step
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();

        Position current = playerPos;
        Position lastValid = playerPos;
        Position mineHit = null;

        while (true) {
            Position next = current.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
            if (next == null) {
                // Out of bounds - stop at current
                break;
            }
            Cell cell = gameBoard.getCell(next.getRow(), next.getCol());

            if (cell instanceof Wall) {
                // Stop before the wall
                break;
            }

            if (cell instanceof StopCell) {
                // Stop on the stop cell
                current = next;
                break;
            }

            // EntityCell
            EntityCell entityCell = (EntityCell) cell;
            Entity entity = entityCell.getEntity();

            if (entity instanceof Mine) {
                // Hit a mine - dead, position unchanged from before the move
                mineHit = next;
                break;
            }

            // Normal movement
            current = next;
            lastValid = next;

            if (entity instanceof Gem) {
                // Collect gem - remove from board
                entityCell.setEntity(null);
                collectedGems.add(next);
            } else if (entity instanceof ExtraLife) {
                // Collect extra life - remove from board
                entityCell.setEntity(null);
                collectedExtraLives.add(next);
            }
        }

        if (mineHit != null) {
            // Dead move - position doesn't change, but the player remains in the same cell
            // The position recorded for the player after a Dead move is the original position
            // Board remains unchanged - restore collected items? No, the spec says:
            // "The board remains unchanged from before the move" - so we must restore!
            // Wait: "any Gem or ExtraLife passed before the Mine is not collected or removed"
            // So gems/extralives passed before the mine are not collected.
            // But during our scan we already removed them. We need to restore them... 
            // However, looking more carefully: gems/extralives BEFORE the mine are not collected.
            // The mine itself is the obstacle. So we should not have collected anything.
            // Restore the collected items to the board
            for (Position p : collectedGems) {
                EntityCell ec = gameBoard.getEntityCell(p);
                ec.setEntity(new Gem());
            }
            for (Position p : collectedExtraLives) {
                EntityCell ec = gameBoard.getEntityCell(p);
                ec.setEntity(new ExtraLife());
            }
            return new Dead(playerPos, playerPos, mineHit);
        }

        // Check if movement happened
        if (lastValid.equals(playerPos)) {
            return new Invalid();
        }

        // Move the player: remove player from current cell, place at new position
        // First, we need to find the player at the new position
        EntityCell newPlayerCell = gameBoard.getEntityCell(lastValid);
        EntityCell oldPlayerCell = gameBoard.getEntityCell(playerPos);

        // Get the player entity
        Player player = null;
        for (int r = 0; r < gameBoard.getNumRows(); r++) {
            for (int c = 0; c < gameBoard.getNumCols(); c++) {
                Cell cell = gameBoard.getCell(r, c);
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e instanceof Player) {
                        player = (Player) e;
                        ((EntityCell) cell).setEntity(null);
                        break;
                    }
                }
            }
            if (player != null) break;
        }

        if (newPlayerCell == null) {
            // can't place player - shouldn't happen
            return new Invalid();
        }
        newPlayerCell.setEntity(player);

        return new Alive(lastValid, playerPos, collectedGems, collectedExtraLives);
    }

    private Position findPlayer() {
        for (int r = 0; r < gameBoard.getNumRows(); r++) {
            for (int c = 0; c < gameBoard.getNumCols(); c++) {
                Cell cell = gameBoard.getCell(r, c);
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e instanceof Player) {
                        return new Position(r, c);
                    }
                }
            }
        }
        return null;
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive)) {
            return;
        }
        Alive alive = (Alive) prevMove;
        // Move player back to origPosition
        Position newPos = alive.getNewPosition();
        Position origPos = alive.getOrigPosition();
        List<Position> gems = alive.getCollectedGems();
        List<Position> extraLives = alive.getCollectedExtraLives();

        // Remove player from new position
        EntityCell newCell = gameBoard.getEntityCell(newPos);
        if (newCell != null) {
            newCell.setEntity(null);
        }

        // Place player at original position
        EntityCell origCell = gameBoard.getEntityCell(origPos);
        if (origCell != null) {
            Player p = new Player();
            origCell.setEntity(p);
        }

        // Restore gems
        for (Position p : gems) {
            EntityCell ec = gameBoard.getEntityCell(p);
            if (ec != null) {
                ec.setEntity(new Gem());
            }
        }

        // Restore extra lives
        for (Position p : extraLives) {
            EntityCell ec = gameBoard.getEntityCell(p);
            if (ec != null) {
                ec.setEntity(new ExtraLife());
            }
        }
    }
}
