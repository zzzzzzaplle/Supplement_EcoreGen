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
        Position currentPos = player.getOwner().getPosition();
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
        Position lastPos = currentPos;
        boolean moved = false;

        while (nextPos != null) {
            nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());

            if (nextCell instanceof Wall) {
                break;
            }

            moved = true;

            if (nextCell instanceof EntityCell) {
                EntityCell entityCell = (EntityCell) nextCell;
                Entity entity = entityCell.getEntity();

                if (entity instanceof Mine) {
                    return new Dead(origPos, origPos, nextPos);
                }

                if (entity instanceof Gem) {
                    collectedGems.add(nextPos);
                    entityCell.setEntity(null);
                }

                if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(nextPos);
                    entityCell.setEntity(null);
                }
            }

            if (nextCell instanceof StopCell) {
                EntityCell currentEntityCell = gameBoard.getEntityCell(lastPos.getRow(), lastPos.getCol());
                Player p = gameBoard.getPlayer();

                if (currentEntityCell instanceof StopCell) {
                    ((StopCell) currentEntityCell).setPlayer(null);
                } else {
                    currentEntityCell.setEntity(null);
                }

                ((StopCell) nextCell).setPlayer(p);
                lastPos = nextPos;
                break;
            }

            EntityCell currentEntityCell = gameBoard.getEntityCell(lastPos.getRow(), lastPos.getCol());
            if (currentEntityCell instanceof StopCell) {
                ((StopCell) currentEntityCell).setPlayer(null);
            } else {
                currentEntityCell.setEntity(null);
            }

            EntityCell nextEntityCell = gameBoard.getEntityCell(nextPos.getRow(), nextPos.getCol());
            if (nextEntityCell instanceof StopCell) {
                ((StopCell) nextEntityCell).setPlayer(gameBoard.getPlayer());
            } else {
                nextEntityCell.setEntity(gameBoard.getPlayer());
            }

            lastPos = nextPos;
            nextPos = nextPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
        }

        if (!moved) {
            return new Invalid(currentPos);
        }

        return new Alive(origPos, lastPos, collectedGems, collectedExtraLives);
    }

    public void undoMove(MoveResult prevMove) {
        if (prevMove instanceof Alive) {
            Alive alive = (Alive) prevMove;
            Position origPos = alive.getOrigPosition();
            Position newPos = alive.getNewPosition();

            EntityCell newEntityCell = gameBoard.getEntityCell(newPos.getRow(), newPos.getCol());
            if (newEntityCell instanceof StopCell) {
                ((StopCell) newEntityCell).setPlayer(null);
            } else {
                newEntityCell.setEntity(null);
            }

            EntityCell origEntityCell = gameBoard.getEntityCell(origPos.getRow(), origPos.getCol());
            if (origEntityCell instanceof StopCell) {
                ((StopCell) origEntityCell).setPlayer(gameBoard.getPlayer());
            } else {
                origEntityCell.setEntity(gameBoard.getPlayer());
            }

            for (Position gemPos : alive.getCollectedGems()) {
                EntityCell gemCell = gameBoard.getEntityCell(gemPos.getRow(), gemPos.getCol());
                gemCell.setEntity(new Gem());
            }

            for (Position lifePos : alive.getCollectedExtraLives()) {
                EntityCell lifeCell = gameBoard.getEntityCell(lifePos.getRow(), lifePos.getCol());
                lifeCell.setEntity(new ExtraLife());
            }
        }
    }
}
