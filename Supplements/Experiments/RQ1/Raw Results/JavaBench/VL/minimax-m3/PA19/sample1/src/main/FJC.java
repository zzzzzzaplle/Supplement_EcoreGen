import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PipePatterns: central constant repository for all map-rendering characters.
 */
final class PipePatterns {
    private PipePatterns() {}

    public static final char WALL = '█';

    public static final class Filled {
        private Filled() {}
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '═';
        public static final char VERTICAL = '║';
        public static final char TOP_LEFT = '╗';
        public static final char TOP_RIGHT = '╔';
        public static final char BOTTOM_LEFT = '╝';
        public static final char BOTTOM_RIGHT = '╚';
        public static final char CROSS = '╬';
    }

    public static final class Unfilled {
        private Unfilled() {}
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '─';
        public static final char VERTICAL = '│';
        public static final char TOP_LEFT = '┘';
        public static final char TOP_RIGHT = '┐';
        public static final char BOTTOM_LEFT = '└';
        public static final char BOTTOM_RIGHT = '┌';
        public static final char CROSS = '┼';
    }
}

/**
 * StringUtils: lightweight string helper utilities.
 */
final class StringUtils {
    private StringUtils() {}

    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }
}

/**
 * Direction enum with opposite and offset.
 */
enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public Direction getOpposite() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: throw new IllegalStateException("Unknown direction");
        }
    }

    public Coordinate getOffset() {
        switch (this) {
            case UP: return new Coordinate(-1, 0);
            case DOWN: return new Coordinate(1, 0);
            case LEFT: return new Coordinate(0, -1);
            case RIGHT: return new Coordinate(0, 1);
            default: throw new IllegalStateException("Unknown direction");
        }
    }
}

/**
 * TerminationType enum.
 */
enum TerminationType {
    SOURCE,
    SINK
}

/**
 * PipeShape enum.
 */
enum PipeShape {
    HORIZONTAL(PipePatterns.Filled.HORIZONTAL, PipePatterns.Unfilled.HORIZONTAL),
    VERTICAL(PipePatterns.Filled.VERTICAL, PipePatterns.Unfilled.VERTICAL),
    TOP_LEFT(PipePatterns.Filled.TOP_LEFT, PipePatterns.Unfilled.TOP_LEFT),
    TOP_RIGHT(PipePatterns.Filled.TOP_RIGHT, PipePatterns.Unfilled.TOP_RIGHT),
    BOTTOM_LEFT(PipePatterns.Filled.BOTTOM_LEFT, PipePatterns.Unfilled.BOTTOM_LEFT),
    BOTTOM_RIGHT(PipePatterns.Filled.BOTTOM_RIGHT, PipePatterns.Unfilled.BOTTOM_RIGHT),
    CROSS(PipePatterns.Filled.CROSS, PipePatterns.Unfilled.CROSS);

    private final char filledChar;
    private final char unfilledChar;

    PipeShape(char filledChar, char unfilledChar) {
        this.filledChar = filledChar;
        this.unfilledChar = unfilledChar;
    }

    public char getCharByState(boolean isFilled) {
        return isFilled ? filledChar : unfilledChar;
    }

    public static PipeShape fromCode(String code) {
        switch (code) {
            case "HZ": return HORIZONTAL;
            case "VT": return VERTICAL;
            case "TL": return TOP_LEFT;
            case "TR": return TOP_RIGHT;
            case "BL": return BOTTOM_LEFT;
            case "BR": return BOTTOM_RIGHT;
            case "CR": return CROSS;
            default: throw new IllegalStateException("Unknown pipe code: " + code);
        }
    }
}

/**
 * Representation of a coordinate in {Map}.
 */
 class Coordinate {

    public int row;
    public int col;

    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Coordinate() {
        this.row = 0;
        this.col = 0;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Coordinate)) {
            return false;
        }

        return equals((Coordinate) obj);
    }

    public boolean equals(Coordinate other) {
        return this.row == other.row && this.col == other.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    public Coordinate add(Coordinate other) {
        return new Coordinate(this.row + other.row, this.col + other.col);
    }
}

/**
 * Interface for elements that can be rendered as a single character.
 */
interface MapElement {
    char toSingleChar();
}

/**
 * DelayBar: counts down from initial value.
 */
class DelayBar {
    private int initialValue;
    private int currentValue;

    public DelayBar() {
        this.initialValue = 0;
        this.currentValue = 0;
    }

    public DelayBar(int initialValue) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
    }

    public int getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(int initialValue) {
        this.initialValue = initialValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public void countdown() {
        currentValue--;
    }

    public int distance() {
        return -currentValue;
    }
}

/**
 * Pipe: represents a pipe with a shape and filled state.
 */
class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

    public Pipe() {
        this.shape = PipeShape.HORIZONTAL;
        this.filled = false;
    }

    public Pipe(PipeShape shape) {
        this.shape = shape;
        this.filled = false;
    }

    public PipeShape getShape() {
        return shape;
    }

    public void setShape(PipeShape shape) {
        this.shape = shape;
    }

    public void setFilled() {
        this.filled = true;
    }

    public boolean getFilled() {
        return filled;
    }

    public void setFilledState(boolean filled) {
        this.filled = filled;
    }

    public Direction[] getConnections() {
        switch (shape) {
            case HORIZONTAL: return new Direction[]{Direction.LEFT, Direction.RIGHT};
            case VERTICAL: return new Direction[]{Direction.UP, Direction.DOWN};
            case TOP_LEFT: return new Direction[]{Direction.UP, Direction.LEFT};
            case TOP_RIGHT: return new Direction[]{Direction.UP, Direction.RIGHT};
            case BOTTOM_LEFT: return new Direction[]{Direction.DOWN, Direction.LEFT};
            case BOTTOM_RIGHT: return new Direction[]{Direction.DOWN, Direction.RIGHT};
            case CROSS: return new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
            default: throw new IllegalStateException("Unknown shape");
        }
    }

    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        return new Pipe(PipeShape.fromCode(rep));
    }
}

/**
 * Abstract Cell.
 */
abstract class Cell implements MapElement {
    public Coordinate coord;

    public Cell() {
        this.coord = new Coordinate(0, 0);
    }

    public Cell(Coordinate coord) {
        this.coord = coord;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    public static Cell fromChar(final char c, final Coordinate coord, final TerminationType terminationType) {
        switch (c) {
            case 'W':
                return createWall(coord);
            case '.':
                return createFillableCell(coord, null);
            case '^':
                return terminationType == null ? null : createTerminationCell(coord, terminationType, Direction.UP);
            case '>':
                return terminationType == null ? null : createTerminationCell(coord, terminationType, Direction.RIGHT);
            case '<':
                return terminationType == null ? null : createTerminationCell(coord, terminationType, Direction.LEFT);
            case 'v':
                return terminationType == null ? null : createTerminationCell(coord, terminationType, Direction.DOWN);
            default:
                return null;
        }
    }

    public static Wall createWall(Coordinate coord) {
        return new Wall(coord);
    }

    public static FillableCell createFillableCell(Coordinate coord, Pipe pipe) {
        return new FillableCell(coord, pipe);
    }

    public static TerminationCell createTerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        return new TerminationCell(coord, type, pointingTo);
    }
}

/**
 * FillableCell: holds an optional pipe.
 */
class FillableCell extends Cell {
    private Pipe pipe;

    public FillableCell() {
        super();
        this.pipe = null;
    }

    public FillableCell(Coordinate coord, Pipe pipe) {
        super(coord);
        this.pipe = pipe;
    }

    public Pipe getPipeField() {
        return pipe;
    }

    public void setPipeField(Pipe pipe) {
        this.pipe = pipe;
    }

    public Optional<Pipe> getPipe() {
        return Optional.ofNullable(pipe);
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }

    @Override
    public char toSingleChar() {
        if (pipe != null) {
            return pipe.toSingleChar();
        }
        return '.';
    }
}

/**
 * TerminationCell: source or sink.
 */
class TerminationCell extends Cell {
    private boolean isFilled;
    public Direction pointingTo;
    public TerminationType type;

    public TerminationCell() {
        super();
        this.isFilled = false;
        this.pointingTo = Direction.UP;
        this.type = TerminationType.SOURCE;
    }

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        super(coord);
        this.isFilled = false;
        this.type = type;
        this.pointingTo = pointingTo;
    }

    public boolean getIsFilled() {
        return isFilled;
    }

    public void setIsFilled(boolean isFilled) {
        this.isFilled = isFilled;
    }

    public Direction getPointingTo() {
        return pointingTo;
    }

    public void setPointingTo(Direction pointingTo) {
        this.pointingTo = pointingTo;
    }

    public TerminationType getType() {
        return type;
    }

    public void setType(TerminationType type) {
        this.type = type;
    }

    public void setFilled() {
        this.isFilled = true;
    }

    @Override
    public char toSingleChar() {
        if (isFilled) {
            switch (pointingTo) {
                case UP: return PipePatterns.Filled.UP_ARROW;
                case DOWN: return PipePatterns.Filled.DOWN_ARROW;
                case LEFT: return PipePatterns.Filled.LEFT_ARROW;
                case RIGHT: return PipePatterns.Filled.RIGHT_ARROW;
                default: throw new IllegalStateException("Invalid pointingTo value!");
            }
        } else {
            switch (pointingTo) {
                case UP: return PipePatterns.Unfilled.UP_ARROW;
                case DOWN: return PipePatterns.Unfilled.DOWN_ARROW;
                case LEFT: return PipePatterns.Unfilled.LEFT_ARROW;
                case RIGHT: return PipePatterns.Unfilled.RIGHT_ARROW;
                default: throw new IllegalStateException("Invalid pointingTo value!");
            }
        }
    }
}

/**
 * TerminationCellCreateInfo: helper to construct TerminationCell info.
 */
class TerminationCellCreateInfo {
    public Coordinate coord;
    public Direction dir;

    public TerminationCellCreateInfo() {
        this.coord = new Coordinate();
        this.dir = Direction.UP;
    }

    public TerminationCellCreateInfo(Coordinate coord, Direction dir) {
        this.coord = coord;
        this.dir = dir;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    public Direction getDir() {
        return dir;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }
}

/**
 * Wall cell.
 */
class Wall extends Cell {
    public Wall() {
        super();
    }

    public Wall(Coordinate coord) {
        super(coord);
    }

    @Override
    public char toSingleChar() {
        return PipePatterns.WALL;
    }
}

/**
 * Map: the game map.
 */
class Map {
    private int rows;
    private int cols;
    public Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;
    private Set<Coordinate> filledTiles;
    private int prevFilledTiles;
    private Integer prevFilledDistance;

    public Map() {
        this.rows = 0;
        this.cols = 0;
        this.cells = new Cell[0][0];
        this.sourceCell = null;
        this.sinkCell = null;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = null;
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = null;
        identifyTerminations();
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public TerminationCell getSourceCell() {
        return sourceCell;
    }

    public void setSourceCell(TerminationCell sourceCell) {
        this.sourceCell = sourceCell;
    }

    public TerminationCell getSinkCell() {
        return sinkCell;
    }

    public void setSinkCell(TerminationCell sinkCell) {
        this.sinkCell = sinkCell;
    }

    public Set<Coordinate> getFilledTiles() {
        return filledTiles;
    }

    public void setFilledTiles(Set<Coordinate> filledTiles) {
        this.filledTiles = filledTiles;
    }

    public int getPrevFilledTiles() {
        return prevFilledTiles;
    }

    public void setPrevFilledTiles(int prevFilledTiles) {
        this.prevFilledTiles = prevFilledTiles;
    }

    public Integer getPrevFilledDistance() {
        return prevFilledDistance;
    }

    public void setPrevFilledDistance(Integer prevFilledDistance) {
        this.prevFilledDistance = prevFilledDistance;
    }

    private void identifyTerminations() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cells[r][c];
                    if (tc.type == TerminationType.SOURCE) {
                        sourceCell = tc;
                    } else if (tc.type == TerminationType.SINK) {
                        sinkCell = tc;
                    }
                }
            }
        }
    }

    static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe p) {
        if (row < 1 || row > rows - 2 || col < 1 || col > cols - 2) {
            return false;
        }
        if (!(cells[row][col] instanceof FillableCell)) {
            return false;
        }
        FillableCell fc = (FillableCell) cells[row][col];
        if (fc.getPipe().isPresent()) {
            return false;
        }
        fc.setPipe(p);
        return true;
    }

    public void undo(Coordinate coord) {
        if (cells[coord.row][coord.col] instanceof FillableCell) {
            FillableCell fc = (FillableCell) cells[coord.row][coord.col];
            fc.setPipe(null);
        }
        filledTiles.remove(coord);
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        if (distance <= 0) return;
        Set<Coordinate> newFilled = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        Coordinate sourceCoord = sourceCell.coord;
        queue.add(sourceCoord);
        newFilled.add(sourceCoord);

        int currentDistance = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            if (currentDistance >= distance) break;
            for (int i = 0; i < size; i++) {
                Coordinate current = queue.poll();
                if (cells[current.row][current.col] instanceof FillableCell) {
                    FillableCell fc = (FillableCell) cells[current.row][current.col];
                    Optional<Pipe> pipeOpt = fc.getPipe();
                    if (!pipeOpt.isPresent()) continue;
                    Pipe pipe = pipeOpt.get();
                    for (Direction dir : pipe.getConnections()) {
                        Coordinate next = current.add(dir.getOffset());
                        if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) continue;
                        if (newFilled.contains(next)) continue;
                        Cell neighbor = cells[next.row][next.col];
                        if (neighbor instanceof FillableCell) {
                            FillableCell nfc = (FillableCell) neighbor;
                            Optional<Pipe> npOpt = nfc.getPipe();
                            if (!npOpt.isPresent()) continue;
                            Pipe np = npOpt.get();
                            boolean hasMatch = false;
                            for (Direction nd : np.getConnections()) {
                                if (nd == dir.getOpposite()) {
                                    hasMatch = true;
                                    break;
                                }
                            }
                            if (hasMatch) {
                                newFilled.add(next);
                                queue.add(next);
                            }
                        } else if (neighbor instanceof TerminationCell) {
                            TerminationCell ntc = (TerminationCell) neighbor;
                            if (ntc.type == TerminationType.SINK) {
                                if (ntc.pointingTo == dir.getOpposite()) {
                                    ntc.setFilled();
                                    newFilled.add(next);
                                }
                            }
                        }
                    }
                } else if (current.equals(sourceCoord)) {
                    Direction dir = sourceCell.pointingTo;
                    Coordinate next = current.add(dir.getOffset());
                    if (next.row >= 0 && next.row < rows && next.col >= 0 && next.col < cols) {
                        Cell neighbor = cells[next.row][next.col];
                        if (neighbor instanceof FillableCell) {
                            FillableCell nfc = (FillableCell) neighbor;
                            Optional<Pipe> npOpt = nfc.getPipe();
                            if (npOpt.isPresent()) {
                                Pipe np = npOpt.get();
                                for (Direction nd : np.getConnections()) {
                                    if (nd == dir.getOpposite()) {
                                        newFilled.add(next);
                                        queue.add(next);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            currentDistance++;
        }

        for (Coordinate c : newFilled) {
            if (cells[c.row][c.col] instanceof FillableCell) {
                FillableCell fc = (FillableCell) cells[c.row][c.col];
                if (fc.getPipe().isPresent()) {
                    fc.getPipe().get().setFilled();
                }
            } else if (cells[c.row][c.col] instanceof TerminationCell) {
                ((TerminationCell) cells[c.row][c.col]).setFilled();
            }
        }
        prevFilledTiles = newFilled.size() - filledTiles.size();
        filledTiles = newFilled;
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) return false;
        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        Coordinate start = sourceCell.coord;
        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            if (current.equals(sinkCell.coord)) {
                return true;
            }
            Cell currentCell = cells[current.row][current.col];
            if (currentCell instanceof FillableCell) {
                FillableCell fc = (FillableCell) currentCell;
                Optional<Pipe> pipeOpt = fc.getPipe();
                if (!pipeOpt.isPresent()) continue;
                Pipe pipe = pipeOpt.get();
                for (Direction dir : pipe.getConnections()) {
                    Coordinate next = current.add(dir.getOffset());
                    if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) continue;
                    if (visited.contains(next)) continue;
                    Cell neighbor = cells[next.row][next.col];
                    if (neighbor instanceof FillableCell) {
                        FillableCell nfc = (FillableCell) neighbor;
                        if (nfc.getPipe().isPresent()) {
                            visited.add(next);
                            queue.add(next);
                        }
                    } else if (neighbor instanceof TerminationCell) {
                        visited.add(next);
                        queue.add(next);
                    }
                }
            } else if (currentCell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) currentCell;
                if (tc.type == TerminationType.SOURCE) {
                    Coordinate next = current.add(tc.pointingTo.getOffset());
                    if (next.row >= 0 && next.row < rows && next.col >= 0 && next.col < cols) {
                        if (!visited.contains(next)) {
                            visited.add(next);
                            queue.add(next);
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean hasLost() {
        return prevFilledTiles == 0;
    }
}

/**
 * CellStack: stack of placed fillable cells.
 */
class CellStack {
    private Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
        this.cellStack = new Stack<>();
        this.count = 0;
    }

    public Stack<FillableCell> getCellStackField() {
        return cellStack;
    }

    public void setCellStackField(Stack<FillableCell> cellStack) {
        this.cellStack = cellStack;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void push(FillableCell cell) {
        cellStack.push(cell);
    }

    public FillableCell pop() {
        FillableCell cell = cellStack.pop();
        count++;
        return cell;
    }

    public int getUndoCount() {
        return count;
    }
}

/**
 * PipeQueue: queue of pipes with auto-refill.
 */
class PipeQueue {
    private static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        if (pipes != null) {
            this.pipeQueue.addAll(pipes);
        }
        while (this.pipeQueue.size() < MAX_GEN_LENGTH) {
            this.pipeQueue.add(generateNewPipe());
        }
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }

    public static int getMaxGenLength() {
        return MAX_GEN_LENGTH;
    }

    public Pipe peek() {
        if (pipeQueue.isEmpty()) {
            pipeQueue.add(generateNewPipe());
        }
        return pipeQueue.peek();
    }

    public void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.poll();
        }
        if (pipeQueue.isEmpty()) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    private static Pipe generateNewPipe() {
        PipeShape[] shapes = PipeShape.values();
        PipeShape shape = shapes[(int) (Math.random() * shapes.length)];
        return new Pipe(shape);
    }
}

/**
 * Game: top-level game controller.
 */
class Game {
    private int numOfSteps;
    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;

    public Game() {
        this.numOfSteps = 0;
        this.map = new Map();
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar();
    }

    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.numOfSteps = 0;
        this.map = new Map(rows, cols, cells);
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
    }

    public int getNumOfSteps() {
        return numOfSteps;
    }

    public void setNumOfSteps(int numOfSteps) {
        this.numOfSteps = numOfSteps;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public CellStack getCellStack() {
        return cellStack;
    }

    public void setCellStack(CellStack cellStack) {
        this.cellStack = cellStack;
    }

    public PipeQueue getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(PipeQueue pipeQueue) {
        this.pipeQueue = pipeQueue;
    }

    public DelayBar getDelayBar() {
        return delayBar;
    }

    public void setDelayBar(DelayBar delayBar) {
        this.delayBar = delayBar;
    }

    static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    public boolean placePipe(int row, char col) {
        int colIndex = col - 'A' + 1;
        Pipe pipe = pipeQueue.peek();
        if (map.tryPlacePipe(row, colIndex, pipe)) {
            FillableCell fc = (FillableCell) map.cells[row][colIndex];
            cellStack.push(fc);
            pipeQueue.consume();
            numOfSteps++;
            return true;
        }
        return false;
    }

    public void skipPipe() {
        pipeQueue.consume();
        numOfSteps++;
    }

    public boolean undoStep() {
        if (cellStack.getCellStackField().isEmpty()) {
            return false;
        }
        FillableCell cell = cellStack.pop();
        Pipe pipe = cell.getPipe().orElse(null);
        if (pipe != null) {
            pipeQueue.undo(pipe);
        }
        map.undo(cell.coord);
        numOfSteps++;
        return true;
    }

    public void updateState() {
        delayBar.countdown();
        int dist = delayBar.distance();
        if (dist > 0) {
            map.fillTiles(dist);
        }
    }

    public boolean hasWon() {
        return map.checkPath();
    }

    public boolean hasLost() {
        int dist = delayBar.distance();
        if (dist > 0) {
            return map.hasLost();
        }
        return false;
    }
}

/**
 * Deserializer: deserialize map input into runtime objects.
 */
class Deserializer {
    private Path path;

    public Deserializer(final String path) throws FileNotFoundException {
        this(Paths.get(path));
    }

    private Deserializer(final Path path) throws FileNotFoundException {
        if (!path.toFile().exists()) {
            throw new FileNotFoundException("Cannot find file to load!");
        }
        this.path = path;
    }

    public Game parseGame() {
        try (var reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;

            var rows = 0;
            if ((line = getFirstNonEmptyLine(reader)) != null) {
                rows = Integer.parseInt(line);
            } else {
                throw new EOFException();
            }

            var cols = 0;
            if ((line = getFirstNonEmptyLine(reader)) != null) {
                cols = Integer.parseInt(line);
            } else {
                throw new EOFException();
            }

            var delay = 0;
            if ((line = getFirstNonEmptyLine(reader)) != null) {
                delay = Integer.parseInt(line);
            } else {
                throw new EOFException();
            }

            final var mapRep = new ArrayList<String>();
            for (int r = 0; r < rows; ++r) {
                line = getFirstNonEmptyLine(reader);
                if (line == null) {
                    throw new EOFException();
                }
                mapRep.add(line);
            }
            final var cells = parseString(rows, cols, String.join("\n", mapRep));

            List<Pipe> defaultPipes = null;
            String s = getFirstNonEmptyLine(reader);
            if (s != null) {
                defaultPipes = Arrays.stream(s.split(","))
                        .map(Pipe::fromString)
                        .collect(Collectors.toList());
            }

            return new Game(rows, cols, delay, cells, defaultPipes);
        } catch (EOFException eofe) {
            System.err.println("Unexpected EOF");
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    public static Cell[][] parseString(final int rows, final int cols, final String cellsRep) {
        var cells = new Cell[rows][cols];
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                var coord = new Coordinate(r, c);
                var ch = cellsRep.lines().skip(r).findFirst().orElseThrow().charAt(c);

                Cell cell;
                if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                    cell = Cell.fromChar(ch, coord, TerminationType.SINK);
                } else {
                    cell = Cell.fromChar(ch, coord, TerminationType.SOURCE);
                }

                cells[r][c] = cell;
            }
        }

        return cells;
    }

    private String getFirstNonEmptyLine(final BufferedReader br) throws IOException {
        do {
            String s = br.readLine();
            if (s == null) {
                return null;
            }
            if (s.isBlank() || s.startsWith("#")) {
                continue;
            }
            return s;
        } while (true);
    }
}