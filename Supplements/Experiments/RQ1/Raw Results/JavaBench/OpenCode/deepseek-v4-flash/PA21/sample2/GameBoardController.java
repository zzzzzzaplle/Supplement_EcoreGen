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
        EntityCell playerCell = player.getOwner();
        Position currentPos = playerCell.getPosition();
        Position origPos = new Position(currentPos.getRow(), currentPos.getCol());

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

        if (nextCell instanceof StopCell) {
            EntityCell nextEntityCell = (EntityCell) nextCell;
            if (nextEntityCell.getEntity() == null) {
                List<Position> collectedGems = new ArrayList<>();
                List<Position> collectedExtraLives = new ArrayList<>();

                playerCell.setEntity(null);
                player.setOwner(nextEntityCell);
                nextEntityCell.setEntity(player);

                Alive alive = new Alive(origPos, nextPos);
                alive.setCollectedGems(collectedGems);
                alive.setCollectedExtraLives(collectedExtraLives);
                return alive;
            }
            return new Invalid(currentPos);
        }

        Position slidePos = nextPos;
        while (true) {
            Position nextSlidePos = slidePos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
            if (nextSlidePos == null) {
                break;
            }
            Cell slideCell = gameBoard.getCell(nextSlidePos.getRow(), nextSlidePos.getCol());
            if (slideCell instanceof Wall) {
                break;
            }
            if (slideCell instanceof StopCell) {
                slidePos = nextSlidePos;
                break;
            }
            slidePos = nextSlidePos;
        }

        Position finalPos = slidePos;

        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        Position mineHitPos = null;

        Position checkPos = new Position(currentPos.getRow() + dRow, currentPos.getCol() + dCol);
        while (checkPos != null) {
            boolean reachedEnd = checkPos.getRow() == finalPos.getRow() && checkPos.getCol() == finalPos.getCol();

            Cell checkCell = gameBoard.getCell(checkPos.getRow(), checkPos.getCol());
            if (checkCell instanceof EntityCell) {
                Entity entity = ((EntityCell) checkCell).getEntity();
                if (entity instanceof Mine) {
                    mineHitPos = new Position(checkPos.getRow(), checkPos.getCol());
                    break;
                }
                if (entity instanceof Gem) {
                    if (!reachedEnd) {
                        collectedGems.add(new Position(checkPos.getRow(), checkPos.getCol()));
                    }
                }
                if (entity instanceof ExtraLife) {
                    if (!reachedEnd) {
                        collectedExtraLives.add(new Position(checkPos.getRow(), checkPos.getCol()));
                    }
                }
            }

            if (reachedEnd) {
                break;
            }

            checkPos = checkPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
        }

        if (mineHitPos != null) {
            Dead dead = new Dead(origPos, origPos, mineHitPos);
            return dead;
        }

        for (Position gemPos : collectedGems) {
            EntityCell gemCell = (EntityCell) gameBoard.getCell(gemPos.getRow(), gemPos.getCol());
            gemCell.setEntity(null);
        }
        for (Position lifePos : collectedExtraLives) {
            EntityCell lifeCell = (EntityCell) gameBoard.getCell(lifePos.getRow(), lifePos.getCol());
            lifeCell.setEntity(null);
        }

        playerCell.setEntity(null);
        player.setOwner((EntityCell) gameBoard.getCell(finalPos.getRow(), finalPos.getCol()));
        ((EntityCell) gameBoard.getCell(finalPos.getRow(), finalPos.getCol())).setEntity(player);

        Alive alive = new Alive(origPos, finalPos);
        alive.setCollectedGems(collectedGems);
        alive.setCollectedExtraLives(collectedExtraLives);
        return alive;
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive)) {
            return;
        }
        Alive alive = (Alive) prevMove;
        Position origPos = alive.getOrigPosition();
        Position newPos = alive.getNewPosition();

        EntityCell newCell = (EntityCell) gameBoard.getCell(newPos.getRow(), newPos.getCol());
        Player player = (Player) newCell.getEntity();

        newCell.setEntity(null);
        player.setOwner(null);

        EntityCell origCell = (EntityCell) gameBoard.getCell(origPos.getRow(), origPos.getCol());
        origCell.setEntity(player);
        player.setOwner(origCell);

        if (origCell instanceof StopCell) {
            StopCell stopCell = (StopCell) origCell;
            stopCell.setPlayer(player);
        }

        for (Position gemPos : alive.getCollectedGems()) {
            EntityCell gemCell = (EntityCell) gameBoard.getCell(gemPos.getRow(), gemPos.getCol());
            gemCell.setEntity(new Gem());
        }
        for (Position lifePos : alive.getCollectedExtraLives()) {
            EntityCell lifeCell = (EntityCell) gameBoard.getCell(lifePos.getRow(), lifePos.getCol());
            lifeCell.setEntity(new ExtraLife());
        }
    }
}
