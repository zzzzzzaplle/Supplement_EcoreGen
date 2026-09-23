import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

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

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cells[r][c];
                    if (tc.getType() == TerminationType.SOURCE) {
                        sourceCell = tc;
                    } else {
                        sinkCell = tc;
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

    public boolean tryPlacePipe(int row, int col, Pipe p) {
        if (row < 1 || row > rows - 2 || col < 1 || col > cols - 2) {
            return false;
        }
        if (!(cells[row][col] instanceof FillableCell)) {
            return false;
        }
        FillableCell cell = (FillableCell) cells[row][col];
        if (cell.getPipe().isPresent()) {
            return false;
        }
        cell.setPipe(p);
        return true;
    }

    public void undo(Coordinate coord) {
        if (cells[coord.row][coord.col] instanceof FillableCell) {
            FillableCell cell = (FillableCell) cells[coord.row][coord.col];
            cell.setPipe(null);
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        int newlyFilled = 0;
        Coordinate sourceCoord = sourceCell.getCoord();
        Direction sourceDir = sourceCell.getPointingTo();
        Coordinate firstStep = sourceCoord.add(sourceDir.getOffset());

        Queue<Coordinate> queue = new LinkedList<>();
        Queue<Integer> distQueue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();

        visited.add(sourceCoord);
        queue.add(firstStep);
        distQueue.add(1);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            Integer currentDist = distQueue.poll();

            if (currentDist > distance) {
                continue;
            }

            if (current.row < 0 || current.row >= rows || current.col < 0 || current.col >= cols) {
                continue;
            }

            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            if (!(cells[current.row][current.col] instanceof FillableCell)) {
                continue;
            }

            FillableCell cell = (FillableCell) cells[current.row][current.col];
            if (!cell.getPipe().isPresent()) {
                continue;
            }

            Pipe pipe = cell.getPipe().get();
            if (!pipe.getFilled()) {
                pipe.setFilled();
                filledTiles.add(current);
                newlyFilled++;
            }

            for (Direction dir : pipe.getConnections()) {
                Coordinate next = current.add(dir.getOffset());
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                    continue;
                }
                if (visited.contains(next)) {
                    continue;
                }

                if (cells[next.row][next.col] instanceof FillableCell) {
                    FillableCell nextCell = (FillableCell) cells[next.row][next.col];
                    if (nextCell.getPipe().isPresent()) {
                        Pipe nextPipe = nextCell.getPipe().get();
                        Direction opposite = dir.getOpposite();
                        boolean hasMatching = false;
                        for (Direction conn : nextPipe.getConnections()) {
                            if (conn == opposite) {
                                hasMatching = true;
                                break;
                            }
                        }
                        if (hasMatching) {
                            queue.add(next);
                            distQueue.add(currentDist + 1);
                        }
                    }
                } else if (cells[next.row][next.col] instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cells[next.row][next.col];
                    Direction opposite = dir.getOpposite();
                    if (tc.getPointingTo() == opposite) {
                        queue.add(next);
                        distQueue.add(currentDist + 1);
                    }
                }
            }
        }

        prevFilledTiles = newlyFilled;
        prevFilledDistance = distance;
    }

    public boolean checkPath() {
        Coordinate sourceCoord = sourceCell.getCoord();
        Direction sourceDir = sourceCell.getPointingTo();
        Coordinate start = sourceCoord.add(sourceDir.getOffset());

        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        visited.add(sourceCoord);
        queue.add(start);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();

            if (current.row < 0 || current.row >= rows || current.col < 0 || current.col >= cols) {
                continue;
            }

            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            if (cells[current.row][current.col] instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) cells[current.row][current.col];
                if (tc.getType() == TerminationType.SINK) {
                    return true;
                }
                continue;
            }

            if (!(cells[current.row][current.col] instanceof FillableCell)) {
                continue;
            }

            FillableCell cell = (FillableCell) cells[current.row][current.col];
            if (!cell.getPipe().isPresent()) {
                continue;
            }

            Pipe pipe = cell.getPipe().get();
            for (Direction dir : pipe.getConnections()) {
                Coordinate next = current.add(dir.getOffset());
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                    continue;
                }
                if (visited.contains(next)) {
                    continue;
                }

                if (cells[next.row][next.col] instanceof FillableCell) {
                    FillableCell nextCell = (FillableCell) cells[next.row][next.col];
                    if (nextCell.getPipe().isPresent()) {
                        Pipe nextPipe = nextCell.getPipe().get();
                        Direction opposite = dir.getOpposite();
                        for (Direction conn : nextPipe.getConnections()) {
                            if (conn == opposite) {
                                queue.add(next);
                                break;
                            }
                        }
                    }
                } else if (cells[next.row][next.col] instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cells[next.row][next.col];
                    Direction opposite = dir.getOpposite();
                    if (tc.getPointingTo() == opposite) {
                        queue.add(next);
                    }
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
}
