import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// --- Constants and Utilities ---

final class PipePatterns {
    private PipePatterns() {}
    public static final char WALL = '#';
    public static class Filled {
        public static final char UP_ARROW = '^', DOWN_ARROW = 'v', LEFT_ARROW = '<', RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '═', VERTICAL = '║', TOP_LEFT = '╔', TOP_RIGHT = '╗', BOTTOM_LEFT = '╚', BOTTOM_RIGHT = '╝', CROSS = '╬';
    }
    public static class Unfilled {
        public static final char UP_ARROW = 'ʌ', DOWN_ARROW = 'v', LEFT_ARROW = '<', RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '─', VERTICAL = '│', TOP_LEFT = '┌', TOP_RIGHT = '┐', BOTTOM_LEFT = '└', BOTTOM_RIGHT = '┘', CROSS = '┼';
    }
}

class StringUtils {
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
            case UP -> new Coordinate(-1, 0);
            case DOWN -> new Coordinate(1, 0);
            case LEFT -> new Coordinate(0, -1);
            case RIGHT -> new Coordinate(0, 1);
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
    PipeShape(char f, char u) { this.filledChar = f; this.unfilledChar = u; }
    public char getCharByState(boolean isFilled) { return isFilled ? filledChar : unfilledChar; }
}

// --- Supporting Classes ---

class Coordinate {
    public int row, col;
    public Coordinate(int row, int col) { this.row = row; this.col = col; }
    public Coordinate add(Coordinate other) { return new Coordinate(this.row + other.row, this.col + other.col); }
    @Override public boolean equals(Object obj) { return obj instanceof Coordinate o && row == o.row && col == o.col; }
    @Override public int hashCode() { return Objects.hash(row, col); }
}

class DelayBar {
    private int initialValue;
    private int currentValue;
    public DelayBar() {}
    public void countdown() { if (currentValue > 0) currentValue--; }
    public int distance() { return -currentValue; }
    public int getInitialValue() { return initialValue; }
    public void setInitialValue(int v) { this.initialValue = v; }
    public int getCurrentValue() { return currentValue; }
    public void setCurrentValue(int v) { this.currentValue = v; }
}

interface MapElement { char toSingleChar(); }

abstract class Cell implements MapElement {
    public Coordinate coord;
    public Cell() {}
    public Coordinate getCoord() { return coord; }
    public void setCoord(Coordinate c) { this.coord = c; }
    public static Cell fromChar(final char c, final Coordinate coord, final TerminationType terminationType) {
        switch (c) {
            case 'W': return new Wall(coord);
            case '.': return new FillableCell(coord);
            case '^': return new TerminationCell(coord, terminationType, Direction.UP);
            case '>': return new TerminationCell(coord, terminationType, Direction.RIGHT);
            case '<': return new TerminationCell(coord, terminationType, Direction.LEFT);
            case 'v': return new TerminationCell(coord, terminationType, Direction.DOWN);
            default: return null;
        }
    }
}

class FillableCell extends Cell {
    private Pipe pipe;
    public FillableCell() {}
    public FillableCell(Coordinate coord) { this.coord = coord; }
    public Optional<Pipe> getPipe() { return Optional.ofNullable(pipe); }
    public void setPipe(Pipe pipe) { this.pipe = pipe; }
    public char toSingleChar() { return getPipe().map(p -> p.toSingleChar()).orElse('.'); }
}

class TerminationCell extends Cell {
    private boolean isFilled;
    public Direction pointingTo;
    public TerminationType type;
    public TerminationCell() {}
    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        this.coord = coord; this.type = type; this.pointingTo = pointingTo;
    }
    public boolean getIsFilled() { return isFilled; }
    public void setFilled() { this.isFilled = true; }
    public Direction getPointingTo() { return pointingTo; }
    public void setPointingTo(Direction d) { this.pointingTo = d; }
    public TerminationType getType() { return type; }
    public void setType(TerminationType t) { this.type = t; }
    public char toSingleChar() {
        char[][] map = isFilled ? new char[][]{{PipePatterns.Filled.UP_ARROW, PipePatterns.Filled.DOWN_ARROW, PipePatterns.Filled.LEFT_ARROW, PipePatterns.Filled.RIGHT_ARROW}}
                                : new char[][]{{PipePatterns.Unfilled.UP_ARROW, PipePatterns.Unfilled.DOWN_ARROW, PipePatterns.Unfilled.LEFT_ARROW, PipePatterns.Unfilled.RIGHT_ARROW}};
        int idx = switch(pointingTo) { case UP -> 0; case DOWN -> 1; case LEFT -> 2; case RIGHT -> 3; };
        return map[0][idx];
    }
}

class Wall extends Cell {
    public Wall() {}
    public Wall(Coordinate c) { this.coord = c; }
    public char toSingleChar() { return PipePatterns.WALL; }
}

class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;
    public Pipe() {}
    public PipeShape getShape() { return shape; }
    public void setShape(PipeShape s) { this.shape = s; }
    public boolean getFilled() { return filled; }
    public void setFilled(boolean f) { this.filled = f; }
    public char toSingleChar() { return shape.getCharByState(filled); }
    public Direction[] getConnections() { throw new UnsupportedOperationException(); }
    public static Pipe fromString(String rep) { return new Pipe(); }
}

class CellStack {
    private Stack<FillableCell> cellStack = new Stack<>();
    private int count = 0;
    public void push(FillableCell c) { cellStack.push(c); count++; }
    public FillableCell pop() { return cellStack.pop(); }
    public int getUndoCount() { return count; }
}

class PipeQueue {
    private static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue = new LinkedList<>();
    public PipeQueue(List<Pipe> pipes) { pipeQueue.addAll(pipes); }
    public Pipe peek() { return pipeQueue.peek(); }
    public void consume() { pipeQueue.poll(); }
    public void undo(Pipe p) { pipeQueue.addFirst(p); }
}

class Map {
    private int rows, cols;
    public Cell[][] cells;
    private TerminationCell sourceCell, sinkCell;
    private Set<Coordinate> filledTiles = new HashSet<>();
    public Map() {}
    public Map(int r, int c, Cell[][] cells) { this.rows = r; this.cols = c; this.cells = cells; }
    public static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }
    public boolean tryPlacePipe(Coordinate coord, Pipe pipe) { return tryPlacePipe(coord.row, coord.col, pipe); }
    public boolean tryPlacePipe(int r, int c, Pipe p) { return false; }
    public void fillBeginTile() { sourceCell.setFilled(); }
    public void fillTiles(int d) {}
    public boolean checkPath() { return false; }
    public boolean hasLost() { return false; }
    public void undo(Coordinate c) {}
}

class Game {
    private int numOfSteps;
    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;
    public Game() {}
    public Game(int r, int c, int d, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(r, c, cells);
        this.pipeQueue = new PipeQueue(pipes);
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
}

// --- Deserializer ---

class Deserializer {
    private Path path;
    public Deserializer(final String path) throws FileNotFoundException { this(Paths.get(path)); }
    private Deserializer(final Path path) throws FileNotFoundException {
        if (!path.toFile().exists()) throw new FileNotFoundException();
        this.path = path;
    }
    public Game parseGame() { return new Game(); }
    public static Cell[][] parseString(final int rows, final int cols, final String cellsRep) {
        Cell[][] cells = new Cell[rows][cols];
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                cells[r][c] = Cell.fromChar(cellsRep.charAt(r * (cols + 1) + c), new Coordinate(r, c), TerminationType.SOURCE);
            }
        }
        return cells;
    }
}