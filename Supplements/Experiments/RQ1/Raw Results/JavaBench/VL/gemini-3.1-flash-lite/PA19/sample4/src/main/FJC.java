import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// --- Utility Classes ---

final class PipePatterns {
    private PipePatterns() {}
    public static final char WALL = '#';
    public static final class Filled {
        public static final char UP_ARROW = '^', DOWN_ARROW = 'v', LEFT_ARROW = '<', RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '═', VERTICAL = '║', TOP_LEFT = '╔', TOP_RIGHT = '╗', BOTTOM_LEFT = '╚', BOTTOM_RIGHT = '╝', CROSS = '╬';
    }
    public static final class Unfilled {
        public static final char UP_ARROW = '^', DOWN_ARROW = 'v', LEFT_ARROW = '<', RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '-', VERTICAL = '|', TOP_LEFT = '┌', TOP_RIGHT = '┐', BOTTOM_LEFT = '└', BOTTOM_RIGHT = '┘', CROSS = '+';
    }
}

final class StringUtils {
    private StringUtils() {}
    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(Math.max(0, count));
    }
}

// --- Enums ---

enum Direction {
    UP, DOWN, LEFT, RIGHT;
    public Direction getOpposite() {
        return switch (this) {
            case UP -> DOWN; case DOWN -> UP; case LEFT -> RIGHT; case RIGHT -> LEFT;
        };
    }
    public Coordinate getOffset() {
        return switch (this) {
            case UP -> new Coordinate(-1, 0); case DOWN -> new Coordinate(1, 0);
            case LEFT -> new Coordinate(0, -1); case RIGHT -> new Coordinate(0, 1);
        };
    }
}

enum TerminationType { SOURCE, SINK }

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
    PipeShape(char filled, char unfilled) { this.filledChar = filled; this.unfilledChar = unfilled; }
    public char getCharByState(boolean isFilled) { return isFilled ? filledChar : unfilledChar; }
}

// --- Interfaces & Classes ---

interface MapElement { char toSingleChar(); }

class Coordinate {
    public int row, col;
    public Coordinate(int row, int col) { this.row = row; this.col = col; }
    @Override public boolean equals(Object obj) { return obj instanceof Coordinate c && row == c.row && col == c.col; }
    @Override public int hashCode() { return Objects.hash(row, col); }
    public Coordinate add(Coordinate other) { return new Coordinate(this.row + other.row, this.col + other.col); }
}

abstract class Cell implements MapElement {
    public Coordinate coord;
    public Cell() {}
    public Coordinate getCoord() { return coord; }
    public void setCoord(Coordinate coord) { this.coord = coord; }
    public static Cell fromChar(final char c, final Coordinate coord, final TerminationType terminationType) {
        return switch (c) {
            case 'W' -> new Wall(coord);
            case '.' -> new FillableCell(coord);
            case '^' -> new TerminationCell(coord, terminationType, Direction.UP);
            case '>' -> new TerminationCell(coord, terminationType, Direction.RIGHT);
            case '<' -> new TerminationCell(coord, terminationType, Direction.LEFT);
            case 'v' -> new TerminationCell(coord, terminationType, Direction.DOWN);
            default -> null;
        };
    }
    private static Wall createWall(Coordinate c) { return new Wall(c); }
    private static FillableCell createFillableCell(Coordinate c, Pipe p) { FillableCell f = new FillableCell(c); f.setPipe(p); return f; }
    private static TerminationCell createTerminationCell(Coordinate c, TerminationType t, Direction d) { return new TerminationCell(c, t, d); }
}

class FillableCell extends Cell {
    private Pipe pipe;
    public FillableCell() {}
    public FillableCell(Coordinate coord) { this.coord = coord; }
    public Optional<Pipe> getPipe() { return Optional.ofNullable(pipe); }
    public void setPipe(Pipe pipe) { this.pipe = pipe; }
    public char toSingleChar() { return pipe == null ? '.' : pipe.toSingleChar(); }
}

class TerminationCell extends Cell {
    private boolean isFilled;
    public Direction pointingTo;
    public TerminationType type;
    public TerminationCell() {}
    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        this.coord = coord; this.type = type; this.pointingTo = pointingTo;
    }
    public void setFilled() { this.isFilled = true; }
    public Direction getPointingTo() { return pointingTo; }
    public TerminationType getType() { return type; }
    public char toSingleChar() {
        char[][] map = isFilled ? new char[][]{{PipePatterns.Filled.UP_ARROW, PipePatterns.Filled.DOWN_ARROW, PipePatterns.Filled.LEFT_ARROW, PipePatterns.Filled.RIGHT_ARROW}}
                                : new char[][]{{PipePatterns.Unfilled.UP_ARROW, PipePatterns.Unfilled.DOWN_ARROW, PipePatterns.Unfilled.LEFT_ARROW, PipePatterns.Unfilled.RIGHT_ARROW}};
        int idx = switch(pointingTo) { case UP -> 0; case DOWN -> 1; case LEFT -> 2; case RIGHT -> 3; };
        return map[0][idx];
    }
}

class Wall extends Cell {
    public Wall() {}
    public Wall(Coordinate coord) { this.coord = coord; }
    public char toSingleChar() { return PipePatterns.WALL; }
}

class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;
    public Pipe() {}
    public Pipe(PipeShape shape) { this.shape = shape; }
    public void setFilled() { this.filled = true; }
    public boolean getFilled() { return filled; }
    public PipeShape getShape() { return shape; }
    public void setShape(PipeShape shape) { this.shape = shape; }
    public Direction[] getConnections() { throw new UnsupportedOperationException(); }
    public char toSingleChar() { return shape.getCharByState(filled); }
    public static Pipe fromString(String rep) { return new Pipe(PipeShape.valueOf(rep)); }
}

class Map {
    private int rows, cols;
    public Cell[][] cells;
    private TerminationCell sourceCell, sinkCell;
    private Set<Coordinate> filledTiles;
    private int prevFilledTiles, prevFilledDistance;
    public Map(int rows, int cols, Cell[][] cells) { this.rows = rows; this.cols = cols; this.cells = cells; }
    public static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }
    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) { return tryPlacePipe(coord.row, coord.col, pipe); }
    public boolean tryPlacePipe(int row, int col, Pipe p) { return false; }
    public void undo(Coordinate c) {}
    public void fillBeginTile() { sourceCell.setFilled(); }
    public void fillTiles(int distance) {}
    public boolean checkPath() { return false; }
    public boolean hasLost() { return false; }
    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }
    public int getCols() { return cols; }
    public void setCols(int cols) { this.cols = cols; }
}

class CellStack {
    private Stack<FillableCell> cellStack = new Stack<>();
    private int count;
    public void push(FillableCell cell) { cellStack.push(cell); }
    public FillableCell pop() { count++; return cellStack.pop(); }
    public int getUndoCount() { return count; }
}

class PipeQueue {
    private static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue = new LinkedList<>();
    public PipeQueue(List<Pipe> pipes) { pipeQueue.addAll(pipes); }
    public Pipe peek() { return pipeQueue.peek(); }
    public void consume() { pipeQueue.removeFirst(); }
    public void undo(Pipe p) { pipeQueue.addFirst(p); }
    private static Pipe generateNewPipe() { return new Pipe(PipeShape.HORIZONTAL); }
}

class DelayBar {
    private int initialValue, currentValue;
    public void countdown() { currentValue--; }
    public int distance() { return -currentValue; }
    public int getInitialValue() { return initialValue; }
    public void setInitialValue(int val) { this.initialValue = val; }
    public int getCurrentValue() { return currentValue; }
    public void setCurrentValue(int val) { this.currentValue = val; }
}

class Game {
    private int numOfSteps;
    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;
    public Game() {}
    public Game(int r, int c, int d, Cell[][] cells, List<Pipe> p) {
        this.map = new Map(r, c, cells);
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue(p);
        this.delayBar = new DelayBar();
    }
    public static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }
    public boolean placePipe(int row, char col) { return false; }
    public void skipPipe() { numOfSteps++; }
    public boolean undoStep() { return false; }
    public void updateState() {}
    public boolean hasWon() { return false; }
    public boolean hasLost() { return false; }
    public int getNumOfSteps() { return numOfSteps; }
    public void setNumOfSteps(int s) { this.numOfSteps = s; }
    public Map getMap() { return map; }
    public void setMap(Map m) { this.map = m; }
}

class Deserializer {
    private Path path;
    public Deserializer(final String path) throws FileNotFoundException { this(Paths.get(path)); }
    private Deserializer(final Path path) throws FileNotFoundException {
        if (!path.toFile().exists()) throw new FileNotFoundException("Cannot find file to load!");
        this.path = path;
    }
    public Game parseGame() { return null; }
    public static Cell[][] parseString(final int rows, final int cols, final String cellsRep) {
        var cells = new Cell[rows][cols];
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                var coord = new Coordinate(r, c);
                var ch = cellsRep.lines().skip(r).findFirst().orElseThrow().charAt(c);
                cells[r][c] = Cell.fromChar(ch, coord, (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) ? TerminationType.SINK : TerminationType.SOURCE);
            }
        }
        return cells;
    }
}