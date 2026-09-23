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
                    } else if (tc.getType() == TerminationType.SINK) {
                        sinkCell = tc;
                    }
                }
            }
        }
    }

    static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(int row, int col, Pipe pipe) {
        if (row <= 0 || row >= rows - 1 || col <= 0 || col >= cols - 1) {
            return false;
        }
        if (!(cells[row][col] instanceof FillableCell)) {
            return false;
        }
        FillableCell fc = (FillableCell) cells[row][col];
        if (fc.getPipe().isPresent()) {
            return false;
        }
        fc.setPipe(pipe);
        return true;
    }

    public boolean tryPlacePipe(Coordinate coord, Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public void undo(Coordinate coord) {
        if (coord.row >= 0 && coord.row < rows && coord.col >= 0 && coord.col < cols) {
            if (cells[coord.row][coord.col] instanceof FillableCell) {
                ((FillableCell) cells[coord.row][coord.col]).setPipe(null);
            }
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        if (distance <= 0) {
            return;
        }

        Queue<Coordinate> queue = new LinkedList<>();
        boolean[][] visited = new boolean[rows][cols];

        Coordinate startCoord = sourceCell.getCoord();
        Direction startDir = sourceCell.getPointingTo();
        Coordinate nextCoord = startCoord.add(startDir.getOffset());

        if (nextCoord.row >= 0 && nextCoord.row < rows && nextCoord.col >= 0 && nextCoord.col < cols) {
            queue.add(nextCoord);
            visited[nextCoord.row][nextCoord.col] = true;
        }

        int currentDist = 0;
        int tilesFilledThisRound = 0;

        while (!queue.isEmpty() && currentDist < distance) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Coordinate current = queue.poll();
                if (cells[current.row][current.col] instanceof FillableCell) {
                    FillableCell fc = (FillableCell) cells[current.row][current.col];
                    if (fc.getPipe().isPresent()) {
                        Pipe pipe = fc.getPipe().get();
                        if (!pipe.getFilled()) {
                            pipe.setFilled();
                            filledTiles.add(current);
                            tilesFilledThisRound++;
                        }
                        for (Direction dir : pipe.getConnections()) {
                            Coordinate neighbor = current.add(dir.getOffset());
                            if (neighbor.row >= 0 && neighbor.row < rows && neighbor.col >= 0 && neighbor.col < cols && !visited[neighbor.row][neighbor.col]) {
                                if (cells[neighbor.row][neighbor.col] instanceof FillableCell) {
                                    FillableCell nfc = (FillableCell) cells[neighbor.row][neighbor.col];
                                    if (nfc.getPipe().isPresent()) {
                                        Pipe np = nfc.getPipe().get();
                                        if (hasMatchingConnection(pipe, dir, np, neighbor, current)) {
                                            visited[neighbor.row][neighbor.col] = true;
                                            queue.add(neighbor);
                                        }
                                    }
                                } else if (cells[neighbor.row][neighbor.col] instanceof TerminationCell) {
                                    TerminationCell tc = (TerminationCell) cells[neighbor.row][neighbor.col];
                                    if (tc.getType() == TerminationType.SINK) {
                                        visited[neighbor.row][neighbor.col] = true;
                                        queue.add(neighbor);
                                    }
                                }
                            }
                        }
                    }
                } else if (cells[current.row][current.col] instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cells[current.row][current.col];
                    if (tc.getType() == TerminationType.SINK) {
                        continue;
                    }
                }
            }
            currentDist++;
            if (currentDist >= distance) {
                break;
            }
        }

        prevFilledTiles = tilesFilledThisRound;
        prevFilledDistance = distance;
    }

    private boolean hasMatchingConnection(Pipe currentPipe, Direction fromDir, Pipe neighborPipe, Coordinate neighborCoord, Coordinate currentCoord) {
        Direction opposite = fromDir.getOpposite();
        for (Direction nd : neighborPipe.getConnections()) {
            if (nd == opposite) {
                return true;
            }
        }
        return false;
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }

        boolean[][] visited = new boolean[rows][cols];
        Queue<Coordinate> queue = new LinkedList<>();

        Coordinate startCoord = sourceCell.getCoord();
        visited[startCoord.row][startCoord.col] = true;
        Direction startDir = sourceCell.getPointingTo();
        Coordinate nextCoord = startCoord.add(startDir.getOffset());

        if (nextCoord.row >= 0 && nextCoord.row < rows && nextCoord.col >= 0 && nextCoord.col < cols) {
            queue.add(nextCoord);
            visited[nextCoord.row][nextCoord.col] = true;
        }

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();

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

            FillableCell fc = (FillableCell) cells[current.row][current.col];
            if (!fc.getPipe().isPresent()) {
                continue;
            }

            Pipe pipe = fc.getPipe().get();
            for (Direction dir : pipe.getConnections()) {
                Coordinate neighbor = current.add(dir.getOffset());
                if (neighbor.row >= 0 && neighbor.row < rows && neighbor.col >= 0 && neighbor.col < cols && !visited[neighbor.row][neighbor.col]) {
                    if (cells[neighbor.row][neighbor.col] instanceof FillableCell) {
                        FillableCell nfc = (FillableCell) cells[neighbor.row][neighbor.col];
                        if (nfc.getPipe().isPresent()) {
                            Pipe np = nfc.getPipe().get();
                            if (hasMatchingConnection(pipe, dir, np, neighbor, current)) {
                                visited[neighbor.row][neighbor.col] = true;
                                queue.add(neighbor);
                            }
                        }
                    } else if (cells[neighbor.row][neighbor.col] instanceof TerminationCell) {
                        TerminationCell tc = (TerminationCell) cells[neighbor.row][neighbor.col];
                        if (tc.getType() == TerminationType.SINK) {
                            return true;
                        }
                        visited[neighbor.row][neighbor.col] = true;
                        queue.add(neighbor);
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
