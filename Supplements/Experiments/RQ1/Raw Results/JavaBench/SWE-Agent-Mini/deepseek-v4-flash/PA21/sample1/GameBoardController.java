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
        // Find the player's current position
        Player player = gameBoard.getPlayer();
        if (player == null) {
            return new Invalid();
        }

        EntityCell playerCell = player.getOwner();
        if (playerCell == null) {
            return new Invalid();
        }

        Position currentPos = playerCell.getPosition();
        Position origPos = new Position(currentPos.getRow(), currentPos.getCol());

        int dRow = direction.getRowOffset();
        int dCol = direction.getColOffset();

        int steps = 0;
        Position lastValidPos = new Position(currentPos.getRow(), currentPos.getCol());
        Position checkPos = currentPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());

        java.util.List<Position> collectedGems = new java.util.ArrayList<>();
        java.util.List<Position> collectedExtraLives = new java.util.ArrayList<>();

        while (checkPos != null) {
            Cell cell = gameBoard.getCell(checkPos.getRow(), checkPos.getCol());

            if (cell instanceof Wall) {
                // Stop before the wall
                break;
            }

            if (cell instanceof StopCell) {
                // Stop on the StopCell
                lastValidPos = checkPos;
                steps++;
                break;
            }

            if (cell instanceof EntityCell) {
                EntityCell ec = (EntityCell) cell;
                Entity entity = ec.getEntity();

                if (entity instanceof Mine) {
                    // Dead - don't move, return to original position
                    Dead dead = new Dead();
                    dead.setOrigPosition(origPos);
                    dead.setNewPosition(origPos);
                    dead.setMinePosition(checkPos);
                    return dead;
                }

                if (entity instanceof Gem) {
                    collectedGems.add(new Position(checkPos.getRow(), checkPos.getCol()));
                }

                if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(new Position(checkPos.getRow(), checkPos.getCol()));
                }
            }

            lastValidPos = checkPos;
            steps++;
            checkPos = checkPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
        }

        if (steps == 0) {
            // Invalid - cannot move at all
            return new Invalid();
        }

        // Move player to last valid position
        // Remove player from old cell
        playerCell.setEntity(null);

        // Place player at new position
        EntityCell targetCell = gameBoard.getEntityCell(lastValidPos);
        if (targetCell instanceof StopCell) {
            ((StopCell) targetCell).setPlayer(player);
        } else {
            targetCell.setEntity(player);
        }
        player.setOwner(targetCell);

        // Collect gems and extra lives along the path (for alive moves)
        // Remove collected items from board
        for (Position gemPos : collectedGems) {
            EntityCell gemCell = gameBoard.getEntityCell(gemPos);
            if (gemCell != null) {
                gemCell.setEntity(null);
            }
        }
        for (Position lifePos : collectedExtraLives) {
            EntityCell lifeCell = gameBoard.getEntityCell(lifePos);
            if (lifeCell != null) {
                lifeCell.setEntity(null);
            }
        }

        Alive alive = new Alive();
        alive.setOrigPosition(origPos);
        alive.setNewPosition(lastValidPos);
        alive.setCollectedGems(collectedGems);
        alive.setCollectedExtraLives(collectedExtraLives);
        return alive;
    }

    public void undoMove(MoveResult prevMove) {
        if (prevMove instanceof Alive) {
            Alive alive = (Alive) prevMove;

            // Get player
            Player player = gameBoard.getPlayer();
            if (player == null) return;

            // Get current player cell and remove player
            EntityCell currentCell = player.getOwner();
            if (currentCell != null) {
                currentCell.setEntity(null);
            }

            // Restore player to original position
            Position origPos = alive.getOrigPosition();
            EntityCell origCell = gameBoard.getEntityCell(origPos);
            if (origCell instanceof StopCell) {
                ((StopCell) origCell).setPlayer(player);
            } else if (origCell != null) {
                origCell.setEntity(player);
            }
            player.setOwner(origCell);

            // Restore collected gems
            for (Position gemPos : alive.getCollectedGems()) {
                EntityCell gemCell = gameBoard.getEntityCell(gemPos);
                if (gemCell != null) {
                    gemCell.setEntity(new Gem());
                }
            }

            // Restore collected extra lives
            for (Position lifePos : alive.getCollectedExtraLives()) {
                EntityCell lifeCell = gameBoard.getEntityCell(lifePos);
                if (lifeCell != null) {
                    lifeCell.setEntity(new ExtraLife());
                }
            }
        }
    }
}
