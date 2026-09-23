import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     *
     * @param rows Rows of the given map.
     * @param cols Columns of the given map.
     * @param cellsRep String representation of the map, with rows delimited by '\n'.
     * @return A 2D cell array from the string.
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

/**
 * Central constant repository for all map-rendering characters.
 */
class PipePatterns {
    public static final char WALL = '#';

    public static class Filled {
        public static final char HORIZONTAL = '=';
        public static final char VERTICAL = '|';
        public static final char TOP_LEFT = '╮';
        public static final char TOP_RIGHT = '╭';
        public static final char BOTTOM_LEFT = '╯';
        public static final char BOTTOM_RIGHT = '╰';
        public static final char CROSS = '╋';
        
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
    }

    public static class Unfilled {
        public static final char HORIZONTAL = '-';
        public static final char VERTICAL = '|';
        public static final char TOP_LEFT = '┐';
        public static final char TOP_RIGHT = '┌';
        public static final char BOTTOM_LEFT = '┘';
        public static final char BOTTOM_RIGHT = '└';
        public static final char CROSS = '┼';
        
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
    }

    private PipePatterns() {
        // Utility class
    }
}

/**
 * Lightweight string helper utilities.
 */
class StringUtils {
    private StringUtils() {
        // Utility class
    }

    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }
}

/**
 * Interface for map elements that can be rendered as a single character.
 */
interface MapElement {
    char toSingleChar();
}

/**
 * Direction enum providing four directions plus opposite/offset.
 */
enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int dr;
    private final int dc;

    Direction(int dr, int dc) {
        this.dr = dr;
        this.dc = dc;
    }

    public Direction getOpposite() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: return null;
        }
    }

    public Coordinate getOffset() {
        return new Coordinate(dr, dc);
    }
}

/**
 * Termination type for Source and Sink cells.
 */
enum TerminationType {
    SOURCE,
    SINK
}

/**
 * Pipe shape enum defining connection directions and rendering characters.
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
        return isFilled ? this.filledChar : this.unfilledChar;
    }

    public Direction[] getConnections() {
        switch (this) {
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
}

/**
 * Abstract base class for cells in the map.
 */
abstract class Cell implements MapElement {
    public Coordinate coord;

    public Cell() {
    }

    public Cell(Coordinate coord) {
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

    private static Cell createWall(Coordinate coord) {
        return new Wall(coord);
    }

    private static Cell createFillableCell(Coordinate coord, Pipe pipe) {
        return new FillableCell(coord, pipe);
    }

    private static Cell createTerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        return new TerminationCell(coord, type, pointingTo);
    }
}

/**
 * A cell that can hold a pipe.
 */
class FillableCell extends Cell {
    private Pipe pipe;

    public FillableCell(Coordinate coord, Pipe pipe) {
        super(coord);
        this.pipe = pipe;
    }

    public FillableCell() {
        super();
    }

    public java.util.Optional<Pipe> getPipe() {
        return java.util.Optional.ofNullable(pipe);
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }

    @Override
    public char toSingleChar() {
        if (pipe == null) {
            return '.';
        }
        return pipe.toSingleChar();
    }
}

/**
 * A termination cell (Source or Sink).
 */
class TerminationCell extends Cell {
    private boolean isFilled;
    private Direction pointingTo;
    private TerminationType type;

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        super(coord);
        this.type = type;
        this.pointingTo = pointingTo;
        this.isFilled = false;
    }

    public TerminationCell() {
        super();
        this.isFilled = false;
    }

    public void setFilled() {
        this.isFilled = true;
    }

    @Override
    public char toSingleChar() {
        char[] arrowsFilled = {
                PipePatterns.Filled.UP_ARROW,
                PipePatterns.Filled.DOWN_ARROW,
                PipePatterns.Filled.LEFT_ARROW,
                PipePatterns.Filled.RIGHT_ARROW
        };
        char[] arrowsUnfilled = {
                PipePatterns.Unfilled.UP_ARROW,
                PipePatterns.Unfilled.DOWN_ARROW,
                PipePatterns.Unfilled.LEFT_ARROW,
                PipePatterns.Unfilled.RIGHT_ARROW
        };

        int index = -1;
        switch (pointingTo) {
            case UP: index = 0; break;
            case DOWN: index = 1; break;
            case LEFT: index = 2; break;
            case RIGHT: index = 3; break;
        }

        if (index == -1) {
            throw new IllegalStateException("Invalid pointingTo value!");
        }

        return isFilled ? arrowsFilled[index] : arrowsUnfilled[index];
    }
}

/**
 * Information required to create a TerminationCell.
 */
class TerminationCellCreateInfo {
    public Coordinate coord;
    public Direction dir;

    public TerminationCellCreateInfo() {
    }
}

/**
 * A wall cell.
 */
class Wall extends Cell {
    public Wall(Coordinate coord) {
        super(coord);
    }

    public Wall() {
        super();
    }

    @Override
    public char toSingleChar() {
        return PipePatterns.WALL;
    }
}

/**
 * A pipe object.
 */
class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

    public Pipe() {
        this.shape = null;
        this.filled = false;
    }

    public Pipe(PipeShape shape) {
        this.shape = shape;
        this.filled = false;
    }

    public void setFilled() {
        this.filled = true;
    }

    public boolean getFilled() {
        return filled;
    }

    public Direction[] getConnections() {
        if (shape == null) {
            return new Direction[0];
        }
        return shape.getConnections();
    }

    @Override
    public char toSingleChar() {
        if (shape == null) {
            return '.';
        }
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        switch (rep) {
            case "HZ": return new Pipe(PipeShape.HORIZONTAL);
            case "VT": return new Pipe(PipeShape.VERTICAL);
            case "TL": return new Pipe(PipeShape.TOP_LEFT);
            case "TR": return new Pipe(PipeShape.TOP_RIGHT);
            case "BL": return new Pipe(PipeShape.BOTTOM_LEFT);
            case "BR": return new Pipe(PipeShape.BOTTOM_RIGHT);
            case "CR": return new Pipe(PipeShape.CROSS);
            default: throw new IllegalArgumentException("Unknown pipe shape: " + rep);
        }
    }
}

/**
 * The main game map.
 */
class Map {
    private int rows;
    private int cols;
    public Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;
    public Set<Coordinate> filledTiles;
    public int prevFilledTiles;
    public Integer prevFilledDistance;

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = 0;

        locateTerminationCells();
    }

    private void locateTerminationCells() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    if (tc.type == TerminationType.SOURCE) {
                        this.sourceCell = tc;
                    } else if (tc.type == TerminationType.SINK) {
                        this.sinkCell = tc;
                    }
                }
            }
        }
    }

    public static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(Coordinate coord, Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe pipe) {
        // Playable area is [1..rows-2] x [1..cols-2]
        if (row < 1 || row >= rows - 1 || col < 1 || col >= cols - 1) {
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
        // This logic is typically handled by Game via CellStack, 
        // but if called directly on Map, we just clear the pipe.
        if (coord != null && coord.row >= 0 && coord.row < rows && coord.col >= 0 && coord.col < cols) {
            Cell cell = cells[coord.row][coord.col];
            if (cell instanceof FillableCell) {
                ((FillableCell) cell).setPipe(null);
            }
        }
    }

    public void fillBeginTile() {
        if (sourceCell != null) {
            sourceCell.setFilled();
        }
    }

    public void fillTiles(int distance) {
        // BFS to fill pipes up to 'distance' from source
        // We need to track visited nodes to avoid cycles and re-processing
        // We also need to track the "wavefront" of filled tiles to expand
        
        // If distance is 0 or negative, we don't propagate new tiles
        if (distance <= 0) {
            return;
        }

        // Current filled tiles set
        Set<Coordinate> currentFilled = new HashSet<>(filledTiles);
        Set<Coordinate> nextFilled = new HashSet<>();

        // If this is the first time filling (distance == 1), ensure source is in currentFilled
        if (filledTiles.isEmpty() && sourceCell != null) {
            currentFilled.add(sourceCell.coord);
        }

        // Expand BFS layer by layer for 'distance' steps
        for (int step = 0; step < distance; step++) {
            Set<Coordinate> newFilled = new HashSet<>();
            
            for (Coordinate coord : currentFilled) {
                Cell cell = cells[coord.row][coord.col];
                if (cell instanceof Pipe) {
                    Pipe p = (Pipe) cell;
                    Direction[] connections = p.getConnections();
                    for (Direction d : connections) {
                        Coordinate neighborCoord = coord.add(d.getOffset());
                        
                        // Check bounds
                        if (neighborCoord.row < 0 || neighborCoord.row >= rows || 
                            neighborCoord.col < 0 || neighborCoord.col >= cols) {
                            continue;
                        }

                        Cell neighbor = cells[neighborCoord.row][neighborCoord.col];
                        
                        // If neighbor is a Pipe and not yet filled, check connection match
                        if (neighbor instanceof Pipe) {
                            Pipe nPipe = (Pipe) neighbor;
                            Direction[] nConnections = nPipe.getConnections();
                            boolean hasOpposite = false;
                            for (Direction nd : nConnections) {
                                if (nd == d.getOpposite()) {
                                    hasOpposite = true;
                                    break;
                                }
                            }
                            
                            if (hasOpposite && !filledTiles.contains(neighborCoord) && !currentFilled.contains(neighborCoord)) {
                                newFilled.add(neighborCoord);
                            }
                        } 
                        // If neighbor is Sink, we don't "fill" it as a pipe, but path exists
                        else if (neighbor instanceof TerminationCell) {
                            TerminationCell tc = (TerminationCell) neighbor;
                            if (tc.type == TerminationType.SINK) {
                                // Sink is not a fillable tile in the same sense, but path is found
                                // We don't add it to filledTiles for propagation
                            }
                        }
                    }
                }
            }
            
            if (newFilled.isEmpty()) {
                break;
            }
            
            currentFilled.addAll(newFilled);
            filledTiles.addAll(newFilled);
        }
        
        // Count how many NEW tiles were filled in this operation relative to previous state
        // The requirement says: "Each round increments the fill distance by 1"
        // and "hasLost() returns true when prevFilledTiles == 0, meaning no new pipes were filled in the previous round"
        // We need to track the count of newly filled tiles for the current distance expansion.
        // However, fillTiles is called with a specific distance. 
        // Let's interpret "prevFilledTiles" as the number of tiles filled in the PREVIOUS call to fillTiles (or equivalent step).
        // Since fillTiles(distance) might be called with distance > 1, we should compare against previous state.
        
        // Actually, looking at Game.updateState, it calls fillTiles(distance).
        // The loss condition is about whether progress was made.
        // Let's record the number of tiles filled DURING this call.
        // But wait, filledTiles is cumulative.
        // Let's track the count added in this specific call.
        
        // Re-evaluating: The prompt says "prevFilledTiles == 0 ... meaning no new pipes were filled in the previous round".
        // So we need to know how many were filled in the LAST round.
        // Let's assume fillTiles is called once per game tick.
        
        // To implement this correctly, we need to know how many tiles were added to filledTiles in THIS call.
        // Since I modified filledTiles directly in the loop above, I can't easily distinguish "previous" vs "current" 
        // unless I track the size before and after.
        
        // Let's refactor fillTiles to return the count of newly filled tiles.
        // But the signature is void.
        // I will update prevFilledTiles at the end.
        // But wait, fillTiles(distance) might fill multiple layers at once if distance > 1?
        // Usually in these games, distance increments by 1.
        // Let's assume standard usage: distance increments by 1.
        
        // Let's store the count of tiles filled in this specific invocation.
        // I'll use a helper variable or just compare sizes.
        // Since I can't change the signature, I'll update the field.
        // But I need to know the STARTING size.
    }
    
    // Helper to track new fills
    private int calculateNewFilledTiles(int distance) {
        Set<Coordinate> currentFilled = new HashSet<>(filledTiles);
        Set<Coordinate> nextFilled = new HashSet<>();

        if (filledTiles.isEmpty() && sourceCell != null) {
            currentFilled.add(sourceCell.coord);
        }

        for (int step = 0; step < distance; step++) {
            Set<Coordinate> newFilled = new HashSet<>();
            
            for (Coordinate coord : currentFilled) {
                Cell cell = cells[coord.row][coord.col];
                if (cell instanceof Pipe) {
                    Pipe p = (Pipe) cell;
                    Direction[] connections = p.getConnections();
                    for (Direction d : connections) {
                        Coordinate neighborCoord = coord.add(d.getOffset());
                        
                        if (neighborCoord.row < 0 || neighborCoord.row >= rows || 
                            neighborCoord.col < 0 || neighborCoord.col >= cols) {
                            continue;
                        }

                        Cell neighbor = cells[neighborCoord.row][neighborCoord.col];
                        
                        if (neighbor instanceof Pipe) {
                            Pipe nPipe = (Pipe) neighbor;
                            Direction[] nConnections = nPipe.getConnections();
                            boolean hasOpposite = false;
                            for (Direction nd : nConnections) {
                                if (nd == d.getOpposite()) {
                                    hasOpposite = true;
                                    break;
                                }
                            }
                            
                            if (hasOpposite && !filledTiles.contains(neighborCoord) && !currentFilled.contains(neighborCoord)) {
                                newFilled.add(neighborCoord);
                            }
                        }
                    }
                }
            }
            
            if (newFilled.isEmpty()) {
                break;
            }
            
            currentFilled.addAll(newFilled);
            // We don't add to filledTiles here yet, we do it at the end to get the count
        }
        
        filledTiles.addAll(currentFilled);
        // Wait, currentFilled now contains all filled tiles up to distance.
        // But filledTiles already had some.
        // The set difference is the new ones.
        
        // Actually, the logic above adds to currentFilled iteratively.
        // Let's just count how many were added to the global filledTiles set.
        
        int oldSize = filledTiles.size();
        filledTiles.addAll(currentFilled);
        return filledTiles.size() - oldSize;
    }

    // Override fillTiles to use the helper
    @Override
    public void fillTiles(int distance) {
        if (distance <= 0) {
            prevFilledTiles = 0;
            return;
        }
        
        // Save old state
        int oldSize = filledTiles.size();
        
        // Perform BFS
        Set<Coordinate> currentFilled = new HashSet<>(filledTiles);
        if (filledTiles.isEmpty() && sourceCell != null) {
            currentFilled.add(sourceCell.coord);
        }

        for (int step = 0; step < distance; step++) {
            Set<Coordinate> newFilled = new HashSet<>();
            
            for (Coordinate coord : currentFilled) {
                Cell cell = cells[coord.row][coord.col];
                if (cell instanceof Pipe) {
                    Pipe p = (Pipe) cell;
                    Direction[] connections = p.getConnections();
                    for (Direction d : connections) {
                        Coordinate neighborCoord = coord.add(d.getOffset());
                        
                        if (neighborCoord.row < 0 || neighborCoord.row >= rows || 
                            neighborCoord.col < 0 || neighborCoord.col >= cols) {
                            continue;
                        }

                        Cell neighbor = cells[neighborCoord.row][neighborCoord.col];
                        
                        if (neighbor instanceof Pipe) {
                            Pipe nPipe = (Pipe) neighbor;
                            Direction[] nConnections = nPipe.getConnections();
                            boolean hasOpposite = false;
                            for (Direction nd : nConnections) {
                                if (nd == d.getOpposite()) {
                                    hasOpposite = true;
                                    break;
                                }
                            }
                            
                            if (hasOpposite && !filledTiles.contains(neighborCoord) && !currentFilled.contains(neighborCoord)) {
                                newFilled.add(neighborCoord);
                            }
                        }
                    }
                }
            }
            
            if (newFilled.isEmpty()) {
                break;
            }
            
            currentFilled.addAll(newFilled);
        }
        
        filledTiles.addAll(currentFilled);
        
        // Calculate how many NEW tiles were added
        prevFilledTiles = filledTiles.size() - oldSize;
        prevFilledDistance = distance;
    }

    public boolean checkPath() {
        // BFS from Source to Sink
        if (sourceCell == null || sinkCell == null) {
            return false;
        }

        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        
        // Start at Source
        visited.add(sourceCell.coord);
        queue.add(sourceCell.coord);
        
        while (!queue.isEmpty()) {
            Coordinate curr = queue.poll();
            Cell cell = cells[curr.row][curr.col];
            
            List<Direction> directionsToCheck = new ArrayList<>();
            
            if (cell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) cell;
                // From Source, step in pointingTo direction
                directionsToCheck.add(tc.pointingTo);
            } else if (cell instanceof Pipe) {
                Pipe p = (Pipe) cell;
                // From Pipe, step in each connection direction
                directionsToCheck.addAll(Arrays.asList(p.getConnections()));
            }
            
            for (Direction d : directionsToCheck) {
                Coordinate nextCoord = curr.add(d.getOffset());
                
                if (nextCoord.row < 0 || nextCoord.row >= rows || 
                    nextCoord.col < 0 || nextCoord.col >= cols) {
                    continue;
                }
                
                Cell nextCell = cells[nextCoord.row][nextCoord.col];
                
                if (nextCell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) nextCell;
                    if (tc.type == TerminationType.SINK) {
                        return true;
                    }
                    // If it's another Source (unlikely), treat as pipe? No, usually Sink is destination.
                    // If it's a Source, it's not the target.
                } else if (nextCell instanceof Pipe) {
                    if (!visited.contains(nextCoord)) {
                        visited.add(nextCoord);
                        queue.add(nextCoord);
                    }
                }
            }
        }
        
        return false;
    }

    public boolean hasLost() {
        // Loss condition: prevFilledTiles == 0, meaning no new pipes were filled in the previous round
        // The game checks loss only after delay has ended (distance > 0)
        // This check is usually done in Game.hasLost(), but the prompt says "hasLost() returns true when..."
        // I will implement the core logic here.
        return prevFilledTiles == 0;
    }
    
    // Getters for Map fields
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public Cell[][] getCells() { return cells; }
    public TerminationCell getSourceCell() { return sourceCell; }
    public TerminationCell getSinkCell() { return sinkCell; }
    public Set<Coordinate> getFilledTiles() { return filledTiles; }
    public int getPrevFilledTiles() { return prevFilledTiles; }
    public Integer getPrevFilledDistance() { return prevFilledDistance; }
}

/**
 * Stack of FillableCells for undo functionality.
 */
class CellStack {
    private Stack<FillableCell> cellStack;
    private int count; // counts successful undo actions

    public CellStack() {
        this.cellStack = new Stack<>();
        this.count = 0;
    }

    public void push(FillableCell cell) {
        cellStack.push(cell);
    }

    public FillableCell pop() {
        if (cellStack.isEmpty()) {
            return null;
        }
        return cellStack.pop();
    }
    
    public FillableCell peek() {
        return cellStack.peek();
    }

    public int getUndoCount() {
        return count;
    }
    
    public void incrementUndoCount() {
        count++;
    }
    
    public boolean isEmpty() {
        return cellStack.isEmpty();
    }
}

/**
 * Queue of Pipes for the player.
 */
class PipeQueue {
    private static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        if (pipes != null) {
            this.pipeQueue.addAll(pipes);
        }
        // Ensure initial size is MAX_GEN_LENGTH
        while (this.pipeQueue.size() < MAX_GEN_LENGTH) {
            this.pipeQueue.add(generateNewPipe());
        }
    }

    public PipeQueue() {
        this(null);
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
            // Refill
            while (pipeQueue.size() < MAX_GEN_LENGTH) {
                pipeQueue.add(generateNewPipe());
            }
        }
    }

    public void undo(Pipe pipe) {
        if (pipe != null) {
            pipeQueue.addFirst(pipe);
        }
    }

    private static Pipe generateNewPipe() {
        PipeShape[] shapes = PipeShape.values();
        Random rand = new Random();
        int idx = rand.nextInt(shapes.length);
        return new Pipe(shapes[idx]);
    }
    
    public boolean isEmpty() {
        return pipeQueue.isEmpty();
    }
    
    public int size() {
        return pipeQueue.size();
    }
}

/**
 * Bar that counts down before water starts flowing.
 */
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

    public void countdown() {
        if (currentValue > 0) {
            currentValue--;
        }
    }
    
    public void reset() {
        currentValue = initialValue;
    }

    public int distance() {
        // Returns -currentValue. 
        // During countdown (currentValue > 0), distance is negative.
        // When currentValue == 0, distance is 0.
        // After delay, if we decrement further? No, usually it stays 0 or becomes positive flow distance.
        // The prompt says: "distance() returns -currentValue, representing how far the water should flow (negative during countdown, positive after delay ends)."
        // This implies currentValue might go below 0? Or distance is calculated differently?
        // Let's stick to the formula: -currentValue.
        // If initialValue is 3.
        // Start: 3. Distance: -3.
        // Countdown 1: 2. Distance: -2.
        // Countdown 2: 1. Distance: -1.
        // Countdown 3: 0. Distance: 0.
        // If countdown continues to -1? Distance: 1.
        
        // To support "positive after delay ends", we need currentValue to become negative.
        // Let's adjust countdown to allow negative values.
        if (currentValue > -initialValue) { // Heuristic: allow going down to -initialValue?
             // Actually, let's just decrement.
        }
        
        // Let's assume countdown decrements indefinitely or until a stop.
        // But initialValue is the start.
        // Let's just decrement.
        if (currentValue > -100) { // Arbitrary limit to prevent overflow if called many times
             currentValue--;
        }
        
        return -currentValue;
    }
    
    public int getCurrentValue() {
        return currentValue;
    }
    
    public int getInitialValue() {
        return initialValue;
    }
}

/**
 * The main Game class.
 */
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
        // No-arg constructor for completeness
        this.numOfSteps = 0;
    }

    public static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * Places a pipe at the specified row and column.
     * Column is a char 'A', 'B', etc. mapped to internal index.
     * 'A' -> 1, 'B' -> 2, etc.
     */
    public boolean placePipe(int row, char colChar) {
        // Map column char to internal index
        // Player-facing: 'A' is col 1.
        int colIndex = colChar - 'A' + 1;
        
        Pipe pipe = pipeQueue.peek();
        if (pipe == null) {
            return false;
        }

        boolean placed = map.tryPlacePipe(row, colIndex, pipe);
        
        if (placed) {
            // Get the cell that was modified to push to stack
            Cell cell = map.cells[row][colIndex];
            if (cell instanceof FillableCell) {
                cellStack.push((FillableCell) cell);
            }
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
        if (cellStack.isEmpty()) {
            return false;
        }
        
        FillableCell cell = cellStack.pop();
        if (cell == null || cell.coord == null) {
            return false;
        }
        
        // Restore pipe to queue head
        Pipe pipe = cell.getPipe().orElse(null);
        if (pipe != null) {
            pipeQueue.undo(pipe);
        }
        
        // Clear cell on map
        map.undo(cell.coord);
        
        // Increment undo count in stack
        cellStack.incrementUndoCount();
        
        numOfSteps++;
        return true;
    }

    public void updateState() {
        // Countdown delay
        int distance = delayBar.distance();
        
        // If delay is active (distance < 0), don't fill tiles yet?
        // Or does countdown happen, then filling?
        // Prompt: "Water propagates ... Each round increments the fill distance by 1."
        // "hasLost() returns true when prevFilledTiles == 0 ... checks loss only after delay has ended (distance > 0)."
        
        // If distance <= 0, we might just be counting down.
        // Let's assume if distance <= 0, we don't fill.
        if (distance > 0) {
            map.fillTiles(distance);
        } else {
            // If distance is 0 or negative, no filling happens, so prevFilledTiles becomes 0?
            // We need to explicitly set it if we don't call fillTiles.
            map.prevFilledTiles = 0;
        }
        
        // Decrement delay bar for next round
        delayBar.countdown();
    }

    public boolean hasWon() {
        return map.checkPath();
    }

    public boolean hasLost() {
        // Loss condition: prevFilledTiles == 0 AND delay has ended (distance > 0)
        // distance > 0 implies delayBar.currentValue < 0
        if (delayBar.distance() <= 0) {
            return false;
        }
        return map.hasLost();
    }

    public int getNumOfSteps() {
        return numOfSteps;
    }
    
    // Getters
    public Map getMap() { return map; }
    public CellStack getCellStack() { return cellStack; }
    public PipeQueue getPipeQueue() { return pipeQueue; }
    public DelayBar getDelayBar() { return delayBar; }
}