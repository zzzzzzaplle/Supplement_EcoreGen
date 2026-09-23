import java.util.*;
import java.util.stream.Collectors;
import java.util.Objects;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// --- Reference Implementation: PipePatterns ---
/**
 * Central constant repository for all map-rendering characters.
 */
class PipePatterns {
    public static final char WALL = '#';
    
    public static class Filled {
        public static final char UP_ARROW = '↑';
        public static final char DOWN_ARROW = '↓';
        public static final char LEFT_ARROW = '←';
        public static final char RIGHT_ARROW = '→';
        
        public static final char HORIZONTAL = '═';
        public static final char VERTICAL = '║';
        public static final char TOP_LEFT = '╗';
        public static final char TOP_RIGHT = '╔';
        public static final char BOTTOM_LEFT = '╝';
        public static final char BOTTOM_RIGHT = '╚';
        public static final char CROSS = '╬';
    }
    
    public static class Unfilled {
        public static final char UP_ARROW = '↑';
        public static final char DOWN_ARROW = '↓';
        public static final char LEFT_ARROW = '←';
        public static final char RIGHT_ARROW = '→';
        
        public static final char HORIZONTAL = '—';
        public static final char VERTICAL = '|';
        public static final char TOP_LEFT = '/';
        public static final char TOP_RIGHT = '\\';
        public static final char BOTTOM_LEFT = '\\';
        public static final char BOTTOM_RIGHT = '/';
        public static final char CROSS = '+';
    }
    
    private PipePatterns() {
        // Non-instantiable
    }
}

// --- Reference Implementation: StringUtils ---
/**
 * Utility class for string operations.
 */
class StringUtils {
    private StringUtils() {
        // Non-instantiable
    }

    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }
}

// --- Reference Implementation: Coordinate ---


/**
 * Representation of a coordinate in Map.
 */
class Coordinate {

    public int row;
    public int col;

    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    public Coordinate() {
        this(0, 0);
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

// --- Reference Implementation: Deserializer ---


/**
 * A deserializer for converting a map file into a Game.
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
     * Deserializes a map from a String.
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

// --- Enums ---

interface MapElement {
    public char toSingleChar();
}

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
            default: return new Coordinate(0, 0);
        }
    }
}

enum TerminationType {
    SOURCE,
    SINK
}

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

// --- Classes ---

abstract class Cell implements MapElement {
    public Coordinate coord;

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

    private static Wall createWall(Coordinate coord) {
        Wall w = new Wall();
        w.setCoord(coord);
        return w;
    }

    private static FillableCell createFillableCell(Coordinate coord, Pipe pipe) {
        FillableCell f = new FillableCell();
        f.setCoord(coord);
        if (pipe != null) {
            f.setPipe(pipe);
        }
        return f;
    }

    private static TerminationCell createTerminationCell(Coordinate coord, TerminationType type, Direction dir) {
        TerminationCell t = new TerminationCell(coord, type, dir);
        return t;
    }
}

class FillableCell extends Cell {
    private Pipe pipe;

    public Pipe getPipe() {
        return pipe;
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }
    
    public Optional<Pipe> getPipeOpt() {
        return Optional.ofNullable(pipe);
    }

    @Override
    public char toSingleChar() {
        if (pipe == null) {
            return '.';
        }
        return pipe.toSingleChar();
    }
}

class TerminationCell extends Cell {
    private boolean isFilled;
    private Direction pointingTo;
    private TerminationType type;

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        this.coord = coord;
        this.type = type;
        this.pointingTo = pointingTo;
        this.isFilled = false;
    }
    
    public TerminationCell() {
        this.coord = new Coordinate();
        this.type = TerminationType.SINK;
        this.pointingTo = Direction.RIGHT;
        this.isFilled = false;
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

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled() {
        this.isFilled = true;
    }

    @Override
    public char toSingleChar() {
        char arrow;
        switch (pointingTo) {
            case UP: arrow = PipePatterns.Filled.UP_ARROW; break;
            case DOWN: arrow = PipePatterns.Filled.DOWN_ARROW; break;
            case LEFT: arrow = PipePatterns.Filled.LEFT_ARROW; break;
            case RIGHT: arrow = PipePatterns.Filled.RIGHT_ARROW; break;
            default: throw new IllegalStateException("Invalid pointingTo value!");
        }
        
        if (isFilled) {
            return arrow;
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

class TerminationCellCreateInfo {
    public Coordinate coord;
    public Direction dir;

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

class Wall extends Cell {
    public Wall() {
        this.coord = new Coordinate();
    }

    @Override
    public char toSingleChar() {
        return PipePatterns.WALL;
    }
}

class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

    public Pipe(PipeShape shape, boolean filled) {
        this.shape = shape;
        this.filled = filled;
    }
    
    public Pipe() {
        this.shape = PipeShape.HORIZONTAL;
        this.filled = false;
    }

    public PipeShape getShape() {
        return shape;
    }

    public void setShape(PipeShape shape) {
        this.shape = shape;
    }

    public boolean getFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public void setFilled() {
        this.filled = true;
    }

    public Direction[] getConnections() {
        switch (shape) {
            case HORIZONTAL:
                return new Direction[]{Direction.LEFT, Direction.RIGHT};
            case VERTICAL:
                return new Direction[]{Direction.UP, Direction.DOWN};
            case TOP_LEFT:
                return new Direction[]{Direction.UP, Direction.LEFT};
            case TOP_RIGHT:
                return new Direction[]{Direction.UP, Direction.RIGHT};
            case BOTTOM_LEFT:
                return new Direction[]{Direction.DOWN, Direction.LEFT};
            case BOTTOM_RIGHT:
                return new Direction[]{Direction.DOWN, Direction.RIGHT};
            case CROSS:
                return new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
            default:
                throw new IllegalStateException("Unknown shape");
        }
    }

    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        switch (rep) {
            case "H": return new Pipe(PipeShape.HORIZONTAL, false);
            case "V": return new Pipe(PipeShape.VERTICAL, false);
            case "TL": return new Pipe(PipeShape.TOP_LEFT, false);
            case "TR": return new Pipe(PipeShape.TOP_RIGHT, false);
            case "BL": return new Pipe(PipeShape.BOTTOM_LEFT, false);
            case "BR": return new Pipe(PipeShape.BOTTOM_RIGHT, false);
            case "C": return new Pipe(PipeShape.CROSS, false);
            default:
                throw new IllegalArgumentException("Unknown pipe string: " + rep);
        }
    }
}

class DelayBar {
    private int initialValue;
    private int currentValue;

    public DelayBar(int initialValue) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
    }
    
    public DelayBar() {
        this(0);
    }

    public int getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(int initialValue) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public void countdown() {
        if (currentValue > 0) {
            currentValue--;
        }
    }

    public int distance() {
        return -currentValue;
    }
}

class Map {
    private int rows;
    private int cols;
    public Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;
    private Set<FillableCell> filledTiles;
    private int prevFilledTiles;
    private Integer prevFilledDistance;

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = 0;
        
        // Find source and sink
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell tCell = (TerminationCell) cell;
                    if (tCell.getType() == TerminationType.SOURCE) {
                        this.sourceCell = tCell;
                    } else if (tCell.getType() == TerminationType.SINK) {
                        this.sinkCell = tCell;
                    }
                }
            }
        }
    }
    
    public Map() {
        this.rows = 0;
        this.cols = 0;
        this.cells = new Cell[0][0];
        this.sourceCell = null;
        this.sinkCell = null;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = 0;
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

    public Set<FillableCell> getFilledTiles() {
        return filledTiles;
    }

    public void setFilledTiles(Set<FillableCell> filledTiles) {
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

    public static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe pipe) {
        // Check bounds: playable area is [1..rows-2] x [1..cols-2]
        if (row < 1 || row >= rows - 1 || col < 1 || col >= cols - 1) {
            return false;
        }

        Cell cell = cells[row][col];
        if (!(cell instanceof FillableCell)) {
            return false;
        }

        FillableCell fillableCell = (FillableCell) cell;
        if (fillableCell.getPipe() != null) {
            return false;
        }

        fillableCell.setPipe(pipe);
        return true;
    }

    public void undo(Coordinate coord) {
        undo(coord.row, coord.col);
    }

    private void undo(int row, int col) {
        if (row < 1 || row >= rows - 1 || col < 1 || col >= cols - 1) {
            return;
        }
        Cell cell = cells[row][col];
        if (cell instanceof FillableCell) {
            FillableCell fillableCell = (FillableCell) cell;
            fillableCell.setPipe(null);
        }
    }

    public void fillBeginTile() {
        if (sourceCell != null) {
            sourceCell.setFilled();
        }
    }

    public void fillTiles(int distance) {
        // Implementation of BFS water flow
        // Reset previous filled tiles tracking for this round logic if needed, 
        // but typically we just expand from existing filled tiles or source.
        
        // For simplicity in this generated code, we assume a standard BFS expansion
        // from the source cell through connected pipes.
        
        // Note: The exact BFS implementation details for water propagation 
        // (checking connections, updating filled state) are complex. 
        // Given the constraints, we provide a structural placeholder that 
        // aligns with the requirements: expanding distance and checking connections.
        
        // In a full implementation, this would iterate through filled tiles,
        // check neighbors for matching pipe connections, and mark new tiles as filled.
        
        // Simplified logic for compilation completeness:
        // 1. Identify all currently filled tiles.
        // 2. For each filled tile, check adjacent cells.
        // 3. If adjacent cell is a pipe that connects to the filled tile, mark it filled.
        // 4. Repeat 'distance' times? Or just expand by 1 step per call?
        // Requirement says: "Each round increments the fill distance by 1."
        // fillTiles(distance) fills pipes within the specified distance.
        
        // Let's implement a BFS that expands up to 'distance' steps from current filled set.
        
        Set<Coordinate> nextFilledCoords = new HashSet<>();
        Set<Coordinate> visited = new HashSet<>();
        
        // Initial queue: all currently filled cells (including source if filled)
        // We need to track which cells are filled to start the BFS
        List<Coordinate> currentFilled = new ArrayList<>();
        for (FillableCell fc : filledTiles) {
            currentFilled.add(fc.getCoord());
        }
        if (sourceCell != null && sourceCell.isFilled()) {
            // Source is not a FillableCell, so not in filledTiles set initially unless added manually?
            // Usually source is considered filled at start.
            // Let's assume filledTiles contains FillableCells that are filled.
            // We need to handle Source filling separately or add it to a logical "filled" set.
        }
        
        // To properly implement, we need a Set of all filled coordinates (including Source/Sink if filled)
        Set<Coordinate> filledCoords = new HashSet<>();
        for (FillableCell fc : filledTiles) {
            filledCoords.add(fc.getCoord());
        }
        if (sourceCell != null && sourceCell.isFilled()) {
            filledCoords.add(sourceCell.getCoord());
        }
        if (sinkCell != null && sinkCell.isFilled()) { // Sink might be filled
            filledCoords.add(sinkCell.getCoord());
        }

        // BFS
        Queue<Coordinate> queue = new LinkedList<>();
        for (Coordinate c : filledCoords) {
            queue.add(c);
            visited.add(c);
        }

        int steps = 0;
        while (!queue.isEmpty() && steps < distance) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Coordinate curr = queue.poll();
                
                // Check all 4 directions
                for (Direction dir : Direction.values()) {
                    Coordinate nextCoord = curr.add(dir.getOffset());
                    
                    // Bounds check
                    if (nextCoord.row < 0 || nextCoord.row >= rows || nextCoord.col < 0 || nextCoord.col >= cols) {
                        continue;
                    }
                    
                    // If already visited/filled, skip
                    if (visited.contains(nextCoord)) {
                        continue;
                    }
                    
                    Cell nextCell = cells[nextCoord.row][nextCoord.col];
                    
                    // Check if nextCell is a valid target (Pipe or Termination)
                    // And check connection compatibility
                    if (nextCell instanceof FillableCell) {
                        FillableCell fc = (FillableCell) nextCell;
                        Pipe p = fc.getPipe();
                        if (p != null) {
                            // Check if current cell connects to nextCell via 'dir'
                            // Current cell must have a pipe connected to 'dir' OR be Source
                            boolean currentConnects = false;
                            if (curr.equals(sourceCell.getCoord()) && sourceCell != null) {
                                // Source connects in its pointingTo direction
                                if (sourceCell.getPointingTo() == dir) {
                                    currentConnects = true;
                                }
                            } else {
                                // Find pipe at current cell
                                Cell currCellObj = cells[curr.row][curr.col];
                                if (currCellObj instanceof FillableCell) {
                                    Pipe currPipe = ((FillableCell) currCellObj).getPipe();
                                    if (currPipe != null) {
                                        for (Direction conn : currPipe.getConnections()) {
                                            if (conn == dir) {
                                                currentConnects = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            
                            if (currentConnects) {
                                // Check if next pipe connects back to current cell (opposite direction)
                                Direction oppositeDir = dir.getOpposite();
                                boolean nextConnectsBack = false;
                                for (Direction conn : p.getConnections()) {
                                    if (conn == oppositeDir) {
                                        nextConnectsBack = true;
                                        break;
                                    }
                                }
                                
                                if (nextConnectsBack) {
                                    nextFilledCoords.add(nextCoord);
                                    visited.add(nextCoord);
                                    queue.add(nextCoord);
                                }
                            }
                        }
                    } else if (nextCell instanceof TerminationCell) {
                        TerminationCell tc = (TerminationCell) nextCell;
                        // Source can connect to Sink if directions align?
                        // Usually Sink is just a target.
                        if (curr.equals(sourceCell.getCoord()) && sourceCell != null) {
                             if (sourceCell.getPointingTo() == dir) {
                                 // Check if Sink connects to this direction (incoming)
                                 // Sink doesn't have outgoing connections in the same way, but it has pointingTo
                                 // If Sink points outward, it accepts water from inside?
                                 // Requirement: "step once in the SOURCE pointingTo direction... reaching the SINK cell implies win"
                                 // For filling: Sink gets filled if connected.
                                 if (tc.getType() == TerminationType.SINK) {
                                     // Sink is filled if the pipe leading to it connects to it.
                                     // Sink's pointingTo is OUTWARD. So the pipe inside must connect to the OUTWARD direction?
                                     // No, the pipe connects to the SINK's INNER side.
                                     // Let's assume Sink is filled if connected.
                                     nextFilledCoords.add(nextCoord);
                                     visited.add(nextCoord);
                                 }
                             }
                        } else {
                            // Pipe connecting to TerminationCell
                            Cell currCellObj = cells[curr.row][curr.col];
                            if (currCellObj instanceof FillableCell) {
                                Pipe currPipe = ((FillableCell) currCellObj).getPipe();
                                if (currPipe != null) {
                                    boolean currentConnects = false;
                                    for (Direction conn : currPipe.getConnections()) {
                                        if (conn == dir) {
                                            currentConnects = true;
                                            break;
                                        }
                                    }
                                    if (currentConnects) {
                                        // Sink accepts water?
                                        nextFilledCoords.add(nextCoord);
                                        visited.add(nextCoord);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            steps++;
        }
        
        // Update filled tiles set
        for (Coordinate c : nextFilledCoords) {
            Cell cell = cells[c.row][c.col];
            if (cell instanceof FillableCell) {
                ((FillableCell) cell).getPipe().setFilled(true);
                filledTiles.add((FillableCell) cell);
            } else if (cell instanceof TerminationCell) {
                ((TerminationCell) cell).setFilled();
            }
        }
        
        // Update stats
        prevFilledTiles = filledTiles.size();
        prevFilledDistance = distance;
    }

    public boolean checkPath() {
        // BFS from Source to Sink
        if (sourceCell == null || sinkCell == null) {
            return false;
        }
        
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        
        queue.add(sourceCell.getCoord());
        visited.add(sourceCell.getCoord());
        
        while (!queue.isEmpty()) {
            Coordinate curr = queue.poll();
            
            // If we reached Sink, return true
            if (curr.equals(sinkCell.getCoord())) {
                return true;
            }
            
            // Determine connection directions from current cell
            List<Direction> directions = new ArrayList<>();
            if (curr.equals(sourceCell.getCoord())) {
                directions.add(sourceCell.getPointingTo());
            } else {
                Cell cell = cells[curr.row][curr.col];
                if (cell instanceof FillableCell) {
                    Pipe p = ((FillableCell) cell).getPipe();
                    if (p != null) {
                        directions.addAll(Arrays.asList(p.getConnections()));
                    }
                }
            }
            
            for (Direction dir : directions) {
                Coordinate nextCoord = curr.add(dir.getOffset());
                
                if (nextCoord.row < 0 || nextCoord.row >= rows || nextCoord.col < 0 || nextCoord.col >= cols) {
                    continue;
                }
                
                if (visited.contains(nextCoord)) {
                    continue;
                }
                
                Cell nextCell = cells[nextCoord.row][nextCoord.col];
                
                // Check if next cell connects back to current
                boolean connectsBack = false;
                if (nextCell instanceof FillableCell) {
                    Pipe p = ((FillableCell) nextCell).getPipe();
                    if (p != null) {
                        for (Direction conn : p.getConnections()) {
                            if (conn == dir.getOpposite()) {
                                connectsBack = true;
                                break;
                            }
                        }
                    }
                } else if (nextCell instanceof TerminationCell) {
                    // Sink accepts connection
                    if (nextCell instanceof TerminationCell) {
                         connectsBack = true; // Sink is always a valid endpoint
                    }
                }
                
                if (connectsBack) {
                    visited.add(nextCoord);
                    queue.add(nextCoord);
                }
            }
        }
        
        return false;
    }

    public boolean hasLost() {
        // Loss if prevFilledTiles == 0 and distance > 0
        // Note: distance is from DelayBar
        return prevFilledTiles == 0;
    }
}

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
        return cellStack.pop();
    }

    public int getUndoCount() {
        return count;
    }
    
    public void incrementUndoCount() {
        count++;
    }
}

class PipeQueue {
    private static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
        // Ensure queue is full
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }
    
    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }

    public Pipe peek() {
        return pipeQueue.peek();
    }

    public void consume() {
        pipeQueue.poll();
        // Refill
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
        // Trim if too large? No, just add to front.
        // If size exceeds MAX_GEN_LENGTH, remove from end?
        while (pipeQueue.size() > MAX_GEN_LENGTH) {
            pipeQueue.removeLast();
        }
    }

    private static Pipe generateNewPipe() {
        PipeShape[] shapes = PipeShape.values();
        Random rand = new Random();
        PipeShape shape = shapes[rand.nextInt(shapes.length)];
        return new Pipe(shape, false);
    }
}

class Game {
    private int numOfSteps;
    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;

    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        this.numOfSteps = 0;
    }
    
    public Game() {
        this.map = new Map();
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar();
        this.numOfSteps = 0;
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

    public static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    public boolean placePipe(int row, char colChar) {
        // Map column letter 'A' to index 1
        int col = colChar - 'A' + 1;
        
        Pipe pipe = pipeQueue.peek();
        if (pipe == null) {
            return false;
        }
        
        if (map.tryPlacePipe(row, col, pipe)) {
            FillableCell fc = (FillableCell) map.getCells()[row][col];
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
        if (cellStack.getCellStack().isEmpty()) {
            return false;
        }
        
        FillableCell fc = cellStack.pop();
        Coordinate coord = fc.getCoord();
        
        // Restore pipe to queue head
        Pipe pipe = fc.getPipe();
        if (pipe != null) {
            pipeQueue.undo(pipe);
        }
        
        // Clear cell on map
        map.undo(coord);
        
        numOfSteps++;
        cellStack.incrementUndoCount();
        return true;
    }

    public void updateState() {
        // Update DelayBar
        delayBar.countdown();
        
        // Get distance
        int distance = delayBar.distance();
        
        // If delay has ended (distance > 0), fill tiles
        if (distance > 0) {
            map.fillTiles(distance);
        }
    }

    public boolean hasWon() {
        return map.checkPath();
    }

    public boolean hasLost() {
        // Loss condition: no new pipes filled in previous round AND delay has ended
        // Requirement: "The game checks loss only after delay has ended (distance > 0)."
        return delayBar.distance() > 0 && map.hasLost();
    }
}