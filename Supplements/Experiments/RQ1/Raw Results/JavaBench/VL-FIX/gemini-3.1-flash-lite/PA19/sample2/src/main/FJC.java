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

final class StringUtils {
    private StringUtils() {}
    public static String createPadding(int count, char ch) {
        if (count < 0) throw new IllegalArgumentException();
        return String.valueOf(ch).repeat(count);
    }
}

final class PipePatterns {
    private PipePatterns() {}
    public static final char WALL = '#';
    public static final class Filled {
        public static final char UP_ARROW = '^', DOWN_ARROW = 'v', LEFT_ARROW = '<', RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '═', VERTICAL = '║', TOP_LEFT = '╔', TOP_RIGHT = '╗', BOTTOM_LEFT = '╚', BOTTOM_RIGHT = '╝', CROSS = '╬';
    }
    public static final class Unfilled {
        public static final char UP_ARROW = '^', DOWN_ARROW = 'v', LEFT_ARROW = '<', RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '─', VERTICAL = '│', TOP_LEFT = '┌', TOP_RIGHT = '┐', BOTTOM_LEFT = '└', BOTTOM_RIGHT = '┘', CROSS = '┼';
    }
}

// --- Enums and Interfaces ---

interface MapElement {
    char toSingleChar();
}

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
    PipeShape(char f, char u) { this.filledChar = f; this.unfilledChar = u; }
    public char getCharByState(boolean isFilled) { return isFilled ? filledChar : unfilledChar; }
}

// --- Core Classes ---

class Coordinate {
    public int row, col;
    public Coordinate(int row, int col) { this.row = row; this.col = col; }
    public Coordinate() {}
    @Override public boolean equals(Object obj) { return obj instanceof Coordinate c && row == c.row && col == c.col; }
    @Override public int hashCode() { return Objects.hash(row, col); }
    public Coordinate add(Coordinate other) { return new Coordinate(this.row + other.row, this.col + other.col); }
}

class DelayBar {
    private int initialValue, currentValue;
    public DelayBar() {}
    public void countdown() { if (currentValue > 0) currentValue--; }
    public int distance() { return -currentValue; }
    public int getInitialValue() { return initialValue; }
    public void setInitialValue(int v) { this.initialValue = v; }
    public int getCurrentValue() { return currentValue; }
    public void setCurrentValue(int v) { this.currentValue = v; }
}

abstract class Cell implements MapElement {
    public Coordinate coord;
    public Cell() {}
    public Coordinate getCoord() { return coord; }
    public void setCoord(Coordinate coord) { this.coord = coord; }
    public static Cell fromChar(final char c, final Coordinate coord, final TerminationType terminationType) {
        switch (c) {
            case 'W': return new Wall(coord);
            case '.': return new FillableCell(coord, null);
            case '^': return new TerminationCell(coord, terminationType, Direction.UP);
            case '>': return new TerminationCell(coord, terminationType, Direction.RIGHT);
            case '<': return new TerminationCell(coord, terminationType, Direction.LEFT);
            case 'v': return new TerminationCell(coord, terminationType, Direction.DOWN);
            default: return null;
        }
    }
    protected static Cell createWall(Coordinate c) { return new Wall(c); }
    protected static Cell createFillableCell(Coordinate c, Pipe p) { return new FillableCell(c, p); }
    protected static Cell createTerminationCell(Coordinate c, TerminationType t, Direction d) { return new TerminationCell(c, t, d); }
}

class FillableCell extends Cell {
    private Pipe pipe;
    public FillableCell() {}
    public FillableCell(Coordinate coord, Pipe pipe) { this.coord = coord; this.pipe = pipe; }
    public Optional<Pipe> getPipe() { return Optional.ofNullable(pipe); }
    public void setPipe(Pipe pipe) { this.pipe = pipe; }
    public char toSingleChar() { return pipe == null ? '.' : pipe.toSingleChar(); }
}

class TerminationCell extends Cell {
    private boolean isFilled;
    private Direction pointingTo;
    private TerminationType type;
    public TerminationCell() {}
    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        this.coord = coord; this.type = type; this.pointingTo = pointingTo;
    }
    public void setFilled() { this.isFilled = true; }
    public char toSingleChar() {
        char[][] map = isFilled ? new char[][]{{PipePatterns.Filled.UP_ARROW, PipePatterns.Filled.DOWN_ARROW, PipePatterns.Filled.LEFT_ARROW, PipePatterns.Filled.RIGHT_ARROW}}
                                : new char[][]{{PipePatterns.Unfilled.UP_ARROW, PipePatterns.Unfilled.DOWN_ARROW, PipePatterns.Unfilled.LEFT_ARROW, PipePatterns.Unfilled.RIGHT_ARROW}};
        int idx = switch(pointingTo) { case UP -> 0; case DOWN -> 1; case LEFT -> 2; case RIGHT -> 3; };
        return map[0][idx];
    }
    public Direction getPointingTo() { return pointingTo; }
    public void setPointingTo(Direction d) { this.pointingTo = d; }
    public TerminationType getType() { return type; }
    public void setType(TerminationType t) { this.type = t; }
}

class TerminationCellCreateInfo {
    public Coordinate coord;
    public Direction dir;
    public TerminationCellCreateInfo() {}
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
    public void setFilled(boolean f) { this.filled = f; }
    public boolean getFilled() { return filled; }
    public PipeShape getShape() { return shape; }
    public void setShape(PipeShape s) { this.shape = s; }
    public Direction[] getConnections() { throw new UnsupportedOperationException(); }
    public char toSingleChar() { return shape.getCharByState(filled); }
    public static Pipe fromString(String rep) { return new Pipe(); }
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
    public void undo(Coordinate coord) {}
    public void fillBeginTile() { sourceCell.setFilled(); }
    public void fillTiles(int distance) {}
    public boolean checkPath() { return false; }
    public boolean hasLost() { return prevFilledTiles == 0; }
}

class CellStack {
    private Stack<FillableCell> cellStack = new Stack<>();
    private int count;
    public void push(FillableCell c) { cellStack.push(c); }
    public FillableCell pop() { count++; return cellStack.pop(); }
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

class Game {
    private int numOfSteps;
    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;
    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
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
    public boolean hasLost() { return map.hasLost(); }
    public int getNumOfSteps() { return numOfSteps; }
}

// --- Deserializer (Verbatim) ---

class Deserializer {
    private Path path;
    public Deserializer(final String path) throws FileNotFoundException { this(Paths.get(path)); }
    private Deserializer( final Path path) throws FileNotFoundException {
        if (!path.toFile().exists()) throw new FileNotFoundException("Cannot find file to load!");
        this.path = path;
    }
    public Game parseGame() {
        try (var reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            var rows = 0; if ((line = getFirstNonEmptyLine(reader)) != null) rows = Integer.parseInt(line); else throw new EOFException();
            var cols = 0; if ((line = getFirstNonEmptyLine(reader)) != null) cols = Integer.parseInt(line); else throw new EOFException();
            var delay = 0; if ((line = getFirstNonEmptyLine(reader)) != null) delay = Integer.parseInt(line); else throw new EOFException();
            final var mapRep = new ArrayList<String>();
            for (int r = 0; r < rows; ++r) { line = getFirstNonEmptyLine(reader); if (line == null) throw new EOFException(); mapRep.add(line); }
            final var cells = parseString(rows, cols, String.join("\n", mapRep));
            List<Pipe> defaultPipes = null;
            String s = getFirstNonEmptyLine(reader);
            if (s != null) defaultPipes = Arrays.stream(s.split(",")).map(Pipe::fromString).collect(Collectors.toList());
            return new Game(rows, cols, delay, cells, defaultPipes);
        } catch (Exception e) { return null; }
    }
    public static Cell[][] parseString(final int rows, final int cols, final String cellsRep) {
        var cells = new Cell[rows][cols];
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                var coord = new Coordinate(r, c);
                var ch = cellsRep.lines().skip(r).findFirst().orElseThrow().charAt(c);
                if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) cells[r][c] = Cell.fromChar(ch, coord, TerminationType.SINK);
                else cells[r][c] = Cell.fromChar(ch, coord, TerminationType.SOURCE);
            }
        }
        return cells;
    }
    private String getFirstNonEmptyLine(final BufferedReader br) throws IOException {
        String s; while ((s = br.readLine()) != null) if (!s.isBlank() && !s.startsWith("#")) return s;
        return null;
    }
}