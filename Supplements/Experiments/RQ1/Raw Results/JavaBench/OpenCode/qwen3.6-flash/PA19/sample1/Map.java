import java.util.*;

/**
 * Manages the game map with cells, pipes, water flow, and win/loss tracking.
 */
public class Map {

    private  int rows;
    private  int cols;
    public  Cell[][] cells;
    private  TerminationCell sourceCell;
    private  TerminationCell sinkCell;
    private  Set<MapTile> filledTiles;
    private  int prevFilledTiles;
    private  Integer prevFilledDistance;

    public Map() {
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = null;
        identifyTerminationCells();
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

    public Set<MapTile> getFilledTiles() {
        return filledTiles;
    }

    public void setFilledTiles(Set<MapTile> filledTiles) {
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

    /**
     * Identifies source and sink termination cells in the map grid.
     */
    private void identifyTerminationCells() {
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
        if (sourceCell == null || sinkCell == null) {
            throw new IllegalStateException("Map must have one SOURCE and one SINK cell.");
        }
    }

    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    /**
     * Attempts to place a pipe at the specified coordinates.
     * Rejects if: (1) row/col outside playable area [1..rows-2] x [1..cols-2],
     * (2) target cell is non-FillableCell,
     * (3) cell is already occupied.
     */
    public boolean tryPlacePipe(int row, int col, Pipe pipe) {
        if (row < 1 || row >= rows - 1 || col < 1 || col >= cols - 1) {
            return false;
        }
        Cell cell = cells[row][col];
        if (!(cell instanceof FillableCell)) {
            return false;
        }
        FillableCell fc = (FillableCell) cell;
        if (fc.getPipe() != null) {
            return false;
        }
        fc.setPipe(pipe);
        return true;
    }

    /**
     * Removes the pipe from the specified cell, restoring it to empty.
     */
    public void undo(Coordinate coord) {
        if (coord.row >= 0 && coord.row < rows && coord.col >= 0 && coord.col < cols) {
            Cell cell = cells[coord.row][coord.col];
            if (cell instanceof FillableCell) {
                FillableCell fillable = (FillableCell) cell;
                fillable.setPipe(null);
            }
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    /**
     * Fills pipes within the specified distance from source using BFS-style expansion.
     * Water propagates along pipes only when adjacent pipes have matching connection directions.
     */
    public void fillTiles(int distance) {
        int prevFilledCount = filledTiles.size();
        filledTiles.clear();

        if (sourceCell == null || !sourceCell.isFilled()) {
            prevFilledTiles = filledTiles.size() - prevFilledCount;
            prevFilledDistance = distance;
            return;
        }

        if (distance <= 0) {
            prevFilledTiles = filledTiles.size() - prevFilledCount;
            prevFilledDistance = distance;
            return;
        }

        MapTile sourceTile = new MapTile(sourceCell);
        filledTiles.add(sourceTile);

        Queue<MapTile> queue = new LinkedList<>();
        queue.add(sourceTile);

        for (int d = 1; d <= distance; d++) {
            List<MapTile> frontier = new ArrayList<>();

            while (!queue.isEmpty()) {
                MapTile current = queue.poll();
                Cell cell = current.cell;

                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    Direction pointingTo = tc.getPointingTo();
                    Coordinate nextCoord = cell.coord.add(pointingTo.getOffset());
                    tryAddNeighbor(nextCoord, d, frontier, queue);
                } else if (cell instanceof FillableCell) {
                    FillableCell fc = (FillableCell) cell;
                    Pipe pipe = fc.getPipe();
                    if (pipe != null && pipe.getFilled()) {
                        for (Direction dir : pipe.getConnections()) {
                            Coordinate nextCoord = cell.coord.add(dir.getOffset());
                            tryAddNeighbor(nextCoord, d, frontier, queue);
                        }
                    }
                }
            }

            for (MapTile tile : frontier) {
                queue.add(tile);
            }
        }

        prevFilledTiles = filledTiles.size() - prevFilledCount;
        prevFilledDistance = distance;
    }

    private void tryAddNeighbor(Coordinate nextCoord, int d, List<MapTile> frontier, Queue<MapTile> queue) {
        if (nextCoord.row < 0 || nextCoord.row >= rows || nextCoord.col < 0 || nextCoord.col >= cols) {
            return;
        }
        Cell nextCell = cells[nextCoord.row][nextCoord.col];
        if (nextCell instanceof FillableCell) {
            FillableCell nextFillable = (FillableCell) nextCell;
            Pipe pipe = nextFillable.getPipe();
            if (pipe != null && pipe.getFilled()) {
                MapTile tile = new MapTile(nextCell, d);
                if (!filledTiles.contains(tile)) {
                    filledTiles.add(tile);
                    frontier.add(tile);
                }
            }
        } else if (nextCell instanceof TerminationCell) {
            MapTile tile = new MapTile(nextCell, d);
            if (!filledTiles.contains(tile)) {
                filledTiles.add(tile);
                frontier.add(tile);
            }
        }
    }

    /**
     * Uses BFS to determine if a connected path exists from SOURCE to SINK.
     * Does not require the traversed pipes to be filled; reaching the SINK cell implies win.
     */
    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }

        Set<Coordinate> visited = new HashSet<>();
        Queue<PathRecord> queue = new LinkedList<>();

        queue.add(new PathRecord(sourceCell, ((TerminationCell) sourceCell).getPointingTo()));
        visited.add(sourceCell.coord);

        while (!queue.isEmpty()) {
            PathRecord current = queue.poll();
            Cell cell = current.cell;
            Direction fromDir = current.fromDir;

            if (cell instanceof TerminationCell && ((TerminationCell) cell).getType() == TerminationType.SINK) {
                return true;
            }

            Direction[] directions;
            if (cell == sourceCell) {
                directions = new Direction[]{((TerminationCell) cell).getPointingTo()};
            } else if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                Pipe pipe = fc.getPipe();
                if (pipe == null) {
                    continue;
                }
                directions = pipe.getConnections();
            } else {
                continue;
            }

            if (fromDir != null) {
                if (cell instanceof FillableCell) {
                    FillableCell fc = (FillableCell) cell;
                    Pipe pipe = fc.getPipe();
                    if (pipe != null) {
                        Direction[] connections = pipe.getConnections();
                        if (ArrayUtils.contains(connections, fromDir.getOpposite())) {
                            continue;
                        }
                    }
                }
            }

            for (Direction dir : directions) {
                if (fromDir != null && fromDir.getOpposite().equals(dir)) {
                    continue;
                }
                Coordinate nextCoord = cell.coord.add(dir.getOffset());
                if (nextCoord.row < 0 || nextCoord.row >= rows || nextCoord.col < 0 || nextCoord.col >= cols) {
                    continue;
                }
                if (visited.contains(nextCoord)) {
                    continue;
                }
                Cell nextCell = cells[nextCoord.row][nextCoord.col];
                if (nextCell instanceof Wall) {
                    continue;
                }

                if (nextCell instanceof TerminationCell) {
                    queue.add(new PathRecord(nextCell, dir));
                    visited.add(nextCoord);
                    continue;
                }

                if (nextCell instanceof FillableCell) {
                    FillableCell nextFillable = (FillableCell) nextCell;
                    Pipe pipe = nextFillable.getPipe();
                    if (pipe != null) {
                        Direction[] connections = pipe.getConnections();
                        for (Direction conn : connections) {
                            if (conn == dir.getOpposite()) {
                                queue.add(new PathRecord(nextCell, dir));
                                visited.add(nextCoord);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns true when prevFilledTiles == 0 and delay has ended.
     */
    public boolean hasLost() {
        if (prevFilledDistance == null || prevFilledDistance <= 0) {
            return false;
        }
        return prevFilledTiles == 0;
    }

    /**
     * Lightweight record for tracking which cell was filled at what distance.
     */
    private static class MapTile extends Coordinate {
        Cell cell;
        int distance;

        MapTile(Cell cell) {
            this(cell, 0);
        }

        MapTile(Cell cell, int distance) {
            super(cell.coord.row, cell.coord.col);
            this.cell = cell;
            this.distance = distance;
        }

        MapTile() {
            super();
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            MapTile that = (MapTile) obj;
            return row == that.row && col == that.col;
        }
    }

    /**
     * BFS record for path checking.
     */
    private static class PathRecord {
        Cell cell;
        Direction fromDir;

        PathRecord(Cell cell, Direction fromDir) {
            this.cell = cell;
            this.fromDir = fromDir;
        }
    }

    private int getDirectionIndex(Direction dir) {
        switch (dir) {
            case UP: return 0;
            case DOWN: return 1;
            case LEFT: return 2;
            case RIGHT: return 3;
            default: return -1;
        }
    }
}
