import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Represents the game map containing cells, source, and sink.
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
     * Attempts to place a pipe at the given coordinate.
     */
    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    /**
     * Attempts to place a pipe at the given row and column.
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
     * Undoes a pipe placement at the given coordinate.
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
        // If distance has not increased, no new tiles to fill
        if (prevFilledDistance != null && distance <= prevFilledDistance) {
            return;
        }

        Set<Coordinate> newlyFilled = new HashSet<>();

        // BFS from source
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        Queue<Integer> distQueue = new LinkedList<>();

        // Start from the cell in the direction the source points to
        Coordinate sourceCoord = sourceCell.getCoord();
        Coordinate startCoord = sourceCoord.add(sourceCell.getPointingTo().getOffset());

        // Check if start coordinate is within bounds
        if (startCoord.row >= 0 && startCoord.row < rows && startCoord.col >= 0 && startCoord.col < cols) {
            queue.add(startCoord);
            distQueue.add(1);
            visited.add(startCoord);
        }

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            int currentDist = distQueue.poll();

            Cell cell = cells[current.row][current.col];

            if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                if (fc.getPipe().isPresent()) {
                    Pipe pipe = fc.getPipe().get();
                    if (currentDist <= distance) {
                        if (!pipe.getFilled()) {
                            pipe.setFilled();
                            newlyFilled.add(current);
                            filledTiles.add(current);
                        }

                        // Expand to connected cells
                        if (currentDist < distance) {
                            for (Direction dir : pipe.getConnections()) {
                                Coordinate nextCoord = current.add(dir.getOffset());
                                if (nextCoord.row >= 0 && nextCoord.row < rows && nextCoord.col >= 0 && nextCoord.col < cols) {
                                    if (!visited.contains(nextCoord)) {
                                        Cell nextCell = cells[nextCoord.row][nextCoord.col];
                                        if (nextCell instanceof FillableCell) {
                                            FillableCell nextFc = (FillableCell) nextCell;
                                            if (nextFc.getPipe().isPresent()) {
                                                Pipe nextPipe = nextFc.getPipe().get();
                                                // Check if connection directions match
                                                Direction opposite = dir.getOpposite();
                                                for (Direction nextDir : nextPipe.getConnections()) {
                                                    if (nextDir == opposite) {
                                                        visited.add(nextCoord);
                                                        queue.add(nextCoord);
                                                        distQueue.add(currentDist + 1);
                                                        break;
                                                    }
                                                }
                                            }
                                        } else if (nextCell instanceof TerminationCell) {
                                            // Can step into termination cell
                                            visited.add(nextCoord);
                                            queue.add(nextCoord);
                                            distQueue.add(currentDist + 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (cell instanceof TerminationCell) {
                // Check if we reached sink
                TerminationCell tc = (TerminationCell) cell;
                if (tc.getType() == TerminationType.SINK) {
                    // Water can flow into sink
                    if (currentDist <= distance) {
                        tc.setFilled();
                        newlyFilled.add(current);
                        filledTiles.add(current);
                    }
                } else if (tc.getType() == TerminationType.SOURCE) {
                    // Skip source
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
        // BFS from source to sink
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();

        Coordinate sourceCoord = sourceCell.getCoord();
        Coordinate startCoord = sourceCoord.add(sourceCell.getPointingTo().getOffset());

        // Check bounds
        if (startCoord.row < 0 || startCoord.row >= rows || startCoord.col < 0 || startCoord.col >= cols) {
            return false;
        }

        queue.add(startCoord);
        visited.add(startCoord);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            Cell cell = cells[current.row][current.col];

            if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                if (fc.getPipe().isPresent()) {
                    Pipe pipe = fc.getPipe().get();
                    for (Direction dir : pipe.getConnections()) {
                        Coordinate nextCoord = current.add(dir.getOffset());
                        if (nextCoord.row >= 0 && nextCoord.row < rows && nextCoord.col >= 0 && nextCoord.col < cols) {
                            if (!visited.contains(nextCoord)) {
                                Cell nextCell = cells[nextCoord.row][nextCoord.col];
                                if (nextCell instanceof FillableCell) {
                                    FillableCell nextFc = (FillableCell) nextCell;
                                    if (nextFc.getPipe().isPresent()) {
                                        Pipe nextPipe = nextFc.getPipe().get();
                                        Direction opposite = dir.getOpposite();
                                        for (Direction nextDir : nextPipe.getConnections()) {
                                            if (nextDir == opposite) {
                                                visited.add(nextCoord);
                                                queue.add(nextCoord);
                                                break;
                                            }
                                        }
                                    }
                                } else if (nextCell instanceof TerminationCell) {
                                    TerminationCell tc = (TerminationCell) nextCell;
                                    if (tc.getType() == TerminationType.SINK) {
                                        return true;
                                    }
                                    visited.add(nextCoord);
                                    queue.add(nextCoord);
                                }
                            }
                        }
                    }
                }
            } else if (cell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) cell;
                if (tc.getType() == TerminationType.SINK) {
                    return true;
                } else if (tc.getType() == TerminationType.SOURCE) {
                    continue;
                }
            }
        }

        return false;
    }

    /**
     * Returns true if no new pipes were filled in the previous round.
     */
    public boolean hasLost() {
        return prevFilledTiles == 0 && prevFilledDistance != null && prevFilledDistance > 0;
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
