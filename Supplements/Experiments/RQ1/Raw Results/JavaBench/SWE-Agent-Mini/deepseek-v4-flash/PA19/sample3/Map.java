import java.util.*;

/**
 * The game map containing cells, source, and sink.
 */
public class Map {

    private int rows;
    private int cols;
    public Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;
    private Set<Coordinate> filledTiles;
    private int prevFilledTiles;
    private Integer prevFilledDistance;

    public Map() {
    }

    /**
     * Constructs a Map with the given dimensions and cell array.
     */
    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = null;

        // Find source and sink cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    if (tc.getType() == TerminationType.SOURCE) {
                        sourceCell = tc;
                    } else if (tc.getType() == TerminationType.SINK) {
                        sinkCell = tc;
                    }
                }
            }
        }
    }

    /**
     * Creates a Map from a string representation.
     */
    public static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    /**
     * Tries to place a pipe at the given coordinate.
     */
    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    /**
     * Tries to place a pipe at the given row and col.
     */
    public boolean tryPlacePipe(int row, int col, Pipe p) {
        // Check bounds: playable area is [1..rows-2] × [1..cols-2]
        if (row < 1 || row > rows - 2 || col < 1 || col > cols - 2) {
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
        fc.setPipe(p);
        return true;
    }

    /**
     * Undoes the placement at the given coordinate.
     */
    public void undo(Coordinate coord) {
        if (coord.row >= 0 && coord.row < rows && coord.col >= 0 && coord.col < cols) {
            Cell cell = cells[coord.row][coord.col];
            if (cell instanceof FillableCell) {
                ((FillableCell) cell).setPipe(null);
            }
        }
    }

    /**
     * Marks the source cell as filled.
     */
    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    /**
     * Fills tiles within the specified distance from source using BFS.
     */
    public void fillTiles(int distance) {
        if (distance <= 0) {
            return;
        }

        // Track newly filled tiles
        Set<Coordinate> newlyFilled = new HashSet<>();

        // BFS from source
        Queue<Coordinate> queue = new LinkedList<>();
        java.util.Map<Coordinate, Integer> distMap = new HashMap<>();

        // Start from source's adjacent cell in the pointing direction
        Coordinate sourceCoord = sourceCell.getCoord();
        Direction sourceDir = sourceCell.getPointingTo();
        Coordinate startCoord = sourceCoord.add(sourceDir.getOffset());

        if (startCoord.row < 0 || startCoord.row >= rows || startCoord.col < 0 || startCoord.col >= cols) {
            prevFilledTiles = 0;
            prevFilledDistance = distance;
            return;
        }

        queue.add(startCoord);
        distMap.put(startCoord, 0);

        // Also add source as filled
        filledTiles.add(sourceCoord);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            int currentDist = distMap.get(current);

            if (currentDist >= distance) {
                continue;
            }

            Cell cell = cells[current.row][current.col];
            if (!(cell instanceof FillableCell)) {
                continue;
            }

            FillableCell fc = (FillableCell) cell;
            if (!fc.getPipe().isPresent()) {
                continue;
            }

            Pipe pipe = fc.getPipe().get();

            // Mark as filled if not already
            if (!filledTiles.contains(current)) {
                pipe.setFilled();
                filledTiles.add(current);
                newlyFilled.add(current);
            }

            // Explore connections
            Direction[] connections = pipe.getConnections();
            for (Direction dir : connections) {
                Coordinate next = current.add(dir.getOffset());
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                    continue;
                }
                if (distMap.containsKey(next)) {
                    continue;
                }

                // Check if the adjacent cell's pipe (if any) has a matching connection
                Cell nextCell = cells[next.row][next.col];
                if (nextCell instanceof FillableCell) {
                    FillableCell nextFc = (FillableCell) nextCell;
                    if (nextFc.getPipe().isPresent()) {
                        Pipe nextPipe = nextFc.getPipe().get();
                        Direction[] nextConns = nextPipe.getConnections();
                        boolean hasMatching = false;
                        Direction opposite = dir.getOpposite();
                        for (Direction nd : nextConns) {
                            if (nd == opposite) {
                                hasMatching = true;
                                break;
                            }
                        }
                        if (hasMatching) {
                            distMap.put(next, currentDist + 1);
                            queue.add(next);
                        }
                    }
                } else if (nextCell instanceof TerminationCell) {
                    // Check if we reached sink or source
                    TerminationCell tc = (TerminationCell) nextCell;
                    // Sink cell - we can reach it
                    if (tc.getType() == TerminationType.SINK) {
                        // Check if sink's pointingTo matches opposite of direction we're coming from
                        Direction sinkDir = tc.getPointingTo();
                        if (sinkDir == dir.getOpposite()) {
                            distMap.put(next, currentDist + 1);
                            queue.add(next);
                        }
                    } else if (tc.getType() == TerminationType.SOURCE) {
                        // Source - already handled
                    }
                }
            }
        }

        prevFilledTiles = newlyFilled.size();
        prevFilledDistance = distance;
    }

    /**
     * Checks if a connected path exists from SOURCE to SINK.
     */
    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }

        // BFS from source side
        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();

        // Start from source's adjacent cell in the pointing direction
        Coordinate sourceCoord = sourceCell.getCoord();
        Direction sourceDir = sourceCell.getPointingTo();
        Coordinate startCoord = sourceCoord.add(sourceDir.getOffset());

        if (startCoord.row < 0 || startCoord.row >= rows || startCoord.col < 0 || startCoord.col >= cols) {
            return false;
        }

        // Check if we can step from source to the adjacent cell
        Cell startCell = cells[startCoord.row][startCoord.col];

        // If adjacent cell is sink, check if directions match
        if (startCell instanceof TerminationCell) {
            TerminationCell tc = (TerminationCell) startCell;
            if (tc.getType() == TerminationType.SINK) {
                Direction sinkDir = tc.getPointingTo();
                if (sinkDir == sourceDir.getOpposite()) {
                    return true;
                }
            }
            return false;
        }

        if (!(startCell instanceof FillableCell)) {
            return false;
        }

        FillableCell startFc = (FillableCell) startCell;
        if (!startFc.getPipe().isPresent()) {
            return false;
        }

        // Check if source pipe connects to the first cell
        Pipe startPipe = startFc.getPipe().get();
        Direction[] startConns = startPipe.getConnections();
        boolean sourceConnected = false;
        for (Direction d : startConns) {
            if (d == sourceDir.getOpposite()) {
                sourceConnected = true;
                break;
            }
        }
        if (!sourceConnected) {
            return false;
        }

        visited.add(startCoord);
        queue.add(startCoord);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            Cell cell = cells[current.row][current.col];

            if (cell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) cell;
                if (tc.getType() == TerminationType.SINK) {
                    return true;
                }
                continue;
            }

            if (!(cell instanceof FillableCell)) {
                continue;
            }

            FillableCell fc = (FillableCell) cell;
            if (!fc.getPipe().isPresent()) {
                continue;
            }

            Pipe pipe = fc.getPipe().get();
            Direction[] connections = pipe.getConnections();

            for (Direction dir : connections) {
                Coordinate next = current.add(dir.getOffset());
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                    continue;
                }
                if (visited.contains(next)) {
                    continue;
                }

                Cell nextCell = cells[next.row][next.col];

                if (nextCell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) nextCell;
                    // Check if sink's direction matches
                    if (tc.getType() == TerminationType.SINK) {
                        Direction sinkDir = tc.getPointingTo();
                        if (sinkDir == dir.getOpposite()) {
                            return true;
                        }
                    }
                    continue;
                }

                if (!(nextCell instanceof FillableCell)) {
                    continue;
                }

                FillableCell nextFc = (FillableCell) nextCell;
                if (!nextFc.getPipe().isPresent()) {
                    continue;
                }

                Pipe nextPipe = nextFc.getPipe().get();
                Direction[] nextConns = nextPipe.getConnections();
                boolean hasMatching = false;
                Direction opposite = dir.getOpposite();
                for (Direction nd : nextConns) {
                    if (nd == opposite) {
                        hasMatching = true;
                        break;
                    }
                }

                if (hasMatching) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }

        return false;
    }

    /**
     * Returns true if the player has lost (no new tiles filled in previous round).
     */
    public boolean hasLost() {
        return prevFilledDistance != null && prevFilledDistance > 0 && prevFilledTiles == 0;
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
}
