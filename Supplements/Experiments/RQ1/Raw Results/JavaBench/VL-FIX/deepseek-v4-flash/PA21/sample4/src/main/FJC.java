import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
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
            default: throw new IllegalStateException("Unknown direction");
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
        Entity oldEntity = this.entity;
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setOwner(this);
        }
        return oldEntity;
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
    private int numRows;
    private int numCols;
    private Cell[][] board;
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
                    EntityCell ec = (EntityCell) cell;
                    if (ec.getEntity() instanceof Player) {
                        return (Player) ec.getEntity();
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
                    EntityCell ec = (EntityCell) cell;
                    if (ec.getEntity() instanceof Gem) {
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

        Position currentPos = player.getOwner().getPosition();
        int dRow = direction.getRowOffset();
        int dCol = direction.getColOffset();

        int newRow = currentPos.getRow() + dRow;
        int newCol = currentPos.getCol() + dCol;

        // Check if first step is blocked
        if (newRow < 0 || newRow >= gameBoard.getNumRows() || newCol < 0 || newCol >= gameBoard.getNumCols()) {
            return new Invalid();
        }
        Cell nextCell = gameBoard.getCell(newRow, newCol);
        if (nextCell instanceof Wall) {
            return new Invalid();
        }

        // Slide until we hit a wall, boundary, or stop cell
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        boolean hitMine = false;
        Position minePosition = null;

        int slideRow = currentPos.getRow();
        int slideCol = currentPos.getCol();

        while (true) {
            int nextSlideRow = slideRow + dRow;
            int nextSlideCol = slideCol + dCol;

            if (nextSlideRow < 0 || nextSlideRow >= gameBoard.getNumRows() || nextSlideCol < 0 || nextSlideCol >= gameBoard.getNumCols()) {
                // Hit boundary, stop before
                break;
            }

            Cell cell = gameBoard.getCell(nextSlideRow, nextSlideCol);
            if (cell instanceof Wall) {
                // Stop before wall
                break;
            }

            slideRow = nextSlideRow;
            slideCol = nextSlideCol;

            if (cell instanceof StopCell) {
                // Stop on stop cell
                break;
            }

            if (cell instanceof EntityCell) {
                EntityCell ec = (EntityCell) cell;
                Entity entity = ec.getEntity();
                if (entity instanceof Mine) {
                    hitMine = true;
                    minePosition = new Position(slideRow, slideCol);
                    break;
                } else if (entity instanceof Gem) {
                    collectedGems.add(new Position(slideRow, slideCol));
                } else if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(new Position(slideRow, slideCol));
                }
            }
        }

        // Check if we actually moved
        if (slideRow == currentPos.getRow() && slideCol == currentPos.getCol()) {
            return new Invalid();
        }

        if (hitMine) {
            return new Dead(new Position(currentPos.getRow(), currentPos.getCol()), minePosition);
        }

        // Perform the move: move player to final position
        EntityCell currentCell = gameBoard.getEntityCell(currentPos);
        currentCell.setEntity(null);

        Position finalPos = new Position(slideRow, slideCol);
        Cell finalCell = gameBoard.getCell(slideRow, slideCol);
        EntityCell targetCell;
        if (finalCell instanceof StopCell) {
            targetCell = (StopCell) finalCell;
        } else if (finalCell instanceof EntityCell) {
            targetCell = (EntityCell) finalCell;
        } else {
            // Should not happen, but create entity cell
            targetCell = new EntityCell(finalPos);
            gameBoard.getBoard()[slideRow][slideCol] = targetCell;
        }
        targetCell.setEntity(player);
        player.setOwner(targetCell);

        // Remove collected gems and extra lives from board
        for (Position gemPos : collectedGems) {
            EntityCell gemCell = gameBoard.getEntityCell(gemPos);
            if (gemCell != null && gemCell.getEntity() instanceof Gem) {
                gemCell.setEntity(null);
            }
        }
        for (Position lifePos : collectedExtraLives) {
            EntityCell lifeCell = gameBoard.getEntityCell(lifePos);
            if (lifeCell != null && lifeCell.getEntity() instanceof ExtraLife) {
                lifeCell.setEntity(null);
            }
        }

        return new Alive(finalPos, currentPos, collectedGems, collectedExtraLives);
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive)) {
            return;
        }
        Alive aliveMove = (Alive) prevMove;

        // Restore player position
        Player player = gameBoard.getPlayer();
        EntityCell currentCell = player.getOwner();
        currentCell.setEntity(null);

        Position origPos = aliveMove.getOrigPosition();
        Cell origCell = gameBoard.getCell(origPos.getRow(), origPos.getCol());
        EntityCell targetCell;
        if (origCell instanceof StopCell) {
            targetCell = (StopCell) origCell;
        } else if (origCell instanceof EntityCell) {
            targetCell = (EntityCell) origCell;
        } else {
            targetCell = new EntityCell(origPos);
            gameBoard.getBoard()[origPos.getRow()][origPos.getCol()] = targetCell;
        }
        targetCell.setEntity(player);
        player.setOwner(targetCell);

        // Restore collected gems
        for (Position gemPos : aliveMove.getCollectedGems()) {
            EntityCell gemCell = gameBoard.getEntityCell(gemPos);
            if (gemCell != null && gemCell.getEntity() == null) {
                gemCell.setEntity(new Gem());
            }
        }

        // Restore collected extra lives
        for (Position lifePos : aliveMove.getCollectedExtraLives()) {
            EntityCell lifeCell = gameBoard.getEntityCell(lifePos);
            if (lifeCell != null && lifeCell.getEntity() == null) {
                lifeCell.setEntity(new ExtraLife());
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
        MoveResult result = gameState.getGameBoardController().makeMove(direction);

        if (result instanceof Invalid) {
            // Invalid moves do not change counters and are not pushed
            return result;
        }

        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            gameState.incrementNumMoves();

            // Apply collected extra lives
            int extraLifeCount = alive.getCollectedExtraLives().size();
            for (int i = 0; i < extraLifeCount; i++) {
                gameState.increaseNumLives(1);
            }

            gameState.getMoveStack().push(result);
        } else if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            if (!gameState.hasUnlimitedLives()) {
                gameState.decrementNumLives();
            }
            // Dead moves are not pushed to stack
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
        // Reverse extra life gains
        int extraLifeCount = alive.getCollectedExtraLives().size();
        for (int i = 0; i < extraLifeCount; i++) {
            if (gameState.getNumLives() > 0) {
                gameState.decreaseNumLives(1);
            }
        }

        gameState.getGameBoardController().undoMove(move);
        return true;
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
    private int initialBoardSize;

    public GameState() {
        this.numDeaths = 0;
        this.numMoves = 0;
        this.moveStack = new MoveStack();
    }

    public GameState(GameBoard gameBoard) {
        this();
        this.gameBoard = gameBoard;
        this.numLives = -1; // unlimited
        this.initialNumOfGems = gameBoard.getNumGems();
        this.initialBoardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this();
        this.gameBoard = gameBoard;
        this.numLives = numLives;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.initialBoardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
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
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        numLives += delta;
        return numLives;
    }

    public int decreaseNumLives(int delta) {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        numLives -= delta;
        if (numLives < 0) {
            numLives = 0;
        }
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
        int undoes = moveStack.getPopCount();
        return initialBoardSize + (collectedGems * 10) - (numMoves * 1) - (undoes * 2) - (numDeaths * 4);
    }

    public GameBoardController getGameBoardController() {
        return new GameBoardController(gameBoard);
    }

    public GameBoardView getGameBoardView() {
        return new GameBoardView(gameBoard);
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

    public int getInitialBoardSize() {
        return initialBoardSize;
    }

    public void setInitialBoardSize(int initialBoardSize) {
        this.initialBoardSize = initialBoardSize;
    }
}

class Gem extends Entity {
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
    private Position newPosition;

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
    private Position origPosition;

    public Valid() {
    }

    public Valid(Position newPosition, Position origPosition) {
        super(newPosition);
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
    private List<Position> collectedGems;
    private List<Position> collectedExtraLives;

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
    private Position minePosition;

    public Dead() {
    }

    public Dead(Position newPosition, Position minePosition) {
        super(newPosition, newPosition);
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
        super(null);
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
        if (stack.isEmpty()) {
            return null;
        }
        popCount++;
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
        return 31 * row + col;
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
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
    }

    public StopCell(Position position) {
        super(position);
    }

    public StopCell(Position position, Entity entity) {
        super(position, entity);
    }

    @Override
    public Entity setEntity(Entity newEntity) {
        Entity oldEntity = getEntity();
        super.setEntity(newEntity);
        return oldEntity;
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

class Wall extends Cell {
    public Wall() {
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

    public static Path writeTo(final GameState gameState, final Path outputFile)
            throws FileAlreadyExistsException {
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

    static void writeTo(final GameState gameState, final BufferedWriter writer)
            throws IOException {
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

    public static GameState loadFrom(final Path inputFile)
            throws FileNotFoundException {
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