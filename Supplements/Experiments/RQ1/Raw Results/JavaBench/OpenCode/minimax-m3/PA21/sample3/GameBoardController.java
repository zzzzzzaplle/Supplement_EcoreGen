import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        Objects.requireNonNull(direction);
        if (gameBoard == null) {
            return new Invalid();
        }
        Player player = gameBoard.getPlayer();
        if (player == null || player.getOwner() == null) {
            return new Invalid();
        }
        Position start = player.getOwner().getPosition();
        PositionOffset offset = direction.getOffset();

        Position firstStep = start.offsetByOrNull(offset, gameBoard.numRows, gameBoard.numCols);
        if (firstStep == null) {
            return new Invalid();
        }
        Cell firstCell = gameBoard.getCell(firstStep.getRow(), firstStep.getCol());
        if (firstCell instanceof Wall) {
            return new Invalid();
        }

        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        Position mineHit = null;
        Position current = start;

        while (true) {
            Position next = current.offsetByOrNull(offset, gameBoard.numRows, gameBoard.numCols);
            if (next == null) {
                break;
            }
            Cell cell = gameBoard.getCell(next.getRow(), next.getCol());
            if (cell instanceof Wall) {
                break;
            }
            if (cell instanceof StopCell) {
                current = next;
                break;
            }
            EntityCell entityCell = (EntityCell) cell;
            Entity entity = entityCell.getEntity();
            if (entity instanceof Mine) {
                mineHit = next;
                break;
            }
            if (entity instanceof Gem) {
                collectedGems.add(next);
                entityCell.setEntity(null);
            } else if (entity instanceof ExtraLife) {
                collectedExtraLives.add(next);
                entityCell.setEntity(null);
            }
            current = next;
        }

        if (mineHit != null) {
            Dead dead = new Dead();
            dead.setNewPosition(start);
            dead.setOrigPosition(start);
            dead.setMinePosition(mineHit);
            return dead;
        }

        EntityCell startCell = player.getOwner();
        startCell.setEntity(null);
        EntityCell destCell = gameBoard.getEntityCell(current);
        destCell.setEntity(player);

        Alive alive = new Alive();
        alive.setNewPosition(current);
        alive.setOrigPosition(start);
        alive.setCollectedGems(collectedGems);
        alive.setCollectedExtraLives(collectedExtraLives);
        return alive;
    }

    public void undoMove(MoveResult prevMove) {
        if (prevMove == null || gameBoard == null) {
            return;
        }
        Player player = gameBoard.getPlayer();
        if (player == null) {
            return;
        }
        EntityCell currentOwner = player.getOwner();
        if (currentOwner != null) {
            currentOwner.setEntity(null);
        }
        if (prevMove instanceof Alive) {
            Alive alive = (Alive) prevMove;
            EntityCell orig = gameBoard.getEntityCell(alive.getOrigPosition());
            if (orig != null) {
                orig.setEntity(player);
            }
            for (Position gemPos : alive.getCollectedGems()) {
                EntityCell ec = gameBoard.getEntityCell(gemPos);
                if (ec != null) {
                    ec.setEntity(new Gem());
                }
            }
            for (Position lifePos : alive.getCollectedExtraLives()) {
                EntityCell ec = gameBoard.getEntityCell(lifePos);
                if (ec != null) {
                    ec.setEntity(new ExtraLife());
                }
            }
        } else if (prevMove instanceof Dead) {
            Dead dead = (Dead) prevMove;
            EntityCell orig = gameBoard.getEntityCell(dead.getOrigPosition());
            if (orig != null) {
                orig.setEntity(player);
            }
        }
    }
}
