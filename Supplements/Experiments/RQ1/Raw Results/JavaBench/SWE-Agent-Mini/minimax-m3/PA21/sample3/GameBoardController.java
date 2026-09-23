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
        // Find player position
        Position playerPos = findPlayer();
        if (playerPos == null) {
            return new Invalid();
        }

        Position origPos = new Position(playerPos.getRow(), playerPos.getCol());
        int dRow = direction.getRowOffset();
        int dCol = direction.getColOffset();

        // Check first step
        Position firstStep = origPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
        if (firstStep == null) {
            return new Invalid(origPos);
        }

        Cell firstCell = gameBoard.getCell(firstStep.getRow(), firstStep.getCol());
        if (firstCell instanceof Wall) {
            return new Invalid(origPos);
        }

        // Slide step by step
        Position current = new Position(origPos.getRow(), origPos.getCol());
        Position lastValid = current;
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();

        Position next = current.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
        while (next != null) {
            Cell cell = gameBoard.getCell(next.getRow(), next.getCol());
            if (cell instanceof Wall) {
                break;
            }
            if (cell instanceof StopCell) {
                // Stop on StopCell
                lastValid = next;
                break;
            }
            // EntityCell
            if (cell instanceof EntityCell) {
                EntityCell ec = (EntityCell) cell;
                Entity entity = ec.getEntity();
                if (entity instanceof Mine) {
                    // Dead result - board unchanged, player stays at origPos
                    // But we also need to handle collectedGems/ExtraLives - "not collected"
                    return new Dead(origPos, origPos, new Position(next.getRow(), next.getCol()));
                }
                if (entity instanceof Gem) {
                    collectedGems.add(new Position(next.getRow(), next.getCol()));
                } else if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(new Position(next.getRow(), next.getCol()));
                } else if (entity instanceof Player) {
                    // Player encountered - treat as stop
                    lastValid = next;
                    break;
                }
            }
            lastValid = next;
            current = next;
            next = current.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
        }

        // Now perform the actual move: remove player from orig, place on lastValid
        // Remove collected gems and extra lives
        if (!lastValid.equals(origPos)) {
            // Remove player from orig
            Cell origCell = gameBoard.getCell(origPos.getRow(), origPos.getCol());
            if (origCell instanceof EntityCell) {
                ((EntityCell) origCell).setEntity(null);
            }
            // Place player on lastValid
            Cell destCell = gameBoard.getCell(lastValid.getRow(), lastValid.getCol());
            if (destCell instanceof EntityCell) {
                ((EntityCell) destCell).setEntity(new Player());
            }
            // Remove collected gems and extra lives
            for (Position p : collectedGems) {
                Cell c = gameBoard.getCell(p.getRow(), p.getCol());
                if (c instanceof EntityCell) {
                    ((EntityCell) c).setEntity(null);
                }
            }
            for (Position p : collectedExtraLives) {
                Cell c = gameBoard.getCell(p.getRow(), p.getCol());
                if (c instanceof EntityCell) {
                    ((EntityCell) c).setEntity(null);
                }
            }
        }

        return new Alive(lastValid, origPos, collectedGems, collectedExtraLives);
    }

    public void undoMove(MoveResult prevMove) {
        if (prevMove instanceof Alive) {
            Alive alive = (Alive) prevMove;
            // Restore player position to orig
            Position orig = alive.getOrigPosition();
            Position newPos = alive.getNewPosition();

            // Remove player from newPos
            Cell newCell = gameBoard.getCell(newPos.getRow(), newPos.getCol());
            if (newCell instanceof EntityCell) {
                ((EntityCell) newCell).setEntity(null);
            }
            // Place player at orig
            Cell origCell = gameBoard.getCell(orig.getRow(), orig.getCol());
            if (origCell instanceof EntityCell) {
                ((EntityCell) origCell).setEntity(new Player());
            }
            // Restore collected gems
            if (alive.getCollectedGems() != null) {
                for (Position p : alive.getCollectedGems()) {
                    Cell c = gameBoard.getCell(p.getRow(), p.getCol());
                    if (c instanceof EntityCell) {
                        ((EntityCell) c).setEntity(new Gem());
                    }
                }
            }
            // Restore collected extra lives
            if (alive.getCollectedExtraLives() != null) {
                for (Position p : alive.getCollectedExtraLives()) {
                    Cell c = gameBoard.getCell(p.getRow(), p.getCol());
                    if (c instanceof EntityCell) {
                        ((EntityCell) c).setEntity(new ExtraLife());
                    }
                }
            }
        }
    }

    private Position findPlayer() {
        for (int r = 0; r < gameBoard.getNumRows(); r++) {
            for (int c = 0; c < gameBoard.getNumCols(); c++) {
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
}
