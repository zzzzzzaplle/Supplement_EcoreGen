import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Map holding the cell grid and water flow state.
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
        this.rows = 0;
        this.cols = 0;
        this.cells = new Cell[0][0];
        this.sourceCell = null;
        this.sinkCell = null;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = null;
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = null;
        // Locate source and sink cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cells[r][c];
                    if (tc.getType() == TerminationType.SOURCE) {
                        this.sourceCell = tc;
                    } else if (tc.getType() == TerminationType.SINK) {
                        this.sinkCell = tc;
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

    public boolean tryPlacePipe(int row, int col, Pipe pipe) {
        // Reject if row/col outside [1..rows-2] x [1..cols-2]
        if (row < 1 || row > rows - 2 || col < 1 || col > cols - 2) {
            return false;
        }
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
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
        Cell cell = cells[coord.row][coord.col];
        if (cell instanceof FillableCell) {
            FillableCell fc = (FillableCell) cell;
            fc.setPipe(null);
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
        filledTiles.add(sourceCell.getCoord());
    }

    public void fillTiles(int distance) {
        if (distance <= 0) {
            return;
        }
        // BFS expansion from currently filled tiles
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        Set<Coordinate> newlyFilled = new HashSet<>();

        // Start from already filled tiles
        for (Coordinate c : filledTiles) {
            queue.add(c);
            visited.add(c);
        }

        int currentDist = 0;
        while (!queue.isEmpty() && currentDist < distance) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Coordinate cur = queue.poll();
                Cell curCell = cells[cur.row][cur.col];
                Direction[] dirs = null;
                if (curCell instanceof FillableCell) {
                    FillableCell fc = (FillableCell) curCell;
                    if (fc.getPipe().isPresent()) {
                        dirs = fc.getPipe().get().getConnections();
                    } else {
                        continue;
                    }
                } else if (curCell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) curCell;
                    if (tc.getType() == TerminationType.SOURCE) {
                        dirs = new Direction[]{tc.getPointingTo()};
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }

                for (Direction d : dirs) {
                    Coordinate offset = d.getOffset();
                    Coordinate next = cur.add(offset);
                    if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                        continue;
                    }
                    if (visited.contains(next)) {
                        continue;
                    }
                    Cell nextCell = cells[next.row][next.col];
                    if (nextCell instanceof FillableCell) {
                        FillableCell nfc = (FillableCell) nextCell;
                        if (nfc.getPipe().isPresent()) {
                            Pipe np = nfc.getPipe().get();
                            Direction[] ndirs = np.getConnections();
                            Direction needed = d.getOpposite();
                            boolean hasMatch = false;
                            for (Direction nd : ndirs) {
                                if (nd == needed) {
                                    hasMatch = true;
                                    break;
                                }
                            }
                            if (hasMatch) {
                                np.setFilled();
                                filledTiles.add(next);
                                newlyFilled.add(next);
                                visited.add(next);
                                queue.add(next);
                            }
                        }
                    } else if (nextCell instanceof TerminationCell) {
                        TerminationCell ntc = (TerminationCell) nextCell;
                        if (ntc.getType() == TerminationType.SINK) {
                            Direction needed = d.getOpposite();
                            if (ntc.getPointingTo() == needed) {
                                ntc.setFilled();
                                filledTiles.add(next);
                                newlyFilled.add(next);
                                visited.add(next);
                                queue.add(next);
                            }
                        }
                    }
                }
            }
            currentDist++;
        }

        prevFilledTiles = newlyFilled.size();
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        Coordinate start = sourceCell.getCoord();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Coordinate cur = queue.poll();
            if (cur.equals(sinkCell.getCoord())) {
                return true;
            }
            Cell curCell = cells[cur.row][cur.col];
            Direction[] dirs = null;
            if (curCell instanceof FillableCell) {
                FillableCell fc = (FillableCell) curCell;
                if (fc.getPipe().isPresent()) {
                    dirs = fc.getPipe().get().getConnections();
                } else {
                    continue;
                }
            } else if (curCell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) curCell;
                if (tc == sourceCell) {
                    dirs = new Direction[]{tc.getPointingTo()};
                } else {
                    continue;
                }
            } else {
                continue;
            }

            for (Direction d : dirs) {
                Coordinate offset = d.getOffset();
                Coordinate next = cur.add(offset);
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                    continue;
                }
                if (visited.contains(next)) {
                    continue;
                }
                Cell nextCell = cells[next.row][next.col];
                if (nextCell instanceof FillableCell) {
                    FillableCell nfc = (FillableCell) nextCell;
                    if (nfc.getPipe().isPresent()) {
                        Direction needed = d.getOpposite();
                        Direction[] ndirs = nfc.getPipe().get().getConnections();
                        for (Direction nd : ndirs) {
                            if (nd == needed) {
                                visited.add(next);
                                queue.add(next);
                                break;
                            }
                        }
                    }
                } else if (nextCell instanceof TerminationCell) {
                    TerminationCell ntc = (TerminationCell) nextCell;
                    if (ntc == sinkCell) {
                        Direction needed = d.getOpposite();
                        if (ntc.getPointingTo() == needed) {
                            return true;
                        }
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
