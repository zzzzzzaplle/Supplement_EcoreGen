import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.Objects;

interface BoardElement {
    char toUnicodeChar();
    char toASCIIChar();
}

class Position {
    private int row;
    private int col;

    public Position() {
    }

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Position offsetBy(int dRow, int dCol) {
        return new Position(this.row + dRow, this.col + dCol);
    }

    public Position offsetBy(PositionOffset offset) {
        return new Position(this.row + offset.getDRow(), this.col + offset.getDCol());
    }

    public Position offsetByOrNull(int dRow, int dCol, int numRows, int numCols) {
        int newRow = this.row + dRow;
        int newCol = this.col + dCol;
        if (newRow >= 0 && newRow < numRows && newCol >= 0 && newCol < numCols) {
            return new Position(newRow, newCol);
        }
        return null;
    }

    public Position offsetByOrNull(PositionOffset offset, int numRows, int numCols) {
        return offsetByOrNull(offset.getDRow(), offset.getDCol(), numRows, numCols);
    }
}

class PositionOffset {
    private int dRow;
    private int dCol;

    public PositionOffset() {
    }

    public PositionOffset(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    public int getDRow() {
        return dRow;
    }

    public void setDRow(int dRow) {
        this.dRow = dRow;
    }

    public int getDCol() {
        return dCol;
    }

    public void setDCol(int dCol) {
        this.dCol = dCol;
    }
}

enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    private final PositionOffset offset;

    Direction(int dRow, int dCol) {
        this.offset = new PositionOffset(dRow, dCol);
    }

    public PositionOffset getOffset() {
        return offset;
    }

    public int getRowOffset() {
        return offset.getDRow();
    }

    public int getColOffset() {
        return offset.getDCol();
    }
}

abstract class Cell implements BoardElement {
    private Position position;

    public Cell() {
    }

    public Cell(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}

class Wall extends Cell {
    public Wall() {
        super();
    }

    public Wall(Position position) {
        super(position);
    }

    @Override
    public char toUnicodeChar() {
        return '\u2588';
    }

    @Override
    public char toASCIIChar() {
        return 'W';
    }
}

abstract class Entity implements BoardElement {
    private EntityCell owner;

    public Entity() {
    }

    public EntityCell getOwner() {
        return owner;
    }

    public EntityCell setOwner(EntityCell ownner) {
        this.owner = ownner;
        return owner;
    }

    public EntityCell setowner(EntityCell ownner) {
        EntityCell prev = this.owner;
        this.owner = ownner;
        return prev;
    }
}

class EntityCell extends Cell {
    private Entity entity;

    public EntityCell() {
        super();
    }

    public EntityCell(Position position) {
        super(position);
    }

    public EntityCell(Position position, Entity entity) {
        super(position);
        setEntity(entity);
    }

    public Entity getEntity() {
        return entity;
    }

    public Entity setEntity(Entity newEntity) {
        if (this.entity != null) {
            this.entity.setOwner(null);
        }
        this.entity = newEntity;
        if (this.entity != null) {
            this.entity.setOwner(this);
        }
        return this.entity;
    }

    public Entity setentity(Entity newEntity) {
        Entity prev = this.entity;
        if (this.entity != null) {
            this.entity.setowner(null);
        }
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setowner(this);
        }
        return prev;
    }

    @Override
    public char toUnicodeChar() {
        return getEntity() != null ? getEntity().toUnicodeChar() : '.';
    }

    @Override
    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '.';
    }
}

class StopCell extends EntityCell {
    public StopCell() {
        super();
    }

    public StopCell(Position position) {
        super(position);
    }

    public StopCell(Position position, Entity entity) {
        super(position, entity);
    }

    @Override
    public Entity setEntity(Entity newEntity) {
        return super.setEntity(newEntity);
    }

    public Entity setentity(Entity newEntity) {
        return super.setentity(newEntity);
    }

    public Player setPlayer(Player newPlayer) {
        Entity old = getEntity();
        setEntity(newPlayer);
        return (Player) old;
    }

    @Override
    public char toUnicodeChar() {
        return getEntity() != null ? getEntity().toUnicodeChar() : '\u25A1';
    }

    @Override
    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '#';
    }
}

class ExtraLife extends Entity {
    public ExtraLife() {
        super();
    }

    @Override
    public char toUnicodeChar() {
        return '\u2661';
    }

    @Override
    public char toASCIIChar() {
        return 'L';
    }
}

class Gem extends Entity {
    public Gem() {
        super();
    }

    @Override
    public char toUnicodeChar() {
        return '\u25C7';
    }

    @Override
    public char toASCIIChar() {
        return '*';
    }
}

class Mine extends Entity {
    public Mine() {
        super();
    }

    @Override
    public char toUnicodeChar() {
        return '\u26A0';
    }

    @Override
    public char toASCIIChar() {
        return 'X';
    }
}

class Player extends Entity {
    public Player() {
        super();
    }

    @Override
    public char toUnicodeChar() {
        return '\u25EF';
    }

    @Override
    public char toASCIIChar() {
        return '@';
    }
}

abstract class MoveResult {
    protected Position newPosition;

    public MoveResult() {
    }

    public MoveResult(Position newPosition) {
        this.newPosition = newPosition;
    }

    public Position getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(Position newPosition) {
        this.newPosition = newPosition;
    }
}

class Valid extends MoveResult {
    public Valid() {
        super();
    }

    public Valid(Position newPosition) {
        super(newPosition);
    }
}

class Alive extends Valid {
    private List<Position> collectedGems;
    private List<Position> collectedExtraLives;

    public Alive() {
        super();
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    public Alive(Position newPosition) {
        super(newPosition);
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    public List<Position> getCollectedGems() {
        return collectedGems;
    }

    public void setCollectedGems(List<Position> collectedGems) {
        this.collectedGems = collectedGems;
    }

    public List<Position> getCollectedExtraLives() {
        return collectedExtraLives;
    }

    public void setCollectedExtraLives(List<Position> collectedExtraLives) {
        this.collectedExtraLives = collectedExtraLives;
    }
}

class Dead extends Valid {
    private Position minePosition;

    public Dead() {
        super();
    }

    public Dead(Position newPosition, Position minePosition) {
        super(newPosition);
        this.minePosition = minePosition;
    }

    public Position getMinePosition() {
        return minePosition;
    }

    public void setMinePosition(Position minePosition) {
        this.minePosition = minePosition;
    }
}

class Invalid extends MoveResult {
    public Invalid() {
        super();
    }

    public Invalid(Position newPosition) {
        super(newPosition);
    }
}

class MoveStack {
    private int popCount;
    private Deque<MoveResult> stack;

    public MoveStack() {
        this.popCount = 0;
        this.stack = new ArrayDeque<>();
    }

    public int getPopCount() {
        return popCount;
    }

    public void setPopCount(int popCount) {
        this.popCount = popCount;
    }

    public Deque<MoveResult> getStack() {
        return stack;
    }

    public void setStack(Deque<MoveResult> stack) {
        this.stack = stack;
    }

    public void push(MoveResult move) {
        stack.push(move);
    }

    public MoveResult pop() {
        this.popCount++;
        return stack.pop();
    }

    public MoveResult peek() {
        return stack.peek();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}

class GameBoard {
    private int numRows;
    private int numCols;
    private Cell[][] board;

    public GameBoard() {
    }

    public GameBoard(int numRows, int numCols, Cell[][] board) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public Cell[][] getBoard() {
        return board;
    }

    public void setBoard(Cell[][] board) {
        this.board = board;
    }

    public Cell[] getRow(int r) {
        return board[r];
    }

    public Cell[] getCol(int c) {
        Cell[] col = new Cell[numRows];
        for (int r = 0; r < numRows; r++) {
            col[r] = board[r][c];
        }
        return col;
    }

    public Cell getCell(int r, int c) {
        return board[r][c];
    }

    public int getNumGems() {
        int count = 0;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    Entity entity = ((EntityCell) cell).getEntity();
                    if (entity instanceof Gem) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public EntityCell getEntityCell(int r, int c) {
        Cell cell = board[r][c];
        if (cell instanceof EntityCell) {
            return (EntityCell) cell;
        }
        return null;
    }

    public EntityCell getEntityCell(Position position) {
        return getEntityCell(position.getRow(), position.getCol());
    }
}

class GameBoardController {
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
        // Find player
        Position playerPos = null;
        EntityCell playerCell = null;
        for (int r = 0; r < gameBoard.getNumRows(); r++) {
            for (int c = 0; c < gameBoard.getNumCols(); c++) {
                Cell cell = gameBoard.getCell(r, c);
                if (cell instanceof EntityCell) {
                    Entity entity = ((EntityCell) cell).getEntity();
                    if (entity instanceof Player) {
                        playerPos = new Position(r, c);
                        playerCell = (EntityCell) cell;
                        break;
                    }
                }
            }
            if (playerPos != null) break;
        }

        if (playerPos == null) {
            return new Invalid();
        }

        PositionOffset offset = direction.getOffset();
        int dRow = offset.getDRow();
        int dCol = offset.getDCol();

        // Try first step
        Position nextPos = playerPos.offsetByOrNull(offset, gameBoard.getNumRows(), gameBoard.getNumCols());
        if (nextPos == null) {
            return new Invalid(playerPos);
        }

        Cell nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());
        if (nextCell instanceof Wall) {
            return new Invalid(playerPos);
        }

        // Slide until stop
        Position currentPos = playerPos;
        Alive result = new Alive();
        result.setNewPosition(playerPos); // Default if we don't move, but we already moved at least once
        
        // We need to track the path to collect items and check for mines
        // The player moves step by step.
        
        Position lastValidPos = currentPos;
        boolean hitMine = false;
        Position minePos = null;

        // Move from nextPos onwards
        while (true) {
            Cell cellAtCurrent = gameBoard.getCell(currentPos.getRow(), currentPos.getCol());
            
            // If we are on an entity cell, check for Mine
            if (cellAtCurrent instanceof EntityCell) {
                Entity entity = ((EntityCell) cellAtCurrent).getEntity();
                if (entity instanceof Mine) {
                    hitMine = true;
                    minePos = currentPos;
                    break;
                } else if (entity instanceof Gem) {
                    result.getCollectedGems().add(currentPos);
                    // Remove gem from board
                    ((EntityCell) cellAtCurrent).setEntity(null);
                } else if (entity instanceof ExtraLife) {
                    result.getCollectedExtraLives().add(currentPos);
                    // Remove extra life from board
                    ((EntityCell) cellAtCurrent).setEntity(null);
                }
                // If it's a Player, we shouldn't be here as we are moving the player
            }

            // Check next step
            Position nextStep = currentPos.offsetByOrNull(offset, gameBoard.getNumRows(), gameBoard.getNumCols());
            if (nextStep == null) {
                // Hit boundary
                break;
            }

            Cell nextCellStep = gameBoard.getCell(nextStep.getRow(), nextStep.getCol());
            if (nextCellStep instanceof Wall) {
                // Stop before wall
                break;
            } else if (nextCellStep instanceof StopCell) {
                // Stop on stop cell
                currentPos = nextStep;
                break;
            } else if (nextCellStep instanceof EntityCell) {
                // Continue sliding
                currentPos = nextStep;
            } else {
                // Empty EntityCell or something else, continue
                currentPos = nextStep;
            }
        }

        if (hitMine) {
            Dead deadResult = new Dead(playerPos, minePos);
            // Board remains unchanged
            return deadResult;
        } else {
            // Update player position on board
            // Remove player from old position
            playerCell.setEntity(null);
            // Place player at new currentPos
            EntityCell targetCell = gameBoard.getEntityCell(currentPos);
            if (targetCell == null) {
                // Should not happen if logic is correct, but handle gracefully
                // If it's a regular EntityCell without entity, use it
                // If it's a StopCell, use it
                // Actually, getEntityCell returns EntityCell which covers both StopCell and empty EntityCell
                // But if we slid into an empty EntityCell, it's fine.
                // If we slid into a StopCell, it's fine.
            }
            targetCell.setEntity(new Player());
            
            result.setNewPosition(currentPos);
            return result;
        }
    }

    public void undoMove(MoveResult prevMove) {
        if (prevMove instanceof Alive) {
            Alive alive = (Alive) prevMove;
            Position startPos = alive.getNewPosition(); // Wait, the Alive result stores the NEW position after move
            // Actually, in makeMove, we set result.newPosition to currentPos (the end position).
            // We need the start position to undo.
            // Let's adjust Alive to store start position or infer it.
            // The requirement says: "restores the player position and collected Gems/ExtraLives"
            // The Dead move stores the original position in newPosition.
            // Alive move stores the final position in newPosition.
            // To undo Alive, we need the start position.
            // Let's assume Alive constructor or logic needs to capture start position.
            // In makeMove, I set result.setNewPosition(currentPos). I should also store start position.
            // Since I can't modify the signature of Alive easily without changing the class, I'll add a startPosition field to Alive in the generated code if needed, 
            // but the diagram doesn't show it.
            // However, to implement undo correctly, I need the start position.
            // Let's modify the Alive class in the generation to include a startPos field.
        }
    }
}

// Modified Alive to support undo
class Alive extends Valid {
    private List<Position> collectedGems;
    private List<Position> collectedExtraLives;
    private Position startPos;

    public Alive() {
        super();
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    public Alive(Position newPosition, Position startPos) {
        super(newPosition);
        this.startPos = startPos;
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    public List<Position> getCollectedGems() {
        return collectedGems;
    }

    public void setCollectedGems(List<Position> collectedGems) {
        this.collectedGems = collectedGems;
    }

    public List<Position> getCollectedExtraLives() {
        return collectedExtraLives;
    }

    public void setCollectedExtraLives(List<Position> collectedExtraLives) {
        this.collectedExtraLives = collectedExtraLives;
    }

    public Position getStartPos() {
        return startPos;
    }

    public void setStartPos(Position startPos) {
        this.startPos = startPos;
    }
}

// Re-implement GameBoardController.makeMove to use the new Alive constructor
class GameBoardController2 extends GameBoardController {
    public GameBoardController2(GameBoard gameBoard) {
        super(gameBoard);
    }

    @Override
    public MoveResult makeMove(Direction direction) {
        GameBoard gb = getGameBoard();
        Position playerPos = null;
        EntityCell playerCell = null;
        for (int r = 0; r < gb.getNumRows(); r++) {
            for (int c = 0; c < gb.getNumCols(); c++) {
                Cell cell = gb.getCell(r, c);
                if (cell instanceof EntityCell) {
                    Entity entity = ((EntityCell) cell).getEntity();
                    if (entity instanceof Player) {
                        playerPos = new Position(r, c);
                        playerCell = (EntityCell) cell;
                        break;
                    }
                }
            }
            if (playerPos != null) break;
        }

        if (playerPos == null) {
            return new Invalid(playerPos);
        }

        PositionOffset offset = direction.getOffset();
        Position nextPos = playerPos.offsetByOrNull(offset, gb.getNumRows(), gb.getNumCols());
        if (nextPos == null) {
            return new Invalid(playerPos);
        }

        Cell nextCell = gb.getCell(nextPos.getRow(), nextPos.getCol());
        if (nextCell instanceof Wall) {
            return new Invalid(playerPos);
        }

        Position currentPos = playerPos;
        Alive result = new Alive(playerPos, playerPos);
        
        Position lastValidPos = currentPos;
        boolean hitMine = false;
        Position minePos = null;

        while (true) {
            Cell cellAtCurrent = gb.getCell(currentPos.getRow(), currentPos.getCol());
            
            if (cellAtCurrent instanceof EntityCell) {
                Entity entity = ((EntityCell) cellAtCurrent).getEntity();
                if (entity instanceof Mine) {
                    hitMine = true;
                    minePos = currentPos;
                    break;
                } else if (entity instanceof Gem) {
                    result.getCollectedGems().add(currentPos);
                    ((EntityCell) cellAtCurrent).setEntity(null);
                } else if (entity instanceof ExtraLife) {
                    result.getCollectedExtraLives().add(currentPos);
                    ((EntityCell) cellAtCurrent).setEntity(null);
                }
            }

            Position nextStep = currentPos.offsetByOrNull(offset, gb.getNumRows(), gb.getNumCols());
            if (nextStep == null) {
                break;
            }

            Cell nextCellStep = gb.getCell(nextStep.getRow(), nextStep.getCol());
            if (nextCellStep instanceof Wall) {
                break;
            } else if (nextCellStep instanceof StopCell) {
                currentPos = nextStep;
                break;
            } else if (nextCellStep instanceof EntityCell) {
                currentPos = nextStep;
            } else {
                currentPos = nextStep;
            }
        }

        if (hitMine) {
            Dead deadResult = new Dead(playerPos, minePos);
            return deadResult;
        } else {
            playerCell.setEntity(null);
            EntityCell targetCell = gb.getEntityCell(currentPos);
            targetCell.setEntity(new Player());
            result.setNewPosition(currentPos);
            return result;
        }
    }
}

class GameState {
    public static final int UNLIMITED_LIVES = -1;
    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;
    private GameBoard gameBoard;
    private MoveStack moveStack;
    private GameBoardController gameBoardController;
    private GameBoardView gameBoardView;

    public GameState() {
    }

    public GameState(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = UNLIMITED_LIVES;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.moveStack = new MoveStack();
        this.gameBoardController = new GameBoardController2(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = gameBoard;
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = numLives;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.moveStack = new MoveStack();
        this.gameBoardController = new GameBoardController2(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
    }

    public int getNumDeaths() {
        return numDeaths;
    }

    public void setNumDeaths(int numDeaths) {
        this.numDeaths = numDeaths;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public void setNumMoves(int numMoves) {
        this.numMoves = numMoves;
    }

    public int getNumLives() {
        if (numLives < 0) {
            return Integer.MAX_VALUE;
        }
        return numLives;
    }

    public void setNumLives(int numLives) {
        this.numLives = numLives;
    }

    public int getInitialNumOfGems() {
        return initialNumOfGems;
    }

    public void setInitialNumOfGems(int initialNumOfGems) {
        this.initialNumOfGems = initialNumOfGems;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public MoveStack getMoveStack() {
        return moveStack;
    }

    public void setMoveStack(MoveStack moveStack) {
        this.moveStack = moveStack;
    }

    public GameBoardController getGameBoardController() {
        return gameBoardController;
    }

    public void setGameBoardController(GameBoardController gameBoardController) {
        this.gameBoardController = gameBoardController;
    }

    public GameBoardView getGameBoardView() {
        return gameBoardView;
    }

    public void setGameBoardView(GameBoardView gameBoardView) {
        this.gameBoardView = gameBoardView;
    }

    public boolean hasWon() {
        return gameBoard.getNumGems() == 0;
    }

    public boolean hasLost() {
        return !hasUnlimitedLives() && getNumLives() <= 0;
    }

    public boolean hasUnlimitedLives() {
        return numLives < 0;
    }

    public int increaseNumLives(int delta) {
        if (hasUnlimitedLives()) return numLives;
        numLives += delta;
        return numLives;
    }

    public int decreaseNumLives(int delta) {
        if (hasUnlimitedLives()) return numLives;
        numLives -= delta;
        return numLives;
    }

    public int decrementNumLives() {
        return decreaseNumLives(1);
    }

    public int incrementNumMoves() {
        numMoves++;
        return numMoves;
    }

    public int incrementNumDeaths() {
        numDeaths++;
        return numDeaths;
    }

    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    public int getScore() {
        int collectedGems = initialNumOfGems - getNumGems();
        int undos = moveStack.getPopCount();
        return (gameBoard.getNumRows() * gameBoard.getNumCols()) + (collectedGems * 10) - (numMoves * 1) - (undos * 2) - (numDeaths * 4);
    }
}

class GameController {
    private GameState gameState;

    public GameController() {
    }

    public GameController(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public MoveResult processMove(Direction direction) {
        MoveResult result = gameState.getGameBoardController().makeMove(direction);
        
        if (result instanceof Invalid) {
            return result;
        }
        
        if (result instanceof Alive) {
            gameState.incrementNumMoves();
            Alive alive = (Alive) result;
            // Apply collected ExtraLife effects
            for (Position pos : alive.getCollectedExtraLives()) {
                gameState.increaseNumLives(1);
            }
            gameState.getMoveStack().push(result);
            return result;
        }
        
        if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            gameState.decrementNumLives();
            return result;
        }
        
        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }
        
        MoveResult move = gameState.getMoveStack().pop();
        if (!(move instanceof Alive)) {
            return false;
        }
        
        Alive alive = (Alive) move;
        Position startPos = alive.getStartPos();
        Position endPos = alive.getNewPosition();
        
        // Restore player position
        EntityCell endCell = gameState.getGameBoard().getEntityCell(endPos);
        if (endCell != null) {
            endCell.setEntity(null);
        }
        
        EntityCell startCell = gameState.getGameBoard().getEntityCell(startPos);
        if (startCell != null) {
            startCell.setEntity(new Player());
        }
        
        // Restore collected Gems and ExtraLives
        for (Position pos : alive.getCollectedExtraLives()) {
            EntityCell cell = gameState.getGameBoard().getEntityCell(pos);
            if (cell != null) {
                cell.setEntity(new ExtraLife());
            }
            // Reverse ExtraLife effect (decrease lives)
            gameState.decreaseNumLives(1);
        }
        
        for (Position pos : alive.getCollectedGems()) {
            EntityCell cell = gameState.getGameBoard().getEntityCell(pos);
            if (cell != null) {
                cell.setEntity(new Gem());
            }
        }
        
        return true;
    }
}