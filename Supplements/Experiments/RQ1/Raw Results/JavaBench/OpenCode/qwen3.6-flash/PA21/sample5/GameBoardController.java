import java.util.Objects;

public class GameBoardController {
    private final GameBoard gameBoard;

    public GameBoardController() {
        this.gameBoard = null;
    }

    public GameBoardController(GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    public MoveResult makeMove(Direction direction) {
        Objects.requireNonNull(direction);
        Objects.requireNonNull(gameBoard);

        // Find player position
        Position playerPos = null;
        for (int r = 0; r < gameBoard.getNumRows(); ++r) {
            for (int c = 0; c < gameBoard.getNumCols(); ++c) {
                Cell cell = gameBoard.getCell(r, c);
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e instanceof Player && cell instanceof StopCell) {
                        playerPos = new Position(r, c);
                        break;
                    }
                }
            }
            if (playerPos != null) break;
        }

        if (playerPos == null) {
            return new Invalid();
        }

        int row = playerPos.getRow();
        int col = playerPos.getCol();
        int dRow = direction.getRowOffset();
        int dCol = direction.getColOffset();
        int startRow = row;
        int startCol = col;

        // Track original position for Dead result
        Position origPosition = new Position(startRow, startCol);

        // List to track collected items
        java.util.List<Position> collectedGems = new java.util.ArrayList<>();
        java.util.List<Position> collectedExtraLives = new java.util.ArrayList<>();

        // Slide step by step
        while (true) {
            int nextRow = row + dRow;
            int nextCol = col + dCol;

            // Check boundary
            if (nextRow < 0 || nextRow >= gameBoard.getNumRows() || nextCol < 0 || nextCol >= gameBoard.getNumCols()) {
                // Blocked by boundary - stop at current position
                // This is valid only if we moved at least one step
                if (row != startRow || col != startCol) {
                    Alive alive = new Alive(new Position(row, col), origPosition);
                    alive.setCollectedGems(collectedGems);
                    alive.setCollectedExtraLives(collectedExtraLives);
                    return alive;
                } else {
                    return new Invalid();
                }
            }

            Cell nextCell = gameBoard.getCell(nextRow, nextCol);

            // Check if wall
            if (nextCell instanceof Wall) {
                // Stop before wall - only valid if moved at least one step
                if (row != startRow || col != startCol) {
                    Alive alive = new Alive(new Position(row, col), origPosition);
                    alive.setCollectedGems(collectedGems);
                    alive.setCollectedExtraLives(collectedExtraLives);
                    return alive;
                } else {
                    return new Invalid();
                }
            }

            // Check for mine
            if (nextCell instanceof EntityCell) {
                Entity e = ((EntityCell) nextCell).getEntity();
                if (e instanceof Mine) {
                    // Dead - player stays at original position
                    return new Dead(origPosition, new Position(nextRow, nextCol));
                }

                // Check for collectible entity
                if (e instanceof Gem) {
                    collectedGems.add(new Position(nextRow, nextCol));
                    // Remove gem from board
                    ((EntityCell) nextCell).setEntity(null);
                } else if (e instanceof ExtraLife) {
                    collectedExtraLives.add(new Position(nextRow, nextCol));
                    // Remove extra life from board
                    ((EntityCell) nextCell).setEntity(null);
                }
            }

            // Check if StopCell - we stop exactly on it
            if (nextCell instanceof StopCell) {
                if (row != startRow || col != startCol) {
                    // Check if there's a player entity on this StopCell
                    Entity e = ((StopCell) nextCell).getEntity();
                    if (e instanceof Player) {
                        // StopCell already occupied by player, stop before it
                        Alive alive = new Alive(new Position(row, col), origPosition);
                        alive.setCollectedGems(collectedGems);
                        alive.setCollectedExtraLives(collectedExtraLives);
                        return alive;
                    }
                }
                // Move onto the StopCell
                if (row != startRow || col != startCol) {
                    Alive alive = new Alive(new Position(nextRow, nextCol), origPosition);
                    alive.setCollectedGems(collectedGems);
                    alive.setCollectedExtraLives(collectedExtraLives);
                    return alive;
                } else {
                    return new Invalid();
                }
            }

            // Regular EntityCell - move onto it
            row = nextRow;
            col = nextCol;
        }
    }

    public void undoMove(MoveResult prevMove) {
        // No-op for now - undo logic is in GameState
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }
}
