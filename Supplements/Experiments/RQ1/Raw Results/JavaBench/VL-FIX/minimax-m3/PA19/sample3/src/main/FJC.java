import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Central constant repository for all map-rendering characters.
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
        public static final char TOP_LEFT = '┐';
        public static final char TOP_RIGHT = '┌';
        public static final char BOTTOM_LEFT = '┘';
        public static final char BOTTOM_RIGHT = '└';
        public static final char CROSS = '┼';
    }
}

/**
 * Lightweight string helper utilities.
 */
final class StringUtils {
    private StringUtils() {}

    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }
}

/**
 * Representation of a coordinate in {Map}.
 */
 class Coordinate {

    public int row;
    public int col;

    public Coordinate() {
        this.row = 0;
        this.col = 0;
    }

    public Coordinate(int row, int col) {
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
 * Direction enum with four directions plus opposite/offset.
 */
enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public Direction getOpposite() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: throw new IllegalStateException("Invalid direction!");
        }
    }

    public Coordinate getOffset() {
        switch (this) {
            case UP: return new Coordinate(-1, 0);
            case DOWN: return new Coordinate(1, 0);
            case LEFT: return new Coordinate(0, -1);
            case RIGHT: return new Coordinate(0, 1);
            default: throw new IllegalStateException("Invalid direction!");
        }
    }
}

/**
 * Distinguishes SOURCE vs SINK termination cells.
 */
enum TerminationType {
    SOURCE, SINK
}

/**
 * Pipe shape with filled and unfilled character representations.
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
}

/**
 * Map element interface.
 */
interface MapElement {
    char toSingleChar();
}

/**
 * Delay bar to manage water flow delay.
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
 * Pipe with shape, filled state, and connections.
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

    public Pipe(PipeShape shape, boolean filled) {
        this.shape = shape;
        this.filled = filled;
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

    public void setFilled(boolean filled) {
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

    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        switch (rep.trim().toUpperCase()) {
            case "HZ": return new Pipe(PipeShape.HORIZONTAL);
            case "VT": return new Pipe(PipeShape.VERTICAL);
            case "TL": return new Pipe(PipeShape.TOP_LEFT);
            case "TR": return new Pipe(PipeShape.TOP_RIGHT);
            case "BL": return new Pipe(PipeShape.BOTTOM_LEFT);
            case "BR": return new Pipe(PipeShape.BOTTOM_RIGHT);
            case "CR": return new Pipe(PipeShape.CROSS);
            default: return new Pipe(PipeShape.HORIZONTAL);
        }
    }
}

/**
 * Abstract cell.
 */
abstract class Cell implements MapElement {
    public Coordinate coord;

    public Cell() {
        this.coord = new Coordinate();
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
 * Fillable cell holding an optional pipe.
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

    public Optional<Pipe> getPipe() {
        return Optional.ofNullable(pipe);
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }

    public char toSingleChar() {
        if (pipe != null) {
            return pipe.toSingleChar();
        }
        return '.';
    }
}

/**
 * Termination cell at source or sink.
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

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled() {
        this.isFilled = true;
    }

    public void setFilled(boolean filled) {
        this.isFilled = filled;
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
 * Info for creating a termination cell.
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

    public char toSingleChar() {
        return PipePatterns.WALL;
    }
}

/**
 * Stack of placed fillable cells for undo operations.
 */
class CellStack {
    private Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
        this.cellStack = new Stack<>();
        this.count = 0;
    }

    public Stack<FillableCell> getCellStack() {
        return cellStack;
    }

    public void setCellStack(Stack<FillableCell> cellStack) {
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
        if (cellStack.isEmpty()) {
            return null;
        }
        FillableCell cell = cellStack.pop();
        count++;
        return cell;
    }

    public int getUndoCount() {
        return count;
    }
}

/**
 * Queue of pipes available to place.
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
            for (Pipe p : pipes) {
                pipeQueue.add(p);
            }
        }
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }

    public Pipe peek() {
        if (pipeQueue.isEmpty()) {
            return null;
        }
        return pipeQueue.peek();
    }

    public void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.poll();
        }
        pipeQueue.add(generateNewPipe());
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
 * Map containing cells, source, sink, and pipe placement logic.
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

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    if (tc.getType() == TerminationType.SOURCE) {
                        this.sourceCell = tc;
                    } else if (tc.getType() == TerminationType.SINK) {
                        this.sinkCell = tc;
                    }
                }
            }
        }
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

    static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe pipe) {
        if (row < 1 || row > rows - 2 || col < 1 || col > cols - 2) {
            return false;
        }
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        Cell cell = cells[row][col];
        if (!(cell instanceof FillableCell)) {
            return false;
        }
        FillableCell fc = (FillableCell) cell;
        if (fc.getPipe().isPresent()) {
            return false;
        }
        fc.setPipe(pipe);
        return true;
    }

    public void undo(Coordinate coord) {
        if (coord.row < 0 || coord.row >= rows || coord.col < 0 || coord.col >= cols) {
            return;
        }
        Cell cell = cells[coord.row][coord.col];
        if (cell instanceof FillableCell) {
            FillableCell fc = (FillableCell) cell;
            fc.setPipe(null);
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
        if (sourceCell.getCoord() != null) {
            filledTiles.add(sourceCell.getCoord());
        }
    }

    public void fillTiles(int distance) {
        if (distance <= 0) {
            return;
        }

        Set<Coordinate> newFilled = new HashSet<>(filledTiles);
        Queue<Coordinate> queue = new LinkedList<>();
        java.util.Map<Coordinate, Integer> distMap = new HashMap<>();

        for (Coordinate c : filledTiles) {
            queue.add(c);
            distMap.put(c, 0);
        }

        int newlyFilled = 0;

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            int curDist = distMap.get(current);

            if (curDist >= distance) {
                continue;
            }

            Cell cell = cells[current.row][current.col];
            Direction[] connections = null;

            if (cell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) cell;
                connections = new Direction[]{tc.getPointingTo()};
            } else if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                if (fc.getPipe().isPresent()) {
                    connections = fc.getPipe().get().getConnections();
                }
            }

            if (connections == null) {
                continue;
            }

            for (Direction d : connections) {
                Coordinate next = current.add(d.getOffset());
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                    continue;
                }

                Cell neighbor = cells[next.row][next.col];
                Direction needed = d.getOpposite();
                boolean canFlow = false;

                if (neighbor instanceof FillableCell) {
                    FillableCell nfc = (FillableCell) neighbor;
                    if (nfc.getPipe().isPresent()) {
                        for (Direction nd : nfc.getPipe().get().getConnections()) {
                            if (nd == needed) {
                                canFlow = true;
                                break;
                            }
                        }
                    }
                } else if (neighbor instanceof TerminationCell) {
                    TerminationCell ntc = (TerminationCell) neighbor;
                    if (ntc.getPointingTo() == needed) {
                        canFlow = true;
                    }
                }

                if (canFlow && !newFilled.contains(next)) {
                    newFilled.add(next);
                    if (neighbor instanceof FillableCell) {
                        FillableCell nfc = (FillableCell) neighbor;
                        if (nfc.getPipe().isPresent()) {
                            nfc.getPipe().get().setFilled();
                        }
                    } else if (neighbor instanceof TerminationCell) {
                        ((TerminationCell) neighbor).setFilled();
                    }
                    distMap.put(next, curDist + 1);
                    queue.add(next);
                    newlyFilled++;
                }
            }
        }

        prevFilledTiles = newlyFilled;
        filledTiles = newFilled;
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }

        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();

        Coordinate start = sourceCell.getCoord().add(sourceCell.getPointingTo().getOffset());
        if (start.row < 0 || start.row >= rows || start.col < 0 || start.col >= cols) {
            return false;
        }

        Cell startCell = cells[start.row][start.col];
        Direction neededFromStart = sourceCell.getPointingTo().getOpposite();
        boolean validStart = false;

        if (startCell instanceof FillableCell) {
            FillableCell fc = (FillableCell) startCell;
            if (fc.getPipe().isPresent()) {
                for (Direction d : fc.getPipe().get().getConnections()) {
                    if (d == neededFromStart) {
                        validStart = true;
                        break;
                    }
                }
            }
        } else if (startCell instanceof TerminationCell) {
            TerminationCell tc = (TerminationCell) startCell;
            if (tc.getPointingTo() == neededFromStart) {
                validStart = true;
            }
        }

        if (!validStart) {
            return false;
        }

        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();

            if (current.equals(sinkCell.getCoord())) {
                return true;
            }

            Cell cell = cells[current.row][current.col];
            Direction[] connections = null;

            if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                if (fc.getPipe().isPresent()) {
                    connections = fc.getPipe().get().getConnections();
                }
            } else if (cell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) cell;
                connections = new Direction[]{tc.getPointingTo()};
            }

            if (connections == null) {
                continue;
            }

            for (Direction d : connections) {
                Coordinate next = current.add(d.getOffset());
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                    continue;
                }
                if (visited.contains(next)) {
                    continue;
                }
                if (next.equals(sinkCell.getCoord())) {
                    return true;
                }

                Cell neighbor = cells[next.row][next.col];
                Direction needed = d.getOpposite();
                boolean canMove = false;

                if (neighbor instanceof FillableCell) {
                    FillableCell nfc = (FillableCell) neighbor;
                    if (nfc.getPipe().isPresent()) {
                        for (Direction nd : nfc.getPipe().get().getConnections()) {
                            if (nd == needed) {
                                canMove = true;
                                break;
                            }
                        }
                    }
                } else if (neighbor instanceof TerminationCell) {
                    TerminationCell ntc = (TerminationCell) neighbor;
                    if (ntc.getPointingTo() == needed) {
                        canMove = true;
                    }
                }

                if (canMove) {
                    visited.add(next);
                    queue.add(next);
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
 * Main game class.
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
        Pipe currentPipe = pipeQueue.peek();
        if (currentPipe == null) {
            return false;
        }

        if (row < 1 || row > map.getRows() - 2 || colIndex < 1 || colIndex > map.getCols() - 2) {
            return false;
        }

        if (map.tryPlacePipe(row, colIndex, currentPipe)) {
            FillableCell fc = (FillableCell) map.getCells()[row][colIndex];
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
        FillableCell cell = cellStack.pop();
        if (cell == null) {
            return false;
        }
        Pipe pipe = cell.getPipe().orElse(null);
        if (pipe != null) {
            pipeQueue.undo(pipe);
        }
        map.undo(cell.getCoord());
        numOfSteps++;
        return true;
    }

    public void updateState() {
        if (delayBar.getCurrentValue() > 0) {
            delayBar.countdown();
        } else {
            int distance = delayBar.distance();
            if (distance == 0) {
                map.fillBeginTile();
            } else if (distance > 0) {
                map.fillTiles(distance);
            }
        }
    }

    public boolean hasWon() {
        return map.checkPath();
    }

    public boolean hasLost() {
        if (delayBar.getCurrentValue() > 0) {
            return false;
        }
        return map.hasLost();
    }
}

/**
 * A deserializer for converting a map file into a {@link Game}.
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

    /**
     * Deserializes a map from a {@link java.lang.String}.
     */
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