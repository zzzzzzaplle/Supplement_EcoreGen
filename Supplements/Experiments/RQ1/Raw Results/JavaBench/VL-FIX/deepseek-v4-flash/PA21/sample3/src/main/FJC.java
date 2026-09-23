import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.Objects;

interface BoardElement {
    char toUnicodeChar();
    char toASCIIChar();
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

enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public PositionOffset getOffset() {
        switch (this) {
            case UP: return new PositionOffset(-1, 0);
            case DOWN: return new PositionOffset(1, 0);
            case LEFT: return new PositionOffset(0, -1);
            case RIGHT: return new PositionOffset(0, 1);
            default: throw new IllegalArgumentException("Unknown direction");
        }
    }

    public int getRowOffset() {
        return getOffset().getDRow();
    }

    public int getColOffset() {
        return getOffset().getDCol();
    }
}

abstract class Entity implements BoardElement {
    private EntityCell owner;

    public Entity() {
    }

    public EntityCell getOwner() {
        return owner;
    }

    public EntityCell setOwner(EntityCell owner) {
        this.owner = owner;
        return owner;
    }

    public EntityCell setowner(EntityCell owner) {
        EntityCell prev = this.owner;
        this.owner = owner;
        return prev;
    }
}

class EntityCell extends Cell {
    Entity entity;

    public EntityCell() {
        super();
    }

    public EntityCell(Position position) {
        super(position);
    }

    public EntityCell(Position position, Entity entity) {
        super(position);
        this.entity = entity;
        if (entity != null) {
            entity.setOwner(this);
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public Entity setEntity(Entity newEntity) {
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setOwner(this);
        }
        return newEntity;
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

class ExtraLife extends Entity {
    public ExtraLife() {
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

class GameBoard {
    public int numRows;
    public int numCols;
    public Cell[][] board;
    private Player player;

    public GameBoard() {
    }

    public GameBoard(int numRows, int numCols, Cell[][] board) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
        this.player = findPlayer();
    }

    private Player findPlayer() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    Entity entity = ((EntityCell) cell).getEntity();
                    if (entity instanceof Player) {
                        return (Player) entity;
                    }
                }
            }
        }
        return null;
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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
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
        Player player = gameBoard.getPlayer();
        if (player == null) {
            return new Invalid();
        }

        EntityCell playerCell = player.getOwner();
        if (playerCell == null) {
            return new Invalid();
        }

        Position currentPos = playerCell.getPosition();
        int dRow = direction.getRowOffset();
        int dCol = direction.getColOffset();

        // Try first step
        Position nextPos = currentPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
        if (nextPos == null) {
            return new Invalid();
        }

        Cell nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());
        if (nextCell instanceof Wall) {
            return new Invalid();
        }

        // Slide until we hit a wall, boundary, or StopCell
        Position slidePos = nextPos;
        Position lastValidPos = null;
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        boolean hitMine = false;
        Position minePos = null;

        while (slidePos != null) {
            Cell cell = gameBoard.getCell(slidePos.getRow(), slidePos.getCol());
            if (cell instanceof Wall) {
                // Stop before the wall
                break;
            }

            if (cell instanceof StopCell) {
                // Stop on the StopCell
                lastValidPos = slidePos;
                break;
            }

            if (cell instanceof EntityCell) {
                Entity entity = ((EntityCell) cell).getEntity();
                if (entity instanceof Mine) {
                    hitMine = true;
                    minePos = slidePos;
                    break;
                }
                if (entity instanceof Gem) {
                    collectedGems.add(slidePos);
                }
                if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(slidePos);
                }
            }

            lastValidPos = slidePos;

            // Try next step
            slidePos = slidePos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
        }

        if (lastValidPos == null) {
            return new Invalid();
        }

        if (hitMine) {
            // Dead move: return to original position, no collection
            return new Dead(currentPos, minePos);
        }

        // Alive move: move player to lastValidPos, collect gems and extra lives
        // Remove player from current cell
        playerCell.setEntity(null);

        // Move player to target cell
        EntityCell targetCell = gameBoard.getEntityCell(lastValidPos.getRow(), lastValidPos.getCol());
        if (targetCell == null) {
            // Should not happen
            return new Invalid();
        }

        // If target is a StopCell, set player on it
        if (targetCell instanceof StopCell) {
            ((StopCell) targetCell).setPlayer(player);
        } else {
            targetCell.setEntity(player);
        }

        // Collect gems
        for (Position gemPos : collectedGems) {
            EntityCell gemCell = gameBoard.getEntityCell(gemPos.getRow(), gemPos.getCol());
            if (gemCell != null) {
                gemCell.setEntity(null);
            }
        }

        // Collect extra lives
        for (Position lifePos : collectedExtraLives) {
            EntityCell lifeCell = gameBoard.getEntityCell(lifePos.getRow(), lifePos.getCol());
            if (lifeCell != null) {
                lifeCell.setEntity(null);
            }
        }

        return new Alive(lastValidPos, currentPos, collectedGems, collectedExtraLives);
    }

    public void undoMove(MoveResult prevMove) {
        if (prevMove instanceof Alive) {
            Alive alive = (Alive) prevMove;

            // Move player back to original position
            EntityCell currentCell = gameBoard.getEntityCell(alive.getNewPosition().getRow(), alive.getNewPosition().getCol());
            if (currentCell != null) {
                Player player = gameBoard.getPlayer();
                if (player != null) {
                    currentCell.setEntity(null);
                    EntityCell origCell = gameBoard.getEntityCell(alive.getOrigPosition().getRow(), alive.getOrigPosition().getCol());
                    if (origCell != null) {
                        origCell.setEntity(player);
                    }
                }
            }

            // Restore collected gems
            for (Position gemPos : alive.getCollectedGems()) {
                EntityCell gemCell = gameBoard.getEntityCell(gemPos.getRow(), gemPos.getCol());
                if (gemCell != null) {
                    gemCell.setEntity(new Gem());
                }
            }

            // Restore collected extra lives
            for (Position lifePos : alive.getCollectedExtraLives()) {
                EntityCell lifeCell = gameBoard.getEntityCell(lifePos.getRow(), lifePos.getCol());
                if (lifeCell != null) {
                    lifeCell.setEntity(new ExtraLife());
                }
            }
        }
    }
}

class GameBoardView {
    private final GameBoard gameBoard;

    public GameBoardView(final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    public void output(final boolean useUnicodeChars) {
        for (int r = 0; r < gameBoard.getNumRows(); ++r) {
            for (int c = 0; c < gameBoard.getNumCols(); ++c) {
                final Cell cell = gameBoard.getCell(r, c);
                final char ch = useUnicodeChars ? cell.toUnicodeChar() : cell.toASCIIChar();
                System.out.print(ch);
            }
            System.out.println();
        }
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
        GameBoardController controller = gameState.getGameBoardController();
        MoveResult result = controller.makeMove(direction);

        if (result instanceof Invalid) {
            // Invalid moves do not change counters and are not pushed
            return result;
        }

        if (result instanceof Alive) {
            // Increment numMoves
            gameState.incrementNumMoves();

            // Apply collected extra life effects
            Alive alive = (Alive) result;
            int extraLifeCount = alive.getCollectedExtraLives().size();
            gameState.increaseNumLives(extraLifeCount);

            // Push to move stack
            gameState.getMoveStack().push(result);
        } else if (result instanceof Dead) {
            // Increment numMoves and numDeaths
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();

            // Decrease lives by one (if finite)
            if (!gameState.hasUnlimitedLives()) {
                gameState.decrementNumLives();
            }

            // Not pushed to move stack
        }

        return result;
    }

    public boolean processUndo() {
        MoveStack moveStack = gameState.getMoveStack();
        if (moveStack.isEmpty()) {
            return false;
        }

        MoveResult move = moveStack.pop();
        if (move instanceof Alive) {
            Alive alive = (Alive) move;

            // Undo the move on the board
            gameState.getGameBoardController().undoMove(move);

            // Reverse collected extra life gains
            int extraLifeCount = alive.getCollectedExtraLives().size();
            gameState.decreaseNumLives(extraLifeCount);

            // Increment popCount (undo count)
            moveStack.popCount++;

            return true;
        }

        return false;
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

    public GameState() {
        this.moveStack = new MoveStack();
    }

    public GameState(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.numLives = -1; // unlimited
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numDeaths = 0;
        this.numMoves = 0;
        this.moveStack = new MoveStack();
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = gameBoard;
        this.numLives = numLives;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numDeaths = 0;
        this.numMoves = 0;
        this.moveStack = new MoveStack();
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
        if (hasUnlimitedLives()) {
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

    public boolean hasWon() {
        return getNumGems() == 0;
    }

    public boolean hasLost() {
        if (hasUnlimitedLives()) {
            return false;
        }
        return numLives <= 0;
    }

    public boolean hasUnlimitedLives() {
        return numLives < 0;
    }

    public int increaseNumLives(int delta) {
        if (!hasUnlimitedLives()) {
            numLives += delta;
        }
        return numLives;
    }

    public int decreaseNumLives(int delta) {
        if (!hasUnlimitedLives()) {
            numLives -= delta;
            if (numLives < 0) {
                numLives = 0;
            }
        }
        return numLives;
    }

    public int decrementNumLives() {
        return decreaseNumLives(1);
    }

    public int incrementNumMoves() {
        return ++numMoves;
    }

    public int incrementNumDeaths() {
        return ++numDeaths;
    }

    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    public int getScore() {
        int collectedGems = initialNumOfGems - getNumGems();
        int initialBoardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        int undoes = moveStack.popCount;
        return initialBoardSize + (collectedGems * 10) - (numMoves * 1) - (undoes * 2) - (numDeaths * 4);
    }

    public GameBoardController getGameBoardController() {
        return new GameBoardController(gameBoard);
    }

    public GameBoardView getGameBoardView() {
        return new GameBoardView(gameBoard);
    }
}

class Gem extends Entity {
    public Gem() {
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

abstract class MoveResult {
    public Position newPosition;

    public MoveResult() {
    }

    public Position getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(Position newPosition) {
        this.newPosition = newPosition;
    }
}

class Valid extends MoveResult {
    public Position origPosition;

    public Valid() {
    }

    public Valid(Position newPosition, Position origPosition) {
        this.newPosition = newPosition;
        this.origPosition = origPosition;
    }

    public Position getOrigPosition() {
        return origPosition;
    }

    public void setOrigPosition(Position origPosition) {
        this.origPosition = origPosition;
    }
}

class Alive extends Valid {
    public List<Position> collectedGems;
    public List<Position> collectedExtraLives;

    public Alive() {
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    public Alive(Position newPosition, Position origPosition, List<Position> collectedGems, List<Position> collectedExtraLives) {
        super(newPosition, origPosition);
        this.collectedGems = collectedGems;
        this.collectedExtraLives = collectedExtraLives;
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
    public Position minePosition;

    public Dead() {
    }

    public Dead(Position origPosition, Position minePosition) {
        super(origPosition, origPosition);
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
    }
}

class MoveStack {
    public int popCount;
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
        return stack.pop();
    }

    public MoveResult peek() {
        return stack.peek();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}

class Player extends Entity {
    public Player() {
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
        return new Position(row + dRow, col + dCol);
    }

    public Position offsetBy(PositionOffset offset) {
        return offsetBy(offset.getDRow(), offset.getDCol());
    }

    public Position offsetByOrNull(int dRow, int dCol, int numRows, int numCols) {
        int newRow = row + dRow;
        int newCol = col + dCol;
        if (newRow < 0 || newRow >= numRows || newCol < 0 || newCol >= numCols) {
            return null;
        }
        return new Position(newRow, newCol);
    }

    public Position offsetByOrNull(PositionOffset offset, int numRows, int numCols) {
        return offsetByOrNull(offset.getDRow(), offset.getDCol(), numRows, numCols);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
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

class StopCell extends EntityCell {
    public StopCell() {
        super();
    }

    public StopCell(Position position) {
        super(position);
    }

    public StopCell(Position position, Player player) {
        super(position, player);
    }

    public Entity setEntity(Entity newEntity) {
        if (newEntity instanceof Player || newEntity == null) {
            return super.setEntity(newEntity);
        }
        throw new IllegalArgumentException("StopCell can only contain Player entity");
    }

    public Player setPlayer(Player newPlayer) {
        return (Player) super.setEntity(newPlayer);
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

class GameStateSerializer {
    private GameStateSerializer() {
    }

    public static Path writeTo(final GameState gameState, final Path outputFile) throws FileAlreadyExistsException {
        Objects.requireNonNull(gameState);
        Objects.requireNonNull(outputFile);

        if (Files.exists(outputFile)) {
            throw new FileAlreadyExistsException(outputFile.toString());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
            writeTo(gameState, writer);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return outputFile;
    }

    static void writeTo(final GameState gameState, final BufferedWriter writer) throws IOException {
        Objects.requireNonNull(gameState);
        Objects.requireNonNull(writer);

        writer.write(Integer.toString(gameState.getGameBoard().getNumRows()));
        writer.newLine();
        writer.write(Integer.toString(gameState.getGameBoard().getNumCols()));
        writer.newLine();
        if (gameState.hasUnlimitedLives()) {
            writer.write("");
        } else {
            writer.write(Integer.toString(gameState.getNumLives()));
        }
        writer.newLine();

        for (int r = 0; r < gameState.getGameBoard().getNumRows(); ++r) {
            final Cell[] row = gameState.getGameBoard().getRow(r);

            for (final Cell cell : row) {
                writer.write(toCellChar(cell));
            }
            writer.newLine();
        }
    }

    public static GameState loadFrom(final Path inputFile) throws FileNotFoundException {
        Objects.requireNonNull(inputFile);

        if (!Files.isRegularFile(inputFile)) {
            throw new FileNotFoundException(inputFile.toString());
        }

        try (BufferedReader reader = Files.newBufferedReader(inputFile)) {
            return loadFrom(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static GameState loadFrom(final BufferedReader reader) throws IOException {
        Objects.requireNonNull(reader);

        final int numRows = Integer.parseInt(reader.readLine());
        final int numCols = Integer.parseInt(reader.readLine());
        final int numLives;
        {
            final String line = reader.readLine();
            if (line.isBlank()) {
                numLives = -1;
            } else {
                numLives = Integer.parseInt(line);
            }
        }

        final Cell[][] board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            final String line = reader.readLine();
            for (int c = 0; c < numCols; ++c) {
                board[r][c] = fromCellChar(line.charAt(c), new Position(r, c));
            }
        }

        final GameBoard gameBoard = new GameBoard(numRows, numCols, board);
        return numLives < 0 ? new GameState(gameBoard) : new GameState(gameBoard, numLives);
    }

    private static char toCellChar(final Cell cell) {
        Objects.requireNonNull(cell);

        if (cell instanceof Wall) {
            return 'W';
        }

        final EntityCell cellWithEntity = (EntityCell) cell;

        final Entity entity = cellWithEntity.getEntity();
        if (entity instanceof ExtraLife) {
            return 'L';
        }
        if (entity instanceof Gem) {
            return 'G';
        }
        if (entity instanceof Mine) {
            return 'M';
        }
        if (entity instanceof Player) {
            return 'P';
        }

        if (cellWithEntity instanceof StopCell) {
            return 'S';
        }
        return '.';
    }

    private static Cell fromCellChar(final char c, final Position position) {
        Objects.requireNonNull(position);

        return switch (c) {
            case 'W' -> new Wall(position);
            case 'L' -> new EntityCell(position, new ExtraLife());
            case 'G' -> new EntityCell(position, new Gem());
            case 'M' -> new EntityCell(position, new Mine());
            case 'P' -> new StopCell(position, new Player());
            case 'S' -> new StopCell(position);
            case '.' -> new EntityCell(position);
            default -> throw new IllegalArgumentException("Unknown cell representation: " + c);
        };
    }
}