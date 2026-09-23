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
        Position playerPos = player.getOwner().getPosition();
        Position currentPos = playerPos;
        Position origPos = currentPos;
        int dRow = direction.getRowOffset();
        int dCol = direction.getColOffset();

        Position nextPos = currentPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
        if (nextPos == null) {
            return new Invalid(currentPos);
        }

        Cell nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());
        if (nextCell instanceof Wall) {
            return new Invalid(currentPos);
        }

        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        boolean moved = false;

        while (true) {
            Position stepPos = currentPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
            if (stepPos == null) {
                break;
            }

            Cell stepCell = gameBoard.getCell(stepPos.getRow(), stepPos.getCol());
            if (stepCell instanceof Wall) {
                break;
            }

            moved = true;

            if (stepCell instanceof StopCell) {
                EntityCell entityCell = (EntityCell) stepCell;
                if (entityCell.getEntity() instanceof Player) {
                    currentPos = stepPos;
                    break;
                }
                if (entityCell.getEntity() == null) {
                    currentPos = stepPos;
                    break;
                }
            }

            if (stepCell instanceof EntityCell) {
                EntityCell entityCell = (EntityCell) stepCell;
                Entity entity = entityCell.getEntity();
                if (entity instanceof Mine) {
                    return new Dead(origPos, origPos, stepPos);
                }
                if (entity instanceof Gem) {
                    collectedGems.add(stepPos);
                }
                if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(stepPos);
                }
            }

            currentPos = stepPos;

            if (stepCell instanceof StopCell) {
                break;
            }
        }

        if (!moved) {
            return new Invalid(currentPos);
        }

        EntityCell origEntityCell = gameBoard.getEntityCell(origPos);
        origEntityCell.setEntity(null);

        EntityCell targetCell = gameBoard.getEntityCell(currentPos);
        if (targetCell instanceof StopCell) {
            ((StopCell) targetCell).setPlayer(player);
        } else {
            targetCell.setEntity(player);
        }

        for (Position gemPos : collectedGems) {
            EntityCell gemCell = gameBoard.getEntityCell(gemPos);
            gemCell.setEntity(null);
        }
        for (Position lifePos : collectedExtraLives) {
            EntityCell lifeCell = gameBoard.getEntityCell(lifePos);
            lifeCell.setEntity(null);
        }

        return new Alive(currentPos, origPos, collectedGems, collectedExtraLives);
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive)) {
            return;
        }
        Alive alive = (Alive) prevMove;

        Position origPos = alive.getOrigPosition();
        Position newPos = alive.getNewPosition();

        EntityCell newCell = gameBoard.getEntityCell(newPos);
        EntityCell origCell = gameBoard.getEntityCell(origPos);

        Player player = gameBoard.getPlayer();
        newCell.setEntity(null);

        if (origCell instanceof StopCell) {
            ((StopCell) origCell).setPlayer(player);
        } else {
            origCell.setEntity(player);
        }

        for (Position gemPos : alive.getCollectedGems()) {
            EntityCell gemCell = gameBoard.getEntityCell(gemPos);
            gemCell.setEntity(new Gem());
        }
        for (Position lifePos : alive.getCollectedExtraLives()) {
            EntityCell lifeCell = gameBoard.getEntityCell(lifePos);
            lifeCell.setEntity(new ExtraLife());
        }
    }
}
