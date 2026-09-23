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
            return new Invalid(null);
        }

        EntityCell playerCell = player.getOwner();
        if (playerCell == null) {
            return new Invalid(null);
        }

        Position currentPos = playerCell.getPosition();
        Position origPos = new Position(currentPos.getRow(), currentPos.getCol());

        int dRow = direction.getRowOffset();
        int dCol = direction.getColOffset();

        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();

        Position nextPos = currentPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());

        // If first step is out of bounds, it's invalid
        if (nextPos == null) {
            return new Invalid(currentPos);
        }

        boolean moved = false;

        while (nextPos != null) {
            Cell nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());

            // Stop before a Wall
            if (nextCell instanceof Wall) {
                if (moved) {
                    // Valid move ending before wall
                    Position finalPos = currentPos;
                    EntityCell finalEntityCell = gameBoard.getEntityCell(finalPos);
                    if (finalEntityCell != null) {
                        finalEntityCell.setEntity(player);
                        player.setOwner(finalEntityCell);
                    }
                    // Clear old cell
                    if (!origPos.equals(finalPos)) {
                        EntityCell oldCell = gameBoard.getEntityCell(origPos);
                        if (oldCell != null) {
                            oldCell.setEntity(null);
                        }
                    }
                    return new Alive(finalPos, origPos, collectedGems, collectedExtraLives);
                } else {
                    return new Invalid(currentPos);
                }
            }

            if (nextCell instanceof EntityCell) {
                EntityCell entityCell = (EntityCell) nextCell;
                Entity entity = entityCell.getEntity();

                // StopCell - stop exactly on it
                if (nextCell instanceof StopCell) {
                    if (!moved) {
                        // Check if there's already a player? Actually StopCell can contain player
                        // If first step is a StopCell, we can move onto it
                    }
                    // Move player to this cell
                    EntityCell oldCell = gameBoard.getEntityCell(currentPos);
                    if (oldCell != null) {
                        oldCell.setEntity(null);
                    }
                    entityCell.setEntity(player);
                    player.setOwner(entityCell);

                    if (!moved) {
                        moved = true;
                    }

                    Position finalPos = nextPos;
                    return new Alive(finalPos, origPos, collectedGems, collectedExtraLives);
                }

                // Check what entity is in the cell
                if (entity instanceof Gem) {
                    // Collect gem, but don't stop - keep sliding
                    collectedGems.add(new Position(nextPos.getRow(), nextPos.getCol()));
                    entityCell.setEntity(null); // Remove gem from board
                    moved = true;
                    currentPos = nextPos;
                    nextPos = nextPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
                    continue;
                }

                if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(new Position(nextPos.getRow(), nextPos.getCol()));
                    entityCell.setEntity(null); // Remove extra life
                    moved = true;
                    currentPos = nextPos;
                    nextPos = nextPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
                    continue;
                }

                if (entity instanceof Mine) {
                    // Dead - stop movement, position is original position
                    // Don't collect anything, don't change board
                    return new Dead(origPos, origPos, new Position(nextPos.getRow(), nextPos.getCol()));
                }

                if (entity instanceof Player) {
                    // Shouldn't happen normally, but if it does, stop
                    if (!moved) {
                        return new Invalid(currentPos);
                    }
                    break;
                }

                // Empty EntityCell - slide through
                moved = true;
                currentPos = nextPos;
                nextPos = nextPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
                continue;
            }

            // Should not reach here
            break;
        }

        // If we exited because nextPos was null (boundary)
        if (moved) {
            // Player is at currentPos
            EntityCell finalEntityCell = gameBoard.getEntityCell(currentPos);
            EntityCell oldCell = gameBoard.getEntityCell(origPos);
            if (oldCell != null && !origPos.equals(currentPos)) {
                oldCell.setEntity(null);
            }
            if (finalEntityCell != null) {
                finalEntityCell.setEntity(player);
                player.setOwner(finalEntityCell);
            }
            return new Alive(currentPos, origPos, collectedGems, collectedExtraLives);
        }

        return new Invalid(currentPos);
    }

    public void undoMove(MoveResult prevMove) {
        if (prevMove instanceof Alive) {
            Alive alive = (Alive) prevMove;

            // Restore player to original position
            Position origPos = alive.getOrigPosition();
            Position newPos = alive.getNewPosition();

            EntityCell origCell = gameBoard.getEntityCell(origPos);
            EntityCell newCell = gameBoard.getEntityCell(newPos);

            Player player = gameBoard.getPlayer();

            // Remove player from new position
            if (newCell != null) {
                newCell.setEntity(null);
            }

            // Place player at original position
            if (origCell != null) {
                origCell.setEntity(player);
                player.setOwner(origCell);
            }

            // Restore collected gems
            for (Position gemPos : alive.getCollectedGems()) {
                EntityCell gemCell = gameBoard.getEntityCell(gemPos);
                if (gemCell != null) {
                    gemCell.setEntity(new Gem());
                }
            }

            // Restore collected extra lives
            for (Position lifePos : alive.getCollectedExtraLives()) {
                EntityCell lifeCell = gameBoard.getEntityCell(lifePos);
                if (lifeCell != null) {
                    lifeCell.setEntity(new ExtraLife());
                }
            }
        }
    }
}
