public class GameBoardController {
    private GameBoard gameBoard;

    public GameBoardController() {
        this.gameBoard = null;
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
        if (gameBoard == null) {
            return new Invalid();
        }

        Player player = gameBoard.getPlayer();
        if (player == null) {
            return new Invalid();
        }

        Position currentPos = player.getOwner() != null ? player.getOwner().getPosition() : null;
        if (currentPos == null) {
            return new Invalid();
        }

        PositionOffset offset = direction.getOffset();
        Position testPos = currentPos.offsetByOrNull(offset, gameBoard.numRows, gameBoard.numCols);

        if (testPos == null) {
            return new Invalid(currentPos);
        }

        Cell firstCell = gameBoard.getCell(testPos.getRow(), testPos.getCol());
        if (firstCell instanceof Wall) {
            return new Invalid(currentPos);
        }

        Position origPosition = new Position(currentPos.getRow(), currentPos.getCol());
        Position lastSafePos = currentPos;
        java.util.List<Position> collectedGems = new java.util.ArrayList<>();
        java.util.List<Position> collectedExtraLives = new java.util.ArrayList<>();
        Position mineHit = null;

        Position stepPos = currentPos;
        while (true) {
            Position nextPos = stepPos.offsetByOrNull(offset, gameBoard.numRows, gameBoard.numCols);
            if (nextPos == null) {
                break;
            }
            Cell cell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());

            if (cell instanceof Wall) {
                break;
            }

            if (cell instanceof StopCell) {
                EntityCell ec = (EntityCell) cell;
                ec.setEntity(player);
                player.setOwner(ec);
                lastSafePos = nextPos;
                break;
            }

            if (cell instanceof EntityCell) {
                EntityCell ec = (EntityCell) cell;
                Entity entity = ec.getEntity();
                if (entity instanceof Mine) {
                    mineHit = nextPos;
                    break;
                }
                if (entity instanceof Gem) {
                    ec.setEntity(player);
                    player.setOwner(ec);
                    collectedGems.add(nextPos);
                    lastSafePos = nextPos;
                } else if (entity instanceof ExtraLife) {
                    ec.setEntity(player);
                    player.setOwner(ec);
                    collectedExtraLives.add(nextPos);
                    lastSafePos = nextPos;
                } else if (entity instanceof Player) {
                    break;
                } else {
                    ec.setEntity(player);
                    player.setOwner(ec);
                    lastSafePos = nextPos;
                }
            }

            stepPos = nextPos;
        }

        if (mineHit != null) {
            return new Dead(origPosition, origPosition);
        }

        if (lastSafePos.equals(origPosition)) {
            return new Invalid(currentPos);
        }

        return new Alive(lastSafePos, origPosition);
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive)) {
            return;
        }
        Alive alive = (Alive) prevMove;
        if (gameBoard == null) {
            return;
        }
        Player player = gameBoard.getPlayer();
        if (player == null) {
            return;
        }
        if (alive.getCollectedGems() != null) {
            for (Position p : alive.getCollectedGems()) {
                Cell cell = gameBoard.getCell(p.getRow(), p.getCol());
                if (cell instanceof EntityCell) {
                    ((EntityCell) cell).setEntity(new Gem());
                }
            }
        }
        if (alive.getCollectedExtraLives() != null) {
            for (Position p : alive.getCollectedExtraLives()) {
                Cell cell = gameBoard.getCell(p.getRow(), p.getCol());
                if (cell instanceof EntityCell) {
                    ((EntityCell) cell).setEntity(new ExtraLife());
                }
            }
        }
        EntityCell origCell = gameBoard.getEntityCell(alive.getOrigPosition());
        origCell.setEntity(player);
        player.setOwner(origCell);
    }
}
