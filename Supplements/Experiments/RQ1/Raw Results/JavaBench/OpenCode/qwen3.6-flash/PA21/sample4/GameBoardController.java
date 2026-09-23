import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

public class GameBoardController {

    private GameBoard gameBoard;

    public GameBoardController(GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public MoveResult makeMove(Direction direction) {
        Player player = getPlayerOnBoard();
        if (player == null) {
            return null;
        }

        EntityCell playerCell = player.getOwner();
        Position startPos = playerCell.getPosition();

        int r = startPos.getRow();
        int c = startPos.getCol();
        int dr = direction.getRowOffset();
        int dc = direction.getColOffset();

        // Record current cell state
        EntityCell startCell = makeEntityCell(r, c);

        List<Position> pathGems = new ArrayList<>();
        List<Position> pathExtraLives = new ArrayList<>();

        boolean hitMine = false;
        Position minePos = null;

        // Sliding loop
        while (true) {
            int nr = r;
            int nc = c;

            if (nr < 0 || nr >= gameBoard.getNumRows() || nc < 0 || nc >= gameBoard.getNumCols()) {
                break;
            }

            Cell cell = gameBoard.getCell(nr, nc);

            // Check for entities on current cell
            if (cell instanceof EntityCell) {
                EntityCell entityCell = (EntityCell) cell;
                Entity entity = entityCell.getEntity();
                if (entity instanceof Mine) {
                    hitMine = true;
                    minePos = entityCell.getPosition();
                    break;
                }
                if (entity instanceof Gem) {
                    pathGems.add(entityCell.getPosition());
                    // Don't actually remove yet - only if not hitting mine
                }
                if (entity instanceof ExtraLife) {
                    pathExtraLives.add(entityCell.getPosition());
                }
            }

            // Determine final position in this step
            r = nr;
            c = nc;

            // Next position check
            int futureR = r + dr;
            int futureC = c + dc;

            // Stop at boundary
            if (futureR < 0 || futureR >= gameBoard.getNumRows() || futureC < 0 || futureC >= gameBoard.getNumCols()) {
                break;
            }

            Cell futureCell = gameBoard.getCell(futureR, futureC);
            if (futureCell instanceof Wall) {
                break; // Wall blocks further sliding
            }

            if (futureCell instanceof StopCell) {
                // Move onto the StopCell
                r = futureR;
                c = futureC;
                break;
            }

            r = futureR;
            c = futureC;
        }

        if (hitMine) {
            // Dead: player returns to original position, gems not collected, board unchanged
            return new Dead(startPos, minePos);
        }

        // Check if player moved at least one cell
        if (r == startPos.getRow() && c == startPos.getCol()) {
            return new Invalid(startPos);
        }

        // Now actually collect gems and remove from board
        for (Position gemPos : pathGems) {
            EntityCell cell = (EntityCell) gameBoard.getCell(gemPos.getRow(), gemPos.getCol());
            cell.setEntity(null);
        }
        for (Position extraLifePos : pathExtraLives) {
            EntityCell cell = (EntityCell) gameBoard.getCell(extraLifePos.getRow(), extraLifePos.getCol());
            cell.setEntity(null);
        }

        // Move player to new position
        removePlayerFromBoard(player);
        placePlayerOnBoard(player, r, c);

        Alive alive = new Alive(startPos, new Position(r, c));
        for (Position p : pathGems) {
            alive.addCollectedGem(p);
        }
        for (Position p : pathExtraLives) {
            alive.addCollectedExtraLife(p);
        }
        return alive;
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive)) {
            return;
        }

        Alive alive = (Alive) prevMove;
        Position origPosition = alive.getOrigPosition();
        Player player = getPlayerOnBoard();
        if (player == null) {
            return;
        }

        // Restore gems and extra lives to the board
        for (Position gemPos : alive.getCollectedGems()) {
            EntityCell cell = (EntityCell) getCell(gemPos.getRow(), gemPos.getCol());
            cell.setEntity(new Gem());
        }
        for (Position extraLifePos : alive.getCollectedExtraLives()) {
            EntityCell cell = (EntityCell) getCell(extraLifePos.getRow(), extraLifePos.getCol());
            cell.setEntity(new ExtraLife());
        }

        // Remove player from current position
        EntityCell currentOwner = player.getOwner();
        if (currentOwner != null) {
            if (currentOwner instanceof StopCell) {
                ((StopCell) currentOwner).setPlayer(null);
            } else {
                currentOwner.setEntity(null);
            }
        }

        // Place player back at original position
        EntityCell origCell = (EntityCell) getCell(origPosition.getRow(), origPosition.getCol());
        if (origCell instanceof StopCell) {
            ((StopCell) origCell).setPlayer(player);
        } else {
            origCell.setEntity(player);
        }
    }

    private Player getPlayerOnBoard() {
        for (int rr = 0; rr < gameBoard.getNumRows(); rr++) {
            for (int cc = 0; cc < gameBoard.getNumCols(); cc++) {
                Cell cell = gameBoard.getCell(rr, cc);
                if (cell instanceof EntityCell) {
                    EntityCell ec = (EntityCell) cell;
                    if (ec.getEntity() instanceof Player) {
                        return (Player) ec.getEntity();
                    }
                }
            }
        }
        return null;
    }

    private void placePlayerOnBoard(Player player, int r, int c) {
        EntityCell cell = (EntityCell) gameBoard.getCell(r, c);
        if (cell instanceof StopCell) {
            ((StopCell) cell).setPlayer(player);
        } else {
            cell.setEntity(player);
        }
    }

    private void removePlayerFromBoard(Player player) {
        EntityCell owner = player.getOwner();
        if (owner != null) {
            if (owner instanceof StopCell) {
                ((StopCell) owner).setPlayer(null);
            } else {
                owner.setEntity(null);
            }
        }
    }

    private Cell getCell(int r, int c) {
        return gameBoard.getCell(r, c);
    }

    private EntityCell makeEntityCell(int r, int c) {
        Cell cell = gameBoard.getCell(r, c);
        if (cell instanceof EntityCell) {
            return (EntityCell) cell;
        }
        return null;
    }
}
