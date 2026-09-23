import java.util.List;
import java.util.ArrayList;

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

        EntityCell ownerCell = player.getOwner();
        if (ownerCell == null) {
            return new Invalid(null);
        }

        Position currentPos = ownerCell.getPosition();
        Position origPos = currentPos;
        Position newPos = origPos;
        boolean moved = false;
        boolean hitMine = false;
        Position minePos = null;
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();

        int dRow = direction.getRowOffset();
        int dCol = direction.getColOffset();

        while (true) {
            Position nextPos = newPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
            if (nextPos == null) {
                break;
            }

            Cell nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());
            if (nextCell instanceof Wall) {
                break;
            }

            if (nextCell instanceof StopCell) {
                newPos = nextPos;
                moved = true;
                break;
            }

            if (nextCell instanceof EntityCell) {
                EntityCell entityCell = (EntityCell) nextCell;
                Entity entity = entityCell.getEntity();

                if (entity instanceof Mine) {
                    newPos = nextPos;
                    moved = true;
                    hitMine = true;
                    minePos = nextPos;
                    break;
                }

                if (entity instanceof Gem) {
                    collectedGems.add(nextPos);
                } else if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(nextPos);
                }
            }

            newPos = nextPos;
            moved = true;
        }

        if (!moved) {
            return new Invalid(origPos);
        }

        if (hitMine) {
            return new Dead(origPos, origPos, minePos);
        }

        EntityCell currentEntityCell = gameBoard.getEntityCell(currentPos.getRow(), currentPos.getCol());
        EntityCell targetEntityCell = gameBoard.getEntityCell(newPos.getRow(), newPos.getCol());

        if (currentEntityCell != null) {
            currentEntityCell.setEntity(null);
        }

        if (targetEntityCell instanceof StopCell) {
            StopCell stopCell = (StopCell) targetEntityCell;
            stopCell.setPlayer(player);
        } else if (targetEntityCell != null) {
            targetEntityCell.setEntity(player);
        }

        for (Position gemPos : collectedGems) {
            EntityCell gemCell = gameBoard.getEntityCell(gemPos.getRow(), gemPos.getCol());
            if (gemCell != null) {
                gemCell.setEntity(null);
            }
        }

        for (Position lifePos : collectedExtraLives) {
            EntityCell lifeCell = gameBoard.getEntityCell(lifePos.getRow(), lifePos.getCol());
            if (lifeCell != null) {
                lifeCell.setEntity(null);
            }
        }

        Alive alive = new Alive(origPos, newPos);
        alive.getCollectedGems().addAll(collectedGems);
        alive.getCollectedExtraLives().addAll(collectedExtraLives);
        return alive;
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive)) {
            return;
        }

        Alive alive = (Alive) prevMove;
        Position origPos = alive.getOrigPosition();
        Position newPos = alive.getNewPosition();

        Player player = gameBoard.getPlayer();
        if (player == null) return;

        EntityCell newEntityCell = gameBoard.getEntityCell(newPos.getRow(), newPos.getCol());
        EntityCell origEntityCell = gameBoard.getEntityCell(origPos.getRow(), origPos.getCol());

        if (origEntityCell instanceof StopCell) {
            ((StopCell) origEntityCell).setPlayer(player);
        } else if (origEntityCell != null) {
            origEntityCell.setEntity(player);
        }

        if (newEntityCell != null) {
            newEntityCell.setEntity(null);
        }

        for (Position gemPos : alive.getCollectedGems()) {
            EntityCell gemCell = gameBoard.getEntityCell(gemPos.getRow(), gemPos.getCol());
            if (gemCell != null) {
                gemCell.setEntity(new Gem());
            }
        }

        for (Position lifePos : alive.getCollectedExtraLives()) {
            EntityCell lifeCell = gameBoard.getEntityCell(lifePos.getRow(), lifePos.getCol());
            if (lifeCell != null) {
                lifeCell.setEntity(new ExtraLife());
            }
        }
    }
}
