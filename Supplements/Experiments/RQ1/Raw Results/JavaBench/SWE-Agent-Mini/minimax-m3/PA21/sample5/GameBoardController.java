import java.util.ArrayList;
import java.util.List;

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
        // Find player position
        Position playerPos = findPlayerPosition();
        if (playerPos == null) {
            return new Invalid();
        }

        Position origPosition = new Position(playerPos.getRow(), playerPos.getCol());
        int dRow = direction.getRowOffset();
        int dCol = direction.getColOffset();

        // Try first step - if blocked immediately, return Invalid
        Position firstStep = playerPos.offsetByOrNull(dRow, dCol, gameBoard.numRows, gameBoard.numCols);
        if (firstStep == null) {
            return new Invalid(origPosition);
        }
        Cell firstCell = gameBoard.getCell(firstStep.getRow(), firstStep.getCol());
        if (firstCell instanceof Wall) {
            return new Invalid(origPosition);
        }

        // Slide step-by-step
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        Position currentPos = origPosition;
        Position minePos = null;
        boolean stopped = false;

        while (true) {
            Position nextPos = currentPos.offsetByOrNull(dRow, dCol, gameBoard.numRows, gameBoard.numCols);
            if (nextPos == null) {
                // boundary - stop on currentPos
                stopped = true;
                break;
            }
            Cell nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());
            if (nextCell instanceof Wall) {
                // stop before wall
                stopped = true;
                break;
            }
            if (nextCell instanceof StopCell) {
                // stop exactly on StopCell
                currentPos = nextPos;
                // Move player into the StopCell
                movePlayer(origPosition, currentPos);
                stopped = true;
                break;
            }
            // EntityCell
            EntityCell entityCell = (EntityCell) nextCell;
            Entity entity = entityCell.getEntity();
            if (entity instanceof Mine) {
                minePos = nextPos;
                // Dead: player stays at original position
                currentPos = origPosition;
                stopped = true;
                break;
            }
            if (entity instanceof Gem) {
                collectedGems.add(new Position(nextPos.getRow(), nextPos.getCol()));
            } else if (entity instanceof ExtraLife) {
                collectedExtraLives.add(new Position(nextPos.getRow(), nextPos.getCol()));
            }
            // step further
            currentPos = nextPos;
        }

        if (minePos != null) {
            return new Dead(origPosition, origPosition, minePos);
        }

        if (stopped) {
            // For Alive, move player to currentPos and remove collected items
            movePlayer(origPosition, currentPos);
            // Remove collected gems and extra lives from the board
            for (Position gp : collectedGems) {
                EntityCell ec = gameBoard.getEntityCell(gp);
                if (ec != null) {
                    ec.setEntity(null);
                }
            }
            for (Position lp : collectedExtraLives) {
                EntityCell ec = gameBoard.getEntityCell(lp);
                if (ec != null) {
                    ec.setEntity(null);
                }
            }
            return new Alive(currentPos, origPosition, collectedGems, collectedExtraLives);
        }

        return new Invalid(origPosition);
    }

    public void undoMove(MoveResult prevMove) {
        if (prevMove instanceof Alive) {
            Alive alive = (Alive) prevMove;
            Position origPos = alive.getOrigPosition();
            Position newPos = alive.getNewPosition();

            // Restore collected gems and extra lives to the board
            for (Position gp : alive.getCollectedGems()) {
                EntityCell ec = gameBoard.getEntityCell(gp);
                if (ec != null) {
                    ec.setEntity(new Gem());
                }
            }
            for (Position lp : alive.getCollectedExtraLives()) {
                EntityCell ec = gameBoard.getEntityCell(lp);
                if (ec != null) {
                    ec.setEntity(new ExtraLife());
                }
            }

            // Move player back from newPos to origPos
            if (newPos != null && origPos != null) {
                EntityCell fromCell = gameBoard.getEntityCell(newPos);
                EntityCell toCell = gameBoard.getEntityCell(origPos);
                if (fromCell != null) {
                    Entity player = fromCell.getEntity();
                    if (player instanceof Player) {
                        fromCell.setEntity(null);
                        if (toCell != null) {
                            toCell.setEntity(player);
                        }
                    }
                }
            }
        }
    }

    private Position findPlayerPosition() {
        for (int r = 0; r < gameBoard.numRows; r++) {
            for (int c = 0; c < gameBoard.numCols; c++) {
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

    private void movePlayer(Position from, Position to) {
        if (from.equals(to)) {
            return;
        }
        EntityCell fromCell = gameBoard.getEntityCell(from);
        EntityCell toCell = gameBoard.getEntityCell(to);
        if (fromCell != null) {
            Entity player = fromCell.getEntity();
            if (player instanceof Player) {
                fromCell.setEntity(null);
                if (toCell != null) {
                    toCell.setEntity(player);
                }
            }
        }
    }
}
