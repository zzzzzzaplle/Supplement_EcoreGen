import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class GameBoardController {
    private GameBoard gameBoard;

    public GameBoardController() {
    }

    public GameBoardController(GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public MoveResult makeMove(Direction direction) {
        if (gameBoard.getPlayer() == null) {
            return new Invalid(new Position(0, 0));
        }

        Player player = gameBoard.getPlayer();
        EntityCell playerCell = getEntityCellForPlayer(player);
        if (playerCell == null) {
            return new Invalid(new Position(0, 0));
        }

        Position startPos = playerCell.getPosition();
        Position currentPos = startPos;
        PositionOffset offset = direction.getOffset();
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        boolean moved = false;

        // Check if first step is blocked
        Position firstNextPos = currentPos.offsetByOrNull(offset, gameBoard.getNumRows(), gameBoard.getNumCols());
        if (firstNextPos == null) {
            return new Invalid(startPos);
        }
        Cell firstNextCell = gameBoard.getCell(firstNextPos.getRow(), firstNextPos.getCol());
        if (firstNextCell instanceof Wall) {
            return new Invalid(startPos);
        }

        // Slide step by step
        while (true) {
            Position nextPos = currentPos.offsetByOrNull(offset, gameBoard.getNumRows(), gameBoard.getNumCols());
            if (nextPos == null) {
                break;
            }

            Cell nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());

            if (nextCell instanceof Wall) {
                break;
            }

            if (nextCell instanceof EntityCell) {
                EntityCell nextEntityCell = (EntityCell) nextCell;
                Entity entity = nextEntityCell.getEntity();

                if (entity instanceof Mine) {
                    return new Dead(startPos, nextPos);
                } else if (entity instanceof Player) {
                    break;
                }

                // Collect gems and extra lives
                if (entity instanceof Gem) {
                    collectedGems.add(nextPos);
                    nextEntityCell.setentity(null);
                } else if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(nextPos);
                    nextEntityCell.setentity(null);
                }
            }

            currentPos = nextPos;
            moved = true;

            // If we're on a StopCell, stop sliding
            if (gameBoard.getCell(currentPos.getRow(), currentPos.getCol()) instanceof StopCell) {
                break;
            }
        }

        if (!moved) {
            return new Invalid(startPos);
        }

        // Remove player from old position
        removePlayerFromBoard();

        // Place player at final position
        Cell finalCell = gameBoard.getCell(currentPos.getRow(), currentPos.getCol());
        if (finalCell instanceof StopCell) {
            ((StopCell) finalCell).setPlayer(player);
        } else {
            StopCell newStopCell = new StopCell(currentPos, player);
            gameBoard.getBoard()[currentPos.getRow()][currentPos.getCol()] = newStopCell;
        }

        Alive alive = new Alive(currentPos, startPos);
        alive.setCollectedGems(collectedGems);
        alive.setCollectedExtraLives(collectedExtraLives);
        return alive;
    }

    private void removePlayerFromBoard() {
        Cell[][] board = gameBoard.getBoard();
        for (int r = 0; r < gameBoard.getNumRows(); r++) {
            for (int c = 0; c < gameBoard.getNumCols(); c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    EntityCell entityCell = (EntityCell) cell;
                    if (entityCell.getEntity() instanceof Player) {
                        entityCell.setentity(null);
                    }
                }
            }
        }
    }

    private EntityCell getEntityCellForPlayer(Player player) {
        Cell[][] board = gameBoard.getBoard();
        for (int r = 0; r < gameBoard.getNumRows(); r++) {
            for (int c = 0; c < gameBoard.getNumCols(); c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    EntityCell entityCell = (EntityCell) cell;
                    if (entityCell.getEntity() == player) {
                        return entityCell;
                    }
                }
            }
        }
        return null;
    }

    public void undoMove(MoveResult prevMove) {
        if (prevMove instanceof Alive) {
            Alive alive = (Alive) prevMove;
            Position origPos = alive.getOrigPosition();

            // Remove player from current position
            Player player = gameBoard.getPlayer();
            Cell[][] board = gameBoard.getBoard();
            for (int r = 0; r < gameBoard.getNumRows(); r++) {
                for (int c = 0; c < gameBoard.getNumCols(); c++) {
                    Cell cell = board[r][c];
                    if (cell instanceof EntityCell) {
                        EntityCell entityCell = (EntityCell) cell;
                        if (entityCell.getEntity() == player) {
                            entityCell.setentity(null);
                        }
                    }
                }
            }

            // Place player back at original position
            Cell origCell = board[origPos.getRow()][origPos.getCol()];
            if (origCell instanceof StopCell) {
                ((StopCell) origCell).setPlayer(player);
            } else {
                StopCell newStopCell = new StopCell(origPos, player);
                board[origPos.getRow()][origPos.getCol()] = newStopCell;
            }

            // Restore collected gems
            for (Position gemPos : alive.getCollectedGems()) {
                Cell gemCell = board[gemPos.getRow()][gemPos.getCol()];
                if (gemCell instanceof EntityCell) {
                    ((EntityCell) gemCell).setentity(new Gem());
                }
            }

            // Restore collected extra lives
            for (Position lifePos : alive.getCollectedExtraLives()) {
                Cell lifeCell = board[lifePos.getRow()][lifePos.getCol()];
                if (lifeCell instanceof EntityCell) {
                    ((EntityCell) lifeCell).setentity(new ExtraLife());
                }
            }
        }
    }
}
