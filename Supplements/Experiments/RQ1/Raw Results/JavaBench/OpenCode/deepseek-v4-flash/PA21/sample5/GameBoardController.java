import java.util.List;
import java.util.ArrayList;

public class GameBoardController {
    private GameBoard gameBoard;

    public GameBoardController() {
    }

    public GameBoardController(GameBoard gameBoard) {
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

        boolean hasMoved = false;
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();

        Position newPos = currentPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());

        if (newPos == null) {
            return new Invalid(new Position(currentPos.getRow(), currentPos.getCol()));
        }

        while (newPos != null) {
            Cell cell = gameBoard.getCell(newPos.getRow(), newPos.getCol());

            if (cell instanceof Wall) {
                break;
            }

            if (cell instanceof EntityCell) {
                EntityCell ec = (EntityCell) cell;
                Entity entity = ec.getEntity();

                if (entity instanceof Mine) {
                    return new Dead(new Position(origPos.getRow(), origPos.getCol()), origPos, newPos);
                }

                if (entity instanceof Gem) {
                    if (hasMoved) {
                        collectedGems.add(new Position(newPos.getRow(), newPos.getCol()));
                    }
                    ec.setEntity(null);
                } else if (entity instanceof ExtraLife) {
                    if (hasMoved) {
                        collectedExtraLives.add(new Position(newPos.getRow(), newPos.getCol()));
                    }
                    ec.setEntity(null);
                }
            }

            hasMoved = true;

            if (cell instanceof StopCell) {
                StopCell stopCell = (StopCell) cell;
                stopCell.setPlayer(player);
                playerCell.setEntity(null);
                return new Alive(newPos, origPos, collectedGems, collectedExtraLives);
            }

            Position nextPos = newPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());

            if (nextPos == null) {
                currentPos = newPos;
                newPos = null;
            } else {
                Cell nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());
                if (nextCell instanceof Wall) {
                    currentPos = newPos;
                    newPos = null;
                } else {
                    currentPos = newPos;
                    newPos = nextPos;
                }
            }
        }

        if (!hasMoved) {
            return new Invalid(new Position(currentPos.getRow(), currentPos.getCol()));
        }

        EntityCell newCell = (EntityCell) gameBoard.getCell(currentPos.getRow(), currentPos.getCol());
        newCell.setEntity(player);
        playerCell.setEntity(null);

        return new Alive(currentPos, origPos, collectedGems, collectedExtraLives);
    }

    public void undoMove(MoveResult prevMove) {
        if (prevMove instanceof Alive) {
            Alive alive = (Alive) prevMove;
            Position origPos = alive.getOrigPosition();
            Position newPos = alive.getNewPosition();

            Player player = gameBoard.getPlayer();

            EntityCell origCell = gameBoard.getEntityCell(origPos);
            if (origCell == null) return;

            EntityCell newCell = gameBoard.getEntityCell(newPos);
            if (newCell == null) return;

            if (newCell instanceof StopCell) {
                StopCell stopCell = (StopCell) newCell;
                stopCell.setEntity(null);
            } else {
                newCell.setEntity(null);
            }

            if (origCell instanceof StopCell) {
                ((StopCell) origCell).setPlayer(player);
            } else {
                origCell.setEntity(player);
            }

            for (Position gp : alive.getCollectedGems()) {
                EntityCell gemCell = gameBoard.getEntityCell(gp);
                if (gemCell != null) {
                    gemCell.setEntity(new Gem());
                }
            }

            for (Position lp : alive.getCollectedExtraLives()) {
                EntityCell lifeCell = gameBoard.getEntityCell(lp);
                if (lifeCell != null) {
                    lifeCell.setEntity(new ExtraLife());
                }
            }
        }
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }
}
