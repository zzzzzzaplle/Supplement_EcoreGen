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
    private Set<String> filledTiles;
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

    public Set<String> getFilledTiles() {
        return filledTiles;
    }

    public void setFilledTiles(Set<String> filledTiles) {
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

    public boolean tryPlacePipe(int row, int col, Pipe pipe) {
        if (row < 1 || row > rows - 2 || col < 1 || col > cols - 2) {
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

    public void undo(Coordinate coord) {
        Cell cell = cells[coord.row][coord.col];
        if (cell instanceof FillableCell) {
            FillableCell fc = (FillableCell) cell;
            fc.setPipe(null);
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        if (prevFilledDistance != null && distance <= prevFilledDistance) {
            return;
        }

        int newFilled = 0;

        Queue<Coordinate> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        Coordinate startCoord = sourceCell.getCoord();
        Direction startDir = sourceCell.getPointingTo();
        Coordinate nextCoord = startCoord.add(startDir.getOffset());
        String startKey = nextCoord.row + "," + nextCoord.col;
        queue.add(nextCoord);
        visited.add(startKey);

        boolean[][] reachable = new boolean[rows][cols];
        reachable[nextCoord.row][nextCoord.col] = true;

        while (!queue.isEmpty()) {
            Coordinate cur = queue.poll();
            Cell curCell = cells[cur.row][cur.col];

            if (!(curCell instanceof FillableCell)) {
                continue;
            }
            FillableCell fc = (FillableCell) curCell;
            Pipe pipe = fc.getPipe();
            if (pipe == null) {
                continue;
            }

            Direction[] connections = pipe.getConnections();
            if (prevFilledDistance != null) {
                int curDist = Math.abs(cur.row - nextCoord.row) + Math.abs(cur.col - nextCoord.col);
                if (curDist >= distance) {
                    continue;
                }
            }

            if (prevFilledDistance == null) {
                int curDist = Math.abs(cur.row - nextCoord.row) + Math.abs(cur.col - nextCoord.col);
                if (curDist >= distance) {
                    continue;
                }
            }

            if (!pipe.getFilled()) {
                pipe.setFilled();
                filledTiles.add(cur.row + "," + cur.col);
                newFilled++;
            }

            for (Direction dir : connections) {
                Coordinate neighbor = cur.add(dir.getOffset());
                String key = neighbor.row + "," + neighbor.col;
                if (visited.contains(key)) {
                    continue;
                }
                if (neighbor.row < 0 || neighbor.row >= rows || neighbor.col < 0 || neighbor.col >= cols) {
                    continue;
                }
                Cell neighborCell = cells[neighbor.row][neighbor.col];
                if (neighborCell instanceof FillableCell) {
                    FillableCell nfc = (FillableCell) neighborCell;
                    Pipe np = nfc.getPipe();
                    if (np == null) {
                        continue;
                    }
                    Direction[] neighborConns = np.getConnections();
                    boolean connected = false;
                    for (Direction nd : neighborConns) {
                        if (nd == dir.getOpposite()) {
                            connected = true;
                            break;
                        }
                    }
                    if (connected) {
                        visited.add(key);
                        reachable[neighbor.row][neighbor.col] = true;
                        queue.add(neighbor);
                    }
                }
            }
        }

        prevFilledTiles = newFilled;
        prevFilledDistance = distance;
    }

    public boolean checkPath() {
        Queue<Coordinate> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        Direction startDir = sourceCell.getPointingTo();
        Coordinate startCoord = sourceCell.getCoord().add(startDir.getOffset());
        String startKey = startCoord.row + "," + startCoord.col;
        queue.add(startCoord);
        visited.add(startKey);

        while (!queue.isEmpty()) {
            Coordinate cur = queue.poll();

            if (cur.row < 0 || cur.row >= rows || cur.col < 0 || cur.col >= cols) {
                continue;
            }

            Cell curCell = cells[cur.row][cur.col];
            if (curCell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) curCell;
                if (tc.getType() == TerminationType.SINK) {
                    return true;
                }
            }

            if (curCell instanceof FillableCell) {
                FillableCell fc = (FillableCell) curCell;
                Pipe pipe = fc.getPipe();
                if (pipe == null) {
                    continue;
                }
                Direction[] connections = pipe.getConnections();
                for (Direction dir : connections) {
                    Coordinate neighbor = cur.add(dir.getOffset());
                    String key = neighbor.row + "," + neighbor.col;
                    if (visited.contains(key)) {
                        continue;
                    }
                    if (neighbor.row < 0 || neighbor.row >= rows || neighbor.col < 0 || neighbor.col >= cols) {
                        continue;
                    }
                    Cell neighborCell = cells[neighbor.row][neighbor.col];
                    if (neighborCell instanceof FillableCell) {
                        FillableCell nfc = (FillableCell) neighborCell;
                        Pipe np = nfc.getPipe();
                        if (np == null) {
                            continue;
                        }
                        Direction[] neighborConns = np.getConnections();
                        boolean connected = false;
                        for (Direction nd : neighborConns) {
                            if (nd == dir.getOpposite()) {
                                connected = true;
                                break;
                            }
                        }
                        if (connected) {
                            visited.add(key);
                            queue.add(neighbor);
                        }
                    } else if (neighborCell instanceof TerminationCell) {
                        visited.add(key);
                        queue.add(neighbor);
                    }
                }
            }
        }

        return false;
    }

    public boolean hasLost() {
        return prevFilledTiles == 0;
    }
}
