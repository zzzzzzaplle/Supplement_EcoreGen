import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controller for handling game board moves.
 */
public class GameBoardController {
    private GameBoard gameBoard;

    /**
     * Creates a new GameBoardController for the specified game board.
     *
     * @param gameBoard The game board.
     */
    public GameBoardController(GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    /**
     * Makes a move in the specified direction from the player's current position.
     *
     * @param direction The direction to move.
     * @return The move result (Alive, Dead, or Invalid).
     */
    public MoveResult makeMove(Direction direction) {
        Player player = gameBoard.player;
        if (player == null) {
            return new Invalid(new Position(0, 0));
        }

        // Find player's current position
        Position playerPos = null;
        for (int r = 0; r < gameBoard.numRows; r++) {
            for (int c = 0; c < gameBoard.numCols; c++) {
                Cell cell = gameBoard.board[r][c];
                if (cell instanceof EntityCell) {
                    EntityCell ec = (EntityCell) cell;
                    if (ec.getEntity() == player) {
                        playerPos = new Position(r, c);
                        break;
                    }
                }
            }
            if (playerPos != null) break;
        }

        if (playerPos == null) {
            return new Invalid(new Position(0, 0));
        }

        int dRow = direction.getRowOffset();
        int dCol = direction.getColOffset();

        Position newPos = playerPos.offsetByOrNull(dRow, dCol, gameBoard.numRows, gameBoard.numCols);
        if (newPos == null) {
            // Blocked by boundary on first step
            return new Invalid(playerPos);
        }

        Cell nextCell = gameBoard.getCell(newPos.getRow(), newPos.getCol());

        if (nextCell instanceof Wall) {
            // Blocked by wall on first step - invalid
            return new Invalid(playerPos);
        }

        // Slide through the board
        Position currentPos = newPos;
        boolean hitMine = false;
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        Position stopPos = null;

        while (true) {
            Cell cellAtPos = gameBoard.getCell(currentPos.getRow(), currentPos.getCol());
            stopPos = currentPos;

            // Check if we hit a mine
            if (cellAtPos instanceof EntityCell) {
                EntityCell ec = (EntityCell) cellAtPos;
                if (ec.getEntity() instanceof Mine) {
                    hitMine = true;
                    break;
                }
            }

            // Check boundary
            Position nextCheck = currentPos.offsetByOrNull(dRow, dCol, gameBoard.numRows, gameBoard.numCols);
            if (nextCheck == null) {
                // Hit boundary - current position is the stopping point
                break;
            }

            Cell nextCellAtPos = gameBoard.getCell(nextCheck.getRow(), nextCheck.getCol());

            // Check if next cell is a wall - stop at current position
            if (nextCellAtPos instanceof Wall) {
                break;
            }

            // Collect gems or extra lives at current position (before moving to next)
            if (cellAtPos instanceof EntityCell) {
                EntityCell ec2 = (EntityCell) cellAtPos;
                Entity ent = ec2.getEntity();
                if (ent instanceof Gem) {
                    collectedGems.add(currentPos);
                    ec2.setEntity(null);
                } else if (ent instanceof ExtraLife) {
                    collectedExtraLives.add(currentPos);
                    ec2.setEntity(null);
                }
            }

            currentPos = nextCheck;
        }

        if (hitMine) {
            // Dead - player stays at original position, board unchanged
            return new Dead(playerPos, currentPos);
        }

        // Move the player to the stop position
        EntityCell oldEntityCell = gameBoard.getEntityCell(playerPos.getCol() > gameBoard.numCols ? 0 : playerPos.getRow(), playerPos.getCol());
        oldEntityCell.setEntity(null);

        EntityCell newEntityCell = gameBoard.getEntityCell(stopPos.getRow(), stopPos.getCol());
        newEntityCell.setEntity(player);

        Alive alive = new Alive(playerPos, stopPos);
        alive.setCollectedGems(collectedGems);
        alive.setCollectedExtraLives(collectedExtraLives);
        return alive;
    }

    /**
     * Gets the game board.
     *
     * @return The game board.
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * Sets the game board.
     *
     * @param gameBoard The game board.
     */
    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }
}
