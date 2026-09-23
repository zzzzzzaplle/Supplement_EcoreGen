import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * The grid map containing cells, source and sink.
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
        this.sourceCell = null;
        this.sinkCell = null;
        identifyTerminations();
    }

    static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    private void identifyTerminations() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    if (tc.type == TerminationType.SOURCE && sourceCell == null) {
                        sourceCell = tc;
                    } else if (tc.type == TerminationType.SINK && sinkCell == null) {
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
        if (distance < 0) {
            return;
        }
        Set<Coordinate> newFilled = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        Coordinate srcCoord = new Coordinate(sourceCell.coord.row, sourceCell.coord.col);
        queue.add(srcCoord);
        newFilled.add(srcCoord);

        // BFS layering
        int[] dist = new int[rows * cols];
        for (int i = 0; i < dist.length; i++) {
            dist[i] = -1;
        }
        dist[srcCoord.row * cols + srcCoord.col] = 0;

        while (!queue.isEmpty()) {
            Coordinate cur = queue.poll();
            int curDist = dist[cur.row * cols + cur.col];
            if (curDist >= distance) {
                continue;
            }

            Cell curCell = cells[cur.row][cur.col];
            Direction[] dirs = null;
            if (curCell instanceof FillableCell) {
                FillableCell fc = (FillableCell) curCell;
                if (fc.getPipe() == null) {
                    continue;
                }
                dirs = fc.getPipe().getConnections();
            } else if (curCell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) curCell;
                if (tc.type == TerminationType.SOURCE) {
                    dirs = new Direction[] { tc.pointingTo };
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
                Cell neighbor = cells[next.row][next.col];
                boolean matched = false;
                if (neighbor instanceof FillableCell) {
                    FillableCell nfc = (FillableCell) neighbor;
                    if (nfc.getPipe() != null) {
                        for (Direction nd : nfc.getPipe().getConnections()) {
                            if (nd == d.getOpposite()) {
                                matched = true;
                                break;
                            }
                        }
                    }
                } else if (neighbor instanceof TerminationCell) {
                    TerminationCell ntc = (TerminationCell) neighbor;
                    if (ntc.type == TerminationType.SINK) {
                        if (d == ntc.pointingTo) {
                            matched = true;
                        }
                    }
                }
                if (!matched) {
                    continue;
                }
                if (dist[next.row * cols + next.col] == -1) {
                    dist[next.row * cols + next.col] = curDist + 1;
                    newFilled.add(next);
                    queue.add(next);
                }
            }
        }

        // Mark filled flags
        for (Coordinate c : newFilled) {
            Cell cell = cells[c.row][c.col];
            if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                if (fc.getPipe() != null) {
                    fc.getPipe().setFilled();
                }
            } else if (cell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) cell;
                tc.setFilled();
            }
        }

        int newlyFilled = newFilled.size() - filledTiles.size();
        if (newlyFilled < 0) {
            newlyFilled = 0;
        }
        prevFilledTiles = newlyFilled;
        filledTiles = newFilled;
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }
        boolean[][] visited = new boolean[rows][cols];
        Queue<Coordinate> queue = new LinkedList<>();
        Coordinate first = sourceCell.coord.add(sourceCell.pointingTo.getOffset());
        if (first.row < 0 || first.row >= rows || first.col < 0 || first.col >= cols) {
            return false;
        }
        Cell firstCell = cells[first.row][first.col];
        if (firstCell instanceof FillableCell) {
            FillableCell fc = (FillableCell) firstCell;
            if (fc.getPipe() == null) {
                return false;
            }
        } else {
            return false;
        }
        queue.add(first);
        visited[first.row][first.col] = true;

        while (!queue.isEmpty()) {
            Coordinate cur = queue.poll();
            Cell curCell = cells[cur.row][cur.col];
            if (curCell instanceof FillableCell) {
                FillableCell fc = (FillableCell) curCell;
                if (fc.getPipe() == null) {
                    continue;
                }
                for (Direction d : fc.getPipe().getConnections()) {
                    Coordinate next = cur.add(d.getOffset());
                    if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                        continue;
                    }
                    if (visited[next.row][next.col]) {
                        continue;
                    }
                    Cell neighbor = cells[next.row][next.col];
                    if (neighbor instanceof TerminationCell) {
                        TerminationCell ntc = (TerminationCell) neighbor;
                        if (ntc.type == TerminationType.SINK && d == ntc.pointingTo) {
                            return true;
                        }
                    }
                    if (neighbor instanceof FillableCell) {
                        FillableCell nfc = (FillableCell) neighbor;
                        if (nfc.getPipe() != null) {
                            boolean matched = false;
                            for (Direction nd : nfc.getPipe().getConnections()) {
                                if (nd == d.getOpposite()) {
                                    matched = true;
                                    break;
                                }
                            }
                            if (matched) {
                                visited[next.row][next.col] = true;
                                queue.add(next);
                            }
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
