import java.util.Objects;

public class GameBoardController {
    private GameBoard gameBoard;

    public GameBoardController() {
        this.gameBoard = null;
    }

    public GameBoardController(final GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public MoveResult makeMove(final Direction direction) {
        final Player player = gameBoard.getPlayer();
        final EntityCell playerCell = (EntityCell) player.getOwner();
        final Position origPosition = playerCell.getPosition();
        final PositionOffset offset = direction.getOffset();

        Position currentPos = origPosition;
        Position nextPos = currentPos.offsetByOrNull(offset, gameBoard.getNumRows(), gameBoard.getNumCols());

        if (nextPos == null) {
            return new Invalid();
        }

        final Cell firstCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());
        if (firstCell instanceof Wall) {
            return new Invalid();
        }

        final Alive aliveResult = new Alive(origPosition, nextPos);
        Position lastValidPos = origPosition;

        while (nextPos != null) {
            final Cell cell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());

            if (cell instanceof Wall) {
                break;
            }

            if (cell instanceof StopCell) {
                lastValidPos = nextPos;
                break;
            }

            if (cell instanceof EntityCell) {
                final Entity entity = ((EntityCell) cell).getEntity();

                if (entity instanceof Gem) {
                    aliveResult.addCollectedGem(nextPos);
                    ((EntityCell) cell).setEntity(null);
                } else if (entity instanceof ExtraLife) {
                    aliveResult.addCollectedExtraLife(nextPos);
                    ((EntityCell) cell).setEntity(null);
                } else if (entity instanceof Mine) {
                    final Dead deadResult = new Dead(origPosition, nextPos, nextPos);
                    movePlayerTo(player, origPosition);
                    return deadResult;
                }
            }

            lastValidPos = nextPos;
            nextPos = nextPos.offsetByOrNull(offset, gameBoard.getNumRows(), gameBoard.getNumCols());
        }

        if (lastValidPos.equals(origPosition)) {
            return new Invalid();
        }

        movePlayerTo(player, lastValidPos);
        return aliveResult;
    }

    public void undoMove(final MoveResult prevMove) {
        Objects.requireNonNull(prevMove);

        if (prevMove instanceof Valid) {
            final Valid valid = (Valid) prevMove;
            final Player player = gameBoard.getPlayer();
            movePlayerTo(player, valid.getOrigPosition());
        }
    }

    private void movePlayerTo(final Player player, final Position position) {
        final EntityCell oldCell = (EntityCell) player.getOwner();
        if (oldCell != null) {
            oldCell.setEntity(null);
        }

        final EntityCell newCell = gameBoard.getEntityCell(position);
        newCell.setPlayer(player);
    }
}
