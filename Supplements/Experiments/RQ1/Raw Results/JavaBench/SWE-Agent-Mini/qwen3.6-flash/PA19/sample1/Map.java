import java.util.*;

/**
 * The game map containing cells, pipes, and water flow logic.
 */
public class Map {

    private int rows;
    private int cols;
    public Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;
    private Set<FillableCell> filledTiles;
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
        this.prevFilledDistance = 0;
        findTerminationCells();
    }

    static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    private void findTerminationCells() {
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    if (tc.type == TerminationType.SOURCE) {
                        sourceCell = tc;
                    } else if (tc.type == TerminationType.SINK) {
                        sinkCell = tc;
                    }
                }
            }
        }
    }

    public boolean tryPlacePipe(Coordinate coord, Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe p) {
        // Check bounds: playable area is [1..rows-2] x [1..cols-2]
        if (row < 1 || row >= rows - 1 || col < 1 || col >= cols - 1) {
            return false;
        }

        Cell cell = cells[row][col];
        if (!(cell instanceof FillableCell)) {
            return false;
        }

        FillableCell fillableCell = (FillableCell) cell;
        // Check if already occupied
        if (fillableCell.getPipe().isPresent()) {
            return false;
        }

        fillableCell.setPipe(p);
        return true;
    }

    public void undo(Coordinate coord) {
        if (coord == null) {
            return;
        }
        int row = coord.row;
        int col = coord.col;
        Cell cell = cells[row][col];
        if (cell instanceof FillableCell) {
            FillableCell fillable = (FillableCell) cell;
            if (fillable.getPipe().isPresent()) {
                fillable.setPipe(null);
            }
        }
    }

    public void fillBeginTile() {
        if (sourceCell != null) {
            sourceCell.setFilled();
            // Also fill the first tile in the direction the source points
            Coordinate startCoord = sourceCell.coord.add(sourceCell.pointingTo.getOffset());
            if (startCoord.row >= 0 && startCoord.row < rows && startCoord.col >= 0 && startCoord.col < cols) {
                Cell startCell = cells[startCoord.row][startCoord.col];
                if (startCell instanceof FillableCell) {
                    FillableCell fillable = (FillableCell) startCell;
                    if (fillable.getPipe().isPresent()) {
                        Pipe pipe = fillable.getPipe().get();
                        // Check if this pipe connects back to the source direction
                        Direction[] conns = pipe.getConnections();
                        for (Direction d : conns) {
                            if (d == sourceCell.pointingTo.getOpposite()) {
                                pipe.setFilled();
                                filledTiles.add(fillable);
                            }
                        }
                    }
                }
            }
        }
        prevFilledTiles = filledTiles.size();
        prevFilledDistance = 0;
    }

    public void fillTiles(int distance) {
        if (distance <= 0) {
            return;
        }
        Set<FillableCell> newFilled = new HashSet<>();

        // BFS-style expansion layer by layer
        if (filledTiles.isEmpty()) {
            return;
        }

        // Build a frontier from filled tiles
        Set<FillableCell> frontier = new HashSet<>();
        frontier.addAll(filledTiles);

        for (int step = 0; step < distance; step++) {
            Set<FillableCell> nextFrontier = new HashSet<>();
            for (FillableCell fc : frontier) {
                Pipe pipe = fc.getPipe().orElse(null);
                if (pipe == null) continue;
                Direction[] conns = pipe.getConnections();
                for (Direction dir : conns) {
                    Coordinate nextCoord = fc.coord.add(dir.getOffset());
                    if (nextCoord.row >= 1 && nextCoord.row < rows - 1 &&
                        nextCoord.col >= 1 && nextCoord.col < cols - 1) {
                        Cell nextCell = cells[nextCoord.row][nextCoord.col];
                        if (nextCell instanceof FillableCell) {
                            FillableCell nextFillable = (FillableCell) nextCell;
                            Pipe nextPipe = nextFillable.getPipe().orElse(null);
                            if (nextPipe != null && !nextPipe.getFilled()) {
                                Direction[] nextConns = nextPipe.getConnections();
                                for (Direction nd : nextConns) {
                                    if (nd == dir.getOpposite()) {
                                        nextPipe.setFilled();
                                        nextFillable.setPipe(nextPipe);
                                        nextFrontier.add(nextFillable);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (nextFrontier.isEmpty()) {
                break;
            }
            filledTiles.addAll(nextFrontier);
            frontier = nextFrontier;
        }

        prevFilledTiles = filledTiles.size();
        prevFilledDistance = distance;
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }
        // BFS from source
        Set<Cell> visited = new HashSet<>();
        Queue<Cell> queue = new LinkedList<>();

        // Start from source, take one step in the pointingTo direction
        Coordinate startCoord = sourceCell.coord.add(sourceCell.pointingTo.getOffset());
        if (startCoord.row < 0 || startCoord.row >= rows || startCoord.col < 0 || startCoord.col >= cols) {
            return false;
        }
        Cell firstCell = cells[startCoord.row][startCoord.col];
        if (firstCell instanceof TerminationCell) {
            return firstCell == sinkCell;
        }
        if (firstCell instanceof FillableCell) {
            FillableCell fillable = (FillableCell) firstCell;
            Pipe pipe = fillable.getPipe().orElse(null);
            if (pipe != null) {
                // Check if pipe connects back to source
                for (Direction d : pipe.getConnections()) {
                    if (d == sourceCell.pointingTo.getOpposite()) {
                        visited.add(firstCell);
                        queue.add(firstCell);
                    }
                }
            }
        }

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            if (current instanceof TerminationCell && ((TerminationCell) current).type == TerminationType.SINK) {
                return true;
            }

            if (!(current instanceof FillableCell)) continue;
            Pipe pipe = ((FillableCell) current).getPipe().orElse(null);
            if (pipe == null) continue;

            for (Direction dir : pipe.getConnections()) {
                Coordinate nextCoord = current.coord.add(dir.getOffset());
                if (nextCoord.row < 0 || nextCoord.row >= rows || nextCoord.col < 0 || nextCoord.col >= cols) {
                    continue;
                }

                if (visited.contains(cells[nextCoord.row][nextCoord.col])) continue;

                Cell nextCell = cells[nextCoord.row][nextCoord.col];
                if (nextCell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) nextCell;
                    if (tc.type == TerminationType.SINK) {
                        return true;
                    }
                }

                if (nextCell instanceof FillableCell) {
                    ((FillableCell) nextCell).getPipe().ifPresent(p -> {
                        Direction[] conns = p.getConnections();
                        for (Direction nd : conns) {
                            if (nd == dir.getOpposite()) {
                                visited.add(nextCell);
                                queue.add(nextCell);
                            }
                        }
                    });
                }
            }
        }

        return false;
    }

    public boolean hasLost() {
        if (prevFilledDistance == null || prevFilledDistance <= 0) {
            return false;
        }
        return prevFilledTiles == 0;
    }

    public int getFilledTiles() {
        return filledTiles.size();
    }

    public TerminationCell getSourceCell() {
        return sourceCell;
    }

    public TerminationCell getSinkCell() {
        return sinkCell;
    }
}
